package com.paoneking.nepallipikeyboard.latin.utils.transliteration

import android.content.Context

object NepalBhasaWordDictionary {

    private const val DEFAULT_FREQUENCY = 100
    private const val FILE_DEFAULT_FREQUENCY = 150

    private val builtInFrequencies = mapOf(
        // Pronouns
        "ji" to 250, "wa" to 248, "thaykhu" to 240, "jhi" to 245,
        "nhu" to 235, "ne" to 240, "cha" to 235,
        // Common verbs
        "yaye" to 228, "yale" to 225, "yana" to 220,
        "bigu" to 215, "bile" to 212, "bina" to 210,
        // Common nouns
        "newa" to 235, "kathmandu" to 232, "nepal" to 235,
        "kha" to 220, "mha" to 218, "gha" to 215,
        "ta" to 230, "ma" to 228,
    )

    private val fileFrequencies = mutableMapOf<String, Int>()

    // Built-in hardcoded map (common Nepal Bhasa words)
    private val builtInWordMap = mapOf(
        // Greetings
        "namaskar"      to "𑐣𑐩𑐳𑑂𑐎𑐵𑐬",
        "namaste"       to "𑐣𑐩𑐳𑑂𑐟𑐾",
        "juijhar"       to "𑐖𑐸𑐂𑐗𑐵𑐬",

        // Places
        "nepal"         to "𑐣𑐾𑐥𑐵𑐮",
        "kathmandu"     to "𑐫𑐾𑑃",
        "patan"         to "𑐫𑐮",
        "bhaktapur"     to "𑐏𑑂𑐰𑐥",
        "newa"          to "𑐣𑐾𑐰𑐵",

        // Numbers (Nepal Bhasa). 1-3 confirmed by a speaker: chhi, nhi, swo.
        // "chhi" previously mapped to 0 while a non-word, "thi", occupied 1.
        // 0 has no entry: its Nepal Bhasa name is not confirmed, and a wrong
        // mapping is worse than none.
        "chhi"          to "𑑑",   // 1
        "nhi"           to "𑑒",   // 2
        "swo"           to "𑑓",   // 3
        "pli"           to "𑑔",   // 4  — romanisation unconfirmed
        "piga"          to "𑑕",   // 5  — romanisation unconfirmed
        "khu"           to "𑑖",   // 6
        "nhye"          to "𑑗",   // 7  — romanisation unconfirmed
        "tya"           to "𑑘",   // 8  — romanisation unconfirmed
        "gu"            to "𑑙",   // 9  — romanisation unconfirmed

        // Common words
        "ta"            to "𑐟",   // TA  (U+1141F)
        "ma"            to "𑐩",   // MA  (U+11429)
        "ji"            to "𑐖𑐶",
        "wa"            to "𑐰𑐵",
        "cha"           to "𑐔",
        "kha"           to "𑐏",
        "ga"            to "𑐐",
        "ba"            to "𑐧",   // BA  (U+11427)
        "pa"            to "𑐥",   // PA  (U+11425)
        "na"            to "𑐣",   // NA  (U+11423)
        "sa"            to "𑐳",   // SA  (U+11433)
        "da"            to "𑐡",   // DA  (U+11421)
        "ha"            to "𑐴",
        "ya"            to "𑐫",   // YA  (U+1142B)
        "ra"            to "𑐬",   // RA  (U+1142C)
        "la"            to "𑐮",   // LA  (U+1142E)
    )

    private val fileWordMap = mutableMapOf<String, String>()

    // User-learned words are kept apart from asset words so that saving the user
    // dictionary does not copy the whole shipped asset into internal storage.
    private val userWordMap = mutableMapOf<String, String>()
    private val userFrequencies = mutableMapOf<String, Int>()
    private var userDirty = false

    private var isFileLoaded = false
    private var loadError: String? = null

    // Cached union of built-in + asset + user words, rebuilt only when a source changes.
    private var mergedCache: Map<String, String>? = null

    // Roman keys with vowel length collapsed (aa->a, ii->i, uu->u) -> script word.
    // Backs the loose lookup below; rebuilt with mergedCache.
    private var normalizedCache: Map<String, List<String>>? = null

    private fun invalidateMerged() {
        mergedCache = null
        normalizedCache = null
    }

    /**
     * Collapses the long/short vowel distinction that Romanised input is inconsistent about:
     * users type "nepal" for "nepaal", "pani" for "paani". Applied to both the dictionary
     * keys and the query so the two meet in the middle.
     */
    private fun normalizeVowels(roman: String): String =
        roman.lowercase().replace("aa", "a").replace("ii", "i").replace("uu", "u")

    // One normalized form can map to several real words: in Nepali, vowel length is
    // meaningful -- "paani" (water) and "pani" (also) both normalize to "pani". So keep
    // every candidate and let the query pick the closest one rather than overwriting.
    private fun normalized(): Map<String, List<String>> = normalizedCache ?: buildMap<String, MutableList<String>> {
        merged().keys.forEach { roman -> getOrPut(normalizeVowels(roman)) { mutableListOf() }.add(roman) }
    }.also { normalizedCache = it }

    /** Levenshtein distance, only ever run on a lookup miss over short words. */
    private fun editDistance(a: String, b: String): Int {
        if (a == b) return 0
        var prev = IntArray(b.length + 1) { it }
        val curr = IntArray(b.length + 1)
        for (i in 1..a.length) {
            curr[0] = i
            for (j in 1..b.length) {
                val sub = prev[j - 1] + if (a[i - 1] == b[j - 1]) 0 else 1
                curr[j] = minOf(curr[j - 1] + 1, prev[j] + 1, sub)
            }
            prev = curr.copyOf()
        }
        return prev[b.length]
    }

    private fun merged(): Map<String, String> = mergedCache ?: buildMap {
        putAll(builtInWordMap)
        putAll(fileWordMap)
        putAll(userWordMap)
    }.also { mergedCache = it }

    @JvmStatic
    @JvmOverloads
    fun loadFromAssets(context: Context, fileName: String = "nepal_bhasa_words.txt") {
        if (isFileLoaded) return
        try {
            var loadedCount = 0
            var skippedCount = 0

            context.assets.open(fileName).bufferedReader().forEachLine { rawLine ->
                val line = rawLine.trim()
                when {
                    line.isEmpty()       -> { /* skip */ }
                    line.startsWith("#") -> { /* skip */ }
                    line.contains("=")  -> {
                        val eqIndex = line.indexOf("=")
                        val roman = line.substring(0, eqIndex).trim().lowercase()
                        val rest  = line.substring(eqIndex + 1).trim()
                        val colonIdx = rest.lastIndexOf(':')
                        val (newa, freq) =
                            if (colonIdx > 0 && rest.substring(colonIdx + 1).all { it.isDigit() })
                                rest.substring(0, colonIdx).trim() to rest.substring(colonIdx + 1).toInt()
                            else
                                rest to FILE_DEFAULT_FREQUENCY
                        if (roman.isNotEmpty() && newa.isNotEmpty()) {
                            fileWordMap[roman] = newa
                            fileFrequencies[roman] = freq
                            loadedCount++
                        } else skippedCount++
                    }
                    else -> skippedCount++
                }
            }

            isFileLoaded = true
            invalidateMerged()
            android.util.Log.d("NepalBhasaDict",
                "Loaded $loadedCount words from $fileName, skipped $skippedCount lines")

        } catch (e: java.io.FileNotFoundException) {
            loadError = "File not found: $fileName"
            android.util.Log.w("NepalBhasaDict", loadError!!)
            isFileLoaded = true
        } catch (e: Exception) {
            loadError = "Error loading $fileName: ${e.message}"
            android.util.Log.e("NepalBhasaDict", loadError!!, e)
            isFileLoaded = true
        }
    }

    /**
     * Look up a word with common Romanization variations (e.g. a instead of aa).
     */
    fun lookupLoose(roman: String): String? {
        lookup(roman)?.let { return it }
        // Fall back to a vowel-length-insensitive match. The previous implementation did
        // replace("a", "aa") on the whole word, which turned "kathmandu" into
        // "kaathmaandu" and matched nothing; only single-'a' words ever worked.
        val low = roman.lowercase()
        val candidates = normalized()[normalizeVowels(roman)] ?: return null
        // Closest spelling wins, so "paanii" resolves to "paani" and not to "pani".
        // Frequency breaks ties.
        val best = candidates.minWithOrNull(
            compareBy({ editDistance(low, it) }, { -getFrequency(it) }, { it })
        ) ?: return null
        return lookup(best)
    }

    fun lookup(romanWord: String): String? {
        val lower = romanWord.trim().lowercase()
        return userWordMap[lower] ?: fileWordMap[lower] ?: builtInWordMap[lower]
            ?: userWordMap[romanWord] ?: fileWordMap[romanWord] ?: builtInWordMap[romanWord]
    }

    fun hasDictionaryEntry(romanWord: String): Boolean = lookup(romanWord) != null

    fun getFrequency(roman: String): Int {
        val lower = roman.trim().lowercase()
        return userFrequencies[lower] ?: fileFrequencies[lower] ?: builtInFrequencies[lower] ?: DEFAULT_FREQUENCY
    }

    fun getPrefixMatchesWithFrequency(prefix: String, maxResults: Int = 30): List<Triple<String, String, Int>> {
        val lower = prefix.lowercase()
        // Resolve the frequency once per candidate instead of twice per comparison:
        // the old comparator called getFrequency() inside sortedWith, so an n-entry
        // prefix match did O(n log n) map lookups on every keystroke.
        return merged().entries
            .filter { it.key.startsWith(lower) }
            .map { Triple(it.key, it.value, getFrequency(it.key)) }
            .sortedWith(
                compareByDescending<Triple<String, String, Int>> { it.third }
                    .thenBy { it.first.length }
                    .thenBy { it.first }
            )
            .take(maxResults)
    }

    /** Add a word at runtime (e.g. user-learned word). Persisted by [saveIfDirty]. */
    fun addWord(roman: String, newa: String, frequency: Int = FILE_DEFAULT_FREQUENCY) {
        val lower = roman.lowercase()
        if (userWordMap[lower] == newa && userFrequencies[lower] == frequency) return
        userWordMap[lower] = newa
        userFrequencies[lower] = frequency
        userDirty = true
        invalidateMerged()
    }

    /** Remove a user-learned word (asset and built-in entries cannot be removed) */
    fun removeWord(roman: String) {
        val lower = roman.lowercase()
        if (userWordMap.remove(lower) != null) {
            userFrequencies.remove(lower)
            userDirty = true
            invalidateMerged()
        }
    }

    fun totalWordCount(): Int = merged().size

    fun isLoaded(): Boolean = isFileLoaded

    fun getLoadError(): String? = loadError

    /** Writes the user dictionary only if something was learned since the last save. */
    @JvmStatic
    fun saveIfDirty(context: Context) {
        if (userDirty) saveToInternalStorage(context)
    }

    /**
     * Save runtime-learned words to internal storage so they survive process death.
     */
    fun saveToInternalStorage(context: Context, fileName: String = "nepal_bhasa_words_user.txt") {
        try {
            context.openFileOutput(fileName, Context.MODE_PRIVATE).bufferedWriter().use { writer ->
                writer.write("# Nepal Bhasa user dictionary — words learned from corrections\n")
                writer.write("# Format: roman=newa:frequency\n\n")
                userWordMap.entries
                    .sortedBy { it.key }
                    .forEach { (roman, newa) ->
                        val freq = userFrequencies[roman] ?: FILE_DEFAULT_FREQUENCY
                        writer.write("$roman=$newa:$freq\n")
                    }
            }
            userDirty = false
            android.util.Log.d("NepalBhasaDict", "Saved ${userWordMap.size} user words to $fileName")
        } catch (e: Exception) {
            android.util.Log.e("NepalBhasaDict", "Error saving dictionary: ${e.message}", e)
        }
    }

    /**
     * Load user-saved words from internal storage. Call alongside [loadFromAssets].
     */
    @JvmStatic
    @JvmOverloads
    fun loadFromInternalStorage(context: Context, fileName: String = "nepal_bhasa_words_user.txt") {
        try {
            context.openFileInput(fileName).bufferedReader().forEachLine { rawLine ->
                val line = rawLine.trim()
                if (line.isNotEmpty() && !line.startsWith("#") && line.contains("=")) {
                    val eqIndex = line.indexOf("=")
                    val roman = line.substring(0, eqIndex).trim().lowercase()
                    val rest = line.substring(eqIndex + 1).trim()
                    val colonIdx = rest.lastIndexOf(':')
                    val (newa, freq) =
                        if (colonIdx > 0 && rest.substring(colonIdx + 1).all { it.isDigit() })
                            rest.substring(0, colonIdx).trim() to rest.substring(colonIdx + 1).toInt()
                        else
                            rest to FILE_DEFAULT_FREQUENCY
                    if (roman.isNotEmpty() && newa.isNotEmpty()) {
                        userWordMap[roman] = newa
                        userFrequencies[roman] = freq
                    }
                }
            }
            invalidateMerged()
            userDirty = false
            android.util.Log.d("NepalBhasaDict", "Loaded ${userWordMap.size} user words from $fileName")
        } catch (e: java.io.FileNotFoundException) {
            // No user file yet — expected on first run
        } catch (e: Exception) {
            android.util.Log.e("NepalBhasaDict", "Error loading user dictionary: ${e.message}", e)
        }
    }
}

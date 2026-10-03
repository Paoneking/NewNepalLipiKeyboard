package com.paoneking.nepallipikeyboard.latin.utils

/**
 * Converts between Devanagari and Nepal Lipi (Newa script).
 * Minimum API level: 21 (no Java 8 stream APIs used).
 *
 * Fixes over original:
 *  1. Longest-match-first for ङ्ह, ञ्ह, न्ह, म्ह, र्ह, ल्ह (3-char Devanagari clusters)
 *  2. Pre-built reverse map — O(1) lookup instead of filterValues O(n*m)
 *  3. Newa→Devanagari handles supplementary chars (U+11400+) via
 *     Character.codePointAt() — works on API 21+, no codePoints() stream needed
 */
object NepalLipiConverter {

    // ── Multi-char Devanagari → single Newa char ──────────────────────────
    // Checked FIRST (longest match) before single-char map
    private val multiCharMap = mapOf(
        "ङ्ह" to "𑐓",
        "ञ्ह" to "𑐙",
        "न्ह" to "𑐤",
        "म्ह" to "𑐪",
        "र्ह" to "𑐭",
        "ल्ह" to "𑐯",
    )

    // ── Single Devanagari char → Newa string ──────────────────────────────
    private val devaToNewaMap = mapOf(
        // Vowels
        'अ' to "𑐀", 'आ' to "𑐁", 'इ' to "𑐂", 'ई' to "𑐃",
        'उ' to "𑐄", 'ऊ' to "𑐅", 'ऋ' to "𑐆", 'ॠ' to "𑐇", 'ऌ' to "𑐈", 'ॡ' to "𑐉",
        'ए' to "𑐊", 'ऐ' to "𑐋", 'ओ' to "𑐌", 'औ' to "𑐍",
        // Velar consonants
        'क' to "𑐎", 'ख' to "𑐏", 'ग' to "𑐐", 'घ' to "𑐑", 'ङ' to "𑐒",
        // Palatal consonants
        'च' to "𑐔", 'छ' to "𑐕", 'ज' to "𑐖", 'झ' to "𑐗", 'ञ' to "𑐘",
        // Retroflex consonants
        'ट' to "𑐚", 'ठ' to "𑐛", 'ड' to "𑐜", 'ढ' to "𑐝", 'ण' to "𑐞",
        // Dental consonants
        'त' to "𑐟", 'थ' to "𑐠", 'द' to "𑐡", 'ध' to "𑐢", 'न' to "𑐣",
        // Labial consonants
        'प' to "𑐥", 'फ' to "𑐦", 'ब' to "𑐧", 'भ' to "𑐨", 'म' to "𑐩",
        // Other consonants
        'य' to "𑐫", 'र' to "𑐬", 'ल' to "𑐮", 'व' to "𑐰",
        'श' to "𑐱", 'ष' to "𑐲", 'स' to "𑐳", 'ह' to "𑐴",
        // Matras (vowel signs)
        'ा' to "𑐵", 'ि' to "𑐶", 'ी' to "𑐷", 'ु' to "𑐸", 'ू' to "𑐹",
        'ृ' to "𑐺", 'ॄ' to "𑐻", 'ॢ' to "𑐼", 'ॣ' to "𑐽",
        'े' to "𑐾", 'ै' to "𑐿", 'ो' to "𑑀", 'ौ' to "𑑁",
        // Special signs
        '्' to "𑑂",
        'ँ' to "𑑃",
        'ं' to "𑑄",
        'ः' to "𑑅",
        '़' to "𑑆",
        'ऽ' to "𑑇",
        'ॐ' to "𑑉",
        // Punctuation
        '।' to "𑑋", '॥' to "𑑌",
        // Digits
        '०' to "𑑐", '१' to "𑑑", '२' to "𑑒", '३' to "𑑓", '४' to "𑑔",
        '५' to "𑑕", '६' to "𑑖", '७' to "𑑗", '८' to "𑑘", '९' to "𑑙",
    )

    // ── Reverse map: Newa string → Devanagari string ──────────────────────
    // Built at init time. Key is the Newa char as a String (may be a
    // surrogate pair in UTF-16, i.e. 2 Kotlin Chars, but we key by the
    // full String so lookup is still a simple map get).
    private val newaToDeva: Map<String, String> = buildMap {
        for ((deva, newa) in multiCharMap) put(newa, deva)
        for ((deva, newa) in devaToNewaMap) put(newa, deva.toString())
    }

    // ─────────────────────────────────────────────────────────────────────
    // Core conversion functions (synchronous, no coroutine overhead)
    // ─────────────────────────────────────────────────────────────────────

    /**
     * Devanagari → Nepal Lipi (Newa).
     * Handles 3-char clusters (ङ्ह, ञ्ह, न्ह, म्ह, र्ह, ल्ह) with longest-match-first. Each becomes the single
     * Newa letter, as Nepalbhasa writes it, so Newa spelled with the explicit conjunct (𑐣𑑂𑐴) comes back
     * from Devanagari as the letter (𑐤): Devanagari has one spelling for both.
     */
    @JvmStatic
    fun convertToNepalLipiSync(text: CharSequence): String {
        val sb = StringBuilder(text.length)
        var i = 0
        while (i < text.length) {
            // Try 3-char cluster first
            if (i + 2 < text.length) {
                val tri = text.substring(i, i + 3)
                val mapped = multiCharMap[tri]
                if (mapped != null) {
                    sb.append(mapped)
                    i += 3
                    continue
                }
            }
            // Single char lookup
            val ch = text[i]
            sb.append(devaToNewaMap[ch] ?: ch.toString())
            i++
        }
        return sb.toString()
    }

    /**
     * Nepal Lipi (Newa) → Devanagari.
     *
     * Newa chars are in Unicode supplementary plane (U+11400+), encoded
     * as UTF-16 surrogate pairs (high surrogate U+D805 + low surrogate) in Kotlin/Java strings.
     * We use Character.codePointAt() to read full codepoints — works on API 21+
     * without needing the Java 8 codePoints() stream.
     */
    @JvmStatic
    fun convertToDevanagariSync(text: CharSequence): String {
        val str = text.toString()
        val sb = StringBuilder(str.length)
        var i = 0
        while (i < str.length) {
            // Read one full Unicode codepoint (handles surrogate pairs)
            val cp = Character.codePointAt(str, i)
            // Convert codepoint back to a String key for map lookup
            val newaChar = String(Character.toChars(cp))
            sb.append(newaToDeva[newaChar] ?: newaChar)
            // Advance by the number of UTF-16 chars this codepoint occupies
            i += Character.charCount(cp)
        }
        return sb.toString()
    }

    /**
     * Converts [text] to the other script: Devanagari to Nepal Lipi, or Nepal Lipi to Devanagari,
     * whichever the text has more of (a tie goes to Nepal Lipi). Anything in neither script --
     * Latin, digits of other scripts, emoji, spaces -- passes through. Null if there is nothing
     * in either script.
     */
    @JvmStatic
    fun convertScript(text: CharSequence): String? {
        val str = text.toString()
        var devanagari = 0
        var newa = 0
        var i = 0
        while (i < str.length) {
            val cp = Character.codePointAt(str, i)
            when (cp) {
                in 0x0900..0x097F -> devanagari++
                in 0x11400..0x1147F -> newa++
            }
            i += Character.charCount(cp)
        }
        return when {
            devanagari == 0 && newa == 0 -> null
            newa > devanagari -> convertToDevanagariSync(str)
            else -> convertToNepalLipiSync(str)
        }
    }

    // ── Suspend wrappers (API compatibility) ──────────────────────────────

    @JvmStatic
    suspend fun convertToNepalLipi(devanagariText: CharSequence): String =
        convertToNepalLipiSync(devanagariText)

    @JvmStatic
    suspend fun convertToDevanagari(newaText: CharSequence): String =
        convertToDevanagariSync(newaText)

}

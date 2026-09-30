package com.paoneking.nepallipikeyboard.latin.utils.transliteration

object NepaliTransliterator {

    private enum class Type { CONSONANT, VOWEL, DIACRITIC }
    private data class Mapping(val roman: String, val devanagari: String, val type: Type)

    private val mappings = listOf(
        // ── 4-char (longest first) ───────────────────────────────────────────
        Mapping("shTr", "ष्ट्र", Type.CONSONANT),
        Mapping("ShTr", "ष्ट्र", Type.CONSONANT),

        // ── 3-char ──────────────────────────────────────────────────────────
        Mapping("ksh", "क्ष", Type.CONSONANT),
        Mapping("Ksh", "क्ष", Type.CONSONANT),
        Mapping("chh", "छ",   Type.CONSONANT),
        Mapping("shh", "ष",   Type.CONSONANT),   // alternative for Sh (must be before sh)
        Mapping("rri", "ऋ",   Type.VOWEL),
        Mapping("ndr", "न्द्र", Type.CONSONANT),
        Mapping("str", "स्त्र", Type.CONSONANT),
        Mapping("spr", "स्प्र", Type.CONSONANT),
        Mapping("mpr", "म्प्र", Type.CONSONANT),

        // ── 2-char aspirated / special consonants ────────────────────────────
        Mapping("kh",  "ख",   Type.CONSONANT), Mapping("Kh", "ख",   Type.CONSONANT),
        Mapping("gh",  "घ",   Type.CONSONANT), Mapping("Gh", "घ",   Type.CONSONANT),
        Mapping("ch",  "च",   Type.CONSONANT),
        Mapping("Ch",  "छ",   Type.CONSONANT),   // capital C → ch aspirate
        Mapping("jh",  "झ",   Type.CONSONANT), Mapping("Jh", "झ",   Type.CONSONANT),
        Mapping("Th",  "ठ",   Type.CONSONANT),   // capital T → retroflex aspirate
        Mapping("Dh",  "ढ",   Type.CONSONANT),   // capital D → retroflex aspirate
        Mapping("th",  "थ",   Type.CONSONANT),
        Mapping("dh",  "ध",   Type.CONSONANT),
        Mapping("ph",  "फ",   Type.CONSONANT), Mapping("Ph", "फ",   Type.CONSONANT),
        Mapping("bh",  "भ",   Type.CONSONANT), Mapping("Bh", "भ",   Type.CONSONANT),
        Mapping("sh",  "श",   Type.CONSONANT),
        Mapping("Sh",  "ष",   Type.CONSONANT),
        Mapping("nh",  "ञ",   Type.CONSONANT), Mapping("Nh", "ञ",   Type.CONSONANT),
        Mapping("ng",  "ङ",   Type.CONSONANT), Mapping("Ng", "ङ",   Type.CONSONANT),
        Mapping("hm",  "ह्म", Type.CONSONANT),
        Mapping("hn",  "ह्न", Type.CONSONANT),
        Mapping("lh",  "ळ",   Type.CONSONANT),
        Mapping("Lh",  "ळ",   Type.CONSONANT),

        // ── 2-char conjuncts ────────────────────────────────────────────────
        Mapping("tr",  "त्र", Type.CONSONANT),
        Mapping("gn",  "ज्ञ", Type.CONSONANT), Mapping("Gn", "ज्ञ", Type.CONSONANT),

        // ── 2-char long vowels ──────────────────────────────────────────────
        Mapping("aa",  "आ",   Type.VOWEL),
        Mapping("ii",  "ई",   Type.VOWEL),
        Mapping("uu",  "ऊ",   Type.VOWEL),
        Mapping("ai",  "ऐ",   Type.VOWEL),
        Mapping("au",  "औ",   Type.VOWEL),

        // ── Uppercase retroflex consonants ───────────────────────────────────
        Mapping("T",   "ट",   Type.CONSONANT),
        Mapping("D",   "ड",   Type.CONSONANT),
        Mapping("N",   "ण",   Type.CONSONANT),

        // ── Other Capitals (Diacritics & Long Vowels) ───────────────────────
        Mapping("M",   "ं",   Type.DIACRITIC),  // M -> Anusvara
        Mapping("H",   "ः",   Type.DIACRITIC),  // H -> Visarga
        Mapping("R",   "ऋ",   Type.VOWEL),      // R -> Ri
        Mapping("A",   "आ",   Type.VOWEL),
        Mapping("I",   "ई",   Type.VOWEL),
        Mapping("U",   "ऊ",   Type.VOWEL),
        Mapping("E",   "ऐ",   Type.VOWEL),
        Mapping("O",   "औ",   Type.VOWEL),

        // ── Capital shortcut keys ────────────────────────────────────────────
        Mapping("F",   "फ",   Type.CONSONANT),
        Mapping("X",   "क्ष", Type.CONSONANT),
        Mapping("Z",   "ज्ञ", Type.CONSONANT),
        Mapping("Q",   "क्व", Type.CONSONANT),
        Mapping("C",   "छ",   Type.CONSONANT),
        Mapping("B",   "भ",   Type.CONSONANT),
        Mapping("L",   "ळ",   Type.CONSONANT),

        // ── Standard consonants ──────────────────────────────────────────────
        Mapping("k",   "क",   Type.CONSONANT), Mapping("K",   "क",   Type.CONSONANT),
        Mapping("g",   "ग",   Type.CONSONANT), Mapping("G",   "ग",   Type.CONSONANT),
        Mapping("c",   "च",   Type.CONSONANT),
        Mapping("j",   "ज",   Type.CONSONANT), Mapping("J",   "ज",   Type.CONSONANT),
        Mapping("t",   "त",   Type.CONSONANT),
        Mapping("d",   "द",   Type.CONSONANT),
        Mapping("n",   "न",   Type.CONSONANT),
        Mapping("p",   "प",   Type.CONSONANT), Mapping("P",   "प",   Type.CONSONANT),
        Mapping("b",   "ब",   Type.CONSONANT),
        Mapping("m",   "म",   Type.CONSONANT),
        Mapping("y",   "य",   Type.CONSONANT), Mapping("Y",   "य",   Type.CONSONANT),
        Mapping("r",   "र",   Type.CONSONANT),
        Mapping("l",   "ल",   Type.CONSONANT),
        Mapping("v",   "व",   Type.CONSONANT), Mapping("V",   "व",   Type.CONSONANT),
        Mapping("w",   "व",   Type.CONSONANT), Mapping("W",   "व",   Type.CONSONANT),
        Mapping("s",   "स",   Type.CONSONANT), Mapping("S",   "स",   Type.CONSONANT),
        Mapping("h",   "ह",   Type.CONSONANT),
        Mapping("f",   "फ",   Type.CONSONANT),
        Mapping("x",   "क्ष", Type.CONSONANT),
        Mapping("z",   "झ",   Type.CONSONANT),

        // ── Short vowels ─────────────────────────────────────────────────────
        Mapping("a",   "अ",   Type.VOWEL),
        Mapping("i",   "इ",   Type.VOWEL),
        Mapping("u",   "उ",   Type.VOWEL),
        Mapping("e",   "ए",   Type.VOWEL),
        Mapping("o",   "ओ",   Type.VOWEL),

        // ── Diacritics ───────────────────────────────────────────────────────
        Mapping("_M",  "ं",   Type.DIACRITIC),
        Mapping("_H",  "ः",   Type.DIACRITIC),
        Mapping("~",   "ँ",   Type.DIACRITIC),
        Mapping("|",   "्",   Type.DIACRITIC),
    )

    // Devanagari vowel → its matra form (empty = inherent 'a', written nothing after consonant)
    private val matraMap = mapOf(
        "अ" to "",   "आ" to "ा",  "इ" to "ि",  "ई" to "ी",
        "उ" to "ु",  "ऊ" to "ू",  "ए" to "े",  "ऐ" to "ै",
        "ओ" to "ो",  "औ" to "ौ",  "ऋ" to "ृ",
    )

    /**
     * Finds the longest matching Roman sequence starting at the given position.
     * Longest-match-first is crucial for disambiguating sequences like 'sh' vs 's'.
     */
    // A rule can only match at a position whose character equals the rule's first
    // character, so bucket by that instead of scanning all ~100 rules per position.
    // groupBy is stable, which preserves the hand-ordered longest-match-first semantics
    // within each bucket -- the result is identical to the old linear scan.
    private val mappingsByFirstChar: Map<Char, List<Mapping>> = mappings.groupBy { it.roman[0] }

    private fun findMatch(input: String, pos: Int): Mapping? {
        val bucket = mappingsByFirstChar[input[pos]] ?: return null
        for (m in bucket) if (input.startsWith(m.roman, pos)) return m
        return null
    }

    /**
     * Transliterates a Roman string into Devanagari using phonetic mappings.
     *
     * Edge cases handled:
     * 1. Virama (्) insertion: Added between consecutive consonants (e.g., 'kt' -> 'क्त').
     * 2. Inherent 'a': Consonants are written in full form by default; if followed by 'a',
     *    no matra is added (inherent vowel). If followed by other vowels, the matra is used.
     * 3. Standalone vowels: Vowels at the start of a word or after another vowel appear in
     *    their independent form (e.g., 'aa' -> 'आ').
     * 4. Capitalization: Phonetical significance is given to specific capitals (e.g., 'T' vs 't').
     * 5. Diacritics: Handles anusvara (M), visarga (H), and chandrabindu (~).
     */
    fun transliterate(input: String): String {
        val result = StringBuilder()
        var i = 0
        var prevConsonant = false

        while (i < input.length) {
            val m = findMatch(input, i)
            if (m == null) {
                // Passthrough non-roman characters (digits, symbols, Devanagari)
                result.append(input[i])
                prevConsonant = false
                i++
                continue
            }
            when (m.type) {
                Type.CONSONANT -> {
                    // Rule: Two consonants in a row require a virama (halant) between them
                    if (prevConsonant) result.append("्")
                    result.append(m.devanagari)
                    prevConsonant = true
                }
                Type.VOWEL -> {
                    if (prevConsonant) {
                        // Consonant + Vowel: Use dependent vowel sign (matra)
                        val matra = matraMap[m.devanagari]
                        if (!matra.isNullOrEmpty()) result.append(matra)
                        // Note: inherent 'a' maps to empty matra string
                    } else {
                        // Standalone Vowel: Use independent form
                        result.append(m.devanagari)
                    }
                    prevConsonant = false
                }
                Type.DIACRITIC -> {
                    // Diacritics (dots, marks) attach to the previous character
                    result.append(m.devanagari)
                    prevConsonant = false
                }
            }
            i += m.roman.length
        }
        return result.toString()
    }

    /**
     * Wrapper for word-level transliteration with basic error handling.
     */
    fun transliterateWord(word: String): String = try {
        transliterate(word)
    } catch (e: Exception) {
        android.util.Log.e("NepaliTranslit", "Failed to transliterate '$word'", e)
        word
    }
}

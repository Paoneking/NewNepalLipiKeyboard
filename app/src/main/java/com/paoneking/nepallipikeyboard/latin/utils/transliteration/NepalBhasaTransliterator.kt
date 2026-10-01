package com.paoneking.nepallipikeyboard.latin.utils.transliteration

object NepalBhasaTransliterator {

    private enum class Type { CONSONANT, VOWEL, DIACRITIC }
    private data class Mapping(val roman: String, val newa: String, val type: Type)

    private val mappings = listOf(
        // ── 3-char (longest first) ───────────────────────────────────────────
        Mapping("ksh", "𑐎𑑂𑐲", Type.CONSONANT),   // ksha conjunct
        Mapping("Ksh", "𑐎𑑂𑐲", Type.CONSONANT),
        Mapping("chh", "𑐕",     Type.CONSONANT),   // chha
        Mapping("rri", "𑐆",     Type.VOWEL),

        // ── 2-char aspirated / special consonants ────────────────────────────
        Mapping("kh",  "𑐏",  Type.CONSONANT), Mapping("Kh", "𑐏",  Type.CONSONANT),   // kha
        Mapping("gh",  "𑐑",  Type.CONSONANT), Mapping("Gh", "𑐑",  Type.CONSONANT),   // gha
        Mapping("Ch",  "𑐕",  Type.CONSONANT),   // capital C → chha
        Mapping("ch",  "𑐔",  Type.CONSONANT),   // cha
        Mapping("jh",  "𑐗",  Type.CONSONANT), Mapping("Jh", "𑐗",  Type.CONSONANT),   // jha
        Mapping("Th",  "𑐛",  Type.CONSONANT),   // retroflex ttha
        Mapping("Dh",  "𑐝",  Type.CONSONANT),   // retroflex ddha
        Mapping("th",  "𑐠",  Type.CONSONANT),   // tha
        Mapping("dh",  "𑐢",  Type.CONSONANT),   // dha
        Mapping("ph",  "𑐦",  Type.CONSONANT), Mapping("Ph", "𑐦",  Type.CONSONANT),   // pha
        Mapping("bh",  "𑐨",  Type.CONSONANT), Mapping("Bh", "𑐨",  Type.CONSONANT),   // bha
        Mapping("Sh",  "𑐲",  Type.CONSONANT),   // retroflex ssa
        Mapping("sh",  "𑐱",  Type.CONSONANT),   // sha
        Mapping("nh",  "𑐘",  Type.CONSONANT), Mapping("Nh", "𑐘",  Type.CONSONANT),   // nya
        Mapping("ng",  "𑐒",  Type.CONSONANT), Mapping("Ng", "𑐒",  Type.CONSONANT),   // nga
        Mapping("hm",  "𑐴𑑂𑐩", Type.CONSONANT),   // ha-virama-ma conjunct
        Mapping("hn",  "𑐴𑑂𑐣", Type.CONSONANT),   // ha-virama-na conjunct
        Mapping("lh",  "𑐯",  Type.CONSONANT),   // lha (murmured lateral)
        Mapping("Lh",  "𑐯",  Type.CONSONANT),

        // ── 2-char conjuncts ────────────────────────────────────────────────
        Mapping("tr",  "𑐟𑑂𑐬", Type.CONSONANT),
        Mapping("gn",  "𑐖𑑂𑐘", Type.CONSONANT), Mapping("Gn", "𑐖𑑂𑐘", Type.CONSONANT),   // ja-virama-nya (gya)

        // ── 2-char long vowels ──────────────────────────────────────────────
        Mapping("aa",  "𑐁",  Type.VOWEL),
        Mapping("ii",  "𑐃",  Type.VOWEL),
        Mapping("uu",  "𑐅",  Type.VOWEL),
        Mapping("ai",  "𑐋",  Type.VOWEL),
        Mapping("au",  "𑐍",  Type.VOWEL),

        // ── Uppercase retroflex consonants ───────────────────────────────────
        Mapping("T",   "𑐚",  Type.CONSONANT),
        Mapping("D",   "𑐜",  Type.CONSONANT),
        Mapping("N",   "𑐞",  Type.CONSONANT),

        // ── Other Capitals (Diacritics & Long Vowels) ───────────────────────
        Mapping("M",   "𑑄",  Type.DIACRITIC),  // M -> Anusvara
        Mapping("H",   "𑑅",  Type.DIACRITIC),  // H -> Visarga
        Mapping("R",   "𑐆",  Type.VOWEL),      // R -> vocalic r
        Mapping("A",   "𑐁",  Type.VOWEL),
        Mapping("I",   "𑐃",  Type.VOWEL),
        Mapping("U",   "𑐅",  Type.VOWEL),
        Mapping("E",   "𑐋",  Type.VOWEL),
        Mapping("O",   "𑐍",  Type.VOWEL),

        // ── Capital shortcut keys ────────────────────────────────────────────
        Mapping("F",   "𑐦",     Type.CONSONANT),
        Mapping("X",   "𑐎𑑂𑐲", Type.CONSONANT),
        Mapping("Z",   "𑐖𑑂𑐘", Type.CONSONANT),
        Mapping("Q",   "𑐎𑑂𑐰", Type.CONSONANT),
        Mapping("C",   "𑐕",     Type.CONSONANT),
        Mapping("B",   "𑐨",     Type.CONSONANT),
        Mapping("L",   "𑐯",     Type.CONSONANT),

        // ── Standard consonants ──────────────────────────────────────────────
        Mapping("k",   "𑐎",  Type.CONSONANT), Mapping("K",   "𑐎",  Type.CONSONANT),
        Mapping("g",   "𑐐",  Type.CONSONANT), Mapping("G",   "𑐐",  Type.CONSONANT),
        Mapping("c",   "𑐔",  Type.CONSONANT),
        Mapping("j",   "𑐖",  Type.CONSONANT), Mapping("J",   "𑐖",  Type.CONSONANT),
        Mapping("t",   "𑐟",  Type.CONSONANT),
        Mapping("d",   "𑐡",  Type.CONSONANT),
        Mapping("n",   "𑐣",  Type.CONSONANT),
        Mapping("p",   "𑐥",  Type.CONSONANT), Mapping("P",   "𑐥",  Type.CONSONANT),
        Mapping("b",   "𑐧",  Type.CONSONANT),
        Mapping("m",   "𑐩",  Type.CONSONANT),
        Mapping("y",   "𑐫",  Type.CONSONANT), Mapping("Y",   "𑐫",  Type.CONSONANT),
        Mapping("r",   "𑐬",  Type.CONSONANT),
        Mapping("l",   "𑐮",  Type.CONSONANT),
        Mapping("v",   "𑐰",  Type.CONSONANT), Mapping("V",   "𑐰",  Type.CONSONANT),
        Mapping("w",   "𑐰",  Type.CONSONANT), Mapping("W",   "𑐰",  Type.CONSONANT),
        Mapping("s",   "𑐳",  Type.CONSONANT), Mapping("S",   "𑐳",  Type.CONSONANT),
        Mapping("h",   "𑐴",  Type.CONSONANT),
        Mapping("f",   "𑐦",  Type.CONSONANT),
        Mapping("x",   "𑐎𑑂𑐲", Type.CONSONANT),
        Mapping("z",   "𑐗",  Type.CONSONANT),

        // ── Short vowels ─────────────────────────────────────────────────────
        Mapping("a",   "𑐀",  Type.VOWEL),
        Mapping("i",   "𑐂",  Type.VOWEL),
        Mapping("u",   "𑐄",  Type.VOWEL),
        Mapping("e",   "𑐊",  Type.VOWEL),
        Mapping("o",   "𑐌",  Type.VOWEL),

        // ── Diacritics ───────────────────────────────────────────────────────
        Mapping("_M",  "𑑄",  Type.DIACRITIC),
        Mapping("_H",  "𑑅",  Type.DIACRITIC),
        Mapping("~",   "𑑃",  Type.DIACRITIC),
        Mapping("|",   "𑑂",  Type.DIACRITIC),
    )

    // Newa vowel → its dependent vowel sign (matra) form
    private val matraMap = mapOf(
        "𑐀" to "",    // a  → inherent, no matra
        "𑐁" to "𑐵",  // aa → vowel sign aa
        "𑐂" to "𑐶",  // i  → vowel sign i
        "𑐃" to "𑐷",  // ii → vowel sign ii
        "𑐄" to "𑐸",  // u  → vowel sign u
        "𑐅" to "𑐹",  // uu → vowel sign uu
        "𑐊" to "𑐾",  // e  → vowel sign e
        "𑐋" to "𑐿",  // ai → vowel sign ai
        "𑐌" to "𑑀",  // o  → vowel sign o
        "𑐍" to "𑑁",  // au → vowel sign au
        "𑐆" to "𑐺",  // ri → vowel sign vocalic r
    )

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

    fun transliterate(input: String): String {
        val result = StringBuilder()
        var i = 0
        var prevConsonant = false

        while (i < input.length) {
            val m = findMatch(input, i)
            if (m == null) {
                // Non-roman char
                result.append(input[i])
                prevConsonant = false
                i++
                continue
            }
            when (m.type) {
                Type.CONSONANT -> {
                    if (prevConsonant) result.append("𑑂") // virama
                    result.append(m.newa)
                    prevConsonant = true
                }
                Type.VOWEL -> {
                    if (prevConsonant) {
                        val matra = matraMap[m.newa]
                        if (!matra.isNullOrEmpty()) result.append(matra)
                    } else {
                        result.append(m.newa)
                    }
                    prevConsonant = false
                }
                Type.DIACRITIC -> {
                    result.append(m.newa)
                    prevConsonant = false
                }
            }
            i += m.roman.length
        }
        return result.toString()
    }

    fun transliterateWord(word: String): String = try { transliterate(word) } catch (_: Exception) { word }
}

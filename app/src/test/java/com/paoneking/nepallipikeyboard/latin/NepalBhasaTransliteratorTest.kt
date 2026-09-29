package com.paoneking.nepallipikeyboard.latin

import com.paoneking.nepallipikeyboard.latin.utils.transliteration.NepalBhasaAutocorrect
import com.paoneking.nepallipikeyboard.latin.utils.transliteration.NepalBhasaTransliterator
import org.junit.Assert.assertEquals
import org.junit.Test

class NepalBhasaTransliteratorTest {

    private fun t(input: String) = NepalBhasaTransliterator.transliterateWord(input)

    @Test
    fun testStandaloneVowels() {
        assertEquals("𑐀", t("a"))
        assertEquals("𑐁", t("aa"))
        assertEquals("𑐂", t("i"))
        assertEquals("𑐃", t("ii"))
        assertEquals("𑐄", t("u"))
        assertEquals("𑐅", t("uu"))
        assertEquals("𑐊", t("e"))
        assertEquals("𑐋", t("ai"))
        assertEquals("𑐌", t("o"))
        assertEquals("𑐍", t("au"))
        assertEquals("𑐆", t("R"))
        assertEquals("𑐆", t("rri"))
        assertEquals("𑐬𑐶", t("ri"))
    }

    @Test
    fun testConsonantWithAllMatras() {
        assertEquals("𑐎𑐵", t("kaa"))
        assertEquals("𑐎𑐶", t("ki"))
        assertEquals("𑐎𑐷", t("kii"))
        assertEquals("𑐎𑐸", t("ku"))
        assertEquals("𑐎𑐹", t("kuu"))
        assertEquals("𑐎𑐾", t("ke"))
        assertEquals("𑐎𑐿", t("kai"))
        assertEquals("𑐎𑑀", t("ko"))
        assertEquals("𑐎𑑁", t("kau"))
        assertEquals("𑐎𑐺", t("kR"))
        assertEquals("𑐎𑐺", t("krri"))
        assertEquals("𑐎𑑂𑐬𑐶", t("kri"))
    }

    @Test
    fun testCommonWords() {
        // "juju" (king)
        assertEquals("𑐖𑐸𑐖𑐸", t("juju"))
        // "nepaal" (Nepal)
        assertEquals("𑐣𑐾𑐥𑐵𑐮", t("nepaal"))
        assertEquals("𑐣𑐾𑐥𑐮", t("nepal"))
    }

    @Test
    fun testShortcutKeys() {
        // Capital shortcut: F → pha
        assertEquals("𑐦", t("F"))
        // Capital shortcut: B → bha
        assertEquals("𑐨", t("B"))
        // Capital shortcut: L → lha
        assertEquals("𑐯", t("L"))
        // Capital shortcut: C → chha
        assertEquals("𑐕", t("C"))
        // Capital shortcut: X → ksha conjunct
        assertEquals("𑐎𑑂𑐲", t("X"))
        // Capital shortcut: Z → jnya conjunct (gya)
        assertEquals("𑐖𑑂𑐘", t("Z"))
        // Capital shortcut: Q → kwa conjunct
        assertEquals("𑐎𑑂𑐰", t("Q"))
        // f (lowercase) → pha, same as F
        assertEquals("𑐦", t("f"))
        // x (lowercase) → ksha, same as X
        assertEquals("𑐎𑑂𑐲", t("x"))
    }

    @Test
    fun testRetroflexCapitals() {
        // T → retroflex TTA (𑐚)
        assertEquals("𑐚", t("T"))
        // D → retroflex DDA (𑐜)
        assertEquals("𑐜", t("D"))
        // N → retroflex NNA (𑐞)
        assertEquals("𑐞", t("N"))
        // T with vowel
        assertEquals("𑐚𑐶", t("Ti"))
        // Th → retroflex aspirate TTHA
        assertEquals("𑐛", t("Th"))
        // Dh → retroflex aspirate DDHA
        assertEquals("𑐝", t("Dh"))
    }

    @Test
    fun testDoubleConsonants() {
        // kk → 𑐎𑑂𑐎 (virama between two ka)
        assertEquals("𑐎𑑂𑐎", t("kk"))
        // tt → 𑐟𑑂𑐟
        assertEquals("𑐟𑑂𑐟", t("tt"))
        // nn → 𑐣𑑂𑐣
        assertEquals("𑐣𑑂𑐣", t("nn"))
        // mm → 𑐩𑑂𑐩
        assertEquals("𑐩𑑂𑐩", t("mm"))
    }

    @Test
    fun testConjuncts() {
        // tr → ta-virama-ra
        assertEquals("𑐟𑑂𑐬", t("tr"))
        // gn (gya) → ja-virama-nya
        assertEquals("𑐖𑑂𑐘", t("gn"))
        // ksh → ka-virama-ssa
        assertEquals("𑐎𑑂𑐲", t("ksh"))
        // chh → chha (single glyph)
        assertEquals("𑐕", t("chh"))
        // sh → sha
        assertEquals("𑐱", t("sh"))
        // Sh → retroflex ssa
        assertEquals("𑐲", t("Sh"))
    }

    @Test
    fun testDiacritics() {
        // M → anusvara (𑑄)
        assertEquals("𑐎𑑄", t("kM"))
        // H → visarga (𑑅)
        assertEquals("𑐎𑑅", t("kH"))
        // ~ → chandrabindu (𑑃)
        assertEquals("𑐎𑑃", t("k~"))
        // | → virama (𑑂) — explicit halant
        assertEquals("𑐎𑑂", t("k|"))
    }

    @Test
    fun testLongVowelShortcuts() {
        // A → long aa vowel (standalone)
        assertEquals("𑐁", t("A"))
        // I → long ii vowel (standalone)
        assertEquals("𑐃", t("I"))
        // U → long uu vowel (standalone)
        assertEquals("𑐅", t("U"))
        // E → ai vowel (standalone)
        assertEquals("𑐋", t("E"))
        // O → au vowel (standalone)
        assertEquals("𑐍", t("O"))
        // A as matra after consonant
        assertEquals("𑐎𑐵", t("kA"))
        // I as matra after consonant
        assertEquals("𑐎𑐷", t("kI"))
    }

    @Test
    fun testAspiratedConsonants() {
        // kh → kha
        assertEquals("𑐏", t("kh"))
        // gh → gha
        assertEquals("𑐑", t("gh"))
        // jh → jha; z also maps to jha
        assertEquals("𑐗", t("jh"))
        assertEquals("𑐗", t("z"))
        // th → tha
        assertEquals("𑐠", t("th"))
        // dh → dha
        assertEquals("𑐢", t("dh"))
        // ph → pha; f also maps to pha
        assertEquals("𑐦", t("ph"))
        assertEquals("𑐦", t("f"))
        // bh → bha
        assertEquals("𑐨", t("bh"))
        // nh → nya
        assertEquals("𑐘", t("nh"))
        // ng → nga
        assertEquals("𑐒", t("ng"))
        // lh → lha
        assertEquals("𑐯", t("lh"))
    }

    @Test
    fun testPassthrough() {
        // Digits pass through unchanged
        assertEquals("1", t("1"))
        assertEquals("42", t("42"))
        // Already-Newa script passes through
        assertEquals("𑐎", t("𑐎"))
    }
}

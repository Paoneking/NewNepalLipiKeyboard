package com.paoneking.nepallipikeyboard.latin

import com.paoneking.nepallipikeyboard.latin.utils.transliteration.NepaliAutocorrect
import com.paoneking.nepallipikeyboard.latin.utils.transliteration.NepaliTransliterator
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test

class NepaliAutocorrectTest {

    // =========================================================
    // 1. getSuggestion — dictionary match
    // =========================================================

    @Test fun testDictionaryExactMatch() {
        assertEquals("नेपाल",    NepaliAutocorrect.getSuggestion("nepal"))
        assertEquals("काठमाडौं", NepaliAutocorrect.getSuggestion("kathmandu"))
        assertEquals("पोखरा",    NepaliAutocorrect.getSuggestion("pokhara"))
        assertEquals("नमस्ते",   NepaliAutocorrect.getSuggestion("namaste"))
        assertEquals("धन्यवाद",  NepaliAutocorrect.getSuggestion("dhanyabad"))
        assertEquals("घर",       NepaliAutocorrect.getSuggestion("ghar"))
        assertEquals("पानी",     NepaliAutocorrect.getSuggestion("paani"))
        assertEquals("राम्रो",   NepaliAutocorrect.getSuggestion("ramro"))
        assertEquals("मान्छे",   NepaliAutocorrect.getSuggestion("maanche"))
        assertEquals("हुन्छ",    NepaliAutocorrect.getSuggestion("hunchha"))
        assertEquals("छ",        NepaliAutocorrect.getSuggestion("chha"))
        assertEquals("छैन",      NepaliAutocorrect.getSuggestion("chhaina"))
        assertEquals("कहाँ",     NepaliAutocorrect.getSuggestion("kaha"))
        assertEquals("यहाँ",     NepaliAutocorrect.getSuggestion("yaha"))
        assertEquals("त्यहाँ",   NepaliAutocorrect.getSuggestion("tyaha"))
        assertEquals("बाहिर",    NepaliAutocorrect.getSuggestion("bahira"))
        assertEquals("भित्र",    NepaliAutocorrect.getSuggestion("bhitra"))
        assertEquals("अगाडि",    NepaliAutocorrect.getSuggestion("agadi"))
    }

    // =========================================================
    // 2. getSuggestion — case insensitive
    // =========================================================

    @Test fun testCaseInsensitiveMatch() {
        assertEquals("नेपाल", NepaliAutocorrect.getSuggestion("Nepal"))
        assertEquals("नेपाल", NepaliAutocorrect.getSuggestion("NEPAL"))
        assertEquals("नेपाल", NepaliAutocorrect.getSuggestion("nEpAl"))
        assertEquals("घर",    NepaliAutocorrect.getSuggestion("Ghar"))
        assertEquals("घर",    NepaliAutocorrect.getSuggestion("GHAR"))
        assertEquals("पानी",  NepaliAutocorrect.getSuggestion("PAANI"))
        assertEquals("नमस्ते",NepaliAutocorrect.getSuggestion("Namaste"))
        assertEquals("राम्रो",NepaliAutocorrect.getSuggestion("RAMRO"))
    }

    // =========================================================
    // 3. getSuggestion — fallback to transliterator
    // =========================================================

    @Test fun testFallbackToTransliterator() {
        // Words not in dictionary fall back to transliterator
        assertEquals("काम",   NepaliAutocorrect.getSuggestion("kaam"))
        assertEquals("राम",   NepaliAutocorrect.getSuggestion("raam"))
        assertEquals("सीता",  NepaliAutocorrect.getSuggestion("siitaa"))
        assertEquals("प्रेम", NepaliAutocorrect.getSuggestion("prem"))
        assertEquals("शक्ति", NepaliAutocorrect.getSuggestion("shakti"))
        assertEquals("सत्य",  NepaliAutocorrect.getSuggestion("satya"))
        assertEquals("धर्म",  NepaliAutocorrect.getSuggestion("dharma"))
        assertEquals("कर्म",  NepaliAutocorrect.getSuggestion("karma"))
    }

    // =========================================================
    // 4. getSuggestion — alternate spellings
    // =========================================================

    @Test fun testAlternateSpellings() {
        // Both spellings should map to same Devanagari
        assertEquals(
            NepaliAutocorrect.getSuggestion("dhanyabad"),
            NepaliAutocorrect.getSuggestion("dhanyabaad")
        )
        assertEquals(
            NepaliAutocorrect.getSuggestion("parivar"),
            NepaliAutocorrect.getSuggestion("parivaar")
        )
        assertEquals(
            NepaliAutocorrect.getSuggestion("manche"),
            NepaliAutocorrect.getSuggestion("maanche")
        )
        assertEquals(
            NepaliAutocorrect.getSuggestion("kaha"),
            NepaliAutocorrect.getSuggestion("kahaa")
        )
        assertEquals(
            NepaliAutocorrect.getSuggestion("agadi"),
            NepaliAutocorrect.getSuggestion("agaadi")
        )
        assertEquals(
            NepaliAutocorrect.getSuggestion("aakash"),
            NepaliAutocorrect.getSuggestion("aakaash")
        )
    }

    // =========================================================
    // 5. getSuggestion — edge cases
    // =========================================================

    @Test fun testEdgeCases() {
        // Empty and blank
        assertEquals("",  NepaliAutocorrect.getSuggestion(""))
        assertEquals("  ", NepaliAutocorrect.getSuggestion("  "))

        // Single character
        val singleM = NepaliAutocorrect.getSuggestion("m")
        assertTrue("Single 'm' should return म or म", singleM == "म")

        // Already Devanagari input — should pass through
        val deva = NepaliAutocorrect.getSuggestion("नेपाल")
        assertTrue(deva.isNotBlank())

        // Numbers — pass through
        assertEquals("123", NepaliAutocorrect.getSuggestion("123"))

        // Mixed — unknown word falls to transliterator
        val result = NepaliAutocorrect.getSuggestion("xyz")
        assertNotNull(result)
    }

    // =========================================================
    // 6. processText — full sentences
    // =========================================================

    @Test fun testProcessTextBasic() {
        assertEquals(
            "नेपाल राम्रो छ",
            NepaliAutocorrect.processText("nepal ramro chha")
        )
        assertEquals(
            "घर कहाँ छ",
            NepaliAutocorrect.processText("ghar kaha chha")
        )
        assertEquals(
            "म नेपाल जान्छु",
            NepaliAutocorrect.processText("ma nepal janchu")
        )
        assertEquals(
            "पानी छैन",
            NepaliAutocorrect.processText("paani chhaina")
        )
        assertEquals(
            "तिमी कहाँ छ",
            NepaliAutocorrect.processText("timi kaha chha")
        )
    }

    @Test fun testProcessTextWithFallback() {
        // Mix of dictionary and transliterated words
        val result = NepaliAutocorrect.processText("nepal sundar chha")
        assertEquals("नेपाल", result.split(" ")[0]) // dict match
        assertEquals("छ",     result.split(" ")[2]) // dict match
        // "sundar" falls back to transliterator
        assertTrue(result.split(" ")[1].isNotBlank())
    }

    @Test fun testProcessTextEdgeCases() {
        // Empty string
        assertEquals("", NepaliAutocorrect.processText(""))

        // Single word
        assertEquals("नेपाल", NepaliAutocorrect.processText("nepal"))

        // Extra spaces
        val result = NepaliAutocorrect.processText("nepal  ghar")
        assertTrue(result.contains("नेपाल"))
        assertTrue(result.contains("घर"))

        // Already blank word in split
        assertEquals(" ", NepaliAutocorrect.processText(" "))
    }

    // =========================================================
    // 7. getSuggestions — suggestion list
    // =========================================================

    @Test fun testSuggestionsNotEmpty() {
        val suggestions = NepaliAutocorrect.getSuggestions("ne")
        assertTrue(suggestions.isNotEmpty())
    }

    @Test fun testSuggestionsTransliterationAlwaysFirst() {
        // Transliteration should always be first item
        val suggestions = NepaliAutocorrect.getSuggestions("ne")
        assertTrue(suggestions.isNotEmpty())
        // first item is transliterated form
        val transliterated = NepaliTransliterator.transliterateWord("ne")
        assertEquals(transliterated, suggestions[0])
    }

    @Test fun testSuggestionsExactMatchIncluded() {
        val suggestions = NepaliAutocorrect.getSuggestions("nepal")
        assertTrue("नेपाल should be in suggestions", "नेपाल" in suggestions)
    }

    @Test fun testSuggestionsPrefixMatchIncluded() {
        // "ne" should eventually find "nepal" -> "नेपाल"
        val suggestions = NepaliAutocorrect.getSuggestions("ne", maxResults = 10)
        assertTrue("नेपाल should appear for prefix 'ne'", "नेपाल" in suggestions)
    }

    @Test fun testSuggestionsMaxResults() {
        val suggestions = NepaliAutocorrect.getSuggestions("a", maxResults = 3)
        assertTrue(suggestions.size <= 3)

        val suggestions5 = NepaliAutocorrect.getSuggestions("a", maxResults = 5)
        assertTrue(suggestions5.size <= 5)
    }

    @Test fun testSuggestionsNoDuplicates() {
        val suggestions = NepaliAutocorrect.getSuggestions("nepal", maxResults = 10)
        assertEquals(suggestions.size, suggestions.distinct().size)
    }

    @Test fun testSuggestionsEmptyInput() {
        assertTrue(NepaliAutocorrect.getSuggestions("").isEmpty())
        assertTrue(NepaliAutocorrect.getSuggestions("   ").isEmpty())
    }

    @Test fun testSuggestionsUnknownWord() {
        // Unknown word — should still return transliteration
        val suggestions = NepaliAutocorrect.getSuggestions("zzz")
        assertTrue(suggestions.isNotEmpty())
        assertEquals(NepaliTransliterator.transliterateWord("zzz"), suggestions[0])
    }

    // =========================================================
    // 8. onWordCommit
    // =========================================================

    @Test fun testOnWordCommit() {
        assertEquals("नेपाल",   NepaliAutocorrect.onWordCommit("nepal"))
        assertEquals("पोखरा",   NepaliAutocorrect.onWordCommit("pokhara"))
        assertEquals("धन्यवाद", NepaliAutocorrect.onWordCommit("dhanyabad"))
        assertEquals("घर",      NepaliAutocorrect.onWordCommit("ghar"))
        assertEquals("पानी",    NepaliAutocorrect.onWordCommit("paani"))
        // Fallback to transliterator
        assertEquals("काम", NepaliAutocorrect.onWordCommit("kaam"))
    }

    @Test fun testOnWordCommitCaseInsensitive() {
        assertEquals("नेपाल", NepaliAutocorrect.onWordCommit("Nepal"))
        assertEquals("नेपाल", NepaliAutocorrect.onWordCommit("NEPAL"))
    }

    // =========================================================
    // 9. learnWord
    // =========================================================

    @Test fun testLearnWord() {
        // Teach a new word
        NepaliAutocorrect.learnWord("pratigya", "प्रतिज्ञा")

        // Should now be found in dictionary
        assertEquals("प्रतिज्ञा", NepaliAutocorrect.getSuggestion("pratigya"))
        assertEquals("प्रतिज्ञा", NepaliAutocorrect.onWordCommit("pratigya"))
        assertTrue(NepaliAutocorrect.wouldAutocorrect("pratigya"))
    }

    @Test fun testLearnWordOverridesBuiltIn() {
        // Override an existing entry
        NepaliAutocorrect.learnWord("nepal", "नेपाल 🇳🇵")
        assertEquals("नेपाल 🇳🇵", NepaliAutocorrect.getSuggestion("nepal"))

        // Cleanup — restore original
        NepaliAutocorrect.learnWord("nepal", "नेपाल")
    }

    @Test fun testLearnWordBlankIgnored() {
        val before = NepaliAutocorrect.getSuggestion("blanktest")
        NepaliAutocorrect.learnWord("", "नेपाल")       // blank roman — ignored
        NepaliAutocorrect.learnWord("blanktest", "")   // blank deva — ignored
        val after = NepaliAutocorrect.getSuggestion("blanktest")
        assertEquals(before, after) // nothing changed
    }

    // =========================================================
    // 10. wouldAutocorrect
    // =========================================================

    @Test fun testWouldAutocorrect() {
        assertTrue(NepaliAutocorrect.wouldAutocorrect("nepal"))
        assertTrue(NepaliAutocorrect.wouldAutocorrect("ghar"))
        assertTrue(NepaliAutocorrect.wouldAutocorrect("namaste"))
        assertTrue(NepaliAutocorrect.wouldAutocorrect("kaam"))    // it IS in dict
        assertFalse(NepaliAutocorrect.wouldAutocorrect("xyz"))    // unknown
        assertFalse(NepaliAutocorrect.wouldAutocorrect(""))       // empty
    }

    @Test fun testWouldAutocorrectCaseInsensitive() {
        assertTrue(NepaliAutocorrect.wouldAutocorrect("Nepal"))
        assertTrue(NepaliAutocorrect.wouldAutocorrect("NEPAL"))
        assertTrue(NepaliAutocorrect.wouldAutocorrect("GHAR"))
    }

    // =========================================================
    // 11. Transliterator — shortcut keys
    // =========================================================

    @Test fun testShortcutKeys() {
        // F → फ (pha)
        assertEquals("फ", NepaliTransliterator.transliterateWord("F"))
        // B → भ (bha)
        assertEquals("भ", NepaliTransliterator.transliterateWord("B"))
        // L → ळ (retroflex lateral)
        assertEquals("ळ", NepaliTransliterator.transliterateWord("L"))
        // C → छ (chha)
        assertEquals("छ", NepaliTransliterator.transliterateWord("C"))
        // X → क्ष (ksha)
        assertEquals("क्ष", NepaliTransliterator.transliterateWord("X"))
        // Z → ज्ञ (gya/jnya)
        assertEquals("ज्ञ", NepaliTransliterator.transliterateWord("Z"))
        // Q → क्व (kva)
        assertEquals("क्व", NepaliTransliterator.transliterateWord("Q"))
        // f (lowercase) → फ, same as F
        assertEquals("फ", NepaliTransliterator.transliterateWord("f"))
        // x (lowercase) → क्ष
        assertEquals("क्ष", NepaliTransliterator.transliterateWord("x"))
        // z (lowercase) → झ (jha) — distinct from Z (ज्ञ)
        assertEquals("झ", NepaliTransliterator.transliterateWord("z"))
    }

    // =========================================================
    // 12. Transliterator — conjuncts and multi-char sequences
    // =========================================================

    @Test fun testConjuncts() {
        // tr → त्र
        assertEquals("त्र", NepaliTransliterator.transliterateWord("tr"))
        // gn → ज्ञ
        assertEquals("ज्ञ", NepaliTransliterator.transliterateWord("gn"))
        // ksh → क्ष (3-char must win over k+sh)
        assertEquals("क्ष", NepaliTransliterator.transliterateWord("ksh"))
        // chh → छ (3-char must win over ch+h)
        assertEquals("छ", NepaliTransliterator.transliterateWord("chh"))
        // shh → ष (3-char must win over sh+h, regression for moved mapping)
        assertEquals("ष", NepaliTransliterator.transliterateWord("shh"))
        // shha → ष (inherent 'a' is silent in Devanagari — no matra written)
        assertEquals("ष", NepaliTransliterator.transliterateWord("shha"))
        // shhaa → षा (long ā matra via aa while still post-consonant)
        assertEquals("षा", NepaliTransliterator.transliterateWord("shhaa"))
        // ndr → न्द्र
        assertEquals("न्द्र", NepaliTransliterator.transliterateWord("ndr"))
        // str → स्त्र
        assertEquals("स्त्र", NepaliTransliterator.transliterateWord("str"))
    }

    @Test fun testDoubleConsonants() {
        // kk → क्क (virama between two ka)
        assertEquals("क्क", NepaliTransliterator.transliterateWord("kk"))
        // tt → त्त
        assertEquals("त्त", NepaliTransliterator.transliterateWord("tt"))
        // nn → न्न
        assertEquals("न्न", NepaliTransliterator.transliterateWord("nn"))
        // mm → म्म
        assertEquals("म्म", NepaliTransliterator.transliterateWord("mm"))
        // ppr → प्र (p+p+r — two viramas)
        assertEquals("प्प्र", NepaliTransliterator.transliterateWord("ppr"))
    }

    @Test fun testRetroflexVsDental() {
        // T (capital) → ट (retroflex)
        assertEquals("ट", NepaliTransliterator.transliterateWord("T"))
        // t (lowercase) → त (dental)
        assertEquals("त", NepaliTransliterator.transliterateWord("t"))
        // D (capital) → ड (retroflex)
        assertEquals("ड", NepaliTransliterator.transliterateWord("D"))
        // d (lowercase) → द (dental)
        assertEquals("द", NepaliTransliterator.transliterateWord("d"))
        // N (capital) → ण (retroflex nasal)
        assertEquals("ण", NepaliTransliterator.transliterateWord("N"))
        // n (lowercase) → न (dental nasal)
        assertEquals("न", NepaliTransliterator.transliterateWord("n"))
        // Sh → ष (retroflex sibilant)
        assertEquals("ष", NepaliTransliterator.transliterateWord("Sh"))
        // sh → श (palatal sibilant)
        assertEquals("श", NepaliTransliterator.transliterateWord("sh"))
    }
}

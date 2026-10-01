package com.paoneking.nepallipikeyboard.latin

import com.paoneking.nepallipikeyboard.latin.utils.transliteration.NepalBhasaAutocorrect
import com.paoneking.nepallipikeyboard.latin.utils.transliteration.NepalBhasaWordDictionary
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class NepalBhasaAutocorrectTest {

    @Test
    fun testSuggestions() {
        // "nepal" should autocorrect to 𑐣𑐾𑐥𑐵𑐮 even if raw is 𑐣𑐾𑐥𑐮
        assertEquals("𑐣𑐾𑐥𑐵𑐮", NepalBhasaAutocorrect.getSuggestion("nepal"))
        // "juju" (king)
        assertEquals("𑐖𑐸𑐖𑐸", NepalBhasaAutocorrect.getSuggestion("juju"))
    }

    @Test
    fun testFallbackToTransliterator() {
        // Words not in dictionary fall back to raw transliteration
        val result = NepalBhasaAutocorrect.getSuggestion("kha")
        // kha → 𑐏 (single kha consonant)
        assertEquals("𑐏", result)
        // Unknown word still produces Newa output (not empty or exception)
        val unknown = NepalBhasaAutocorrect.getSuggestion("zzz")
        assertNotNull(unknown)
        assertTrue(unknown.isNotEmpty())
    }

    @Test
    fun testEdgeCases() {
        // Empty string
        val empty = NepalBhasaAutocorrect.getSuggestion("")
        assertNotNull(empty)
        // Single letter
        val single = NepalBhasaAutocorrect.getSuggestion("k")
        assertNotNull(single)
        assertTrue(single.isNotEmpty())
    }

    @Test
    fun testSuggestionsNotEmpty() {
        val suggestions = NepalBhasaAutocorrect.getSuggestionsWithFrequencies("ne", 10)
        assertFalse(suggestions.isEmpty())
    }

    @Test
    fun testSuggestionsDeduplicated() {
        val suggestions = NepalBhasaAutocorrect.getSuggestionsWithFrequencies("nepal", 20)
        val words = suggestions.map { it.first }
        assertEquals(words.distinct().size, words.size)
    }

    @Test
    fun testDictionaryBuiltInWords() {
        // Built-in place names should be in dictionary
        assertTrue(NepalBhasaWordDictionary.hasDictionaryEntry("nepal"))
        assertTrue(NepalBhasaWordDictionary.hasDictionaryEntry("kathmandu"))
        assertTrue(NepalBhasaWordDictionary.hasDictionaryEntry("newa"))
        // Correct Newa codepoints for common words
        assertEquals("𑐣𑐾𑐥𑐵𑐮", NepalBhasaWordDictionary.lookup("nepal"))
        assertEquals("𑐣𑐾𑐰𑐵", NepalBhasaWordDictionary.lookup("newa"))
    }

    @Test
    fun testDictionaryCommonWords() {
        // Single-syllable words with correct Newa codepoints (verified fixes)
        assertEquals("𑐟", NepalBhasaWordDictionary.lookup("ta"))  // TA U+1141F
        assertEquals("𑐩", NepalBhasaWordDictionary.lookup("ma"))  // MA U+11429
        assertEquals("𑐣", NepalBhasaWordDictionary.lookup("na"))  // NA U+11423
        assertEquals("𑐥", NepalBhasaWordDictionary.lookup("pa"))  // PA U+11425
        assertEquals("𑐧", NepalBhasaWordDictionary.lookup("ba"))  // BA U+11427
        assertEquals("𑐳", NepalBhasaWordDictionary.lookup("sa"))  // SA U+11433
        assertEquals("𑐡", NepalBhasaWordDictionary.lookup("da"))  // DA U+11421
        assertEquals("𑐫", NepalBhasaWordDictionary.lookup("ya"))  // YA U+1142B
        assertEquals("𑐬", NepalBhasaWordDictionary.lookup("ra"))  // RA U+1142C
        assertEquals("𑐮", NepalBhasaWordDictionary.lookup("la"))  // LA U+1142E
    }

    @Test
    fun testDictionaryNumbers() {
        // 1-3 confirmed by a Nepal Bhasa speaker. "chhi" is one; it previously mapped to
        // zero while a non-word, "thi", held one.
        assertEquals("𑑑", NepalBhasaWordDictionary.lookup("chhi"))  // 1
        assertEquals("𑑒", NepalBhasaWordDictionary.lookup("nhi"))   // 2
        assertEquals("𑑓", NepalBhasaWordDictionary.lookup("swo"))   // 3
        assertNull(NepalBhasaWordDictionary.lookup("thi"))           // not a numeral
        // numbers must stay distinct (regression: nhi was once duplicated for 2 and 7)
        assertEquals("𑑗", NepalBhasaWordDictionary.lookup("nhye"))  // 7
        assertEquals("𑑘", NepalBhasaWordDictionary.lookup("tya"))   // 8
    }

    @Test
    fun testLooseLookup() {
        // lookalike: a → aa variant should still find entries
        val result = NepalBhasaAutocorrect.getSuggestion("nepaal")
        assertEquals("𑐣𑐾𑐥𑐵𑐮", result)
    }

    @Test
    fun testLearnWord() {
        val roman = "testword_newa"
        val newa = "𑐟𑐾𑐳𑑂𑐚"
        NepalBhasaAutocorrect.learnWord(roman, newa)
        assertEquals(newa, NepalBhasaAutocorrect.getSuggestion(roman))
    }

    @Test
    fun testCaseInsensitive() {
        // Lookup should be case-insensitive
        val lower = NepalBhasaAutocorrect.getSuggestion("nepal")
        val upper = NepalBhasaAutocorrect.getSuggestion("Nepal")
        assertEquals(lower, upper)
    }
}

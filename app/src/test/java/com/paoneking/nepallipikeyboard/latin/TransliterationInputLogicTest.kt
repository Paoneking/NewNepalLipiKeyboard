package com.paoneking.nepallipikeyboard.latin

import com.paoneking.nepallipikeyboard.latin.utils.transliteration.NepaliAutocorrect
import com.paoneking.nepallipikeyboard.latin.utils.transliteration.NepaliTransliterator
import kotlin.test.Test
import kotlin.test.assertEquals

class NepaliTransliteratorTest {

    private fun t(input: String) = NepaliTransliterator.transliterateWord(input)
    private fun tSmart(input: String) = NepaliAutocorrect.getRawTransliteration(input)

    // =========================================================
    // 1. STANDALONE VOWELS
    // 1. STANDALONE VOWELS
    // =========================================================
    @Test fun testStandaloneVowels() {
        assertEquals("अ", t("a"))
        assertEquals("आ", t("aa"))
        assertEquals("आ", t("A"))
        assertEquals("इ", t("i"))
        assertEquals("ई", t("ii"))
        assertEquals("ई", t("I"))
        assertEquals("उ", t("u"))
        assertEquals("ऊ", t("uu"))
        assertEquals("ऊ", t("U"))
        assertEquals("ए", t("e"))
        assertEquals("ऐ", t("ai"))
        assertEquals("ओ", t("o"))
        assertEquals("औ", t("au"))
        assertEquals("ऋ", t("R"))
        assertEquals("ऋ", t("rri"))
        assertEquals("रि", t("ri"))
    }

    // =========================================================
    // 2. SINGLE CONSONANTS (bare, no vowel = no trailing अ)
    // =========================================================
    @Test fun testBareConsonants() {
        assertEquals("क", t("k"))
        assertEquals("ख", t("kh"))
        assertEquals("ग", t("g"))
        assertEquals("घ", t("gh"))
        assertEquals("ङ", t("ng"))
        assertEquals("च", t("c"))
        assertEquals("च", t("ch"))
        assertEquals("छ", t("Ch"))
        assertEquals("ज", t("j"))
        assertEquals("झ", t("jh"))
        assertEquals("ञ", t("nh"))
        assertEquals("ट", t("T"))
        assertEquals("ठ", t("Th"))
        assertEquals("ड", t("D"))
        assertEquals("ढ", t("Dh"))
        assertEquals("ण", t("N"))
        assertEquals("त", t("t"))
        assertEquals("थ", t("th"))
        assertEquals("द", t("d"))
        assertEquals("ध", t("dh"))
        assertEquals("न", t("n"))
        assertEquals("प", t("p"))
        assertEquals("फ", t("ph"))
        assertEquals("ब", t("b"))
        assertEquals("भ", t("bh"))
        assertEquals("म", t("m"))
        assertEquals("य", t("y"))
        assertEquals("र", t("r"))
        assertEquals("ल", t("l"))
        assertEquals("व", t("v"))
        assertEquals("व", t("w"))
        assertEquals("श", t("sh"))
        assertEquals("ष", t("Sh"))
        assertEquals("स", t("s"))
        assertEquals("ह", t("h"))
    }

    // =========================================================
    // 3. CONSONANT + INHERENT 'A' (ka, ga, etc.)
    // =========================================================
    @Test fun testConsonantWithInherentA() {
        assertEquals("क", t("ka"))
        assertEquals("ख", t("kha"))
        assertEquals("ग", t("ga"))
        assertEquals("घ", t("gha"))
        assertEquals("च", t("cha"))
        assertEquals("ज", t("ja"))
        assertEquals("त", t("ta"))
        assertEquals("थ", t("tha"))
        assertEquals("द", t("da"))
        assertEquals("ध", t("dha"))
        assertEquals("न", t("na"))
        assertEquals("प", t("pa"))
        assertEquals("फ", t("pha"))
        assertEquals("ब", t("ba"))
        assertEquals("भ", t("bha"))
        assertEquals("म", t("ma"))
        assertEquals("र", t("ra"))
        assertEquals("ल", t("la"))
        assertEquals("स", t("sa"))
        assertEquals("ह", t("ha"))
        assertEquals("व", t("va"))
        assertEquals("व", t("wa"))
        assertEquals("श", t("sha"))
        assertEquals("ष", t("Sha"))
        assertEquals("ट", t("Ta"))
        assertEquals("ड", t("Da"))
        assertEquals("ण", t("Na"))
    }

    // =========================================================
    // 4. CONSONANT + ALL MATRAS
    // =========================================================
    @Test fun testConsonantWithAllMatras() {
        // क with every vowel
        assertEquals("का", t("kaa"))
        assertEquals("का", t("kA"))
        assertEquals("कि", t("ki"))
        assertEquals("की", t("kii"))
        assertEquals("की", t("kI"))
        assertEquals("कु", t("ku"))
        assertEquals("कू", t("kuu"))
        assertEquals("कू", t("kU"))
        assertEquals("के", t("ke"))
        assertEquals("कै", t("kai"))
        assertEquals("को", t("ko"))
        assertEquals("कौ", t("kau"))
        assertEquals("कृ", t("kR"))
        assertEquals("कृ", t("krri"))
        assertEquals("क्रि", t("kri"))
        // ह with every vowel
        assertEquals("हा", t("haa"))
        assertEquals("हि", t("hi"))
        assertEquals("ही", t("hii"))
        assertEquals("हु", t("hu"))
        assertEquals("हू", t("huu"))
        assertEquals("हे", t("he"))
        assertEquals("है", t("hai"))
        assertEquals("हो", t("ho"))
        assertEquals("हौ", t("hau"))
    }

    // =========================================================
    // 5. CONJUNCTS (virama between consonants)
    // =========================================================
    @Test fun testConjuncts() {
        assertEquals("क्क", t("kka"))
        assertEquals("क्त", t("kta"))
        assertEquals("क्न", t("kna"))
        assertEquals("क्म", t("kma"))
        assertEquals("क्य", t("kya"))
        assertEquals("क्र", t("kra"))
        assertEquals("क्ल", t("kla"))
        assertEquals("क्व", t("kva"))
        assertEquals("क्ष", t("ksha"))
        assertEquals("त्त", t("tta"))
        assertEquals("त्र", t("tra"))
        assertEquals("त्न", t("tna"))
        assertEquals("त्म", t("tma"))
        assertEquals("त्य", t("tya"))
        assertEquals("त्व", t("tva"))
        assertEquals("द्ध", t("ddha"))
        assertEquals("द्व", t("dva"))
        assertEquals("द्य", t("dya"))
        assertEquals("द्म", t("dma"))
        assertEquals("न्त", t("nta"))
        assertEquals("न्द", t("nda"))
        assertEquals("न्ध", t("ndha"))
        assertEquals("न्न", t("nna"))
        assertEquals("न्म", t("nma"))
        assertEquals("न्य", t("nya"))
        assertEquals("न्र", t("nra"))
        assertEquals("न्व", t("nva"))
        assertEquals("म्म", t("mma"))
        assertEquals("म्न", t("mna"))
        assertEquals("म्प", t("mpa"))
        assertEquals("म्ब", t("mba"))
        assertEquals("म्भ", t("mbha"))
        assertEquals("ल्ल", t("lla"))
        assertEquals("ल्य", t("lya"))
        assertEquals("ल्व", t("lva"))
        assertEquals("ज्ञ", t("gna"))
        assertEquals("श्र", t("shra"))
        assertEquals("ष्ट", t("ShTa"))
        assertEquals("ष्ठ", t("ShTha"))
        assertEquals("ह्न", t("hna"))
        assertEquals("ह्म", t("hma"))
        assertEquals("ह्य", t("hya"))
        assertEquals("ह्र", t("hra"))
        assertEquals("ह्व", t("hva"))
        assertEquals("ह्ल", t("hla"))
    }

    // =========================================================
    // 6. TRIPLE CONJUNCTS
    // =========================================================
    @Test fun testTripleConjuncts() {
        assertEquals("क्ष्म", t("kshma"))
        assertEquals("त्र्य", t("trya"))
        assertEquals("स्त्र", t("stra"))
        assertEquals("न्त्र", t("ntra"))
        assertEquals("म्प्र", t("mpra"))
        assertEquals("ष्ट्र", t("ShTra"))
    }

    // =========================================================
    // 7. DIACRITICS (anusvara, visarga)
    // =========================================================
    @Test fun testDiacritics() {
        assertEquals("अं", t("aM"))
        assertEquals("अः", t("aH"))
        assertEquals("कं", t("kaM"))
        assertEquals("कः", t("kaH"))
        assertEquals("नं", t("naM"))
        assertEquals("हंस", t("haMsa"))
        assertEquals("अंक", t("aMka"))
        assertEquals("दुःख", t("duHkha"))
    }

    // =========================================================
    // 8. COMMON NEPALI WORDS
    // =========================================================
    @Test fun testCommonWords() {
        assertEquals("नमस्ते", tSmart("namaste"))
        assertEquals("नेपाल", tSmart("nepal"))
        assertEquals("धन्यवाद", tSmart("dhanyabaad"))
        assertEquals("काठमाडौं", tSmart("kaaThmaDauM"))
        assertEquals("राम", tSmart("raam"))
        assertEquals("सीता", tSmart("siitaa"))
        assertEquals("पानी", tSmart("paanii"))
        assertEquals("घर", tSmart("ghar"))
        assertEquals("मान्छे", tSmart("maanche"))
        assertEquals("खाना", tSmart("khaanaa"))
        assertEquals("पढ्नु", tSmart("paDhnu"))
        assertEquals("लेख्नु", tSmart("lekhnu"))
        assertEquals("बोल्नु", tSmart("bolnu"))
        assertEquals("जानु", tSmart("jaanu"))
        assertEquals("आउनु", tSmart("aaunu"))
        assertEquals("गर्नु", tSmart("garnu"))
        assertEquals("हुनु", tSmart("hunu"))
        assertEquals("भन्नु", tSmart("bhannu"))
        assertEquals("सुन्नु", tSmart("sunnu"))
        assertEquals("देख्नु", tSmart("dekhnu"))
    }

    // =========================================================
    // 9. NUMBERS AND PUNCTUATION PASSTHROUGH
    // =========================================================
    @Test fun testNumbersAndPunctuation() {
        assertEquals("1", t("1"))
        assertEquals("2", t("2"))
        assertEquals("123", t("123"))
        assertEquals("!", t("!"))
        assertEquals("?", t("?"))
        assertEquals(",", t(","))
        assertEquals(".", t("."))
        assertEquals("क।", t("ka।"))
        assertEquals("क।।", t("ka।।"))
    }

    // =========================================================
    // 10. MIXED NEPALI + NUMBERS
    // =========================================================
    @Test fun testMixedInput() {
        assertEquals("राम1", tSmart("raam1"))
        assertEquals("नेपाल2", tSmart("nepaal2"))
        assertEquals("क+ख", tSmart("ka+kha"))
        // Test capitalized swiped input (gesture bug fix)
        assertEquals("कुरा", tSmart("Kuraa"))
        assertEquals("नेपाली", tSmart("Nepali"))
    }

    // =========================================================
    // 11. EMPTY AND EDGE CASES
    // =========================================================
    @Test fun testEdgeCases() {
        assertEquals("", t(""))
        assertEquals("अ", t("a"))
        assertEquals("क", t("k"))
        // Double vowels
        assertEquals("आअ", t("aaa"))   // aa + a
        assertEquals("ईइ", t("iii"))   // ii + i
        // Vowel at start then consonant
        assertEquals("अक", t("aka"))
        assertEquals("आग", t("aaga"))
        assertEquals("इन", t("ina"))
        // Consonant cluster at end (no vowel)
        assertEquals("अन्त", tSmart("anta"))
        assertEquals("वर्ष", tSmart("varSha"))
        assertEquals("धर्म", tSmart("dharma"))
        assertEquals("कर्म", tSmart("karma"))
    }

    // =========================================================
    // 12. LONG SENTENCES
    // =========================================================
    @Test fun testSentences() {
        // "राम घर जान्छ" (Ram goes home)
        assertEquals("राम", tSmart("raam"))
        assertEquals("घर", tSmart("ghar"))
        assertEquals("जान्छ", tSmart("jaancha"))
        // "म नेपाली हुँ" (I am Nepali)
        assertEquals("म", tSmart("ma"))
        assertEquals("नेपाली", tSmart("nepaali"))
        // "मेरो नाम राम हो" (My name is Ram)
        assertEquals("मेरो", tSmart("mero"))
        assertEquals("नाम", tSmart("naam"))
        assertEquals("हो", tSmart("ho"))
    }

    // =========================================================
    // 13. AMBIGUOUS SEQUENCES
    // =========================================================
    @Test fun testAmbiguous() {
        // "sh" should be श not स+ह
        assertEquals("श", t("sh"))
        assertEquals("शा", t("shaa"))
        // "th" should be थ not त+ह
        assertEquals("थ", t("th"))
        // "kh" should be ख not क+ह
        assertEquals("ख", t("kh"))
        // "ph" should be फ not प+ह
        assertEquals("फ", t("ph"))
        // "gh" should be घ not ग+ह
        assertEquals("घ", t("gh"))
        // "dh" should be ध not द+ह
        assertEquals("ध", t("dh"))
        // "bh" should be भ not ब+ह
        assertEquals("भ", t("bh"))
        // "ch" should be च not (c+h)
        assertEquals("च", t("ch"))
        // "ksh" should be क्ष not क+श
        assertEquals("क्ष", t("ksh"))
    }

    // =========================================================
    // 14. HALANTA (explicit virama at end)
    // =========================================================
    @Test fun testHalanta() {
        // Consonant followed by explicit halanta marker
        assertEquals("क्", t("k्"))
        assertEquals("त्", t("t्"))
        assertEquals("न्", t("n्"))
    }

    // =========================================================
    // 15. ANUSVARA IN WORDS
    // =========================================================
    @Test fun testAnusvaraInWords() {
        assertEquals("संस्कृत", tSmart("saMskrit"))
        assertEquals("अंग्रेजी", tSmart("aMgreji"))
        assertEquals("हिंदी", tSmart("hiMdi"))
        assertEquals("बंगाल", tSmart("baMgaal"))
        assertEquals("रंग", tSmart("raMga"))
        assertEquals("मंगल", tSmart("maMgal"))
        assertEquals("पंख", tSmart("paMkha"))
    }
}

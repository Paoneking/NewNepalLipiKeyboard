// SPDX-License-Identifier: GPL-3.0-only
package com.paoneking.nepallipikeyboard.latin

import com.paoneking.nepallipikeyboard.latin.utils.NepalLipiConverter
import com.paoneking.nepallipikeyboard.latin.utils.transliteration.NepalBhasaWordDictionary
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

/**
 * Newa codepoints are hard to eyeball, so several built-in entries shipped as garbage:
 * "namaste" decoded to नफ्ष्ढे and "kathmandu" to येय्, which ends in a dangling
 * half-consonant. Round-tripping each entry through NepalLipiConverter turns the Newa back
 * into Devanagari, where malformed output is obvious and machine-checkable.
 *
 * This catches the shape of the bug without needing to know the language: no orthography
 * judgement, just structural validity.
 */
class NepalBhasaDictionarySanityTest {

    private fun entries() = NepalBhasaWordDictionary.getPrefixMatchesWithFrequency("", 10000)

    @Test fun `no entry ends in a dangling virama`() {
        val bad = entries()
            .map { it.first to NepalLipiConverter.convertToDevanagariSync(it.second) }
            .filter { it.second.endsWith("्") }
        assertTrue(bad.isEmpty(), "entries ending in a half-consonant: $bad")
    }

    @Test fun `no entry contains an invalid virama-ha cluster`() {
        // ज्ह and friends are not written clusters; the aspirate has its own letter (झ)
        val bad = entries()
            .map { it.first to NepalLipiConverter.convertToDevanagariSync(it.second) }
            .filter { "्ह" in it.second }
        assertTrue(bad.isEmpty(), "entries with a virama+ha cluster: $bad")
    }

    @Test fun `every entry round-trips through the script converter`() {
        entries().forEach { (roman, newa, _) ->
            val back = NepalLipiConverter.convertToNepalLipiSync(
                NepalLipiConverter.convertToDevanagariSync(newa)
            )
            assertEquals(newa, back, "'$roman' does not survive a Newa->Devanagari->Newa round trip")
        }
    }

    @Test fun `the previously broken entries decode correctly`() {
        fun deva(roman: String) =
            NepalLipiConverter.convertToDevanagariSync(NepalBhasaWordDictionary.lookup(roman)!!)
        assertEquals("नमस्ते", deva("namaste"))
        assertEquals("नमस्कार", deva("namaskar"))
        assertEquals("जुइझार", deva("juijhar"))
        assertEquals("येँ", deva("kathmandu"))     // Yen, the Nepal Bhasa name for Kathmandu
        // these were already right and must stay -- real Nepal Bhasa placenames
        assertEquals("यल", deva("patan"))          // Yala
        assertEquals("ख्वप", deva("bhaktapur"))    // Khwapa
    }
}

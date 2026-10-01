// SPDX-License-Identifier: GPL-3.0-only
package com.paoneking.nepallipikeyboard.latin

import com.paoneking.nepallipikeyboard.latin.utils.transliteration.NepaliWordDictionary
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

/**
 * Loose lookup absorbs the long/short vowel inconsistency of Romanised Nepali input
 * ("nepal" for "nepaal") without flattening pairs where vowel length is the whole
 * difference between two real words.
 */
class LooseLookupTest {

    @Test fun `exact spelling always wins`() {
        assertEquals("नेपाल", NepaliWordDictionary.lookupLoose("nepal"))
        assertEquals("पानी", NepaliWordDictionary.lookupLoose("paani"))
    }

    @Test fun `vowel length variants resolve to the nearest real spelling`() {
        // paani (water) and pani (also) differ only in vowel length; a query that is closer
        // to one must not be answered with the other
        assertEquals("पानी", NepaliWordDictionary.lookupLoose("paanii"))
        assertEquals("पनि", NepaliWordDictionary.lookupLoose("pani"))
    }

    @Test fun `multi-a words are not mangled`() {
        // The old implementation did replace("a", "aa") across the whole word, turning
        // "kathmandu" into "kaathmaandu" and matching nothing.
        assertEquals("काठमाडौं", NepaliWordDictionary.lookupLoose("kathmandu"))
        assertEquals("नमस्ते", NepaliWordDictionary.lookupLoose("namaste"))
        assertEquals("धन्यवाद", NepaliWordDictionary.lookupLoose("dhanyabaad"))
    }

    @Test fun `unknown words still return null`() {
        assertNull(NepaliWordDictionary.lookupLoose("zzzqqq"))
        assertNull(NepaliWordDictionary.lookupLoose(""))
    }
}

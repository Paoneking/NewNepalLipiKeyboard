package com.paoneking.nepallipikeyboard.latin.utils

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

class NepalLipiConverterTest {
    // every letter, sign and digit the converter maps, the clusters included
    private val everyMapped = "अआइईउऊऋॠऌॡएऐओऔ कखगघङचछजझञटठडढणतथदधनपफबभमयरलवशषसह " +
        "कािीुूृॄॢॣेैोौ्ँंः़ ऽ ॐ।॥ ०१२३४५६७८९ ङ्ह ञ्ह न्ह म्ह र्ह ल्ह"

    @Test fun everyMappingSurvivesTheRoundTrip() {
        val newa = NepalLipiConverter.convertToNepalLipiSync(everyMapped)
        assertEquals(everyMapped, NepalLipiConverter.convertToDevanagariSync(newa))
    }

    @Test fun nhaMhaLhaAreSingleNewaLetters() {
        assertEquals("𑐤𑐹𑐐𑐸 𑐪𑐳𑐷𑐎𑐵 𑐯", NepalLipiConverter.convertToNepalLipiSync("न्हूगु म्हसीका ल्ह"))
    }

    @Test fun newaTypedWithTheLettersRoundTrips() {
        val newa = "𑐤𑐹𑐐𑐸 𑐪𑐳𑐷𑐎𑐵 𑐯𑐵"
        assertEquals(newa, NepalLipiConverter.convertScript(NepalLipiConverter.convertScript(newa)!!))
    }

    @Test fun convertScriptPicksTheDirectionFromTheText() {
        assertEquals("𑐣𑐾𑐥𑐵𑐮 𑐨𑐵𑐲𑐵", NepalLipiConverter.convertScript("नेपाल भाषा"))
        assertEquals("नेपाल भाषा", NepalLipiConverter.convertScript("𑐣𑐾𑐥𑐵𑐮 𑐨𑐵𑐲𑐵"))
    }

    @Test fun convertScriptLeavesOtherTextAlone() {
        assertEquals("Hello 𑐣𑐾𑐥𑐵𑐮 2026 🙂", NepalLipiConverter.convertScript("Hello नेपाल 2026 🙂"))
        assertNull(NepalLipiConverter.convertScript("Hello 2026"))
    }
}

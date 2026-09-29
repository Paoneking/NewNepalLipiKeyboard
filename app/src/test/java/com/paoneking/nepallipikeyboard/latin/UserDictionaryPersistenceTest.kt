// SPDX-License-Identifier: GPL-3.0-only
package com.paoneking.nepallipikeyboard.latin

import androidx.test.core.app.ApplicationProvider
import com.paoneking.nepallipikeyboard.latin.utils.transliteration.NepalBhasaWordDictionary
import com.paoneking.nepallipikeyboard.latin.utils.transliteration.NepaliWordDictionary
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.shadows.ShadowLog
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotEquals

/**
 * Words the user teaches the keyboard must survive process death (§8.2). Before this,
 * saveToInternalStorage() had no caller and loadFromInternalStorage() was commented out,
 * so every correction was lost when the IME process went away.
 */
@RunWith(RobolectricTestRunner::class)
class UserDictionaryPersistenceTest {
    private val context = ApplicationProvider.getApplicationContext<App>()

    @BeforeTest fun setup() {
        ShadowLog.setupLogging()
    }

    @Test fun `learned nepali word survives a save and reload`() {
        val roman = "testasabda"
        val script = "टेस्टशब्द"

        NepaliWordDictionary.addWord(roman, script)
        assertEquals(script, NepaliWordDictionary.lookup(roman))

        NepaliWordDictionary.saveToInternalStorage(context, "persistence_test_ne.txt")

        // drop it from memory to prove the value comes back off disk, not from the map
        NepaliWordDictionary.removeWord(roman)
        assertNotEquals(script, NepaliWordDictionary.lookup(roman))

        NepaliWordDictionary.loadFromInternalStorage(context, "persistence_test_ne.txt")
        assertEquals(script, NepaliWordDictionary.lookup(roman), "learned word did not survive reload")
    }

    @Test fun `learned nepal bhasa word survives a save and reload`() {
        val roman = "testajhyaa"
        val script = "𑀣𑀾"   // arbitrary Newa codepoints

        NepalBhasaWordDictionary.addWord(roman, script)
        assertEquals(script, NepalBhasaWordDictionary.lookup(roman))

        NepalBhasaWordDictionary.saveToInternalStorage(context, "persistence_test_new.txt")
        NepalBhasaWordDictionary.removeWord(roman)
        assertNotEquals(script, NepalBhasaWordDictionary.lookup(roman))

        NepalBhasaWordDictionary.loadFromInternalStorage(context, "persistence_test_new.txt")
        assertEquals(script, NepalBhasaWordDictionary.lookup(roman), "learned word did not survive reload")
    }

    @Test fun `frequency round-trips so suggestion ranking is preserved`() {
        val roman = "testabaaraमbaarata"
        NepaliWordDictionary.addWord(roman, "टे", frequency = 233)
        NepaliWordDictionary.saveToInternalStorage(context, "persistence_test_freq.txt")
        NepaliWordDictionary.removeWord(roman)
        NepaliWordDictionary.loadFromInternalStorage(context, "persistence_test_freq.txt")
        assertEquals(233, NepaliWordDictionary.getFrequency(roman), "frequency was not persisted")
    }
}

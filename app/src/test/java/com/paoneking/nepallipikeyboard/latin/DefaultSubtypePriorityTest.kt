// SPDX-License-Identifier: GPL-3.0-only
package com.paoneking.nepallipikeyboard.latin

import com.paoneking.nepallipikeyboard.latin.settings.Defaults
import kotlin.test.Test
import kotlin.test.assertTrue

/**
 * This is a Nepal Bhasa keyboard. A fresh install must come up in Nepal Bhasa, with Nepali
 * next and English after that, and all three must appear checked in Languages & Layouts.
 *
 * Two things decide that, and both are asserted here:
 *  - [Defaults.PREF_ENABLED_SUBTYPES] — preloaded so the three show as enabled in the UI.
 *    getSelectedSubtype() falls back to the first enabled subtype, so its order is the
 *    keyboard a fresh install starts typing with.
 *  - SubtypeSettings.getDefaultEnabledSubtypes() adds new-NP, then ne-NP, then the system
 *    locale, which is the fallback path when the pref has been cleared.
 *
 * Deliberate consequence: upstream's InputTest assumes a Latin QWERTY default and looks up
 * an 'a' key, so it fails in this fork. See docs/transliteration.md §8.8.
 *
 * Plain JVM test on purpose — the Robolectric variant of this shared SubtypeSettings'
 * singleton state with SubtypeTest and failed depending on execution order.
 */
class DefaultSubtypePriorityTest {

    private val entries: List<String> =
        Defaults.PREF_ENABLED_SUBTYPES.split(";").filter { it.isNotEmpty() }

    private val languages: List<String> = entries.map { it.substringBefore("§") }

    @Test fun `nepal bhasa is the default keyboard on a fresh install`() {
        assertTrue(
            languages.firstOrNull() == "new-NP",
            "first enabled subtype must be new-NP, got ${languages.firstOrNull()} in $languages"
        )
    }

    @Test fun `priority is nepal bhasa then nepali then english`() {
        val newa = languages.indexOf("new-NP")
        val nepali = languages.indexOf("ne-NP")
        val english = languages.indexOf("en-US")
        assertTrue(newa >= 0, "new-NP missing from defaults: $languages")
        assertTrue(nepali >= 0, "ne-NP missing from defaults: $languages")
        assertTrue(english >= 0, "en-US missing from defaults: $languages")
        assertTrue(newa < nepali, "Nepal Bhasa must precede Nepali: $languages")
        assertTrue(nepali < english, "Nepali must precede English: $languages")
    }

    @Test fun `all three languages are preloaded so they show checked in settings`() {
        assertTrue(
            languages.toSet() == setOf("new-NP", "ne-NP", "en-US"),
            "expected exactly new-NP, ne-NP and en-US to be preloaded, got ${languages.toSet()}"
        )
    }

    @Test fun `both transliteration subtypes are preloaded`() {
        // A transliteration subtype carries no KeyboardLayoutSet - that is what distinguishes
        // it from the traditional and romanized layouts of the same language.
        val transliteration = entries
            .filterNot { it.contains("KeyboardLayoutSet") }
            .map { it.substringBefore("§") }
            .toSet()
        assertTrue("new-NP" in transliteration, "Nepal Bhasa transliteration missing: $transliteration")
        assertTrue("ne-NP" in transliteration, "Nepali transliteration missing: $transliteration")
    }
}

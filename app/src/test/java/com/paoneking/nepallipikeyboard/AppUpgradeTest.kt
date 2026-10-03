package com.paoneking.nepallipikeyboard

import com.paoneking.nepallipikeyboard.latin.LatinIME
import com.paoneking.nepallipikeyboard.latin.checkVersionUpgrade
import com.paoneking.nepallipikeyboard.latin.common.Constants.Separators
import com.paoneking.nepallipikeyboard.latin.settings.Defaults
import com.paoneking.nepallipikeyboard.latin.settings.Settings
import com.paoneking.nepallipikeyboard.latin.settings.createPrefKeyForBooleanSettings
import com.paoneking.nepallipikeyboard.latin.utils.DeviceProtectedUtils
import com.paoneking.nepallipikeyboard.latin.utils.LayoutType
import com.paoneking.nepallipikeyboard.latin.utils.LayoutType.Companion.folder
import com.paoneking.nepallipikeyboard.latin.utils.prefs
import org.junit.runner.RunWith
import java.io.File
import org.robolectric.Robolectric
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

/** Upgrades between this fork's versionCodes must not replay upstream's migrations (see AppUpgrade). */
@RunWith(RobolectricTestRunner::class)
@Config(shadows = [ShadowInputMethodManager2::class])
class AppUpgradeTest {
    private val latinIME = Robolectric.setupService(LatinIME::class.java)
    private val prefs = latinIME.prefs()
    private val gapKeys = (0..3).map { createPrefKeyForBooleanSettings(Settings.PREF_KEY_GAP_SCALE_PREFIX, it, 2) }

    // the 1.1.0 default subtypes, as a 1.1.0 install stored them
    private val v110Enabled = "new-NP§KeyboardLayoutSet=MAIN:nepalbhasa_traditional,NoShiftProximityCorrection;" +
        "new-NP§KeyboardLayoutSet=MAIN:nepalbhasa_romanized,NoShiftProximityCorrection;" +
        "new-NP§SupportTouchPositionCorrection,TrySuppressingImeSwitcher;" +
        "ne-NP§KeyboardLayoutSet=MAIN:nepali_romanized,NoShiftProximityCorrection;" +
        "ne-NP§KeyboardLayoutSet=MAIN:nepali_traditional,NoShiftProximityCorrection;" +
        "ne-NP§SupportTouchPositionCorrection,TrySuppressingImeSwitcher;" +
        "en-US§SupportTouchPositionCorrection,TrySuppressingImeSwitcher"

    // prefs outlive a test here, so one test's values would leak into the next
    @BeforeTest fun clearPrefs() {
        prefs.edit().clear().commit()
    }

    @Test fun upgradeFrom110KeepsEverySubtype() {
        val selected = "ne-NP§KeyboardLayoutSet=MAIN:nepali_romanized,NoShiftProximityCorrection"
        prefs.edit()
            .putString(Settings.PREF_ENABLED_SUBTYPES, v110Enabled)
            .putString(Settings.PREF_SELECTED_SUBTYPE, selected)
            .putString(Settings.PREF_ADDITIONAL_SUBTYPES, Defaults.PREF_ADDITIONAL_SUBTYPES)
            .putInt(Settings.PREF_VERSION_CODE, 2)
            .commit()

        checkVersionUpgrade(latinIME)

        assertEquals(v110Enabled, prefs.getString(Settings.PREF_ENABLED_SUBTYPES, null))
        assertEquals(selected, prefs.getString(Settings.PREF_SELECTED_SUBTYPE, null))
        assertEquals(Defaults.PREF_ADDITIONAL_SUBTYPES, prefs.getString(Settings.PREF_ADDITIONAL_SUBTYPES, null))
        gapKeys.forEach { assertFalse(prefs.contains(it), it) }
    }

    @Test fun upgradeFromReplayedVersionRepairsWhatTheReplayBroke() {
        // what 1.1.0 -> 1.1.1 -> 1.1.2 left behind, in the shapes the replay produces
        val collapsed = "new-NP§SupportTouchPositionCorrection,TrySuppressingImeSwitcher;" +
            "new-NP§SupportTouchPositionCorrection,TrySuppressingImeSwitcher;" +
            "ne-NP§KeyboardLayoutSet=MAIN:nepali_traditional,NoShiftProximityCorrection;" +
            "ne-NP§KeyboardLayoutSet=MAIN:nepali_traditional,NoShiftProximityCorrection;" +
            "en-US§SupportTouchPositionCorrection,TrySuppressingImeSwitcher"
        // a custom subtype after two replays, and one the user made after them
        val broken = "ne-NP§KeyboardLayoutSet=MAIN,KeyboardLayoutSet=MAIN:KeyboardLayoutSet=MAIN,nepali_traditional|SYMBOLS"
        val intact = "new-NP§KeyboardLayoutSet=MAIN:nepalbhasa_traditional,NoShiftProximityCorrection"
        // the replay renamed layouts/main to layouts/main.
        val filesDir = DeviceProtectedUtils.getFilesDir(latinIME)
        val renamed = File(filesDir, LayoutType.MAIN.folder + ".").apply { mkdirs() }
        File(renamed, "custom.test.").writeText("[]")
        prefs.edit().apply {
            putString(Settings.PREF_ENABLED_SUBTYPES, collapsed)
            putString(Settings.PREF_SELECTED_SUBTYPE, "ne-NP§KeyboardLayoutSet=MAIN:nepali_traditional,NoShiftProximityCorrection")
            putString(Settings.PREF_ADDITIONAL_SUBTYPES, "$broken;$intact")
            putFloat(gapKeys[0], 1.75f) // replayed
            putFloat(gapKeys[1], 1.4f) // changed by the user since
            putInt(Settings.PREF_VERSION_CODE, 4)
        }.commit()

        checkVersionUpgrade(latinIME)

        assertFalse(prefs.contains(Settings.PREF_ENABLED_SUBTYPES))
        assertFalse(prefs.contains(Settings.PREF_SELECTED_SUBTYPE))
        assertEquals(intact, prefs.getString(Settings.PREF_ADDITIONAL_SUBTYPES, null))
        assertFalse(prefs.contains(gapKeys[0]))
        assertEquals(1.4f, prefs.getFloat(gapKeys[1], 0f))
        assertFalse(renamed.exists())
        assertTrue(File(File(filesDir, LayoutType.MAIN.folder), "custom.test.").exists())
    }

    @Test fun upgradeFromReplayedVersionLeavesUntouchedPrefsAlone() {
        // a clean 1.1.2 install with the user's own key gap
        prefs.edit().apply {
            putString(Settings.PREF_ENABLED_SUBTYPES, Defaults.PREF_ENABLED_SUBTYPES)
            putString(Settings.PREF_ADDITIONAL_SUBTYPES, Defaults.PREF_ADDITIONAL_SUBTYPES)
            gapKeys.forEach { putFloat(it, 1.3f) }
            putInt(Settings.PREF_VERSION_CODE, 4)
        }.commit()

        checkVersionUpgrade(latinIME)

        assertEquals(Defaults.PREF_ENABLED_SUBTYPES, prefs.getString(Settings.PREF_ENABLED_SUBTYPES, null))
        assertEquals(Defaults.PREF_ADDITIONAL_SUBTYPES, prefs.getString(Settings.PREF_ADDITIONAL_SUBTYPES, null))
        gapKeys.forEach { assertTrue(prefs.getFloat(it, 0f) == 1.3f, it) }
    }

    @Test fun convertScriptKeyJoinsASavedToolbarAfterSettings() {
        // a toolbar saved by 1.1.4, before the key existed
        prefs.edit()
            .putString(Settings.PREF_TOOLBAR_KEYS, "SETTINGS:true|VOICE:true|CLIPBOARD:false")
            .putInt(Settings.PREF_VERSION_CODE, 6)
            .commit()

        checkVersionUpgrade(latinIME)

        val keys = prefs.getString(Settings.PREF_TOOLBAR_KEYS, null)!!.split(Separators.ENTRY)
        assertEquals(listOf("SETTINGS:true", "CONVERT_SCRIPT:true", "VOICE:true", "CLIPBOARD:false"), keys.take(4))
    }

    @Test fun convertScriptKeyTurnedOffStaysOff() {
        prefs.edit()
            .putString(Settings.PREF_TOOLBAR_KEYS, "SETTINGS:true|CONVERT_SCRIPT:false|VOICE:true")
            .putInt(Settings.PREF_VERSION_CODE, 6)
            .commit()

        checkVersionUpgrade(latinIME)

        val keys = prefs.getString(Settings.PREF_TOOLBAR_KEYS, null)!!.split(Separators.ENTRY)
        assertEquals("CONVERT_SCRIPT:false", keys.single { it.startsWith("CONVERT_SCRIPT:") })
    }
}

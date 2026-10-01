// SPDX-License-Identifier: GPL-3.0-only
package com.paoneking.nepallipikeyboard.settings

import android.content.Context
import com.paoneking.nepallipikeyboard.keyboard.internal.KeyboardIconsSet
import com.paoneking.nepallipikeyboard.latin.settings.Settings
import com.paoneking.nepallipikeyboard.latin.utils.SubtypeSettings

// file is meant for making compose previews work

fun initPreview(context: Context) {
    Settings.init(context)
    SubtypeSettings.init(context)
    Settings.getInstance().loadSettings(context)
    SettingsActivity.settingsContainer = SettingsContainer(context)
    KeyboardIconsSet.instance.loadIcons(context)
}

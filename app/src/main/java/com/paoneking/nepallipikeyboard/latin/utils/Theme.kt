// SPDX-License-Identifier: GPL-3.0-only
package com.paoneking.nepallipikeyboard.latin.utils

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Typography
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.core.graphics.ColorUtils
import com.paoneking.nepallipikeyboard.keyboard.KeyboardTheme
import com.paoneking.nepallipikeyboard.latin.common.ColorType
import com.paoneking.nepallipikeyboard.latin.common.Colors
import com.paoneking.nepallipikeyboard.settings.SettingsActivity

@Composable
fun Theme(@Suppress("UNUSED_PARAMETER") dark: Boolean = isSystemInDarkTheme(), content: @Composable () -> Unit) {
    val material3 = Typography()

    // Re-compose when any keyboard preference changes (e.g. user picks a new theme).
    // The .value must be READ here — without it Compose never registers a snapshot dependency
    // and the Theme composable silently skips recomposition when the selected theme changes.
    (LocalContext.current.getActivity() as? SettingsActivity)?.prefChanged?.collectAsState()?.value

    // Read colors directly from prefs so the theme reflects changes immediately without
    // waiting for the IME to reload its SettingsValues.
    val colorScheme: ColorScheme = keyboardColorScheme(KeyboardTheme.getColorsForCurrentTheme(LocalContext.current))

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography(
            titleLarge = material3.titleLarge.copy(fontWeight = FontWeight.Bold),
            titleMedium = material3.titleMedium.copy(fontWeight = FontWeight.Bold),
            titleSmall = material3.titleSmall.copy(fontWeight = FontWeight.Bold)
        ),
        content = content
    )
}

/**
 * Builds a Material3 [ColorScheme] whose colours mirror the active keyboard theme.
 * Dark vs. light variant is chosen by the perceptual luminance of the keyboard background.
 */
private fun keyboardColorScheme(colors: Colors): ColorScheme {
    val accentInt      = colors.get(ColorType.ACTION_KEY_BACKGROUND)
    val backgroundInt  = colors.get(ColorType.MAIN_BACKGROUND)
    val keyBgInt       = colors.get(ColorType.KEY_BACKGROUND)
    val surfaceInt     = colors.get(ColorType.STRIP_BACKGROUND)
    val keyTextInt     = colors.get(ColorType.KEY_TEXT)
    val keyHintInt     = colors.get(ColorType.KEY_HINT_TEXT)
    val functionalInt  = colors.get(ColorType.FUNCTIONAL_KEY_BACKGROUND)

    val accent     = Color(accentInt)
    val background = Color(backgroundInt)
    val keyBg      = Color(keyBgInt)
    val surface    = Color(surfaceInt)
    val keyText    = Color(keyTextInt)
    val keyHint    = Color(keyHintInt)
    val functional = Color(functionalInt)
    val onAccent   = if (isBrightColor(accentInt)) Color.Black else Color.White

    val isDark = ColorUtils.calculateLuminance(backgroundInt) < 0.5

    return if (isDark) {
        darkColorScheme(
            primary                  = accent,
            onPrimary                = onAccent,
            secondary                = functional,
            onSecondary              = keyText,
            background               = background,
            onBackground             = keyText,
            surface                  = keyBg,
            onSurface                = keyText,
            surfaceVariant           = surface,
            onSurfaceVariant         = keyHint,
            surfaceContainer         = surface,
            surfaceContainerHigh     = keyBg,
            surfaceContainerHighest  = keyBg,
        )
    } else {
        lightColorScheme(
            primary                  = accent,
            onPrimary                = onAccent,
            secondary                = functional,
            onSecondary              = keyText,
            background               = background,
            onBackground             = keyText,
            surface                  = keyBg,
            onSurface                = keyText,
            surfaceVariant           = surface,
            onSurfaceVariant         = keyHint,
            surfaceContainer         = surface,
            surfaceContainerHigh     = keyBg,
            surfaceContainerHighest  = keyBg,
        )
    }
}

const val previewDark = true

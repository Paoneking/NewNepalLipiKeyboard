// SPDX-License-Identifier: GPL-3.0-only
package com.paoneking.nepallipikeyboard.settings.screens

import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.edit
import com.paoneking.nepallipikeyboard.latin.BuildConfig
import com.paoneking.nepallipikeyboard.latin.R
import com.paoneking.nepallipikeyboard.latin.settings.DebugSettings
import com.paoneking.nepallipikeyboard.latin.settings.Defaults
import com.paoneking.nepallipikeyboard.latin.utils.Theme
import com.paoneking.nepallipikeyboard.latin.utils.prefs
import com.paoneking.nepallipikeyboard.latin.utils.previewDark
import com.paoneking.nepallipikeyboard.settings.SearchSettingsScreen
import com.paoneking.nepallipikeyboard.settings.SettingsActivity
import com.paoneking.nepallipikeyboard.settings.SettingsContainer

private const val PRIVACY_URL = "https://paoneking.github.io/NewNepalLipiKeyboard/privacy/"
private const val TAPS_TO_DEBUG_SETTINGS = 5

/**
 * The app itself: what it is, its version, who made it and who supports it.
 * The Foundation's own story is on [AboutCallijatraScreen].
 */
@Composable
fun AboutAppScreen(
    onClickBack: () -> Unit,
    onClickAboutCallijatra: () -> Unit,
) {
    SearchSettingsScreen(
        onClickBack = onClickBack,
        title = stringResource(R.string.settings_screen_about_app),
        settings = emptyList(),
    ) {
        Scaffold(contentWindowInsets = WindowInsets.safeDrawing.only(WindowInsetsSides.Bottom)) { innerPadding ->
            Column(
                Modifier
                    .verticalScroll(rememberScrollState())
                    .then(Modifier.padding(innerPadding))
                    .padding(start = 24.dp, top = 8.dp, end = 24.dp, bottom = 40.dp)
            ) {
                Identity()
                AboutBody(stringResource(R.string.about_app_description))

                AboutHeading(stringResource(R.string.about_app_heading_made_by))
                Box(Modifier.fillMaxWidth().padding(vertical = 6.dp), Alignment.Center) {
                    Image(
                        painter = painterResource(R.drawable.callijatra_foundation),
                        // an alpha mask with no colour of its own, so it takes the theme's ink
                        colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.onSurface),
                        contentScale = ContentScale.Fit,
                        modifier = Modifier.width(170.dp),
                        contentDescription = "Callijatra Foundation",
                    )
                }
                Spacer(Modifier.height(8.dp))
                AboutPageLink(
                    stringResource(R.string.settings_screen_about_callijatra),
                    stringResource(R.string.about_app_callijatra_summary),
                    onClickAboutCallijatra,
                )

                AboutHeading(stringResource(R.string.about_callijatra_supported_by))
                // The fund's wordmark keeps its greens, which vanish on a dark surface, so it sits on a
                // light plate in both themes rather than being recoloured.
                Box(Modifier.fillMaxWidth(), Alignment.Center) {
                    Box(
                        Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(Color(0xFFF7F6F2))
                            .padding(horizontal = 16.dp, vertical = 10.dp),
                    ) {
                        Image(
                            painter = painterResource(R.drawable.ic_global_greengrants),
                            contentDescription = "Global Greengrants Fund",
                            modifier = Modifier.height(24.dp),
                        )
                    }
                }

                AboutHeading(stringResource(R.string.about_app_heading_privacy))
                AboutLink(
                    stringResource(R.string.about_app_privacy_policy),
                    stringResource(R.string.about_app_privacy_summary),
                    PRIVACY_URL,
                )
            }
        }
    }
}

/** Icon, name and version. Five taps on the version turn on the debug settings, as the old Version row did. */
@Composable
private fun Identity() {
    val ctx = LocalContext.current
    val prefs = ctx.prefs()
    var taps by rememberSaveable { mutableIntStateOf(0) }
    Column(Modifier.fillMaxWidth().padding(top = 12.dp, bottom = 18.dp), horizontalAlignment = Alignment.CenterHorizontally) {
        Image(
            painter = painterResource(R.drawable.app_icon),
            contentDescription = null, // the name is right below it
            modifier = Modifier.size(96.dp).clip(RoundedCornerShape(22.dp)),
        )
        Text(
            text = stringResource(R.string.english_ime_name),
            color = MaterialTheme.colorScheme.onSurface,
            fontSize = 19.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(top = 14.dp),
        )
        val unlocked = BuildConfig.DEBUG ||
            prefs.getBoolean(DebugSettings.PREF_SHOW_DEBUG_SETTINGS, Defaults.PREF_SHOW_DEBUG_SETTINGS)
        val unlockLabel = stringResource(R.string.about_app_version_tap_label)
        Text(
            text = stringResource(R.string.version_text, BuildConfig.VERSION_NAME),
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            fontSize = 12.sp,
            modifier = Modifier
                .then(if (unlocked) Modifier else Modifier.clickable(onClickLabel = unlockLabel) {
                    val left = TAPS_TO_DEBUG_SETTINGS - ++taps
                    if (left > 0) {
                        if (left <= 3) Toast.makeText(ctx, ctx.resources.getQuantityString(
                            R.plurals.about_app_version_taps_left, left, left), Toast.LENGTH_SHORT).show()
                        return@clickable
                    }
                    prefs.edit { putBoolean(DebugSettings.PREF_SHOW_DEBUG_SETTINGS, true) }
                    Toast.makeText(ctx, R.string.prefs_debug_settings_enabled, Toast.LENGTH_LONG).show()
                })
                .padding(horizontal = 12.dp, vertical = 6.dp),
        )
    }
}

@Preview
@Composable
private fun Preview() {
    SettingsActivity.settingsContainer = SettingsContainer(LocalContext.current)
    Theme(previewDark) {
        Surface {
            AboutAppScreen({ }, { })
        }
    }
}

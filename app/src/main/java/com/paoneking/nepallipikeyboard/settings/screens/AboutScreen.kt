// SPDX-License-Identifier: GPL-3.0-only
package com.paoneking.nepallipikeyboard.settings.screens

import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.text.method.LinkMovementMethod
import android.view.View
import android.widget.TextView
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalResources
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import com.paoneking.nepallipikeyboard.latin.R
import com.paoneking.nepallipikeyboard.latin.utils.Log
import com.paoneking.nepallipikeyboard.latin.utils.SpannableStringUtils
import com.paoneking.nepallipikeyboard.latin.utils.getActivity
import com.paoneking.nepallipikeyboard.settings.SettingsContainer
import com.paoneking.nepallipikeyboard.settings.SettingsDestination
import com.paoneking.nepallipikeyboard.settings.SettingsWithoutKey
import com.paoneking.nepallipikeyboard.settings.Setting
import com.paoneking.nepallipikeyboard.settings.preferences.Preference
import com.paoneking.nepallipikeyboard.settings.SearchSettingsScreen
import com.paoneking.nepallipikeyboard.settings.SettingsActivity
import com.paoneking.nepallipikeyboard.latin.utils.Theme
import com.paoneking.nepallipikeyboard.latin.utils.previewDark
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

@Composable
fun AboutScreen(
    onClickBack: () -> Unit,
) {
    val items = listOf(
        SettingsWithoutKey.APP,
        SettingsWithoutKey.ABOUT_CALLIJATRA,
        SettingsWithoutKey.HIDDEN_FEATURES,
        SettingsWithoutKey.SAVE_LOG,
    )
    SearchSettingsScreen(
        onClickBack = onClickBack,
        title = stringResource(R.string.settings_screen_about),
        settings = items
    )
}

fun createAboutSettings(context: Context) = listOf(
    Setting(context, SettingsWithoutKey.APP, R.string.settings_screen_about_app, R.string.about_app_summary) {
        Preference(
            name = it.title,
            description = it.description,
            onClick = { SettingsDestination.navigateTo(SettingsDestination.AboutApp) },
            icon = R.mipmap.ic_launcher_round
        )
    },
    Setting(context, SettingsWithoutKey.ABOUT_CALLIJATRA, R.string.settings_screen_about_callijatra, R.string.about_callijatra_summary) {
        Preference(
            name = it.title,
            description = it.description,
            onClick = { SettingsDestination.navigateTo(SettingsDestination.AboutCallijatra) },
            icon = R.drawable.ic_settings_about_community
        )
    },
    Setting(context, SettingsWithoutKey.HIDDEN_FEATURES, R.string.hidden_features_title, R.string.hidden_features_summary) {
        val ctx = LocalContext.current
        val resources = LocalResources.current
        Preference(
            name = it.title,
            description = it.description,
            onClick = {
                // Compose dialogs are in a rather sad state. They don't understand HTML, and don't scroll without customization.
                // this should be re-done in compose, but... bah
                val link = ("<a href=\"https://developer.android.com/reference/android/content/Context#createDeviceProtectedStorageContext()\">"
                        + resources.getString(R.string.hidden_features_text) + "</a>")
                val message = resources.getString(R.string.hidden_features_message, link)
                val dialogMessage = SpannableStringUtils.fromHtml(message)
                val builder = AlertDialog.Builder(ctx)
                    .setIcon(R.drawable.ic_settings_about_hidden_features)
                    .setTitle(R.string.hidden_features_title)
                    .setMessage(dialogMessage)
                    .setPositiveButton(R.string.dialog_close, null)
                    .create()
                builder.show()
                (builder.findViewById<View>(android.R.id.message) as TextView).movementMethod = LinkMovementMethod.getInstance()
            },
            icon = R.drawable.ic_settings_about_hidden_features
        )
    },
    Setting(context, SettingsWithoutKey.SAVE_LOG, R.string.save_log) { setting ->
        val ctx = LocalContext.current
        val resources = LocalResources.current
        val scope = rememberCoroutineScope()
        val launcher = rememberLauncherForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode != Activity.RESULT_OK) return@rememberLauncherForActivityResult
            val uri = result.data?.data ?: return@rememberLauncherForActivityResult
            scope.launch(Dispatchers.IO) {
                ctx.getActivity()?.contentResolver?.openOutputStream(uri)?.use { os ->
                    os.writer().use { writer ->
                        val logcat = Runtime.getRuntime().exec("logcat -d -b all *:W").inputStream.use { it.reader().readText() }
                        val internal = Log.getLog().joinToString("\n")
                        writer.write(logcat + "\n\n" + internal)
                    }
                }
            }
        }
        Preference(
            name = setting.title,
            description = setting.description,
            onClick = {
                val date = SimpleDateFormat("yyyy-MM-dd_HH-mm-ss", Locale.US).format(Calendar.getInstance().time)
                val intent = Intent(Intent.ACTION_CREATE_DOCUMENT)
                    .addCategory(Intent.CATEGORY_OPENABLE)
                    .putExtra(
                        Intent.EXTRA_TITLE,
                        resources.getString(R.string.english_ime_name)
                            .replace(" ", "_") + "_log_$date.txt"
                    )
                    .setType("text/plain")
                launcher.launch(intent)
            },
            icon = R.drawable.ic_settings_about_log
        )
    },
)

@Preview
@Composable
private fun Preview() {
    SettingsActivity.settingsContainer = SettingsContainer(LocalContext.current)
    Theme(previewDark) {
        Surface {
            AboutScreen {  }
        }
    }
}

// SPDX-License-Identifier: GPL-3.0-only
package com.paoneking.nepallipikeyboard.settings.screens

import android.content.Context
import android.provider.UserDictionary
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.paoneking.nepallipikeyboard.latin.R
import com.paoneking.nepallipikeyboard.latin.common.LocaleUtils.localizedDisplayName
import com.paoneking.nepallipikeyboard.latin.personalization.PersonalizationHelper
import com.paoneking.nepallipikeyboard.latin.settings.Defaults
import com.paoneking.nepallipikeyboard.latin.settings.Settings
import com.paoneking.nepallipikeyboard.latin.utils.prefs
import com.paoneking.nepallipikeyboard.settings.DropDownField
import com.paoneking.nepallipikeyboard.settings.SearchScreen
import com.paoneking.nepallipikeyboard.settings.dialogs.ConfirmationDialog
import com.paoneking.nepallipikeyboard.settings.dialogs.ThreeButtonAlertDialog
import java.util.Locale

@Composable
fun UserHistoryWordsScreen(
    onClickBack: () -> Unit,
    locale: Locale
) {
    val context = LocalContext.current
    var words by remember { mutableStateOf(getUserHistoryWords(context, locale)) }
    var selectedWord: HistoryWord? by remember { mutableStateOf(null) }
    var showClearAllDialog by remember { mutableStateOf(false) }

    fun refreshWords() { words = getUserHistoryWords(context, locale) }

    SearchScreen(
        onClickBack = onClickBack,
        title = {
            Column {
                Text(stringResource(R.string.user_history_words_title))
                Text(
                    locale.localizedDisplayName(LocalContext.current.resources),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        },
        filteredItems = { term ->
            words.filter { it.word.startsWith(term, true) }
        },
        itemContent = { historyWord ->
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { selectedWord = historyWord }
                    .padding(vertical = 6.dp, horizontal = 16.dp)
            ) {
                Column {
                    Text(historyWord.word, style = MaterialTheme.typography.bodyLarge)
                    if (historyWord.frequency != null) {
                        Text(
                            stringResource(R.string.user_history_word_frequency, historyWord.frequency),
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
                Icon(painterResource(R.drawable.ic_edit), stringResource(R.string.user_dict_settings_edit_dialog_title))
            }
        },
        menu = listOf(stringResource(R.string.user_history_clear_all) to { showClearAllDialog = true })
    )

    if (selectedWord != null) {
        EditHistoryWordDialog(
            word = selectedWord!!,
            locale = locale,
            onDismissRequest = { selectedWord = null },
            onSaved = { refreshWords(); selectedWord = null },
            onDeleted = { refreshWords(); selectedWord = null }
        )
    }

    if (showClearAllDialog) {
        ConfirmationDialog(
            onDismissRequest = { showClearAllDialog = false },
            onConfirmed = {
                clearUserHistoryForLocale(context, locale)
                words = emptyList()
                showClearAllDialog = false
            },
            content = {
                Text(stringResource(R.string.user_history_clear_all_confirm))
            }
        )
    }

    ExtendedFloatingActionButton(
        onClick = { selectedWord = HistoryWord("", null) },
        text = { Text(stringResource(R.string.user_dict_add_word_button)) },
        icon = { Icon(painter = painterResource(R.drawable.ic_edit), stringResource(R.string.user_dict_add_word_button)) },
        modifier = Modifier.wrapContentSize(Alignment.BottomEnd).padding(all = 12.dp)
            .then(Modifier.safeDrawingPadding())
    )
}

@Composable
private fun EditHistoryWordDialog(
    word: HistoryWord,
    locale: Locale,
    onDismissRequest: () -> Unit,
    onSaved: () -> Unit,
    onDeleted: () -> Unit
) {
    val ctx = LocalContext.current
    val prefs = ctx.prefs()
    val addToPersonalDict = prefs.getBoolean(Settings.PREF_ADD_TO_PERSONAL_DICTIONARY, Defaults.PREF_ADD_TO_PERSONAL_DICTIONARY)
    val focusRequester = remember { FocusRequester() }
    var newWord by remember { mutableStateOf(word.word) }
    var newLocale by remember { mutableStateOf(locale) }
    val isNewWord = word.word.isEmpty() && word.frequency == null
    val existingWords = remember { getUserHistoryWords(ctx, locale).map { it.word } }
    val wordValid = newWord.isNotBlank() && (newWord == word.word || newWord !in existingWords)

    fun save() {
        if (newWord.isBlank()) return
        // Remove old word from original locale if editing
        if (!isNewWord) {
            deleteUserHistoryWord(ctx, locale, word.word)
        }
        addUserHistoryWord(ctx, newLocale, newWord)
        // Also add to personal dictionary if the setting is enabled
        if (addToPersonalDict) {
            runCatching { UserDictionary.Words.addWord(ctx, newWord, WEIGHT_FOR_USER_DICTIONARY, null, newLocale) }
        }
    }

    ThreeButtonAlertDialog(
        onDismissRequest = onDismissRequest,
        onConfirmed = { save(); onSaved() },
        checkOk = { wordValid },
        confirmButtonText = stringResource(R.string.save),
        neutralButtonText = if (!isNewWord) stringResource(R.string.delete) else null,
        onNeutral = {
            deleteUserHistoryWord(ctx, locale, word.word)
            onDeleted()
        },
        title = {
            Column {
                Text(stringResource(if (isNewWord) R.string.user_history_add_word_title else R.string.user_dict_settings_edit_dialog_title))
                Text(
                    locale.localizedDisplayName(ctx.resources),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        },
        content = {
            LaunchedEffect(word) {
                if (isNewWord) focusRequester.requestFocus()
            }
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                TextField(
                    value = newWord,
                    onValueChange = { newWord = it },
                    modifier = Modifier.fillMaxWidth().focusRequester(focusRequester),
                    singleLine = true,
                    label = { Text(stringResource(R.string.user_dict_settings_add_word_hint)) },
                    keyboardActions = KeyboardActions {
                        if (wordValid) {
                            save()
                            onSaved()
                        }
                    }
                )
                if (!isNewWord && word.frequency != null) {
                    Text(
                        stringResource(R.string.user_history_word_frequency, word.frequency),
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(stringResource(R.string.user_dict_settings_add_locale_option_name), Modifier.fillMaxWidth(0.3f))
                    DropDownField(
                        items = getSortedLocalesForHistory(locale),
                        selectedItem = newLocale,
                        onSelected = { newLocale = it },
                    ) {
                        Text(it.localizedDisplayName(ctx.resources))
                    }
                }
                if (!wordValid && newWord.isNotBlank())
                    Text(
                        stringResource(R.string.user_dict_word_already_present, newLocale.localizedDisplayName(ctx.resources)),
                        color = MaterialTheme.colorScheme.error
                    )
                if (addToPersonalDict)
                    Text(
                        stringResource(R.string.user_history_also_adds_to_personal_dict),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
            }
        }
    )
}

private fun getSortedLocalesForHistory(firstLocale: Locale): List<Locale> {
    val list = getSortedDictionaryLocales().toMutableList()
    list.remove(firstLocale)
    list.add(0, firstLocale)
    return list
}

private data class HistoryWord(val word: String, val frequency: Int?)

private fun getUserHistoryWords(context: Context, locale: Locale): List<HistoryWord> {
    val dict = PersonalizationHelper.getUserHistoryDictionary(context, locale)
    val wordProperties = dict.getWordPropertiesForSyncing()
    return wordProperties
        .filter { it.mWord != null }
        .map { HistoryWord(it.mWord, it.probability) }
        .sortedBy { it.word.lowercase() }
}

private fun addUserHistoryWord(context: Context, locale: Locale, word: String) {
    val dict = PersonalizationHelper.getUserHistoryDictionary(context, locale)
    val timestamp = (System.currentTimeMillis() / 1000).toInt()
    dict.addUnigramEntry(word, DEFAULT_WEIGHT, null, 0, false, false, timestamp)
}

private fun deleteUserHistoryWord(context: Context, locale: Locale, word: String) {
    val dict = PersonalizationHelper.getUserHistoryDictionary(context, locale)
    dict.removeUnigramEntryDynamically(word)
}

private fun clearUserHistoryForLocale(context: Context, locale: Locale) {
    val dict = PersonalizationHelper.getUserHistoryDictionary(context, locale)
    dict.clear()
}

private const val DEFAULT_WEIGHT = 200
private const val WEIGHT_FOR_USER_DICTIONARY = 250

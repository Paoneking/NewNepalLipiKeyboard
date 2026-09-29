// SPDX-License-Identifier: GPL-3.0-only
package com.paoneking.nepallipikeyboard.settings.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.paoneking.nepallipikeyboard.latin.R
import com.paoneking.nepallipikeyboard.latin.common.LocaleUtils.localizedDisplayName
import com.paoneking.nepallipikeyboard.latin.common.splitOnWhitespace
import com.paoneking.nepallipikeyboard.latin.utils.NextScreenIcon
import com.paoneking.nepallipikeyboard.settings.SearchScreen
import com.paoneking.nepallipikeyboard.settings.SettingsDestination

@Composable
fun UserHistoryWordLocalesScreen(
    onClickBack: () -> Unit,
) {
    val ctx = LocalContext.current
    val locales = getSortedDictionaryLocales().toList()
    SearchScreen(
        onClickBack = onClickBack,
        title = { Text(stringResource(R.string.user_history_words_title)) },
        filteredItems = { term ->
            locales.filter { locale ->
                locale.localizedDisplayName(ctx.resources).replace("(", "")
                    .splitOnWhitespace().any { it.startsWith(term, true) }
            }
        },
        itemContent = {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable {
                        SettingsDestination.navigateTo(SettingsDestination.UserHistoryWords + it.toLanguageTag())
                    }
                    .heightIn(min = 44.dp)
                    .padding(12.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(it.localizedDisplayName(ctx.resources), style = MaterialTheme.typography.bodyLarge)
                NextScreenIcon()
            }
        }
    )
}

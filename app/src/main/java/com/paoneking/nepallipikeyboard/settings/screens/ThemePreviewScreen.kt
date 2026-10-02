// SPDX-License-Identifier: GPL-3.0-only
package com.paoneking.nepallipikeyboard.settings.screens

import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.produceState
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.paoneking.nepallipikeyboard.keyboard.ColorSetting
import com.paoneking.nepallipikeyboard.keyboard.KeyboardSwitcher
import com.paoneking.nepallipikeyboard.keyboard.KeyboardTheme
import com.paoneking.nepallipikeyboard.latin.R
import com.paoneking.nepallipikeyboard.latin.common.ColorType
import com.paoneking.nepallipikeyboard.latin.common.Colors
import com.paoneking.nepallipikeyboard.latin.common.Links
import com.paoneking.nepallipikeyboard.latin.settings.Defaults
import com.paoneking.nepallipikeyboard.latin.settings.Settings
import com.paoneking.nepallipikeyboard.latin.utils.Log
import androidx.compose.material3.Icon
import androidx.compose.ui.res.painterResource
import com.paoneking.nepallipikeyboard.latin.utils.getActivity
import com.paoneking.nepallipikeyboard.latin.utils.getStringResourceOrName
import com.paoneking.nepallipikeyboard.latin.utils.htmlToAnnotated
import com.paoneking.nepallipikeyboard.latin.utils.prefs
import com.paoneking.nepallipikeyboard.latin.utils.withHtmlLink
import com.paoneking.nepallipikeyboard.settings.SearchScreen
import com.paoneking.nepallipikeyboard.settings.SettingsActivity
import com.paoneking.nepallipikeyboard.settings.SettingsDestination
import com.paoneking.nepallipikeyboard.settings.contentTextDirectionStyle
import com.paoneking.nepallipikeyboard.settings.dialogs.ConfirmationDialog
import com.paoneking.nepallipikeyboard.settings.dialogs.InfoDialog
import com.paoneking.nepallipikeyboard.settings.dialogs.ThreeButtonAlertDialog
import com.paoneking.nepallipikeyboard.settings.filePicker
import com.paoneking.nepallipikeyboard.latin.common.decodeBase36
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlinx.serialization.SerializationException
import kotlinx.serialization.json.Json
import java.util.EnumMap
import androidx.core.content.edit
import androidx.compose.ui.tooling.preview.Preview
import com.paoneking.nepallipikeyboard.latin.common.AllColors
import com.paoneking.nepallipikeyboard.latin.utils.Theme

@Composable
fun ThemePreviewScreen(
    onClickBack: () -> Unit,
    isNight: Boolean,
    prefKey: String,
    default: String
) {
    val ctx = LocalContext.current
    val prefs = ctx.prefs()
    val b = (ctx.getActivity() as? SettingsActivity)?.prefChanged?.collectAsState()
    if ((b?.value ?: 0) < 0)
        Log.v("irrelevant", "stupid way to trigger recomposition on preference change")

    val defaultColors = KeyboardTheme.getAvailableDefaultColors(prefs, isNight)
    val userColors = (prefs.all ?: emptyMap()).keys.mapNotNull {
        when {
            it.startsWith(Settings.PREF_USER_COLORS_PREFIX) -> it.substringAfter(Settings.PREF_USER_COLORS_PREFIX)
            it.startsWith(Settings.PREF_USER_ALL_COLORS_PREFIX) -> it.substringAfter(Settings.PREF_USER_ALL_COLORS_PREFIX)
            it.startsWith(Settings.PREF_USER_MORE_COLORS_PREFIX) -> it.substringAfter(Settings.PREF_USER_MORE_COLORS_PREFIX)
            else -> null
        }
    }.toSortedSet()
    val selectedColor = prefs.getString(prefKey, default)!!
    if (selectedColor !in defaultColors)
        userColors.add(selectedColor)

    val allThemes = userColors.toList() + defaultColors
    val targetScreen = if (isNight) SettingsDestination.ColorsNight else SettingsDestination.Colors

    // Load all colors on a background thread so composition is never blocked.
    // Result is cached for the lifetime of this screen.
    val colorMap by produceState<Map<String, Colors>>(initialValue = emptyMap(), allThemes) {
        value = withContext(Dispatchers.Default) {
            allThemes.associateWith { themeName ->
                runCatching { KeyboardTheme.getColorsForTheme(themeName, ctx, prefs, isNight) }
                    .getOrNull()
            }.filterValues { it != null }.mapValues { it.value!! }
        }
    }

    var showLoadDialog by remember { mutableStateOf(false) }
    var showAddDialog by remember { mutableStateOf(false) }
    var errorDialog by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()
    val loadFilePicker = filePicker { uri ->
        ctx.getActivity()?.contentResolver?.openInputStream(uri)?.use {
            val text = it.reader().readText()
            scope.launch { errorDialog = !loadColorString(text, prefs) }
        }
    }

    SearchScreen(
        onClickBack = onClickBack,
        title = { if (isNight) Text(stringResource(R.string.theme_colors_night)) else Text(stringResource(R.string.theme_colors)) },
        filteredItems = { term -> allThemes.filter { it.contains(term, ignoreCase = true) } },
        itemContent = { themeName ->
            Box(Modifier.padding(horizontal = 8.dp, vertical = 4.dp)) {
                ThemePreviewCard(
                    themeName = themeName,
                    colors = colorMap[themeName],
                    isSelected = themeName == selectedColor,
                    isUser = themeName in userColors,
                    targetScreen = targetScreen,
                    prefKey = prefKey,
                    onSelected = {
                        prefs.edit { putString(prefKey, themeName) }
                        KeyboardSwitcher.getInstance().setThemeNeedsReload()
                    }
                )
            }
        },
        menu = listOf(
            stringResource(R.string.add) to { showAddDialog = true },
            stringResource(R.string.load) to { showLoadDialog = true }
        ),
        content = {
            Scaffold(contentWindowInsets = WindowInsets.safeDrawing.only(WindowInsetsSides.Bottom)) { innerPadding ->
                LazyVerticalGrid(
                    columns = GridCells.Fixed(2),
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding)
                        .padding(horizontal = 8.dp, vertical = 4.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(allThemes, key = { it }) { themeName ->
                        ThemePreviewCard(
                            themeName = themeName,
                            colors = colorMap[themeName],
                            isSelected = themeName == selectedColor,
                            isUser = themeName in userColors,
                            targetScreen = targetScreen,
                            prefKey = prefKey,
                            onSelected = {
                                prefs.edit { putString(prefKey, themeName) }
                                KeyboardSwitcher.getInstance().setThemeNeedsReload()
                            }
                        )
                    }
                }
            }
        }
    )

    if (showAddDialog) {
        AddThemeDialog(
            onDismissRequest = { showAddDialog = false },
            userColors = userColors,
            targetScreen = targetScreen,
            prefKey = prefKey
        )
    }

    if (showLoadDialog) {
        ConfirmationDialog(
            onDismissRequest = { showLoadDialog = false },
            title = { Text(stringResource(R.string.load)) },
            content = {
                val link = stringResource(R.string.discussion_section_link).withHtmlLink(Links.CUSTOM_COLORS)
                val text = stringResource(R.string.get_colors_message, link)
                Text(text.htmlToAnnotated())
            },
            onConfirmed = {
                val intent = Intent(Intent.ACTION_OPEN_DOCUMENT)
                    .addCategory(Intent.CATEGORY_OPENABLE)
                    .putExtra(Intent.EXTRA_MIME_TYPES, arrayOf("text/*", "application/octet-stream", "application/json"))
                    .setType("*/*")
                loadFilePicker.launch(intent)
            },
            confirmButtonText = stringResource(R.string.button_load_custom),
            onNeutral = {
                showLoadDialog = false
                val cm = ctx.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                val clip = cm.primaryClip?.takeIf { it.itemCount > 0 } ?: return@ConfirmationDialog
                val text = clip.getItemAt(0).text
                errorDialog = !loadColorString(text.toString(), prefs)
            },
            neutralButtonText = stringResource(R.string.paste)
        )
    }

    if (errorDialog)
        InfoDialog(stringResource(R.string.file_read_error)) { errorDialog = false }
}

@Composable
private fun ThemePreviewCard(
    themeName: String,
    colors: Colors?,
    isSelected: Boolean,
    isUser: Boolean,
    targetScreen: String,
    prefKey: String,
    onSelected: () -> Unit
) {
    val ctx = LocalContext.current
    val prefs = ctx.prefs()
    var showDeleteDialog by remember { mutableStateOf(false) }
    var showCopyDialog by remember { mutableStateOf(false) }

    val borderColor = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outlineVariant
    val borderWidth = if (isSelected) 2.dp else 0.5.dp

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .border(borderWidth, borderColor, RoundedCornerShape(12.dp))
            .clip(RoundedCornerShape(12.dp))
            .clickable { onSelected() },
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = if (isSelected) 6.dp else 2.dp)
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Box(modifier = Modifier.fillMaxWidth()) {
                if (colors != null) {
                    KeyboardMiniPreview(
                        colors = colors,
                        modifier = Modifier.fillMaxWidth()
                    )
                } else {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(90.dp)
                            .background(MaterialTheme.colorScheme.surfaceVariant),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("?", style = MaterialTheme.typography.headlineSmall)
                    }
                }
                if (colors != null) {
                    Row(
                        modifier = Modifier
                            .align(Alignment.TopEnd)
                            .padding(4.dp),
                        horizontalArrangement = Arrangement.spacedBy(3.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(22.dp)
                                .clip(RoundedCornerShape(5.dp))
                                .background(MaterialTheme.colorScheme.surface.copy(alpha = 0.88f))
                                .clickable {
                                    if (isUser) SettingsDestination.navigateTo(targetScreen + themeName)
                                    else showCopyDialog = true
                                },
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                painterResource(R.drawable.ic_edit),
                                contentDescription = "edit",
                                modifier = Modifier.size(13.dp),
                                tint = MaterialTheme.colorScheme.primary
                            )
                        }
                        if (isUser) {
                            Box(
                                modifier = Modifier
                                    .size(22.dp)
                                    .clip(RoundedCornerShape(5.dp))
                                    .background(MaterialTheme.colorScheme.surface.copy(alpha = 0.88f))
                                    .clickable { showDeleteDialog = true },
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    painterResource(R.drawable.ic_bin),
                                    contentDescription = "delete",
                                    modifier = Modifier.size(13.dp),
                                    tint = MaterialTheme.colorScheme.error
                                )
                            }
                        }
                    }
                }
            }
            Text(
                text = themeName.getStringResourceOrName("theme_name_", ctx),
                style = MaterialTheme.typography.bodySmall,
                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface,
                textAlign = TextAlign.Center,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp, vertical = 5.dp)
            )
        }
    }

    if (showDeleteDialog)
        ConfirmationDialog(
            onDismissRequest = { showDeleteDialog = false },
            content = { Text(stringResource(R.string.delete_confirmation, themeName)) },
            onConfirmed = {
                showDeleteDialog = false
                prefs.edit {
                    remove(Settings.PREF_USER_COLORS_PREFIX + themeName)
                    remove(Settings.PREF_USER_ALL_COLORS_PREFIX + themeName)
                    remove(Settings.PREF_USER_MORE_COLORS_PREFIX + themeName)
                    if (isSelected) remove(prefKey)
                }
                KeyboardSwitcher.getInstance().setThemeNeedsReload()
            }
        )

    if (showCopyDialog && colors != null)
        CopyThemeDialog(
            onDismissRequest = { showCopyDialog = false },
            sourceThemeName = themeName,
            sourceColors = colors,
            targetScreen = targetScreen,
            prefKey = prefKey
        )
}

@Composable
private fun KeyboardMiniPreview(
    colors: Colors,
    modifier: Modifier = Modifier
) {
    val previewColors = remember(colors) {
        PreviewColors(
            bg = Color(colors.get(ColorType.MAIN_BACKGROUND)),
            keyBg = Color(colors.get(ColorType.KEY_BACKGROUND)),
            funcKeyBg = Color(colors.get(ColorType.FUNCTIONAL_KEY_BACKGROUND)),
            actionKeyBg = Color(colors.get(ColorType.ACTION_KEY_BACKGROUND)),
            spaceBarBg = Color(colors.get(ColorType.SPACE_BAR_BACKGROUND)),
            keyText = Color(colors.get(ColorType.KEY_TEXT)),
            hintText = Color(colors.get(ColorType.KEY_HINT_TEXT)),
            spaceText = Color(colors.get(ColorType.SPACE_BAR_TEXT)),
            popupBg = Color(colors.get(ColorType.KEY_PREVIEW_BACKGROUND)),
            popupText = Color(colors.get(ColorType.KEY_PREVIEW_TEXT)),
            pressedKeyBg = Color(colors.get(ColorType.KEY_BACKGROUND)).copy(alpha = 0.35f),
            pressedKeyText = Color(colors.get(ColorType.KEY_TEXT)).copy(alpha = 0.35f),
        )
    }

    val keyShape = RoundedCornerShape(3.dp)
    val keySpacing = 2.dp
    val rowSpacing = 2.dp

    Box(
        modifier = modifier
            .clip(RoundedCornerShape(8.dp))
            .background(previewColors.bg)
            .padding(4.dp)
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(rowSpacing)) {
            Row(modifier = Modifier.fillMaxWidth()) {
                Spacer(modifier = Modifier.weight(1.8f))
                Box(
                    modifier = Modifier
                        .height(16.dp)
                        .clip(RoundedCornerShape(4.dp))
                        .background(previewColors.popupBg)
                        .padding(horizontal = 5.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "e",
                        color = previewColors.popupText,
                        fontSize = 8.sp,
                        fontWeight = FontWeight.Bold,
                        maxLines = 1,
                        lineHeight = 10.sp
                    )
                }
                Spacer(modifier = Modifier.weight(6f))
            }
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(keySpacing)
            ) {
                val letters = "qwertyuiop"
                val hints = "1234567890"
                letters.forEachIndexed { i, letter ->
                    val pressed = letter == 'e'
                    MiniKey(
                        text = letter.toString(),
                        bgColor = if (pressed) previewColors.pressedKeyBg else previewColors.keyBg,
                        textColor = if (pressed) previewColors.pressedKeyText else previewColors.keyText,
                        modifier = Modifier.weight(1f).height(20.dp),
                        shape = keyShape,
                        hint = if (pressed) "" else hints[i].toString(),
                        hintColor = previewColors.hintColor
                    )
                }
            }
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 6.dp),
                horizontalArrangement = Arrangement.spacedBy(keySpacing)
            ) {
                "asdfghjkl".forEach { letter ->
                    MiniKey(
                        text = letter.toString(),
                        bgColor = previewColors.keyBg,
                        textColor = previewColors.keyText,
                        modifier = Modifier.weight(1f).height(20.dp),
                        shape = keyShape
                    )
                }
            }
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(keySpacing)
            ) {
                MiniKey(
                    text = "\u21E7",
                    bgColor = previewColors.funcKeyBg,
                    textColor = previewColors.keyText,
                    modifier = Modifier.weight(1.4f).height(20.dp),
                    shape = keyShape
                )
                "zxcvbnm".forEach { letter ->
                    MiniKey(
                        text = letter.toString(),
                        bgColor = previewColors.keyBg,
                        textColor = previewColors.keyText,
                        modifier = Modifier.weight(1f).height(20.dp),
                        shape = keyShape
                    )
                }
                MiniKey(
                    text = "\u232B",
                    bgColor = previewColors.funcKeyBg,
                    textColor = previewColors.keyText,
                    modifier = Modifier.weight(1.4f).height(20.dp),
                    shape = keyShape
                )
            }
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(keySpacing)
            ) {
                MiniKey(
                    text = "?123",
                    bgColor = previewColors.funcKeyBg,
                    textColor = previewColors.keyText,
                    modifier = Modifier.weight(1.5f).height(20.dp),
                    shape = keyShape,
                    fontSize = 5
                )
                MiniKey(
                    text = "",
                    bgColor = previewColors.spaceBarBg,
                    textColor = previewColors.spaceText,
                    modifier = Modifier.weight(5f).height(20.dp),
                    shape = keyShape
                )
                MiniKey(
                    text = "\u23CE",
                    bgColor = previewColors.actionKeyBg,
                    textColor = Color.White,
                    modifier = Modifier.weight(1.5f).height(20.dp),
                    shape = keyShape
                )
            }
        }
    }
}

private data class PreviewColors(
    val bg: Color,
    val keyBg: Color,
    val funcKeyBg: Color,
    val actionKeyBg: Color,
    val spaceBarBg: Color,
    val keyText: Color,
    val hintText: Color,
    val spaceText: Color,
    val popupBg: Color,
    val popupText: Color,
    val pressedKeyBg: Color,
    val pressedKeyText: Color,
    val hintColor: Color = Color.Transparent
)

@Composable
private fun MiniKey(
    text: String,
    bgColor: Color,
    textColor: Color,
    modifier: Modifier,
    shape: RoundedCornerShape,
    fontSize: Int = 7,
    hint: String = "",
    hintColor: Color = Color.Transparent
) {
    Box(
        modifier = modifier
            .clip(shape)
            .background(bgColor),
        contentAlignment = Alignment.Center
    ) {
        if (text.isNotEmpty()) {
            Text(
                text = text,
                color = textColor,
                fontSize = fontSize.sp,
                maxLines = 1,
                lineHeight = fontSize.sp
            )
        }
        if (hint.isNotEmpty()) {
            Text(
                text = hint,
                color = hintColor,
                fontSize = 3.5.sp,
                maxLines = 1,
                lineHeight = 3.5.sp,
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(end = 1.dp, top = 1.dp)
            )
        }
    }
}

@Composable
private fun CopyThemeDialog(
    onDismissRequest: () -> Unit,
    sourceThemeName: String,
    sourceColors: Colors,
    targetScreen: String,
    prefKey: String
) {
    val ctx = LocalContext.current
    val prefs = ctx.prefs()
    var textValue by remember { mutableStateOf(TextFieldValue()) }
    val baseName = sourceThemeName.getStringResourceOrName("theme_name_", ctx)
    val defaultName = KeyboardTheme.getUnusedThemeName(baseName, prefs)
    val textEmpty = textValue.text.isEmpty()
    val currentName = if (textEmpty) defaultName else textValue.text
    val existingNames = (prefs.all ?: emptyMap()).keys.mapNotNull {
        when {
            it.startsWith(Settings.PREF_USER_COLORS_PREFIX) -> it.substringAfter(Settings.PREF_USER_COLORS_PREFIX)
            it.startsWith(Settings.PREF_USER_ALL_COLORS_PREFIX) -> it.substringAfter(Settings.PREF_USER_ALL_COLORS_PREFIX)
            it.startsWith(Settings.PREF_USER_MORE_COLORS_PREFIX) -> it.substringAfter(Settings.PREF_USER_MORE_COLORS_PREFIX)
            else -> null
        }
    }.toSet()
    val nameValid = currentName.isNotBlank() && currentName !in existingNames

    fun confirm() {
        onDismissRequest()
        val colorMap = EnumMap<ColorType, Int>(ColorType::class.java)
        ColorType.entries.forEach { colorMap[it] = sourceColors.get(it) }
        KeyboardTheme.writeUserAllColors(prefs, currentName, colorMap)
        KeyboardTheme.writeUserMoreColors(prefs, currentName, 2) // 2 = "all colors" mode — reads from writeUserAllColors
        prefs.edit { putString(prefKey, currentName) }
        SettingsDestination.navigateTo(targetScreen + currentName)
        KeyboardSwitcher.getInstance().setThemeNeedsReload()
    }

    ThreeButtonAlertDialog(
        onDismissRequest = onDismissRequest,
        title = { Text(stringResource(R.string.add)) },
        content = {
            TextField(
                value = textValue,
                onValueChange = { textValue = it },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                label = if (textEmpty) { { Text(defaultName) } } else null,
                textStyle = contentTextDirectionStyle,
                keyboardActions = KeyboardActions { if (nameValid) confirm() }
            )
        },
        onConfirmed = { if (nameValid) confirm() },
        checkOk = { nameValid }
    )
}

@Composable
private fun AddThemeDialog(
    onDismissRequest: () -> Unit,
    userColors: Collection<String>,
    targetScreen: String,
    prefKey: String
) {
    val prefs = LocalContext.current.prefs()
    var textValue by remember { mutableStateOf(TextFieldValue()) }
    val defaultName = KeyboardTheme.getUnusedThemeName(stringResource(R.string.theme_name_user), prefs)
    val textEmpty = textValue.text.isEmpty()
    val currentName = if (textEmpty) defaultName else textValue.text
    val nameValid = currentName.isNotBlank() && currentName !in userColors

    ThreeButtonAlertDialog(
        onDismissRequest = onDismissRequest,
        title = { Text(stringResource(R.string.add)) },
        content = {
            TextField(
                value = textValue,
                onValueChange = { textValue = it },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                label = if (textEmpty) { { Text(defaultName) } } else null,
                textStyle = contentTextDirectionStyle,
                keyboardActions = KeyboardActions {
                    if (nameValid) {
                        onDismissRequest()
                        prefs.edit { putString(prefKey, currentName) }
                        KeyboardTheme.writeUserMoreColors(prefs, currentName, Defaults.PREF_USER_MORE_COLORS)
                        SettingsDestination.navigateTo(targetScreen + currentName)
                        KeyboardSwitcher.getInstance().setThemeNeedsReload()
                    }
                }
            )
        },
        onConfirmed = {
            onDismissRequest()
            prefs.edit { putString(prefKey, currentName) }
            KeyboardTheme.writeUserMoreColors(prefs, currentName, Defaults.PREF_USER_MORE_COLORS)
            SettingsDestination.navigateTo(targetScreen + currentName)
            KeyboardSwitcher.getInstance().setThemeNeedsReload()
        },
        checkOk = { nameValid }
    )
}

// returns whether the string was successfully deserialized and stored in prefs
private fun loadColorString(colorString: String, prefs: android.content.SharedPreferences): Boolean {
    try {
        val that = Json.decodeFromString<SaveThoseColors>(colorString)
        val themeName = KeyboardTheme.getUnusedThemeName(that.name ?: "imported colors", prefs)
        val colors = that.colors.map { ColorSetting(it.key, it.value.second, it.value.first) }
        KeyboardTheme.writeUserColors(prefs, themeName, colors)
        KeyboardTheme.writeUserMoreColors(prefs, themeName, that.moreColors)
    } catch (_: SerializationException) {
        try {
            val allColorsStringMap = Json.decodeFromString<Map<String, Int>>(colorString)
            val allColors = EnumMap<ColorType, Int>(ColorType::class.java)
            var themeName = "imported colors"
            allColorsStringMap.forEach { (key, value) ->
                try {
                    allColors[ColorType.valueOf(key)] = value
                } catch (_: IllegalArgumentException) {
                    if (value == 0)
                        runCatching { decodeBase36(key) }.getOrNull()?.let { themeName = it }
                }
            }
            themeName = KeyboardTheme.getUnusedThemeName(themeName, prefs)
            KeyboardTheme.writeUserAllColors(prefs, themeName, allColors)
            KeyboardTheme.writeUserMoreColors(prefs, themeName, 2)
        } catch (_: SerializationException) {
            return false
        }
    }
    return true
}

@Preview
@Composable
private fun KeyboardMiniPreviewPreview() {
    val colorMap = EnumMap<ColorType, Int>(ColorType::class.java).apply {
        put(ColorType.MAIN_BACKGROUND, android.graphics.Color.DKGRAY)
        put(ColorType.KEY_BACKGROUND, android.graphics.Color.GRAY)
        put(ColorType.FUNCTIONAL_KEY_BACKGROUND, android.graphics.Color.BLACK)
        put(ColorType.ACTION_KEY_BACKGROUND, android.graphics.Color.BLUE)
        put(ColorType.SPACE_BAR_BACKGROUND, android.graphics.Color.GRAY)
        put(ColorType.KEY_TEXT, android.graphics.Color.WHITE)
        put(ColorType.KEY_HINT_TEXT, android.graphics.Color.LTGRAY)
        put(ColorType.SPACE_BAR_TEXT, android.graphics.Color.LTGRAY)
        put(ColorType.KEY_PREVIEW_BACKGROUND, android.graphics.Color.WHITE)
        put(ColorType.KEY_PREVIEW_TEXT, android.graphics.Color.BLACK)
    }
    val colors = AllColors(colorMap, KeyboardTheme.STYLE_MATERIAL, true, null)
    Theme {
        KeyboardMiniPreview(
            colors = colors,
            modifier = Modifier.padding(16.dp).size(200.dp, 120.dp)
        )
    }
}

/*
 * Copyright (C) 2014 The Android Open Source Project
 * modified
 * SPDX-License-Identifier: Apache-2.0 AND GPL-3.0-only
 */
package com.paoneking.nepallipikeyboard.keyboard

import android.content.Context
import android.content.SharedPreferences
import android.graphics.Color
import android.graphics.drawable.Drawable
import android.os.Build
import android.util.TypedValue
import android.view.ContextThemeWrapper
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.core.content.ContextCompat
import androidx.core.content.edit
import com.paoneking.nepallipikeyboard.latin.R
import com.paoneking.nepallipikeyboard.latin.common.AllColors
import com.paoneking.nepallipikeyboard.latin.common.ColorType
import com.paoneking.nepallipikeyboard.latin.common.Colors
import com.paoneking.nepallipikeyboard.latin.common.DefaultColors
import com.paoneking.nepallipikeyboard.latin.common.DynamicColors
import com.paoneking.nepallipikeyboard.latin.settings.Defaults
import com.paoneking.nepallipikeyboard.latin.settings.Settings
import com.paoneking.nepallipikeyboard.latin.utils.ResourceUtils
import com.paoneking.nepallipikeyboard.latin.utils.brightenOrDarken
import com.paoneking.nepallipikeyboard.latin.utils.isBrightColor
import com.paoneking.nepallipikeyboard.latin.utils.isGoodContrast
import com.paoneking.nepallipikeyboard.latin.utils.prefs
import com.paoneking.nepallipikeyboard.settings.SettingsActivity
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import java.util.EnumMap
import androidx.core.graphics.toColorInt

class KeyboardTheme
private constructor(val themeId: Int, @JvmField val mStyleId: Int) {
    override fun equals(other: Any?) = if (other === this) true
    else (other as? KeyboardTheme)?.themeId == themeId
    override fun hashCode(): Int = themeId

    companion object {
        const val STYLE_MATERIAL = "Material"
        const val STYLE_HOLO = "Holo"
        const val STYLE_ROUNDED = "Rounded"

        // ── System ────────────────────────────────────────────────────────
        const val THEME_LIGHT        = "light"
        const val THEME_HOLO_WHITE   = "holo_white"
        const val THEME_DARK         = "dark"
        const val THEME_DARKER       = "darker"
        const val THEME_BLACK        = "black"
        const val THEME_DYNAMIC      = "dynamic"

        // ── Blue Gray ─────────────────────────────────────────────────────
        const val THEME_BLUE_GRAY       = "blue_gray"
        const val THEME_BLUE_GRAY_DARK  = "blue_gray_dark"

        // ── Brown ─────────────────────────────────────────────────────────
        const val THEME_BROWN       = "brown"
        const val THEME_BROWN_DARK  = "brown_dark"

        // ── Chocolate ─────────────────────────────────────────────────────
        const val THEME_CHOCOLATE_LIGHT = "chocolate_light"
        const val THEME_CHOCOLATE       = "chocolate"

        // ── Cloudy ────────────────────────────────────────────────────────
        const val THEME_CLOUDY_LIGHT = "cloudy_light"
        const val THEME_CLOUDY       = "cloudy"

        // ── Forest ────────────────────────────────────────────────────────
        const val THEME_FOREST_LIGHT = "forest_light"
        const val THEME_FOREST       = "forest"

        // ── Indigo ────────────────────────────────────────────────────────
        const val THEME_INDIGO      = "indigo"
        const val THEME_INDIGO_DARK = "indigo_dark"

        // ── Ocean ─────────────────────────────────────────────────────────
        const val THEME_OCEAN_LIGHT = "ocean_light"
        const val THEME_OCEAN       = "ocean"

        // ── Pink ──────────────────────────────────────────────────────────
        const val THEME_PINK      = "pink"
        const val THEME_PINK_DARK = "pink_dark"

        // ── Sand ──────────────────────────────────────────────────────────
        const val THEME_SAND      = "sand"
        const val THEME_SAND_DARK = "sand_dark"

        // ── Violette ──────────────────────────────────────────────────────
        const val THEME_VIOLETTE_LIGHT = "violette_light"
        const val THEME_VIOLETTE       = "violette"

        // ── Periwinkle ────────────────────────────────────────────────────
        const val THEME_PERIWINKLE      = "periwinkle"
        const val THEME_PERIWINKLE_DARK = "periwinkle_dark"

        // ── Cream ─────────────────────────────────────────────────────────
        const val THEME_CREAM      = "cream"
        const val THEME_CREAM_DARK = "cream_dark"

        // ── Mint ──────────────────────────────────────────────────────────
        const val THEME_MINT      = "mint"
        const val THEME_MINT_DARK = "mint_dark"

        // ── Rose ──────────────────────────────────────────────────────────
        const val THEME_ROSE      = "rose"
        const val THEME_ROSE_DARK = "rose_dark"

        // ── Midnight ──────────────────────────────────────────────────────
        const val THEME_MIDNIGHT_LIGHT = "midnight_light"
        const val THEME_MIDNIGHT       = "midnight"

        // ── Galaxy ────────────────────────────────────────────────────────
        const val THEME_GALAXY_LIGHT = "galaxy_light"
        const val THEME_GALAXY       = "galaxy"

        // ── Jungle ────────────────────────────────────────────────────────
        const val THEME_JUNGLE_LIGHT = "jungle_light"
        const val THEME_JUNGLE       = "jungle"

        // ── Ember ─────────────────────────────────────────────────────────
        const val THEME_EMBER_LIGHT = "ember_light"
        const val THEME_EMBER       = "ember"

        // ── Lavender ──────────────────────────────────────────────────────
        const val THEME_LAVENDER      = "lavender"
        const val THEME_LAVENDER_DARK = "lavender_dark"

        // ── Arctic ────────────────────────────────────────────────────────
        const val THEME_ARCTIC      = "arctic"
        const val THEME_ARCTIC_DARK = "arctic_dark"

        // ── Grape ─────────────────────────────────────────────────────────
        const val THEME_GRAPE      = "grape"
        const val THEME_GRAPE_DARK = "grape_dark"

        // ── Aqua ──────────────────────────────────────────────────────────
        const val THEME_AQUA      = "aqua"
        const val THEME_AQUA_DARK = "aqua_dark"

        // ── Honey ─────────────────────────────────────────────────────────
        const val THEME_HONEY      = "honey"
        const val THEME_HONEY_DARK = "honey_dark"

        // ── Sage ──────────────────────────────────────────────────────────
        const val THEME_SAGE      = "sage"
        const val THEME_SAGE_DARK = "sage_dark"

        // ── Peach ─────────────────────────────────────────────────────────
        const val THEME_PEACH      = "peach"
        const val THEME_PEACH_DARK = "peach_dark"

        // ── Matrix ────────────────────────────────────────────────────────
        const val THEME_MATRIX_LIGHT = "matrix_light"
        const val THEME_MATRIX       = "matrix"

        // ── Synthwave ─────────────────────────────────────────────────────
        const val THEME_SYNTHWAVE_LIGHT = "synthwave_light"
        const val THEME_SYNTHWAVE       = "synthwave"

        // ── Abyss ─────────────────────────────────────────────────────────
        const val THEME_ABYSS_LIGHT = "abyss_light"
        const val THEME_ABYSS       = "abyss"

        // ── Crimson ───────────────────────────────────────────────────────
        const val THEME_CRIMSON_LIGHT = "crimson_light"
        const val THEME_CRIMSON       = "crimson"

        // ── Moss ──────────────────────────────────────────────────────────
        const val THEME_MOSS_LIGHT = "moss_light"
        const val THEME_MOSS       = "moss"

        // ── Dusk ──────────────────────────────────────────────────────────
        const val THEME_DUSK_LIGHT = "dusk_light"
        const val THEME_DUSK       = "dusk"

        // ── Obsidian ──────────────────────────────────────────────────────
        const val THEME_OBSIDIAN_LIGHT = "obsidian_light"
        const val THEME_OBSIDIAN       = "obsidian"

        // ── Steel ─────────────────────────────────────────────────────────
        const val THEME_STEEL_LIGHT = "steel_light"
        const val THEME_STEEL       = "steel"

        // ── Nebula ────────────────────────────────────────────────────────
        const val THEME_NEBULA_LIGHT = "nebula_light"
        const val THEME_NEBULA       = "nebula"

        // ── Emerald ───────────────────────────────────────────────────────
        const val THEME_EMERALD_LIGHT = "emerald_light"
        const val THEME_EMERALD       = "emerald"

        // ── Lava ──────────────────────────────────────────────────────────
        const val THEME_LAVA_LIGHT = "lava_light"
        const val THEME_LAVA       = "lava"

        // ── Teal ──────────────────────────────────────────────────────────
        const val THEME_TEAL_DAY   = "teal_day"
        const val THEME_TEAL_NIGHT = "teal_night"

        // ── Noir Rose ─────────────────────────────────────────────────────
        const val THEME_NOIR_ROSE_LIGHT = "noir_rose_light"
        const val THEME_NOIR_ROSE       = "noir_rose"

        // ── Camo ──────────────────────────────────────────────────────────
        const val THEME_CAMO_LIGHT = "camo_light"
        const val THEME_CAMO       = "camo"

        // ── Garnet ────────────────────────────────────────────────────────
        const val THEME_GARNET_LIGHT = "garnet_light"
        const val THEME_GARNET       = "garnet"

        // ── Aurora ────────────────────────────────────────────────────────
        const val THEME_AURORA_LIGHT = "aurora_light"
        const val THEME_AURORA       = "aurora"

        // ── Torch ─────────────────────────────────────────────────────────
        const val THEME_TORCH_LIGHT = "torch_light"
        const val THEME_TORCH       = "torch"

        // ── Void ──────────────────────────────────────────────────────────
        const val THEME_VOID_LIGHT = "void_light"
        const val THEME_VOID       = "void"

        // ── IDE Themes: Monokai ─────────────────────────────────────────────
        const val THEME_MONOKAI       = "monokai"
        const val THEME_MONOKAI_PRO   = "monokai_pro"

        // ── IDE Themes: Dracula ────────────────────────────────────────────
        const val THEME_DRACULA       = "dracula"
        const val THEME_DRACULA_ORCHID = "dracula_orchid"

        // ── IDE Themes: One Dark ────────────────────────────────────────────
        const val THEME_ONE_DARK     = "one_dark"
        const val THEME_ONE_DARK_PRO = "one_dark_pro"

        // ── IDE Themes: Night Owl ───────────────────────────────────────────
        const val THEME_NIGHT_OWL    = "night_owl"
        const val THEME_NIGHT_OWL_LIGHT = "night_owl_light"

        // ── IDE Themes: Solarized ────────────────────────────────────────────
        const val THEME_SOLARIZED_LIGHT = "solarized_light"
        const val THEME_SOLARIZED_DARK  = "solarized_dark"

        // ── IDE Themes: Gruvbox ─────────────────────────────────────────────
        const val THEME_GRUVBOX_LIGHT = "gruvbox_light"
        const val THEME_GRUVBOX_DARK  = "gruvbox_dark"

        // ── IDE Themes: Atom ────────────────────────────────────────────────
        const val THEME_ATOM_ONE_DARK   = "atom_one_dark"
        const val THEME_ATOM_ONE_LIGHT  = "atom_one_light"

        // ── IDE Themes: VS Code ─────────────────────────────────────────────
        const val THEME_VSCODE_DARK_PLUS   = "vscode_dark_plus"
        const val THEME_VSCODE_LIGHT_PLUS   = "vscode_light_plus"
        const val THEME_VSCODE_DARK_MODERN  = "vscode_dark_modern"

        // ── IDE Themes: JetBrains ───────────────────────────────────────────
        const val THEME_JETBRAINS_DARK = "jetbrains_dark"
        const val THEME_JETBRAINS_LIGHT = "jetbrains_light"

        // ── IDE Themes: Github ───────────────────────────────────────────────
        const val THEME_GITHUB_LIGHT   = "github_light"
        const val THEME_GITHUB_DARK    = "github_dark"

        // ── IDE Themes: Sublime ──────────────────────────────────────────────
        const val THEME_SUBLIME       = "sublime"

        // ── IDE Themes: Palenight ───────────────────────────────────────────
        const val THEME_PALENIGHT     = "palenight"
        const val THEME_PALENIGHT_LIGHT = "palenight_light"

        // ── Terminal Themes: Nord ───────────────────────────────────────────
        const val THEME_NORD         = "nord"
        const val THEME_NORD_FROZEN  = "nord_frozen"
        const val THEME_NORD_POLAR  = "nord_polar"

        // ── Terminal Themes: Tokyo Night ────────────────────────────────────
        const val THEME_TOKYO_NIGHT   = "tokyo_night"
        const val THEME_TOKYO_NIGHT_STORM = "tokyo_night_storm"
        const val THEME_TOKYO_NIGHT_LIGHT = "tokyo_night_light"

        // ── Terminal Themes: Catppuccin ─────────────────────────────────────
        const val THEME_CATPUCCIN_LATTE  = "catppuccin_latte"
        const val THEME_CATPUCCIN_MOCHA   = "catppuccin_mocha"
        const val THEME_CATPUCCIN_MACCHIATO = "catppuccin_macchiato"
        const val THEME_CATPUCCIN_FRAPPE = "catppuccin_frappe"

        // ── Terminal Themes: One Half ────────────────────────────────────────
        const val THEME_ONE_HALF_DARK  = "one_half_dark"
        const val THEME_ONE_HALF_LIGHT = "one_half_light"

        // ── Terminal Themes: Dracula ─────────────────────────────────────────
        const val THEME_TERMINAL_DRACULA = "terminal_dracula"

        // ── Terminal Themes: Gruvbox ─────────────────────────────────────────
        const val THEME_TERMINAL_GRUVBOX_MEDIUM = "terminal_gruvbox_medium"
        const val THEME_TERMINAL_GRUVBOX_SOFT   = "terminal_gruvbox_soft"

        // ── Terminal Themes: Everforest ─────────────────────────────────────
        const val THEME_EVERFOREST_DARK   = "everforest_dark"
        const val THEME_EVERFOREST_LIGHT  = "everforest_light"

        // ── Terminal Themes: Tokyo Day ──────────────────────────────────────
        const val THEME_TOKYO_DAY      = "tokyo_day"

        // ── Terminal Themes: Rosé Pine ──────────────────────────────────────
        const val THEME_ROSE_PINE     = "rose_pine"
        const val THEME_ROSE_PINE_DAWN = "rose_pine_dawn"
        const val THEME_ROSE_PINE_MOON = "rose_pine_moon"

        // ── Terminal Themes: Material ────────────────────────────────────────
        const val THEME_MATERIAL_THEME_DARK  = "material_theme_dark"
        const val THEME_MATERIAL_THEME_LIGHT = "material_theme_light"

        private var cachedDefaultColors: Pair<String, List<String>>? = null

        fun getAvailableDefaultColors(prefs: SharedPreferences, isNight: Boolean): List<String> {
            val cacheKey = "${prefs.getString(Settings.PREF_THEME_STYLE, Defaults.PREF_THEME_STYLE)}_$isNight"
            cachedDefaultColors?.let { if (it.first == cacheKey) return it.second }

            val colors = computeDefaultColors(prefs, isNight)
            cachedDefaultColors = cacheKey to colors
            return colors
        }

        private fun computeDefaultColors(prefs: SharedPreferences, isNight: Boolean) = listOfNotNull(
            if (!isNight) THEME_LIGHT else null,
            THEME_DARK,
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) THEME_DYNAMIC else null,
            if (prefs.getString(Settings.PREF_THEME_STYLE, Defaults.PREF_THEME_STYLE) == STYLE_HOLO) THEME_HOLO_WHITE else null,
            THEME_DARKER,
            THEME_BLACK,
            if (!isNight) THEME_BLUE_GRAY else null,       THEME_BLUE_GRAY_DARK,
            if (!isNight) THEME_BROWN else null,            THEME_BROWN_DARK,
            if (!isNight) THEME_CHOCOLATE_LIGHT else null,  THEME_CHOCOLATE,
            if (!isNight) THEME_CLOUDY_LIGHT else null,     THEME_CLOUDY,
            if (!isNight) THEME_FOREST_LIGHT else null,     THEME_FOREST,
            if (!isNight) THEME_INDIGO else null,           THEME_INDIGO_DARK,
            if (!isNight) THEME_OCEAN_LIGHT else null,      THEME_OCEAN,
            if (!isNight) THEME_PINK else null,             THEME_PINK_DARK,
            if (!isNight) THEME_SAND else null,             THEME_SAND_DARK,
            if (!isNight) THEME_VIOLETTE_LIGHT else null,   THEME_VIOLETTE,
            if (!isNight) THEME_PERIWINKLE else null,       THEME_PERIWINKLE_DARK,
            if (!isNight) THEME_CREAM else null,            THEME_CREAM_DARK,
            if (!isNight) THEME_MINT else null,             THEME_MINT_DARK,
            if (!isNight) THEME_ROSE else null,             THEME_ROSE_DARK,
            if (!isNight) THEME_MIDNIGHT_LIGHT else null,   THEME_MIDNIGHT,
            if (!isNight) THEME_GALAXY_LIGHT else null,     THEME_GALAXY,
            if (!isNight) THEME_JUNGLE_LIGHT else null,     THEME_JUNGLE,
            if (!isNight) THEME_EMBER_LIGHT else null,      THEME_EMBER,
            if (!isNight) THEME_LAVENDER else null,         THEME_LAVENDER_DARK,
            if (!isNight) THEME_ARCTIC else null,           THEME_ARCTIC_DARK,
            if (!isNight) THEME_GRAPE else null,            THEME_GRAPE_DARK,
            if (!isNight) THEME_AQUA else null,             THEME_AQUA_DARK,
            if (!isNight) THEME_HONEY else null,            THEME_HONEY_DARK,
            if (!isNight) THEME_SAGE else null,             THEME_SAGE_DARK,
            if (!isNight) THEME_PEACH else null,            THEME_PEACH_DARK,
            if (!isNight) THEME_MATRIX_LIGHT else null,     THEME_MATRIX,
            if (!isNight) THEME_SYNTHWAVE_LIGHT else null,  THEME_SYNTHWAVE,
            if (!isNight) THEME_ABYSS_LIGHT else null,      THEME_ABYSS,
            if (!isNight) THEME_CRIMSON_LIGHT else null,    THEME_CRIMSON,
            if (!isNight) THEME_MOSS_LIGHT else null,       THEME_MOSS,
            if (!isNight) THEME_DUSK_LIGHT else null,       THEME_DUSK,
            if (!isNight) THEME_OBSIDIAN_LIGHT else null,   THEME_OBSIDIAN,
            if (!isNight) THEME_STEEL_LIGHT else null,      THEME_STEEL,
            if (!isNight) THEME_NEBULA_LIGHT else null,     THEME_NEBULA,
            if (!isNight) THEME_EMERALD_LIGHT else null,    THEME_EMERALD,
            if (!isNight) THEME_LAVA_LIGHT else null,       THEME_LAVA,
            if (!isNight) THEME_TEAL_DAY else null,         THEME_TEAL_NIGHT,
            if (!isNight) THEME_NOIR_ROSE_LIGHT else null,  THEME_NOIR_ROSE,
            if (!isNight) THEME_CAMO_LIGHT else null,       THEME_CAMO,
            if (!isNight) THEME_GARNET_LIGHT else null,     THEME_GARNET,
            if (!isNight) THEME_AURORA_LIGHT else null,     THEME_AURORA,
            if (!isNight) THEME_TORCH_LIGHT else null,      THEME_TORCH,
            if (!isNight) THEME_VOID_LIGHT else null,       THEME_VOID,
            if (!isNight) THEME_MONOKAI else null,          THEME_MONOKAI_PRO,
            if (!isNight) THEME_DRACULA else null,          THEME_DRACULA_ORCHID,
            if (!isNight) THEME_ONE_DARK else null,          THEME_ONE_DARK_PRO,
            if (!isNight) THEME_NIGHT_OWL_LIGHT else null,   THEME_NIGHT_OWL,
            if (!isNight) THEME_SOLARIZED_LIGHT else null,   THEME_SOLARIZED_DARK,
            if (!isNight) THEME_GRUVBOX_LIGHT else null,     THEME_GRUVBOX_DARK,
            if (!isNight) THEME_ATOM_ONE_LIGHT else null,    THEME_ATOM_ONE_DARK,
            if (!isNight) THEME_VSCODE_LIGHT_PLUS else null, THEME_VSCODE_DARK_PLUS,
            if (!isNight) THEME_VSCODE_DARK_MODERN else null,
            if (!isNight) THEME_JETBRAINS_LIGHT else null,  THEME_JETBRAINS_DARK,
            if (!isNight) THEME_GITHUB_LIGHT else null,      THEME_GITHUB_DARK,
            if (!isNight) THEME_SUBLIME else null,
            if (!isNight) THEME_PALENIGHT_LIGHT else null,  THEME_PALENIGHT,
            THEME_NORD,
            if (!isNight) THEME_NORD_FROZEN else null,       THEME_NORD_POLAR,
            THEME_TOKYO_NIGHT,
            if (!isNight) THEME_TOKYO_NIGHT_STORM else null, if (!isNight) THEME_TOKYO_NIGHT_LIGHT else null,
            if (!isNight) THEME_CATPUCCIN_LATTE else null,   THEME_CATPUCCIN_MOCHA,
            if (!isNight) THEME_CATPUCCIN_MACCHIATO else null, THEME_CATPUCCIN_FRAPPE,
            if (!isNight) THEME_ONE_HALF_LIGHT else null,   THEME_ONE_HALF_DARK,
            THEME_TERMINAL_DRACULA,
            if (!isNight) THEME_TERMINAL_GRUVBOX_SOFT else null, THEME_TERMINAL_GRUVBOX_MEDIUM,
            if (!isNight) THEME_EVERFOREST_LIGHT else null,  THEME_EVERFOREST_DARK,
            if (!isNight) THEME_TOKYO_DAY else null,
            if (!isNight) THEME_ROSE_PINE_DAWN else null,    THEME_ROSE_PINE,
            if (!isNight) THEME_ROSE_PINE_MOON else null,
            if (!isNight) THEME_MATERIAL_THEME_LIGHT else null, THEME_MATERIAL_THEME_DARK
        )

        val STYLES = arrayOf(STYLE_MATERIAL, STYLE_HOLO, STYLE_ROUNDED)

        private const val THEME_ID_HOLO_BASE          = 0
        private const val THEME_ID_LXX_BASE           = 1
        private const val THEME_ID_LXX_BASE_BORDER    = 2
        private const val THEME_ID_ROUNDED_BASE        = 3
        private const val THEME_ID_ROUNDED_BASE_BORDER = 4
        private const val DEFAULT_THEME_ID = THEME_ID_LXX_BASE

        private val KEYBOARD_THEMES = arrayOf(
            KeyboardTheme(THEME_ID_HOLO_BASE,           R.style.KeyboardTheme_HoloBase),
            KeyboardTheme(THEME_ID_LXX_BASE,            R.style.KeyboardTheme_LXX_Base),
            KeyboardTheme(THEME_ID_LXX_BASE_BORDER,     R.style.KeyboardTheme_LXX_Base_Border),
            KeyboardTheme(THEME_ID_ROUNDED_BASE,        R.style.KeyboardTheme_Rounded_Base),
            KeyboardTheme(THEME_ID_ROUNDED_BASE_BORDER, R.style.KeyboardTheme_Rounded_Base_Border)
        )

        const val COLOR_ACCENT          = "accent"
        const val COLOR_GESTURE         = "gesture"
        const val COLOR_SUGGESTION_TEXT = "suggestion_text"
        const val COLOR_TEXT            = "text"
        const val COLOR_HINT_TEXT       = "hint_text"
        const val COLOR_KEYS            = "keys"
        const val COLOR_FUNCTIONAL_KEYS = "functional_keys"
        const val COLOR_SPACEBAR        = "spacebar"
        const val COLOR_SPACEBAR_TEXT   = "spacebar_text"
        const val COLOR_BACKGROUND      = "background"

        @JvmStatic
        fun getKeyboardTheme(context: Context): KeyboardTheme {
            val prefs = context.prefs()
            val style = prefs.getString(Settings.PREF_THEME_STYLE, Defaults.PREF_THEME_STYLE)
            val borders = prefs.getBoolean(Settings.PREF_THEME_KEY_BORDERS, Defaults.PREF_THEME_KEY_BORDERS)
            val matchingId = when (style) {
                STYLE_HOLO    -> THEME_ID_HOLO_BASE
                STYLE_ROUNDED -> if (borders) THEME_ID_ROUNDED_BASE_BORDER else THEME_ID_ROUNDED_BASE
                else          -> if (borders) THEME_ID_LXX_BASE_BORDER else THEME_ID_LXX_BASE
            }
            return KEYBOARD_THEMES.firstOrNull { it.themeId == matchingId } ?: KEYBOARD_THEMES[DEFAULT_THEME_ID]
        }

        fun getThemeActionAndEmojiKeyLabelFlags(themeId: Int): Int =
            if (themeId == THEME_ID_LXX_BASE || themeId == THEME_ID_ROUNDED_BASE)
                Key.LABEL_FLAGS_KEEP_BACKGROUND_ASPECT_RATIO else 0

        @JvmStatic
        fun getColorsForCurrentTheme(context: Context): Colors {
            val prefs = context.prefs()
            val isNight = SettingsActivity.forceNight
                ?: (ResourceUtils.isNight(context.resources) && prefs.getBoolean(Settings.PREF_THEME_DAY_NIGHT, Defaults.PREF_THEME_DAY_NIGHT))
            val themeName = SettingsActivity.forceTheme ?: if (isNight)
                prefs.getString(Settings.PREF_THEME_COLORS_NIGHT, Defaults.PREF_THEME_COLORS_NIGHT)
                    ?: Defaults.PREF_THEME_COLORS_NIGHT
            else
                prefs.getString(Settings.PREF_THEME_COLORS, Defaults.PREF_THEME_COLORS)
                    ?: Defaults.PREF_THEME_COLORS
            val themeStyle = prefs.getString(Settings.PREF_THEME_STYLE, Defaults.PREF_THEME_STYLE)
                ?: Defaults.PREF_THEME_STYLE
            return getThemeColors(themeName, themeStyle, context, prefs, isNight)
        }

        fun getColorsForTheme(themeName: String, context: Context, prefs: SharedPreferences, isNight: Boolean): Colors {
            val themeStyle = prefs.getString(Settings.PREF_THEME_STYLE, Defaults.PREF_THEME_STYLE)
                ?: Defaults.PREF_THEME_STYLE
            return getThemeColors(themeName, themeStyle, context, prefs, isNight)
        }

        private fun getThemeColors(themeName: String, themeStyle: String, context: Context, prefs: SharedPreferences, isNight: Boolean): Colors {
            val hasBorders = prefs.getBoolean(Settings.PREF_THEME_KEY_BORDERS, Defaults.PREF_THEME_KEY_BORDERS)
            val backgroundImage = Settings.readUserBackgroundImage(context, isNight)
            return when (themeName) {

                // ── System ────────────────────────────────────────────────
                THEME_DYNAMIC -> {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S)
                        DynamicColors(context, themeStyle, hasBorders, backgroundImage)
                    else getThemeColors(THEME_LIGHT, themeStyle, context, prefs, isNight)
                }
                THEME_LIGHT -> DefaultColors(
                    themeStyle, hasBorders,
                    ContextCompat.getColor(context, R.color.gesture_trail_color_lxx_light),
                    ContextCompat.getColor(context, R.color.keyboard_background_lxx_light_border),
                    ContextCompat.getColor(context, R.color.key_background_normal_lxx_light_border),
                    ContextCompat.getColor(context, R.color.key_background_functional_lxx_light_border),
                    ContextCompat.getColor(context, R.color.key_background_normal_lxx_light_border),
                    ContextCompat.getColor(context, R.color.key_text_color_lxx_light),
                    ContextCompat.getColor(context, R.color.key_hint_letter_color_lxx_light),
                    keyboardBackground = backgroundImage
                )
                THEME_DARK -> DefaultColors(
                    themeStyle, hasBorders,
                    ContextCompat.getColor(context, R.color.gesture_trail_color_lxx_dark),
                    "#263238".toColorInt(), "#364248".toColorInt(), "#2d393f".toColorInt(),
                    "#364248".toColorInt(),
                    ContextCompat.getColor(context, R.color.key_text_color_lxx_dark),
                    ContextCompat.getColor(context, R.color.key_hint_letter_color_lxx_dark),
                    keyboardBackground = backgroundImage
                )
                THEME_HOLO_WHITE -> DefaultColors(
                    themeStyle, hasBorders,
                    Color.WHITE, "#282828".toColorInt(), Color.WHITE, "#444444".toColorInt(),
                    Color.WHITE, Color.WHITE, "#282828".toColorInt(),
                    Color.WHITE, "#80FFFFFF".toColorInt(),
                    keyboardBackground = backgroundImage
                )
                THEME_DARKER -> DefaultColors(
                    themeStyle, hasBorders,
                    ContextCompat.getColor(context, R.color.gesture_trail_color_lxx_dark),
                    ContextCompat.getColor(context, R.color.keyboard_background_lxx_dark_border),
                    ContextCompat.getColor(context, R.color.key_background_normal_lxx_dark_border),
                    ContextCompat.getColor(context, R.color.key_background_functional_lxx_dark_border),
                    ContextCompat.getColor(context, R.color.key_background_normal_lxx_dark_border),
                    ContextCompat.getColor(context, R.color.key_text_color_lxx_dark),
                    ContextCompat.getColor(context, R.color.key_hint_letter_color_lxx_dark),
                    keyboardBackground = backgroundImage
                )
                THEME_BLACK -> DefaultColors(
                    themeStyle, hasBorders,
                    ContextCompat.getColor(context, R.color.gesture_trail_color_lxx_dark),
                    ContextCompat.getColor(context, R.color.background_amoled_black),
                    ContextCompat.getColor(context, R.color.background_amoled_dark),
                    ContextCompat.getColor(context, R.color.background_amoled_dark),
                    ContextCompat.getColor(context, R.color.background_amoled_dark),
                    ContextCompat.getColor(context, R.color.key_text_color_lxx_dark),
                    ContextCompat.getColor(context, R.color.key_hint_letter_color_lxx_dark),
                    keyboardBackground = backgroundImage
                )

                // ── Blue Gray ─────────────────────────────────────────────
                THEME_BLUE_GRAY -> DefaultColors(
                    themeStyle, hasBorders,
                    Color.rgb(84, 110, 122),
                    Color.rgb(236, 239, 241), Color.WHITE, Color.rgb(176, 190, 197), Color.WHITE,
                    Color.BLACK, Color.DKGRAY, keyboardBackground = backgroundImage
                )
                THEME_BLUE_GRAY_DARK -> DefaultColors(
                    themeStyle, hasBorders,
                    Color.rgb(84, 110, 122),
                    Color.rgb(28, 38, 44), Color.rgb(44, 58, 66), Color.rgb(20, 30, 36), Color.rgb(44, 58, 66),
                    Color.WHITE, Color.rgb(120, 150, 165), keyboardBackground = backgroundImage
                )

                // ── Brown ─────────────────────────────────────────────────
                THEME_BROWN -> DefaultColors(
                    themeStyle, hasBorders,
                    Color.rgb(109, 76, 65),
                    Color.rgb(239, 235, 233), Color.WHITE, Color.rgb(188, 170, 164), Color.WHITE,
                    Color.BLACK, Color.DKGRAY, keyboardBackground = backgroundImage
                )
                THEME_BROWN_DARK -> DefaultColors(
                    themeStyle, hasBorders,
                    Color.rgb(141, 110, 99),
                    Color.rgb(38, 28, 24), Color.rgb(58, 44, 38), Color.rgb(28, 18, 14), Color.rgb(58, 44, 38),
                    Color.WHITE, Color.rgb(160, 130, 115), keyboardBackground = backgroundImage
                )

                // ── Chocolate ─────────────────────────────────────────────
                THEME_CHOCOLATE_LIGHT -> DefaultColors(
                    themeStyle, hasBorders,
                    Color.rgb(80, 128, 255),
                    Color.rgb(245, 235, 228), Color.WHITE, Color.rgb(220, 195, 178), Color.WHITE,
                    Color.BLACK, Color.DKGRAY, keyboardBackground = backgroundImage
                )
                THEME_CHOCOLATE -> DefaultColors(
                    themeStyle, hasBorders,
                    Color.rgb(80, 128, 255),
                    Color.rgb(140, 112, 94), Color.rgb(193, 163, 146), Color.rgb(100, 72, 54), Color.rgb(193, 163, 146),
                    Color.WHITE, Color.rgb(220, 200, 188), keyboardBackground = backgroundImage
                )

                // ── Cloudy ────────────────────────────────────────────────
                THEME_CLOUDY_LIGHT -> DefaultColors(
                    themeStyle, hasBorders,
                    Color.rgb(255, 113, 129),
                    Color.rgb(228, 234, 240), Color.WHITE, Color.rgb(192, 202, 214), Color.WHITE,
                    Color.BLACK, Color.DKGRAY, keyboardBackground = backgroundImage
                )
                THEME_CLOUDY -> DefaultColors(
                    themeStyle, hasBorders,
                    Color.rgb(255, 113, 129),
                    Color.rgb(81, 97, 113), Color.rgb(117, 128, 142), Color.rgb(54, 66, 80), Color.rgb(117, 128, 142),
                    Color.WHITE, Color.rgb(180, 190, 200), keyboardBackground = backgroundImage
                )

                // ── Forest ────────────────────────────────────────────────
                THEME_FOREST_LIGHT -> DefaultColors(
                    themeStyle, hasBorders,
                    Color.rgb(75, 110, 75),
                    Color.rgb(235, 225, 210), Color.WHITE, Color.rgb(205, 185, 158), Color.WHITE,
                    Color.BLACK, Color.DKGRAY, keyboardBackground = backgroundImage
                )
                THEME_FOREST -> DefaultColors(
                    themeStyle, hasBorders,
                    Color.rgb(75, 110, 75),
                    Color.rgb(181, 125, 88), Color.rgb(228, 212, 191), Color.rgb(160, 120, 80), Color.rgb(228, 212, 191),
                    Color.rgb(0, 50, 0), Color.rgb(40, 80, 20), Color.rgb(0, 50, 0), Color.rgb(0, 80, 0),
                    keyboardBackground = backgroundImage
                )

                // ── Indigo ────────────────────────────────────────────────
                THEME_INDIGO -> DefaultColors(
                    themeStyle, hasBorders,
                    Color.rgb(92, 107, 192),
                    Color.rgb(232, 234, 246), Color.WHITE, Color.rgb(159, 168, 218), Color.WHITE,
                    Color.BLACK, Color.DKGRAY, keyboardBackground = backgroundImage
                )
                THEME_INDIGO_DARK -> DefaultColors(
                    themeStyle, hasBorders,
                    Color.rgb(121, 134, 203),
                    Color.rgb(26, 28, 56), Color.rgb(42, 46, 86), Color.rgb(18, 20, 44), Color.rgb(42, 46, 86),
                    Color.WHITE, Color.rgb(140, 150, 210), keyboardBackground = backgroundImage
                )

                // ── Ocean ─────────────────────────────────────────────────
                THEME_OCEAN_LIGHT -> DefaultColors(
                    themeStyle, hasBorders,
                    Color.rgb(255, 124, 0),
                    Color.rgb(220, 230, 248), Color.WHITE, Color.rgb(178, 198, 235), Color.WHITE,
                    Color.BLACK, Color.DKGRAY, keyboardBackground = backgroundImage
                )
                THEME_OCEAN -> DefaultColors(
                    themeStyle, hasBorders,
                    Color.rgb(255, 124, 0),
                    Color.rgb(89, 109, 155), Color.rgb(132, 157, 212), Color.rgb(55, 78, 138), Color.rgb(132, 157, 212),
                    Color.WHITE, Color.rgb(180, 200, 235), keyboardBackground = backgroundImage
                )

                // ── Pink ──────────────────────────────────────────────────
                THEME_PINK -> DefaultColors(
                    themeStyle, hasBorders,
                    Color.rgb(236, 64, 122),
                    Color.rgb(252, 228, 236), Color.WHITE, Color.rgb(240, 167, 195), Color.WHITE,
                    Color.BLACK, Color.DKGRAY, keyboardBackground = backgroundImage
                )
                THEME_PINK_DARK -> DefaultColors(
                    themeStyle, hasBorders,
                    Color.rgb(236, 64, 122),
                    Color.rgb(50, 20, 32), Color.rgb(76, 34, 52), Color.rgb(36, 12, 24), Color.rgb(76, 34, 52),
                    Color.WHITE, Color.rgb(200, 130, 160), keyboardBackground = backgroundImage
                )

                // ── Sand ──────────────────────────────────────────────────
                THEME_SAND -> DefaultColors(
                    themeStyle, hasBorders,
                    Color.rgb(110, 155, 255),
                    Color.rgb(242, 232, 218), Color.WHITE, Color.rgb(210, 185, 150), Color.WHITE,
                    Color.BLACK, Color.DKGRAY, keyboardBackground = backgroundImage
                )
                THEME_SAND_DARK -> DefaultColors(
                    themeStyle, hasBorders,
                    Color.rgb(110, 155, 255),
                    Color.rgb(44, 36, 24), Color.rgb(68, 56, 38), Color.rgb(32, 26, 14), Color.rgb(68, 56, 38),
                    Color.WHITE, Color.rgb(175, 155, 115), keyboardBackground = backgroundImage
                )

                // ── Violette ──────────────────────────────────────────────
                THEME_VIOLETTE_LIGHT -> DefaultColors(
                    themeStyle, hasBorders,
                    Color.rgb(200, 80, 220),
                    Color.rgb(240, 236, 255), Color.WHITE, Color.rgb(210, 198, 248), Color.WHITE,
                    Color.BLACK, Color.DKGRAY, keyboardBackground = backgroundImage
                )
                THEME_VIOLETTE -> DefaultColors(
                    themeStyle, hasBorders,
                    Color.rgb(200, 80, 220),
                    Color.rgb(112, 112, 174), Color.rgb(150, 150, 216), Color.rgb(75, 75, 155), Color.rgb(150, 150, 216),
                    Color.WHITE, Color.rgb(190, 190, 235), keyboardBackground = backgroundImage
                )

                // ── Periwinkle ────────────────────────────────────────────
                THEME_PERIWINKLE -> DefaultColors(
                    themeStyle, hasBorders,
                    Color.rgb(99, 119, 220),
                    Color.rgb(240, 244, 255), Color.WHITE, Color.rgb(196, 206, 245), Color.WHITE,
                    Color.BLACK, Color.DKGRAY, keyboardBackground = backgroundImage
                )
                THEME_PERIWINKLE_DARK -> DefaultColors(
                    themeStyle, hasBorders,
                    Color.rgb(99, 119, 220),
                    Color.rgb(20, 24, 52), Color.rgb(34, 40, 80), Color.rgb(14, 16, 40), Color.rgb(34, 40, 80),
                    Color.WHITE, Color.rgb(130, 148, 210), keyboardBackground = backgroundImage
                )

                // ── Cream ─────────────────────────────────────────────────
                THEME_CREAM -> DefaultColors(
                    themeStyle, hasBorders,
                    Color.rgb(200, 120, 30),
                    Color.rgb(255, 248, 240), Color.WHITE, Color.rgb(240, 210, 160), Color.WHITE,
                    Color.BLACK, Color.DKGRAY, keyboardBackground = backgroundImage
                )
                THEME_CREAM_DARK -> DefaultColors(
                    themeStyle, hasBorders,
                    Color.rgb(200, 120, 30),
                    Color.rgb(40, 30, 16), Color.rgb(62, 48, 26), Color.rgb(28, 20, 8), Color.rgb(62, 48, 26),
                    Color.WHITE, Color.rgb(185, 145, 90), keyboardBackground = backgroundImage
                )

                // ── Mint ──────────────────────────────────────────────────
                THEME_MINT -> DefaultColors(
                    themeStyle, hasBorders,
                    Color.rgb(30, 160, 90),
                    Color.rgb(240, 251, 244), Color.WHITE, Color.rgb(172, 220, 188), Color.WHITE,
                    Color.BLACK, Color.DKGRAY, keyboardBackground = backgroundImage
                )
                THEME_MINT_DARK -> DefaultColors(
                    themeStyle, hasBorders,
                    Color.rgb(30, 160, 90),
                    Color.rgb(14, 36, 22), Color.rgb(22, 56, 34), Color.rgb(8, 26, 14), Color.rgb(22, 56, 34),
                    Color.WHITE, Color.rgb(80, 170, 120), keyboardBackground = backgroundImage
                )

                // ── Rose ──────────────────────────────────────────────────
                THEME_ROSE -> DefaultColors(
                    themeStyle, hasBorders,
                    Color.rgb(220, 60, 100),
                    Color.rgb(255, 240, 245), Color.WHITE, Color.rgb(240, 172, 198), Color.WHITE,
                    Color.BLACK, Color.DKGRAY, keyboardBackground = backgroundImage
                )
                THEME_ROSE_DARK -> DefaultColors(
                    themeStyle, hasBorders,
                    Color.rgb(220, 60, 100),
                    Color.rgb(42, 16, 26), Color.rgb(66, 26, 42), Color.rgb(28, 8, 16), Color.rgb(66, 26, 42),
                    Color.WHITE, Color.rgb(195, 115, 145), keyboardBackground = backgroundImage
                )

                // ── Midnight ──────────────────────────────────────────────
                THEME_MIDNIGHT_LIGHT -> DefaultColors(
                    themeStyle, hasBorders,
                    Color.rgb(88, 166, 255),
                    Color.rgb(225, 234, 248), Color.WHITE, Color.rgb(178, 208, 245), Color.WHITE,
                    Color.BLACK, Color.DKGRAY, keyboardBackground = backgroundImage
                )
                THEME_MIDNIGHT -> DefaultColors(
                    themeStyle, hasBorders,
                    Color.rgb(88, 166, 255),
                    Color.rgb(13, 17, 23), Color.rgb(28, 42, 58), Color.rgb(18, 28, 44), Color.rgb(28, 42, 58),
                    Color.WHITE, Color.rgb(160, 185, 210), keyboardBackground = backgroundImage
                )

                // ── Galaxy ────────────────────────────────────────────────
                THEME_GALAXY_LIGHT -> DefaultColors(
                    themeStyle, hasBorders,
                    Color.rgb(184, 157, 255),
                    Color.rgb(240, 236, 255), Color.WHITE, Color.rgb(210, 198, 252), Color.WHITE,
                    Color.BLACK, Color.DKGRAY, keyboardBackground = backgroundImage
                )
                THEME_GALAXY -> DefaultColors(
                    themeStyle, hasBorders,
                    Color.rgb(184, 157, 255),
                    Color.rgb(26, 26, 46), Color.rgb(37, 37, 69), Color.rgb(20, 20, 50), Color.rgb(37, 37, 69),
                    Color.WHITE, Color.rgb(170, 160, 220), keyboardBackground = backgroundImage
                )

                // ── Jungle ────────────────────────────────────────────────
                THEME_JUNGLE_LIGHT -> DefaultColors(
                    themeStyle, hasBorders,
                    Color.rgb(92, 203, 120),
                    Color.rgb(232, 248, 235), Color.WHITE, Color.rgb(178, 228, 188), Color.WHITE,
                    Color.BLACK, Color.DKGRAY, keyboardBackground = backgroundImage
                )
                THEME_JUNGLE -> DefaultColors(
                    themeStyle, hasBorders,
                    Color.rgb(92, 203, 120),
                    Color.rgb(10, 31, 13), Color.rgb(20, 43, 24), Color.rgb(10, 26, 12), Color.rgb(20, 43, 24),
                    Color.WHITE, Color.rgb(140, 190, 148), keyboardBackground = backgroundImage
                )

                // ── Ember ─────────────────────────────────────────────────
                THEME_EMBER_LIGHT -> DefaultColors(
                    themeStyle, hasBorders,
                    Color.rgb(255, 159, 64),
                    Color.rgb(255, 244, 232), Color.WHITE, Color.rgb(252, 210, 160), Color.WHITE,
                    Color.BLACK, Color.DKGRAY, keyboardBackground = backgroundImage
                )
                THEME_EMBER -> DefaultColors(
                    themeStyle, hasBorders,
                    Color.rgb(255, 159, 64),
                    Color.rgb(28, 16, 8), Color.rgb(46, 28, 12), Color.rgb(24, 12, 4), Color.rgb(46, 28, 12),
                    Color.WHITE, Color.rgb(200, 160, 110), keyboardBackground = backgroundImage
                )

                // ── Lavender ──────────────────────────────────────────────
                THEME_LAVENDER -> DefaultColors(
                    themeStyle, hasBorders,
                    Color.rgb(91, 61, 200),
                    Color.rgb(245, 240, 255), Color.WHITE, Color.rgb(200, 188, 242), Color.WHITE,
                    Color.BLACK, Color.DKGRAY, keyboardBackground = backgroundImage
                )
                THEME_LAVENDER_DARK -> DefaultColors(
                    themeStyle, hasBorders,
                    Color.rgb(91, 61, 200),
                    Color.rgb(24, 18, 50), Color.rgb(40, 30, 78), Color.rgb(16, 10, 38), Color.rgb(40, 30, 78),
                    Color.WHITE, Color.rgb(148, 128, 210), keyboardBackground = backgroundImage
                )

                // ── Arctic ────────────────────────────────────────────────
                THEME_ARCTIC -> DefaultColors(
                    themeStyle, hasBorders,
                    Color.rgb(14, 107, 140),
                    Color.rgb(240, 250, 254), Color.WHITE, Color.rgb(168, 214, 232), Color.WHITE,
                    Color.BLACK, Color.DKGRAY, keyboardBackground = backgroundImage
                )
                THEME_ARCTIC_DARK -> DefaultColors(
                    themeStyle, hasBorders,
                    Color.rgb(14, 107, 140),
                    Color.rgb(8, 26, 36), Color.rgb(14, 44, 58), Color.rgb(4, 18, 28), Color.rgb(14, 44, 58),
                    Color.WHITE, Color.rgb(80, 160, 195), keyboardBackground = backgroundImage
                )

                // ── Grape ─────────────────────────────────────────────────
                THEME_GRAPE -> DefaultColors(
                    themeStyle, hasBorders,
                    Color.rgb(160, 60, 220),
                    Color.rgb(253, 246, 255), Color.WHITE, Color.rgb(224, 190, 248), Color.WHITE,
                    Color.BLACK, Color.DKGRAY, keyboardBackground = backgroundImage
                )
                THEME_GRAPE_DARK -> DefaultColors(
                    themeStyle, hasBorders,
                    Color.rgb(160, 60, 220),
                    Color.rgb(28, 12, 42), Color.rgb(46, 20, 66), Color.rgb(18, 6, 30), Color.rgb(46, 20, 66),
                    Color.WHITE, Color.rgb(175, 110, 220), keyboardBackground = backgroundImage
                )

                // ── Aqua ──────────────────────────────────────────────────
                THEME_AQUA -> DefaultColors(
                    themeStyle, hasBorders,
                    Color.rgb(0, 168, 132),
                    Color.rgb(240, 255, 252), Color.WHITE, Color.rgb(164, 228, 216), Color.WHITE,
                    Color.BLACK, Color.DKGRAY, keyboardBackground = backgroundImage
                )
                THEME_AQUA_DARK -> DefaultColors(
                    themeStyle, hasBorders,
                    Color.rgb(0, 168, 132),
                    Color.rgb(6, 30, 24), Color.rgb(10, 50, 40), Color.rgb(4, 20, 16), Color.rgb(10, 50, 40),
                    Color.WHITE, Color.rgb(60, 160, 130), keyboardBackground = backgroundImage
                )

                // ── Honey ─────────────────────────────────────────────────
                THEME_HONEY -> DefaultColors(
                    themeStyle, hasBorders,
                    Color.rgb(210, 140, 0),
                    Color.rgb(255, 251, 240), Color.WHITE, Color.rgb(248, 218, 140), Color.WHITE,
                    Color.BLACK, Color.DKGRAY, keyboardBackground = backgroundImage
                )
                THEME_HONEY_DARK -> DefaultColors(
                    themeStyle, hasBorders,
                    Color.rgb(210, 140, 0),
                    Color.rgb(36, 26, 6), Color.rgb(58, 42, 8), Color.rgb(24, 16, 2), Color.rgb(58, 42, 8),
                    Color.WHITE, Color.rgb(185, 145, 50), keyboardBackground = backgroundImage
                )

                // ── Sage ──────────────────────────────────────────────────
                THEME_SAGE -> DefaultColors(
                    themeStyle, hasBorders,
                    Color.rgb(80, 150, 60),
                    Color.rgb(242, 248, 240), Color.WHITE, Color.rgb(176, 210, 168), Color.WHITE,
                    Color.BLACK, Color.DKGRAY, keyboardBackground = backgroundImage
                )
                THEME_SAGE_DARK -> DefaultColors(
                    themeStyle, hasBorders,
                    Color.rgb(80, 150, 60),
                    Color.rgb(16, 28, 12), Color.rgb(26, 46, 20), Color.rgb(10, 18, 6), Color.rgb(26, 46, 20),
                    Color.WHITE, Color.rgb(100, 165, 80), keyboardBackground = backgroundImage
                )

                // ── Peach ─────────────────────────────────────────────────
                THEME_PEACH -> DefaultColors(
                    themeStyle, hasBorders,
                    Color.rgb(220, 90, 40),
                    Color.rgb(255, 245, 240), Color.WHITE, Color.rgb(245, 188, 164), Color.WHITE,
                    Color.BLACK, Color.DKGRAY, keyboardBackground = backgroundImage
                )
                THEME_PEACH_DARK -> DefaultColors(
                    themeStyle, hasBorders,
                    Color.rgb(220, 90, 40),
                    Color.rgb(40, 18, 8), Color.rgb(62, 28, 12), Color.rgb(26, 10, 4), Color.rgb(62, 28, 12),
                    Color.WHITE, Color.rgb(190, 120, 80), keyboardBackground = backgroundImage
                )

                // ── Matrix ────────────────────────────────────────────────
                THEME_MATRIX_LIGHT -> DefaultColors(
                    themeStyle, hasBorders,
                    Color.rgb(100, 220, 40),
                    Color.rgb(232, 248, 224), Color.WHITE, Color.rgb(185, 230, 155), Color.WHITE,
                    Color.BLACK, Color.DKGRAY, keyboardBackground = backgroundImage
                )
                THEME_MATRIX -> DefaultColors(
                    themeStyle, hasBorders,
                    Color.rgb(100, 220, 40),
                    Color.rgb(14, 26, 10), Color.rgb(25, 46, 18), Color.rgb(10, 18, 6), Color.rgb(25, 46, 18),
                    Color.WHITE, Color.rgb(120, 190, 80), keyboardBackground = backgroundImage
                )

                // ── Synthwave ─────────────────────────────────────────────
                THEME_SYNTHWAVE_LIGHT -> DefaultColors(
                    themeStyle, hasBorders,
                    Color.rgb(240, 112, 200),
                    Color.rgb(252, 236, 252), Color.WHITE, Color.rgb(240, 188, 238), Color.WHITE,
                    Color.BLACK, Color.DKGRAY, keyboardBackground = backgroundImage
                )
                THEME_SYNTHWAVE -> DefaultColors(
                    themeStyle, hasBorders,
                    Color.rgb(240, 112, 200),
                    Color.rgb(18, 16, 26), Color.rgb(30, 27, 46), Color.rgb(14, 12, 32), Color.rgb(30, 27, 46),
                    Color.WHITE, Color.rgb(200, 150, 220), keyboardBackground = backgroundImage
                )

                // ── Abyss ─────────────────────────────────────────────────
                THEME_ABYSS_LIGHT -> DefaultColors(
                    themeStyle, hasBorders,
                    Color.rgb(64, 192, 240),
                    Color.rgb(230, 246, 254), Color.WHITE, Color.rgb(172, 224, 248), Color.WHITE,
                    Color.BLACK, Color.DKGRAY, keyboardBackground = backgroundImage
                )
                THEME_ABYSS -> DefaultColors(
                    themeStyle, hasBorders,
                    Color.rgb(64, 192, 240),
                    Color.rgb(10, 21, 32), Color.rgb(18, 32, 48), Color.rgb(8, 16, 28), Color.rgb(18, 32, 48),
                    Color.WHITE, Color.rgb(120, 170, 210), keyboardBackground = backgroundImage
                )

                // ── Crimson ───────────────────────────────────────────────
                THEME_CRIMSON_LIGHT -> DefaultColors(
                    themeStyle, hasBorders,
                    Color.rgb(232, 80, 80),
                    Color.rgb(255, 236, 236), Color.WHITE, Color.rgb(245, 182, 182), Color.WHITE,
                    Color.BLACK, Color.DKGRAY, keyboardBackground = backgroundImage
                )
                THEME_CRIMSON -> DefaultColors(
                    themeStyle, hasBorders,
                    Color.rgb(232, 80, 80),
                    Color.rgb(26, 10, 10), Color.rgb(46, 18, 18), Color.rgb(22, 6, 6), Color.rgb(46, 18, 18),
                    Color.WHITE, Color.rgb(200, 130, 130), keyboardBackground = backgroundImage
                )

                // ── Moss ──────────────────────────────────────────────────
                THEME_MOSS_LIGHT -> DefaultColors(
                    themeStyle, hasBorders,
                    Color.rgb(140, 200, 80),
                    Color.rgb(238, 246, 228), Color.WHITE, Color.rgb(195, 226, 158), Color.WHITE,
                    Color.BLACK, Color.DKGRAY, keyboardBackground = backgroundImage
                )
                THEME_MOSS -> DefaultColors(
                    themeStyle, hasBorders,
                    Color.rgb(140, 200, 80),
                    Color.rgb(15, 18, 16), Color.rgb(28, 35, 26), Color.rgb(10, 14, 10), Color.rgb(28, 35, 26),
                    Color.WHITE, Color.rgb(140, 175, 110), keyboardBackground = backgroundImage
                )

                // ── Dusk ──────────────────────────────────────────────────
                THEME_DUSK_LIGHT -> DefaultColors(
                    themeStyle, hasBorders,
                    Color.rgb(255, 188, 80),
                    Color.rgb(255, 248, 232), Color.WHITE, Color.rgb(252, 220, 152), Color.WHITE,
                    Color.BLACK, Color.DKGRAY, keyboardBackground = backgroundImage
                )
                THEME_DUSK -> DefaultColors(
                    themeStyle, hasBorders,
                    Color.rgb(255, 188, 80),
                    Color.rgb(17, 14, 8), Color.rgb(35, 28, 14), Color.rgb(14, 10, 4), Color.rgb(35, 28, 14),
                    Color.WHITE, Color.rgb(195, 160, 100), keyboardBackground = backgroundImage
                )

                // ── Obsidian ──────────────────────────────────────────────
                THEME_OBSIDIAN_LIGHT -> DefaultColors(
                    themeStyle, hasBorders,
                    Color.rgb(100, 100, 100),
                    Color.rgb(245, 245, 245), Color.WHITE, Color.rgb(200, 200, 200), Color.WHITE,
                    Color.BLACK, Color.DKGRAY, keyboardBackground = backgroundImage
                )
                THEME_OBSIDIAN -> DefaultColors(
                    themeStyle, hasBorders,
                    Color.rgb(180, 180, 180),
                    Color.rgb(13, 13, 13), Color.rgb(26, 26, 26), Color.rgb(10, 10, 10), Color.rgb(26, 26, 26),
                    Color.WHITE, Color.rgb(130, 130, 130), keyboardBackground = backgroundImage
                )

                // ── Steel ─────────────────────────────────────────────────
                THEME_STEEL_LIGHT -> DefaultColors(
                    themeStyle, hasBorders,
                    Color.rgb(90, 158, 240),
                    Color.rgb(238, 243, 250), Color.WHITE, Color.rgb(189, 212, 240), Color.WHITE,
                    Color.BLACK, Color.DKGRAY, keyboardBackground = backgroundImage
                )
                THEME_STEEL -> DefaultColors(
                    themeStyle, hasBorders,
                    Color.rgb(90, 158, 240),
                    Color.rgb(10, 14, 26), Color.rgb(20, 28, 48), Color.rgb(8, 12, 22), Color.rgb(20, 28, 48),
                    Color.WHITE, Color.rgb(100, 140, 190), keyboardBackground = backgroundImage
                )

                // ── Nebula ────────────────────────────────────────────────
                THEME_NEBULA_LIGHT -> DefaultColors(
                    themeStyle, hasBorders,
                    Color.rgb(168, 85, 247),
                    Color.rgb(248, 240, 255), Color.WHITE, Color.rgb(222, 184, 248), Color.WHITE,
                    Color.BLACK, Color.DKGRAY, keyboardBackground = backgroundImage
                )
                THEME_NEBULA -> DefaultColors(
                    themeStyle, hasBorders,
                    Color.rgb(168, 85, 247),
                    Color.rgb(16, 10, 20), Color.rgb(30, 18, 40), Color.rgb(12, 6, 18), Color.rgb(30, 18, 40),
                    Color.WHITE, Color.rgb(160, 110, 210), keyboardBackground = backgroundImage
                )

                // ── Emerald ───────────────────────────────────────────────
                THEME_EMERALD_LIGHT -> DefaultColors(
                    themeStyle, hasBorders,
                    Color.rgb(52, 211, 153),
                    Color.rgb(237, 251, 244), Color.WHITE, Color.rgb(168, 232, 204), Color.WHITE,
                    Color.BLACK, Color.DKGRAY, keyboardBackground = backgroundImage
                )
                THEME_EMERALD -> DefaultColors(
                    themeStyle, hasBorders,
                    Color.rgb(52, 211, 153),
                    Color.rgb(10, 20, 16), Color.rgb(20, 34, 24), Color.rgb(6, 14, 10), Color.rgb(20, 34, 24),
                    Color.WHITE, Color.rgb(80, 180, 130), keyboardBackground = backgroundImage
                )

                // ── Lava ──────────────────────────────────────────────────
                THEME_LAVA_LIGHT -> DefaultColors(
                    themeStyle, hasBorders,
                    Color.rgb(234, 112, 18),
                    Color.rgb(255, 243, 234), Color.WHITE, Color.rgb(245, 192, 144), Color.WHITE,
                    Color.BLACK, Color.DKGRAY, keyboardBackground = backgroundImage
                )
                THEME_LAVA -> DefaultColors(
                    themeStyle, hasBorders,
                    Color.rgb(234, 112, 18),
                    Color.rgb(20, 14, 8), Color.rgb(36, 24, 4), Color.rgb(14, 8, 2), Color.rgb(36, 24, 4),
                    Color.WHITE, Color.rgb(190, 130, 70), keyboardBackground = backgroundImage
                )

                // ── Teal ──────────────────────────────────────────────────
                THEME_TEAL_DAY -> DefaultColors(
                    themeStyle, hasBorders,
                    Color.rgb(6, 182, 212),
                    Color.rgb(234, 251, 251), Color.WHITE, Color.rgb(144, 221, 232), Color.WHITE,
                    Color.BLACK, Color.DKGRAY, keyboardBackground = backgroundImage
                )
                THEME_TEAL_NIGHT -> DefaultColors(
                    themeStyle, hasBorders,
                    Color.rgb(6, 182, 212),
                    Color.rgb(12, 20, 20), Color.rgb(22, 34, 34), Color.rgb(6, 14, 14), Color.rgb(22, 34, 34),
                    Color.WHITE, Color.rgb(80, 170, 170), keyboardBackground = backgroundImage
                )

                // ── Noir Rose ─────────────────────────────────────────────
                THEME_NOIR_ROSE_LIGHT -> DefaultColors(
                    themeStyle, hasBorders,
                    Color.rgb(236, 72, 153),
                    Color.rgb(255, 240, 248), Color.WHITE, Color.rgb(240, 176, 216), Color.WHITE,
                    Color.BLACK, Color.DKGRAY, keyboardBackground = backgroundImage
                )
                THEME_NOIR_ROSE -> DefaultColors(
                    themeStyle, hasBorders,
                    Color.rgb(236, 72, 153),
                    Color.rgb(16, 8, 16), Color.rgb(32, 12, 32), Color.rgb(10, 4, 12), Color.rgb(32, 12, 32),
                    Color.WHITE, Color.rgb(190, 100, 160), keyboardBackground = backgroundImage
                )

                // ── Camo ──────────────────────────────────────────────────
                THEME_CAMO_LIGHT -> DefaultColors(
                    themeStyle, hasBorders,
                    Color.rgb(163, 230, 53),
                    Color.rgb(244, 248, 238), Color.WHITE, Color.rgb(192, 220, 144), Color.WHITE,
                    Color.BLACK, Color.DKGRAY, keyboardBackground = backgroundImage
                )
                THEME_CAMO -> DefaultColors(
                    themeStyle, hasBorders,
                    Color.rgb(163, 230, 53),
                    Color.rgb(14, 16, 10), Color.rgb(28, 32, 16), Color.rgb(8, 10, 4), Color.rgb(28, 32, 16),
                    Color.WHITE, Color.rgb(150, 185, 80), keyboardBackground = backgroundImage
                )

                // ── Garnet ────────────────────────────────────────────────
                THEME_GARNET_LIGHT -> DefaultColors(
                    themeStyle, hasBorders,
                    Color.rgb(251, 113, 133),
                    Color.rgb(255, 240, 242), Color.WHITE, Color.rgb(240, 168, 184), Color.WHITE,
                    Color.BLACK, Color.DKGRAY, keyboardBackground = backgroundImage
                )
                THEME_GARNET -> DefaultColors(
                    themeStyle, hasBorders,
                    Color.rgb(251, 113, 133),
                    Color.rgb(18, 8, 10), Color.rgb(36, 16, 24), Color.rgb(12, 4, 8), Color.rgb(36, 16, 24),
                    Color.WHITE, Color.rgb(200, 120, 135), keyboardBackground = backgroundImage
                )

                // ── Aurora ────────────────────────────────────────────────
                THEME_AURORA_LIGHT -> DefaultColors(
                    themeStyle, hasBorders,
                    Color.rgb(99, 102, 241),
                    Color.rgb(238, 240, 255), Color.WHITE, Color.rgb(184, 188, 248), Color.WHITE,
                    Color.BLACK, Color.DKGRAY, keyboardBackground = backgroundImage
                )
                THEME_AURORA -> DefaultColors(
                    themeStyle, hasBorders,
                    Color.rgb(99, 102, 241),
                    Color.rgb(8, 12, 20), Color.rgb(14, 24, 48), Color.rgb(4, 8, 18), Color.rgb(14, 24, 48),
                    Color.WHITE, Color.rgb(110, 120, 210), keyboardBackground = backgroundImage
                )

                // ── Torch ─────────────────────────────────────────────────
                THEME_TORCH_LIGHT -> DefaultColors(
                    themeStyle, hasBorders,
                    Color.rgb(245, 158, 11),
                    Color.rgb(255, 251, 238), Color.WHITE, Color.rgb(245, 216, 128), Color.WHITE,
                    Color.BLACK, Color.DKGRAY, keyboardBackground = backgroundImage
                )
                THEME_TORCH -> DefaultColors(
                    themeStyle, hasBorders,
                    Color.rgb(245, 158, 11),
                    Color.rgb(16, 12, 6), Color.rgb(32, 24, 8), Color.rgb(10, 6, 2), Color.rgb(32, 24, 8),
                    Color.WHITE, Color.rgb(190, 155, 70), keyboardBackground = backgroundImage
                )

                // ── Void ──────────────────────────────────────────────────
                THEME_VOID_LIGHT -> DefaultColors(
                    themeStyle, hasBorders,
                    Color.rgb(56, 189, 248),
                    Color.rgb(238, 244, 255), Color.WHITE, Color.rgb(168, 204, 248), Color.WHITE,
                    Color.BLACK, Color.DKGRAY, keyboardBackground = backgroundImage
                )
                THEME_VOID -> DefaultColors(
                    themeStyle, hasBorders,
                    Color.rgb(56, 189, 248),
                    Color.rgb(10, 10, 18), Color.rgb(20, 20, 40), Color.rgb(6, 6, 16), Color.rgb(20, 20, 40),
                    Color.WHITE, Color.rgb(90, 160, 200), keyboardBackground = backgroundImage
                )

                // ── IDE Themes: Monokai ────────────────────────────────────────
                THEME_MONOKAI -> DefaultColors(
                    themeStyle, hasBorders,
                    Color.rgb(249, 38, 114),
                    Color.rgb(39, 40, 34), Color.rgb(60, 58, 50), Color.rgb(50, 48, 40), Color.rgb(60, 58, 50),
                    Color.WHITE, Color.rgb(180, 130, 130), keyboardBackground = backgroundImage
                )

                // ── IDE Themes: Dracula ──────────────────────────────────────────
                THEME_DRACULA -> DefaultColors(
                    themeStyle, hasBorders,
                    Color.rgb(189, 147, 249),
                    Color.rgb(40, 42, 54), Color.rgb(68, 71, 90), Color.rgb(52, 56, 74), Color.rgb(68, 71, 90),
                    Color.WHITE, Color.rgb(165, 160, 200), keyboardBackground = backgroundImage
                )

                // ── IDE Themes: One Dark ────────────────────────────────────────
                THEME_ONE_DARK -> DefaultColors(
                    themeStyle, hasBorders,
                    Color.rgb(139, 233, 253),
                    Color.rgb(40, 44, 52), Color.rgb(57, 62, 70), Color.rgb(48, 52, 60), Color.rgb(57, 62, 70),
                    Color.WHITE, Color.rgb(140, 180, 210), keyboardBackground = backgroundImage
                )

                // ── IDE Themes: Night Owl ────────────────────────────────────────
                THEME_NIGHT_OWL -> DefaultColors(
                    themeStyle, hasBorders,
                    Color.rgb(130, 170, 255),
                    Color.rgb(11, 30, 27), Color.rgb(24, 49, 43), Color.rgb(14, 32, 28), Color.rgb(24, 49, 43),
                    Color.WHITE, Color.rgb(140, 170, 200), keyboardBackground = backgroundImage
                )
                THEME_NIGHT_OWL_LIGHT -> DefaultColors(
                    themeStyle, hasBorders,
                    Color.rgb(42, 100, 185),
                    Color.rgb(243, 246, 249), Color.WHITE, Color.rgb(198, 209, 222), Color.WHITE,
                    Color.BLACK, Color.DKGRAY, keyboardBackground = backgroundImage
                )

                // ── IDE Themes: Solarized ──────────────────────────────────────
                THEME_SOLARIZED_LIGHT -> DefaultColors(
                    themeStyle, hasBorders,
                    Color.rgb(38, 139, 210),
                    Color.rgb(253, 246, 227), Color.WHITE, Color.rgb(181, 137, 0), Color.WHITE,
                    Color.rgb(101, 123, 131), Color.rgb(131, 148, 150), keyboardBackground = backgroundImage
                )
                THEME_SOLARIZED_DARK -> DefaultColors(
                    themeStyle, hasBorders,
                    Color.rgb(38, 139, 210),
                    Color.rgb(0, 43, 54), Color.rgb(7, 54, 66), Color.rgb(0, 33, 44), Color.rgb(7, 54, 66),
                    Color.rgb(131, 148, 150), Color.rgb(88, 110, 117), keyboardBackground = backgroundImage
                )

                // ── IDE Themes: Gruvbox ─────────────────────────────────────────
                THEME_GRUVBOX_LIGHT -> DefaultColors(
                    themeStyle, hasBorders,
                    Color.rgb(184, 69, 50),
                    Color.rgb(250, 237, 205), Color.WHITE, Color.rgb(213, 196, 161), Color.WHITE,
                    Color.BLACK, Color.DKGRAY, keyboardBackground = backgroundImage
                )
                THEME_GRUVBOX_DARK -> DefaultColors(
                    themeStyle, hasBorders,
                    Color.rgb(251, 73, 52),
                    Color.rgb(40, 33, 30), Color.rgb(60, 49, 45), Color.rgb(50, 39, 35), Color.rgb(60, 49, 45),
                    Color.WHITE, Color.rgb(190, 140, 110), keyboardBackground = backgroundImage
                )

                // ── IDE Themes: JetBrains ─────────────────────────────────────
                THEME_JETBRAINS_DARK -> DefaultColors(
                    themeStyle, hasBorders,
                    Color.rgb(215, 166, 78),
                    Color.rgb(43, 43, 43), Color.rgb(61, 61, 61), Color.rgb(50, 50, 50), Color.rgb(61, 61, 61),
                    Color.WHITE, Color.rgb(160, 160, 160), keyboardBackground = backgroundImage
                )
                THEME_JETBRAINS_LIGHT -> DefaultColors(
                    themeStyle, hasBorders,
                    Color.rgb(0, 110, 165),
                    Color.rgb(255, 255, 255), Color.WHITE, Color.rgb(245, 245, 245), Color.WHITE,
                    Color.BLACK, Color.DKGRAY, keyboardBackground = backgroundImage
                )

                // ── IDE Themes: Sublime ────────────────────────────────────────
                THEME_SUBLIME -> DefaultColors(
                    themeStyle, hasBorders,
                    Color.rgb(255, 153, 0),
                    Color.rgb(45, 45, 48), Color.rgb(60, 60, 64), Color.rgb(50, 50, 54), Color.rgb(60, 60, 64),
                    Color.WHITE, Color.rgb(200, 170, 100), keyboardBackground = backgroundImage
                )

                // ── IDE Themes: VS Code ────────────────────────────────────────
                THEME_VSCODE_DARK_PLUS -> DefaultColors(
                    themeStyle, hasBorders,
                    Color.rgb(86, 182, 194),
                    Color.rgb(30, 30, 30), Color.rgb(51, 51, 51), Color.rgb(40, 40, 40), Color.rgb(51, 51, 51),
                    Color.WHITE, Color.rgb(150, 150, 150), keyboardBackground = backgroundImage
                )
                THEME_VSCODE_LIGHT_PLUS -> DefaultColors(
                    themeStyle, hasBorders,
                    Color.rgb(0, 122, 204),
                    Color.rgb(255, 255, 255), Color.WHITE, Color.rgb(243, 243, 243), Color.WHITE,
                    Color.BLACK, Color.DKGRAY, keyboardBackground = backgroundImage
                )

                // ── IDE Themes: Github ─────────────────────────────────────────
                THEME_GITHUB_LIGHT -> DefaultColors(
                    themeStyle, hasBorders,
                    Color.rgb(36, 41, 46),
                    Color.rgb(255, 255, 255), Color.WHITE, Color.rgb(246, 248, 250), Color.WHITE,
                    Color.BLACK, Color.DKGRAY, keyboardBackground = backgroundImage
                )
                THEME_GITHUB_DARK -> DefaultColors(
                    themeStyle, hasBorders,
                    Color.rgb(88, 166, 255),
                    Color.rgb(13, 17, 23), Color.rgb(22, 27, 34), Color.rgb(16, 22, 28), Color.rgb(22, 27, 34),
                    Color.WHITE, Color.rgb(140, 160, 190), keyboardBackground = backgroundImage
                )

                // ── Terminal Themes: Nord ──────────────────────────────────────
                THEME_NORD -> DefaultColors(
                    themeStyle, hasBorders,
                    Color.rgb(136, 192, 208),
                    Color.rgb(46, 52, 64), Color.rgb(67, 80, 98), Color.rgb(52, 61, 76), Color.rgb(67, 80, 98),
                    Color.WHITE, Color.rgb(146, 170, 190), keyboardBackground = backgroundImage
                )

                // ── Terminal Themes: Tokyo Night ──────────────────────────────
                THEME_TOKYO_NIGHT -> DefaultColors(
                    themeStyle, hasBorders,
                    Color.rgb(125, 207, 255),
                    Color.rgb(24, 25, 34), Color.rgb(39, 42, 56), Color.rgb(30, 32, 44), Color.rgb(39, 42, 56),
                    Color.WHITE, Color.rgb(140, 160, 200), keyboardBackground = backgroundImage
                )
                THEME_TOKYO_NIGHT_LIGHT -> DefaultColors(
                    themeStyle, hasBorders,
                    Color.rgb(0, 159, 249),
                    Color.rgb(227, 230, 236), Color.WHITE, Color.rgb(196, 205, 218), Color.WHITE,
                    Color.BLACK, Color.DKGRAY, keyboardBackground = backgroundImage
                )

                // ── Terminal Themes: Catppuccin ────────────────────────────────
                THEME_CATPUCCIN_LATTE -> DefaultColors(
                    themeStyle, hasBorders,
                    Color.rgb(16, 104, 179),
                    Color.rgb(239, 240, 245), Color.WHITE, Color.rgb(198, 208, 224), Color.WHITE,
                    Color.rgb(76, 79, 105), Color.rgb(125, 134, 156), keyboardBackground = backgroundImage
                )
                THEME_CATPUCCIN_MOCHA -> DefaultColors(
                    themeStyle, hasBorders,
                    Color.rgb(137, 180, 250),
                    Color.rgb(30, 30, 46), Color.rgb(49, 51, 70), Color.rgb(38, 40, 56), Color.rgb(49, 51, 70),
                    Color.WHITE, Color.rgb(160, 170, 200), keyboardBackground = backgroundImage
                )

                // ── Terminal Themes: One Half ──────────────────────────────────
                THEME_ONE_HALF_DARK -> DefaultColors(
                    themeStyle, hasBorders,
                    Color.rgb(97, 175, 239),
                    Color.rgb(40, 42, 54), Color.rgb(57, 62, 70), Color.rgb(48, 52, 60), Color.rgb(57, 62, 70),
                    Color.WHITE, Color.rgb(160, 170, 190), keyboardBackground = backgroundImage
                )
                THEME_ONE_HALF_LIGHT -> DefaultColors(
                    themeStyle, hasBorders,
                    Color.rgb(97, 175, 239),
                    Color.rgb(250, 250, 250), Color.WHITE, Color.rgb(200, 200, 200), Color.WHITE,
                    Color.BLACK, Color.DKGRAY, keyboardBackground = backgroundImage
                )

                // ── Terminal Themes: Everforest ────────────────────────────────
                THEME_EVERFOREST_DARK -> DefaultColors(
                    themeStyle, hasBorders,
                    Color.rgb(70, 142, 120),
                    Color.rgb(43, 48, 42), Color.rgb(61, 68, 60), Color.rgb(50, 56, 50), Color.rgb(61, 68, 60),
                    Color.WHITE, Color.rgb(130, 160, 130), keyboardBackground = backgroundImage
                )
                THEME_EVERFOREST_LIGHT -> DefaultColors(
                    themeStyle, hasBorders,
                    Color.rgb(70, 142, 120),
                    Color.rgb(236, 232, 220), Color.WHITE, Color.rgb(200, 190, 170), Color.WHITE,
                    Color.BLACK, Color.DKGRAY, keyboardBackground = backgroundImage
                )

                // ── Terminal Themes: Tokyo Day ─────────────────────────────────
                THEME_TOKYO_DAY -> DefaultColors(
                    themeStyle, hasBorders,
                    Color.rgb(0, 157, 224),
                    Color.rgb(230, 234, 246), Color.WHITE, Color.rgb(182, 197, 220), Color.WHITE,
                    Color.BLACK, Color.DKGRAY, keyboardBackground = backgroundImage
                )

                // ── Terminal Themes: Rosé Pine ────────────────────────────────
                THEME_ROSE_PINE -> DefaultColors(
                    themeStyle, hasBorders,
                    Color.rgb(212, 163, 255),
                    Color.rgb(49, 50, 68), Color.rgb(69, 70, 90), Color.rgb(58, 59, 78), Color.rgb(69, 70, 90),
                    Color.WHITE, Color.rgb(170, 150, 200), keyboardBackground = backgroundImage
                )
                THEME_ROSE_PINE_DAWN -> DefaultColors(
                    themeStyle, hasBorders,
                    Color.rgb(209, 125, 161),
                    Color.rgb(253, 250, 244), Color.WHITE, Color.rgb(220, 210, 198), Color.WHITE,
                    Color.rgb(80, 73, 69), Color.rgb(150, 140, 130), keyboardBackground = backgroundImage
                )

                // ── Terminal Themes: Material ──────────────────────────────────
                THEME_MATERIAL_THEME_DARK -> DefaultColors(
                    themeStyle, hasBorders,
                    Color.rgb(95, 207, 214),
                    Color.rgb(40, 42, 46), Color.rgb(57, 61, 66), Color.rgb(48, 51, 56), Color.rgb(57, 61, 66),
                    Color.WHITE, Color.rgb(140, 160, 170), keyboardBackground = backgroundImage
                )
                THEME_MATERIAL_THEME_LIGHT -> DefaultColors(
                    themeStyle, hasBorders,
                    Color.rgb(16, 150, 188),
                    Color.rgb(250, 250, 250), Color.WHITE, Color.rgb(225, 225, 225), Color.WHITE,
                    Color.BLACK, Color.DKGRAY, keyboardBackground = backgroundImage
                )

                // ── IDE Themes: Monokai Pro ─────────────────────────────────────
                THEME_MONOKAI_PRO -> DefaultColors(
                    themeStyle, hasBorders,
                    Color.rgb(255, 157, 0),
                    Color.rgb(46, 44, 42), Color.rgb(64, 62, 58), Color.rgb(54, 52, 48), Color.rgb(64, 62, 58),
                    Color.WHITE, Color.rgb(190, 160, 110), keyboardBackground = backgroundImage
                )

                // ── IDE Themes: Dracula Orchid ─────────────────────────────────
                THEME_DRACULA_ORCHID -> DefaultColors(
                    themeStyle, hasBorders,
                    Color.rgb(247, 118, 142),
                    Color.rgb(47, 49, 66), Color.rgb(70, 74, 94), Color.rgb(56, 58, 78), Color.rgb(70, 74, 94),
                    Color.WHITE, Color.rgb(200, 140, 160), keyboardBackground = backgroundImage
                )

                // ── IDE Themes: One Dark Pro ───────────────────────────────────
                THEME_ONE_DARK_PRO -> DefaultColors(
                    themeStyle, hasBorders,
                    Color.rgb(95, 199, 255),
                    Color.rgb(36, 41, 50), Color.rgb(55, 61, 72), Color.rgb(44, 50, 60), Color.rgb(55, 61, 72),
                    Color.WHITE, Color.rgb(150, 170, 200), keyboardBackground = backgroundImage
                )

                // ── IDE Themes: Atom ───────────────────────────────────────────
                THEME_ATOM_ONE_DARK -> DefaultColors(
                    themeStyle, hasBorders,
                    Color.rgb(97, 175, 239),
                    Color.rgb(40, 44, 52), Color.rgb(57, 62, 70), Color.rgb(48, 52, 60), Color.rgb(57, 62, 70),
                    Color.WHITE, Color.rgb(160, 170, 190), keyboardBackground = backgroundImage
                )
                THEME_ATOM_ONE_LIGHT -> DefaultColors(
                    themeStyle, hasBorders,
                    Color.rgb(51, 151, 219),
                    Color.rgb(252, 252, 252), Color.WHITE, Color.rgb(198, 198, 198), Color.WHITE,
                    Color.BLACK, Color.DKGRAY, keyboardBackground = backgroundImage
                )

                // ── IDE Themes: VS Code Dark Modern ───────────────────────────
                THEME_VSCODE_DARK_MODERN -> DefaultColors(
                    themeStyle, hasBorders,
                    Color.rgb(78, 201, 243),
                    Color.rgb(30, 30, 46), Color.rgb(51, 50, 68), Color.rgb(40, 38, 54), Color.rgb(51, 50, 68),
                    Color.WHITE, Color.rgb(150, 160, 200), keyboardBackground = backgroundImage
                )

                // ── IDE Themes: Palenight ─────────────────────────────────────
                THEME_PALENIGHT -> DefaultColors(
                    themeStyle, hasBorders,
                    Color.rgb(198, 120, 221),
                    Color.rgb(40, 42, 54), Color.rgb(68, 71, 90), Color.rgb(52, 56, 74), Color.rgb(68, 71, 90),
                    Color.WHITE, Color.rgb(170, 150, 200), keyboardBackground = backgroundImage
                )
                THEME_PALENIGHT_LIGHT -> DefaultColors(
                    themeStyle, hasBorders,
                    Color.rgb(162, 89, 213),
                    Color.rgb(249, 250, 255), Color.WHITE, Color.rgb(198, 205, 230), Color.WHITE,
                    Color.BLACK, Color.DKGRAY, keyboardBackground = backgroundImage
                )

                // ── Terminal Themes: Nord Variants ─────────────────────────────
                THEME_NORD_FROZEN -> DefaultColors(
                    themeStyle, hasBorders,
                    Color.rgb(129, 161, 193),
                    Color.rgb(38, 46, 59), Color.rgb(59, 69, 86), Color.rgb(47, 55, 70), Color.rgb(59, 69, 86),
                    Color.WHITE, Color.rgb(140, 160, 180), keyboardBackground = backgroundImage
                )
                THEME_NORD_POLAR -> DefaultColors(
                    themeStyle, hasBorders,
                    Color.rgb(136, 192, 208),
                    Color.rgb(55, 63, 76), Color.rgb(76, 89, 106), Color.rgb(64, 74, 88), Color.rgb(76, 89, 106),
                    Color.WHITE, Color.rgb(150, 170, 190), keyboardBackground = backgroundImage
                )

                // ── Terminal Themes: Tokyo Night Storm ─────────────────────────
                THEME_TOKYO_NIGHT_STORM -> DefaultColors(
                    themeStyle, hasBorders,
                    Color.rgb(94, 180, 255),
                    Color.rgb(25, 26, 37), Color.rgb(41, 44, 59), Color.rgb(31, 34, 47), Color.rgb(41, 44, 59),
                    Color.WHITE, Color.rgb(140, 165, 200), keyboardBackground = backgroundImage
                )

                // ── Terminal Themes: Catppuccin Variants ───────────────────────
                THEME_CATPUCCIN_MACCHIATO -> DefaultColors(
                    themeStyle, hasBorders,
                    Color.rgb(166, 227, 161),
                    Color.rgb(30, 30, 46), Color.rgb(49, 51, 70), Color.rgb(38, 40, 56), Color.rgb(49, 51, 70),
                    Color.WHITE, Color.rgb(150, 180, 140), keyboardBackground = backgroundImage
                )
                THEME_CATPUCCIN_FRAPPE -> DefaultColors(
                    themeStyle, hasBorders,
                    Color.rgb(139, 213, 202),
                    Color.rgb(48, 52, 63), Color.rgb(67, 69, 82), Color.rgb(56, 58, 70), Color.rgb(67, 69, 82),
                    Color.WHITE, Color.rgb(150, 170, 180), keyboardBackground = backgroundImage
                )

                // ── Terminal Themes: Terminal Dracula ─────────────────────────
                THEME_TERMINAL_DRACULA -> DefaultColors(
                    themeStyle, hasBorders,
                    Color.rgb(189, 147, 249),
                    Color.rgb(40, 42, 54), Color.rgb(68, 71, 90), Color.rgb(52, 56, 74), Color.rgb(68, 71, 90),
                    Color.WHITE, Color.rgb(165, 160, 200), keyboardBackground = backgroundImage
                )

                // ── Terminal Themes: Terminal Gruvbox ─────────────────────────
                THEME_TERMINAL_GRUVBOX_MEDIUM -> DefaultColors(
                    themeStyle, hasBorders,
                    Color.rgb(251, 73, 52),
                    Color.rgb(42, 35, 33), Color.rgb(60, 50, 45), Color.rgb(50, 40, 35), Color.rgb(60, 50, 45),
                    Color.WHITE, Color.rgb(190, 140, 110), keyboardBackground = backgroundImage
                )
                THEME_TERMINAL_GRUVBOX_SOFT -> DefaultColors(
                    themeStyle, hasBorders,
                    Color.rgb(250, 189, 47),
                    Color.rgb(250, 240, 228), Color.WHITE, Color.rgb(215, 198, 166), Color.WHITE,
                    Color.BLACK, Color.DKGRAY, keyboardBackground = backgroundImage
                )

                // ── Terminal Themes: Rosé Pine Moon ───────────────────────────
                THEME_ROSE_PINE_MOON -> DefaultColors(
                    themeStyle, hasBorders,
                    Color.rgb(200, 167, 253),
                    Color.rgb(32, 32, 44), Color.rgb(48, 48, 64), Color.rgb(38, 38, 52), Color.rgb(48, 48, 64),
                    Color.WHITE, Color.rgb(165, 155, 200), keyboardBackground = backgroundImage
                )

                else -> { // user-defined theme
                    val colorSettings = readUserColors(prefs, themeName)
                    val colors = readUserColorTheme(themeStyle, hasBorders, colorSettings, context, isNight, backgroundImage)
                    if (readUserMoreColors(prefs, themeName) == 2)
                        AllColors(readUserAllColors(prefs, themeName, colors), themeStyle, hasBorders, backgroundImage)
                    else colors
                }
            }
        }

        fun readUserColorTheme(themeStyle: String, hasBorders: Boolean, colorSettings: List<ColorSetting>, context: Context, isNight: Boolean, backgroundImage: Drawable?): Colors {
            return DefaultColors(
                themeStyle, hasBorders,
                determineUserColor(colorSettings, context, COLOR_ACCENT, isNight),
                determineUserColor(colorSettings, context, COLOR_BACKGROUND, isNight),
                determineUserColor(colorSettings, context, COLOR_KEYS, isNight),
                determineUserColor(colorSettings, context, COLOR_FUNCTIONAL_KEYS, isNight),
                determineUserColor(colorSettings, context, COLOR_SPACEBAR, isNight),
                determineUserColor(colorSettings, context, COLOR_TEXT, isNight),
                determineUserColor(colorSettings, context, COLOR_HINT_TEXT, isNight),
                determineUserColor(colorSettings, context, COLOR_SUGGESTION_TEXT, isNight),
                determineUserColor(colorSettings, context, COLOR_SPACEBAR_TEXT, isNight),
                determineUserColor(colorSettings, context, COLOR_GESTURE, isNight),
                backgroundImage,
            )
        }

        fun writeUserColors(prefs: SharedPreferences, themeName: String, colors: List<ColorSetting>) {
            val key = Settings.PREF_USER_COLORS_PREFIX + themeName
            val value = Json.encodeToString(colors.filter { it.color != null || it.auto == false })
            prefs.edit { putString(key, value) }
            KeyboardSwitcher.getInstance().setThemeNeedsReload()
        }

        fun readUserColors(prefs: SharedPreferences, themeName: String): List<ColorSetting> {
            val key = Settings.PREF_USER_COLORS_PREFIX + themeName
            return Json.decodeFromString(prefs.getString(key, Defaults.PREF_USER_COLORS)!!)
        }

        fun writeUserMoreColors(prefs: SharedPreferences, themeName: String, value: Int) {
            val key = Settings.PREF_USER_MORE_COLORS_PREFIX + themeName
            prefs.edit { putInt(key, value) }
            KeyboardSwitcher.getInstance().setThemeNeedsReload()
        }

        fun readUserMoreColors(prefs: SharedPreferences, themeName: String): Int {
            val key = Settings.PREF_USER_MORE_COLORS_PREFIX + themeName
            return prefs.getInt(key, Defaults.PREF_USER_MORE_COLORS)
        }

        fun writeUserAllColors(prefs: SharedPreferences, themeName: String, colorMap: EnumMap<ColorType, Int>) {
            val key = Settings.PREF_USER_ALL_COLORS_PREFIX + themeName
            prefs.edit { putString(key, colorMap.map { "${it.key},${it.value}" }.joinToString(";")) }
            KeyboardSwitcher.getInstance().setThemeNeedsReload()
        }

        fun readUserAllColors(prefs: SharedPreferences, themeName: String, fallback: Colors?): EnumMap<ColorType, Int> {
            val key = Settings.PREF_USER_ALL_COLORS_PREFIX + themeName
            val colorsString = prefs.getString(key, Defaults.PREF_USER_ALL_COLORS)!!
            val colorMap = EnumMap<ColorType, Int>(ColorType::class.java)
            colorsString.split(";").forEach {
                val ct = try {
                    ColorType.valueOf(it.substringBefore(",").uppercase())
                } catch (_: IllegalArgumentException) { return@forEach }
                val i = it.substringAfter(",").toIntOrNull() ?: return@forEach
                colorMap[ct] = i
            }
            if (fallback != null && colorMap.size < ColorType.entries.size) {
                ColorType.entries.forEach {
                    if (it in colorMap) return@forEach
                    colorMap[it] = fallback.get(it)
                }
            }
            return colorMap
        }

        fun getUnusedThemeName(initialName: String, prefs: SharedPreferences): String {
            val existingNames = getExistingThemeNames(prefs)
            if (initialName !in existingNames) return initialName
            var i = 1
            while ("$initialName$i" in existingNames) i++
            return "$initialName$i"
        }

        private fun getExistingThemeNames(prefs: SharedPreferences) =
            prefs.all.keys.mapNotNull {
                when {
                    it.startsWith(Settings.PREF_USER_COLORS_PREFIX)      -> it.substringAfter(Settings.PREF_USER_COLORS_PREFIX)
                    it.startsWith(Settings.PREF_USER_ALL_COLORS_PREFIX)   -> it.substringAfter(Settings.PREF_USER_ALL_COLORS_PREFIX)
                    it.startsWith(Settings.PREF_USER_MORE_COLORS_PREFIX)  -> it.substringAfter(Settings.PREF_USER_MORE_COLORS_PREFIX)
                    else -> null
                }
            }.toSortedSet()

        fun renameUserColors(from: String, to: String, prefs: SharedPreferences): Boolean {
            if (to.isBlank()) return false
            if (to == from) return true
            val existingNames = getExistingThemeNames(prefs)
            if (to in existingNames) return false
            prefs.edit {
                if (prefs.contains(Settings.PREF_USER_COLORS_PREFIX + from)) {
                    putString(Settings.PREF_USER_COLORS_PREFIX + to, prefs.getString(Settings.PREF_USER_COLORS_PREFIX + from, ""))
                    remove(Settings.PREF_USER_COLORS_PREFIX + from)
                }
                if (prefs.contains(Settings.PREF_USER_ALL_COLORS_PREFIX + from)) {
                    putString(Settings.PREF_USER_ALL_COLORS_PREFIX + to, prefs.getString(Settings.PREF_USER_ALL_COLORS_PREFIX + from, ""))
                    remove(Settings.PREF_USER_ALL_COLORS_PREFIX + from)
                }
                if (prefs.contains(Settings.PREF_USER_MORE_COLORS_PREFIX + from)) {
                    putInt(Settings.PREF_USER_MORE_COLORS_PREFIX + to, prefs.getInt(Settings.PREF_USER_MORE_COLORS_PREFIX + from, 0))
                    remove(Settings.PREF_USER_MORE_COLORS_PREFIX + from)
                }
                if (prefs.getString(Settings.PREF_THEME_COLORS, Defaults.PREF_THEME_COLORS) == from)
                    putString(Settings.PREF_THEME_COLORS, to)
                if (prefs.getString(Settings.PREF_THEME_COLORS_NIGHT, Defaults.PREF_THEME_COLORS_NIGHT) == from)
                    putString(Settings.PREF_THEME_COLORS_NIGHT, to)
            }
            return true
        }

        fun determineUserColor(colors: List<ColorSetting>, context: Context, colorName: String, isNight: Boolean): Int {
            val c = colors.firstOrNull { it.name == colorName }
            val color = c?.color
            val auto = c?.auto ?: true
            return if (auto || color == null) determineAutoColor(colors, colorName, isNight, context)
            else color
        }

        private fun determineAutoColor(colors: List<ColorSetting>, colorName: String, isNight: Boolean, context: Context): Int {
            when (colorName) {
                COLOR_ACCENT -> {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q && Build.VERSION.SDK_INT < Build.VERSION_CODES.S) {
                        val wrapper: Context = ContextThemeWrapper(context, android.R.style.Theme_DeviceDefault)
                        val value = TypedValue()
                        if (wrapper.theme.resolveAttribute(android.R.attr.colorAccent, value, true)) return value.data
                    }
                    return ContextCompat.getColor(Settings.getDayNightContext(context, isNight), R.color.accent)
                }
                COLOR_GESTURE         -> return determineUserColor(colors, context, COLOR_ACCENT, isNight)
                COLOR_SUGGESTION_TEXT -> return determineUserColor(colors, context, COLOR_TEXT, isNight)
                COLOR_TEXT -> {
                    val background = determineUserColor(colors, context, COLOR_BACKGROUND, isNight)
                    return if (isBrightColor(background)) {
                        if (!context.prefs().getBoolean(Settings.PREF_THEME_KEY_BORDERS, Defaults.PREF_THEME_KEY_BORDERS)
                            || isGoodContrast(Color.BLACK, determineUserColor(colors, context, COLOR_KEYS, isNight))
                        ) Color.BLACK else Color.GRAY
                    } else Color.WHITE
                }
                COLOR_HINT_TEXT -> {
                    return if (isBrightColor(determineUserColor(colors, context, COLOR_KEYS, isNight))) Color.DKGRAY
                    else determineUserColor(colors, context, COLOR_TEXT, isNight)
                }
                COLOR_KEYS           -> return brightenOrDarken(determineUserColor(colors, context, COLOR_BACKGROUND, isNight), isNight)
                COLOR_FUNCTIONAL_KEYS -> return brightenOrDarken(determineUserColor(colors, context, COLOR_KEYS, isNight), true)
                COLOR_SPACEBAR       -> return determineUserColor(colors, context, COLOR_KEYS, isNight)
                COLOR_SPACEBAR_TEXT  -> {
                    val spacebar = determineUserColor(colors, context, COLOR_SPACEBAR, isNight)
                    val hintText = determineUserColor(colors, context, COLOR_HINT_TEXT, isNight)
                    if (isGoodContrast(hintText, spacebar)) return hintText and -0x7f000001
                    val text = determineUserColor(colors, context, COLOR_TEXT, isNight)
                    if (isGoodContrast(text, spacebar)) return text and -0x7f000001
                    return if (isBrightColor(spacebar)) Color.BLACK and -0x7f000001 else Color.WHITE and -0x7f000001
                }
                COLOR_BACKGROUND -> return ContextCompat.getColor(Settings.getDayNightContext(context, isNight), R.color.keyboard_background)
                else             -> return ContextCompat.getColor(Settings.getDayNightContext(context, isNight), R.color.keyboard_background)
            }
        }
    }
}

@Serializable
data class ColorSetting(val name: String, val auto: Boolean?, val color: Int?) {
    var displayName = name
}

/*
 * Copyright (C) 2011 The Android Open Source Project
 * modified
 * SPDX-License-Identifier: Apache-2.0 AND GPL-3.0-only
 */
package com.paoneking.nepallipikeyboard.latin.suggestions

import android.animation.ValueAnimator
import android.view.animation.DecelerateInterpolator
import android.annotation.SuppressLint
import android.content.Context
import android.content.SharedPreferences
import android.content.SharedPreferences.OnSharedPreferenceChangeListener
import android.graphics.Color
import android.graphics.drawable.Drawable
import android.graphics.drawable.GradientDrawable
import android.text.TextUtils
import android.util.AttributeSet
import android.util.TypedValue
import android.view.GestureDetector
import android.view.GestureDetector.SimpleOnGestureListener
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.View.OnLongClickListener
import android.view.ViewGroup
import android.view.accessibility.AccessibilityEvent
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.core.view.doOnNextLayout
import androidx.core.view.isVisible
import com.paoneking.nepallipikeyboard.compat.isDeviceLocked
import com.paoneking.nepallipikeyboard.event.HapticEvent
import com.paoneking.nepallipikeyboard.keyboard.KeyboardSwitcher
import com.paoneking.nepallipikeyboard.keyboard.internal.KeyboardIconsSet
import com.paoneking.nepallipikeyboard.keyboard.internal.keyboard_parser.floris.KeyCode
import com.paoneking.nepallipikeyboard.latin.AudioAndHapticFeedbackManager
import com.paoneking.nepallipikeyboard.latin.dictionary.Dictionary
import com.paoneking.nepallipikeyboard.latin.R
import com.paoneking.nepallipikeyboard.latin.SuggestedWords
import com.paoneking.nepallipikeyboard.latin.SuggestedWords.SuggestedWordInfo
import com.paoneking.nepallipikeyboard.latin.common.ColorType
import com.paoneking.nepallipikeyboard.latin.common.Colors
import com.paoneking.nepallipikeyboard.latin.common.Constants
import com.paoneking.nepallipikeyboard.latin.define.DebugFlags
import com.paoneking.nepallipikeyboard.latin.settings.DebugSettings
import com.paoneking.nepallipikeyboard.latin.settings.Defaults
import com.paoneking.nepallipikeyboard.latin.settings.Settings
import com.paoneking.nepallipikeyboard.latin.utils.ToolbarKey
import com.paoneking.nepallipikeyboard.latin.utils.ToolbarMode
import com.paoneking.nepallipikeyboard.latin.utils.addPinnedKey
import com.paoneking.nepallipikeyboard.latin.utils.createToolbarKey
import com.paoneking.nepallipikeyboard.latin.utils.dpToPx
import com.paoneking.nepallipikeyboard.latin.utils.getEnabledToolbarKeys
import com.paoneking.nepallipikeyboard.latin.utils.getPinnedToolbarKeys
import com.paoneking.nepallipikeyboard.latin.utils.prefs
import com.paoneking.nepallipikeyboard.latin.utils.removeFirst
import com.paoneking.nepallipikeyboard.latin.utils.removePinnedKey
import com.paoneking.nepallipikeyboard.latin.utils.setToolbarButtonsActivatedStateOnPrefChange
import android.os.Handler
import android.os.Looper
import java.util.concurrent.atomic.AtomicBoolean
import kotlin.math.abs
import kotlin.math.min
import androidx.core.view.isGone
import com.paoneking.nepallipikeyboard.latin.utils.onClickToolbarKey
import com.paoneking.nepallipikeyboard.latin.utils.onLongClickToolbarKey

@SuppressLint("InflateParams")
class SuggestionStripView(context: Context, attrs: AttributeSet?, defStyle: Int) :
    RelativeLayout(context, attrs, defStyle), View.OnClickListener, OnLongClickListener, OnSharedPreferenceChangeListener {

    /** Construct a [SuggestionStripView] for showing suggestions to be picked by the user. */
    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, R.attr.suggestionStripViewStyle)

    interface Listener {
        fun pickSuggestionManually(word: SuggestedWordInfo?)
        fun onCodeInput(primaryCode: Int, x: Int, y: Int, isKeyRepeat: Boolean)
        fun removeSuggestion(word: String?)
        fun removeExternalSuggestions()
        fun onSwipeDownOnToolbar()
        fun onTransliterationHelpRequested(isNepalBhasa: Boolean)
    }

    private val moreSuggestionsContainer: View
    private val wordViews = ArrayList<TextView>()
    private val debugInfoViews = ArrayList<TextView>()
    private val dividerViews = ArrayList<View>()

    init {
        val inflater = LayoutInflater.from(context)
        inflater.inflate(R.layout.suggestions_strip, this)
        moreSuggestionsContainer = inflater.inflate(R.layout.more_suggestions, null)

        val colors = Settings.getValues().mColors
        colors.setBackground(this, ColorType.STRIP_BACKGROUND)
        repeat(SuggestedWords.MAX_SUGGESTIONS) {
            val word = TextView(context, null, R.attr.suggestionWordStyle)
            word.contentDescription = resources.getString(R.string.spoken_empty_suggestion)
            word.setOnClickListener(this)
            word.setOnLongClickListener(this)
            colors.setBackground(word, ColorType.STRIP_BACKGROUND)
            wordViews.add(word)
            val divider = inflater.inflate(R.layout.suggestion_divider, null)
            dividerViews.add(divider)
            val info = TextView(context, null, R.attr.suggestionWordStyle)
            info.setTextColor(colors.get(ColorType.KEY_TEXT))
            info.setTextSize(TypedValue.COMPLEX_UNIT_DIP, DEBUG_INFO_TEXT_SIZE_IN_DIP)
            debugInfoViews.add(info)
        }

        DEBUG_SUGGESTIONS = context.prefs().getBoolean(DebugSettings.PREF_SHOW_SUGGESTION_INFOS, Defaults.PREF_SHOW_SUGGESTION_INFOS)
    }

    // toolbar views, drawables and setup
    private val toolbar: ViewGroup = findViewById(R.id.toolbar)
    private val toolbarContainer: View = findViewById(R.id.toolbar_container)
    private val pinnedKeys: ViewGroup = findViewById(R.id.pinned_keys)
    private val suggestionsStrip: ViewGroup = findViewById(R.id.suggestions_strip)
    private val toolbarExpandKey = findViewById<ImageButton>(R.id.suggestions_strip_toolbar_key)

    // transliteration composing bar
    private val transliterationComposingBar: View = findViewById(R.id.transliteration_composing_bar)
    private val transliterationRomanText: TextView = findViewById(R.id.transliteration_roman_text)
    private val transliterationArrowText: TextView = findViewById(R.id.transliteration_arrow_text)
    private val transliterationScriptText: TextView = findViewById(R.id.transliteration_script_text)
    private val transliterationHelpButton: TextView = findViewById(R.id.transliteration_help_button)
    private var transliterationBarIsNepalBhasa = false
    private val incognitoIcon = KeyboardIconsSet.instance.getNewDrawable(ToolbarKey.INCOGNITO.name, context)
    private val toolbarArrowIcon = KeyboardIconsSet.instance.getNewDrawable(KeyboardIconsSet.NAME_TOOLBAR_KEY, context)
    private val defaultToolbarBackground: Drawable = toolbarExpandKey.background
    private val enabledToolKeyBackground = GradientDrawable()
    private var direction = 1 // 1 if LTR, -1 if RTL

    private val stripRowHeight: Int get() =
        resources.getDimensionPixelSize(R.dimen.config_suggestions_strip_height)

    private val toolbarKeyLayoutParams = LinearLayout.LayoutParams(
        resources.getDimensionPixelSize(R.dimen.config_suggestions_strip_edge_key_width),
        LinearLayout.LayoutParams.MATCH_PARENT
    )

    init {
        val colors = Settings.getValues().mColors

        // expand key: keep it square, capped at strip height
        val toolbarHeight = min(toolbarExpandKey.layoutParams.height, stripRowHeight)
        toolbarExpandKey.layoutParams.height = toolbarHeight
        toolbarExpandKey.layoutParams.width = toolbarHeight // we want it square
        colors.setBackground(toolbarExpandKey, ColorType.STRIP_BACKGROUND) // necessary because background is re-used for defaultToolbarBackground
        colors.setColor(toolbarExpandKey, ColorType.TOOL_BAR_EXPAND_KEY)
        colors.setColor(toolbarExpandKey.background, ColorType.TOOL_BAR_EXPAND_KEY_BACKGROUND)

        // background indicator for pinned keys
        val color = colors.get(ColorType.TOOL_BAR_KEY_ENABLED_BACKGROUND) or -0x1000000 // ignore alpha (in Java this is more readable 0xFF000000)
        enabledToolKeyBackground.colors = intArrayOf(color, Color.TRANSPARENT)
        enabledToolKeyBackground.gradientType = GradientDrawable.RADIAL_GRADIENT
        enabledToolKeyBackground.gradientRadius = stripRowHeight / 2.1f

        val mToolbarMode = if (isGone) ToolbarMode.HIDDEN else Settings.getValues().mToolbarMode
        if (mToolbarMode == ToolbarMode.TOOLBAR_KEYS) {
            setToolbarVisibility(true)
        }

        // toolbar keys setup (no need to hide them any more when locked, because then suggestion strip is gone anyway
        for (key in getEnabledToolbarKeys(context.prefs())) {
            val button = createToolbarKey(context, key)
            button.layoutParams = toolbarKeyLayoutParams
            setupKey(button, colors)
            toolbar.addView(button)
        }
        for (pinnedKey in getPinnedToolbarKeys(context.prefs())) {
            val button = createToolbarKey(context, pinnedKey)
            button.layoutParams = toolbarKeyLayoutParams
            setupKey(button, colors)
            pinnedKeys.addView(button)
            val pinnedKeyInToolbar = toolbar.findViewWithTag<View>(pinnedKey)
            if (pinnedKeyInToolbar != null && Settings.getValues().mQuickPinToolbarKeys)
                pinnedKeyInToolbar.background = enabledToolKeyBackground
        }
        toolbarContainer.doOnNextLayout {
            // set min with of the toolbar so the weight of the toolbar keys actually does something
            // todo: results in requestLayout() improperly called by android.widget.LinearLayout during layout: running second layout pass
            toolbar.minimumWidth = toolbarContainer.width
        }

        // transliteration composing bar colors and click listeners
        colors.setBackground(transliterationComposingBar, ColorType.STRIP_BACKGROUND)
        transliterationArrowText.setTextColor(colors.get(ColorType.KEY_HINT_TEXT))
        transliterationRomanText.setTextColor(colors.get(ColorType.KEY_HINT_TEXT))
        colors.setBackground(transliterationRomanText, ColorType.STRIP_BACKGROUND)
        transliterationScriptText.setTextColor(colors.get(ColorType.SUGGESTION_TYPED_WORD))
        transliterationHelpButton.setTextColor(colors.get(ColorType.TOOL_BAR_KEY))
        colors.setBackground(transliterationHelpButton, ColorType.STRIP_BACKGROUND)
        // Tap roman text to commit it as-is (useful for typing English/Roman words)
        transliterationRomanText.setOnClickListener {
            val roman = transliterationRomanText.text.toString()
            if (::listener.isInitialized && transliterationRomanText.isClickable && roman.isNotEmpty()) {
                val info = SuggestedWordInfo(
                    roman, "", SuggestedWordInfo.MAX_SCORE, SuggestedWordInfo.KIND_TYPED,
                    com.paoneking.nepallipikeyboard.latin.dictionary.Dictionary.DICTIONARY_USER_TYPED,
                    SuggestedWordInfo.NOT_AN_INDEX, SuggestedWordInfo.NOT_A_CONFIDENCE
                )
                listener.pickSuggestionManually(info)
            }
        }
        transliterationHelpButton.setOnClickListener {
            if (::listener.isInitialized)
                listener.onTransliterationHelpRequested(transliterationBarIsNepalBhasa)
        }

        updateKeys()
    }

    private lateinit var listener: Listener
    private var suggestedWords = SuggestedWords.getEmptyInstance()
    private var startIndexOfMoreSuggestions = 0
    private var isExternalSuggestionVisible = false // Required to disable the more suggestions if other suggestions are visible
    private val layoutHelper = SuggestionStripLayoutHelper(context, attrs, defStyle, wordViews, dividerViews, debugInfoViews)
    private val moreSuggestionsView = moreSuggestionsContainer.findViewById<MoreSuggestionsView>(R.id.more_suggestions_view).apply {
        val slidingListener = object : SimpleOnGestureListener() {
            override fun onScroll(down: MotionEvent?, me: MotionEvent, deltaX: Float, deltaY: Float): Boolean {
                if (down == null) return false
                val dy = me.y - down.y
                val dx = me.x - down.x

                if (Settings.getValues().mToolbarSwipeDownToHide && dy > 50.dpToPx(resources) && abs(dy) > abs(dx)) {
                    listener.onSwipeDownOnToolbar()
                    return true
                }

                // Allow swipe-up only when the touch started in the suggestion strip row (below the toolbar).
                val touchStartedInStrip = down.y >= toolbarContainer.height
                return if (!isExternalSuggestionVisible && touchStartedInStrip && deltaY > 0 && dy < (-10).dpToPx(resources)) showMoreSuggestions()
                else false
            }
        }
        gestureDetector = GestureDetector(context, slidingListener)
    }

    // public stuff

    val isShowingMoreSuggestionPanel get() = moreSuggestionsView.isShowingInParent

    /** A connection back to the input method. */
    fun setListener(newListener: Listener, inputView: View) {
        listener = newListener
        moreSuggestionsView.listener = newListener
        moreSuggestionsView.mainKeyboardView = inputView.findViewById(R.id.keyboard_view)
    }

    fun setRtl(isRtlLanguage: Boolean) {
        val newLayoutDirection: Int
        if (!Settings.getValues().mVarToolbarDirection)
            newLayoutDirection = LAYOUT_DIRECTION_LOCALE
        else {
            newLayoutDirection = if (isRtlLanguage) LAYOUT_DIRECTION_RTL else LAYOUT_DIRECTION_LTR
            direction = if (isRtlLanguage) -1 else 1
        }
        layoutDirection = newLayoutDirection
        suggestionsStrip.layoutDirection = newLayoutDirection
    }

    private var isToolbarExpandedByUser = false

    fun setToolbarVisibility(toolbarVisible: Boolean) {
        val locked = isDeviceLocked(context)
        val actuallyExpanded = toolbarVisible && !locked

        // Remember expanded state so keep-expanded can restore it on next input
        if (!locked) isToolbarExpandedByUser = toolbarVisible

        // Suggestions always visible
        suggestionsStrip.isVisible = true

        if (DEBUG_SUGGESTIONS) {
            for (view in debugInfoViews) view.visibility = VISIBLE
        }

        // Cancel any in-progress rotation before starting a new one
        toolbarExpandKey.animate().cancel()
        val targetRotation = if (actuallyExpanded) 90f else 270f
        toolbarExpandKey.animate()
            .rotation(targetRotation)
            .setDuration(200)
            .setInterpolator(DecelerateInterpolator())
            .start()

        if (actuallyExpanded) {
            // Hide pinned keys, make toolbar visible at height=0, then animate to full height
            pinnedKeys.visibility = GONE
            animateToolbarHeight(from = 0, to = stripRowHeight, onStart = {
                toolbarContainer.visibility = VISIBLE
            })
        } else {
            // Animate toolbar height to 0, then hide it and restore pinned keys
            animateToolbarHeight(from = stripRowHeight, to = 0, onEnd = {
                if (!isToolbarExpandedByUser) {
                    toolbarContainer.visibility = GONE
                    if (!locked) pinnedKeys.visibility = VISIBLE
                }
            })
        }
    }

    /** Cancels any in-flight toolbar height animation before starting a new one. */
    private var expandAnimator: ValueAnimator? = null

    /**
     * Animates only the [toolbarContainer] height between [from] and [to].
     * The suggestion strip row is never touched — it stays at its fixed height always.
     * This avoids triggering a parent layout pass every frame, eliminating flicker.
     */
    private fun animateToolbarHeight(
        from: Int,
        to: Int,
        onStart: (() -> Unit)? = null,
        onEnd: (() -> Unit)? = null
    ) {
        expandAnimator?.cancel()
        expandAnimator = null

        val lp = toolbarContainer.layoutParams ?: return

        // Already at target — snap and callback immediately
        if (toolbarContainer.height == to && lp.height == to) {
            onStart?.invoke()
            onEnd?.invoke()
            return
        }

        onStart?.invoke()

        // Set starting height immediately so the animator starts from the right place
        lp.height = from
        toolbarContainer.layoutParams = lp

        ValueAnimator.ofInt(from, to).also { anim ->
            expandAnimator = anim
            anim.duration = 200
            anim.interpolator = DecelerateInterpolator()
            anim.addUpdateListener {
                val lp2 = toolbarContainer.layoutParams ?: return@addUpdateListener
                lp2.height = it.animatedValue as Int
                toolbarContainer.layoutParams = lp2
            }
            anim.addListener(object : android.animation.AnimatorListenerAdapter() {
                override fun onAnimationEnd(animation: android.animation.Animator) {
                    onEnd?.invoke()
                    if (expandAnimator === anim) expandAnimator = null
                }
                override fun onAnimationCancel(animation: android.animation.Animator) {
                    if (expandAnimator === anim) expandAnimator = null
                }
            })
            anim.start()
        }
    }

    /**
     * Called by the IME when a new input field is focused / input starts.
     * If keep-expanded is on and the toolbar was last left open, re-expand it immediately.
     */
    fun onStartInput() {
        if (isDeviceLocked(context)) return
        val sv = Settings.getValues()
        if (sv.mToolbarMode != ToolbarMode.EXPANDABLE) return
        if (sv.mAlwaysExpandToolbar || isToolbarExpandedByUser) setToolbarVisibility(true) else setToolbarVisibility(false)
    }

    /**
     * Called by the IME when the selection changes (cursor move, text selected, etc.).
     * If keep-expanded is on and the toolbar was last left open, re-expand it.
     */
    fun onSelectionChanged(selectionStart: Int, selectionEnd: Int) {
        if (isDeviceLocked(context)) return
        val sv = Settings.getValues()
        if (sv.mToolbarMode != ToolbarMode.EXPANDABLE) return
        if (sv.mAlwaysExpandToolbar || isToolbarExpandedByUser) setToolbarVisibility(true) else setToolbarVisibility(false)
    }

    fun collapseToolbar() {
        expandAnimator?.cancel()
        expandAnimator = null
        toolbarExpandKey.animate().cancel()
        toolbarExpandKey.rotation = 0f
        // Snap toolbar height back to 0 and hide immediately — no animation needed
        val lp = toolbarContainer.layoutParams ?: return
        lp.height = 0
        toolbarContainer.layoutParams = lp
        toolbarContainer.visibility = GONE
        if (!isDeviceLocked(context)) pinnedKeys.visibility = VISIBLE
    }

    fun setSuggestions(suggestions: SuggestedWords, isRtlLanguage: Boolean) {
        clear()
        setRtl(isRtlLanguage)
        suggestedWords = suggestions
        startIndexOfMoreSuggestions = layoutHelper.layoutAndReturnStartIndexOfMoreSuggestions(
            context, suggestedWords, suggestionsStrip, this
        )
        isExternalSuggestionVisible = false
        updateKeys()
    }

    fun showTransliterationBar(roman: String, script: String, isNepalBhasa: Boolean, devanagariHint: String = "") {
        transliterationBarIsNepalBhasa = isNepalBhasa
        val colors = Settings.getValues().mColors
        if (roman.isEmpty() && script.isEmpty()) {
            // Idle state: "Roman → Nepali/Nepal Bhasa" hint; clicking does nothing
            transliterationRomanText.text = "Roman"
            transliterationRomanText.isClickable = false
            transliterationRomanText.setTextColor(colors.get(ColorType.KEY_HINT_TEXT))
            transliterationScriptText.text = if (isNepalBhasa) "Nepal Bhasa" else "Nepali"
            transliterationScriptText.setTextColor(colors.get(ColorType.KEY_HINT_TEXT))
        } else {
            transliterationRomanText.text = roman
            transliterationRomanText.isClickable = true
            transliterationRomanText.setTextColor(colors.get(ColorType.SUGGESTION_TYPED_WORD))
            // For Nepal Bhasa: append Devanagari hint inline in smaller hint-colored text
            if (isNepalBhasa && devanagariHint.isNotEmpty()) {
                val sb = android.text.SpannableStringBuilder(script)
                sb.append("  (")
                val devStart = sb.length
                sb.append(devanagariHint)
                sb.append(")")
                sb.setSpan(
                    android.text.style.RelativeSizeSpan(0.8f),
                    devStart - 3, sb.length,
                    android.text.Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
                )
                sb.setSpan(
                    android.text.style.ForegroundColorSpan(colors.get(ColorType.KEY_HINT_TEXT)),
                    devStart - 3, sb.length,
                    android.text.Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
                )
                transliterationScriptText.text = sb
            } else {
                transliterationScriptText.text = script
            }
            transliterationScriptText.setTextColor(colors.get(ColorType.SUGGESTION_TYPED_WORD))
        }
        if (transliterationComposingBar.visibility != VISIBLE)
            transliterationComposingBar.visibility = VISIBLE
    }

    fun hideTransliterationBar() {
        if (transliterationComposingBar.visibility != GONE)
            transliterationComposingBar.visibility = GONE
    }

    fun setExternalSuggestionView(view: View?, addCloseButton: Boolean) {
        clear()
        isExternalSuggestionVisible = true

        if (addCloseButton) {
            val wrapper = LinearLayout(context)
            suggestionsStrip.doOnNextLayout {
                wrapper.layoutParams = LinearLayout.LayoutParams(suggestionsStrip.width - 30.dpToPx(resources), LayoutParams.MATCH_PARENT)
            }
            wrapper.addView(view)
            suggestionsStrip.addView(wrapper)

            val closeButton = createToolbarKey(context, ToolbarKey.CLOSE_HISTORY)
            closeButton.layoutParams = toolbarKeyLayoutParams
            setupKey(closeButton, Settings.getValues().mColors)
            closeButton.setOnClickListener {
                listener.removeExternalSuggestions()
            }
            suggestionsStrip.addView(closeButton)
        } else {
            suggestionsStrip.addView(view)
        }
    }

    fun setMoreSuggestionsHeight(remainingHeight: Int) {
        layoutHelper.setMoreSuggestionsHeight(remainingHeight)
    }

    fun dismissMoreSuggestionsPanel() {
        moreSuggestionsView.dismissPopupKeysPanel()
    }

    // overrides: necessarily public, but not used from outside

    override fun onSharedPreferenceChanged(prefs: SharedPreferences, key: String?) {
        setToolbarButtonsActivatedStateOnPrefChange(pinnedKeys, key)
        setToolbarButtonsActivatedStateOnPrefChange(toolbar, key)
        if (key == Settings.PREF_ALWAYS_INCOGNITO_MODE)
            Handler(Looper.getMainLooper()).postDelayed({ updateKeys() }, 10)
    }

    override fun onVisibilityChanged(view: View, visibility: Int) {
        super.onVisibilityChanged(view, visibility)
        // Only propagate GONE/INVISIBLE — never force-hide suggestions when view becomes VISIBLE
        if (view === this && visibility != VISIBLE)
            suggestionsStrip.visibility = visibility
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        dismissMoreSuggestionsPanel()
    }

    override fun onLayout(changed: Boolean, l: Int, t: Int, r: Int, b: Int) {
        super.onLayout(changed, l, t, r, b)
        // Keep debug info views aligned with the suggestion strip row.
        // When the toolbar is expanded above the strip, the debug info views (placed at y=0 in
        // this RelativeLayout by SuggestionStripLayoutHelper) need to be offset by the toolbar
        // height so they sit at the top of the strip row, not the top of the toolbar.
        if (!DEBUG_SUGGESTIONS) return
        val yOffset = toolbarContainer.height
        if (yOffset == 0) return
        for (info in debugInfoViews) {
            if (info.parent === this) {
                info.offsetTopAndBottom(yOffset)
            }
        }
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        // Called by the framework when the size is known. Show the important notice if applicable.
        // This may be overridden by showing suggestions later, if applicable.
    }

    override fun dispatchPopulateAccessibilityEvent(event: AccessibilityEvent): Boolean {
        // Don't populate accessibility event with suggested words and voice key.
        return true
    }

    override fun onInterceptTouchEvent(motionEvent: MotionEvent): Boolean {
        // Detecting sliding up finger to show MoreSuggestionsView.
        return moreSuggestionsView.shouldInterceptTouchEvent(motionEvent)
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onTouchEvent(motionEvent: MotionEvent): Boolean {
        moreSuggestionsView.touchEvent(motionEvent)
        return true
    }

    override fun onClick(view: View) {
        val tag = view.tag
        if (tag is ToolbarKey) {
            onClickToolbarKey(view) { listener.onCodeInput(it, Constants.SUGGESTION_STRIP_COORDINATE, Constants.SUGGESTION_STRIP_COORDINATE, false) }
            return
        }
        AudioAndHapticFeedbackManager.getInstance().performHapticAndAudioFeedback(KeyCode.NOT_SPECIFIED, this, HapticEvent.KEY_PRESS)
        if (view === toolbarExpandKey) {
            setToolbarVisibility(toolbarContainer.visibility != VISIBLE)
        }

        // tag for word views is set in SuggestionStripLayoutHelper (setupWordViewsTextAndColor, layoutPunctuationSuggestions)
        if (tag is Int) {
            if (tag >= suggestedWords.size()) return
            val wordInfo = suggestedWords.getInfo(tag)
            listener.pickSuggestionManually(wordInfo)
        }
    }

    override fun onLongClick(view: View): Boolean {
        if (view.tag is ToolbarKey) {
            onLongClickToolbarKey(view)
            return true
        }
        AudioAndHapticFeedbackManager.getInstance().performHapticFeedback(this, HapticEvent.KEY_LONG_PRESS)
        return if (view is TextView && wordViews.contains(view)) {
            onLongClickSuggestion(view)
        } else {
            showMoreSuggestions()
        }
    }

    // private stuff

    private fun onLongClickToolbarKey(view: View) {
        val tag = view.tag as? ToolbarKey ?: return
        if (!Settings.getValues().mQuickPinToolbarKeys || view.parent === pinnedKeys) {
            onLongClickToolbarKey(view) { code, isRepeat -> listener.onCodeInput(code, Constants.SUGGESTION_STRIP_COORDINATE, Constants.SUGGESTION_STRIP_COORDINATE, isRepeat) }
        } else if (view.parent === toolbar) {
            AudioAndHapticFeedbackManager.getInstance().performHapticFeedback(this, HapticEvent.KEY_LONG_PRESS)
            val pinnedKeyView = pinnedKeys.findViewWithTag<View>(tag)
            if (pinnedKeyView == null) {
                addKeyToPinnedKeys(tag)
                toolbar.findViewWithTag<View>(tag).background = enabledToolKeyBackground
                addPinnedKey(context.prefs(), tag)
            } else {
                removePinnedKey(context.prefs(), tag)
                toolbar.findViewWithTag<View>(tag).background = defaultToolbarBackground.constantState?.newDrawable(resources)
                pinnedKeys.removeView(pinnedKeyView)
            }
        }
    }

    @SuppressLint("ClickableViewAccessibility") // no need for View#performClick, we only return false mostly anyway
    private fun onLongClickSuggestion(wordView: TextView): Boolean {
        var showIcon = true
        if (wordView.tag is Int) {
            val index = wordView.tag as Int
            val type = suggestedWords.getInfo(index).mSourceDict
            if (type == Dictionary.DICTIONARY_USER_TYPED || type == Dictionary.DICTIONARY_HARDCODED)
                showIcon = false
        }
        if (showIcon) {
            val icon = KeyboardIconsSet.instance.getNewDrawable(KeyboardIconsSet.NAME_BIN, context)
                ?: return false
            Settings.getValues().mColors.setColor(icon, ColorType.REMOVE_SUGGESTION_ICON)
            val w = icon.intrinsicWidth
            val h = icon.intrinsicHeight
            wordView.setCompoundDrawablesWithIntrinsicBounds(icon, null, null, null)
            wordView.ellipsize = TextUtils.TruncateAt.END
            val downOk = AtomicBoolean(false)
            wordView.setOnTouchListener { _, motionEvent ->
                if (motionEvent.action == MotionEvent.ACTION_UP && downOk.get()) {
                    val x = motionEvent.x
                    val y = motionEvent.y
                    if (0 < x && x < w && 0 < y && y < h) {
                        removeSuggestion(wordView)
                        wordView.cancelLongPress()
                        wordView.isPressed = false
                        return@setOnTouchListener true
                    }
                } else if (motionEvent.action == MotionEvent.ACTION_DOWN) {
                    val x = motionEvent.x
                    val y = motionEvent.y
                    if (0 < x && x < w && 0 < y && y < h) downOk.set(true)
                }
                false
            }
        }
        if (DebugFlags.DEBUG_ENABLED && (isShowingMoreSuggestionPanel || !showMoreSuggestions())) {
            showSourceDict(wordView)
            return true
        }
        return showMoreSuggestions()
    }

    private fun showMoreSuggestions(): Boolean {
        if (suggestedWords.size() <= startIndexOfMoreSuggestions) return false
        if (!moreSuggestionsView.show(
                suggestedWords, startIndexOfMoreSuggestions, moreSuggestionsContainer, layoutHelper, this
            )) return false
        for (i in 0..<startIndexOfMoreSuggestions) {
            wordViews[i].isPressed = false
        }
        return true
    }

    private fun showSourceDict(wordView: TextView) {
        val word = wordView.text.toString()
        val index = wordView.tag as? Int ?: return
        if (index >= suggestedWords.size()) return
        val info = suggestedWords.getInfo(index)
        if (info.word != word) return
        val text = info.mSourceDict.mDictType + ":" + info.mSourceDict.mLocale
        if (isShowingMoreSuggestionPanel) moreSuggestionsView.dismissPopupKeysPanel()
        KeyboardSwitcher.getInstance().showToast(text, true)
    }

    private fun removeSuggestion(wordView: TextView) {
        val word = wordView.text.toString()
        listener.removeSuggestion(word)
        moreSuggestionsView.dismissPopupKeysPanel()
        // show suggestions, but without the removed word
        val suggestedWordInfos = ArrayList<SuggestedWordInfo>()
        for (i in 0..<suggestedWords.size()) {
            val info = suggestedWords.getInfo(i)
            if (info.word != word) suggestedWordInfos.add(info)
        }
        suggestedWords.mRawSuggestions?.removeFirst { it.word == word }

        val newSuggestedWords = SuggestedWords(
            suggestedWordInfos, suggestedWords.mRawSuggestions, suggestedWords.typedWordInfo, suggestedWords.mTypedWordValid,
            suggestedWords.mWillAutoCorrect, suggestedWords.mIsObsoleteSuggestions, suggestedWords.mInputStyle, suggestedWords.mSequenceNumber
        )
        setSuggestions(newSuggestedWords, direction != 1)
        suggestionsStrip.isVisible = true
    }

    private fun clear() {
        suggestionsStrip.removeAllViews()
        if (DEBUG_SUGGESTIONS) removeAllDebugInfoViews()
        suggestionsStrip.isVisible = true // always show suggestion row, even when toolbar is expanded above it
        dismissMoreSuggestionsPanel()
        for (word in wordViews) {
            word.setOnTouchListener(null)
        }
    }

    private fun removeAllDebugInfoViews() {
        for (debugInfoView in debugInfoViews) {
            val parent = debugInfoView.parent
            if (parent is ViewGroup) parent.removeView(debugInfoView)
        }
    }

    fun updateVoiceKey() {
        val show = Settings.getValues().mShowsVoiceInputKey
        toolbar.findViewWithTag<View>(ToolbarKey.VOICE)?.isVisible = show
        pinnedKeys.findViewWithTag<View>(ToolbarKey.VOICE)?.isVisible = show
    }

    private fun updateKeys() {
        updateVoiceKey()
        val settingsValues = Settings.getValues()

        val toolbarIsExpandable = settingsValues.mToolbarMode == ToolbarMode.EXPANDABLE
        if (settingsValues.mIncognitoModeEnabled) {
            toolbarExpandKey.setImageDrawable(incognitoIcon)
            toolbarExpandKey.isVisible = true
        } else {
            toolbarExpandKey.setImageDrawable(toolbarArrowIcon)
            toolbarExpandKey.isVisible = toolbarIsExpandable
        }

        // hide pinned keys if device is locked, and avoid expanding toolbar
        val hideToolbarKeys = isDeviceLocked(context)
        toolbarExpandKey.setOnClickListener(if (hideToolbarKeys || !toolbarIsExpandable) null else this)
        pinnedKeys.visibility = if (hideToolbarKeys || toolbarContainer.isVisible) GONE else VISIBLE
        isExternalSuggestionVisible = false
    }

    private fun addKeyToPinnedKeys(pinnedKey: ToolbarKey) {
        val original = toolbar.findViewWithTag<ImageButton>(pinnedKey) ?: return
        // copy the original key to a new ImageButton
        val copy = ImageButton(context, null, R.attr.suggestionWordStyle)
        copy.tag = pinnedKey
        copy.scaleType = original.scaleType
        copy.scaleX = original.scaleX
        copy.scaleY = original.scaleY
        copy.contentDescription = original.contentDescription
        copy.setImageDrawable(original.drawable)
        copy.layoutParams = original.layoutParams
        copy.isActivated = original.isActivated
        setupKey(copy, Settings.getValues().mColors)
        pinnedKeys.addView(copy)
    }

    private fun setupKey(view: ImageButton, colors: Colors) {
        view.setOnClickListener(this)
        view.setOnLongClickListener(this)
        (view.layoutParams as LinearLayout.LayoutParams).weight = 1f
        colors.setColor(view, ColorType.TOOL_BAR_KEY)
        colors.setBackground(view, ColorType.STRIP_BACKGROUND)
    }

    companion object {
        @JvmField
        var DEBUG_SUGGESTIONS = false
        private const val DEBUG_INFO_TEXT_SIZE_IN_DIP = 6.5f
        private val TAG = SuggestionStripView::class.java.simpleName
    }
}

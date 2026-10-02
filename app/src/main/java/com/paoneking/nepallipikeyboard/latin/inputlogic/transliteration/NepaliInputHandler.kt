package com.paoneking.nepallipikeyboard.latin.inputlogic.transliteration

import com.paoneking.nepallipikeyboard.event.Event
import com.paoneking.nepallipikeyboard.event.InputTransaction
import com.paoneking.nepallipikeyboard.keyboard.internal.keyboard_parser.floris.KeyCode
import com.paoneking.nepallipikeyboard.latin.CapsMode
import com.paoneking.nepallipikeyboard.latin.RichInputConnection
import com.paoneking.nepallipikeyboard.latin.SuggestedWords
import com.paoneking.nepallipikeyboard.latin.SuggestedWords.SuggestedWordInfo
import com.paoneking.nepallipikeyboard.latin.WordComposer
import com.paoneking.nepallipikeyboard.latin.dictionary.Dictionary
import com.paoneking.nepallipikeyboard.latin.inputlogic.InputLogic
import com.paoneking.nepallipikeyboard.latin.settings.Settings
import com.paoneking.nepallipikeyboard.latin.utils.Log
import com.paoneking.nepallipikeyboard.latin.utils.transliteration.NepaliAutocorrect
import com.paoneking.nepallipikeyboard.latin.utils.transliteration.NepaliWordDictionary

object NepaliInputHandler {

    private const val TAG = "NepaliInputHandler"

    private val romanBuffer       = StringBuilder()
    private var devanagariPreview = ""
    private var enabled           = false

    // Recorrection: remember the last committed word so backspace can restore composing state
    private var lastCommittedRoman     = ""
    private var lastCommittedDevanagari = ""

    val romanWord:   String  get() = romanBuffer.toString()
    val previewText: String  get() = devanagariPreview
    val isComposing: Boolean get() = romanBuffer.isNotEmpty()

    @JvmStatic fun isActive(): Boolean = enabled

    fun enable()  { enabled = true;  resetBuffer(); clearLastCommitted(); Log.d(TAG, "enabled") }
    fun disable() { enabled = false; resetBuffer(); clearLastCommitted(); Log.d(TAG, "disabled") }

    // ─────────────────────────────────────────────────────────────────────────
    // Shift normalisation
    //
    // Letters with a DISTINCT phonetic meaning when capitalised (keep as-is):
    //   T→ट  D→ड  N→ण  (retroflex series)
    //   A→आ  I→ई  U→ऊ  (long vowels)
    //   M→ं  H→ः        (diacritics)
    //   R→ऋ  E→ऐ  O→औ  (vowel shortcuts)
    //   C → used in Ch→छ  digraph (distinct from ch→च)
    //   S → used in Sh→ष  digraph (distinct from sh→श)
    //   F→फ  X→क्ष  Z→ज्ञ  Q→क्व  B→भ  L→ळ  (shortcut keys)
    //
    // All other capitals (K G J P Y V W) map identically to their lowercase
    // equivalents, so they are normalised to lowercase on auto-shift.
    // ─────────────────────────────────────────────────────────────────────────
    private val MEANINGFUL_UPPERCASE = setOf(
        'T', 'D', 'N',              // retroflex: T→ट  D→ड  N→ण
        'M', 'H',                   // diacritics: M→anusvara  H→visarga
        'A', 'I', 'U', 'E', 'O', 'R', // long vowels / vowel shortcuts
        'C', 'S',                   // digraph starters: Ch→छ  Sh→ष
        'F', 'X', 'Z', 'Q', 'B', 'L'  // shortcut keys
    )

    private fun normaliseForBuffer(cp: Int): Char {
        val ch = cp.toChar()
        return when {
            ch.isLowerCase()           -> ch
            ch in MEANINGFUL_UPPERCASE -> ch
            else                       -> ch.lowercaseChar()
        }
    }

    fun handleLetter(
        event: Event,
        wordComposer: WordComposer,
        connection: RichInputConnection,
        inputTransaction: InputTransaction
    ): Boolean {
        if (!enabled) return false
        val cp = event.codePoint
        val isTilde = cp == '~'.code
        if (!isTilde && cp !in 'A'.code..'Z'.code && cp !in 'a'.code..'z'.code) return false

        wordComposer.applyProcessedEvent(event)
        if (wordComposer.isSingleLetter())
            wordComposer.setCapitalizedModeAtStartComposingTime(inputTransaction.shiftState)

        val ch = if (isTilde) '~'
        else {
            // Check if the keyboard automatically shifted (start of sentence)
            val isAutoShifted = romanBuffer.isEmpty() &&
                (inputTransaction.shiftState == CapsMode.AUTO || inputTransaction.shiftState == CapsMode.AUTO_LOCKED)
            if (isAutoShifted) {
                val char = cp.toChar()
                // Preserve phonetically distinct capitals even on auto-shift (T→ट, not त)
                if (char in MEANINGFUL_UPPERCASE) char else char.lowercaseChar()
            } else normaliseForBuffer(cp)
        }
        clearLastCommitted()
        romanBuffer.append(ch)
        refreshPreview()
        connection.setComposingText(devanagariPreview, 1)

        Log.d(TAG, "tap '${cp.toChar()}': '$romanWord' → '$devanagariPreview' (shift=${inputTransaction.shiftState})")
        return true
    }

    fun handleSeparator(
        wordComposer: WordComposer,
        connection: RichInputConnection,
        inputLogic: InputLogic
    ) {
        if (!enabled || !isComposing) return

        val autocorrectEnabled = Settings.getValues()?.mAutoCorrectEnabled ?: false
        val committed = if (autocorrectEnabled) NepaliAutocorrect.onWordCommit(romanWord)
        else devanagariPreview

        Log.d(TAG, "separator: '$romanWord' → '$committed' (ac=$autocorrectEnabled)")

        if (committed != devanagariPreview) connection.setComposingText(committed, 1)
        connection.finishComposingText()
        wordComposer.reset()
        inputLogic.addNepaliWordToHistory(committed)

        // Save for recorrection (backspace after space)
        lastCommittedRoman = romanWord
        lastCommittedDevanagari = committed
        resetBuffer()
    }

    fun handleBackspace(wordComposer: WordComposer, connection: RichInputConnection): Boolean {
        if (!enabled) return false

        // Recorrection: backspace after a committed Nepali word — restore composing state
        if (!isComposing && lastCommittedRoman.isNotEmpty()) {
            val wordWithSep = "$lastCommittedDevanagari "
            val charsToDelete = when {
                connection.sameAsTextBeforeCursor(wordWithSep) -> wordWithSep.length
                connection.sameAsTextBeforeCursor(lastCommittedDevanagari) -> lastCommittedDevanagari.length
                else -> 0
            }
            if (charsToDelete > 0) {
                connection.deleteTextBeforeCursor(charsToDelete)
                romanBuffer.append(lastCommittedRoman)
                clearLastCommitted()
                refreshPreview()
                connection.setComposingText(devanagariPreview, 1)
                Log.d(TAG, "recorrect: restored '$romanWord' → '$devanagariPreview'")
                return true
            }
            clearLastCommitted()
        }

        if (!isComposing) return false

        romanBuffer.deleteCharAt(romanBuffer.length - 1)

        if (romanBuffer.isEmpty()) {
            connection.setComposingText("", 1)
            connection.finishComposingText()
            wordComposer.reset()
        } else {
            refreshPreview()
            wordComposer.applyProcessedEvent(
                Event.createSoftwareKeypressEvent(KeyCode.DELETE, 0, 0, 0, false)
            )
            connection.setComposingText(devanagariPreview, 1)
        }

        Log.d(TAG, "backspace: '$romanWord' → '$devanagariPreview'")
        return true
    }

    fun commitAndReset(wordComposer: WordComposer, connection: RichInputConnection) {
        if (!enabled || !isComposing) return
        val committed = NepaliAutocorrect.getRawTransliteration(romanWord)
        if (committed != devanagariPreview) connection.setComposingText(committed, 1)
        connection.finishComposingText()
        wordComposer.reset()
        lastCommittedRoman = romanWord
        lastCommittedDevanagari = committed
        resetBuffer()
        Log.d(TAG, "finishInput: '$committed'")
    }

    fun buildSuggestedWords(inputLogic: InputLogic): SuggestedWords {
        val roman = romanWord
        if (roman.isEmpty()) return SuggestedWords.getEmptyInstance()

        val autocorrectEnabled = Settings.getValues()?.mAutoCorrectEnabled ?: false
        val rawTranslit = NepaliAutocorrect.getRawTransliteration(roman)
        val dictMatch   = NepaliAutocorrect.getBestDictMatch(roman)
        // Extras from roman_ne.dict (Devanagari keyed) — lower priority than our dict
        val extras = if (devanagariPreview.isNotEmpty())
            inputLogic.getNepaliDictSuggestions(devanagariPreview, 30)
        else emptyList()

        val infos = ArrayList<SuggestedWordInfo>()

        if (autocorrectEnabled && dictMatch != null && dictMatch != rawTranslit) {
            // [0] typed word (raw translit), [1] autocorrect target
            infos.add(info(rawTranslit, SuggestedWordInfo.KIND_TYPED, SuggestedWordInfo.MAX_SCORE))
            val dictFreq = NepaliWordDictionary.getFrequency(roman)
            infos.add(infoHardcoded(dictMatch, SuggestedWordInfo.KIND_CORRECTION, dictFreq))
            val seen = mutableSetOf(rawTranslit, dictMatch)
            // Our dict suggestions first, then roman_ne.dict extras
            NepaliAutocorrect.getSuggestionsWithFrequencies(roman, 30)
                .filter { it.first !in seen }.also { seen.addAll(it.map { s -> s.first }) }.take(20)
                .forEach { (w, freq) -> infos.add(infoHardcoded(w, SuggestedWordInfo.KIND_COMPLETION, freq)) }
            extras.filter { it !in seen }.take(9)
                .forEach { w -> infos.add(infoHardcoded(w, SuggestedWordInfo.KIND_COMPLETION, 0)) }
            if (roman !in seen)
                infos.add(info(roman, SuggestedWordInfo.KIND_COMPLETION, 0))
            return SuggestedWords(infos, null, infos[0], true, true, false,
                SuggestedWords.INPUT_STYLE_TYPING, SuggestedWords.NOT_A_SEQUENCE_NUMBER)
        }

        // Autocorrect OFF or no dict match: [0] best word, then our dict, then roman_ne.dict
        val best = dictMatch ?: rawTranslit
        val bestScore = if (dictMatch != null) NepaliWordDictionary.getFrequency(roman)
                        else SuggestedWordInfo.MAX_SCORE
        infos.add(info(best, SuggestedWordInfo.KIND_TYPED, bestScore))
        val seen = mutableSetOf(best)

        // Our dictionary suggestions (builtIn + file) — show with actual frequency scores
        NepaliAutocorrect.getSuggestionsWithFrequencies(roman, 30)
            .filter { it.first !in seen }.also { seen.addAll(it.map { s -> s.first }) }.take(20)
            .forEach { (w, freq) -> infos.add(infoHardcoded(w, SuggestedWordInfo.KIND_COMPLETION, freq)) }

        // roman_ne.dict extras — append after our dict words
        extras.filter { it !in seen }.take(9)
            .forEach { w -> infos.add(infoHardcoded(w, SuggestedWordInfo.KIND_COMPLETION, 0)) }

        // Roman word as-is — lets the user commit the typed Roman text unchanged
        if (roman !in seen)
            infos.add(info(roman, SuggestedWordInfo.KIND_COMPLETION, 0))

        return SuggestedWords(infos, null, infos[0], true, false, false,
            SuggestedWords.INPUT_STYLE_TYPING, SuggestedWords.NOT_A_SEQUENCE_NUMBER)
    }

    fun buildGestureSuggestions(devanagariGestureWord: String, inputLogic: InputLogic): SuggestedWords {
        if (devanagariGestureWord.isEmpty()) return SuggestedWords.getEmptyInstance()

        val extras = inputLogic.getNepaliDictSuggestions(devanagariGestureWord, 30)

        val infos = ArrayList<SuggestedWordInfo>()
        infos.add(info(devanagariGestureWord, SuggestedWordInfo.KIND_TYPED, SuggestedWordInfo.MAX_SCORE))
        val seen = mutableSetOf(devanagariGestureWord)
        extras.filter { it !in seen }.take(29)
            .forEach { w -> infos.add(infoHardcoded(w, SuggestedWordInfo.KIND_COMPLETION, 0)) }

        return SuggestedWords(infos, null, infos[0], true, false, false,
            SuggestedWords.INPUT_STYLE_TAIL_BATCH, SuggestedWords.NOT_A_SEQUENCE_NUMBER)
    }

    /**
     * Converts ALL words in a gesture SuggestedWords from Roman to Devanagari.
     * Used for both the floating preview bubble and the suggestion strip during gliding.
     */
    @JvmStatic
    fun buildDevanagariPreviewWords(suggestedWords: SuggestedWords): SuggestedWords {
        if (suggestedWords.size() == 0) return suggestedWords
        val topWord = suggestedWords.getWord(0) ?: return suggestedWords
        // If top word already contains Devanagari, assume all are already converted
        if (topWord.any { it.code in 0x0900..0x097F }) return suggestedWords

        val infos = ArrayList<SuggestedWordInfo>(suggestedWords.size())
        for (i in 0 until suggestedWords.size()) {
            val word = suggestedWords.getWord(i) ?: continue
            val src = suggestedWords.getInfo(i)
            val devanagari = NepaliAutocorrect.getSuggestion(word)
            infos.add(SuggestedWordInfo(
                devanagari, "", src.mScore, src.mKindAndFlags,
                Dictionary.DICTIONARY_HARDCODED,
                SuggestedWordInfo.NOT_AN_INDEX, SuggestedWordInfo.NOT_A_CONFIDENCE
            ))
        }
        if (infos.isEmpty()) return suggestedWords
        return SuggestedWords(infos, null, infos[0],
            suggestedWords.mTypedWordValid, suggestedWords.mWillAutoCorrect, false,
            suggestedWords.mInputStyle, SuggestedWords.NOT_A_SEQUENCE_NUMBER)
    }

    fun pickSuggestion(
        devanagari: String,
        wordComposer: WordComposer,
        connection: RichInputConnection,
        inputLogic: InputLogic
    ) {
        if (!enabled) return
        val roman = romanWord
        connection.setComposingText(devanagari, 1)
        connection.finishComposingText()
        wordComposer.reset()
        resetBuffer()
        // Only learn the mapping if the committed word is actually Devanagari (not the roman word itself)
        if (devanagari.any { it.code in 0x0900..0x097F })
            NepaliAutocorrect.learnWord(roman, devanagari)
        inputLogic.addNepaliWordToHistory(devanagari)
        Log.d(TAG, "picked: '$devanagari' (roman='$roman')")
    }

    /**
     * Called when user taps to place cursor on a Devanagari word.
     * Restores composing state if the word matches the last committed word.
     * Returns true if composing was restored (caller should show Nepali suggestions).
     */
    fun tryRestoreFromCursorTap(
        wordAtCursor: String,
        connection: RichInputConnection,
        composingStart: Int,
        composingEnd: Int
    ): Boolean {
        if (!enabled || lastCommittedDevanagari.isEmpty()) return false
        if (wordAtCursor != lastCommittedDevanagari) return false
        romanBuffer.clear()
        romanBuffer.append(lastCommittedRoman)
        clearLastCommitted()
        refreshPreview()
        connection.setComposingRegion(composingStart, composingEnd)
        Log.d(TAG, "cursorTap restore: '$romanWord' → '$devanagariPreview'")
        return true
    }

    /**
     * Build suggestions for a Devanagari word when cursor is tapped on it but we don't
     * have the roman form (roman buffer is not set). Shows the word + dict alternatives.
     */
    fun buildSuggestionsForDevanagariWord(devanagari: String, inputLogic: InputLogic): SuggestedWords {
        val extras = inputLogic.getNepaliDictSuggestions(devanagari, 30)
        val infos = ArrayList<SuggestedWordInfo>()
        infos.add(info(devanagari, SuggestedWordInfo.KIND_TYPED, SuggestedWordInfo.MAX_SCORE))
        val seen = mutableSetOf(devanagari)
        extras.filter { it !in seen }.take(29)
            .forEach { w -> infos.add(infoHardcoded(w, SuggestedWordInfo.KIND_COMPLETION, 0)) }
        return SuggestedWords(infos, null, infos[0], true, false, false,
            SuggestedWords.INPUT_STYLE_RECORRECTION, SuggestedWords.NOT_A_SEQUENCE_NUMBER)
    }

    fun translateGestureWord(batchInputText: String, inputLogic: InputLogic): String {
        if (!enabled) return batchInputText
        val devanagari = NepaliAutocorrect.getSuggestion(batchInputText)
        Log.d(TAG, "gesture: '$batchInputText' → '$devanagari'")
        inputLogic.addNepaliWordToHistory(devanagari)
        return devanagari
    }

    // ─────────────────────────────────────────────────────────────────────────
    // Internals
    // ─────────────────────────────────────────────────────────────────────────

    private fun refreshPreview() {
        devanagariPreview = if (romanBuffer.isEmpty()) ""
        else NepaliAutocorrect.getRawTransliteration(romanBuffer.toString())
    }

    private fun resetBuffer() {
        romanBuffer.clear()
        devanagariPreview = ""
    }

    private fun clearLastCommitted() {
        lastCommittedRoman = ""
        lastCommittedDevanagari = ""
    }

    // Used for the "typed" word (what's in the composing spot)
    private fun info(word: String, kind: Int, score: Int) = SuggestedWordInfo(
        word, "", score, kind, Dictionary.DICTIONARY_USER_TYPED,
        SuggestedWordInfo.NOT_AN_INDEX, SuggestedWordInfo.NOT_A_CONFIDENCE
    )

    // Used for completion/correction words — DICTIONARY_HARDCODED shows delete icon on long-press
    private fun infoHardcoded(word: String, kind: Int, score: Int) = SuggestedWordInfo(
        word, "", score, kind, Dictionary.DICTIONARY_HARDCODED,
        SuggestedWordInfo.NOT_AN_INDEX, SuggestedWordInfo.NOT_A_CONFIDENCE
    )
}

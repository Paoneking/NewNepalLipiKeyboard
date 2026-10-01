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
import com.paoneking.nepallipikeyboard.latin.utils.transliteration.NepalBhasaAutocorrect
import com.paoneking.nepallipikeyboard.latin.utils.transliteration.NepalBhasaWordDictionary

object NepalBhasaInputHandler {

    private const val TAG = "NepalBhasaInputHandler"

    private val romanBuffer       = StringBuilder()
    private var newaPreview       = ""
    private var enabled           = false

    private var lastCommittedRoman = ""
    private var lastCommittedNewa  = ""
    private var lastGestureRoman   = ""

    val romanWord:   String  get() = romanBuffer.toString()
    val previewText: String  get() = newaPreview
    val isComposing: Boolean get() = romanBuffer.isNotEmpty()

    @JvmStatic fun isActive(): Boolean = enabled

    fun enable()  { enabled = true;  resetBuffer(); clearLastCommitted(); Log.d(TAG, "enabled") }
    fun disable() { enabled = false; resetBuffer(); clearLastCommitted(); Log.d(TAG, "disabled") }

    // Letters with a DISTINCT phonetic meaning when capitalised (keep as-is):
    //   T→TTA  D→DDA  N→NNA  (retroflex series)
    //   A→long ā  I→long ī  U→long ū  (long vowels)
    //   M→anusvara  H→visarga
    //   R→vocalic r  E→ai  O→au  (vowel shortcuts)
    //   C→CHA  S→SSHA  B→BHA  L→murmured lateral  F→PHA  X→ksha  Z→jña  Q→kwa
    //
    // All other capitals (K G J P Y V W) map identically to their lowercase
    // equivalents, so they are normalised to lowercase on auto-shift.
    private val MEANINGFUL_UPPERCASE = setOf(
        'T', 'D', 'N',              // retroflex
        'M', 'H',                   // diacritics
        'A', 'I', 'U', 'E', 'O', 'R', // long vowels / vowel shortcuts
        'C', 'S',                   // digraph starters: Ch  Sh
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
                // Preserve phonetically distinct capitals even on auto-shift (T→TTA, not TA)
                if (char in MEANINGFUL_UPPERCASE) char else char.lowercaseChar()
            } else normaliseForBuffer(cp)
        }
        clearLastCommitted()
        romanBuffer.append(ch)
        refreshPreview()
        connection.setComposingText(newaPreview, 1)

        Log.d(TAG, "tap '${cp.toChar()}': '$romanWord' → '$newaPreview' (shift=${inputTransaction.shiftState})")
        return true
    }

    fun handleSeparator(
        wordComposer: WordComposer,
        connection: RichInputConnection,
        inputLogic: InputLogic
    ) {
        if (!enabled || !isComposing) return

        val autocorrectEnabled = Settings.getValues()?.mAutoCorrectEnabled ?: false
        val committed = if (autocorrectEnabled) NepalBhasaAutocorrect.onWordCommit(romanWord)
        else newaPreview

        Log.d(TAG, "separator: '$romanWord' → '$committed' (ac=$autocorrectEnabled)")

        if (committed != newaPreview) connection.setComposingText(committed, 1)
        connection.finishComposingText()
        wordComposer.reset()
        inputLogic.addNepalBhasaWordToHistory(committed)

        lastCommittedRoman = romanWord
        lastCommittedNewa  = committed
        resetBuffer()
    }

    fun handleBackspace(wordComposer: WordComposer, connection: RichInputConnection): Boolean {
        if (!enabled) return false

        if (!isComposing && lastCommittedRoman.isNotEmpty()) {
            val wordWithSep = "$lastCommittedNewa "
            val charsToDelete = when {
                connection.sameAsTextBeforeCursor(wordWithSep) -> wordWithSep.length
                connection.sameAsTextBeforeCursor(lastCommittedNewa) -> lastCommittedNewa.length
                else -> 0
            }
            if (charsToDelete > 0) {
                connection.deleteTextBeforeCursor(charsToDelete)
                romanBuffer.append(lastCommittedRoman)
                clearLastCommitted()
                refreshPreview()
                connection.setComposingText(newaPreview, 1)
                Log.d(TAG, "recorrect: restored '$romanWord' → '$newaPreview'")
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
            connection.setComposingText(newaPreview, 1)
        }

        Log.d(TAG, "backspace: '$romanWord' → '$newaPreview'")
        return true
    }

    fun commitAndReset(wordComposer: WordComposer, connection: RichInputConnection) {
        if (!enabled || !isComposing) return
        val committed = NepalBhasaAutocorrect.getRawTransliteration(romanWord)
        if (committed != newaPreview) connection.setComposingText(committed, 1)
        connection.finishComposingText()
        wordComposer.reset()
        lastCommittedRoman = romanWord
        lastCommittedNewa  = committed
        resetBuffer()
        Log.d(TAG, "finishInput: '$committed'")
    }

    fun buildSuggestedWords(inputLogic: InputLogic): SuggestedWords {
        val roman = romanWord
        if (roman.isEmpty()) return SuggestedWords.getEmptyInstance()

        val autocorrectEnabled = Settings.getValues()?.mAutoCorrectEnabled ?: false
        val rawTranslit = NepalBhasaAutocorrect.getRawTransliteration(roman)
        val dictMatch   = NepalBhasaAutocorrect.getBestDictMatch(roman)
        val extras = if (newaPreview.isNotEmpty())
            inputLogic.getNepalBhasaDictSuggestions(newaPreview, 30)
        else emptyList()

        val infos = ArrayList<SuggestedWordInfo>()

        if (autocorrectEnabled && dictMatch != null && dictMatch != rawTranslit) {
            infos.add(info(rawTranslit, SuggestedWordInfo.KIND_TYPED, SuggestedWordInfo.MAX_SCORE))
            val dictFreq = NepalBhasaWordDictionary.getFrequency(roman)
            infos.add(infoHardcoded(dictMatch, SuggestedWordInfo.KIND_CORRECTION, dictFreq))
            val seen = mutableSetOf(rawTranslit, dictMatch)
            NepalBhasaAutocorrect.getSuggestionsWithFrequencies(roman, 30)
                .filter { it.first !in seen }.also { seen.addAll(it.map { s -> s.first }) }.take(20)
                .forEach { (w, freq) -> infos.add(infoHardcoded(w, SuggestedWordInfo.KIND_COMPLETION, freq)) }
            extras.filter { it !in seen }.take(9)
                .forEach { w -> infos.add(infoHardcoded(w, SuggestedWordInfo.KIND_COMPLETION, 0)) }
            if (roman !in seen)
                infos.add(info(roman, SuggestedWordInfo.KIND_COMPLETION, 0))
            return SuggestedWords(infos, null, infos[0], true, true, false,
                SuggestedWords.INPUT_STYLE_TYPING, SuggestedWords.NOT_A_SEQUENCE_NUMBER)
        }

        val best = dictMatch ?: rawTranslit
        val bestScore = if (dictMatch != null) NepalBhasaWordDictionary.getFrequency(roman)
                        else SuggestedWordInfo.MAX_SCORE
        infos.add(info(best, SuggestedWordInfo.KIND_TYPED, bestScore))
        val seen = mutableSetOf(best)

        NepalBhasaAutocorrect.getSuggestionsWithFrequencies(roman, 30)
            .filter { it.first !in seen }.also { seen.addAll(it.map { s -> s.first }) }.take(20)
            .forEach { (w, freq) -> infos.add(infoHardcoded(w, SuggestedWordInfo.KIND_COMPLETION, freq)) }

        extras.filter { it !in seen }.take(9)
            .forEach { w -> infos.add(infoHardcoded(w, SuggestedWordInfo.KIND_COMPLETION, 0)) }

        // Roman word as-is — lets the user commit the typed Roman text unchanged
        if (roman !in seen)
            infos.add(info(roman, SuggestedWordInfo.KIND_COMPLETION, 0))

        return SuggestedWords(infos, null, infos[0], true, false, false,
            SuggestedWords.INPUT_STYLE_TYPING, SuggestedWords.NOT_A_SEQUENCE_NUMBER)
    }

    fun buildGestureSuggestions(newaGestureWord: String, inputLogic: InputLogic): SuggestedWords {
        if (newaGestureWord.isEmpty()) return SuggestedWords.getEmptyInstance()

        val extras = inputLogic.getNepalBhasaDictSuggestions(newaGestureWord, 30)

        val infos = ArrayList<SuggestedWordInfo>()
        infos.add(info(newaGestureWord, SuggestedWordInfo.KIND_TYPED, SuggestedWordInfo.MAX_SCORE))
        val seen = mutableSetOf(newaGestureWord)
        extras.filter { it !in seen }.take(20)
            .forEach { w -> infos.add(infoHardcoded(w, SuggestedWordInfo.KIND_COMPLETION, 0)); seen.add(w) }

        // Also look up prefix matches from builtInWordMap and roman_new.dict for the roman gesture word
        if (lastGestureRoman.isNotEmpty()) {
            NepalBhasaWordDictionary.getPrefixMatchesWithFrequency(lastGestureRoman, 20)
                .filter { (_, newa, _) -> newa !in seen }
                .forEach { (_, newa, freq) ->
                    infos.add(infoHardcoded(newa, SuggestedWordInfo.KIND_COMPLETION, freq))
                    seen.add(newa)
                }
        }

        return SuggestedWords(infos, null, infos[0], true, false, false,
            SuggestedWords.INPUT_STYLE_TAIL_BATCH, SuggestedWords.NOT_A_SEQUENCE_NUMBER)
    }

    @JvmStatic
    fun buildNewaPreviewWords(suggestedWords: SuggestedWords): SuggestedWords {
        if (suggestedWords.size() == 0) return suggestedWords
        val topWord = suggestedWords.getWord(0) ?: return suggestedWords
        // If top word already contains Newa script, assume all are already converted
        if (topWord.any { it.code in 0x11400..0x1147F }) return suggestedWords

        val infos = ArrayList<SuggestedWordInfo>(suggestedWords.size())
        for (i in 0 until suggestedWords.size()) {
            val word = suggestedWords.getWord(i) ?: continue
            val src = suggestedWords.getInfo(i)
            val newa = NepalBhasaAutocorrect.getSuggestion(word)
            infos.add(SuggestedWordInfo(
                newa, "", src.mScore, src.mKindAndFlags,
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
        newa: String,
        wordComposer: WordComposer,
        connection: RichInputConnection,
        inputLogic: InputLogic
    ) {
        if (!enabled) return
        val roman = romanWord
        connection.setComposingText(newa, 1)
        connection.finishComposingText()
        wordComposer.reset()
        resetBuffer()
        // Only learn the mapping if the committed word is actually Newa script (not the roman word itself)
        if (newa.any { it.code in 0x11400..0x1147F })
            NepalBhasaAutocorrect.learnWord(roman, newa)
        inputLogic.addNepalBhasaWordToHistory(newa)
        Log.d(TAG, "picked: '$newa' (roman='$roman')")
    }

    fun tryRestoreFromCursorTap(
        wordAtCursor: String,
        connection: RichInputConnection,
        composingStart: Int,
        composingEnd: Int
    ): Boolean {
        if (!enabled || lastCommittedNewa.isEmpty()) return false
        if (wordAtCursor != lastCommittedNewa) return false
        romanBuffer.clear()
        romanBuffer.append(lastCommittedRoman)
        clearLastCommitted()
        refreshPreview()
        connection.setComposingRegion(composingStart, composingEnd)
        Log.d(TAG, "cursorTap restore: '$romanWord' → '$newaPreview'")
        return true
    }

    fun buildSuggestionsForNewaWord(newa: String, inputLogic: InputLogic): SuggestedWords {
        val extras = inputLogic.getNepalBhasaDictSuggestions(newa, 30)
        val infos = ArrayList<SuggestedWordInfo>()
        infos.add(info(newa, SuggestedWordInfo.KIND_TYPED, SuggestedWordInfo.MAX_SCORE))
        val seen = mutableSetOf(newa)
        extras.filter { it !in seen }.take(29)
            .forEach { w -> infos.add(infoHardcoded(w, SuggestedWordInfo.KIND_COMPLETION, 0)) }
        return SuggestedWords(infos, null, infos[0], true, false, false,
            SuggestedWords.INPUT_STYLE_RECORRECTION, SuggestedWords.NOT_A_SEQUENCE_NUMBER)
    }

    fun translateGestureWord(batchInputText: String, inputLogic: InputLogic): String {
        if (!enabled) return batchInputText
        lastGestureRoman = batchInputText
        val newa = NepalBhasaAutocorrect.getSuggestion(batchInputText)
        Log.d(TAG, "gesture: '$batchInputText' → '$newa'")
        inputLogic.addNepalBhasaWordToHistory(newa)
        return newa
    }

    // ─────────────────────────────────────────────────────────────────────────

    private fun refreshPreview() {
        newaPreview = if (romanBuffer.isEmpty()) ""
        else NepalBhasaAutocorrect.getRawTransliteration(romanBuffer.toString())
    }

    private fun resetBuffer() {
        romanBuffer.clear()
        newaPreview = ""
    }

    private fun clearLastCommitted() {
        lastCommittedRoman = ""
        lastCommittedNewa  = ""
    }

    private fun info(word: String, kind: Int, score: Int) = SuggestedWordInfo(
        word, "", score, kind, Dictionary.DICTIONARY_USER_TYPED,
        SuggestedWordInfo.NOT_AN_INDEX, SuggestedWordInfo.NOT_A_CONFIDENCE
    )

    private fun infoHardcoded(word: String, kind: Int, score: Int) = SuggestedWordInfo(
        word, "", score, kind, Dictionary.DICTIONARY_HARDCODED,
        SuggestedWordInfo.NOT_AN_INDEX, SuggestedWordInfo.NOT_A_CONFIDENCE
    )
}

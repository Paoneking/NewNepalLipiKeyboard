package com.paoneking.nepallipikeyboard.latin.utils.transliteration

object NepaliAutocorrect {

    /**
     * Raw transliteration with dictionary-aware "loose" matching.
     * This is what the user SEES while composing (the live preview).
     * By checking the dictionary first, common English spellings (like "nepal")
     * will immediately show their correct form ("नेपाल") instead of strict
     * phonetic results ("नेपल").
     */
    fun getRawTransliteration(romanWord: String): String {
        if (romanWord.isBlank()) return romanWord
        // Prefer dictionary match if available (Loose Transliteration)
        return getBestDictMatch(romanWord) ?: NepaliTransliterator.transliterateWord(romanWord)
    }

    /**
     * Best dictionary match for the complete roman word (Loose matching allowed).
     * Returns null if no dict entry exists.
     * Used by autocorrect: replaces transliteration with the known word.
     */
    fun getBestDictMatch(romanWord: String): String? {
        if (romanWord.isBlank()) return null
        return NepaliWordDictionary.lookupLoose(romanWord)
    }

    /**
     * Best Devanagari for a complete roman word:
     *   1. Exact dict match  (autocorrect target)
     *   2. Raw transliteration (fallback)
     */
    fun getSuggestion(romanWord: String): String {
        if (romanWord.isBlank()) return romanWord
        return getBestDictMatch(romanWord) ?: getRawTransliteration(romanWord)
    }

    /**
     * Suggestion list for the strip while composing a partial word.
     *
     * Autocorrect ON  → caller reorders so dict match is [0].
     * Autocorrect OFF → [0] is always raw transliteration (what user sees),
     *                   followed by dict/prefix alternatives.
     *
     * Order returned here (caller may reorder for autocorrect):
     *   [0] Raw transliteration — always first so user sees live preview
     *   [1] Exact dict match (if different)
     *   [2+] Prefix matches, shortest first
     */
    fun getSuggestions(partialRoman: String, maxResults: Int = 30): List<String> =
        getSuggestionsWithFrequencies(partialRoman, maxResults).map { it.first }

    /**
     * Like getSuggestions but also returns the frequency for each word.
     * [0] Raw transliteration (frequency = 0 — just a live preview, no dict entry)
     * [1] Exact dict match (frequency from dictionary)
     * [2+] Prefix matches sorted by frequency descending
     */
    fun getSuggestionsWithFrequencies(partialRoman: String, maxResults: Int = 30): List<Pair<String, Int>> {
        if (partialRoman.isBlank()) return emptyList()

        val suggestions = mutableListOf<Pair<String, Int>>()
        val seen = mutableSetOf<String>()

        // [0] Live transliteration — always present
        val transliterated = getRawTransliteration(partialRoman)
        if (transliterated.isNotBlank()) {
            suggestions.add(transliterated to 0)
            seen.add(transliterated)
        }

        // [1] Exact dict match (inserted at position 1 if different from transliteration)
        val exact = getBestDictMatch(partialRoman)
        if (exact != null && exact !in seen) {
            val freq = NepaliWordDictionary.getFrequency(partialRoman)
            suggestions.add(minOf(1, suggestions.size), exact to freq)
            seen.add(exact)
        }

        // [2+] Prefix matches sorted by frequency descending
        NepaliWordDictionary.getPrefixMatchesWithFrequency(partialRoman, maxResults)
            .filter { (_, dev, _) -> dev !in seen }
            .forEach { (_, dev, freq) ->
                suggestions.add(dev to freq)
                seen.add(dev)
            }

        return suggestions.take(maxResults)
    }

    /**
     * Called on word commit (space/punctuation pressed).
     * Returns the best Devanagari — dict match preferred, transliteration fallback.
     */
    fun onWordCommit(romanWord: String): String = getSuggestion(romanWord)

    /**
     * Learn a word from user correction (persists for the session).
     * Call NepaliWordDictionary.saveToInternalStorage() to make it permanent.
     */
    fun learnWord(roman: String, devanagari: String) {
        if (roman.isBlank() || devanagari.isBlank()) return
        NepaliWordDictionary.addWord(roman, devanagari)
    }

    /**
     * Helper to process a full string of Romanized text (e.g. for testing or batch conversion).
     */
    fun processText(text: String): String {
        if (text.isEmpty()) return ""
        return text.split(" ").joinToString(" ") { word ->
            if (word.isEmpty()) "" else getSuggestion(word)
        }
    }

    /** True if this roman word has a dict entry (will autocorrect when AC is ON). */
    fun wouldAutocorrect(romanWord: String): Boolean =
        NepaliWordDictionary.hasDictionaryEntry(romanWord)
}

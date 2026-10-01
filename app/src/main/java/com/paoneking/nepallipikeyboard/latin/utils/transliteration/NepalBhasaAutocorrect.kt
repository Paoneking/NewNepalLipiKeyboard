package com.paoneking.nepallipikeyboard.latin.utils.transliteration

object NepalBhasaAutocorrect {

    /**
     * Raw transliteration with dictionary-aware "loose" matching.
     * This is what the user SEES while composing (the live preview).
     */
    fun getRawTransliteration(romanWord: String): String {
        if (romanWord.isBlank()) return romanWord
        // Prefer dictionary match if available (Loose Transliteration)
        return getBestDictMatch(romanWord) ?: NepalBhasaTransliterator.transliterateWord(romanWord)
    }

    fun getBestDictMatch(romanWord: String): String? {
        if (romanWord.isBlank()) return null
        return NepalBhasaWordDictionary.lookupLoose(romanWord)
    }

    fun getSuggestion(romanWord: String): String {
        if (romanWord.isBlank()) return romanWord
        return getBestDictMatch(romanWord) ?: getRawTransliteration(romanWord)
    }

    fun getSuggestionsWithFrequencies(partialRoman: String, maxResults: Int = 30): List<Pair<String, Int>> {
        if (partialRoman.isBlank()) return emptyList()

        val suggestions = mutableListOf<Pair<String, Int>>()
        val seen = mutableSetOf<String>()

        val transliterated = getRawTransliteration(partialRoman)
        if (transliterated.isNotBlank()) {
            suggestions.add(transliterated to 0)
            seen.add(transliterated)
        }

        val exact = getBestDictMatch(partialRoman)
        if (exact != null && exact !in seen) {
            val freq = NepalBhasaWordDictionary.getFrequency(partialRoman)
            suggestions.add(minOf(1, suggestions.size), exact to freq)
            seen.add(exact)
        }

        NepalBhasaWordDictionary.getPrefixMatchesWithFrequency(partialRoman, maxResults)
            .filter { (_, newa, _) -> newa !in seen }
            .forEach { (_, newa, freq) ->
                suggestions.add(newa to freq)
                seen.add(newa)
            }

        return suggestions.take(maxResults)
    }

    fun onWordCommit(romanWord: String): String = getSuggestion(romanWord)

    fun learnWord(roman: String, newa: String) {
        if (roman.isBlank() || newa.isBlank()) return
        NepalBhasaWordDictionary.addWord(roman, newa)
    }

    fun wouldAutocorrect(romanWord: String): Boolean =
        NepalBhasaWordDictionary.hasDictionaryEntry(romanWord)
}

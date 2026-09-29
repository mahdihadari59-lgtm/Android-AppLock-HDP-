package com.example.hormozgansmart.engine

import com.example.hormozgansmart.data.model.DialectWord
import com.example.hormozgansmart.data.repository.BandariDictionaryRepository

data class TranslationResult(
    val sourceText: String,
    val translatedText: String,
    val detectedWords: List<DialectWord>,
    val explanation: String,
    val direction: TranslationDirection
)

enum class TranslationDirection {
    PERSIAN_TO_BANDARI,
    BANDARI_TO_PERSIAN,
    AUTO_DETECT
}

object BandariDialectEngine {

    fun translate(input: String, direction: TranslationDirection = TranslationDirection.AUTO_DETECT): TranslationResult {
        val cleanInput = input.trim()
        if (cleanInput.isEmpty()) {
            return TranslationResult(
                sourceText = "",
                translatedText = "",
                detectedWords = emptyList(),
                explanation = "لطفاً متن یا کلمه‌ای برای ترجمه وارد کنید.",
                direction = direction
            )
        }

        val effectiveDirection = if (direction == TranslationDirection.AUTO_DETECT) {
            if (isLikelyBandari(cleanInput)) TranslationDirection.BANDARI_TO_PERSIAN else TranslationDirection.PERSIAN_TO_BANDARI
        } else {
            direction
        }

        val detected = mutableListOf<DialectWord>()
        var outputText = cleanInput

        when (effectiveDirection) {
            TranslationDirection.PERSIAN_TO_BANDARI -> {
                // Check exact phrases first
                val matchingPhrase = BandariDictionaryRepository.phrases.find { 
                    it.persian.contains(cleanInput, ignoreCase = true) || cleanInput.contains(it.persian, ignoreCase = true)
                }
                if (matchingPhrase != null) {
                    return TranslationResult(
                        sourceText = cleanInput,
                        translatedText = matchingPhrase.bandari,
                        detectedWords = emptyList(),
                        explanation = "عبارت معادل بندری: «${matchingPhrase.bandari}» — ${matchingPhrase.meaning}",
                        direction = effectiveDirection
                    )
                }

                // Word level replacement
                val wordsList = BandariDictionaryRepository.words.sortedByDescending { it.persian.length }
                for (item in wordsList) {
                    if (cleanInput.contains(item.persian, ignoreCase = true)) {
                        detected.add(item)
                        outputText = outputText.replace(Regex("\\b${Regex.escape(item.persian)}\\b|${Regex.escape(item.persian)}"), item.bandari)
                    }
                }

                val explanation = if (detected.isNotEmpty()) {
                    "کلمات تبدیل‌شده به لهجه بندری: " + detected.joinToString("، ") { "${it.persian} ➔ ${it.bandari} (${it.categoryLabel})" }
                } else {
                    "ترجمه واژگانی: «$outputText» (برای اصطلاحات خاص، بخش دسته‌بندی لغات را ببینید)"
                }

                return TranslationResult(
                    sourceText = cleanInput,
                    translatedText = outputText,
                    detectedWords = detected,
                    explanation = explanation,
                    direction = effectiveDirection
                )
            }

            TranslationDirection.BANDARI_TO_PERSIAN -> {
                val matchingPhrase = BandariDictionaryRepository.phrases.find {
                    it.bandari.contains(cleanInput, ignoreCase = true) || cleanInput.contains(it.bandari, ignoreCase = true)
                }
                if (matchingPhrase != null) {
                    return TranslationResult(
                        sourceText = cleanInput,
                        translatedText = matchingPhrase.persian,
                        detectedWords = emptyList(),
                        explanation = "معنی عبارت به فارسی معیار: «${matchingPhrase.persian}» — ${matchingPhrase.meaning}",
                        direction = effectiveDirection
                    )
                }

                val wordsList = BandariDictionaryRepository.words.sortedByDescending { it.bandari.length }
                for (item in wordsList) {
                    val normalizedBandari = normalizePersian(item.bandari)
                    val normalizedInput = normalizePersian(cleanInput)
                    if (normalizedInput.contains(normalizedBandari, ignoreCase = true) || cleanInput.contains(item.bandari, ignoreCase = true)) {
                        detected.add(item)
                        outputText = outputText.replace(item.bandari, item.persian)
                    }
                }

                val explanation = if (detected.isNotEmpty()) {
                    "کلمات بندری شناسایی‌شده: " + detected.joinToString("، ") { "${it.bandari} = ${it.persian}" }
                } else {
                    "معنی کلمه در فارسی: «$outputText»"
                }

                return TranslationResult(
                    sourceText = cleanInput,
                    translatedText = outputText,
                    detectedWords = detected,
                    explanation = explanation,
                    direction = effectiveDirection
                )
            }
            else -> {
                return TranslationResult(cleanInput, cleanInput, emptyList(), "", effectiveDirection)
            }
        }
    }

    private fun isLikelyBandari(text: String): Boolean {
        val bandariMarkers = listOf("خَری", "دِز", "شیکَم", "خُفتَن", "پَرتَک", "چوک", "مَه", "نادونُم", "اَرُم", "خاش", "خُبَه", "اَبی", "سِیل", "هُندَن", "تَوا", "مُگُت", "چِش")
        val clean = normalizePersian(text)
        return bandariMarkers.any { clean.contains(normalizePersian(it)) }
    }

    private fun normalizePersian(text: String): String {
        return text
            .replace("َ", "")
            .replace("ُ", "")
            .replace("ِ", "")
            .replace("ّ", "")
            .replace("ْ", "")
            .replace("ي", "ی")
            .replace("ك", "ک")
    }
}

package com.paoneking.nepallipikeyboard.keyboard.internal

import com.paoneking.nepallipikeyboard.keyboard.KeyboardElement

enum class ShiftMode(@JvmField val element: KeyboardElement) {
    UNSHIFT(KeyboardElement.ALPHABET),
    MANUAL(KeyboardElement.ALPHABET_MANUAL_SHIFTED),
    AUTOMATIC(KeyboardElement.ALPHABET_AUTOMATIC_SHIFTED),
    LOCKED(KeyboardElement.ALPHABET_SHIFT_LOCKED),
}

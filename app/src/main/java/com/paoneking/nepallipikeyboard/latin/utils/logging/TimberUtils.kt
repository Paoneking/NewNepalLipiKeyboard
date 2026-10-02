package com.paoneking.nepallipikeyboard.latin.utils.logging

import timber.log.Timber

object TimberUtils {
    @JvmStatic
    fun init(show: Boolean) {
        if (show) {
            Timber.plant(LineNumberDebugTree() as Timber.Tree)
        } else {
            Timber.plant(CrashReportingTree())
        }
    }
}

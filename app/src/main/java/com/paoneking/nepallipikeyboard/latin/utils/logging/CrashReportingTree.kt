package com.paoneking.nepallipikeyboard.latin.utils.logging

import android.util.Log
import androidx.annotation.NonNull
import timber.log.Timber


class CrashReportingTree : Timber.Tree() {
    protected override fun log(
        priority: Int,
        tag: String?,
        @NonNull message: String,
        t: Throwable?
    ) {
        if (priority == Log.VERBOSE || priority == Log.DEBUG) {
            return
        }
        FakeCrashLibrary.Companion.log(priority, tag, message)
        if (t != null) {
            if (priority == Log.ERROR) {
                FakeCrashLibrary.Companion.logError(t)
            } else if (priority == Log.WARN) {
                FakeCrashLibrary.Companion.logWarning(t)
            }
        }
    }
}

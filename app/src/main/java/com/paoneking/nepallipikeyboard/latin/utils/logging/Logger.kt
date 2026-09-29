package com.paoneking.nepallipikeyboard.latin.utils.logging

import timber.log.Timber

object Logger {
    const val ENCODING = "UTF-8"

    @JvmStatic
    @JvmOverloads
    fun d(message: String?, t: Throwable? = null) {
        Timber.d(t, message?.replace("%".toRegex(), "%%"), null)
    }

    @JvmStatic
    @JvmOverloads
    fun d(tag: String, message: String?, t: Throwable? = null) {
        Timber.tag(tag).d(t, message?.replace("%".toRegex(), "%%"), null)
    }

    @JvmStatic
    @JvmOverloads
    fun e(message: String?, t: Throwable? =  null) {
        Timber.e(t, message?.replace("%".toRegex(), "%%"), null)
    }

    @JvmStatic
    @JvmOverloads
    fun e(tag: String, message: String?, t: Throwable? = null) {
        Timber.tag(tag).e(t, message?.replace("%".toRegex(), "%%"), null)
    }

    @JvmStatic
    @JvmOverloads
    fun i(message: String?, t: Throwable? = null) {
        Timber.i(t, message?.replace("%".toRegex(), "%%"), null)
    }

    @JvmStatic
    @JvmOverloads
    fun i(tag: String, message: String?, t: Throwable? = null) {
        Timber.tag(tag).i(t, message?.replace("%".toRegex(), "%%"), null)
    }

    @JvmStatic
    @JvmOverloads
    fun w(message: String?, t: Throwable? = null) {
        Timber.w(t, message?.replace("%".toRegex(), "%%"), null)
    }

    @JvmStatic
    @JvmOverloads
    fun w(tag: String, message: String?, t: Throwable? = null) {
        Timber.tag(tag).w(t, message?.replace("%".toRegex(), "%%"), null)
    }

    @JvmStatic
    @JvmOverloads
    fun v(message: String?, t: Throwable? = null) {
        Timber.v(t, message?.replace("%".toRegex(), "%%"), null)
    }

    @JvmStatic
    @JvmOverloads
    fun v(tag: String, message: String?, t: Throwable? = null) {
        Timber.tag(tag).v(t, message?.replace("%".toRegex(), "%%"), null)
    }

    @JvmStatic
    @JvmOverloads
    fun wtf(message: String?, t: Throwable? = null) {
        Timber.wtf(t, message?.replace("%".toRegex(), "%%"), null)
    }

    @JvmStatic
    @JvmOverloads
    fun wtf(tag: String, message: String?, t: Throwable? = null) {
        Timber.tag(tag).wtf(t, message?.replace("%".toRegex(), "%%"), null)
    }
}

package com.paoneking.nepallipikeyboard.latin.utils.logging

import com.paoneking.nepallipikeyboard.latin.utils.Log
import timber.log.Timber
import kotlin.jvm.java

class LineNumberDebugTree : Timber.DebugTree() {

    private val fqcnIgnore = listOf(
        Timber::class.java.name,
        Timber.Forest::class.java.name,
        Timber.Tree::class.java.name,
        Timber.DebugTree::class.java.name,
        LineNumberDebugTree::class.java.name,
        Logger::class.java.name,
        Log::class.java.name,
    )

    override fun log(
        priority: Int, tag: String?, message: String, t: Throwable?
    ) {
        var newTag = tag ?: ""
        val tags = Throwable().stackTrace
            .first { it.className !in fqcnIgnore }
            .let(::createStackElementTag)
        if (newTag.contains("Logger.kt") or newTag.contains("Log.kt")) {
            newTag = tags.split(" => ").first() ?: ""
        }
        super.log(priority, newTag, "${if (tags.contains(newTag)) "" else "$newTag::"}$tags::$message", t)
    }

    override fun createStackElementTag(element: StackTraceElement): String {
        var mName = element.methodName
        if (mName.startsWith("lambda$")) {
            val frames = mName.split("\\$").toTypedArray()
            if (frames.size > 2) {
                mName = frames[0] + "$" + frames[1]
            }
        }

        if (mName.contains("invokeSuspend")) {
            mName = element.className
            val frames = mName.split("$")
            if (frames.size > 2) {
                mName = frames[1] + "$" + frames[2]
            }
        }

        val methodName = mName
        return String.format("%s:%s[$methodName]", element.fileName, element.lineNumber)
    }
}

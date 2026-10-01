package com.paoneking.nepallipikeyboard

import androidx.test.core.app.ApplicationProvider
import com.paoneking.nepallipikeyboard.latin.App
import com.paoneking.nepallipikeyboard.latin.BuildConfig
import com.paoneking.nepallipikeyboard.latin.common.Links
import com.paoneking.nepallipikeyboard.latin.common.LocaleUtils.constructLocale
import com.paoneking.nepallipikeyboard.latin.utils.getKnownDictionariesForLocale
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import java.io.File
import java.net.HttpURLConnection
import java.net.URL
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

@RunWith(RobolectricTestRunner::class)
class XLinkTest { // Without the X, SubtypeTests fail with ClassCastException. WTF?
    @Test fun knownDictionaries() {
        if (BuildConfig.BUILD_TYPE == "runTests") return // don't spam requests to Codeberg on every PR update
        val context = ApplicationProvider.getApplicationContext<App>()
        val urls = mutableSetOf<String>()
        context.assets.open("dictionaries_in_dict_repo.csv").reader().readLines().forEach { line ->
            getKnownDictionariesForLocale(line.split(",")[1].constructLocale(), context).forEach {
                urls.add(it.second)
            }
        }
        // can't check everything at once, this will trigger some rate limit
        val typeToCheck = listOf("/dictionaries_experimental/", "/emoji_cldr_signal_dictionaries/", "/dictionaries/").random()
        urls.forEach {
            if (it.contains(typeToCheck))
            checkLink(it)
        }
    }

    @Test fun readmeLinks() {
        val file = File("../README.md")
        val linkRegex = "(?:https?:\\/\\/.)?(?:www\\.)?[-a-zA-Z0-9@%._\\+~#=]{2,256}\\.[a-z]{2,6}\\b(?:[-a-zA-Z0-9@:%_\\+.~#?&\\/\\/=]*)".toRegex()
        val links = linkRegex.findAll(file.readText())
        links.forEach {
            if (it.value.contains("heli", true))
                checkLink(it.value.trim('.'))
        }
    }

    @Test fun layoutsLinks() {
        val file = File("../layouts.md")
        val linkRegex = "(?:https?:\\/\\/.)?(?:www\\.)?[-a-zA-Z0-9@%._\\+~#=]{2,256}\\.[a-z]{2,6}\\b(?:[-a-zA-Z0-9@:%_\\+.~#?&\\/\\/=]*)".toRegex()
        val links = linkRegex.findAll(file.readText())
        links.forEach {
            if (it.value.contains("heli", true))
                checkLink(it.value)
        }
    }

    @Test fun layoutsLinksInternal() {
        // Resolved against the local checkout, not Links.GITHUB: this is a fork, so its own
        // source paths do not exist in the upstream repository. Checking the working tree
        // tests what the links are actually for -- that they point at files that exist --
        // and does it without a network round trip.
        val file = File("../layouts.md")
        val internalLinkRegex = "app/src/\\b(?:[-a-zA-Z0-9@:%_\\+.~#?&\\/\\/=]*)".toRegex()
        internalLinkRegex.findAll(file.readText()).forEach { match ->
            val path = match.value.substringBefore('#')   // strip #L109 line anchors
            assertTrue(File("../$path").exists(), "layouts.md links to a missing file: $path")
        }
    }

    @Test fun otherLinks() {
        listOf(Links.LICENSE, Links.LAYOUT_WIKI_URL, Links.WIKI_URL, Links.CUSTOM_LAYOUTS, Links.CUSTOM_COLORS).forEach {
            checkLink(it)
        }
    }

    private fun checkLink(link: String) {
        if (link.contains("wiki/"))
            return checkWikiLink(link)
        val url = URL(link)
        val connection = url.openConnection() as HttpURLConnection
        connection.requestMethod = "HEAD"
        if (connection.responseCode != 200)
            println("error checking $link")
        assertEquals(200, connection.responseCode)
    }

    private fun checkWikiLink(link: String) {
        val url = URL(link)
        val connection = url.openConnection() as HttpURLConnection
        if (connection.responseCode != 200)
            println("error checking $link")
        assertEquals(200, connection.responseCode)
        val text = connection.getInputStream().reader().readText()
        if ("Create new page" in text)
            println("error checking wiki $link")
        assert("Create new page" !in text)
    }
}

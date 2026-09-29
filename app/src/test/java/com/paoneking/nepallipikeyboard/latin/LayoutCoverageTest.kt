// SPDX-License-Identifier: GPL-3.0-only
package com.paoneking.nepallipikeyboard.latin

import java.io.File
import kotlin.test.Test
import kotlin.test.assertTrue

/**
 * Every letter of each script must be typeable - as a key, or via long-press.
 *
 * Nepal Bhasa shipped with its six murmured letters (NGHA NYHA NHA MHA RHA LHA) unreachable,
 * which are exactly the letters that distinguish it from Devanagari. new.txt already defined
 * their matra popups, so the popup file assumed keys the layout never provided.
 *
 * Parsed with a regex rather than org.json, which is stubbed in plain unit tests.
 */
class LayoutCoverageTest {
    private val assets = File("src/main/assets")

    /** decodes the \\uXXXX escapes some labels are stored with, leaves literal text alone */
    private fun unescape(s: String): String =
        Regex("""\\u([0-9a-fA-F]{4})""").replace(s) { it.groupValues[1].toInt(16).toChar().toString() }

    private fun layoutLabels(name: String): Set<String> {
        val text = File(assets, "layouts/main/$name.json").readText()
        return Regex(""""(?:default|manualOrLocked)":\s*\{\s*"label":\s*"((?:[^"\\]|\\.)*)"""")
            .findAll(text).map { unescape(it.groupValues[1]) }.toSet()
    }

    private fun popupLines(name: String) =
        File(assets, "locale_key_texts/$name.txt").readLines()
            .filter { it.isNotBlank() && !it.startsWith("[") && !it.startsWith("punctuation") }

    private fun popupTargets(name: String): Set<String> =
        popupLines(name)
            .flatMap { it.trim().split(" ").drop(1) }
            .filterNot { it.startsWith("!") }
            .map { it.replace("\\", "") }
            .toSet()

    private fun reachable(layout: String, popups: String) = layoutLabels(layout) + popupTargets(popups)

    private fun cp(code: Int) = String(Character.toChars(code))

    @Test fun `every nepali letter is reachable`() {
        val r = reachable("nepali_traditional", "ne")
        val need = ("क ख ग घ ङ च छ ज झ ञ ट ठ ड ढ ण त थ द ध न प फ ब भ म य र ल व श ष स ह " +
                    "अ आ इ ई उ ऊ ऋ ए ऐ ओ औ").split(" ")
        val missing = need.filterNot { it in r }
        assertTrue(missing.isEmpty(), "unreachable in nepali_traditional: $missing")
    }

    @Test fun `every nepal bhasa consonant is reachable`() {
        val r = reachable("nepalbhasa_traditional", "new")
        val missing = (0x1140E..0x11434).map { cp(it) }.filterNot { it in r }
        assertTrue(missing.isEmpty(),
            "unreachable Newa consonants: " + missing.map { "U+%04X".format(it.codePointAt(0)) })
    }

    @Test fun `the murmured letters unique to nepal bhasa are reachable`() {
        val r = reachable("nepalbhasa_traditional", "new")
        // NGHA NYHA NHA MHA RHA LHA - absent from Devanagari, so no other way to type them
        listOf(0x11413, 0x11419, 0x11424, 0x1142A, 0x1142D, 0x1142F).forEach {
            assertTrue(cp(it) in r, "U+%04X is not reachable".format(it))
        }
    }

    @Test fun `nepal bhasa gya conjunct is reachable, as in nepali`() {
        // new.txt built gya from NNA instead of NYA, so its matra popups hung off a conjunct
        // nothing produced. Nepali reaches gya as a popup of tta; Nepal Bhasa now matches.
        val gya = cp(0x11416) + cp(0x11442) + cp(0x11418)   // JA + VIRAMA + NYA
        assertTrue(gya in reachable("nepalbhasa_traditional", "new"), "gya conjunct is unreachable")
    }

    @Test fun `nepal bhasa shift row mirrors nepali`() {
        // Every shifted position corresponded to Nepali except the first, which held
        // HA+VIRAMA+NA where Nepali has tta - stranding the gya and nya popups new.txt
        // defined for TA+VIRAMA+TA.
        val tta = cp(0x1141F) + cp(0x11442) + cp(0x1141F)   // TA + VIRAMA + TA
        assertTrue(tta in layoutLabels("nepalbhasa_traditional"), "shift row is missing tta")
    }
}

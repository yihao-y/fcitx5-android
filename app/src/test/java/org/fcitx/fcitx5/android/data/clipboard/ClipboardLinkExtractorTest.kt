/*
 * SPDX-License-Identifier: LGPL-2.1-or-later
 * SPDX-FileCopyrightText: Copyright 2026 Fcitx5 for Android Contributors
 */
package org.fcitx.fcitx5.android.data.clipboard

import org.junit.Assert.assertEquals
import org.junit.Test

class ClipboardLinkExtractorTest {
    @Test
    fun extractsDistinctLinks() {
        assertEquals(
            listOf("https://a.com/path", "https://b.org"),
            ClipboardLinkExtractor.extract("https://a.com/path 和 https://b.org；再看 https://a.com/path")
        )
        assertEquals(
            listOf("example.com", "www.example.org/x"),
            ClipboardLinkExtractor.extract("访问 example.com 和 www.example.org/x")
        )
    }

    @Test
    fun preservesUrlSyntaxAndStripsSentencePunctuation() {
        assertEquals(
            listOf("https://a.com/a_(b)?x=1&y=2"),
            ClipboardLinkExtractor.extract("(https://a.com/a_(b)?x=1&y=2)。")
        )
        assertEquals(
            listOf("https://example.com/report"),
            ClipboardLinkExtractor.extract("资料在 https://example.com/report，请查看。")
        )
    }

    @Test
    fun avoidsInvalidOrRedundantSuggestions() {
        assertEquals(emptyList<String>(), ClipboardLinkExtractor.extract("邮箱：abc@example.com"))
        assertEquals(emptyList<String>(), ClipboardLinkExtractor.extract("没有网址的普通文字"))
        assertEquals(emptyList<String>(), ClipboardLinkExtractor.extract(" https://a.com "))
        assertEquals(emptyList<String>(), ClipboardLinkExtractor.extract(""))
    }

    @Test
    fun boundsWorkForKeyboardResponsiveness() {
        assertEquals(
            listOf("a.com", "b.com"),
            ClipboardLinkExtractor.extract("a.com b.com c.com", limit = 2)
        )
        assertEquals(emptyList<String>(),
            ClipboardLinkExtractor.extract("a".repeat(20_000) + " https://example.com"))
        assertEquals(emptyList<String>(), ClipboardLinkExtractor.extract("a.com", limit = 0))
    }
}

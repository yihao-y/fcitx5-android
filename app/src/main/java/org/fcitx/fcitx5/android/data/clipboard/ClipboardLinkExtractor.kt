/*
 * SPDX-License-Identifier: LGPL-2.1-or-later
 * SPDX-FileCopyrightText: Copyright 2026 Fcitx5 for Android Contributors
 */
package org.fcitx.fcitx5.android.data.clipboard

/** Extract distinct URLs for temporary clipboard suggestions without modifying clipboard history. */
object ClipboardLinkExtractor {
    private const val MAX_SCANNABLE_CHARS = 16_384
    // Exclude email addresses, but accept links with or without http(s):// and www.
    private val urlPattern = Regex(
        """(?i)(?<![\p{L}\p{N}_@])(?:https?://)?(?:[\p{L}\p{N}](?:[\p{L}\p{N}-]*[\p{L}\p{N}])?\.)+[\p{L}]{2,}(?::\d{1,5})?(?:[/?#][^\s<>"'“”‘’，。；！？、]*)?"""
    )

    private val trailingPunctuation = ".,;:!?，。；：！？、）】》」』)]}"

    fun extract(text: String, limit: Int = 8): List<String> {
        // Never block the keyboard's UI thread on an unusually large copied document.
        if (limit <= 0 || text.length > MAX_SCANNABLE_CHARS) return emptyList()
        val original = text.trim()
        return urlPattern.findAll(text)
            .map { it.value.stripTrailingPunctuation() }
            .filter { it.isNotEmpty() && it != original }
            .distinct()
            .take(limit)
            .toList()
    }

    private fun String.stripTrailingPunctuation(): String {
        var end = length
        while (end > 0 && this[end - 1] in trailingPunctuation) {
            val last = this[end - 1]
            if (last == ')' && substring(0, end).count { it == ')' } <= substring(0, end).count { it == '(' }) break
            if (last == ']' && substring(0, end).count { it == ']' } <= substring(0, end).count { it == '[' }) break
            end--
        }
        return substring(0, end)
    }
}

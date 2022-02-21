package com.stark.emoji_status_app

import android.content.Context
import android.text.InputFilter
import android.text.Spanned
import android.widget.Toast
import java.util.regex.Pattern

class EmojiFilter(
    private val context: Context,
) : InputFilter {

    private val regex = "([\\u1f60\\u20a0-\\u32ff\\ud83c\\udc00-\\ud83d\\udeff\\udbb9\\udce5-\\udbb9\\udcee])"

    override fun filter(
        source: CharSequence?,
        p1: Int,
        p2: Int,
        p3: Spanned?,
        p4: Int,
        p5: Int,
    ): CharSequence {
        // Some emojis is excluded using this way, will fix it soon.
        if (source == null || source.isBlank()) return ""
        val pattern = Pattern.compile(regex).matcher(source)
        if (!pattern.find()) {
            Toast.makeText(context,
                "Please choose valid emoji",
                Toast.LENGTH_SHORT)
                .show()
            return ""
        }

        // if pass through all conditions, means a valid emoji.
        return source
    }
}
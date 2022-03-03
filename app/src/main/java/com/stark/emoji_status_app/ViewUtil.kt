package com.stark.emoji_status_app

import android.content.Context
import android.widget.Toast


fun Context.createToast(
    message: String,
    length: Int
) = Toast.makeText(this, message, length)
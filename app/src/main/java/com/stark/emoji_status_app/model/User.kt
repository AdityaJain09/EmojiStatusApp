package com.stark.emoji_status_app.model

import com.google.firebase.Timestamp


// Make sure user properties name are same as document created in firebase.
data class User (
    val displayName: String = "",
    val emojis: String = "",
    val createdDate: Timestamp = Timestamp.now()
)
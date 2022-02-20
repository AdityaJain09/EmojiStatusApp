package com.stark.emoji_status_app.model


// Make sure user properties name are same as document created in firebase.
data class User (
    val displayName: String = "",
    val emojis: String = ""
)
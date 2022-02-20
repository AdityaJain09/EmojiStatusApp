package com.stark.emoji_status_app

object RandomColorUtil {

    private val profileBackgroundColors = listOf(
        R.color.cadmium_orange,
        R.color.blue_damselfly,
        R.color.choclate_rush,
        R.color.cobalt_night,
        R.color.wisteria_yellow,
        R.color.temptatious_tangerine
    )

    fun getRandomColor(): Int {
        return profileBackgroundColors.shuffled().random()
    }
}
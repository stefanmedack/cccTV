package de.stefanmedack.ccctv.util

val SIXTEEN_BY_NINE_RATIO = 16 / 9F
val FOUR_BY_THREE_RATIO = 4 / 3F

fun switchAspectRatio(width: Int, height: Int) = (height *
        if (Math.abs(width / height.toFloat() - SIXTEEN_BY_NINE_RATIO) > 0.01)
            SIXTEEN_BY_NINE_RATIO
        else
            FOUR_BY_THREE_RATIO
        ).toInt()
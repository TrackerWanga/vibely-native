package com.megan.music.util

fun formatCount(count: Long): String = when {
    count >= 1_000_000 -> "${"%.1f".format(count / 1_000_000f)}M"
    count >= 1_000 -> "${count / 1_000}K"
    else -> count.toString()
}

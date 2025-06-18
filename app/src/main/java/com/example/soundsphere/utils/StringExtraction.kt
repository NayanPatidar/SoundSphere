package com.example.soundsphere.utils

private const val JIOSAAVN_URL_PATTERN = "https://www.jiosaavn.com/(s/)?\\w*"
private val JIOSAAVN_REGEX = Regex(JIOSAAVN_URL_PATTERN)

fun getHref(url: String, type: String): String {
    val newPath = url.replace(JIOSAAVN_REGEX, type)
    return "/$newPath"
}

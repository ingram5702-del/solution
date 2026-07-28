package com.solutionwin.app

import android.content.Context
import android.webkit.URLUtil

object WebUrlStore {
    private const val PREFS_NAME = "solutionwin_web_url"
    private const val KEY_CACHED_URL = "cached_url"

    fun getCachedUrl(context: Context): String? = context
        .getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        .getString(KEY_CACHED_URL, null)
        ?.trim()
        ?.takeIf(::isWebUrl)

    fun saveUrl(context: Context, url: String) {
        val normalized = url.trim()
        if (!isWebUrl(normalized)) return
        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            .edit()
            .putString(KEY_CACHED_URL, normalized)
            .apply()
    }

    fun isWebUrl(url: String): Boolean =
        URLUtil.isValidUrl(url) &&
            (url.startsWith("https://", ignoreCase = true) || url.startsWith("http://", ignoreCase = true))
}

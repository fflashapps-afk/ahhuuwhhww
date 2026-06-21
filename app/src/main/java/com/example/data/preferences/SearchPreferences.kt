package com.example.data.preferences

import android.content.Context
import android.content.SharedPreferences

class SearchPreferences(context: Context) {
    private val prefs: SharedPreferences = context.getSharedPreferences("cll_shrpv_prefs", Context.MODE_PRIVATE)

    companion object {
        private const val KEY_THEME = "prefs_theme" // "system", "light", "dark"
        private const val KEY_LANGUAGE = "prefs_language" // "uz", "en", "ru"
        private const val KEY_SAFE_SEARCH = "prefs_safe_search"
        private const val KEY_GRID_LAYOUT = "prefs_grid_layout"
        private const val KEY_DEFAULT_ENGINE = "prefs_default_engine" // "google", etc.
    }

    var theme: String
        get() = prefs.getString(KEY_THEME, "system") ?: "system"
        set(value) = prefs.edit().putString(KEY_THEME, value).apply()

    var language: String
        get() = prefs.getString(KEY_LANGUAGE, "uz") ?: "uz"
        set(value) = prefs.edit().putString(KEY_LANGUAGE, value).apply()

    var isSafeSearch: Boolean
        get() = prefs.getBoolean(KEY_SAFE_SEARCH, true)
        set(value) = prefs.edit().putBoolean(KEY_SAFE_SEARCH, value).apply()

    var isGridLayout: Boolean
        get() = prefs.getBoolean(KEY_GRID_LAYOUT, false)
        set(value) = prefs.edit().putBoolean(KEY_GRID_LAYOUT, value).apply()

    var defaultEngineId: String
        get() = prefs.getString(KEY_DEFAULT_ENGINE, "google") ?: "google"
        set(value) = prefs.edit().putString(KEY_DEFAULT_ENGINE, value).apply()
}

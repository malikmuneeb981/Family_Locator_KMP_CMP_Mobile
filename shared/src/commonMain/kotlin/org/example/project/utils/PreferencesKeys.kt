package org.example.project.utils

import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey

object PreferencesKeys {
    const val USER_NAME = "UserName"
    const val IS_FIRST_TIME = "isFirstTime"
    const val LANGUAGE_SELECTED = "languageSelected"
    const val REELS_MUTE = "REELS_MUTE"
    const val APP_THEME = "AppTheme"
    const val OUTSIDE_APP_DOWNLOADER = "OUTSIDE_APP_DOWNLOADER"

    // Backward-compatible alias
    const val UserName = USER_NAME

    const val User_Object = "User_Object"

    // Typed Keys
    val KEY_USER_NAME = stringPreferencesKey(USER_NAME)
    val KEY_IS_FIRST_TIME = booleanPreferencesKey(IS_FIRST_TIME)
    val KEY_LANGUAGE_SELECTED = stringPreferencesKey(LANGUAGE_SELECTED)
    val KEY_REELS_MUTE = booleanPreferencesKey(REELS_MUTE)
    val KEY_APP_THEME = stringPreferencesKey(APP_THEME)
    val KEY_OUTSIDE_APP_DOWNLOADER = booleanPreferencesKey(OUTSIDE_APP_DOWNLOADER)
}
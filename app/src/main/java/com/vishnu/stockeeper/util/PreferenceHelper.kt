package com.vishnu.stockeeper.util

import android.content.Context
import android.content.SharedPreferences

class PreferenceHelper {

    companion object {
        private fun preferences(context: Context): SharedPreferences =
            context.getSharedPreferences("default", 0)

        fun setMessage(context: Context, key: String, message: String) {
            preferences(context).edit().putString(key, message).apply()
        }

        fun getMessage(context: Context, key: String): String =
            preferences(context).getString(key, "No message added to shared pref yet") ?: ""

        fun setBoolean(context: Context, key: String, message: Boolean) {
            preferences(context).edit().putBoolean(key, message).apply()
        }

        fun getBoolean(context: Context, key: String): Boolean =
            preferences(context).getBoolean(key, false)
    }
}

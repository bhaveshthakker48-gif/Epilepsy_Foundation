package com.india.epilepsyfoundation.utils

import android.content.Context
import android.content.SharedPreferences

object SharedPreference {

    private const val PREF_NAME = "EpilepsyAppPrefs"
    private lateinit var prefs: SharedPreferences

    fun init(context: Context) {
        prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
    }

    fun set(key: String, value: String) {
        prefs.edit().putString(key, value).apply()
    }

    fun get(key: String): String? {
        return prefs.getString(key, null)
    }

    fun clear(key: String) {
        prefs.edit().remove(key).apply()
    }

    fun clearAll() {
        prefs.edit().clear().apply()
    }

    fun setBoolean(key: String, value: Boolean) {
        prefs.edit().putBoolean(key, value).apply()
    }

    fun getBoolean(key: String, default: Boolean = false): Boolean {
        return prefs.getBoolean(key, default)
    }

    fun remove(key: String) {
        prefs.edit().remove(key).apply()
    }

    fun setPrivacyAccepted(value: Boolean) {
        setBoolean("privacy_accepted", value)
    }

    fun isPrivacyAccepted(): Boolean {
        return getBoolean("privacy_accepted", false)
    }

}

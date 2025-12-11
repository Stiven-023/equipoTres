package com.univalle.equipotres.utils

import android.content.Context
import android.content.SharedPreferences

class SessionManager(context: Context) {

    private val prefs: SharedPreferences = context.getSharedPreferences(
        PREFS_NAME,
        Context.MODE_PRIVATE
    )

    companion object {
        private const val PREFS_NAME = "inventory_session"
        private const val KEY_IS_LOGGED_IN = "is_logged_in"
    }

    fun isLoggedIn(): Boolean {
        return prefs.getBoolean(KEY_IS_LOGGED_IN, false)
    }

    fun setLoggedIn(isLoggedIn: Boolean) {
        prefs.edit().putBoolean(KEY_IS_LOGGED_IN, isLoggedIn).apply()
    }

    fun clearSession() {
        prefs.edit().clear().apply()
    }


    fun setOpenedFromWidget(value: Boolean) {
        prefs.edit().putBoolean("opened_from_widget", value).apply()
    }

    fun wasOpenedFromWidget(): Boolean {
        return prefs.getBoolean("opened_from_widget", false)
    }

}
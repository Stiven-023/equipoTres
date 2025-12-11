package com.univalle.equipotres.utils

/*
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
}
 */

import android.content.Context
import android.content.SharedPreferences
import com.univalle.equipotres.model.User
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SessionManager @Inject constructor(
    @ApplicationContext context: Context
) {
    private val prefs: SharedPreferences =
        context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)

    companion object {
        private const val PREF_NAME = "inventory_session"
        private const val KEY_USER_ID = "user_id"
        private const val KEY_USER_EMAIL = "user_email"
        private const val KEY_IS_LOGGED_IN = "is_logged_in"
    }

    fun saveUserSession(user: User) {
        prefs.edit().apply {
            putString(KEY_USER_ID, user.uid)
            putString(KEY_USER_EMAIL, user.email)
            putBoolean(KEY_IS_LOGGED_IN, true)
            apply()
        }
    }

    fun getUserSession(): User? {
        val isLoggedIn = prefs.getBoolean(KEY_IS_LOGGED_IN, false)
        return if (isLoggedIn) {
            User(
                uid = prefs.getString(KEY_USER_ID, "") ?: "",
                email = prefs.getString(KEY_USER_EMAIL, "") ?: ""
            )
        } else {
            null
        }
    }

    fun clearSession() {
        prefs.edit().clear().apply()
    }

    fun isLoggedIn(): Boolean {
        return prefs.getBoolean(KEY_IS_LOGGED_IN, false)
    }
}
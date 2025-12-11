package com.univalle.equipotres.utils

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
        private const val KEY_OPENED_FROM_WIDGET = "opened_from_widget"
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

    // Funciones integradas desde la rama develop
    fun setOpenedFromWidget(value: Boolean) {
        prefs.edit().putBoolean(KEY_OPENED_FROM_WIDGET, value).apply()
    }

    fun wasOpenedFromWidget(): Boolean {
        return prefs.getBoolean(KEY_OPENED_FROM_WIDGET, false)
    }
}
package com.example.taskmaster.viewmodel.sharedPreferences

import android.content.Context
import androidx.core.content.edit

object Prefs {
    private const val FILE = "taskmaster_prefs"
    private const val KEY_EMAIL = "email"
    private const val KEY_PASSWORD = "password"
    private const val KEY_TOKEN = "token"

    fun saveAll(context: Context, email: String, password: String, token: String) {
        context.getSharedPreferences(FILE, Context.MODE_PRIVATE)
            .edit {
                putString(KEY_EMAIL, email)
                    .putString(KEY_PASSWORD, password)
                    .putString(KEY_TOKEN, token)
            }
    }

    fun saveEmailAndPassword(context: Context, email: String, password: String) {
        context.getSharedPreferences(FILE, Context.MODE_PRIVATE)
            .edit {
                putString(KEY_EMAIL, email)
                    .putString(KEY_PASSWORD, password)
            }
    }

    fun loadEmail(context: Context): String? =
        context.getSharedPreferences(FILE, Context.MODE_PRIVATE).getString(KEY_EMAIL, null)

    fun loadPassword(context: Context): String? =
        context.getSharedPreferences(FILE, Context.MODE_PRIVATE).getString(KEY_PASSWORD, null)

    fun loadToken(context: Context): String? =
        context.getSharedPreferences(FILE, Context.MODE_PRIVATE).getString(KEY_TOKEN, null)

    fun clearToken(context: Context) {
        context.getSharedPreferences(FILE, Context.MODE_PRIVATE)
            .edit {
                remove(KEY_TOKEN)
            }
    }
}
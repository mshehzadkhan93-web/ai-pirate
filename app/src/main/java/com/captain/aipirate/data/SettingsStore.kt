package com.captain.aipirate.data

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "ai_pirate_settings")

/**
 * Stores the user's Gemini setup ON THE PHONE ONLY (DataStore).
 *
 * The Gemini API key is typed in the app's Settings screen. It is never
 * sent anywhere except to Google's Gemini API, and it must never be
 * pasted into any chat, file or log.
 */
class SettingsStore(private val context: Context) {

    companion object {
        private val KEY_API_KEY = stringPreferencesKey("gemini_api_key")
        private val KEY_TTS_VOICE = stringPreferencesKey("tts_voice")
        private val KEY_TTS_MODEL = stringPreferencesKey("tts_model")
        private val KEY_CHAT_MODEL = stringPreferencesKey("chat_model")
    }

    suspend fun getApiKey(): String =
        context.dataStore.data.map { it[KEY_API_KEY] ?: "" }.first()

    suspend fun setApiKey(value: String) {
        context.dataStore.edit { it[KEY_API_KEY] = value.trim() }
    }

    suspend fun getTtsVoice(): String =
        context.dataStore.data.map { it[KEY_TTS_VOICE] ?: "Aoede" }.first()

    suspend fun setTtsVoice(value: String) {
        context.dataStore.edit { it[KEY_TTS_VOICE] = value.trim() }
    }

    suspend fun getTtsModel(): String =
        context.dataStore.data.map { it[KEY_TTS_MODEL] ?: "gemini-2.5-flash-preview-tts" }.first()

    suspend fun getChatModel(): String =
        context.dataStore.data.map { it[KEY_CHAT_MODEL] ?: "gemini-2.0-flash" }.first()
}

package com.captain.aipirate.viewmodel

import android.app.Application
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.captain.aipirate.data.ChatMessage
import com.captain.aipirate.data.GeminiClient
import com.captain.aipirate.data.MemoryStore
import com.captain.aipirate.data.PirateAudioPlayer
import com.captain.aipirate.data.PirateContent
import com.captain.aipirate.data.SettingsStore
import kotlinx.coroutines.launch

class PirateViewModel(application: Application) : AndroidViewModel(application) {

    private val settings = SettingsStore(application)
    private val memory = MemoryStore(application)
    private val audioPlayer = PirateAudioPlayer()

    var apiKey by mutableStateOf("")
        private set
    var keyLoaded by mutableStateOf(false)
        private set
    var ttsVoice by mutableStateOf("Aoede")
        private set
    var messages by mutableStateOf<List<ChatMessage>>(emptyList())
        private set
    var isThinking by mutableStateOf(false)
        private set
    var isSpeaking by mutableStateOf(false)
        private set
    var error by mutableStateOf<String?>(null)
        private set

    init {
        viewModelScope.launch {
            apiKey = settings.getApiKey()
            ttsVoice = settings.getTtsVoice()
            messages = memory.load()
            if (messages.isEmpty()) {
                messages = listOf(
                    ChatMessage(
                        "assistant",
                        "Ahoy, matey! I be Captain Reef, yer AI pirate captain. " +
                            "Suno yar — kya haal hai? Ask me anything, or go to the " +
                            "Fun tab for stories, riddles and pirate shers!"
                    )
                )
            }
            keyLoaded = true
        }
    }

    fun refreshKey() {
        viewModelScope.launch {
            apiKey = settings.getApiKey()
            ttsVoice = settings.getTtsVoice()
        }
    }

    fun saveApiKey(key: String, onDone: () -> Unit) {
        viewModelScope.launch {
            settings.setApiKey(key)
            apiKey = key.trim()
            onDone()
        }
    }

    fun saveVoice(voice: String) {
        viewModelScope.launch {
            settings.setTtsVoice(voice)
            ttsVoice = voice
        }
    }

    /** Text chat. Works offline with a gentle nudge when no key is set. */
    fun sendMessage(text: String) {
        val clean = text.trim()
        if (clean.isEmpty() || isThinking) return
        val updated = messages + ChatMessage("user", clean)
        messages = updated
        viewModelScope.launch { memory.save(updated) }
        if (apiKey.isBlank()) {
            val reply = ChatMessage(
                "assistant",
                "Arrr, matey! I need me treasure map first — yar, apni Gemini API key " +
                    "Settings mein daalo, phir main tum se poori tarah baat kar sakoon ga. " +
                    "Settings tab mein key paste karo (free key: Google AI Studio se milti hai)."
            )
            messages = messages + reply
            viewModelScope.launch { memory.save(messages) }
            return
        }
        isThinking = true
        error = null
        viewModelScope.launch {
            try {
                val client = GeminiClient(apiKey)
                val model = settings.getChatModel()
                val note = memory.memoryNote(updated)
                val history = if (note.isBlank()) updated
                else listOf(ChatMessage("user", note)) + updated
                val reply = client.chat(history, model)
                messages = messages + ChatMessage("assistant", reply)
                memory.save(messages)
            } catch (e: Exception) {
                error = e.message ?: "Something went wrong, matey."
            } finally {
                isThinking = false
            }
        }
    }

    /** Voice chat: chat with Gemini, then speak the reply with Gemini TTS. */
    fun sendVoiceMessage(text: String) {
        sendMessage(text)
        // Speak the reply once it arrives.
        viewModelScope.launch {
            val before = messages.size
            var waited = 0
            while (isThinking && waited < 60) {
                kotlinx.coroutines.delay(500)
                waited++
            }
            val reply = messages.lastOrNull()
            if (reply != null && reply.role == "assistant" && messages.size > before) {
                speak(reply.content)
            }
        }
    }

    /** Natural Gemini TTS speech — never Android's robotic TTS. */
    fun speak(text: String) {
        if (apiKey.isBlank() || isSpeaking) return
        isSpeaking = true
        viewModelScope.launch {
            try {
                val client = GeminiClient(apiKey)
                val wav = client.synthesize(text.take(900), ttsVoice, settings.getTtsModel())
                audioPlayer.play(getApplication(), wav) { isSpeaking = false }
            } catch (e: Exception) {
                error = e.message ?: "Voice failed, matey."
                isSpeaking = false
            }
        }
    }

    fun stopSpeaking() {
        audioPlayer.stop()
        isSpeaking = false
    }

    /** One-off Gemini generation for stories, jokes, translation, etc. */
    fun generate(prompt: String, onResult: (String?) -> Unit) {
        if (apiKey.isBlank()) {
            onResult(null)
            return
        }
        viewModelScope.launch {
            try {
                val client = GeminiClient(apiKey)
                val model = settings.getChatModel()
                val reply = client.chat(listOf(ChatMessage("user", prompt)), model)
                onResult(reply)
            } catch (e: Exception) {
                error = e.message ?: "Generation failed, matey."
                onResult(null)
            }
        }
    }

    fun clearHistory() {
        viewModelScope.launch {
            memory.clear()
            messages = listOf(
                ChatMessage(
                    "assistant",
                    "Ahoy! Fresh page in the logbook, matey. What shall we talk about?"
                )
            )
            memory.save(messages)
        }
    }

    fun offlineJoke(seed: Int) = PirateContent.randomJoke(seed)
    fun offlineSher(seed: Int) = PirateContent.randomSher(seed)

    override fun onCleared() {
        audioPlayer.stop()
        super.onCleared()
    }
}

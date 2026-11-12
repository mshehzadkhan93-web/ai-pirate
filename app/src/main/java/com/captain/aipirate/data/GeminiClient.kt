package com.captain.aipirate.data

import android.content.Context
import android.media.MediaPlayer
import android.util.Base64
import java.io.File
import java.nio.ByteBuffer
import java.nio.ByteOrder
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONArray
import org.json.JSONObject

/**
 * The pirate's brain — Google Gemini ONLY.
 *
 * Chat model: gemini-2.0-flash. Voice: gemini-2.5-flash-preview-tts,
 * voice Aoede by default. The API key is passed in from the Settings
 * screen (typed by the user on the phone) and sent as a ?key= query
 * parameter, exactly how the Gemini API expects it.
 *
 * The captain is an ORIGINAL AI pirate character. He never claims to be
 * Johnny Depp or any movie character.
 */
class GeminiClient(private val apiKey: String) {

    private val http = OkHttpClient()

    companion object {
        const val PIRATE_PERSONA = "You are Captain Reef, an original AI pirate " +
            "captain and cheerful companion — a wise, funny old sea captain who " +
            "loves adventure, treasure, storms and good stories. You are NOT " +
            "Johnny Depp and NOT any movie character; you are your own original " +
            "pirate. Speak in playful pirate speak mixed with Roman Urdu and " +
            "English, the way a friendly Pakistani captain would talk to his " +
            "mate (words like yar, dost, bhai, kya baat hai, zabardast, " +
            "suno yar). Keep answers warm, funny and wise, usually short " +
            "unless the user asks for a long story. Never be rude or scary. " +
            "If asked who you are, say you are Captain Reef, an AI pirate " +
            "captain made for fun and friendship. Sprinkle in pirate words " +
            "(ahoy, matey, arr, treasure, seven seas) but keep it readable."
    }

    /** Sends chat history (with the pirate persona) and returns the reply. */
    suspend fun chat(
        history: List<ChatMessage>,
        model: String,
        systemPrompt: String = PIRATE_PERSONA
    ): String = withContext(Dispatchers.IO) {
        val contents = JSONArray()
        for (msg in history) {
            if (msg.content.isBlank()) continue
            val role = if (msg.role == "assistant") "model" else "user"
            contents.put(
                JSONObject()
                    .put("role", role)
                    .put("parts", JSONArray().put(JSONObject().put("text", msg.content)))
            )
        }
        val body = JSONObject()
            .put(
                "systemInstruction",
                JSONObject().put(
                    "parts",
                    JSONArray().put(JSONObject().put("text", systemPrompt))
                )
            )
            .put("contents", contents)
            .toString()
        val request = Request.Builder()
            .url(
                "https://generativelanguage.googleapis.com/v1beta/models/" +
                    "$model:generateContent?key=$apiKey"
            )
            .post(body.toRequestBody("application/json".toMediaType()))
            .build()
        val response = http.newCall(request).execute()
        val raw = response.body?.string().orEmpty()
        if (!response.isSuccessful) {
            throw Exception("Gemini error (HTTP ${response.code}): ${extractError(raw)}")
        }
        val text = StringBuilder()
        val parts = JSONObject(raw)
            .optJSONArray("candidates")
            ?.optJSONObject(0)
            ?.optJSONObject("content")
            ?.optJSONArray("parts")
        if (parts != null) {
            for (i in 0 until parts.length()) {
                text.append(parts.optJSONObject(i)?.optString("text").orEmpty())
            }
        }
        val reply = text.toString().trim()
        if (reply.isEmpty()) "Arrr, the sea went quiet on that one, matey. Try again!" else reply
    }

    /**
     * NATURAL voice output — deliberately NOT Android's built-in TTS.
     * Gemini TTS returns base64 raw PCM (16-bit, 24 kHz, mono); we wrap it
     * in a WAV header so MediaPlayer can play it. That is what makes the
     * voice sound human instead of robotic.
     */
    suspend fun synthesize(
        text: String,
        voice: String,
        model: String = "gemini-2.5-flash-preview-tts"
    ): ByteArray = withContext(Dispatchers.IO) {
        val body = JSONObject()
            .put(
                "contents",
                JSONArray().put(
                    JSONObject().put(
                        "parts",
                        JSONArray().put(JSONObject().put("text", text))
                    )
                )
            )
            .put(
                "generationConfig",
                JSONObject()
                    .put("responseModalities", JSONArray().put("AUDIO"))
                    .put(
                        "speechConfig",
                        JSONObject().put(
                            "voiceConfig",
                            JSONObject().put(
                                "prebuiltVoiceConfig",
                                JSONObject().put("voiceName", voice)
                            )
                        )
                    )
            )
            .toString()
        val request = Request.Builder()
            .url(
                "https://generativelanguage.googleapis.com/v1beta/models/" +
                    "$model:generateContent?key=$apiKey"
            )
            .post(body.toRequestBody("application/json".toMediaType()))
            .build()
        val response = http.newCall(request).execute()
        val raw = response.body?.string().orEmpty()
        if (!response.isSuccessful) {
            throw Exception("Gemini voice error (HTTP ${response.code}): ${extractError(raw)}")
        }
        val data = JSONObject(raw)
            .optJSONArray("candidates")
            ?.optJSONObject(0)
            ?.optJSONObject("content")
            ?.optJSONArray("parts")
            ?.optJSONObject(0)
            ?.optJSONObject("inlineData")
            ?.optString("data")
            .orEmpty()
        if (data.isBlank()) throw Exception("Gemini voice returned no audio")
        pcmToWav(Base64.decode(data, Base64.DEFAULT), sampleRate = 24000)
    }

    private fun pcmToWav(pcm: ByteArray, sampleRate: Int): ByteArray {
        val header = ByteBuffer.allocate(44).order(ByteOrder.LITTLE_ENDIAN)
        header.put("RIFF".toByteArray(Charsets.US_ASCII))
        header.putInt(36 + pcm.size)
        header.put("WAVE".toByteArray(Charsets.US_ASCII))
        header.put("fmt ".toByteArray(Charsets.US_ASCII))
        header.putInt(16)
        header.putShort(1)
        header.putShort(1)
        header.putInt(sampleRate)
        header.putInt(sampleRate * 2)
        header.putShort(2)
        header.putShort(16)
        header.put("data".toByteArray(Charsets.US_ASCII))
        header.putInt(pcm.size)
        return header.array() + pcm
    }

    private fun extractError(raw: String): String {
        return try {
            JSONObject(raw).optJSONObject("error")?.optString("message")
                ?.takeIf { it.isNotBlank() } ?: raw.take(200)
        } catch (e: Exception) {
            raw.take(200)
        }
    }
}

/** Tiny WAV player for Gemini TTS replies. One sound at a time. */
class PirateAudioPlayer {
    private var player: MediaPlayer? = null

    fun play(context: Context, audioBytes: ByteArray, onDone: () -> Unit) {
        stop()
        val file = File(context.cacheDir, "pirate_reply.wav")
        file.writeBytes(audioBytes)
        player = MediaPlayer().apply {
            setDataSource(file.absolutePath)
            setOnCompletionListener { onDone() }
            prepare()
            start()
        }
    }

    fun stop() {
        player?.release()
        player = null
    }
}

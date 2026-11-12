package com.captain.aipirate.data

import android.content.Context
import java.io.File
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONArray
import org.json.JSONObject

/**
 * MEMORY: the captain remembers past chats, stored ONLY on the phone.
 *
 * Chat history is saved as a local JSON file in the app's private storage
 * (nothing leaves the device except the messages sent to Gemini to answer).
 * The ViewModel loads the recent history as context for every reply, and
 * the captain can refer back to earlier conversations ("last time ye
 * kaha tha..."). History is capped so the file stays small.
 */
class MemoryStore(context: Context) {

    private val file = File(context.filesDir, "pirate_chat_history.json")
    private val maxMessages = 60

    suspend fun load(): List<ChatMessage> = withContext(Dispatchers.IO) {
        if (!file.exists()) return@withContext emptyList()
        try {
            val arr = JSONArray(file.readText())
            val out = ArrayList<ChatMessage>()
            for (i in 0 until arr.length()) {
                val obj = arr.optJSONObject(i) ?: continue
                out.add(ChatMessage(obj.optString("role"), obj.optString("content")))
            }
            out
        } catch (e: Exception) {
            emptyList()
        }
    }

    suspend fun save(messages: List<ChatMessage>) = withContext(Dispatchers.IO) {
        try {
            val kept = messages.takeLast(maxMessages)
            val arr = JSONArray()
            for (msg in kept) {
                arr.put(JSONObject().put("role", msg.role).put("content", msg.content))
            }
            file.writeText(arr.toString())
        } catch (e: Exception) {
            // Memory is a bonus, never a crash.
        }
    }

    suspend fun clear() = withContext(Dispatchers.IO) {
        try {
            if (file.exists()) file.delete()
        } catch (e: Exception) {
        }
    }

    /** Short memory summary the captain sees before answering. */
    fun memoryNote(history: List<ChatMessage>): String {
        if (history.isEmpty()) return ""
        val userLines = history.filter { it.role == "user" }.takeLast(5)
        if (userLines.isEmpty()) return ""
        return "Things you remember from earlier chats with this mate: " +
            userLines.joinToString(" | ") { it.content.take(80) } +
            ". Refer back to these naturally when it fits, like a good old friend would."
    }
}

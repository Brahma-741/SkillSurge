package com.skillsurge.ai

import android.annotation.SuppressLint
import com.google.ai.client.generativeai.GenerativeModel
import com.google.ai.client.generativeai.type.generationConfig
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class Skiller {

    companion object {
        private const val MODEL_NAME = "gemini-2.0-flash"
        private const val API_KEY: String = "AIzaSyAQDv_UcUGFJTJyod1av8DHIykFX4NxWxs"
    }

    @SuppressLint("SecretInSource")
    private val model = GenerativeModel(
        modelName = MODEL_NAME,
        apiKey = API_KEY,
        generationConfig = generationConfig {
            temperature = 1f
            topK = 40
            topP = 0.95f
            maxOutputTokens = 8192
            responseMimeType = "text/plain"
        }
    )

    private val chat = model.startChat(history = emptyList()) // Start chat session

    suspend fun generateText(prompt: String): String {
        return try {
            withContext(Dispatchers.IO) {  // Run in background thread
                val response = chat.sendMessage(prompt)
                response.text ?: "No response generated"
            }
        } catch (e: Exception) {
            "Error: ${e.localizedMessage ?: "Unknown error occurred"}"
        }
    }
}

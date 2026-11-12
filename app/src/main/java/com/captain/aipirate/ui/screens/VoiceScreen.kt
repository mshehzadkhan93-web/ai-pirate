package com.captain.aipirate.ui.screens

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.speech.RecognitionListener
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import com.captain.aipirate.ui.theme.DeepSea
import com.captain.aipirate.ui.theme.FoamWhite
import com.captain.aipirate.ui.theme.MutedFoam
import com.captain.aipirate.ui.theme.PirateGold
import com.captain.aipirate.viewmodel.PirateViewModel

/**
 * Voice chat: mic input via Android SpeechRecognizer (speech-to-text only),
 * replies spoken with NATURAL Gemini TTS (gemini-3.8-flash-tts).
 * Android's robotic TextToSpeech is never used for replies.
 */
@Composable
fun VoiceScreen(viewModel: PirateViewModel) {
    val context = LocalContext.current
    var listening by remember { mutableStateOf(false) }
    var heard by remember { mutableStateOf("") }
    var status by remember { mutableStateOf("Tap the mic and talk to the Captain, matey!") }

    val permissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { granted ->
        status = if (granted) "Permission granted — tap the mic, yar!" else "Mic permission needed to talk, matey."
    }

    val recognizer = remember {
        if (SpeechRecognizer.isRecognitionAvailable(context)) {
            SpeechRecognizer.createSpeechRecognizer(context)
        } else null
    }

    DisposableEffect(recognizer) {
        val listener = object : RecognitionListener {
            override fun onReadyForSpeech(params: Bundle?) { status = "Listening… speak now, matey!" }
            override fun onBeginningOfSpeech() { status = "Listening…" }
            override fun onRmsChanged(rmsdB: Float) {}
            override fun onBufferReceived(buffer: ByteArray?) {}
            override fun onEndOfSpeech() { status = "Heard ye! Thinking…" }
            override fun onError(error: Int) {
                listening = false
                status = "Arrr, I didn't catch that (error $error). Try again, yar!"
            }
            override fun onResults(results: Bundle?) {
                listening = false
                val matches = results?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
                val text = matches?.firstOrNull().orEmpty()
                if (text.isNotBlank()) {
                    heard = text
                    status = "Ye said: \"$text\""
                    viewModel.sendVoiceMessage(text)
                } else {
                    status = "I heard nothing, matey. Try again!"
                }
            }
            override fun onPartialResults(partialResults: Bundle?) {}
            override fun onEvent(eventType: Int, params: Bundle?) {}
        }
        recognizer?.setRecognitionListener(listener)
        onDispose { recognizer?.destroy() }
    }

    fun startListening() {
        if (ContextCompat.checkSelfPermission(context, Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
            permissionLauncher.launch(Manifest.permission.RECORD_AUDIO)
            return
        }
        if (recognizer == null) {
            status = "Speech recognition is not available on this phone, matey."
            return
        }
        val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
            putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
            putExtra(RecognizerIntent.EXTRA_LANGUAGE, "en-US")
            putExtra(RecognizerIntent.EXTRA_MAX_RESULTS, 3)
        }
        listening = true
        status = "Listening…"
        recognizer.startListening(intent)
    }

    Column(
        modifier = Modifier.fillMaxSize().background(DeepSea).padding(20.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text("🏴‍☠️", fontSize = 64.sp)
        Spacer(Modifier.height(8.dp))
        Text("Talk to Captain Reef", color = FoamWhite, fontWeight = FontWeight.Bold, fontSize = 22.sp)
        Spacer(Modifier.height(6.dp))
        Text(
            "Speak, and the Captain answers in a natural human-like voice " +
                "(Gemini TTS) — no robotic voice here.",
            color = MutedFoam, fontSize = 13.sp, textAlign = TextAlign.Center
        )
        Spacer(Modifier.height(24.dp))
        IconButton(
            onClick = {
                if (listening) {
                    recognizer?.stopListening()
                    listening = false
                } else if (viewModel.isSpeaking) {
                    viewModel.stopSpeaking()
                } else {
                    startListening()
                }
            },
            modifier = Modifier.size(96.dp).background(PirateGold, CircleShape)
        ) {
            Icon(
                if (listening) Icons.Filled.Stop else Icons.Filled.Mic,
                contentDescription = if (listening) "Stop listening" else "Start talking",
                tint = DeepSea,
                modifier = Modifier.size(44.dp)
            )
        }
        Spacer(Modifier.height(20.dp))
        Text(status, color = FoamWhite, fontSize = 15.sp, textAlign = TextAlign.Center)
        if (heard.isNotBlank()) {
            Spacer(Modifier.height(8.dp))
            Text("You: $heard", color = MutedFoam, fontSize = 13.sp, textAlign = TextAlign.Center)
        }
        val lastReply = viewModel.messages.lastOrNull { it.role == "assistant" }?.content.orEmpty()
        if (lastReply.isNotBlank()) {
            Spacer(Modifier.height(12.dp))
            Text("Captain: $lastReply", color = FoamWhite, fontSize = 14.sp, textAlign = TextAlign.Center)
        }
        if (viewModel.isSpeaking) {
            Spacer(Modifier.height(12.dp))
            Button(
                onClick = { viewModel.stopSpeaking() },
                colors = ButtonDefaults.buttonColors(containerColor = PirateGold, contentColor = DeepSea)
            ) { Text("Stop speaking", fontWeight = FontWeight.Bold) }
        }
        if (viewModel.apiKey.isBlank()) {
            Spacer(Modifier.height(16.dp))
            Text(
                "⚠ Set your Gemini API key in Settings first, matey!",
                color = PirateGold, fontSize = 13.sp, textAlign = TextAlign.Center
            )
        }
    }
}

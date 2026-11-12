package com.captain.aipirate.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.captain.aipirate.ui.theme.DeepSea
import com.captain.aipirate.ui.theme.FoamWhite
import com.captain.aipirate.ui.theme.MutedFoam
import com.captain.aipirate.ui.theme.PirateGold
import com.captain.aipirate.ui.theme.SeaNavy
import com.captain.aipirate.ui.theme.SeaNavyLight
import com.captain.aipirate.viewmodel.PirateViewModel

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun SettingsScreen(viewModel: PirateViewModel) {
    var keyInput by remember { mutableStateOf("") }
    var saved by remember { mutableStateOf(false) }
    var voice by remember { mutableStateOf(viewModel.ttsVoice) }
    val voices = listOf("Aoede", "Kore", "Puck", "Charon", "Zephyr")

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(DeepSea)
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
    ) {
        Text("⚙️ Settings", color = FoamWhite, fontWeight = FontWeight.Bold, fontSize = 22.sp)
        Spacer(Modifier.height(6.dp))
        Text(
            "Your Gemini API key lives ONLY on this phone. It is never sent " +
                "anywhere except to Google's Gemini API.",
            color = MutedFoam, fontSize = 13.sp
        )
        Spacer(Modifier.height(16.dp))

        Card(
            colors = CardDefaults.cardColors(containerColor = SeaNavy),
            shape = RoundedCornerShape(14.dp)
        ) {
            Column(Modifier.padding(16.dp)) {
                Text("🔑 Gemini API Key", color = PirateGold, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                Spacer(Modifier.height(6.dp))
                Text(
                    "1. Open Google AI Studio (aistudio.google.com) in your browser.\n" +
                        "2. Tap \"Get API key\" and copy it.\n" +
                        "3. Paste it here and tap Save. Done, matey!",
                    color = FoamWhite, fontSize = 13.sp
                )
                Spacer(Modifier.height(12.dp))
                OutlinedTextField(
                    value = keyInput,
                    onValueChange = { keyInput = it; saved = false },
                    modifier = Modifier.fillMaxWidth(),
                    label = { Text("Paste Gemini API key here", color = MutedFoam) },
                    placeholder = { Text(if (viewModel.apiKey.isBlank()) "No key saved yet" else "Key saved ✓ (hidden)", color = MutedFoam) },
                    visualTransformation = PasswordVisualTransformation(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = FoamWhite,
                        unfocusedTextColor = FoamWhite,
                        focusedContainerColor = SeaNavyLight,
                        unfocusedContainerColor = SeaNavyLight,
                        focusedBorderColor = PirateGold,
                        unfocusedBorderColor = SeaNavyLight
                    ),
                    shape = RoundedCornerShape(12.dp)
                )
                Spacer(Modifier.height(10.dp))
                Button(
                    onClick = {
                        viewModel.saveApiKey(keyInput) { keyInput = ""; saved = true }
                    },
                    enabled = keyInput.isNotBlank(),
                    colors = ButtonDefaults.buttonColors(containerColor = PirateGold, contentColor = DeepSea),
                    modifier = Modifier.fillMaxWidth()
                ) { Text("Save API Key", fontWeight = FontWeight.Bold) }
                if (saved) {
                    Spacer(Modifier.height(8.dp))
                    Text("✅ Key saved on this phone. Now go chat with the Captain!", color = PirateGold, fontSize = 13.sp)
                }
                if (viewModel.apiKey.isNotBlank()) {
                    Spacer(Modifier.height(8.dp))
                    Text("Status: key is set ✓", color = FoamWhite, fontSize = 13.sp)
                }
            }
        }

        Spacer(Modifier.height(16.dp))
        Card(
            colors = CardDefaults.cardColors(containerColor = SeaNavy),
            shape = RoundedCornerShape(14.dp)
        ) {
            Column(Modifier.padding(16.dp)) {
                Text("🎙️ Voice (Gemini TTS)", color = PirateGold, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                Spacer(Modifier.height(6.dp))
                Text(
                    "Spoken replies use Gemini's natural voice — not Android's robotic voice. " +
                        "Pick a voice, matey:",
                    color = FoamWhite, fontSize = 13.sp
                )
                Spacer(Modifier.height(10.dp))
                androidx.compose.foundation.layout.FlowRow(
                    horizontalArrangement = androidx.compose.foundation.layout.Arrangement.spacedBy(8.dp),
                    verticalArrangement = androidx.compose.foundation.layout.Arrangement.spacedBy(8.dp)
                ) {
                    for (v in voices) {
                        Button(
                            onClick = { voice = v; viewModel.saveVoice(v) },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = if (voice == v) PirateGold else SeaNavyLight,
                                contentColor = if (voice == v) DeepSea else FoamWhite
                            )
                        ) { Text(v) }
                    }
                }
            }
        }

        Spacer(Modifier.height(16.dp))
        Card(
            colors = CardDefaults.cardColors(containerColor = SeaNavy),
            shape = RoundedCornerShape(14.dp)
        ) {
            Column(Modifier.padding(16.dp)) {
                Text("🧠 Memory", color = PirateGold, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                Spacer(Modifier.height(6.dp))
                Text(
                    "Captain Reef remembers your past chats on this phone and refers " +
                        "back to them. Clear it any time:",
                    color = FoamWhite, fontSize = 13.sp
                )
                Spacer(Modifier.height(10.dp))
                Button(
                    onClick = { viewModel.clearHistory() },
                    colors = ButtonDefaults.buttonColors(containerColor = SeaNavyLight, contentColor = FoamWhite),
                    modifier = Modifier.fillMaxWidth()
                ) { Text("Clear Chat Memory") }
            }
        }

        Spacer(Modifier.height(16.dp))
        Text(
            "AI Pirate v1.0 • Captain Reef is an original AI character. " +
                "He is not Johnny Depp or any movie character.",
            color = MutedFoam, fontSize = 12.sp
        )
    }
}

package com.captain.aipirate.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Send
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.captain.aipirate.data.ChatMessage
import com.captain.aipirate.ui.theme.PirateGold
import com.captain.aipirate.ui.theme.SeaNavy
import com.captain.aipirate.ui.theme.SeaNavyLight
import com.captain.aipirate.ui.theme.DeepSea
import com.captain.aipirate.ui.theme.FoamWhite
import com.captain.aipirate.ui.theme.MutedFoam
import com.captain.aipirate.viewmodel.PirateViewModel

@Composable
fun ChatScreen(viewModel: PirateViewModel, onGoSettings: () -> Unit) {
    var input by remember { mutableStateOf("") }
    val listState = rememberLazyListState()

    LaunchedEffect(viewModel.messages.size) {
        if (viewModel.messages.isNotEmpty()) {
            listState.animateScrollToItem(viewModel.messages.size - 1)
        }
    }

    Column(modifier = Modifier.fillMaxSize().background(DeepSea).padding(12.dp)) {
        // Cute pirate avatar + title header.
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .background(PirateGold, RoundedCornerShape(24.dp))
                    .padding(horizontal = 12.dp, vertical = 6.dp),
                contentAlignment = Alignment.Center
            ) {
                Text("🏴‍☠️", fontSize = 28.sp)
            }
            Spacer(Modifier.width(10.dp))
            Column {
                Text("Captain Reef", color = FoamWhite, fontWeight = FontWeight.Bold, fontSize = 20.sp)
                Text("yer AI pirate matey • Roman Urdu + English", color = MutedFoam, fontSize = 12.sp)
            }
            Spacer(Modifier.weight(1f))
            IconButton(onClick = { viewModel.clearHistory() }) {
                Icon(Icons.Filled.Delete, contentDescription = "Clear chat", tint = MutedFoam)
            }
        }

        if (viewModel.keyLoaded && viewModel.apiKey.isBlank()) {
            Spacer(Modifier.height(10.dp))
            Card(
                colors = CardDefaults.cardColors(containerColor = SeaNavyLight),
                shape = RoundedCornerShape(12.dp)
            ) {
                Row(Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Filled.Info, contentDescription = null, tint = PirateGold)
                    Spacer(Modifier.width(8.dp))
                    Column(Modifier.weight(1f)) {
                        Text(
                            "Setup needed, matey!",
                            color = FoamWhite, fontWeight = FontWeight.Bold, fontSize = 14.sp
                        )
                        Text(
                            "Paste your Gemini API key in Settings to start chatting. " +
                                "No key is stored anywhere except on your phone.",
                            color = MutedFoam, fontSize = 12.sp
                        )
                    }
                    Button(
                        onClick = onGoSettings,
                        colors = ButtonDefaults.buttonColors(containerColor = PirateGold, contentColor = DeepSea)
                    ) { Text("Settings", fontWeight = FontWeight.Bold) }
                }
            }
        }

        Spacer(Modifier.height(10.dp))
        LazyColumn(
            state = listState,
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(viewModel.messages) { msg -> ChatBubble(msg, viewModel) }
            if (viewModel.isThinking) {
                item {
                    Text("Captain Reef is thinking… ⚓", color = MutedFoam, fontSize = 13.sp)
                }
            }
        }
        viewModel.error?.let {
            Text("⚠ $it", color = Color(0xFFFF8A80), fontSize = 12.sp, modifier = Modifier.padding(vertical = 4.dp))
        }
        Spacer(Modifier.height(8.dp))
        Row(verticalAlignment = Alignment.CenterVertically) {
            OutlinedTextField(
                value = input,
                onValueChange = { input = it },
                modifier = Modifier.weight(1f),
                placeholder = { Text("Ahoy! Say something, matey…", color = MutedFoam) },
                colors = OutlinedTextFieldDefaults.colors(
                    focusedTextColor = FoamWhite,
                    unfocusedTextColor = FoamWhite,
                    focusedContainerColor = SeaNavy,
                    unfocusedContainerColor = SeaNavy,
                    focusedBorderColor = PirateGold,
                    unfocusedBorderColor = SeaNavyLight
                ),
                shape = RoundedCornerShape(24.dp),
                maxLines = 4
            )
            Spacer(Modifier.width(8.dp))
            IconButton(
                onClick = {
                    viewModel.sendMessage(input)
                    input = ""
                },
                modifier = Modifier.background(PirateGold, RoundedCornerShape(24.dp))
            ) {
                Icon(Icons.Filled.Send, contentDescription = "Send", tint = DeepSea)
            }
        }
    }
}

@Composable
private fun ChatBubble(msg: ChatMessage, viewModel: PirateViewModel) {
    val isUser = msg.role == "user"
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = if (isUser) Arrangement.End else Arrangement.Start
    ) {
        Card(
            colors = CardDefaults.cardColors(
                containerColor = if (isUser) PirateGold else SeaNavy
            ),
            shape = RoundedCornerShape(14.dp),
            modifier = Modifier.fillMaxWidth(0.85f)
        ) {
            Column(Modifier.padding(12.dp)) {
                if (!isUser) {
                    Text("🏴‍☠️ Captain Reef", color = PirateGold, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                }
                Text(
                    msg.content,
                    color = if (isUser) DeepSea else FoamWhite,
                    fontSize = 15.sp
                )
                if (!isUser && viewModel.apiKey.isNotBlank()) {
                    Row {
                        Spacer(Modifier.weight(1f))
                        IconButton(onClick = { viewModel.speak(msg.content) }) {
                            Icon(Icons.Filled.VolumeUp, contentDescription = "Hear it", tint = PirateGold)
                        }
                    }
                }
            }
        }
    }
}

package com.captain.aipirate.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.captain.aipirate.data.PirateContent
import com.captain.aipirate.ui.theme.DeepSea
import com.captain.aipirate.ui.theme.FoamWhite
import com.captain.aipirate.ui.theme.MutedFoam
import com.captain.aipirate.ui.theme.PirateGold
import com.captain.aipirate.ui.theme.SeaNavy
import com.captain.aipirate.ui.theme.SeaNavyLight
import com.captain.aipirate.viewmodel.PirateViewModel
import java.util.Calendar

/** Stories, daily riddle, jokes, shers, Captain's Log and Pirate Translator. */
@Composable
fun FunScreen(viewModel: PirateViewModel) {
    val riddle = PirateContent.todaysRiddle()
    val log = PirateContent.todaysLog()
    var showAnswer by remember { mutableStateOf(false) }
    var story by remember { mutableStateOf("") }
    var storyBusy by remember { mutableStateOf(false) }
    var joke by remember { mutableStateOf("") }
    var sher by remember { mutableStateOf("") }
    var translateInput by remember { mutableStateOf("") }
    var translation by remember { mutableStateOf("") }
    var translateBusy by remember { mutableStateOf(false) }
    var seed by remember { mutableStateOf(Calendar.getInstance().get(Calendar.DAY_OF_YEAR)) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(DeepSea)
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        Text("⭐ Pirate Fun", color = FoamWhite, fontWeight = FontWeight.Bold, fontSize = 22.sp)

        FunCard("📜 Captain's Log — today") {
            Text("“${log.quote}”", color = PirateGold, fontSize = 15.sp, fontWeight = FontWeight.Bold)
            Spacer(Modifier.height(6.dp))
            Text(log.lesson, color = FoamWhite, fontSize = 14.sp)
        }

        FunCard("🧩 Daily Riddle") {
            Text(riddle.question, color = FoamWhite, fontSize = 15.sp)
            Spacer(Modifier.height(8.dp))
            if (showAnswer) {
                Text("Answer: ${riddle.answer}", color = PirateGold, fontSize = 14.sp)
            } else {
                Button(
                    onClick = { showAnswer = true },
                    colors = ButtonDefaults.buttonColors(containerColor = PirateGold, contentColor = DeepSea)
                ) { Text("Reveal answer", fontWeight = FontWeight.Bold) }
            }
        }

        FunCard("📖 Pirate Story") {
            Text(
                "Pick a tale, and Captain Reef will spin it — in pirate speak, " +
                    "Roman Urdu + English.",
                color = FoamWhite, fontSize = 14.sp
            )
            Spacer(Modifier.height(8.dp))
            for (title in PirateContent.storyStarters) {
                Button(
                    onClick = {
                        storyBusy = true
                        story = ""
                        if (viewModel.apiKey.isBlank()) {
                            story = "Arrr, matey! \"$title\" — ek dafa ka zikr hai, saat samundar " +
                                "ke paar ek jazeera tha jo sirf aadhi raat ko dikhta tha… " +
                                "(Poori kahani ke liye Settings mein apni Gemini API key daalo, " +
                                "phir Captain tumhe lambi kahani sunayega!)"
                            storyBusy = false
                        } else {
                            viewModel.generate(
                                "Tell a fun pirate adventure story titled \"$title\" in pirate " +
                                    "speak mixed with Roman Urdu and English, about 200 words, " +
                                    "exciting and funny."
                            ) { result ->
                                story = result ?: "The sea went quiet, matey. Try again!"
                                storyBusy = false
                            }
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = SeaNavyLight, contentColor = FoamWhite),
                    modifier = Modifier.fillMaxWidth()
                ) { Text("⚓ $title") }
            }
            if (storyBusy) {
                Spacer(Modifier.height(8.dp))
                Text("Captain Reef is spinning the tale…", color = MutedFoam, fontSize = 13.sp)
            }
            if (story.isNotBlank()) {
                Spacer(Modifier.height(8.dp))
                Text(story, color = FoamWhite, fontSize = 14.sp)
            }
        }

        FunCard("😂 Jokes & Shers") {
            Button(
                onClick = { seed++; joke = viewModel.offlineJoke(seed) },
                colors = ButtonDefaults.buttonColors(containerColor = PirateGold, contentColor = DeepSea),
                modifier = Modifier.fillMaxWidth()
            ) { Text("Tell me a pirate joke", fontWeight = FontWeight.Bold) }
            if (joke.isNotBlank()) {
                Spacer(Modifier.height(8.dp))
                Text(joke, color = FoamWhite, fontSize = 14.sp)
            }
            Spacer(Modifier.height(10.dp))
            Button(
                onClick = { seed++; sher = viewModel.offlineSher(seed) },
                colors = ButtonDefaults.buttonColors(containerColor = PirateGold, contentColor = DeepSea),
                modifier = Modifier.fillMaxWidth()
            ) { Text("Sunao ek pirate sher", fontWeight = FontWeight.Bold) }
            if (sher.isNotBlank()) {
                Spacer(Modifier.height(8.dp))
                Text(sher, color = PirateGold, fontSize = 15.sp)
            }
        }

        FunCard("🏴‍☠️ Pirate Translator") {
            Text(
                "Type a normal sentence, and the Captain rewrites it in pirate speak.",
                color = FoamWhite, fontSize = 14.sp
            )
            Spacer(Modifier.height(8.dp))
            OutlinedTextField(
                value = translateInput,
                onValueChange = { translateInput = it },
                modifier = Modifier.fillMaxWidth(),
                placeholder = { Text("e.g. Hello my friend, let's go find treasure", color = MutedFoam) },
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
            Spacer(Modifier.height(8.dp))
            Button(
                onClick = {
                    if (translateInput.isBlank()) return@Button
                    translateBusy = true
                    if (viewModel.apiKey.isBlank()) {
                        translation = PirateContent.translateOffline(translateInput)
                        translateBusy = false
                    } else {
                        viewModel.generate(
                            "Rewrite this sentence in playful pirate speak mixed with a " +
                                "little Roman Urdu, keeping the meaning: \"$translateInput\""
                        ) { result ->
                            translation = result ?: PirateContent.translateOffline(translateInput)
                            translateBusy = false
                        }
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = PirateGold, contentColor = DeepSea),
                modifier = Modifier.fillMaxWidth()
            ) { Text("Translate, Captain!", fontWeight = FontWeight.Bold) }
            if (translateBusy) {
                Spacer(Modifier.height(8.dp))
                Text("Translating…", color = MutedFoam, fontSize = 13.sp)
            }
            if (translation.isNotBlank()) {
                Spacer(Modifier.height(8.dp))
                Text(translation, color = PirateGold, fontSize = 15.sp)
            }
        }
    }
}

@Composable
private fun FunCard(title: String, content: @Composable () -> Unit) {
    Card(
        colors = CardDefaults.cardColors(containerColor = SeaNavy),
        shape = RoundedCornerShape(14.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(Modifier.padding(16.dp)) {
            Text(title, color = PirateGold, fontWeight = FontWeight.Bold, fontSize = 16.sp)
            Spacer(Modifier.height(8.dp))
            content()
        }
    }
}

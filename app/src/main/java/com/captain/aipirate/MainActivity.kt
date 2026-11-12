package com.captain.aipirate

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Chat
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.captain.aipirate.ui.screens.ChatScreen
import com.captain.aipirate.ui.screens.FunScreen
import com.captain.aipirate.ui.screens.SettingsScreen
import com.captain.aipirate.ui.screens.VoiceScreen
import com.captain.aipirate.ui.theme.AiPirateTheme
import com.captain.aipirate.ui.theme.DeepSea
import com.captain.aipirate.ui.theme.FoamWhite
import com.captain.aipirate.ui.theme.MutedFoam
import com.captain.aipirate.ui.theme.PirateGold
import com.captain.aipirate.ui.theme.SeaNavy
import com.captain.aipirate.viewmodel.PirateViewModel
import kotlinx.coroutines.delay

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            AiPirateTheme {
                var showSplash by remember { mutableStateOf(true) }
                LaunchedEffect(Unit) {
                    delay(1800)
                    showSplash = false
                }
                if (showSplash) {
                    SplashScreen()
                } else {
                    PirateApp()
                }
            }
        }
    }
}

/** Splash: pirate ship sailing on the deep sea, gold title. */
@Composable
private fun SplashScreen() {
    Box(
        modifier = Modifier.fillMaxSize().background(DeepSea),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text("⛵", fontSize = 96.sp)
            Text("🏴‍☠️", fontSize = 48.sp)
            Spacer(Modifier.height(12.dp))
            Text(
                "AI PIRATE",
                color = PirateGold,
                fontWeight = FontWeight.Bold,
                fontSize = 36.sp,
                letterSpacing = 4.sp
            )
            Spacer(Modifier.height(6.dp))
            Text(
                "Captain Reef • Seven Seas of Chat",
                color = MutedFoam,
                fontSize = 14.sp,
                textAlign = TextAlign.Center
            )
            Spacer(Modifier.height(24.dp))
            Text("🌊 ~~~ 🌊 ~~~ 🌊", color = PirateGold, fontSize = 16.sp)
        }
    }
}

@Composable
private fun PirateApp() {
    val viewModel: PirateViewModel = viewModel()
    var tab by remember { mutableStateOf(0) }
    Scaffold(
        containerColor = DeepSea,
        bottomBar = {
            NavigationBar(containerColor = SeaNavy) {
                val items = listOf(
                    Triple("Chat", Icons.Filled.Chat, 0),
                    Triple("Voice", Icons.Filled.Mic, 1),
                    Triple("Fun", Icons.Filled.Star, 2),
                    Triple("Settings", Icons.Filled.Settings, 3)
                )
                for ((label, icon, index) in items) {
                    NavigationBarItem(
                        selected = tab == index,
                        onClick = {
                            tab = index
                            if (index == 3) viewModel.refreshKey()
                        },
                        icon = { Icon(icon, contentDescription = label) },
                        label = { Text(label) },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = PirateGold,
                            selectedTextColor = PirateGold,
                            unselectedIconColor = MutedFoam,
                            unselectedTextColor = MutedFoam,
                            indicatorColor = Color(0xFF14324F)
                        )
                    )
                }
            }
        }
    ) { innerPadding ->
        Box(Modifier.fillMaxSize().padding(innerPadding)) {
            when (tab) {
                0 -> ChatScreen(viewModel) { tab = 3 }
                1 -> VoiceScreen(viewModel)
                2 -> FunScreen(viewModel)
                3 -> SettingsScreen(viewModel)
            }
        }
    }
}

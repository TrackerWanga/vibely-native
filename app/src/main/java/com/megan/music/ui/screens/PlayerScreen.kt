package com.megan.music.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.SkipNext
import androidx.compose.material.icons.filled.SkipPrevious
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PlayerScreen(navController: NavController) {
    var isPlaying by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Now Playing") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier.fillMaxSize().padding(padding).padding(32.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // Album art
            Surface(
                modifier = Modifier.size(280.dp),
                shape = MaterialTheme.shapes.extraLarge,
                color = MaterialTheme.colorScheme.surfaceVariant
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Text("🎵", fontSize = 80.sp)
                }
            }

            Spacer(Modifier.height(32.dp))

            Text("Select a song to play", fontSize = 22.sp, fontWeight = FontWeight.Bold)
            Spacer(Modifier.height(4.dp))
            Text("Search or browse trending music", fontSize = 14.sp, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f))

            Spacer(Modifier.height(40.dp))

            // Progress bar
            LinearProgressIndicator(
                progress = { 0f },
                modifier = Modifier.fillMaxWidth().height(4.dp),
                color = MaterialTheme.colorScheme.primary,
                trackColor = MaterialTheme.colorScheme.surfaceVariant,
            )

            Spacer(Modifier.height(24.dp))

            // Controls
            Row(
                horizontalArrangement = Arrangement.SpaceEvenly,
                modifier = Modifier.fillMaxWidth()
            ) {
                IconButton(onClick = { }) {
                    Icon(Icons.Filled.SkipPrevious, "Previous", modifier = Modifier.size(44.dp))
                }
                FilledIconButton(
                    onClick = { isPlaying = !isPlaying },
                    modifier = Modifier.size(72.dp)
                ) {
                    Icon(Icons.Filled.PlayArrow, if (isPlaying) "Pause" else "Play", modifier = Modifier.size(36.dp))
                }
                IconButton(onClick = { }) {
                    Icon(Icons.Filled.SkipNext, "Next", modifier = Modifier.size(44.dp))
                }
            }
        }
    }
}

package com.megan.music.ui.screens

import android.app.Application
import android.content.ComponentName
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.media3.common.MediaItem
import androidx.media3.session.MediaController
import androidx.media3.session.SessionToken
import androidx.navigation.NavController
import com.google.common.util.concurrent.MoreExecutors

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PlayerScreen(navController: NavController) {
    val context = LocalContext.current
    var isPlaying by remember { mutableStateOf(false) }
    var currentTitle by remember { mutableStateOf("No track selected") }
    var currentArtist by remember { mutableStateOf("") }

    // Connect to MusicService
    LaunchedEffect(Unit) {
        try {
            val sessionToken = SessionToken(context, ComponentName(context, "com.megan.music.service.MusicService"))
            val controller = MediaController.Builder(context, sessionToken).buildAsync()
            controller.addListener(MoreExecutors.directExecutor(), object : MediaController.Listener {
                override fun onPlaybackStateChanged(state: Int) {
                    isPlaying = state == androidx.media3.common.Player.STATE_READY
                }
                override fun onMediaItemTransition(mediaItem: MediaItem?, reason: Int) {
                    currentTitle = mediaItem?.mediaMetadata?.title?.toString() ?: "Unknown"
                    currentArtist = mediaItem?.mediaMetadata?.artist?.toString() ?: ""
                }
            })
        } catch (e: Exception) {
            // MusicService not available yet
        }
    }

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
            // Album art placeholder
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

            Text(currentTitle, fontSize = 22.sp, fontWeight = FontWeight.Bold)
            Spacer(Modifier.height(4.dp))
            Text(currentArtist, fontSize = 14.sp, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f))

            Spacer(Modifier.height(40.dp))

            // Progress bar placeholder
            LinearProgressIndicator(
                progress = { 0.3f },
                modifier = Modifier.fillMaxWidth().height(4.dp),
                color = MaterialTheme.colorScheme.primary,
                trackColor = MaterialTheme.colorScheme.surfaceVariant,
            )

            Spacer(Modifier.height(8.dp))
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text("1:23", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f))
                Text("3:45", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f))
            }

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
                    Icon(
                        if (isPlaying) Icons.Filled.PlayArrow else Icons.Filled.PlayArrow,
                        if (isPlaying) "Pause" else "Play",
                        modifier = Modifier.size(36.dp)
                    )
                }
                IconButton(onClick = { }) {
                    Icon(Icons.Filled.SkipNext, "Next", modifier = Modifier.size(44.dp))
                }
            }

            Spacer(Modifier.height(32.dp))

            Text(
                "🎵 Select a song from Search or Trending to start playing",
                fontSize = 13.sp,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.4f),
                modifier = Modifier.padding(horizontal = 32.dp)
            )
        }
    }
}

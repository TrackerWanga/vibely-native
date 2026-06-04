package com.megan.music.ui.screens

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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.session.MediaController
import androidx.media3.session.SessionToken
import androidx.navigation.NavController
import com.megan.music.service.MusicService

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PlayerScreen(navController: NavController) {
    val context = LocalContext.current
    var isPlaying by remember { mutableStateOf(false) }
    var title by remember { mutableStateOf(MusicService.currentTitle ?: "Select a song") }
    var artist by remember { mutableStateOf(MusicService.currentArtist ?: "Browse trending or search") }

    LaunchedEffect(Unit) {
        try {
            val sessionToken = SessionToken(context, ComponentName(context, MusicService::class.java))
            val controllerFuture = MediaController.Builder(context, sessionToken).buildAsync()
            controllerFuture.addListener({
                val controller = controllerFuture.get()
                controller.addListener(object : Player.Listener {
                    override fun onIsPlayingChanged(playing: Boolean) { isPlaying = playing }
                    override fun onMediaItemTransition(item: MediaItem?, reason: Int) {
                        title = item?.mediaMetadata?.title?.toString() ?: "Unknown"
                        artist = item?.mediaMetadata?.artist?.toString() ?: "Unknown Artist"
                    }
                })
            }, { it.run() })
        } catch (e: Exception) { }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Now Playing", color = Color.White) },
                navigationIcon = { IconButton(onClick = { navController.popBackStack() }) { Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back", tint = Color(0xFFA78BFA)) } },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color(0xFF0A0A18))
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier.fillMaxSize().padding(padding).padding(32.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Surface(modifier = Modifier.size(280.dp), shape = MaterialTheme.shapes.extraLarge, color = Color(0xFF1A1A2E)) {
                Box(contentAlignment = Alignment.Center) { Text("🎵", fontSize = 80.sp) }
            }

            Spacer(Modifier.height(32.dp))
            Text(title, fontSize = 22.sp, fontWeight = FontWeight.Bold, color = Color.White, maxLines = 1)
            Spacer(Modifier.height(4.dp))
            Text(artist, fontSize = 14.sp, color = Color(0xFF94A3B8))

            Spacer(Modifier.height(40.dp))
            LinearProgressIndicator(progress = { 0.3f }, modifier = Modifier.fillMaxWidth().height(4.dp), color = Color(0xFF7C3AED), trackColor = Color(0xFF1A1A2E))

            Spacer(Modifier.height(24.dp))
            Row(horizontalArrangement = Arrangement.SpaceEvenly, modifier = Modifier.fillMaxWidth()) {
                IconButton(onClick = { }) { Icon(Icons.Filled.SkipPrevious, "Previous", modifier = Modifier.size(44.dp), tint = Color.White) }
                FilledIconButton(onClick = { isPlaying = !isPlaying }, modifier = Modifier.size(72.dp), colors = IconButtonDefaults.filledIconButtonColors(containerColor = Color(0xFF7C3AED))) {
                    Icon(Icons.Filled.PlayArrow, if (isPlaying) "Pause" else "Play", modifier = Modifier.size(36.dp), tint = Color.White)
                }
                IconButton(onClick = { }) { Icon(Icons.Filled.SkipNext, "Next", modifier = Modifier.size(44.dp), tint = Color.White) }
            }

            Spacer(Modifier.height(24.dp))
            Text("🎧 Megan Music Player", fontSize = 13.sp, color = Color(0xFF475569))
            Text("Stream music via Megan API", fontSize = 11.sp, color = Color(0xFF475569))
        }
    }
}

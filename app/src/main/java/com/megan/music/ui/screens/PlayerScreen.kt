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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.megan.music.data.PlayerManager
import com.megan.music.data.PlayerState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PlayerScreen(navController: NavController) {
    val context = LocalContext.current
    val title by PlayerState.currentTitle.collectAsState()
    val artist by PlayerState.currentArtist.collectAsState()
    val thumbnail by PlayerState.currentThumbnail.collectAsState()
    val isPlaying by PlayerState.isPlaying.collectAsState()

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
            modifier = Modifier.fillMaxSize().padding(padding).padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // Artwork
            Surface(modifier = Modifier.size(260.dp), shape = MaterialTheme.shapes.extraLarge, color = Color(0xFF1A1A2E)) {
                if (thumbnail.isNotEmpty()) AsyncImage(model = thumbnail, contentDescription = null, modifier = Modifier.fillMaxSize(), contentScale = ContentScale.Crop)
                else Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxSize()) { Text("🎵", fontSize = 72.sp) }
            }

            Spacer(Modifier.height(28.dp))
            Text(title, fontSize = 22.sp, fontWeight = FontWeight.Bold, color = Color.White, maxLines = 2)
            Spacer(Modifier.height(6.dp))
            Text(artist, fontSize = 15.sp, color = Color(0xFF94A3B8))

            Spacer(Modifier.height(32.dp))
            LinearProgressIndicator(progress = { 0f }, modifier = Modifier.fillMaxWidth().height(4.dp), color = Color(0xFF7C3AED), trackColor = Color(0xFF1A1A2E))

            Spacer(Modifier.height(28.dp))
            Row(horizontalArrangement = Arrangement.SpaceEvenly, modifier = Modifier.fillMaxWidth()) {
                IconButton(onClick = { }) { Icon(Icons.Filled.SkipPrevious, "Previous", modifier = Modifier.size(44.dp), tint = Color.White) }
                FilledIconButton(onClick = { PlayerManager.toggle() }, modifier = Modifier.size(72.dp), colors = IconButtonDefaults.filledIconButtonColors(containerColor = Color(0xFF7C3AED))) {
                    Icon(Icons.Filled.PlayArrow, if (isPlaying) "Pause" else "Play", modifier = Modifier.size(36.dp), tint = Color.White)
                }
                IconButton(onClick = { }) { Icon(Icons.Filled.SkipNext, "Next", modifier = Modifier.size(44.dp), tint = Color.White) }
            }

            Spacer(Modifier.height(28.dp))
            Text("🎧 Megan Music", fontSize = 14.sp, color = Color(0xFFA78BFA), fontWeight = FontWeight.Medium)
        }
    }
}

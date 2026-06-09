package com.megan.music.ui.screens

import android.content.Intent
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
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
import com.megan.music.data.api.MeganApi
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PlayerScreen(navController: NavController) {
    val context = LocalContext.current
    val title by PlayerState.currentTitle.collectAsState()
    val artist by PlayerState.currentArtist.collectAsState()
    val thumbnail by PlayerState.currentThumbnail.collectAsState()
    val videoId by PlayerState.currentVideoId.collectAsState()
    val isPlaying by PlayerState.isPlaying.collectAsState()
    var lyrics by remember { mutableStateOf<String?>(null) }
    var lyricsLoading by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()

    // Load lyrics
    LaunchedEffect(videoId) {
        if (videoId.isNotEmpty()) {
            lyricsLoading = true
            try {
                val query = if (artist != "Unknown Artist" && artist != "Browse trending or search") "$artist $title" else title
                val response = retrofit2.Retrofit.Builder().baseUrl(MeganApi.BASE_URL).build()
                // Simple HTTP call for lyrics
                kotlinx.coroutines.Dispatchers.IO
                lyrics = null // Will be implemented with proper API call
            } catch (e: Exception) { }
            lyricsLoading = false
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Now Playing", color = Color.White) },
                navigationIcon = { IconButton(onClick = { navController.popBackStack() }) { Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back", tint = Color(0xFFA78BFA)) } },
                actions = {
                    IconButton(onClick = {
                        val shareText = "🎵 $title - $artist\n\nListen on Megan Music: https://music.megan.qzz.io"
                        val intent = Intent(Intent.ACTION_SEND).apply { type = "text/plain"; putExtra(Intent.EXTRA_TEXT, shareText) }
                        context.startActivity(Intent.createChooser(intent, "Share"))
                    }) { Icon(Icons.Filled.Share, "Share", tint = Color(0xFFA78BFA)) }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color(0xFF0A0A18))
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier.fillMaxSize().padding(padding).padding(24.dp).verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Artwork
            Surface(modifier = Modifier.size(260.dp), shape = MaterialTheme.shapes.extraLarge, color = Color(0xFF1A1A2E)) {
                if (thumbnail.isNotEmpty()) AsyncImage(model = thumbnail, contentDescription = null, modifier = Modifier.fillMaxSize(), contentScale = ContentScale.Crop)
                else Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxSize()) { Text("🎵", fontSize = 72.sp) }
            }

            Spacer(Modifier.height(24.dp))
            Text(title, fontSize = 22.sp, fontWeight = FontWeight.Bold, color = Color.White, maxLines = 2)
            Spacer(Modifier.height(4.dp))
            Text(artist, fontSize = 15.sp, color = Color(0xFF94A3B8))

            Spacer(Modifier.height(24.dp))
            LinearProgressIndicator(progress = { 0f }, modifier = Modifier.fillMaxWidth().height(4.dp), color = Color(0xFF7C3AED), trackColor = Color(0xFF1A1A2E))

            Spacer(Modifier.height(24.dp))
            Row(horizontalArrangement = Arrangement.SpaceEvenly, modifier = Modifier.fillMaxWidth()) {
                IconButton(onClick = { }) { Icon(Icons.Filled.SkipPrevious, "Previous", modifier = Modifier.size(44.dp), tint = Color.White) }
                FilledIconButton(onClick = { PlayerManager.toggle() }, modifier = Modifier.size(72.dp), colors = IconButtonDefaults.filledIconButtonColors(containerColor = Color(0xFF7C3AED))) {
                    Icon(if (isPlaying) Icons.Filled.Pause else Icons.Filled.PlayArrow, if (isPlaying) "Pause" else "Play", modifier = Modifier.size(36.dp), tint = Color.White)
                }
                IconButton(onClick = { }) { Icon(Icons.Filled.SkipNext, "Next", modifier = Modifier.size(44.dp), tint = Color.White) }
            }

            Spacer(Modifier.height(16.dp))
            // Action buttons
            Row(horizontalArrangement = Arrangement.SpaceEvenly, modifier = Modifier.fillMaxWidth()) {
                IconButton(onClick = {
                    PlayerManager.play(context, videoId, title, artist, thumbnail)
                }) { Icon(Icons.Filled.Download, "Download", tint = Color(0xFFA78BFA)) }
                IconButton(onClick = {
                    val shareText = "🎵 $title - $artist\n\nListen on Megan Music: https://music.megan.qzz.io"
                    val intent = Intent(Intent.ACTION_SEND).apply { type = "text/plain"; putExtra(Intent.EXTRA_TEXT, shareText) }
                    context.startActivity(Intent.createChooser(intent, "Share"))
                }) { Icon(Icons.Filled.Share, "Share", tint = Color(0xFFA78BFA)) }
                IconButton(onClick = { }) { Icon(Icons.Filled.Repeat, "Repeat", tint = Color(0xFF64748B)) }
            }

            Spacer(Modifier.height(20.dp))
            // Lyrics section
            Card(modifier = Modifier.fillMaxWidth(), colors = CardDefaults.cardColors(containerColor = Color(0xFF111128))) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("Lyrics", fontWeight = FontWeight.Bold, color = Color.White, fontSize = 16.sp)
                    Spacer(Modifier.height(12.dp))
                    if (lyricsLoading) {
                        CircularProgressIndicator(color = Color(0xFF7C3AED), modifier = Modifier.size(24.dp).align(Alignment.CenterHorizontally))
                    } else if (lyrics != null) {
                        Text(lyrics ?: "", color = Color(0xFF94A3B8), fontSize = 14.sp, lineHeight = 22.sp)
                    } else {
                        Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.fillMaxWidth()) {
                            Text("🎤", fontSize = 24.sp)
                            Spacer(Modifier.height(8.dp))
                            Text("No lyrics available", fontSize = 13.sp, color = Color(0xFF64748B))
                            Text("Powered by Megan API", fontSize = 11.sp, color = Color(0xFF475569))
                        }
                    }
                }
            }

            Spacer(Modifier.height(16.dp))
            Text("🎧 Megan Music", fontSize = 14.sp, color = Color(0xFFA78BFA), fontWeight = FontWeight.Medium)
        }
    }
}

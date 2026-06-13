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
import com.megan.music.data.DownloadManager

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PlayerScreen(navController: NavController) {
    val context = LocalContext.current
    val title by PlayerState.currentTitle.collectAsState()
    val artist by PlayerState.currentArtist.collectAsState()
    val thumbnail by PlayerState.currentThumbnail.collectAsState()
    val videoId by PlayerState.currentVideoId.collectAsState()
    val isPlaying by PlayerState.isPlaying.collectAsState()
    val isLoading by PlayerState.isLoading.collectAsState()
    var showLyrics by remember { mutableStateOf(false) }
    var progress by remember { mutableFloatStateOf(0f) }

    // Simulate progress bar (replace with actual MediaPlayer position tracking)
    LaunchedEffect(isPlaying) {
        while (isPlaying) {
            progress = (progress + 0.001f) % 1f
            kotlinx.coroutines.delay(1000)
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Now Playing", color = Color.White) },
                navigationIcon = { 
                    IconButton(onClick = { navController.popBackStack() }) { 
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back", tint = Color(0xFFA78BFA)) 
                    } 
                },
                actions = {
                    IconButton(onClick = {
                        val shareText = "🎵 $title - $artist\n\nListen on Megan Music: https://music.megan.qzz.io"
                        val intent = Intent(Intent.ACTION_SEND).apply { 
                            type = "text/plain"
                            putExtra(Intent.EXTRA_TEXT, shareText) 
                        }
                        context.startActivity(Intent.createChooser(intent, "Share"))
                    }) { 
                        Icon(Icons.Filled.Share, "Share", tint = Color(0xFFA78BFA)) 
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color(0xFF0A0A18))
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier.fillMaxSize().padding(padding).padding(24.dp).verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Artwork with loading overlay
            Box(modifier = Modifier.size(260.dp), contentAlignment = Alignment.Center) {
                Surface(
                    modifier = Modifier.fillMaxSize(), 
                    shape = MaterialTheme.shapes.extraLarge, 
                    color = Color(0xFF1A1A2E)
                ) {
                    if (thumbnail.isNotEmpty()) 
                        AsyncImage(model = thumbnail, contentDescription = null, modifier = Modifier.fillMaxSize(), contentScale = ContentScale.Crop)
                    else 
                        Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxSize()) { 
                            Text("🎵", fontSize = 72.sp) 
                        }
                }
                if (isLoading) {
                    Surface(
                        modifier = Modifier.fillMaxSize(), 
                        color = Color.Black.copy(alpha = 0.6f), 
                        shape = MaterialTheme.shapes.extraLarge
                    ) {
                        Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxSize()) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                CircularProgressIndicator(color = Color(0xFF7C3AED), modifier = Modifier.size(40.dp))
                                Spacer(Modifier.height(8.dp))
                                Text("Loading...", color = Color.White, fontSize = 13.sp)
                            }
                        }
                    }
                }
            }

            Spacer(Modifier.height(24.dp))
            Text(title, fontSize = 22.sp, fontWeight = FontWeight.Bold, color = Color.White, maxLines = 2)
            Spacer(Modifier.height(4.dp))
            Text(artist, fontSize = 15.sp, color = Color(0xFF94A3B8))

            // Progress bar
            Spacer(Modifier.height(20.dp))
            LinearProgressIndicator(
                progress = { progress },
                modifier = Modifier.fillMaxWidth().height(3.dp),
                color = Color(0xFF7C3AED),
                trackColor = Color(0xFF1A1A2E)
            )

            // Main playback controls
            Spacer(Modifier.height(24.dp))
            Row(
                horizontalArrangement = Arrangement.SpaceEvenly,
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Previous button
                IconButton(
                    onClick = { /* Previous track - implement playlist navigation */ },
                    modifier = Modifier.size(48.dp)
                ) {
                    Icon(
                        Icons.Filled.SkipPrevious, 
                        "Previous", 
                        modifier = Modifier.size(36.dp), 
                        tint = Color.White
                    )
                }

                // Play/Pause button (large center button)
                FilledIconButton(
                    onClick = { PlayerManager.toggle() },
                    modifier = Modifier.size(68.dp),
                    colors = IconButtonDefaults.filledIconButtonColors(containerColor = Color(0xFF7C3AED))
                ) {
                    if (isLoading) 
                        CircularProgressIndicator(color = Color.White, modifier = Modifier.size(28.dp), strokeWidth = 2.dp)
                    else 
                        Icon(
                            if (isPlaying) Icons.Filled.Pause else Icons.Filled.PlayArrow,
                            if (isPlaying) "Pause" else "Play",
                            modifier = Modifier.size(34.dp),
                            tint = Color.White
                        )
                }

                // Next button
                IconButton(
                    onClick = { /* Next track - implement playlist navigation */ },
                    modifier = Modifier.size(48.dp)
                ) {
                    Icon(
                        Icons.Filled.SkipNext, 
                        "Next", 
                        modifier = Modifier.size(36.dp), 
                        tint = Color.White
                    )
                }
            }

            // Secondary controls
            Spacer(Modifier.height(16.dp))
            Row(
                horizontalArrangement = Arrangement.SpaceEvenly,
                modifier = Modifier.fillMaxWidth()
            ) {
                // Shuffle button
                IconButton(onClick = { /* Toggle shuffle */ }) {
                    Icon(
                        Icons.Filled.Shuffle, 
                        "Shuffle", 
                        tint = Color(0xFF64748B),
                        modifier = Modifier.size(24.dp)
                    )
                }

                // Download button
                IconButton(onClick = { 
                    DownloadManager.downloadSong(context, videoId, title) { 
                        navController.navigate("auth") 
                    } 
                }) {
                    Icon(
                        Icons.Filled.Download, 
                        "Download", 
                        tint = Color(0xFFA78BFA),
                        modifier = Modifier.size(24.dp)
                    )
                }

                // Lyrics button
                IconButton(onClick = { showLyrics = !showLyrics }) {
                    Icon(
                        Icons.Filled.Lyrics, 
                        "Lyrics", 
                        tint = if (showLyrics) Color(0xFFA78BFA) else Color(0xFF64748B),
                        modifier = Modifier.size(24.dp)
                    )
                }

                // Repeat button
                IconButton(onClick = { /* Toggle repeat */ }) {
                    Icon(
                        Icons.Filled.Repeat, 
                        "Repeat", 
                        tint = Color(0xFF64748B),
                        modifier = Modifier.size(24.dp)
                    )
                }

                // Share button
                IconButton(onClick = {
                    val shareText = "🎵 $title - $artist\n\nListen on Megan Music"
                    val intent = Intent(Intent.ACTION_SEND).apply { 
                        type = "text/plain"
                        putExtra(Intent.EXTRA_TEXT, shareText) 
                    }
                    context.startActivity(Intent.createChooser(intent, "Share"))
                }) {
                    Icon(
                        Icons.Filled.Share, 
                        "Share", 
                        tint = Color(0xFFA78BFA),
                        modifier = Modifier.size(24.dp)
                    )
                }
            }

            // Lyrics section
            if (showLyrics) {
                Spacer(Modifier.height(16.dp))
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF111128))
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text("Lyrics", fontWeight = FontWeight.Bold, color = Color.White, fontSize = 15.sp)
                        Spacer(Modifier.height(12.dp))
                        Text(
                            "Lyrics powered by Megan API", 
                            color = Color(0xFF475569), 
                            fontSize = 13.sp
                        )
                    }
                }
            }

            Spacer(Modifier.height(16.dp))
            Text("🎧 Megan Music", fontSize = 13.sp, color = Color(0xFFA78BFA), fontWeight = FontWeight.Medium)
        }
    }
}

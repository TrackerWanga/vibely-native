package com.megan.music.ui.screens

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.provider.MediaStore
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.navigation.NavController

data class LocalTrack(val title: String, val artist: String, val path: String, val duration: Long, val size: Long)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OfflineScreen(navController: NavController) {
    val context = LocalContext.current
    var tracks by remember { mutableStateOf<List<LocalTrack>>(emptyList()) }
    var loading by remember { mutableStateOf(true) }
    var permissionDenied by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(context, Manifest.permission.READ_MEDIA_AUDIO) == PackageManager.PERMISSION_GRANTED) {
                tracks = scanAudioFiles(context)
            } else { permissionDenied = true }
        } else {
            if (ContextCompat.checkSelfPermission(context, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                tracks = scanAudioFiles(context)
            } else { permissionDenied = true }
        }
        loading = false
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("💾 Offline Library", color = Color.White) },
                navigationIcon = { IconButton(onClick = { navController.popBackStack() }) { Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back", tint = Color(0xFFA78BFA)) } },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color(0xFF0A0A18))
            )
        }
    ) { padding ->
        if (loading) {
            Box(modifier = Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = Color(0xFF7C3AED))
            }
        } else if (permissionDenied) {
            Box(modifier = Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) {
                Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.padding(32.dp)) {
                    Text("🔒", fontSize = 48.sp)
                    Spacer(Modifier.height(12.dp))
                    Text("Storage Permission Required", fontWeight = FontWeight.Bold, fontSize = 18.sp, color = Color.White)
                    Spacer(Modifier.height(8.dp))
                    Text("Grant music & audio access to see your songs", color = Color(0xFF94A3B8), fontSize = 14.sp)
                }
            }
        } else if (tracks.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(Icons.Filled.MusicNote, null, tint = Color(0xFF7C3AED).copy(alpha = 0.3f), modifier = Modifier.size(64.dp))
                    Spacer(Modifier.height(16.dp))
                    Text("No audio files found", color = Color(0xFF94A3B8), fontSize = 15.sp)
                }
            }
        } else {
            LazyColumn(modifier = Modifier.fillMaxSize().padding(padding)) {
                item { Text("${tracks.size} songs", color = Color(0xFF64748B), fontSize = 13.sp, modifier = Modifier.padding(16.dp)) }
                items(tracks) { track ->
                    Surface(
                        onClick = { /* Play track */ },
                        color = Color(0xFF111128),
                        shape = MaterialTheme.shapes.medium,
                        modifier = Modifier.fillMaxWidth().padding(horizontal = 12.dp, vertical = 3.dp)
                    ) {
                        Row(modifier = Modifier.padding(14.dp), verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Filled.MusicNote, null, tint = Color(0xFF7C3AED), modifier = Modifier.size(36.dp))
                            Spacer(Modifier.width(12.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Text(track.title, color = Color.White, fontSize = 14.sp, fontWeight = FontWeight.Medium, maxLines = 1, overflow = TextOverflow.Ellipsis)
                                Text(track.artist, color = Color(0xFF64748B), fontSize = 12.sp)
                            }
                            Icon(Icons.Filled.PlayArrow, null, tint = Color(0xFFA78BFA), modifier = Modifier.size(20.dp))
                        }
                    }
                }
            }
        }
    }
}

fun scanAudioFiles(context: android.content.Context): List<LocalTrack> {
    val tracks = mutableListOf<LocalTrack>()
    val projection = arrayOf(
        MediaStore.Audio.Media.TITLE,
        MediaStore.Audio.Media.ARTIST,
        MediaStore.Audio.Media.DATA,
        MediaStore.Audio.Media.DURATION,
        MediaStore.Audio.Media.SIZE
    )
    val cursor = context.contentResolver.query(
        MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, projection, null, null,
        MediaStore.Audio.Media.TITLE + " ASC"
    )
    cursor?.use {
        val titleCol = it.getColumnIndexOrThrow(MediaStore.Audio.Media.TITLE)
        val artistCol = it.getColumnIndexOrThrow(MediaStore.Audio.Media.ARTIST)
        val dataCol = it.getColumnIndexOrThrow(MediaStore.Audio.Media.DATA)
        val durCol = it.getColumnIndexOrThrow(MediaStore.Audio.Media.DURATION)
        val sizeCol = it.getColumnIndexOrThrow(MediaStore.Audio.Media.SIZE)
        while (it.moveToNext()) {
            tracks.add(LocalTrack(
                title = it.getString(titleCol) ?: "Unknown",
                artist = it.getString(artistCol) ?: "Unknown Artist",
                path = it.getString(dataCol) ?: "",
                duration = it.getLong(durCol),
                size = it.getLong(sizeCol)
            ))
        }
    }
    return tracks.take(200)
}

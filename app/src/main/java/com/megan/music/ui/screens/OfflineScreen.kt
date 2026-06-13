package com.megan.music.ui.screens

import android.Manifest
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.content.pm.PackageManager
import android.os.Build
import android.os.IBinder
import android.provider.MediaStore
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
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
import com.megan.music.service.MusicService

data class LocalTrack(
    val title: String,
    val artist: String,
    val path: String,
    val duration: Long,
    val size: Long
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OfflineScreen(navController: NavController) {
    val context = LocalContext.current
    var tracks by remember { mutableStateOf<List<LocalTrack>>(emptyList()) }
    var currentIndex by remember { mutableIntStateOf(-1) }
    var isPlaying by remember { mutableStateOf(false) }
    var shuffle by remember { mutableStateOf(false) }
    var musicService by remember { mutableStateOf<MusicService?>(null) }

    val connection = remember {
        object : ServiceConnection {
            override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
                musicService = (service as MusicService.MusicBinder).getService()
            }
            override fun onServiceDisconnected(name: ComponentName?) {
                musicService = null
            }
        }
    }

    val permissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { granted ->
        if (granted) tracks = scanAudioFiles(context)
    }

    LaunchedEffect(Unit) {
        val permission = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) 
            Manifest.permission.READ_MEDIA_AUDIO 
        else 
            Manifest.permission.READ_EXTERNAL_STORAGE

        if (ContextCompat.checkSelfPermission(context, permission) == PackageManager.PERMISSION_GRANTED) {
            tracks = scanAudioFiles(context)
        } else {
            permissionLauncher.launch(permission)
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Offline Music", color = Color.White) },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color(0xFF0A0A18))
            )
        },
        bottomBar = {
            if (currentIndex >= 0 && currentIndex < tracks.size) {
                val track = tracks[currentIndex]
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF111128)),
                    shape = RoundedCornerShape(topStart = 12.dp, topEnd = 12.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(track.title, color = Color.White, fontWeight = FontWeight.Medium, maxLines = 1, overflow = TextOverflow.Ellipsis)
                            Text(track.artist, color = Color(0xFF64748B), fontSize = 12.sp)
                        }

                        IconButton(onClick = { shuffle = !shuffle }) {
                            Icon(Icons.Filled.Shuffle, "Shuffle", tint = if (shuffle) Color(0xFF7C3AED) else Color(0xFF64748B))
                        }

                        IconButton(onClick = {
                            if (currentIndex > 0) {
                                currentIndex--
                                playTrack(context, tracks[currentIndex], musicService, connection)
                                isPlaying = true
                            }
                        }) {
                            Icon(Icons.Filled.SkipPrevious, "Prev", tint = Color.White, modifier = Modifier.size(28.dp))
                        }

                        FilledIconButton(
                            onClick = {
                                if (isPlaying) {
                                    musicService?.pause()
                                    isPlaying = false
                                } else {
                                    if (musicService != null) {
                                        musicService?.resume()
                                    } else {
                                        playTrack(context, tracks[currentIndex], musicService, connection)
                                    }
                                    isPlaying = true
                                }
                            },
                            colors = IconButtonDefaults.filledIconButtonColors(containerColor = Color(0xFF7C3AED)),
                            modifier = Modifier.size(44.dp)
                        ) {
                            Icon(
                                if (isPlaying) Icons.Filled.Pause else Icons.Filled.PlayArrow,
                                if (isPlaying) "Pause" else "Play",
                                tint = Color.White,
                                modifier = Modifier.size(24.dp)
                            )
                        }

                        IconButton(onClick = {
                            if (currentIndex < tracks.size - 1) {
                                currentIndex++
                                playTrack(context, tracks[currentIndex], musicService, connection)
                                isPlaying = true
                            }
                        }) {
                            Icon(Icons.Filled.SkipNext, "Next", tint = Color.White, modifier = Modifier.size(28.dp))
                        }
                    }
                }
            }
        }
    ) { padding ->
        if (tracks.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(Icons.Filled.MusicNote, null, tint = Color(0xFF64748B), modifier = Modifier.size(64.dp))
                    Spacer(Modifier.height(16.dp))
                    Text("No audio files found", color = Color(0xFF64748B), fontSize = 16.sp)
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize().padding(padding).padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(tracks.size) { index ->
                    val track = tracks[index]
                    Card(
                        modifier = Modifier.fillMaxWidth().clickable {
                            currentIndex = index
                            playTrack(context, track, musicService, connection)
                            isPlaying = true
                        },
                        colors = CardDefaults.cardColors(
                            containerColor = if (index == currentIndex) Color(0xFF7C3AED).copy(alpha = 0.2f) else Color(0xFF111128)
                        ),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Filled.MusicNote, null, tint = Color(0xFF7C3AED), modifier = Modifier.size(36.dp))
                            Spacer(Modifier.width(12.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Text(track.title, color = Color.White, fontWeight = FontWeight.Medium, maxLines = 1, overflow = TextOverflow.Ellipsis)
                                Text(track.artist, color = Color(0xFF64748B), fontSize = 12.sp)
                            }
                        }
                    }
                }
            }
        }
    }

    DisposableEffect(Unit) {
        onDispose { context.unbindService(connection) }
    }
}

private fun playTrack(context: Context, track: LocalTrack, service: MusicService?, connection: ServiceConnection) {
    service?.stopAll()
    val intent = Intent(context, MusicService::class.java).apply {
        putExtra("url", track.path)
        putExtra("title", track.title)
        putExtra("artist", track.artist)
    }
    context.startForegroundService(intent)
    context.bindService(intent, connection, Context.BIND_AUTO_CREATE)
}

private fun scanAudioFiles(context: Context): List<LocalTrack> {
    val tracks = mutableListOf<LocalTrack>()
    val projection = arrayOf(
        MediaStore.Audio.Media.TITLE,
        MediaStore.Audio.Media.ARTIST,
        MediaStore.Audio.Media.DATA,
        MediaStore.Audio.Media.DURATION,
        MediaStore.Audio.Media.SIZE
    )
    context.contentResolver.query(
        MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
        projection, null, null,
        "${MediaStore.Audio.Media.TITLE} ASC"
    )?.use { cursor ->
        val titleCol = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.TITLE)
        val artistCol = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ARTIST)
        val dataCol = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DATA)
        val durationCol = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DURATION)
        val sizeCol = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.SIZE)
        var count = 0
        while (cursor.moveToNext() && count < 200) {
            tracks.add(LocalTrack(
                title = cursor.getString(titleCol) ?: "Unknown",
                artist = cursor.getString(artistCol) ?: "Unknown Artist",
                path = cursor.getString(dataCol) ?: "",
                duration = cursor.getLong(durationCol),
                size = cursor.getLong(sizeCol)
            ))
            count++
        }
    }
    return tracks
}

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
import com.megan.music.data.PlayerManager

data class LocalTrack(val title: String, val artist: String, val path: String, val duration: Long, val size: Long)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OfflineScreen(navController: NavController) {
    val context = LocalContext.current
    var tracks by remember { mutableStateOf<List<LocalTrack>>(emptyList()) }
    var loading by remember { mutableStateOf(true) }
    var permissionDenied by remember { mutableStateOf(false) }
    var currentIndex by remember { mutableStateOf(-1) }

    LaunchedEffect(Unit) {
        val perm = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) Manifest.permission.READ_MEDIA_AUDIO else Manifest.permission.READ_EXTERNAL_STORAGE
        if (ContextCompat.checkSelfPermission(context, perm) == PackageManager.PERMISSION_GRANTED) tracks = scanAudioFiles(context) else permissionDenied = true
        loading = false
    }

    fun playTrack(index: Int) {
        if (index in tracks.indices) {
            currentIndex = index
            val t = tracks[index]
            PlayerManager.play(context, t.path, t.title, t.artist, null)
            navController.navigate("player")
        }
    }

    Scaffold(topBar = { TopAppBar(title = { Text("💾 Offline Library", color = Color.White) }, navigationIcon = { IconButton(onClick = { navController.popBackStack() }) { Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back", tint = Color(0xFFA78BFA)) } }, colors = TopAppBarDefaults.topAppBarColors(containerColor = Color(0xFF0A0A18))) }) { padding ->
        if (loading) Box(Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) { CircularProgressIndicator(color = Color(0xFF7C3AED)) }
        else if (permissionDenied) Box(Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) { Text("Storage Permission Required", fontWeight = FontWeight.Bold, fontSize = 18.sp, color = Color.White) }
        else if (tracks.isEmpty()) Box(Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) { Icon(Icons.Filled.MusicNote, null, tint = Color(0xFF7C3AED).copy(alpha = 0.3f), modifier = Modifier.size(64.dp)) }
        else LazyColumn(Modifier.fillMaxSize().padding(padding)) {
            item { Text("${tracks.size} songs • Tap to play", color = Color(0xFF64748B), fontSize = 13.sp, modifier = Modifier.padding(16.dp)) }
            items(tracks.size) { i ->
                val t = tracks[i]
                Surface(onClick = { playTrack(i) }, color = if (i == currentIndex) Color(0xFF1A1A2E) else Color(0xFF111128), shape = MaterialTheme.shapes.medium, modifier = Modifier.fillMaxWidth().padding(horizontal = 12.dp, vertical = 3.dp)) {
                    Row(Modifier.padding(14.dp), verticalAlignment = Alignment.CenterVertically) {
                        Text("${i + 1}", color = Color(0xFF64748B), fontSize = 14.sp, modifier = Modifier.width(30.dp))
                        Icon(Icons.Filled.MusicNote, null, tint = Color(0xFF7C3AED), modifier = Modifier.size(36.dp))
                        Spacer(Modifier.width(12.dp))
                        Column(Modifier.weight(1f)) { Text(t.title, color = Color.White, fontSize = 14.sp, fontWeight = FontWeight.Medium, maxLines = 1, overflow = TextOverflow.Ellipsis); Text(t.artist, color = Color(0xFF64748B), fontSize = 12.sp) }
                        Icon(Icons.Filled.PlayArrow, null, tint = Color(0xFFA78BFA), modifier = Modifier.size(20.dp))
                    }
                }
            }
        }
    }
}

fun scanAudioFiles(c: android.content.Context): List<LocalTrack> {
    val tracks = mutableListOf<LocalTrack>()
    val proj = arrayOf(MediaStore.Audio.Media.TITLE, MediaStore.Audio.Media.ARTIST, MediaStore.Audio.Media.DATA, MediaStore.Audio.Media.DURATION, MediaStore.Audio.Media.SIZE)
    c.contentResolver.query(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, proj, null, null, MediaStore.Audio.Media.TITLE + " ASC")?.use { cur ->
        val ti = cur.getColumnIndexOrThrow(MediaStore.Audio.Media.TITLE); val ar = cur.getColumnIndexOrThrow(MediaStore.Audio.Media.ARTIST); val da = cur.getColumnIndexOrThrow(MediaStore.Audio.Media.DATA); val du = cur.getColumnIndexOrThrow(MediaStore.Audio.Media.DURATION); val si = cur.getColumnIndexOrThrow(MediaStore.Audio.Media.SIZE)
        while (cur.moveToNext()) tracks.add(LocalTrack(cur.getString(ti) ?: "Unknown", cur.getString(ar) ?: "Unknown Artist", cur.getString(da) ?: "", cur.getLong(du), cur.getLong(si)))
    }
    return tracks
}

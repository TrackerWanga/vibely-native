package com.megan.music.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.megan.music.data.PlayerManager
import com.megan.music.data.api.MeganSong

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchScreen(navController: NavController, viewModel: SearchViewModel = hiltViewModel()) {
    val context = LocalContext.current
    val results by viewModel.results.collectAsState()
    val loading by viewModel.loading.collectAsState()
    var query by remember { mutableStateOf("") }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    TextField(value = query, onValueChange = { query = it }, placeholder = { Text("Search artists, songs...", color = Color(0xFF64748B)) }, singleLine = true, colors = TextFieldDefaults.colors(focusedContainerColor = Color.Transparent, unfocusedContainerColor = Color.Transparent, focusedTextColor = Color.White, unfocusedTextColor = Color.White))
                },
                navigationIcon = { IconButton(onClick = { navController.popBackStack() }) { Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back", tint = Color(0xFFA78BFA)) } },
                actions = { IconButton(onClick = { if (query.isNotBlank()) viewModel.search(query) }) { Icon(Icons.Filled.Search, "Search", tint = Color(0xFFA78BFA)) } },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color(0xFF0A0A18))
            )
        }
    ) { padding ->
        if (loading) {
            Box(modifier = Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) { CircularProgressIndicator(color = Color(0xFF7C3AED)) }
        } else if (results.isEmpty() && query.isNotEmpty()) {
            Box(modifier = Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) { Text("No results", color = Color(0xFF94A3B8), fontSize = 16.sp); Spacer(Modifier.height(4.dp)); Text("Try a different search", color = Color(0xFF64748B), fontSize = 13.sp) }
            }
        } else if (results.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) { Icon(Icons.Filled.MusicNote, null, tint = Color(0xFF7C3AED).copy(alpha = 0.3f), modifier = Modifier.size(64.dp)); Spacer(Modifier.height(12.dp)); Text("Search for artists or songs", color = Color(0xFF94A3B8), fontSize = 15.sp) }
            }
        } else {
            LazyColumn(modifier = Modifier.padding(padding)) {
                items(results) { song ->
                    ListItem(
                        leadingContent = { AsyncImage(model = song.thumbnail ?: "", contentDescription = null, modifier = Modifier.size(56.dp)) },
                        headlineContent = { Text(song.title?.take(50) ?: "", color = Color.White, fontSize = 14.sp, fontWeight = FontWeight.Medium, maxLines = 1) },
                        supportingContent = { Text("${song.author ?: "Unknown"} • ${song.duration ?: ""}", color = Color(0xFF64748B), fontSize = 12.sp) },
                        modifier = Modifier.clickable {
                            PlayerManager.play(context, song.videoId ?: "", song.title, song.author, song.thumbnail)
                            navController.navigate("player")
                        }
                    )
                    HorizontalDivider(color = Color(0xFF1A1A2E))
                }
            }
        }
    }
}

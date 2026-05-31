package com.megan.music.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.megan.music.data.api.Artist
import com.megan.music.data.api.Song
import com.megan.music.util.formatCount

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ArtistScreen(artistName: String, navController: NavController, viewModel: ArtistViewModel = hiltViewModel()) {
    val artist by viewModel.artist.collectAsState()
    val similar by viewModel.similar.collectAsState()
    val loading by viewModel.loading.collectAsState()
    LaunchedEffect(artistName) { viewModel.loadArtist(artistName) }

    Scaffold(
        topBar = { TopAppBar(title = { Text(artist?.name ?: "Artist", color = Color.White) }, navigationIcon = { IconButton(onClick = { navController.popBackStack() }) { Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back", tint = Color(0xFFA78BFA)) } }, colors = TopAppBarDefaults.topAppBarColors(containerColor = Color(0xFF0A0A18))) }
    ) { padding ->
        if (loading) Box(modifier = Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) { CircularProgressIndicator(color = Color(0xFF7C3AED)) }
        else if (artist != null) LazyColumn(modifier = Modifier.fillMaxSize().padding(padding).padding(horizontal = 16.dp)) {
            item {
                Row(modifier = Modifier.padding(vertical = 16.dp), verticalAlignment = Alignment.CenterVertically) {
                    AsyncImage(model = artist!!.channel?.image ?: "", contentDescription = artist!!.name, modifier = Modifier.size(120.dp).clip(CircleShape).background(Color(0xFF1A1A2E)), contentScale = ContentScale.Crop)
                    Spacer(Modifier.width(20.dp))
                    Column { Text("${artist!!.flag ?: ""} ${artist!!.country ?: ""}", color = Color(0xFFA78BFA), fontSize = 13.sp); Text(artist!!.name ?: "", color = Color.White, fontSize = 28.sp, fontWeight = FontWeight.Bold); Text("${formatCount(artist!!.channel?.subscribers ?: 0)} subscribers", color = Color(0xFF94A3B8), fontSize = 14.sp) }
                }
                Spacer(Modifier.height(12.dp))
                Button(onClick = { navController.navigate("player") }, colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF7C3AED)), modifier = Modifier.fillMaxWidth()) { Icon(Icons.Filled.PlayArrow, "Play", tint = Color.White, modifier = Modifier.size(18.dp)); Spacer(Modifier.width(8.dp)); Text("Play All", color = Color.White) }
                Spacer(Modifier.height(24.dp))
            }
            val songs = artist!!.topSongs ?: emptyList()
            if (songs.isNotEmpty()) {
                item { Text("Top Songs", color = Color.White, fontSize = 20.sp, fontWeight = FontWeight.Bold) }
                items(songs) { s -> Surface(onClick = { navController.navigate("player") }, color = Color(0xFF111128), shape = MaterialTheme.shapes.medium, modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)) { Row(modifier = Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) { AsyncImage(model = s.thumbnail ?: "", contentDescription = null, modifier = Modifier.size(48.dp, 36.dp).clip(MaterialTheme.shapes.small), contentScale = ContentScale.Crop); Spacer(Modifier.width(12.dp)); Column(modifier = Modifier.weight(1f)) { Text(s.title ?: "", color = Color(0xFFF1F5F9), fontSize = 14.sp, maxLines = 1, overflow = TextOverflow.Ellipsis); Text("${formatCount(s.views ?: 0)} views • ${s.duration ?: ""}", color = Color(0xFF64748B), fontSize = 12.sp) }; Icon(Icons.Filled.PlayArrow, "Play", tint = Color(0xFFA78BFA), modifier = Modifier.size(20.dp)) } } }
            }
            if (similar.isNotEmpty()) {
                item { Spacer(Modifier.height(24.dp)); Text("Similar Artists", color = Color.White, fontSize = 20.sp, fontWeight = FontWeight.Bold) }
                item { LazyRow { items(similar) { a -> Card(modifier = Modifier.width(150.dp).padding(8.dp).clickable { navController.navigate("artist/${a.name}") }, colors = CardDefaults.cardColors(containerColor = Color(0xFF111128))) { Column(modifier = Modifier.padding(12.dp), horizontalAlignment = Alignment.CenterHorizontally) { AsyncImage(model = a.channel?.image ?: "", contentDescription = a.name, modifier = Modifier.size(64.dp).clip(CircleShape).background(Color(0xFF1A1A2E)), contentScale = ContentScale.Crop); Spacer(Modifier.height(8.dp)); Text(a.name ?: "", color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.Medium, maxLines = 1); Text("${a.flag ?: ""} ${a.country ?: ""}", color = Color(0xFF64748B), fontSize = 10.sp) } } } } }
            }
            item { Spacer(Modifier.height(100.dp)) }
        }
    }
}

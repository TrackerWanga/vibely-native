package com.megan.music.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.MusicNote
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
import com.megan.music.util.formatCount

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GospelScreen(navController: NavController, viewModel: GospelViewModel = hiltViewModel()) {
    val artists by viewModel.artists.collectAsState()
    val loading by viewModel.loading.collectAsState()
    val grouped by viewModel.groupedArtists.collectAsState()
    LaunchedEffect(Unit) { viewModel.loadGospel() }

    Scaffold(
        topBar = { TopAppBar(title = { Text("🙏 Gospel Music", color = Color.White) }, navigationIcon = { IconButton(onClick = { navController.popBackStack() }) { Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back", tint = Color(0xFFA78BFA)) } }, colors = TopAppBarDefaults.topAppBarColors(containerColor = Color(0xFF0A0A18))) }
    ) { padding ->
        if (loading) Box(modifier = Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) { CircularProgressIndicator(color = Color(0xFF10B981)) }
        else LazyColumn(modifier = Modifier.fillMaxSize().padding(padding).padding(horizontal = 16.dp)) {
            item { Text("${artists.size} gospel artists from ${grouped.size} countries", color = Color(0xFF64748B), fontSize = 14.sp, modifier = Modifier.padding(vertical = 8.dp)) }
            grouped.forEach { (country, countryArtists) ->
                item { Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(vertical = 12.dp)) { Text(countryArtists.firstOrNull()?.flag ?: "🌍", fontSize = 24.sp); Spacer(Modifier.width(8.dp)); Text(country, color = Color.White, fontSize = 18.sp, fontWeight = FontWeight.Bold); Spacer(Modifier.width(8.dp)); Text("${countryArtists.size} artists", color = Color(0xFF64748B), fontSize = 13.sp) } }
                items(countryArtists) { a ->
                    Surface(onClick = { navController.navigate("artist/${a.name}") }, color = Color(0xFF111128), shape = MaterialTheme.shapes.medium, modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                if (a.channel?.image != null) AsyncImage(model = a.channel.image, contentDescription = a.name, modifier = Modifier.size(44.dp).clip(CircleShape), contentScale = ContentScale.Crop)
                                else Box(modifier = Modifier.size(44.dp).clip(CircleShape).background(Color(0xFF065F46)), contentAlignment = Alignment.Center) { Icon(Icons.Filled.MusicNote, null, tint = Color(0xFF10B981), modifier = Modifier.size(20.dp)) }
                                Spacer(Modifier.width(12.dp)); Column(modifier = Modifier.weight(1f)) { Text(a.name ?: "", color = Color.White, fontSize = 14.sp, fontWeight = FontWeight.Medium); Text("${a.songCount ?: 0} songs • ${formatCount(a.channel?.subscribers ?: 0)} subs", color = Color(0xFF64748B), fontSize = 11.sp) }
                                Surface(onClick = { }, color = Color(0xFF10B981), shape = MaterialTheme.shapes.small) { Row(modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp), verticalAlignment = Alignment.CenterVertically) { Icon(Icons.Filled.PlayArrow, "Play", tint = Color.White, modifier = Modifier.size(12.dp)); Spacer(Modifier.width(4.dp)); Text("Play All", color = Color.White, fontSize = 11.sp) } }
                            }
                            a.topSongs?.take(3)?.forEach { s -> Spacer(Modifier.height(4.dp)); Row(modifier = Modifier.fillMaxWidth().padding(start = 56.dp), verticalAlignment = Alignment.CenterVertically) { Text(s.title?.take(40) ?: "", color = Color(0xFF94A3B8), fontSize = 12.sp, maxLines = 1, overflow = TextOverflow.Ellipsis, modifier = Modifier.weight(1f)); Text(s.duration ?: "", color = Color(0xFF64748B), fontSize = 10.sp); Icon(Icons.Filled.PlayArrow, null, tint = Color(0xFF10B981), modifier = Modifier.size(10.dp)) } }
                        }
                    }
                }
            }
            item { Spacer(Modifier.height(100.dp)) }
        }
    }
}

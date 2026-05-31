package com.megan.music.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
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
fun BelovedScreen(navController: NavController, viewModel: BelovedViewModel = hiltViewModel()) {
    val beloved by viewModel.beloved.collectAsState()
    val loading by viewModel.loading.collectAsState()
    LaunchedEffect(Unit) { viewModel.loadBeloved() }

    Scaffold(
        topBar = { TopAppBar(title = { Text("🌟 Beloved & Trending", color = Color.White) }, navigationIcon = { IconButton(onClick = { navController.popBackStack() }) { Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back", tint = Color(0xFFA78BFA)) } }, colors = TopAppBarDefaults.topAppBarColors(containerColor = Color(0xFF0A0A18))) }
    ) { padding ->
        if (loading) Box(modifier = Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) { CircularProgressIndicator(color = Color(0xFFF59E0B)) }
        else LazyVerticalGrid(columns = GridCells.Adaptive(minSize = 180.dp), modifier = Modifier.fillMaxSize().padding(padding).padding(8.dp), horizontalArrangement = Arrangement.spacedBy(8.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            items(beloved) { a ->
                Card(onClick = { navController.navigate("artist/${a.name}") }, colors = CardDefaults.cardColors(containerColor = Color(0xFF111128))) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            if (a.channel?.image != null) AsyncImage(model = a.channel.image, contentDescription = a.name, modifier = Modifier.size(48.dp).clip(CircleShape), contentScale = ContentScale.Crop)
                            else Box(modifier = Modifier.size(48.dp).clip(CircleShape).background(Color(0xFF78350F)), contentAlignment = Alignment.Center) { Icon(Icons.Filled.MusicNote, null, tint = Color(0xFFF59E0B), modifier = Modifier.size(24.dp)) }
                            Spacer(Modifier.width(10.dp)); Column(modifier = Modifier.weight(1f)) { Text(a.name ?: "", color = Color.White, fontSize = 14.sp, fontWeight = FontWeight.Medium, maxLines = 1); Text("${formatCount(a.channel?.subscribers ?: 0)} subs", color = Color(0xFF64748B), fontSize = 11.sp) }
                        }
                        a.topSongs?.take(3)?.forEach { s ->
                            Spacer(Modifier.height(4.dp)); Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) { Text(s.title?.take(30) ?: "", color = Color(0xFF94A3B8), fontSize = 11.sp, maxLines = 1, overflow = TextOverflow.Ellipsis, modifier = Modifier.weight(1f)); Text(s.duration ?: "", color = Color(0xFF64748B), fontSize = 10.sp); Icon(Icons.Filled.PlayArrow, null, tint = Color(0xFFF59E0B), modifier = Modifier.size(10.dp)) }
                        }
                    }
                }
            }
        }
    }
}

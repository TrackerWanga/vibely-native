import com.megan.music.util.formatCount
package com.megan.music.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.megan.music.data.api.MeganSong
import com.megan.music.data.api.Artist as DiscoveryArtist
import com.megan.music.data.api.Country
import kotlinx.coroutines.delay

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(navController: NavController, viewModel: HomeViewModel = hiltViewModel()) {
    val trending by viewModel.trending.collectAsState()
    val homepage by viewModel.homepage.collectAsState()
    val loading by viewModel.loading.collectAsState()
    val visibleCountries by viewModel.visibleCountries.collectAsState()
    val countrySongs by viewModel.countrySongs.collectAsState()

    var bannerIndex by remember { mutableStateOf(0) }
    val banners = homepage?.banner ?: emptyList()

    LaunchedEffect(banners.size) {
        if (banners.size > 1) {
            while (true) { delay(5000); bannerIndex = (bannerIndex + 1) % banners.size }
        }
    }

    // Infinite scroll
    val listState = rememberLazyListState()
    val shouldLoadMore = remember {
        derivedStateOf {
            val lastItem = listState.layoutInfo.visibleItemsInfo.lastOrNull() ?: return@derivedStateOf false
            lastItem.index >= listState.layoutInfo.totalItemsCount - 3
        }
    }
    LaunchedEffect(shouldLoadMore.value) {
        if (shouldLoadMore.value) viewModel.loadMoreCountries()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("🎵 Megan Music", fontWeight = FontWeight.Bold, color = Color.White) },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color(0xFF0A0A18)),
                actions = {
                    IconButton(onClick = { navController.navigate("search") }) {
                        Icon(Icons.Default.Search, "Search", tint = Color(0xFFA78BFA))
                    }
                }
            )
        }
    ) { padding ->
        if (loading) {
            Box(modifier = Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    CircularProgressIndicator(color = Color(0xFF7C3AED))
                    Text("Loading...", color = Color(0xFF94A3B8))
                }
            }
        } else {
            LazyColumn(modifier = Modifier.fillMaxSize().padding(padding), state = listState) {
                // Banner
                if (banners.isNotEmpty()) {
                    item {
                        val artist = banners[bannerIndex]
                        BannerCard(artist)
                        if (banners.size > 1) {
                            Row(modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp), horizontalArrangement = Arrangement.Center) {
                                banners.forEachIndexed { i, _ ->
                                    Box(modifier = Modifier.padding(4.dp).size(if (i == bannerIndex) 10.dp else 8.dp).clip(MaterialTheme.shapes.small).background(if (i == bannerIndex) Color(0xFFA78BFA) else Color(0xFF475569)))
                                }
                            }
                        }
                    }
                }

                // Quick actions
                item {
                    Row(modifier = Modifier.fillMaxWidth().padding(12.dp), horizontalArrangement = Arrangement.SpaceEvenly) {
                        QuickChip("🙏 Gospel") { navController.navigate("gospel") }
                        QuickChip("🌟 Beloved") { navController.navigate("beloved") }
                        QuickChip("💾 Offline") { navController.navigate("offline") }
                    }
                }

                // Discover
                if (trending.isNotEmpty()) {
                    item { SectionTitle("🔥 Discover", "Trending now") }
                    item {
                        LazyRow(contentPadding = PaddingValues(horizontal = 8.dp)) {
                            items(trending) { song -> YouTubeCard(song) { navController.navigate("player") } }
                        }
                    }
                }

                // Featured Artists
                val discoveryArtists = homepage?.trending
                if (discoveryArtists != null && discoveryArtists.isNotEmpty()) {
                    item { SectionTitle("🎤 Featured Artists") }
                    item {
                        LazyRow(contentPadding = PaddingValues(horizontal = 8.dp)) {
                            items(discoveryArtists.take(20)) { artist -> ArtistCard(artist) { navController.navigate("artist/${artist.name}") } }
                        }
                    }
                }

                // Top Artists
                val topArtists = homepage?.topArtists
                if (topArtists != null && topArtists.isNotEmpty()) {
                    item { SectionTitle("🏆 Top Artists") }
                    item {
                        LazyRow(contentPadding = PaddingValues(horizontal = 8.dp)) {
                            items(topArtists.take(10)) { artist -> TopArtistCard(artist, topArtists.indexOf(artist) + 1) { navController.navigate("artist/${artist.name}") } }
                        }
                    }
                }

                // Countries with songs - vertical sections
                items(visibleCountries) { country ->
                    CountrySection(
                        country = country,
                        songs = countrySongs[country.code ?: country.name ?: ""] ?: emptyList(),
                        onSongClick = { navController.navigate("player") }
                    )
                }

                // Loading more indicator
                item {
                    if (visibleCountries.size < (homepage?.countries?.size ?: 0)) {
                        Box(modifier = Modifier.fillMaxWidth().padding(24.dp), contentAlignment = Alignment.Center) {
                            CircularProgressIndicator(color = Color(0xFF7C3AED), modifier = Modifier.size(24.dp))
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun CountrySection(country: Country, songs: List<MeganSong>, onSongClick: () -> Unit) {
    Column(modifier = Modifier.padding(vertical = 8.dp)) {
        Row(modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 8.dp), verticalAlignment = Alignment.CenterVertically) {
            Text(country.flag ?: "🌍", fontSize = 28.sp)
            Spacer(Modifier.width(8.dp))
            Text(country.name ?: "", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = Color(0xFFF1F5F9))
            Spacer(Modifier.weight(1f))
            Text("${country.totalArtists ?: 0} artists", fontSize = 12.sp, color = Color(0xFF64748B))
        }
        if (songs.isNotEmpty()) {
            LazyRow(contentPadding = PaddingValues(horizontal = 8.dp)) {
                items(songs.take(10)) { song ->
                    YouTubeCard(song, onClick = onSongClick)
                }
            }
        } else {
            Box(modifier = Modifier.fillMaxWidth().height(120.dp).padding(8.dp).clip(MaterialTheme.shapes.medium).background(Color(0xFF111128)), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = Color(0xFF7C3AED), modifier = Modifier.size(20.dp))
            }
        }
    }
}

@Composable
fun SectionTitle(title: String, subtitle: String? = null) {
    Column(modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 8.dp)) {
        Text(title, fontSize = 20.sp, fontWeight = FontWeight.Bold, color = Color(0xFFF1F5F9))
        if (subtitle != null) Text(subtitle, fontSize = 12.sp, color = Color(0xFF64748B))
    }
}

@Composable
fun BannerCard(artist: DiscoveryArtist) {
    Box(modifier = Modifier.fillMaxWidth().height(220.dp).padding(horizontal = 8.dp).clip(MaterialTheme.shapes.large)) {
        AsyncImage(model = artist.channel?.image ?: "", contentDescription = artist.name, modifier = Modifier.fillMaxSize(), contentScale = ContentScale.Crop)
        Box(modifier = Modifier.fillMaxSize().background(Brush.verticalGradient(listOf(Color.Transparent, Color.Black.copy(alpha = 0.8f)))))
        Column(modifier = Modifier.align(Alignment.BottomStart).padding(16.dp)) {
            Text("${artist.flag ?: ""} ${artist.country ?: ""}", color = Color(0xFFA78BFA), fontSize = 12.sp)
            Text(artist.name ?: "", color = Color.White, fontSize = 26.sp, fontWeight = FontWeight.Bold, maxLines = 1)
            if (artist.topSongs?.isNotEmpty() == true) {
                Spacer(Modifier.height(8.dp))
                Surface(onClick = { }, color = Color(0xFF7C3AED), shape = MaterialTheme.shapes.medium) {
                    Row(modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp), verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Filled.PlayArrow, "Play", tint = Color.White, modifier = Modifier.size(18.dp))
                        Spacer(Modifier.width(4.dp))
                        Text("Play Audio", color = Color.White, fontSize = 13.sp)
                    }
                }
            }
        }
    }
}

@Composable
fun YouTubeCard(song: MeganSong, onClick: () -> Unit) {
    Card(modifier = Modifier.width(180.dp).padding(8.dp).clickable(onClick = onClick), colors = CardDefaults.cardColors(containerColor = Color(0xFF111128))) {    .clickable(onClick = onClick)
        Column {
            Box(modifier = Modifier.fillMaxWidth().height(110.dp).clip(MaterialTheme.shapes.medium)) {
                AsyncImage(model = song.thumbnail ?: "", contentDescription = song.title, modifier = Modifier.fillMaxSize(), contentScale = ContentScale.Crop)
                if (song.duration != null) {
                    Box(modifier = Modifier.align(Alignment.BottomEnd).padding(4.dp).background(Color.Black.copy(alpha = 0.7f), MaterialTheme.shapes.small).padding(horizontal = 6.dp, vertical = 2.dp)) {
                        Text(song.duration, color = Color.White, fontSize = 10.sp)
                    }
                }
            }
            Column(modifier = Modifier.padding(8.dp)) {
                Text(song.title?.take(45) ?: "", fontSize = 12.sp, fontWeight = FontWeight.Medium, color = Color(0xFFF1F5F9), maxLines = 2, overflow = TextOverflow.Ellipsis, lineHeight = 16.sp)
                Text(song.author ?: "Unknown", fontSize = 11.sp, color = Color(0xFFA78BFA), maxLines = 1, overflow = TextOverflow.Ellipsis)
                Text("${formatCount(song.views ?: 0)} views", fontSize = 10.sp, color = Color(0xFF64748B))
            }
        }
    }
}

@Composable
fun ArtistCard(artist: DiscoveryArtist, onClick: () -> Unit = {}) {
    Card(modifier = Modifier.width(150.dp).padding(8.dp), colors = CardDefaults.cardColors(containerColor = Color(0xFF111128))) {    .clickable(onClick = onClick)
        Column(modifier = Modifier.padding(12.dp), horizontalAlignment = Alignment.CenterHorizontally) {
            Box(modifier = Modifier.size(72.dp).clip(MaterialTheme.shapes.medium).background(Color(0xFF1A1A2E)), contentAlignment = Alignment.Center) {
                AsyncImage(model = artist.channel?.image ?: "", contentDescription = artist.name, modifier = Modifier.fillMaxSize(), contentScale = ContentScale.Crop)
            }
            Spacer(Modifier.height(8.dp))
            Text(artist.name ?: "", fontSize = 13.sp, fontWeight = FontWeight.Medium, color = Color(0xFFF1F5F9), maxLines = 1, overflow = TextOverflow.Ellipsis)
            Text("${artist.flag ?: ""} ${artist.country ?: ""}", fontSize = 11.sp, color = Color(0xFF64748B), maxLines = 1)
        }
    }
}

@Composable
fun TopArtistCard(artist: DiscoveryArtist, rank: Int, onClick: () -> Unit = {}) {
    val rankColor = when (rank) { 1 -> Color(0xFFF59E0B); 2 -> Color(0xFF94A3B8); 3 -> Color(0xFFCD7F32); else -> Color(0xFF475569) }
    Card(modifier = Modifier.width(180.dp).padding(8.dp), colors = CardDefaults.cardColors(containerColor = Color(0xFF111128))) {    .clickable(onClick = onClick)
        Row(modifier = Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
            Text("#$rank", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = rankColor)
            Spacer(Modifier.width(10.dp))
            Box(modifier = Modifier.size(44.dp).clip(MaterialTheme.shapes.small).background(Color(0xFF1A1A2E)), contentAlignment = Alignment.Center) {
                AsyncImage(model = artist.channel?.image ?: "", contentDescription = artist.name, modifier = Modifier.fillMaxSize(), contentScale = ContentScale.Crop)
            }
            Spacer(Modifier.width(8.dp))
            Column {
                Text(artist.name ?: "", fontSize = 12.sp, fontWeight = FontWeight.Medium, color = Color(0xFFF1F5F9), maxLines = 1, overflow = TextOverflow.Ellipsis)
                Text("${artist.flag ?: ""} ${artist.country ?: ""}", fontSize = 10.sp, color = Color(0xFF64748B))
            }
        }
    }
}

@Composable
fun QuickChip(label: String, onClick: () -> Unit) {
    Surface(onClick = onClick, shape = MaterialTheme.shapes.medium, color = Color(0xFF1A1A2E)) {
        Text(label, modifier = Modifier.padding(horizontal = 20.dp, vertical = 12.dp), fontSize = 14.sp, color = Color(0xFFE2E8F0))
    }
}


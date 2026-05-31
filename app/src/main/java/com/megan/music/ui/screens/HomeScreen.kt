package com.megan.music.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
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
import kotlinx.coroutines.delay

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(navController: NavController, viewModel: HomeViewModel = hiltViewModel()) {
    val trending by viewModel.trending.collectAsState()
    val homepage by viewModel.homepage.collectAsState()
    val loading by viewModel.loading.collectAsState()

    // Auto-rotating banner
    var bannerIndex by remember { mutableIntStateOf(0) }
    val banners = homepage?.banner ?: emptyList()

    LaunchedEffect(banners.size) {
        if (banners.size > 1) {
            while (true) {
                delay(5000)
                bannerIndex = (bannerIndex + 1) % banners.size
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("🎵 Megan Music", fontWeight = FontWeight.Bold) },
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
                    Spacer(Modifier.height(16.dp))
                    Text("Loading...", color = Color(0xFF94A3B8))
                }
            }
        } else {
            Column(
                modifier = Modifier.fillMaxSize().padding(padding).verticalScroll(rememberScrollState())
            ) {
                // Rotating Banner
                if (banners.isNotEmpty()) {
                    val artist = banners[bannerIndex]
                    BannerCard(artist)
                    // Dots indicator
                    if (banners.size > 1) {
                        Row(
                            modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
                            horizontalArrangement = Arrangement.Center
                        ) {
                            banners.forEachIndexed { index, _ ->
                                Box(
                                    modifier = Modifier
                                        .padding(4.dp)
                                        .size(if (index == bannerIndex) 10.dp else 8.dp)
                                        .clip(MaterialTheme.shapes.small)
                                        .background(if (index == bannerIndex) Color(0xFFA78BFA) else Color(0xFF475569))
                                )
                            }
                        }
                    }
                }

                // Quick actions
                Row(
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 12.dp, vertical = 8.dp),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    QuickChip("🙏 Gospel") { }
                    QuickChip("🌟 Beloved") { }
                    QuickChip("💾 Offline") { navController.navigate("offline") }
                }

                // Discover - YouTube Trending
                if (trending.isNotEmpty()) {
                    SectionTitle("🔥 Discover", "Trending now")
                    LazyRow(modifier = Modifier.fillMaxWidth(), contentPadding = PaddingValues(horizontal = 8.dp)) {
                        items(trending) { song ->
                            YouTubeCard(song) { navController.navigate("player") }
                        }
                    }
                }

                // Featured Artists from Discovery
                val discoveryArtists = homepage?.trending
                if (discoveryArtists != null && discoveryArtists.isNotEmpty()) {
                    SectionTitle("🎤 Featured Artists", "${discoveryArtists.size} artists")
                    LazyRow(modifier = Modifier.fillMaxWidth(), contentPadding = PaddingValues(horizontal = 8.dp)) {
                        items(discoveryArtists.take(20)) { artist ->
                            ArtistCard(artist)
                        }
                    }
                }

                // Top Artists
                val topArtists = homepage?.topArtists
                if (topArtists != null && topArtists.isNotEmpty()) {
                    SectionTitle("🏆 Top Artists", "Global rankings")
                    LazyRow(modifier = Modifier.fillMaxWidth(), contentPadding = PaddingValues(horizontal = 8.dp)) {
                        items(topArtists.take(10)) { artist ->
                            TopArtistCard(artist, topArtists.indexOf(artist) + 1)
                        }
                    }
                }

                Spacer(Modifier.height(32.dp))
            }
        }
    }
}

@Composable
fun SectionTitle(title: String, subtitle: String? = null) {
    Column(modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp)) {
        Text(title, fontSize = 20.sp, fontWeight = FontWeight.Bold, color = Color(0xFFF1F5F9))
        if (subtitle != null) {
            Text(subtitle, fontSize = 12.sp, color = Color(0xFF64748B))
        }
    }
}

@Composable
fun BannerCard(artist: DiscoveryArtist) {
    Box(
        modifier = Modifier.fillMaxWidth().height(220.dp).padding(horizontal = 8.dp).clip(MaterialTheme.shapes.large)
    ) {
        AsyncImage(
            model = artist.channel?.image ?: "",
            contentDescription = artist.name,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )
        Box(
            modifier = Modifier.fillMaxSize().background(
                Brush.verticalGradient(listOf(Color.Transparent, Color.Black.copy(alpha = 0.8f)))
            )
        )
        Column(modifier = Modifier.align(Alignment.BottomStart).padding(16.dp)) {
            Text("${artist.flag ?: ""} ${artist.country ?: ""}", color = Color(0xFFA78BFA), fontSize = 12.sp)
            Text(artist.name ?: "", color = Color.White, fontSize = 26.sp, fontWeight = FontWeight.Bold, maxLines = 1)
            Text(
                "${artist.channel?.subscribers?.let { formatCount(it) } ?: ""} subscribers",
                color = Color(0xFF94A3B8), fontSize = 13.sp
            )
            if (artist.topSongs?.isNotEmpty() == true) {
                Spacer(Modifier.height(8.dp))
                Surface(
                    onClick = { },
                    color = Color(0xFF7C3AED),
                    shape = MaterialTheme.shapes.medium
                ) {
                    Row(modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp), verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Filled.PlayArrow, "Play", tint = Color.White, modifier = Modifier.size(18.dp))
                        Spacer(Modifier.width(4.dp))
                        Text("Play Audio", color = Color.White, fontSize = 13.sp, fontWeight = FontWeight.Medium)
                    }
                }
            }
        }
    }
}

@Composable
fun YouTubeCard(song: MeganSong, onClick: () -> Unit) {
    Card(
        modifier = Modifier.width(180.dp).padding(8.dp).clickable(onClick = onClick),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF111128)),
        shape = MaterialTheme.shapes.medium
    ) {
        Column {
            Box(modifier = Modifier.fillMaxWidth().height(110.dp).clip(MaterialTheme.shapes.medium)) {
                AsyncImage(
                    model = song.thumbnail ?: "",
                    contentDescription = song.title,
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )
                // Duration badge
                if (song.duration != null) {
                    Box(
                        modifier = Modifier.align(Alignment.BottomEnd).padding(4.dp)
                            .background(Color.Black.copy(alpha = 0.7f), MaterialTheme.shapes.small)
                            .padding(horizontal = 6.dp, vertical = 2.dp)
                    ) {
                        Text(song.duration, color = Color.White, fontSize = 10.sp)
                    }
                }
            }
            Column(modifier = Modifier.padding(8.dp)) {
                Text(song.title?.take(45) ?: "", fontSize = 12.sp, fontWeight = FontWeight.Medium, color = Color(0xFFF1F5F9), maxLines = 2, overflow = TextOverflow.Ellipsis, lineHeight = 16.sp)
                Spacer(Modifier.height(4.dp))
                Text(song.author ?: "Unknown", fontSize = 11.sp, color = Color(0xFFA78BFA), maxLines = 1, overflow = TextOverflow.Ellipsis)
                Text("${formatCount(song.views ?: 0)} views", fontSize = 10.sp, color = Color(0xFF64748B))
            }
        }
    }
}

@Composable
fun ArtistCard(artist: DiscoveryArtist) {
    Card(
        modifier = Modifier.width(150.dp).padding(8.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF111128)),
        shape = MaterialTheme.shapes.medium
    ) {
        Column(modifier = Modifier.padding(12.dp), horizontalAlignment = Alignment.CenterHorizontally) {
            Box(modifier = Modifier.size(72.dp).clip(MaterialTheme.shapes.medium).background(Color(0xFF1A1A2E)), contentAlignment = Alignment.Center) {
                AsyncImage(model = artist.channel?.image ?: "", contentDescription = artist.name, modifier = Modifier.fillMaxSize(), contentScale = ContentScale.Crop)
            }
            Spacer(Modifier.height(8.dp))
            Text(artist.name ?: "", fontSize = 13.sp, fontWeight = FontWeight.Medium, color = Color(0xFFF1F5F9), maxLines = 1, overflow = TextOverflow.Ellipsis)
            Text("${artist.flag ?: ""} ${artist.country ?: ""}", fontSize = 11.sp, color = Color(0xFF64748B), maxLines = 1)
            Text("${artist.songCount ?: 0} songs", fontSize = 10.sp, color = Color(0xFFA78BFA))
        }
    }
}

@Composable
fun TopArtistCard(artist: DiscoveryArtist, rank: Int) {
    val rankColor = when (rank) { 1 -> Color(0xFFF59E0B); 2 -> Color(0xFF94A3B8); 3 -> Color(0xFFCD7F32); else -> Color(0xFF475569) }

    Card(
        modifier = Modifier.width(170.dp).padding(8.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF111128)),
        shape = MaterialTheme.shapes.medium
    ) {
        Row(modifier = Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
            Text("#$rank", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = rankColor)
            Spacer(Modifier.width(10.dp))
            Box(modifier = Modifier.size(44.dp).clip(MaterialTheme.shapes.small).background(Color(0xFF1A1A2E)), contentAlignment = Alignment.Center) {
                AsyncImage(model = artist.channel?.image ?: "", contentDescription = artist.name, modifier = Modifier.fillMaxSize(), contentScale = ContentScale.Crop)
            }
            Spacer(Modifier.width(8.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(artist.name ?: "", fontSize = 12.sp, fontWeight = FontWeight.Medium, color = Color(0xFFF1F5F9), maxLines = 1, overflow = TextOverflow.Ellipsis)
                Text("${artist.flag ?: ""} ${artist.country ?: ""}", fontSize = 10.sp, color = Color(0xFF64748B))
                Text("${artist.songCount ?: 0} songs", fontSize = 9.sp, color = Color(0xFFA78BFA))
            }
        }
    }
}

@Composable
fun QuickChip(label: String, onClick: () -> Unit) {
    Surface(
        onClick = onClick, shape = MaterialTheme.shapes.medium,
        color = Color(0xFF1A1A2E)
    ) {
        Text(label, modifier = Modifier.padding(horizontal = 20.dp, vertical = 12.dp), fontSize = 14.sp, color = Color(0xFFE2E8F0))
    }
}

fun formatCount(count: Long): String = when {
    count >= 1_000_000 -> "${count / 1_000_000}M"
    count >= 1_000 -> "${count / 1_000}K"
    else -> count.toString()
}

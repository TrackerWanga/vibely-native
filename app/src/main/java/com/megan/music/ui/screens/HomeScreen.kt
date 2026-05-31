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
import androidx.compose.material.icons.filled.MusicNote
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
import com.megan.music.data.api.MeganSong
import com.megan.music.data.api.Artist as DiscoveryArtist
import com.megan.music.data.api.HomepageResponse

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(navController: NavController, viewModel: HomeViewModel = hiltViewModel()) {
    val trending by viewModel.trending.collectAsState()
    val homepage by viewModel.homepage.collectAsState()
    val loading by viewModel.loading.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("🎵 Megan Music", fontWeight = FontWeight.Bold) },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.surface),
                actions = {
                    IconButton(onClick = { navController.navigate("search") }) {
                        Icon(Icons.Default.Search, "Search")
                    }
                }
            )
        }
    ) { padding ->
        if (loading) {
            Box(modifier = Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    CircularProgressIndicator()
                    Spacer(Modifier.height(16.dp))
                    Text("Loading...", color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f))
                }
            }
        } else {
            Column(
                modifier = Modifier.fillMaxSize().padding(padding).verticalScroll(rememberScrollState())
            ) {
                // Banner
                val bannerArtist = homepage?.banner?.firstOrNull()
                if (bannerArtist != null) {
                    BannerCard(bannerArtist)
                }

                // Quick actions
                Row(
                    modifier = Modifier.fillMaxWidth().padding(12.dp),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    QuickChip("🙏 Gospel") { }
                    QuickChip("🌟 Beloved") { }
                    QuickChip("💾 Offline") { navController.navigate("offline") }
                }

                // YouTube Trending
                if (trending.isNotEmpty()) {
                    SectionTitle("🔥 Trending Now")
                    LazyRow(modifier = Modifier.fillMaxWidth()) {
                        items(trending) { song ->
                            YouTubeCard(song) {
                                navController.navigate("player")
                            }
                        }
                    }
                }

                // Discovery Featured Artists
                val discoveryArtists = homepage?.trending
                if (discoveryArtists != null && discoveryArtists.isNotEmpty()) {
                    SectionTitle("🎤 Featured Artists")
                    LazyRow(modifier = Modifier.fillMaxWidth()) {
                        items(discoveryArtists.take(20)) { artist ->
                            ArtistMiniCard(artist)
                        }
                    }
                }

                // Top Artists
                val topArtists = homepage?.topArtists
                if (topArtists != null && topArtists.isNotEmpty()) {
                    SectionTitle("🏆 Top Artists")
                    LazyRow(modifier = Modifier.fillMaxWidth()) {
                        items(topArtists.take(10)) { artist ->
                            TopArtistRow(artist, topArtists.indexOf(artist) + 1)
                        }
                    }
                }

                Spacer(Modifier.height(32.dp))
            }
        }
    }
}

@Composable
fun SectionTitle(title: String) {
    Text(
        title, fontSize = 20.sp, fontWeight = FontWeight.Bold,
        modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
    )
}

@Composable
fun BannerCard(artist: DiscoveryArtist) {
    Card(
        modifier = Modifier.fillMaxWidth().height(200.dp).padding(8.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            AsyncImage(
                model = artist.channel?.image ?: "",
                contentDescription = artist.name,
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop
            )
            Column(
                modifier = Modifier.align(Alignment.BottomStart).padding(16.dp)
                    .background(Color.Black.copy(alpha = 0.5f))
                    .padding(8.dp)
            ) {
                Text("${artist.flag ?: ""} ${artist.country ?: ""}", color = Color.White, fontSize = 12.sp)
                Text(artist.name ?: "", color = Color.White, fontSize = 24.sp, fontWeight = FontWeight.Bold)
            }
        }
    }
}

@Composable
fun YouTubeCard(song: MeganSong, onClick: () -> Unit) {
    Card(
        modifier = Modifier.width(180.dp).padding(8.dp).clickable(onClick = onClick),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
    ) {
        Column {
            AsyncImage(
                model = song.thumbnail ?: "",
                contentDescription = song.title,
                modifier = Modifier.fillMaxWidth().height(110.dp),
                contentScale = ContentScale.Crop
            )
            Text(
                song.title?.take(40) ?: "", fontSize = 12.sp, fontWeight = FontWeight.Medium,
                maxLines = 2, overflow = TextOverflow.Ellipsis,
                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
            )
            Text(
                song.author ?: "", fontSize = 11.sp,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                modifier = Modifier.padding(horizontal = 8.dp).padding(bottom = 8.dp),
                maxLines = 1, overflow = TextOverflow.Ellipsis
            )
        }
    }
}

@Composable
fun ArtistMiniCard(artist: DiscoveryArtist) {
    Card(
        modifier = Modifier.width(140.dp).padding(8.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
    ) {
        Column(modifier = Modifier.padding(12.dp), horizontalAlignment = Alignment.CenterHorizontally) {
            Box(
                modifier = Modifier.size(64.dp).clip(MaterialTheme.shapes.medium)
                    .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.2f)),
                contentAlignment = Alignment.Center
            ) {
                AsyncImage(
                    model = artist.channel?.image ?: "",
                    contentDescription = artist.name,
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )
            }
            Spacer(Modifier.height(8.dp))
            Text(artist.name ?: "", fontSize = 12.sp, fontWeight = FontWeight.Medium, maxLines = 1, overflow = TextOverflow.Ellipsis)
            Text("${artist.flag ?: ""} ${artist.country ?: ""}", fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f))
        }
    }
}

@Composable
fun TopArtistRow(artist: DiscoveryArtist, rank: Int) {
    Card(
        modifier = Modifier.width(160.dp).padding(8.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
    ) {
        Row(modifier = Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
            Text("#$rank", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
            Spacer(Modifier.width(8.dp))
            Box(
                modifier = Modifier.size(40.dp).clip(MaterialTheme.shapes.small)
                    .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.2f)),
                contentAlignment = Alignment.Center
            ) {
                AsyncImage(model = artist.channel?.image ?: "", contentDescription = artist.name, modifier = Modifier.fillMaxSize(), contentScale = ContentScale.Crop)
            }
            Spacer(Modifier.width(8.dp))
            Column {
                Text(artist.name ?: "", fontSize = 12.sp, fontWeight = FontWeight.Medium, maxLines = 1, overflow = TextOverflow.Ellipsis)
                Text("${artist.flag ?: ""} ${artist.country ?: ""}", fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f))
            }
        }
    }
}

@Composable
fun QuickChip(label: String, onClick: () -> Unit) {
    Surface(
        onClick = onClick, shape = MaterialTheme.shapes.large,
        color = MaterialTheme.colorScheme.primaryContainer
    ) {
        Text(label, modifier = Modifier.padding(horizontal = 16.dp, vertical = 10.dp), fontSize = 13.sp)
    }
}

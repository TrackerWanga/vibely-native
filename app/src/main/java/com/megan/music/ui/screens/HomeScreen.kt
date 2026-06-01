package com.megan.music.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Search
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
import com.megan.music.data.api.Country
import com.megan.music.data.api.MeganSong
import com.megan.music.data.api.Artist as DiscoveryArtist
import com.megan.music.util.formatCount
import kotlinx.coroutines.delay

val DarkBg = Color(0xFF06060E)
val CardBg = Color(0xFF111128)
val Accent = Color(0xFF7C3AED)
val AccentLight = Color(0xFFA78BFA)
val White = Color(0xFFF1F5F9)
val Gray = Color(0xFF94A3B8)
val Muted = Color(0xFF64748B)
val PlaceholderBg = Color(0xFF1A1A2E)
val GospelGreen = Color(0xFF10B981)
val BelovedGold = Color(0xFFF59E0B)

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
        if (banners.size > 1) while (true) { delay(5000); bannerIndex = (bannerIndex + 1) % banners.size }
    }

    val listState = rememberLazyListState()
    val shouldLoadMore = remember { derivedStateOf { listState.layoutInfo.visibleItemsInfo.lastOrNull()?.index ?: 0 >= listState.layoutInfo.totalItemsCount - 4 } }
    LaunchedEffect(shouldLoadMore.value) { if (shouldLoadMore.value) viewModel.loadMoreCountries() }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("🎵 Megan Music", fontWeight = FontWeight.Bold, color = White) },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = DarkBg),
                actions = { IconButton(onClick = { navController.navigate("search") }) { Icon(Icons.Default.Search, "Search", tint = AccentLight) } }
            )
        }
    ) { padding ->
        if (loading) {
            Box(modifier = Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    CircularProgressIndicator(color = Accent)
                    Spacer(Modifier.height(16.dp))
                    Text("Discovering music...", color = Muted)
                }
            }
        } else {
            LazyColumn(modifier = Modifier.fillMaxSize().padding(padding), state = listState) {
                if (banners.isNotEmpty()) {
                    val artist = banners[bannerIndex]
                    item {
                        Box(modifier = Modifier.fillMaxWidth().height(240.dp).padding(8.dp).clip(MaterialTheme.shapes.large)) {
                            AsyncImage(model = artist.channel?.image ?: "", contentDescription = artist.name, modifier = Modifier.fillMaxSize(), contentScale = ContentScale.Crop)
                            Box(modifier = Modifier.fillMaxSize().background(Brush.verticalGradient(listOf(Color.Transparent, DarkBg.copy(alpha = 0.95f)))))
                            Column(modifier = Modifier.align(Alignment.BottomStart).padding(20.dp)) {
                                Text("${artist.flag ?: ""} ${artist.country ?: ""}", color = AccentLight, fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
                                Text(artist.name ?: "", color = White, fontSize = 28.sp, fontWeight = FontWeight.Bold, maxLines = 1)
                                Text("${formatCount(artist.channel?.subscribers ?: 0)} subscribers", color = Gray, fontSize = 13.sp)
                                if (artist.topSongs?.isNotEmpty() == true) {
                                    Spacer(Modifier.height(10.dp))
                                    Surface(onClick = { navController.navigate("player") }, color = Accent, shape = MaterialTheme.shapes.medium) {
                                        Row(modifier = Modifier.padding(horizontal = 20.dp, vertical = 10.dp), verticalAlignment = Alignment.CenterVertically) {
                                            Icon(Icons.Filled.PlayArrow, "Play", tint = White, modifier = Modifier.size(20.dp))
                                            Spacer(Modifier.width(6.dp))
                                            Text("Play Audio", color = White, fontSize = 14.sp, fontWeight = FontWeight.Medium)
                                        }
                                    }
                                }
                            }
                        }
                        if (banners.size > 1) {
                            Row(modifier = Modifier.fillMaxWidth().padding(vertical = 6.dp), horizontalArrangement = Arrangement.Center) {
                                banners.forEachIndexed { i, _ ->
                                    Box(modifier = Modifier.padding(4.dp).size(if (i == bannerIndex) 10.dp else 8.dp).clip(MaterialTheme.shapes.small).background(if (i == bannerIndex) AccentLight else Muted))
                                }
                            }
                        }
                    }
                }

                item {
                    Row(modifier = Modifier.fillMaxWidth().padding(horizontal = 12.dp, vertical = 12.dp), horizontalArrangement = Arrangement.SpaceEvenly) {
                        QuickChip("🙏 Gospel", GospelGreen) { navController.navigate("gospel") }
                        QuickChip("🌟 Beloved", BelovedGold) { navController.navigate("beloved") }
                        QuickChip("💾 Offline", Accent) { navController.navigate("offline") }
                    }
                }

                if (trending.isNotEmpty()) {
                    item { SectionTitle("🔥 Discover", "Trending now") }
                    item { LazyRow(contentPadding = PaddingValues(horizontal = 8.dp)) { items(trending) { s -> YouTubeCard(s) { navController.navigate("player") } } } }
                }

                val artists = homepage?.trending
                if (artists != null && artists.isNotEmpty()) {
                    item { SectionTitle("🎤 Featured Artists", "${artists.size} artists") }
                    item { LazyRow(contentPadding = PaddingValues(horizontal = 8.dp)) { items(artists.take(20)) { a -> ArtistCard(a) { navController.navigate("artist/" + java.net.URLEncoder.encode(a.name ?: "", "UTF-8")) } } } }
                }

                val tops = homepage?.topArtists
                if (tops != null && tops.isNotEmpty()) {
                    item { SectionTitle("🏆 Top Artists", "Global rankings") }
                    item { LazyRow(contentPadding = PaddingValues(horizontal = 8.dp)) { items(tops.take(10)) { a -> TopArtistCard(a, tops.indexOf(a) + 1) { navController.navigate("artist/" + java.net.URLEncoder.encode(a.name ?: "", "UTF-8")) } } } }
                }

                items(visibleCountries.size) { i ->
                    val country = visibleCountries[i]
                    val songs = countrySongs[country.code?.takeIf { it.isNotBlank() } ?: country.name ?: ""] ?: emptyList()
                    CountrySection(country, songs) { navController.navigate("player") }
                }

                item { Spacer(Modifier.height(120.dp)) }
            }
        }
    }
}

@Composable
fun SectionTitle(title: String, subtitle: String? = null) {
    Column(modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 10.dp)) {
        Text(title, fontSize = 20.sp, fontWeight = FontWeight.Bold, color = White)
        if (subtitle != null) Text(subtitle, fontSize = 12.sp, color = Muted)
    }
}

@Composable
fun CountrySection(country: Country, songs: List<MeganSong>, onClick: () -> Unit) {
    Column(modifier = Modifier.padding(vertical = 4.dp)) {
        Row(modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 10.dp), verticalAlignment = Alignment.CenterVertically) {
            Text(country.flag ?: "🌍", fontSize = 28.sp)
            Spacer(Modifier.width(10.dp))
            Text(country.name ?: "", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = White)
            Spacer(Modifier.weight(1f))
            Text("${country.totalArtists ?: 0} artists", fontSize = 12.sp, color = Muted)
            if (songs.isNotEmpty()) {
                Spacer(Modifier.width(8.dp))
                Text("Play All", fontSize = 12.sp, color = AccentLight)
            }
        }
        if (songs.isNotEmpty()) {
            LazyRow(contentPadding = PaddingValues(horizontal = 8.dp)) {
                items(songs.take(16)) { song -> YouTubeCard(song, onClick) }
            }
        } else {
            Box(modifier = Modifier.fillMaxWidth().height(130.dp).padding(horizontal = 8.dp).clip(MaterialTheme.shapes.medium).background(CardBg), contentAlignment = Alignment.Center) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    CircularProgressIndicator(color = Accent, modifier = Modifier.size(18.dp), strokeWidth = 2.dp)
                    Spacer(Modifier.width(8.dp))
                    Text("Loading songs...", color = Muted, fontSize = 13.sp)
                }
            }
        }
    }
}

@Composable
fun YouTubeCard(song: MeganSong, onClick: () -> Unit) {
    Card(modifier = Modifier.width(190.dp).padding(6.dp).clickable(onClick = onClick), colors = CardDefaults.cardColors(containerColor = CardBg), shape = MaterialTheme.shapes.medium) {
        Column {
            Box(modifier = Modifier.fillMaxWidth().height(115.dp).clip(MaterialTheme.shapes.medium)) {
                AsyncImage(model = song.thumbnail ?: "", contentDescription = song.title, modifier = Modifier.fillMaxSize(), contentScale = ContentScale.Crop)
                if (song.duration != null) {
                    Box(modifier = Modifier.align(Alignment.BottomEnd).padding(4.dp).background(Color.Black.copy(alpha = 0.75f), MaterialTheme.shapes.small).padding(horizontal = 6.dp, vertical = 2.dp)) {
                        Text(song.duration, color = White, fontSize = 10.sp)
                    }
                }
            }
            Column(modifier = Modifier.padding(horizontal = 10.dp, vertical = 8.dp)) {
                Text(song.title?.take(45) ?: "", fontSize = 12.sp, fontWeight = FontWeight.Medium, color = White, maxLines = 2, overflow = TextOverflow.Ellipsis, lineHeight = 17.sp, minLines = 1)
                Spacer(Modifier.height(4.dp))
                if (song.author != null) Text(song.author, fontSize = 11.sp, color = AccentLight, maxLines = 1, overflow = TextOverflow.Ellipsis)
                Text("${formatCount(song.views ?: 0)} views", fontSize = 10.sp, color = Muted)
            }
        }
    }
}

@Composable
fun ArtistCard(artist: DiscoveryArtist, onClick: () -> Unit) {
    Card(modifier = Modifier.width(155.dp).padding(6.dp).clickable(onClick = onClick), colors = CardDefaults.cardColors(containerColor = CardBg), shape = MaterialTheme.shapes.medium) {
        Column(modifier = Modifier.padding(14.dp), horizontalAlignment = Alignment.CenterHorizontally) {
            Box(modifier = Modifier.size(76.dp).clip(MaterialTheme.shapes.medium).background(PlaceholderBg), contentAlignment = Alignment.Center) {
                AsyncImage(model = artist.channel?.image ?: "", contentDescription = artist.name, modifier = Modifier.fillMaxSize(), contentScale = ContentScale.Crop)
            }
            Spacer(Modifier.height(10.dp))
            Text(artist.name ?: "", fontSize = 13.sp, fontWeight = FontWeight.Medium, color = White, maxLines = 1, overflow = TextOverflow.Ellipsis)
            Text("${artist.flag ?: ""} ${artist.country ?: ""}", fontSize = 11.sp, color = Muted, maxLines = 1)
            Text("${artist.songCount ?: 0} songs", fontSize = 10.sp, color = AccentLight)
        }
    }
}

@Composable
fun TopArtistCard(artist: DiscoveryArtist, rank: Int, onClick: () -> Unit) {
    val rankColor = when (rank) { 1 -> BelovedGold; 2 -> Color(0xFF94A3B8); 3 -> Color(0xFFCD7F32); else -> Muted }
    Card(modifier = Modifier.width(185.dp).padding(6.dp).clickable(onClick = onClick), colors = CardDefaults.cardColors(containerColor = CardBg), shape = MaterialTheme.shapes.medium) {
        Row(modifier = Modifier.padding(14.dp), verticalAlignment = Alignment.CenterVertically) {
            Text("#$rank", fontSize = 22.sp, fontWeight = FontWeight.Bold, color = rankColor)
            Spacer(Modifier.width(10.dp))
            Box(modifier = Modifier.size(46.dp).clip(MaterialTheme.shapes.small).background(PlaceholderBg), contentAlignment = Alignment.Center) {
                AsyncImage(model = artist.channel?.image ?: "", contentDescription = artist.name, modifier = Modifier.fillMaxSize(), contentScale = ContentScale.Crop)
            }
            Spacer(Modifier.width(10.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(artist.name ?: "", fontSize = 13.sp, fontWeight = FontWeight.Medium, color = White, maxLines = 1, overflow = TextOverflow.Ellipsis)
                Text("${artist.flag ?: ""} ${artist.country ?: ""}", fontSize = 10.sp, color = Muted)
                Text("${artist.songCount ?: 0} songs", fontSize = 9.sp, color = AccentLight)
            }
        }
    }
}

@Composable
fun QuickChip(label: String, color: Color, onClick: () -> Unit) {
    Surface(onClick = onClick, shape = MaterialTheme.shapes.medium, color = color.copy(alpha = 0.15f)) {
        Text(label, modifier = Modifier.padding(horizontal = 20.dp, vertical = 12.dp), fontSize = 14.sp, color = color, fontWeight = FontWeight.Medium)
    }
}

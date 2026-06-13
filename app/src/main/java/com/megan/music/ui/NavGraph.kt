package com.megan.music.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.megan.music.data.PlayerManager
import com.megan.music.data.PlayerState
import com.megan.music.ui.screens.*

@Composable
fun NavGraph() {
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route
    val context = LocalContext.current

    // Player state for floating footer
    val title by PlayerState.currentTitle.collectAsState()
    val artist by PlayerState.currentArtist.collectAsState()
    val isPlaying by PlayerState.isPlaying.collectAsState()
    val showPlayerBar = title != "Select a song"

    Scaffold(
        bottomBar = {
            Column {
                // Floating player footer
                if (showPlayerBar && currentRoute != "player") {
                    Surface(
                        onClick = { navController.navigate("player") },
                        color = Color(0xFF111128),
                        shape = RoundedCornerShape(topStart = 12.dp, topEnd = 12.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier.padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(Icons.Filled.MusicNote, null, tint = Color(0xFF7C3AED), modifier = Modifier.size(36.dp))
                            Spacer(Modifier.width(12.dp))
                            Column(Modifier.weight(1f)) {
                                Text(title, color = Color.White, fontSize = 13.sp, fontWeight = FontWeight.Medium, maxLines = 1, overflow = TextOverflow.Ellipsis)
                                Text(artist, color = Color(0xFF64748B), fontSize = 11.sp)
                            }
                            IconButton(onClick = { PlayerManager.toggle() }) {
                                Icon(if (isPlaying) Icons.Filled.Pause else Icons.Filled.PlayArrow, if (isPlaying) "Pause" else "Play", tint = Color(0xFFA78BFA), modifier = Modifier.size(28.dp))
                            }
                        }
                    }
                }

                // Bottom navigation
                NavigationBar(containerColor = Color(0xFF0A0A18)) {
                    NavigationBarItem(
                        icon = { Icon(Icons.Filled.Home, "Home") },
                        label = { Text("Home") },
                        selected = currentRoute == "home",
                        onClick = { navController.navigate("home") { popUpTo("home") { inclusive = true } } },
                        colors = navColors()
                    )
                    NavigationBarItem(
                        icon = { Icon(Icons.Filled.Search, "Search") },
                        label = { Text("Search") },
                        selected = currentRoute == "search",
                        onClick = { navController.navigate("search") },
                        colors = navColors()
                    )
                    NavigationBarItem(
                        icon = { Icon(Icons.Filled.MusicNote, "Offline") },
                        label = { Text("Offline") },
                        selected = currentRoute == "offline",
                        onClick = { navController.navigate("offline") },
                        colors = navColors()
                    )
                    NavigationBarItem(
                        icon = { Icon(Icons.Filled.Person, "Account") },
                        label = { Text("Account") },
                        selected = currentRoute == "auth",
                        onClick = { navController.navigate("auth") },
                        colors = navColors()
                    )
                }
            }
        }
    ) { padding ->
        NavHost(navController = navController, startDestination = "home", modifier = Modifier.padding(padding)) {
            composable("home") { HomeScreen(navController) }
            composable("search") { SearchScreen(navController) }
            composable("player") { PlayerScreen(navController) }
            composable("offline") { OfflineScreen(navController) }
            composable("gospel") { GospelScreen(navController) }
            composable("beloved") { BelovedScreen(navController) }
            composable("auth") { AuthScreen(navController) }
            composable(
                "artist/{name}",
                arguments = listOf(navArgument("name") { type = NavType.StringType })
            ) { backStackEntry ->
                val name = backStackEntry.arguments?.getString("name") ?: ""
                ArtistScreen(artistName = name, navController = navController)
            }
        }
    }
}

@Composable
fun navColors() = NavigationBarItemDefaults.colors(
    selectedIconColor = Color(0xFFA78BFA),
    selectedTextColor = Color(0xFFA78BFA),
    unselectedIconColor = Color(0xFF64748B),
    unselectedTextColor = Color(0xFF64748B),
    indicatorColor = Color(0xFF1A1A2E)
)

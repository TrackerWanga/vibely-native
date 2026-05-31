package com.megan.music.ui

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.megan.music.ui.screens.HomeScreen
import com.megan.music.ui.screens.PlayerScreen
import com.megan.music.ui.screens.SearchScreen
import com.megan.music.ui.screens.OfflineScreen

@Composable
fun NavGraph() {
    val navController = rememberNavController()
    NavHost(navController = navController, startDestination = "home") {
        composable("home") { HomeScreen(navController) }
        composable("search") { SearchScreen(navController) }
        composable("player") { PlayerScreen(navController) }
        composable("offline") { OfflineScreen(navController) }
    }
}

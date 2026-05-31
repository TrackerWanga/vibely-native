package com.megan.music.ui

import androidx.compose.runtime.Composable
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.megan.music.ui.screens.*

@Composable
fun NavGraph() {
    val navController = rememberNavController()
    NavHost(navController = navController, startDestination = "home") {
        composable("home") { HomeScreen(navController) }
        composable("search") { SearchScreen(navController) }
        composable("player") { PlayerScreen(navController) }
        composable("offline") { OfflineScreen(navController) }
        composable("gospel") { GospelScreen(navController) }
        composable("beloved") { BelovedScreen(navController) }
        composable(
            "artist/{name}",
            arguments = listOf(navArgument("name") { type = NavType.StringType })
        ) { backStackEntry ->
            val name = backStackEntry.arguments?.getString("name") ?: ""
            ArtistScreen(artistName = name, navController = navController)
        }
    }
}

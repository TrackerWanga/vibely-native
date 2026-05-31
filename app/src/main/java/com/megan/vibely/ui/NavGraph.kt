package com.megan.vibely.ui

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.megan.vibely.ui.screens.HomeScreen
import com.megan.vibely.ui.screens.PlayerScreen
import com.megan.vibely.ui.screens.SearchScreen
import com.megan.vibely.ui.screens.OfflineScreen

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

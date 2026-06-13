package com.megan.music.ui.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector

data class NavItem(val label: String, val icon: ImageVector, val route: String)

@Composable
fun BottomNavBar(currentRoute: String?, onNavigate: (String) -> Unit) {
    val items = listOf(
        NavItem("Home", Icons.Filled.Home, "home"),
        NavItem("Search", Icons.Filled.Search, "search"),
        NavItem("Offline", Icons.Filled.MusicNote, "offline"),
        NavItem("Account", Icons.Filled.Person, "auth"),
    )

    NavigationBar(containerColor = Color(0xFF0A0A18)) {
        items.forEach { item ->
            NavigationBarItem(
                icon = { Icon(item.icon, contentDescription = item.label) },
                label = { Text(item.label) },
                selected = currentRoute == item.route,
                onClick = { onNavigate(item.route) },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = Color(0xFFA78BFA),
                    selectedTextColor = Color(0xFFA78BFA),
                    unselectedIconColor = Color(0xFF64748B),
                    unselectedTextColor = Color(0xFF64748B),
                    indicatorColor = Color(0xFF1A1A2E)
                )
            )
        }
    }
}

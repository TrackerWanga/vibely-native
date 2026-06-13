package com.megan.music.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.AsyncImage

@Composable
fun MenuDrawer(
    isOpen: Boolean,
    onClose: () -> Unit,
    navController: NavController,
    isSignedIn: Boolean = false,
    onSignOut: () -> Unit = {}
) {
    if (!isOpen) return

    Box(modifier = Modifier.fillMaxSize()) {
        // Backdrop
        Box(modifier = Modifier.fillMaxSize().background(Color.Black.copy(alpha = 0.5f)).clickable(onClick = onClose))

        // Drawer
        Column(
            modifier = Modifier.fillMaxHeight().width(290.dp).background(Color(0xFF0A0A18)).padding(20.dp).align(Alignment.CenterEnd)
        ) {
            // Header
            Row(verticalAlignment = Alignment.CenterVertically) {
                AsyncImage(
                    model = "https://files.catbox.moe/r1rptl.png",
                    contentDescription = "Profile",
                    modifier = Modifier.size(48.dp).clip(CircleShape)
                )
                Spacer(Modifier.width(12.dp))
                Column {
                    Text(if (isSignedIn) "User" else "Sign In", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                    Text("Megan Music", color = Color(0xFF64748B), fontSize = 12.sp)
                }
            }

            Spacer(Modifier.height(24.dp))

            // Menu items
            val menuItems = listOf(
                "Home" to Icons.Filled.Home,
                "Search" to Icons.Filled.Search,
                "Gospel" to Icons.Filled.MusicNote,
                "Beloved" to Icons.Filled.Favorite,
                "Offline Library" to Icons.Filled.OfflineBolt,
                "Docs & Help" to Icons.Filled.MenuBook,
                "Sign In" to Icons.Filled.Login,
            )

            menuItems.forEach { (label, icon) ->
                Row(
                    modifier = Modifier.fillMaxWidth().clickable {
                        onClose()
                        when (label) {
                            "Home" -> navController.navigate("home")
                            "Search" -> navController.navigate("search")
                            "Gospel" -> navController.navigate("gospel")
                            "Beloved" -> navController.navigate("beloved")
                            "Offline Library" -> navController.navigate("offline")
                            "Docs & Help" -> navController.navigate("docs")
                            "Sign In" -> navController.navigate("auth")
                        }
                    }.padding(vertical = 14.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(icon, null, tint = Color(0xFFA78BFA), modifier = Modifier.size(20.dp))
                    Spacer(Modifier.width(14.dp))
                    Text(label, color = Color(0xFFE2E8F0), fontSize = 14.sp)
                }
            }

            Spacer(Modifier.weight(1f))

            // Footer
            Text("v1.0.0", color = Color(0xFF475569), fontSize = 11.sp)
            Text("© 2026 Tracker Wanga", color = Color(0xFF475569), fontSize = 11.sp)
        }
    }
}

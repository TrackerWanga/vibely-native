package com.megan.music.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OfflineScreen(navController: NavController) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("💾 Offline Library", color = Color.White) },
                navigationIcon = { IconButton(onClick = { navController.popBackStack() }) { Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back", tint = Color(0xFFA78BFA)) } },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color(0xFF0A0A18))
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier.fillMaxSize().padding(padding),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(Icons.Filled.MusicNote, null, tint = Color(0xFF7C3AED), modifier = Modifier.size(80.dp))
            Spacer(Modifier.height(20.dp))
            Text("Offline Library", fontSize = 22.sp, fontWeight = FontWeight.Bold, color = Color.White)
            Spacer(Modifier.height(8.dp))
            Text("Download songs to play without internet", fontSize = 14.sp, color = Color(0xFF64748B))
            Spacer(Modifier.height(24.dp))
            Text("🎧", fontSize = 40.sp)
            Spacer(Modifier.height(8.dp))
            Text("Coming soon", fontSize = 13.sp, color = Color(0xFF475569))
        }
    }
}

package com.megan.vibely.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchScreen(navController: NavController, viewModel: SearchViewModel = hiltViewModel()) {
    val results by viewModel.results.collectAsState()
    val loading by viewModel.loading.collectAsState()
    var query by remember { mutableStateOf("") }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { TextField(value = query, onValueChange = { query = it }, placeholder = { Text("Search...") }, singleLine = true, colors = TextFieldDefaults.colors(unfocusedContainerColor = MaterialTheme.colorScheme.surface)) },
                navigationIcon = { IconButton(onClick = { navController.popBackStack() }) { Icon(Icons.Default.ArrowBack, "Back") } },
                actions = { IconButton(onClick = { viewModel.search(query) }) { Icon(Icons.Default.Search, "Search") } }
            )
        }
    ) { padding ->
        LazyColumn(modifier = Modifier.padding(padding)) {
            items(results) { song ->
                ListItem(
                    headlineContent = { Text(song.title.take(50)) },
                    supportingContent = { Text("${song.author ?: ""} • ${song.views?.toString() ?: ""} views") },
                    modifier = Modifier.clickable {
                        viewModel.play(song)
                        navController.navigate("player")
                    }
                )
            }
        }
        if (loading) { CircularProgressIndicator(modifier = Modifier.padding(16.dp)) }
    }
}

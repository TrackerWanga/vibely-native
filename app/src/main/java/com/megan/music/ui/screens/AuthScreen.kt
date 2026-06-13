package com.megan.music.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Email
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.megan.music.data.AuthManager
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AuthScreen(navController: NavController) {
    var isLogin by remember { mutableStateOf(true) }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var loading by remember { mutableStateOf(false) }
    var error by remember { mutableStateOf<String?>(null) }
    val scope = rememberCoroutineScope()

    // If already signed in, show account info
    val user = AuthManager.currentUser

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(if (isLogin) "Sign In" else "Sign Up", color = Color.White) },
                navigationIcon = { IconButton(onClick = { navController.popBackStack() }) { Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back", tint = Color(0xFFA78BFA)) } },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color(0xFF0A0A18))
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier.fillMaxSize().padding(padding).padding(32.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            if (user != null) {
                // Already signed in
                Text("✅", fontSize = 64.sp)
                Spacer(Modifier.height(16.dp))
                Text("Signed in as", color = Color(0xFF64748B), fontSize = 14.sp)
                Text(user.email ?: "", color = Color.White, fontSize = 18.sp, fontWeight = FontWeight.Bold)
                Spacer(Modifier.height(24.dp))
                Button(onClick = { AuthManager.signOut(); navController.popBackStack() }, colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFF43F5E)), modifier = Modifier.fillMaxWidth()) {
                    Text("Sign Out", color = Color.White)
                }
            } else {
                Text("🎵", fontSize = 64.sp)
                Spacer(Modifier.height(16.dp))
                Text("Megan Music", fontSize = 28.sp, fontWeight = FontWeight.Bold, color = Color.White)
                Text("Sign in to unlock lyrics & downloads", fontSize = 14.sp, color = Color(0xFF64748B))

                Spacer(Modifier.height(32.dp))

                OutlinedTextField(
                    value = email, onValueChange = { email = it },
                    label = { Text("Email") },
                    leadingIcon = { Icon(Icons.Filled.Email, null, tint = Color(0xFF7C3AED)) },
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = Color(0xFF7C3AED), unfocusedBorderColor = Color(0xFF1A1A2E), focusedTextColor = Color.White, unfocusedTextColor = Color.White)
                )
                Spacer(Modifier.height(12.dp))
                OutlinedTextField(
                    value = password, onValueChange = { password = it },
                    label = { Text("Password") },
                    visualTransformation = PasswordVisualTransformation(),
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = Color(0xFF7C3AED), unfocusedBorderColor = Color(0xFF1A1A2E), focusedTextColor = Color.White, unfocusedTextColor = Color.White)
                )

                if (error != null) {
                    Spacer(Modifier.height(8.dp))
                    Text(error!!, color = Color(0xFFF43F5E), fontSize = 13.sp)
                }

                Spacer(Modifier.height(24.dp))
                Button(
                    onClick = {
                        scope.launch {
                            loading = true; error = null
                            val result = if (isLogin) AuthManager.signIn(email, password)
                                         else AuthManager.signUp(email, password)
                            result.onSuccess { navController.popBackStack() }
                            result.onFailure { error = it.message }
                            loading = false
                        }
                    },
                    modifier = Modifier.fillMaxWidth().height(50.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF7C3AED)),
                    enabled = !loading && email.isNotBlank() && password.isNotBlank()
                ) {
                    if (loading) CircularProgressIndicator(color = Color.White, modifier = Modifier.size(20.dp))
                    else Text(if (isLogin) "Sign In" else "Create Account", color = Color.White, fontSize = 16.sp)
                }

                Spacer(Modifier.height(16.dp))
                TextButton(onClick = { isLogin = !isLogin; error = null }) {
                    Text(if (isLogin) "Don't have an account? Sign Up" else "Already have an account? Sign In", color = Color(0xFFA78BFA))
                }
            }
        }
    }
}

package com.megan.music.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Person
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
    var name by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var loading by remember { mutableStateOf(false) }
    var error by remember { mutableStateOf<String?>(null) }
    val scope = rememberCoroutineScope()
    val user = AuthManager.currentUser

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(if (user != null) "Account" else if (isLogin) "Sign In" else "Sign Up", color = Color.White) },
                navigationIcon = { IconButton(onClick = { navController.popBackStack() }) { Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back", tint = Color(0xFFA78BFA)) } },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color(0xFF0A0A18))
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier.fillMaxSize().padding(padding).padding(28.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            if (user != null) {
                Text("✅", fontSize = 64.sp)
                Spacer(Modifier.height(12.dp))
                Text("Signed in as", color = Color(0xFF64748B), fontSize = 14.sp)
                Text(AuthManager.userName.ifEmpty { user.email ?: "" }, color = Color.White, fontSize = 18.sp, fontWeight = FontWeight.Bold)
                Text(user.email ?: "", color = Color(0xFF64748B), fontSize = 13.sp)
                Spacer(Modifier.height(28.dp))

                Card(modifier = Modifier.fillMaxWidth(), colors = CardDefaults.cardColors(containerColor = Color(0xFF111128))) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text("🎵 Premium Features", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 15.sp)
                        Spacer(Modifier.height(8.dp))
                        Text("✅ Download songs", color = Color(0xFF10B981), fontSize = 13.sp)
                        Text("✅ Lyrics access", color = Color(0xFF10B981), fontSize = 13.sp)
                        Text("✅ High quality streaming", color = Color(0xFF10B981), fontSize = 13.sp)
                    }
                }

                Spacer(Modifier.height(20.dp))
                Button(onClick = { AuthManager.signOut(); navController.popBackStack() }, colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFF43F5E)), modifier = Modifier.fillMaxWidth().height(48.dp)) {
                    Text("Sign Out", color = Color.White, fontSize = 15.sp)
                }
            } else {
                Text("🎵", fontSize = 56.sp)
                Spacer(Modifier.height(8.dp))
                Text("Megan Music", fontSize = 26.sp, fontWeight = FontWeight.Bold, color = Color.White)
                Text(if (isLogin) "Sign in to your account" else "Create a new account", fontSize = 14.sp, color = Color(0xFF64748B))

                Spacer(Modifier.height(28.dp))

                if (!isLogin) {
                    OutlinedTextField(value = name, onValueChange = { name = it }, label = { Text("Full Name") }, leadingIcon = { Icon(Icons.Filled.Person, null, tint = Color(0xFF7C3AED)) }, modifier = Modifier.fillMaxWidth(), singleLine = true, colors = fieldColors())
                    Spacer(Modifier.height(10.dp))
                }

                OutlinedTextField(value = email, onValueChange = { email = it }, label = { Text("Email") }, leadingIcon = { Icon(Icons.Filled.Email, null, tint = Color(0xFF7C3AED)) }, modifier = Modifier.fillMaxWidth(), singleLine = true, colors = fieldColors())
                Spacer(Modifier.height(10.dp))
                OutlinedTextField(value = password, onValueChange = { password = it }, label = { Text("Password") }, visualTransformation = PasswordVisualTransformation(), modifier = Modifier.fillMaxWidth(), singleLine = true, colors = fieldColors())

                if (!isLogin) {
                    Spacer(Modifier.height(10.dp))
                    OutlinedTextField(value = confirmPassword, onValueChange = { confirmPassword = it }, label = { Text("Confirm Password") }, visualTransformation = PasswordVisualTransformation(), modifier = Modifier.fillMaxWidth(), singleLine = true, colors = fieldColors(), isError = confirmPassword.isNotEmpty() && password != confirmPassword)
                    if (confirmPassword.isNotEmpty() && password != confirmPassword) {
                        Text("Passwords don't match", color = Color(0xFFF43F5E), fontSize = 12.sp, modifier = Modifier.align(Alignment.Start))
                    }
                }

                if (error != null) {
                    Spacer(Modifier.height(8.dp))
                    Text(error!!, color = Color(0xFFF43F5E), fontSize = 13.sp)
                }

                Spacer(Modifier.height(20.dp))
                Button(
                    onClick = {
                        if (!isLogin && password != confirmPassword) { error = "Passwords don't match"; return@Button }
                        scope.launch {
                            loading = true; error = null
                            val result = if (isLogin) AuthManager.signIn(email, password)
                                         else AuthManager.signUp(name, email, password)
                            result.onSuccess { navController.popBackStack() }
                            result.onFailure { error = it.message?.replace("Firebase: ", "") }
                            loading = false
                        }
                    },
                    modifier = Modifier.fillMaxWidth().height(50.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF7C3AED)),
                    enabled = !loading && email.isNotBlank() && password.isNotBlank() && (isLogin || name.isNotBlank())
                ) {
                    if (loading) CircularProgressIndicator(color = Color.White, modifier = Modifier.size(22.dp))
                    else Text(if (isLogin) "Sign In" else "Create Account", color = Color.White, fontSize = 15.sp)
                }

                Spacer(Modifier.height(8.dp))
                // Google Sign-In button (placeholder for now)
                OutlinedButton(
                    onClick = { /* Google Sign-In */ },
                    modifier = Modifier.fillMaxWidth().height(48.dp),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.White),
                    border = ButtonDefaults.outlinedButtonBorder
                ) {
                    Text("G", color = Color(0xFF4285F4), fontSize = 20.sp, fontWeight = FontWeight.Bold)
                    Spacer(Modifier.width(8.dp))
                    Text("Continue with Google", color = Color(0xFF94A3B8), fontSize = 14.sp)
                }

                Spacer(Modifier.height(12.dp))
                TextButton(onClick = { isLogin = !isLogin; error = null; confirmPassword = "" }) {
                    Text(if (isLogin) "Don't have an account? Sign Up" else "Already have an account? Sign In", color = Color(0xFFA78BFA), fontSize = 13.sp)
                }
            }
        }
    }
}

@Composable
fun fieldColors() = OutlinedTextFieldDefaults.colors(
    focusedBorderColor = Color(0xFF7C3AED),
    unfocusedBorderColor = Color(0xFF1A1A2E),
    focusedTextColor = Color.White,
    unfocusedTextColor = Color.White,
    focusedLabelColor = Color(0xFFA78BFA),
    unfocusedLabelColor = Color(0xFF64748B)
)

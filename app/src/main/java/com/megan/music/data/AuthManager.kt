package com.megan.music.data

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

object AuthManager {
    private val auth = FirebaseAuth.getInstance()
    private val db = FirebaseFirestore.getInstance()

    val currentUser: FirebaseUser? get() = auth.currentUser
    val isSignedIn: Boolean get() = auth.currentUser != null
    val userName: String get() = auth.currentUser?.displayName ?: ""

    suspend fun signUp(name: String, email: String, password: String): Result<FirebaseUser> {
        return try {
            val result = auth.createUserWithEmailAndPassword(email, password).await()
            result.user?.let { user ->
                // Save profile to Firestore
                db.collection("users").document(user.uid).set(mapOf(
                    "name" to name,
                    "email" to email,
                    "createdAt" to System.currentTimeMillis()
                )).await()
            }
            Result.success(result.user!!)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun signIn(email: String, password: String): Result<FirebaseUser> {
        return try {
            val result = auth.signInWithEmailAndPassword(email, password).await()
            Result.success(result.user!!)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun signInWithGoogle(idToken: String): Result<FirebaseUser> {
        return try {
            val credential = GoogleAuthProvider.getCredential(idToken, null)
            val result = auth.signInWithCredential(credential).await()
            // Save profile if new user
            result.user?.let { user ->
                val doc = db.collection("users").document(user.uid).get().await()
                if (!doc.exists()) {
                    db.collection("users").document(user.uid).set(mapOf(
                        "name" to (user.displayName ?: ""),
                        "email" to (user.email ?: ""),
                        "createdAt" to System.currentTimeMillis()
                    )).await()
                }
            }
            Result.success(result.user!!)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    fun signOut() {
        auth.signOut()
    }
}

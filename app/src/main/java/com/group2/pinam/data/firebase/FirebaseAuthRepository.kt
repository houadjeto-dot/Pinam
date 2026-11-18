package com.group2.pinam.data.firebase

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthException
import com.google.firebase.firestore.FirebaseFirestore
import com.group2.pinam.data.AuthRepository
import com.group2.pinam.model.User
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class FirebaseAuthRepository : AuthRepository {
    private val auth = FirebaseAuth.getInstance()
    private val db = FirebaseFirestore.getInstance()

    private val _user = MutableStateFlow<User?>(null)
    override val currentUser: StateFlow<User?> = _user.asStateFlow()

    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Main)

    init {
        val firebaseUser = auth.currentUser
        if (firebaseUser != null) {
            scope.launch {
                try {
                    val doc = db.collection("users").document(firebaseUser.uid).get().await()
                    val restoredUser = doc.toObject(User::class.java) ?: User(
                        id = firebaseUser.uid,
                        name = firebaseUser.displayName ?: firebaseUser.email?.substringBefore("@") ?: "User",
                        email = firebaseUser.email ?: ""
                    )
                    _user.value = restoredUser
                } catch (_: Exception) {
                    _user.value = User(
                        id = firebaseUser.uid,
                        name = firebaseUser.displayName ?: firebaseUser.email?.substringBefore("@") ?: "User",
                        email = firebaseUser.email ?: ""
                    )
                }
            }
        }
    }

    override suspend fun signUp(name: String, email: String, password: String): Result<User> {
        return runCatching {
            val authResult = auth.createUserWithEmailAndPassword(email, password).await()
            val uid = authResult.user?.uid ?: throw IllegalStateException("User creation failed")
            val user = User(id = uid, name = name, email = email)
            db.collection("users").document(uid).set(user).await()
            _user.value = user
            user
        }.mapCatching { it }.recoverCatching { throw mapAuthException(it) }
    }

    override suspend fun signIn(email: String, password: String): Result<User> {
        return runCatching {
            val authResult = auth.signInWithEmailAndPassword(email, password).await()
            val uid = authResult.user?.uid ?: throw IllegalStateException("Sign in failed")
            val doc = db.collection("users").document(uid).get().await()
            val user = doc.toObject(User::class.java) ?: User(
                id = uid,
                name = auth.currentUser?.displayName ?: email.substringBefore("@"),
                email = email
            )
            _user.value = user
            user
        }.mapCatching { it }.recoverCatching { throw mapAuthException(it) }
    }

    override fun signOut() {
        try {
            auth.signOut()
        } catch (_: Exception) {}
        _user.value = null
    }

    private fun mapAuthException(e: Throwable): Throwable {
        if (e is FirebaseAuthException) {
            val message = when (e.errorCode) {
                "ERROR_INVALID_EMAIL" -> "Invalid email address."
                "ERROR_WRONG_PASSWORD" -> "Wrong password."
                "ERROR_USER_NOT_FOUND" -> "No account with that email."
                "ERROR_USER_DISABLED" -> "User account has been disabled."
                "ERROR_TOO_MANY_REQUESTS" -> "Too many requests. Try again later."
                "ERROR_EMAIL_ALREADY_IN_USE" -> "Email already in use."
                "ERROR_WEAK_PASSWORD" -> "Password is too weak."
                "ERROR_NETWORK_REQUEST_FAILED" -> "Network error. Check your connection."
                else -> e.localizedMessage ?: e.message ?: "Authentication failed."
            }
            return Exception(message)
        }
        return Exception(e.localizedMessage ?: e.message ?: "Authentication failed.")
    }
}

package com.group2.pinam.data.firebase

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.group2.pinam.data.AuthRepository
import com.group2.pinam.model.User
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class FirebaseAuthRepository : AuthRepository {
    private val auth = FirebaseAuth.getInstance()
    private val db = FirebaseFirestore.getInstance()
    private val _user = MutableStateFlow<User?>(null)
    override val currentUser: StateFlow<User?> = _user

    init {
        val firebaseUser = auth.currentUser
        if (firebaseUser != null) {
            CoroutineScope(Dispatchers.IO).launch {
                try {
                    val doc = db.collection("users").document(firebaseUser.uid).get().await()
                    val user = doc.toObject(User::class.java)?.copy(id = firebaseUser.uid)
                        ?: User(
                            id = firebaseUser.uid,
                            name = firebaseUser.displayName ?: "",
                            email = firebaseUser.email ?: "",
                        )
                    _user.value = user
                } catch (_: Exception) {
                    _user.value = User(
                        id = firebaseUser.uid,
                        name = firebaseUser.displayName ?: "",
                        email = firebaseUser.email ?: ""
                    )
                }
            }
        }
    }

    override suspend fun signUp(name: String, email: String, password: String): Result<User> = runCatching {
        val authResult = auth.createUserWithEmailAndPassword(email, password).await()
        val uid = authResult.user?.uid ?: auth.currentUser?.uid ?: throw IllegalStateException("User creation failed")
        val user = User(id = uid, name = name, email = email)
        db.collection("users").document(uid).set(user).await()
        _user.value = user
        user
    }

    override suspend fun signIn(email: String, password: String): Result<User> = runCatching {
        val authResult = auth.signInWithEmailAndPassword(email, password).await()
        val uid = authResult.user?.uid ?: auth.currentUser?.uid ?: throw IllegalStateException("Sign in failed")
        val doc = db.collection("users").document(uid).get().await()
        val user = doc.toObject(User::class.java)?.copy(id = uid)
            ?: User(id = uid, email = email)
        _user.value = user
        user
    }

    override fun signOut() {
        try {
            auth.signOut()
        } catch (_: Exception) {}
        _user.value = null
    }
}

package com.group2.pinam.data.firebase

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.group2.pinam.data.AuthRepository
import com.group2.pinam.model.User
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await


    private val _user = MutableStateFlow<User?>(null)


    init {
        val firebaseUser = auth.currentUser
        if (firebaseUser != null) {
            scope.launch {
                try {
                    val doc = db.collection("users").document(firebaseUser.uid).get().await()
                            id = firebaseUser.uid,
                            email = firebaseUser.email ?: ""
                        )
                    _user.value = User(
                        id = firebaseUser.uid,
                        email = firebaseUser.email ?: ""
                    )
                }
            }
        }
    }

        val authResult = auth.createUserWithEmailAndPassword(email, password).await()
        val user = User(id = uid, name = name, email = email)
        db.collection("users").document(uid).set(user).await()
        _user.value = user
        user

        val authResult = auth.signInWithEmailAndPassword(email, password).await()
        val doc = db.collection("users").document(uid).get().await()
                    id = uid,
                )
        _user.value = user
        user

    override fun signOut() {
        try {
            auth.signOut()
        } catch (_: Exception) {}
        _user.value = null
    }

        }
        return Exception(message)
    }
}

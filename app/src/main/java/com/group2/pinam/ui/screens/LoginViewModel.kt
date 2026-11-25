package com.group2.pinam.ui.screens

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.group2.pinam.data.ServiceLocator
import kotlinx.coroutines.launch

class LoginViewModel : ViewModel() {

    var name by mutableStateOf("")
        private set

    var email by mutableStateOf("")
        private set

    var password by mutableStateOf("")
        private set

    var isSignUp by mutableStateOf(true)
        private set

    var errorMessage by mutableStateOf<String?>(null)
        private set

    var isLoading by mutableStateOf(false)
        private set

    var emailError by mutableStateOf<String?>(null)
        private set

    var passwordError by mutableStateOf<String?>(null)
        private set

    fun onNameChange(value: String) {
        name = value
    }

    fun onEmailChange(value: String) {
        email = value
        emailError = null
    }

    fun onPasswordChange(value: String) {
        password = value
        passwordError = null
    }

    fun toggleMode() {
        isSignUp = !isSignUp
        errorMessage = null
        emailError = null
        passwordError = null
    }

    fun submit(onLoggedIn: () -> Unit) {
        var hasError = false
        emailError = null
        passwordError = null
        errorMessage = null

        if (!email.contains("@")) {
            emailError = "Email must contain @"
            hasError = true
        }

        if (password.length < 6) {
            passwordError = "Password must be at least 6 characters"
            hasError = true
        }

        if (hasError) return

        viewModelScope.launch {
            isLoading = true
            errorMessage = null

            val result = if (isSignUp) {
                ServiceLocator.auth.signUp(name, email, password)
            } else {
                ServiceLocator.auth.signIn(email, password)
            }

            result.fold(
                onSuccess = { onLoggedIn() },
                onFailure = { errorMessage = it.message ?: "Authentication failed" }
            )

            isLoading = false
        }
    }
}

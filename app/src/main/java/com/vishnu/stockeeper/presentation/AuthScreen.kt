package com.vishnu.stockeeper.presentation

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import com.vishnu.stockeeper.viewmodel.AuthViewModel
import kotlinx.coroutines.launch

@Composable
fun AuthScreen(
    authViewModel: AuthViewModel,
    onLoginSuccess: () -> Unit
) {
    val authState by authViewModel.authState.collectAsState()
    val coroutineScope = rememberCoroutineScope()

    // State to hold email and password input
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    // State to manage errors
    var emailError by remember { mutableStateOf<String?>(null) }
    var passwordError by remember { mutableStateOf<String?>(null) }

    // Update error states based on auth state
    LaunchedEffect(key1 = authState) {
        when (authState) {
            is AuthViewModel.AuthState.Error -> {
                val errorMessage = (authState as AuthViewModel.AuthState.Error).message
                emailError = if (errorMessage?.contains("email") == true) "Invalid email" else null
                passwordError =
                    if (errorMessage?.contains("credential") == true) "Invalid password" else null
            }

            else -> {
                emailError = null
                passwordError = null
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        TextField(
            value = email,
            onValueChange = { email = it },
            label = { Text("Email") },
            modifier = Modifier
                .fillMaxWidth()
                .border(1.dp, if (emailError != null) Color.Red else Color.Transparent),
            colors = TextFieldDefaults.colors(
                focusedIndicatorColor = if (emailError != null) Color.Red else MaterialTheme.colorScheme.primary,
                unfocusedIndicatorColor = if (emailError != null) Color.Red else MaterialTheme.colorScheme.onSurface,
                focusedLabelColor = if (emailError != null) Color.Red else MaterialTheme.colorScheme.primary
            ),
            isError = emailError != null
        )

        Spacer(modifier = Modifier.height(8.dp))

        if (emailError != null) {
            Text(
                text = emailError!!,
                color = Color.Red,
                style = MaterialTheme.typography.bodySmall
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        TextField(
            value = password,
            onValueChange = { password = it },
            label = { Text("Password") },
            modifier = Modifier
                .fillMaxWidth()
                .border(1.dp, if (passwordError != null) Color.Red else Color.Transparent),
            colors = TextFieldDefaults.colors(
                focusedIndicatorColor = if (passwordError != null) Color.Red else MaterialTheme.colorScheme.primary,
                unfocusedIndicatorColor = if (passwordError != null) Color.Red else MaterialTheme.colorScheme.onSurface,
                focusedLabelColor = if (passwordError != null) Color.Red else MaterialTheme.colorScheme.primary
            ),
            isError = passwordError != null,
            visualTransformation = PasswordVisualTransformation()
        )

        Spacer(modifier = Modifier.height(8.dp))

        if (passwordError != null) {
            Text(
                text = passwordError!!,
                color = Color.Red,
                style = MaterialTheme.typography.bodySmall
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        Button(onClick = {
            coroutineScope.launch {
                if (email.isNotBlank() && password.isNotBlank()) {
                    authViewModel.signup(email, password) { uid ->
                        if (uid != null) {
                            onLoginSuccess()
                        }
                    }
                }
            }
        }) {
            Text(text = "Sign Up")
        }

        Spacer(modifier = Modifier.height(16.dp))

        Button(onClick = {
            coroutineScope.launch {
                if (email.isNotBlank() && password.isNotBlank()) {
                    authViewModel.authenticate(email, password) { uid ->
                        if (uid != null) {
                            onLoginSuccess()
                        }
                    }
                }
            }
        }) {
            Text(text = "Log In")
        }

        Spacer(modifier = Modifier.height(16.dp))

        Button(onClick = {
            coroutineScope.launch {
                authViewModel.signOut()
            }
        }) {
            Text(text = "Sign Out")
        }

        when (authState) {
            is AuthViewModel.AuthState.Error -> {
                Text(
                    text = (authState as AuthViewModel.AuthState.Error).message
                        ?: "An unknown error occurred",
                    color = Color.Red
                )
            }

            is AuthViewModel.AuthState.Idle -> {
                Text(
                    text = "",
                    color = Color.Blue
                )
            }
        }
    }
}

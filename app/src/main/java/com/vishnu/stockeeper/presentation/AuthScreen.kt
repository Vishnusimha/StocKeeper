package com.vishnu.stockeeper.presentation


import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.vishnu.stockeeper.viewmodel.AuthViewModel
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AuthScreen(authViewModel: AuthViewModel, navController: NavHostController) {
    val isUserPresent by authViewModel.isUserPresent.collectAsState(false)
    val authState by authViewModel.authState.collectAsState()

    // Coroutine scope to handle side effects
    val coroutineScope = rememberCoroutineScope()
    val isRefreshing = remember { mutableStateOf(false) }
    val context = LocalContext.current

    // State to hold email and password input
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    // State to manage errors
    var emailError by remember { mutableStateOf<String?>(null) }
    var passwordError by remember { mutableStateOf<String?>(null) }
    // Navigation based on authentication state
    LaunchedEffect(key1 = isUserPresent) {
        if (isUserPresent) {
            navController.navigate(Screen.StockScreen.route) {
                popUpTo(Screen.AuthScreen.route) { inclusive = true }
            }
        }
    }
    // Update error states based on auth state
    LaunchedEffect(key1 = authState) {
        when (authState) {
            is AuthViewModel.AuthState.Error -> {
                //Parse the error message and set error states
                val errorMessage = (authState as AuthViewModel.AuthState.Error).message
                // Here you can parse the errorMessage and set specific field errors
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

    Scaffold(
        topBar = {
            TopAppBar(
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.primary,
                ),
                title = {
                    Box(
                        modifier = Modifier.fillMaxWidth(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("Welcome")
                    }
                }
            )
        }
    ) { innerPadding ->
        Surface(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize(),
            shadowElevation = 4.dp,
            color = Color.White,
        ) {
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

                Spacer(modifier = Modifier.height(16.dp))

                Button(onClick = {
                    coroutineScope.launch {
                        if (email.isNotBlank() && password.isNotBlank()) {
                            authViewModel.signup(email, password)
                        }
                    }
                }) {
                    Text(text = "Sign Up")
                }

                Spacer(modifier = Modifier.height(16.dp))

                Button(onClick = {
                    coroutineScope.launch {
                        if (email.isNotBlank() && password.isNotBlank()) {
                            authViewModel.authenticate(email, password)
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

                    else -> {}
                }
            }
        }
    }
}




package com.vishnu.stockeeper.presentation

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import com.vishnu.stockeeper.presentation.ui.theme.StocKeeperTheme
import com.vishnu.stockeeper.viewmodel.AuthViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class LoginActivity : ComponentActivity() {

    private val authViewModel: AuthViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            StocKeeperTheme {
                val isUserPresent by authViewModel.isUserPresent.collectAsState(false)

                // Navigate to LandingActivity if user is present
                LaunchedEffect(isUserPresent) {
                    if (isUserPresent) {
                        navigateToMainActivity()
                    }
                }

                // Display the AuthScreen
                AuthScreen(
                    authViewModel = authViewModel,
                    onLoginSuccess = {
                        navigateToMainActivity()
                    }
                )
            }
        }
    }

    private fun navigateToMainActivity() {
        val intent = Intent(this, LandingActivity::class.java)
        startActivity(intent)
        finish()
    }
}

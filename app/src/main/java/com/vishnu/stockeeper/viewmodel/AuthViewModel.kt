package com.vishnu.stockeeper.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor() : ViewModel() {

    private var auth: FirebaseAuth = Firebase.auth
    private var currentUser = auth.currentUser

    private val _isUserPresent = MutableStateFlow(false)
    val isUserPresent: StateFlow<Boolean> get() = _isUserPresent.asStateFlow()

    private val _authState = MutableStateFlow<AuthState>(AuthState.Idle)
    val authState: StateFlow<AuthState> get() = _authState.asStateFlow()

    init {
        checkCurrentUser()
    }

    private fun checkCurrentUser() {
        currentUser = auth.currentUser
        _isUserPresent.value = currentUser != null
    }

    fun authenticate(email: String, password: String) {
        if (currentUser != null) {
            _isUserPresent.value = true
            userData()
            Log.d(TAG, "authenticate: currently User is available")
        } else {
            signIn(email, password)
        }
    }

    fun signup(email: String, password: String) {
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Log.d(TAG, "createUserWithEmail:success")
                    currentUser = auth.currentUser
                    _isUserPresent.value = true
                } else {
                    Log.w(TAG, "createUserWithEmail:failure", task.exception)
                    _authState.value = AuthState.Error(task.exception?.message)
                }
            }
    }

    private fun signIn(email: String, password: String) {
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                    Log.d(TAG, "signInWithEmail:success")
                    currentUser = auth.currentUser
                    _isUserPresent.value = true
                    Log.d(TAG, "signInWithEmail:success - ${currentUser?.email}")
                } else {
                    _isUserPresent.value = false
                    // If sign in fails, display a message to the user.
                    Log.w(TAG, "signInWithEmail:failure", task.exception)
                    _authState.value = AuthState.Error(task.exception?.message)
                }
            }
    }

    fun userData() {
        currentUser?.let {
            // Name, email address, and profile photo Url
            val name = it.displayName
            val email = it.email
            val photoUrl = it.photoUrl

            // Check if user's email is verified
            val emailVerified = it.isEmailVerified

            // The user's ID, unique to the Firebase project. Do NOT use this value to
            // authenticate with your backend server, if you have one. Use
            // FirebaseUser.getIdToken() instead.
            val uid = it.uid

            Log.i(TAG, " name = $name, email = $email , url = $photoUrl  verified = $emailVerified")
        }
    }

    fun signOut() {
        auth.signOut()
        _isUserPresent.value = false
        currentUser = null
    }

    sealed class AuthState {
        object Idle : AuthState()
        data class Error(val message: String?) : AuthState()
    }

}
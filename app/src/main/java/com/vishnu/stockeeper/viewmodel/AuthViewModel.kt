package com.vishnu.stockeeper.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import com.vishnu.stockeeper.repository.StockManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor(private val stockManager: StockManager) : ViewModel() {
    private var auth: FirebaseAuth = Firebase.auth
    private var currentUser = auth.currentUser

    private val _isUserPresent = MutableStateFlow(false)
    val isUserPresent: StateFlow<Boolean> get() = _isUserPresent.asStateFlow()

    private val _authState = MutableStateFlow<AuthState>(AuthState.Idle)
    val authState: StateFlow<AuthState> get() = _authState.asStateFlow()
    var needToInitiate = true

    init {
        checkCurrentUser()
    }

    private fun checkCurrentUser() {
        currentUser = auth.currentUser
        _isUserPresent.value = currentUser != null
    }

    fun authenticate(email: String, password: String, onResult: (String?) -> Unit) {
        if (currentUser != null) {
            _isUserPresent.value = true
            Log.d(TAG, "authenticate: currently User is available")
            onResult(currentUser?.uid)
        } else {
            signIn(email, password, onResult)
        }
    }

    private fun initialiseFirebaseRepo(uid: String?) {
        if (uid != null) {
            stockManager.initFirebaseStockRepository(uid)
        }
    }

    fun signup(email: String, password: String, onResult: (String?) -> Unit) {
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Log.d(TAG, "createUserWithEmail:success")
                    currentUser = auth.currentUser
                    _isUserPresent.value = true
                    needToInitiate = isUserPresent.value
                    onResult(currentUser?.uid)
                } else {
                    Log.w(TAG, "createUserWithEmail:failure", task.exception)
                    _authState.value = AuthState.Error(task.exception?.message)
                    onResult(null)
                }
            }
    }

    private fun signIn(email: String, password: String, onResult: (String?) -> Unit) {
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Log.d(TAG, "signInWithEmail:success")
                    currentUser = auth.currentUser
                    _isUserPresent.value = true
                    Log.d(TAG, "signInWithEmail:success - ${currentUser?.email}")
                    needToInitiate = isUserPresent.value
                    onResult(currentUser?.uid)
                } else {
                    _isUserPresent.value = false
                    Log.w(TAG, "signInWithEmail:failure", task.exception)
                    _authState.value = AuthState.Error(task.exception?.message)
                    onResult(null)
                }
            }
    }

    fun signOut() {
        viewModelScope.launch {
            stockManager.deleteAllItemsFromLocal()
            _isUserPresent.value = false
            currentUser = null
            needToInitiate = isUserPresent.value
            auth.signOut()
        }
    }

    sealed class AuthState {
        object Idle : AuthState()
        data class Error(val message: String?) : AuthState()
    }
}
package com.example.taskmaster.viewmodel.model

import androidx.lifecycle.ViewModel
import com.example.taskmaster.viewmodel.data.repo.AuthRepository
import com.example.taskmaster.viewmodel.data.users.UserDto
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class AuthViewModel(
    private val repo: AuthRepository = AuthRepository()
) : ViewModel() {

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error

    private val _token = MutableStateFlow<String?>(null)
    val token: StateFlow<String?> = _token

    private val _signedUpUser = MutableStateFlow<UserDto?>(null)
    val signedUpUser: StateFlow<UserDto?> = _signedUpUser

    fun signIn(email: String, password: String) = launchCatching(_isLoading, _error) {
        _token.value = repo.signIn(email, password)
    }


    fun signUpUsername(username: String, email: String, password: String) =
        launchCatching(_isLoading, _error) {
            _signedUpUser.value = repo.signUpWithUsername(username, email, password)
        }
}

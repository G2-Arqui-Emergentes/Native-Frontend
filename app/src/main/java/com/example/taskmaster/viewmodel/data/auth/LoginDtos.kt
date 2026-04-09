package com.example.taskmaster.viewmodel.data.auth

data class LoginRequest(
    val email: String,
    val password: String
)

data class LoginResponse(
    val token: String
)



data class SignUpRequest(
    val name: String,
    val lastName: String,
    val email: String,
    val password: String,
    val roles: List<String> = listOf("ROLE_LEADER")
)
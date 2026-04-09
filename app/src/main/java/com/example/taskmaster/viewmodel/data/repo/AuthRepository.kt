package com.example.taskmaster.viewmodel.data.repo

import com.example.taskmaster.viewmodel.data.auth.LoginRequest
import com.example.taskmaster.viewmodel.data.auth.SignUpRequest
import com.example.taskmaster.viewmodel.data.net.ApiFactory
import com.example.taskmaster.viewmodel.data.net.AuthApi
import com.example.taskmaster.viewmodel.data.net.TokenStore
import com.example.taskmaster.viewmodel.data.users.UserDto

class AuthRepository(private val api: AuthApi = ApiFactory.auth) {

    suspend fun signIn(email: String, password: String): String {
        val res = api.signIn(LoginRequest(email, password))
        TokenStore.token = res.token
        return res.token
    }


    suspend fun signUpWithUsername(username: String, email: String, password: String): UserDto {
        val (name, last) = splitUsername(username)
        return api.signUp(
            SignUpRequest(
                name = name,
                lastName = last,
                email = email,
                password = password,
                roles = listOf("ROLE_LEADER")
            )
        )
    }

    private fun splitUsername(username: String): Pair<String, String> {
        val parts = username.trim().split(Regex("\\s+")).filter { it.isNotBlank() }
        val name = parts.firstOrNull() ?: "Usuario"
        val last = if (parts.size > 1) parts.drop(1).joinToString(" ") else "Nuevo"
        return name to last
    }
}

package com.example.taskmaster.viewmodel.data.users

data class UserDto(
    val id: Long,
    val email: String,
    val roles: List<String>,
    val name: String,
    val lastName: String,
    val imageUrl: String?,
    val salary: Double?,
    val projectIds: List<Any>
)

data class UserUpdateRequest(
    val name: String,
    val lastName: String,
    val imageUrl: String?,
    val salary: Double?
)
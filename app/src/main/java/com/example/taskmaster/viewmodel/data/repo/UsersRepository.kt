package com.example.taskmaster.viewmodel.data.repo

import com.example.taskmaster.viewmodel.data.net.ApiFactory
import com.example.taskmaster.viewmodel.data.net.UsersApi
import com.example.taskmaster.viewmodel.data.users.UserDto
import com.example.taskmaster.viewmodel.data.users.UserUpdateRequest


class UsersRepository(
    private val api: UsersApi = ApiFactory.users
) {
    // GET /api/v1/users
    suspend fun getAll(): List<UserDto> = api.getUsers()

    // PUT /api/v1/users
    suspend fun update(body: UserUpdateRequest): UserDto = api.updateUser(body)

    // GET /api/v1/users/{userId}
    suspend fun getById(userId: Long): UserDto = api.getUserById(userId)

    // DELETE /api/v1/users/{userId}
    suspend fun delete(userId: Long) { api.deleteUser(userId) }

    // GET /api/v1/users/email/{email}
    suspend fun getByEmail(email: String): UserDto = api.getUserByEmail(email)


}

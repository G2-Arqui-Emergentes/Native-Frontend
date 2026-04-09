package com.example.taskmaster.viewmodel.data.net

import com.example.taskmaster.viewmodel.data.auth.LoginRequest
import com.example.taskmaster.viewmodel.data.auth.LoginResponse
import com.example.taskmaster.viewmodel.data.auth.SignUpRequest
import com.example.taskmaster.viewmodel.data.notifications.NotificationDto
import com.example.taskmaster.viewmodel.data.projects.ProjectCodeRequest
import com.example.taskmaster.viewmodel.data.projects.ProjectCreateRequest
import com.example.taskmaster.viewmodel.data.projects.ProjectDto
import com.example.taskmaster.viewmodel.data.projects.ProjectUpdateRequest
import com.example.taskmaster.viewmodel.data.tasks.TaskAssignRequest
import com.example.taskmaster.viewmodel.data.tasks.TaskCreateRequest
import com.example.taskmaster.viewmodel.data.tasks.TaskDto
import com.example.taskmaster.viewmodel.data.tasks.TaskPriority
import com.example.taskmaster.viewmodel.data.tasks.TaskStatus
import com.example.taskmaster.viewmodel.data.tasks.TaskStatusUpdateRequest
import com.example.taskmaster.viewmodel.data.tasks.TaskUpdateRequest
import com.example.taskmaster.viewmodel.data.users.UserDto
import com.example.taskmaster.viewmodel.data.users.UserUpdateRequest
import retrofit2.http.*

interface AuthApi {

    @POST("api/v1/authentication/sign-in")
    suspend fun signIn(@Body body: LoginRequest): LoginResponse
    @POST("api/v1/authentication/sign-up")
    suspend fun signUp(@Body body: SignUpRequest): UserDto
}

/** ---- USERS ---- **/
interface UsersApi {
    @GET("api/v1/users")
    suspend fun getUsers(): List<UserDto>

    @PUT("api/v1/users")
    suspend fun updateUser(@Body body: UserUpdateRequest): UserDto

    @GET("api/v1/users/{userId}")
    suspend fun getUserById(@Path("userId") userId: Long): UserDto

    @DELETE("api/v1/users/{userId}")
    suspend fun deleteUser(@Path("userId") userId: Long)

    @GET("api/v1/users/email/{email}")
    suspend fun getUserByEmail(@Path("email") email: String): UserDto


}

/** ---- PROJECTS ---- **/
interface ProjectsApi {
    @GET("api/v1/projects")
    suspend fun getProjects(): List<ProjectDto>

    @POST("api/v1/projects")
    suspend fun createProject(@Body body: ProjectCreateRequest): ProjectDto

    @GET("api/v1/projects/{projectId}")
    suspend fun getProject(@Path("projectId") projectId: Long): ProjectDto

    @PUT("api/v1/projects/{id}")
    suspend fun updateProject(@Path("id") id: Long, @Body body: ProjectUpdateRequest): ProjectDto

    @DELETE("api/v1/projects/{id}")
    suspend fun deleteProject(@Path("id") id: Long)

    @PUT("api/v1/projects/{projectId}/code")
    suspend fun setProjectCode(@Path("projectId") projectId: Long, @Body body: ProjectCodeRequest): ProjectDto


    @GET("api/v1/projects/member/{memberId}")
    suspend fun getProjectsByMember(@Path("memberId") memberId: Long): List<ProjectDto>

    @GET("api/v1/projects/leader/{leaderId}")
    suspend fun getProjectsByLeader(@Path("leaderId") leaderId: Long): List<ProjectDto>

    @GET("api/v1/projects/join/{key}")
    suspend fun joinProjectByKey(@Path("key") key: String): ProjectDto

    @DELETE("api/v1/projects/{projectId}/members/{memberId}")
    suspend fun removeMember(
        @Path("projectId") projectId: Long,
        @Path("memberId") memberId: Long
    )
}

/** ---- TASKS ---- **/
interface TasksApi {
    @GET("api/v1/tasks")
    suspend fun getTasks(): List<TaskDto>

    @POST("api/v1/tasks")
    suspend fun createTask(@Body body: TaskCreateRequest): TaskDto

    @GET("api/v1/tasks/{taskId}")
    suspend fun getTask(@Path("taskId") taskId: Long): TaskDto

    @PUT("api/v1/tasks/{taskId}")
    suspend fun updateTask(@Path("taskId") taskId: Long, @Body body: TaskUpdateRequest): TaskDto

    @DELETE("api/v1/tasks/{taskId}")
    suspend fun deleteTask(@Path("taskId") taskId: Long)

    @PUT("api/v1/tasks/{taskId}/assign")
    suspend fun assignTask(@Path("taskId") taskId: Long, @Body body: TaskAssignRequest): TaskDto

    @PUT("api/v1/tasks/{taskId}/unassign")
    suspend fun unassignTask(@Path("taskId") taskId: Long, @Body body: TaskAssignRequest): TaskDto

    @PUT("api/v1/tasks/{taskId}/status")
    suspend fun updateTaskStatus(@Path("taskId") taskId: Long, @Body body: TaskStatusUpdateRequest): TaskDto

    @GET("api/v1/tasks/user/{userId}")
    suspend fun getTasksByUser(@Path("userId") userId: Long): List<TaskDto>

    @GET("api/v1/tasks/project/{projectId}")
    suspend fun getTasksByProject(@Path("projectId") projectId: Long): List<TaskDto>

    @GET("api/v1/tasks/project/{projectId}/user/{userId}")
    suspend fun getTasksByProjectAndUser(
        @Path("projectId") projectId: Long,
        @Path("userId") userId: Long
    ): List<TaskDto>

    @GET("api/v1/tasks/project/{projectId}/status/{status}")
    suspend fun getTasksByProjectAndStatus(
        @Path("projectId") projectId: Long,
        @Path("status") status: TaskStatus
    ): List<TaskDto>

    @GET("api/v1/tasks/project/{projectId}/priority/{priority}")
    suspend fun getTasksByProjectAndPriority(
        @Path("projectId") projectId: Long,
        @Path("priority") priority: TaskPriority
    ): List<TaskDto>
}


interface NotificationsApi {

    // GET /api/v1/notifications/me
    @GET("api/v1/notifications/me")
    suspend fun getMyNotifications(): List<NotificationDto>
}
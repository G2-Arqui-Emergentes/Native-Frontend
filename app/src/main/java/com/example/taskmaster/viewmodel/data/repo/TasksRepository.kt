package com.example.taskmaster.viewmodel.data.repo

import com.example.taskmaster.viewmodel.data.net.ApiFactory
import com.example.taskmaster.viewmodel.data.net.TasksApi
import com.example.taskmaster.viewmodel.data.tasks.TaskAssignRequest
import com.example.taskmaster.viewmodel.data.tasks.TaskCreateRequest
import com.example.taskmaster.viewmodel.data.tasks.TaskDto
import com.example.taskmaster.viewmodel.data.tasks.TaskPriority
import com.example.taskmaster.viewmodel.data.tasks.TaskStatus
import com.example.taskmaster.viewmodel.data.tasks.TaskStatusUpdateRequest
import com.example.taskmaster.viewmodel.data.tasks.TaskUpdateRequest

class TasksRepository(
    private val api: TasksApi = ApiFactory.tasks
) {
    // GET /api/v1/tasks/{taskId}
    suspend fun getById(taskId: Long): TaskDto = api.getTask(taskId)

    // PUT /api/v1/tasks/{taskId}
    suspend fun update(taskId: Long, body: TaskUpdateRequest): TaskDto =
        api.updateTask(taskId, body)

    // DELETE /api/v1/tasks/{taskId}
    suspend fun delete(taskId: Long) { api.deleteTask(taskId) }

    // PUT /api/v1/tasks/{taskId}/unassign
    suspend fun unassign(taskId: Long, userId: Long): TaskDto =
        api.unassignTask(taskId, TaskAssignRequest(userId))

    // PUT /api/v1/tasks/{taskId}/status
    suspend fun updateStatus(taskId: Long, status: TaskStatus): TaskDto =
        api.updateTaskStatus(taskId, TaskStatusUpdateRequest(status))

    // PUT /api/v1/tasks/{taskId}/assign
    suspend fun assign(taskId: Long, userId: Long): TaskDto =
        api.assignTask(taskId, TaskAssignRequest(userId))

    // GET /api/v1/tasks
    suspend fun getAll(): List<TaskDto> = api.getTasks()

    // POST /api/v1/tasks
    suspend fun create(body: TaskCreateRequest): TaskDto = api.createTask(body)

    // GET /api/v1/tasks/user/{userId}
    suspend fun getByUser(userId: Long): List<TaskDto> = api.getTasksByUser(userId)

    // GET /api/v1/tasks/project/{projectId}
    suspend fun getByProject(projectId: Long): List<TaskDto> =
        api.getTasksByProject(projectId)

    // GET /api/v1/tasks/project/{projectId}/user/{userId}
    suspend fun getByProjectAndUser(projectId: Long, userId: Long): List<TaskDto> =
        api.getTasksByProjectAndUser(projectId, userId)

    // GET /api/v1/tasks/project/{projectId}/status/{status}
    suspend fun getByProjectAndStatus(projectId: Long, status: TaskStatus): List<TaskDto> =
        api.getTasksByProjectAndStatus(projectId, status)

    // GET /api/v1/tasks/project/{projectId}/priority/{priority}
    suspend fun getByProjectAndPriority(projectId: Long, priority: TaskPriority): List<TaskDto> =
        api.getTasksByProjectAndPriority(projectId, priority)
}

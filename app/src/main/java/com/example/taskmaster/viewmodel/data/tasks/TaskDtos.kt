package com.example.taskmaster.viewmodel.data.tasks

data class TaskDto(
    val id: Long,
    val taskId: Long,
    val projectId: Long,
    val title: String,
    val description: String,
    val startDate: String,
    val endDate: String,
    val status: TaskStatus,
    val priority: TaskPriority,
    val assignedUserIds: List<Long>,
    val createdAt: String,
    val updatedAt: String
)

enum class TaskStatus { TO_DO, IN_PROGRESS, DONE }
enum class TaskPriority { LOW, MEDIUM, HIGH }

// Requests
data class TaskCreateRequest(
    val projectId: Long,
    val title: String,
    val description: String,
    val startDate: String,
    val endDate: String,
    val status: String,
    val priority: String,
    val assignedUserIds: List<Long>
)

data class TaskUpdateRequest(
    val title: String,
    val description: String,
    val startDate: String,
    val endDate: String,
    val priority: TaskPriority,
    val status: TaskStatus,
    val assignedUserIds: List<Long>
)

data class TaskAssignRequest(val userId: Long)
data class TaskStatusUpdateRequest(val status: TaskStatus)
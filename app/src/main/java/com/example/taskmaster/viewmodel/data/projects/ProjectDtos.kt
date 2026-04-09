package com.example.taskmaster.viewmodel.data.projects


data class ProjectDto(
    val id: Long,
    val projectId: Long,
    val key: String,
    val leaderId: Long,
    val name: String,
    val description: String,
    val imageUrl: String?,
    val budget: Double,
    val status: String,
    val startDate: String,
    val endDate: String
)

data class ProjectCreateRequest(
    val name: String,
    val description: String,
    val imageUrl : String?,
    val budget: Double,
    val endDate: String
)

data class ProjectUpdateRequest(
    val name: String,
    val description: String,
    val imageUrl : String,
    val budget: Double,
    val status: String,
    val endDate: String
)

data class ProjectStats(
    val totalTasks: Int = 0,
    val overdueTasks: Int = 0,
    val bestMember: String = "Ninguno",
    val worstMember: String = "Ninguno",
    val todoTasks: Int = 0,
    val inProgressTasks: Int = 0,
    val doneTasks: Int = 0,
    val highPriorityTasks: Int = 0,
    val mediumPriorityTasks: Int = 0,
    val lowPriorityTasks: Int = 0,
    val budget: Double = 0.0,
    val usedBudget: Double = 0.0
)

data class ProjectCodeRequest(val code: String)

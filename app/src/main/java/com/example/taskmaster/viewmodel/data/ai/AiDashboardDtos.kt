package com.example.taskmaster.viewmodel.data.ai

data class LeaderDashboardDto(
    val topRiskProject: ProjectRiskDto?,
    val recommendations: List<String> = emptyList(),
    val memberPerformances: List<MemberPerformanceDto> = emptyList(),
    val generatedAt: String? = null,
    val totalProjects: Int = 0,
    val highRiskProjects: Int = 0
)

data class ProjectRiskDto(
    val id: Long? = null,
    val name: String? = null,
    val status: String? = null,
    val delayRisk: Double = 0.0,
    val overallEfficiency: Double = 0.0,
    val riskLevel: String? = null,
    val totalTasks: Int = 0,
    val completedTasks: Int = 0,
    val delayedTasks: Int = 0,
    val inProgressTasks: Int = 0
)

data class MemberPerformanceDto(
    val userId: Long,
    val userName: String,
    val performanceLevel: String,
    val score: Double,
    val tasksCompleted: Int,
    val tasksDelayed: Int
)

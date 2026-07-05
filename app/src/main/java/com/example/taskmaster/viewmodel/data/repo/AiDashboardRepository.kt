package com.example.taskmaster.viewmodel.data.repo

import com.example.taskmaster.viewmodel.data.ai.LeaderDashboardDto
import com.example.taskmaster.viewmodel.data.net.AiDashboardApi
import com.example.taskmaster.viewmodel.data.net.ApiFactory

class AiDashboardRepository(
    private val api: AiDashboardApi = ApiFactory.aiDashboard
) {
    suspend fun getLeaderDashboard(): LeaderDashboardDto = api.getLeaderDashboard()
}

package com.example.taskmaster.viewmodel.data.net

object ApiFactory {
    val auth: AuthApi by lazy { RetrofitProvider.retrofit.create(AuthApi::class.java) }
    val users: UsersApi by lazy { RetrofitProvider.retrofit.create(UsersApi::class.java) }
    val projects: ProjectsApi by lazy { RetrofitProvider.retrofit.create(ProjectsApi::class.java) }
    val tasks: TasksApi by lazy { RetrofitProvider.retrofit.create(TasksApi::class.java) }
    val notifications: NotificationsApi = RetrofitProvider.retrofit.create(NotificationsApi::class.java)
    val aiDashboard: AiDashboardApi by lazy { RetrofitProvider.retrofit.create(AiDashboardApi::class.java) }
    val chatbot: ChatbotApi by lazy { RetrofitProvider.retrofit.create(ChatbotApi::class.java) }
}

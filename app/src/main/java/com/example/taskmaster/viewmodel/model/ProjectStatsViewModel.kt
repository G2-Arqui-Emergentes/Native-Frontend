package com.example.taskmaster.viewmodel.model

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.taskmaster.viewmodel.data.projects.ProjectStats
import com.example.taskmaster.viewmodel.data.tasks.TaskDto
import com.example.taskmaster.viewmodel.data.tasks.TaskStatus
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

class ProjectStatsViewModel(
    private val tasksRepo: com.example.taskmaster.viewmodel.data.repo.TasksRepository = com.example.taskmaster.viewmodel.data.repo.TasksRepository(),
    private val usersRepo: com.example.taskmaster.viewmodel.data.repo.UsersRepository = com.example.taskmaster.viewmodel.data.repo.UsersRepository()
) : ViewModel() {

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    private val _tasks = MutableStateFlow<List<TaskDto>>(emptyList())
    val tasks: StateFlow<List<TaskDto>> = _tasks.asStateFlow()

    private val _stats = MutableStateFlow(ProjectStats())
    val stats: StateFlow<ProjectStats> = _stats.asStateFlow()

    fun loadProjectTasks(projectId: Long) {
        _isLoading.value = true
        _error.value = null

        viewModelScope.launch {
            try {
                val tasksList = tasksRepo.getByProject(projectId)
                _tasks.value = tasksList
                calculateStats(tasksList)
            } catch (e: Exception) {
                _error.value = "Error al cargar tareas: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    private suspend fun calculateStats(tasks: List<TaskDto>) {
        val totalTasks = tasks.size

        val currentDate = Date()
        val overdueTasks = tasks.count { task ->
            try {
                val endDate = parseDate(task.endDate)
                endDate != null && endDate.before(currentDate) && task.status != TaskStatus.DONE
            } catch (e: Exception) {
                false
            }
        }

        val todoTasks = tasks.count { it.status == TaskStatus.TO_DO }
        val inProgressTasks = tasks.count { it.status == TaskStatus.IN_PROGRESS }
        val doneTasks = tasks.count { it.status == TaskStatus.DONE }

        val highPriorityTasks =
            tasks.count { it.priority == com.example.taskmaster.viewmodel.data.tasks.TaskPriority.HIGH }
        val mediumPriorityTasks =
            tasks.count { it.priority == com.example.taskmaster.viewmodel.data.tasks.TaskPriority.MEDIUM }
        val lowPriorityTasks =
            tasks.count { it.priority == com.example.taskmaster.viewmodel.data.tasks.TaskPriority.LOW }

        val bestMember = findBestMember(tasks)
        val worstMember = findWorstMember(tasks)

        _stats.value = ProjectStats(
            totalTasks = totalTasks,
            overdueTasks = overdueTasks,
            bestMember = bestMember,
            worstMember = worstMember,
            todoTasks = todoTasks,
            inProgressTasks = inProgressTasks,
            doneTasks = doneTasks,
            highPriorityTasks = highPriorityTasks,
            mediumPriorityTasks = mediumPriorityTasks,
            lowPriorityTasks = lowPriorityTasks,
            budget = 15000.0,
            usedBudget = 4500.0
        )
    }

    private fun parseDate(dateString: String): Date? {
        return try {
            val formats = listOf(
                "yyyy-MM-dd'T'HH:mm:ss.SSSXXX",
                "yyyy-MM-dd'T'HH:mm:ssXXX",
                "yyyy-MM-dd HH:mm:ss.SSS",
                "yyyy-MM-dd"
            )

            for (format in formats) {
                try {
                    val sdf = SimpleDateFormat(format, Locale.getDefault())
                    sdf.timeZone = TimeZone.getTimeZone("UTC")
                    return sdf.parse(dateString)
                } catch (e: Exception) {
                    continue
                }
            }
            null
        } catch (e: Exception) {
            null
        }
    }

    private suspend fun findBestMember(tasks: List<TaskDto>): String {
        val userDoneCount = mutableMapOf<Long, Int>()

        tasks.forEach { task ->
            if (task.status == TaskStatus.DONE) {
                task.assignedUserIds.forEach { userId ->
                    userDoneCount[userId] = userDoneCount.getOrDefault(userId, 0) + 1
                }
            }
        }

        return if (userDoneCount.isNotEmpty()) {
            val bestUserId = userDoneCount.maxByOrNull { it.value }?.key
            val user = bestUserId?.let { usersRepo.getById(it) }
            if (user != null) "${user.name} ${user.lastName}" else "Usuario $bestUserId"
        } else {
            "Ninguno"
        }
    }

    private fun findWorstMember(tasks: List<TaskDto>): String {
        val userTodoCount = mutableMapOf<Long, Int>()

        tasks.forEach { task ->
            if (task.status == TaskStatus.TO_DO || task.status == TaskStatus.IN_PROGRESS) {
                task.assignedUserIds.forEach { userId ->
                    userTodoCount[userId] = userTodoCount.getOrDefault(userId, 0) + 1
                }
            }
        }

        return if (userTodoCount.isNotEmpty()) {
            val worstUserId = userTodoCount.maxByOrNull { it.value }?.key
            "Usuario $worstUserId"
        } else {
            "Ninguno"
        }
    }
}

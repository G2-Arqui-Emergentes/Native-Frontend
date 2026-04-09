package com.example.taskmaster.viewmodel.model



import androidx.lifecycle.ViewModel
import com.example.taskmaster.viewmodel.data.repo.TasksRepository
import com.example.taskmaster.viewmodel.data.tasks.TaskCreateRequest
import com.example.taskmaster.viewmodel.data.tasks.TaskDto
import com.example.taskmaster.viewmodel.data.tasks.TaskPriority
import com.example.taskmaster.viewmodel.data.tasks.TaskStatus
import com.example.taskmaster.viewmodel.data.tasks.TaskUpdateRequest
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class TasksViewModel(
    private val repo: TasksRepository = TasksRepository()
) : ViewModel() {

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error

    private val _tasks = MutableStateFlow<List<TaskDto>>(emptyList())
    val tasks: StateFlow<List<TaskDto>> = _tasks

    private val _selected = MutableStateFlow<TaskDto?>(null)
    val selected: StateFlow<TaskDto?> = _selected

    // ----- Loaders -----
    fun loadAll() = launchCatching(_isLoading, _error) {
        _tasks.value = repo.getAll()
    }

    fun loadById(taskId: Long) = launchCatching(_isLoading, _error) {
        _selected.value = repo.getById(taskId)
    }

    fun loadByUser(userId: Long) = launchCatching(_isLoading, _error) {
        _tasks.value = repo.getByUser(userId)
    }

    fun loadByProject(projectId: Long) = launchCatching(_isLoading, _error) {
        _tasks.value = repo.getByProject(projectId)
    }

    fun loadByProjectAndUser(projectId: Long, userId: Long) = launchCatching(_isLoading, _error) {
        _tasks.value = repo.getByProjectAndUser(projectId, userId)
    }

    fun loadByProjectAndStatus(projectId: Long, status: TaskStatus) = launchCatching(_isLoading, _error) {
        _tasks.value = repo.getByProjectAndStatus(projectId, status)
    }

    fun loadByProjectAndPriority(projectId: Long, priority: TaskPriority) = launchCatching(_isLoading, _error) {
        _tasks.value = repo.getByProjectAndPriority(projectId, priority)
    }

    // ----- Mutations -----
    fun create(req: TaskCreateRequest) = launchCatching(_isLoading, _error) {
        repo.create(req)
        loadByProject(req.projectId)
    }

    fun update(taskId: Long, req: TaskUpdateRequest) = launchCatching(_isLoading, _error) {
        repo.update(taskId, req)
        loadAll()
    }

    fun assign(taskId: Long, userId: Long) = launchCatching(_isLoading, _error) {
        repo.assign(taskId, userId)
        loadAll()
    }

    fun unassign(taskId: Long, userId: Long) = launchCatching(_isLoading, _error) {
        repo.unassign(taskId, userId)
        loadAll()
    }

    fun updateStatus(taskId: Long, status: TaskStatus) = launchCatching(_isLoading, _error) {
        repo.updateStatus(taskId, status)
        loadAll()
    }

    fun delete(taskId: Long) = launchCatching(_isLoading, _error) {
        repo.delete(taskId)
        loadAll()
    }
}

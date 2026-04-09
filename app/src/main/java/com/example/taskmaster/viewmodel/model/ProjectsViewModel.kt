package com.example.taskmaster.viewmodel.model

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.taskmaster.viewmodel.data.projects.ProjectCreateRequest
import com.example.taskmaster.viewmodel.data.projects.ProjectDto
import com.example.taskmaster.viewmodel.data.projects.ProjectUpdateRequest
import com.example.taskmaster.viewmodel.data.repo.ProjectRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class ProjectsViewModel(
    private val repo: ProjectRepository = ProjectRepository()
) : ViewModel() {

    private val scope = CoroutineScope(Dispatchers.IO)

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error

    private val _projects = MutableStateFlow<List<ProjectDto>>(emptyList())
    val projects: StateFlow<List<ProjectDto>> = _projects

    private val _created = MutableStateFlow<ProjectDto?>(null)
    val created: StateFlow<ProjectDto?> = _created

    fun loadAll() = scope.launch {
        try {
            _isLoading.value = true
            _projects.value = repo.getAll()
            _error.value = null
        } catch (e: Exception) {
            _error.value = e.message
        } finally {
            _isLoading.value = false
        }
    }

    fun create(
        name: String,
        description: String,
        imageUrl: String,
        budget: Double?,
        endDate: String
    ) = scope.launch {
        try {
            _isLoading.value = true
            val body = ProjectCreateRequest(
                name = name,
                description = description,
                imageUrl = imageUrl,
                budget = budget ?: 0.0,
                endDate = endDate
            )
            val res = repo.create(body)
            _created.value = res
            _projects.value = repo.getAll()
            _error.value = null
        } catch (e: Exception) {
            _error.value = e.message
        } finally {
            _isLoading.value = false
        }
    }

    fun clearCreated() { _created.value = null }
    private val _current = MutableStateFlow<ProjectDto?>(null)
    val current: StateFlow<ProjectDto?> = _current

    fun loadById(id: Long) = viewModelScope.launch {
        runCatching { repo.getById(id) }
            .onSuccess { _current.value = it }
            .onFailure {  }
    }

    fun update(
        id: Long,
        name: String,
        description: String,
        imageUrl: String,
        budget: Double?,
        endDate: String,
        status: String
    ) = viewModelScope.launch {
        _isLoading.value = true
        runCatching {
            val safeBudget = budget ?: 0.0
            repo.update(
                id,
                ProjectUpdateRequest(
                    name = name,
                    description = description,
                    imageUrl = imageUrl,
                    budget = safeBudget,
                    endDate = endDate,
                    status = status
                )
            )
        }.onSuccess {
            _current.value = it
            _error.value = null
        }.onFailure {
            _error.value = it.message
        }
        _isLoading.value = false
    }


    fun delete(id: Long) = viewModelScope.launch {
        _isLoading.value = true
        runCatching { repo.delete(id) }
            .onSuccess {  _current.value = null }
            .onFailure { _error.value = it.message }
        _isLoading.value = false
    }
    fun removeMember(projectId: Long, memberId: Long) = viewModelScope.launch {
        _isLoading.value = true
        runCatching { repo.removeMember(projectId, memberId) }
            .onSuccess { _error.value = null }
            .onFailure { _error.value = it.message }
        _isLoading.value = false
    }
}

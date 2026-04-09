package com.example.taskmaster.viewmodel.model

import androidx.lifecycle.ViewModel
import com.example.taskmaster.viewmodel.data.repo.TasksRepository
import com.example.taskmaster.viewmodel.data.tasks.TaskDto
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.YearMonth
import java.time.format.DateTimeFormatter

data class CalendarDay(
    val day: Int,
    val date: LocalDate,
    val tasks: List<TaskDto> = emptyList(),
    val isCurrentMonth: Boolean = true
)

class CalendarViewModel(
    private val tasksRepo: TasksRepository = TasksRepository()
) : ViewModel() {

    private val scope = CoroutineScope(Dispatchers.IO)

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error

    private val _selectedDate = MutableStateFlow<LocalDate?>(null)
    val selectedDate: StateFlow<LocalDate?> = _selectedDate

    private val _currentMonth = MutableStateFlow(YearMonth.now())
    val currentMonth: StateFlow<YearMonth> = _currentMonth

    private val _allTasks = MutableStateFlow<List<TaskDto>>(emptyList())
    val allTasks: StateFlow<List<TaskDto>> = _allTasks

    // Tareas filtradas por fecha seleccionada
    val selectedDateTasks = combine(_selectedDate, _allTasks) { date, tasks ->
        if (date == null) emptyList()
        else {
            val dateString = date.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))
            tasks.filter { task ->
                val taskStartDate = task.startDate.take(10)
                val taskEndDate = task.endDate.take(10)
                dateString >= taskStartDate && dateString <= taskEndDate
            }
        }
    }

    // Generar días del calendario para el mes actual
    val calendarDays = combine(_currentMonth, _allTasks) { month, tasks ->
        generateCalendarDays(month, tasks)
    }

    fun selectDate(date: LocalDate) {
        _selectedDate.value = date
    }

    fun navigateToMonth(month: YearMonth) {
        _currentMonth.value = month
    }

    fun loadAllTasks() {
        scope.launch {
            try {
                _isLoading.value = true
                _error.value = null
                _allTasks.value = tasksRepo.getAll()
            } catch (e: Exception) {
                _error.value = e.message
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun loadTasksByProject(projectId: Long) {
        scope.launch {
            try {
                _isLoading.value = true
                _error.value = null
                _allTasks.value = tasksRepo.getByProject(projectId)
            } catch (e: Exception) {
                _error.value = e.message
            } finally {
                _isLoading.value = false
            }
        }
    }

    private fun generateCalendarDays(yearMonth: YearMonth, tasks: List<TaskDto>): List<CalendarDay> {
        val firstDayOfMonth = yearMonth.atDay(1)
        val lastDayOfMonth = yearMonth.atEndOfMonth()
        val startCalendar = firstDayOfMonth.minusDays((firstDayOfMonth.dayOfWeek.value % 7).toLong())

        val days = mutableListOf<CalendarDay>()
        var currentDate = startCalendar

        // Generar 6 semanas (42 días) para cubrir todo el calendario
        repeat(42) {
            val tasksForDay = tasks.filter { task ->
                val taskStartDate = LocalDate.parse(task.startDate.take(10))
                val taskEndDate = LocalDate.parse(task.endDate.take(10))
                currentDate >= taskStartDate && currentDate <= taskEndDate
            }

            days.add(
                CalendarDay(
                    day = currentDate.dayOfMonth,
                    date = currentDate,
                    tasks = tasksForDay,
                    isCurrentMonth = currentDate.month == yearMonth.month
                )
            )
            currentDate = currentDate.plusDays(1)
        }

        return days
    }

    private fun launchCatching(
        loading: MutableStateFlow<Boolean>,
        error: MutableStateFlow<String?>,
        block: suspend () -> Unit
    ) {
        scope.launch {
            try {
                loading.value = true
                error.value = null
                block()
            } catch (e: Exception) {
                error.value = e.message
            } finally {
                loading.value = false
            }
        }
    }
}
package com.example.taskmaster.views.layout.project

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.example.taskmaster.R
import com.example.taskmaster.viewmodel.data.tasks.TaskDto
import com.example.taskmaster.views.layout.task.EmptyTask
import com.example.taskmaster.views.layout.task.TaskList
import com.example.taskmaster.viewmodel.model.ProjectsViewModel
import com.example.taskmaster.viewmodel.model.TasksViewModel
import com.example.taskmaster.viewmodel.ui.users.UsersViewModel
import com.example.taskmaster.views.layout.task.TaskFilterDialog
import java.time.LocalDate
import java.time.format.DateTimeFormatter

// ---- enums de filtro ----
enum class PriorityFilter(val labels: List<String>) {
    HIGH(listOf("Alta", "High", "HIGH")),
    MEDIUM(listOf("Media", "Medium", "MEDIUM")),
    LOW(listOf("Baja", "Low", "LOW"))
}

enum class StatusFilter(val labels: List<String>) {
    TO_DO(listOf("Por hacer", "To Do", "TO_DO")),
    IN_PROGRESS(listOf("En progreso", "IN_PROGRESS", "In Progress")),
    DONE(listOf("Completada", "Done", "DONE"))
}

data class TaskFilters(
    val priority: PriorityFilter? = null,
    val status: StatusFilter? = null,
    val memberId: Long? = null,
    val dateFrom: String? = null,
    val dateTo: String? = null

)

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun ProjectTasks(
    nav: NavHostController,
    projectId: Long,
    pvm: ProjectsViewModel = remember { ProjectsViewModel() },
    tvm: TasksViewModel = remember { TasksViewModel() },
    usersVm: UsersViewModel = remember { UsersViewModel() }
) {
    val project by pvm.current.collectAsState()
    val isLoading by tvm.isLoading.collectAsState()
    val error by tvm.error.collectAsState()
    val tasks by tvm.tasks.collectAsState()
    val members by usersVm.members.collectAsState()
    var query by remember { mutableStateOf("") }

    // modal visible / oculto
    var showFilters by remember { mutableStateOf(false) }

    // estado actual de filtros seleccionados
    var filters by remember { mutableStateOf(TaskFilters()) }

    LaunchedEffect(projectId) {
        pvm.loadById(projectId)
        tvm.loadByProject(projectId)
        usersVm.loadMembersForProject(projectId)
    }

    Scaffold(
        topBar = {
            Column {
                ProjectTopBar(
                    title = project?.name ?: "",
                    onBack = { nav.popBackStack() }
                )
                ProjectMiniTabs(
                    selected = ProjectSection.TASKS,
                    onSelect = {
                        when (it) {
                            ProjectSection.TASKS    -> Unit
                            ProjectSection.STATS    -> nav.navigate("projectStats/$projectId") { launchSingleTop = true }
                            ProjectSection.SETTINGS -> nav.navigate("projectSettings/$projectId") { launchSingleTop = true }
                        }
                    }
                )
            }
        }
    ) { inner ->
        Column(
            Modifier
                .fillMaxSize()
                .padding(inner)
                .padding(horizontal = 12.dp, vertical = 10.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                var queryText by remember { mutableStateOf("") }

                TextField(
                    value = queryText,
                    onValueChange = { value ->
                        queryText = value
                        query = value
                    },
                    modifier = Modifier
                        .weight(1f)
                        .padding(top = 6.dp, bottom = 12.dp)
                        .heightIn(min = 40.dp),
                    singleLine = true,
                    textStyle = MaterialTheme.typography.bodyLarge,
                    placeholder = {
                        Text(
                            "Buscar tareas",
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                        )
                    },
                    trailingIcon = {
                        Icon(
                            painter = painterResource(R.drawable.ic_search),
                            contentDescription = "Buscar",
                            tint = MaterialTheme.colorScheme.onSurface,
                            modifier = Modifier.size(20.dp)
                        )
                    },
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor   = MaterialTheme.colorScheme.secondaryContainer,
                        unfocusedContainerColor = MaterialTheme.colorScheme.secondaryContainer,
                        focusedTextColor   = MaterialTheme.colorScheme.onSurface,
                        unfocusedTextColor = MaterialTheme.colorScheme.onSurface,
                        cursorColor        = MaterialTheme.colorScheme.primary,
                        focusedIndicatorColor   = Color.Transparent,
                        unfocusedIndicatorColor = Color.Transparent,
                        disabledIndicatorColor  = Color.Transparent,
                        errorIndicatorColor     = Color.Transparent
                    )
                )

                IconButton(
                    onClick = { showFilters = true },
                    modifier = Modifier.size(40.dp)
                ) {
                    Icon(
                        painter = painterResource(R.drawable.ic_filter),
                        contentDescription = "Filtrar",
                                modifier = Modifier.size(32.dp),
                    )
                }

                IconButton(
                    onClick = { nav.navigate("taskCreate/$projectId") },
                    modifier = Modifier.size(40.dp)
                ) {
                    Icon(
                        painter = painterResource(R.drawable.ic_add),
                        contentDescription = "Agregar"
                    )
                }
            }

            Spacer(Modifier.height(12.dp))

            Box(Modifier.fillMaxSize()) {
                when {
                    isLoading -> {
                        CircularProgressIndicator(Modifier.align(Alignment.Center))
                    }
                    !error.isNullOrBlank() -> {
                        Text(
                            text = error ?: "",
                            color = MaterialTheme.colorScheme.error,
                            modifier = Modifier.align(Alignment.Center)
                        )
                    }
                    tasks.isEmpty() -> {
                        EmptyTask()
                    }
                    else -> {
                        val formatter = DateTimeFormatter.ISO_LOCAL_DATE

                        val filtered = tasks
                            // búsqueda por texto
                            .filter { t ->
                                val q = query
                                val title = t.title ?: ""
                                val desc  = t.description ?: ""
                                title.contains(q, true) || desc.contains(q, true)
                            }
                            // prioridad
                            .filter { t ->
                                filters.priority?.let { pf ->
                                    val p = (t.priority?.toString() ?: "").trim().lowercase()
                                    when (pf) {
                                        PriorityFilter.HIGH   -> p in listOf("alta", "high", "high_priority", "highpriority")
                                        PriorityFilter.MEDIUM -> p in listOf("media", "medium", "medium_priority", "mediumpriority")
                                        PriorityFilter.LOW    -> p in listOf("baja", "low", "low_priority", "lowpriority")
                                    }
                                } ?: true
                            }
                            // estado
                            .filter { t ->
                                filters.status?.let { sf ->
                                    val st = (t.status?.toString() ?: "").uppercase()
                                    sf.labels.any { lbl -> st == lbl.uppercase() }
                                } ?: true
                            }
                            // miembro
                            .filter { t ->
                                filters.memberId?.let { uid ->
                                    val ids = t.assignedUserIds ?: emptyList<Long>()
                                    ids.contains(uid)
                                } ?: true
                            }
                            // rango de fechas (usa endDate; ajusta si quieres usar startDate)
                            .filter { t ->
                                val taskEnd = t.endDate
                                val fromOk = if (!filters.dateFrom.isNullOrBlank() && !taskEnd.isNullOrBlank()) {
                                    try {
                                        LocalDate.parse(taskEnd.substring(0, 10), formatter) >=
                                                LocalDate.parse(filters.dateFrom!!.substring(0, 10), formatter)
                                    } catch (_: Exception) { true }
                                } else true

                                val toOk = if (!filters.dateTo.isNullOrBlank() && !taskEnd.isNullOrBlank()) {
                                    try {
                                        LocalDate.parse(taskEnd.substring(0, 10), formatter) <=
                                                LocalDate.parse(filters.dateTo!!.substring(0, 10), formatter)
                                    } catch (_: Exception) { true }
                                } else true

                                fromOk && toOk
                            }

                        TaskList(
                            tasks = filtered,
                            members = members,
                            onTaskClick = {
                                nav.navigate("taskEdit/$projectId/${it.taskId}" )
                            }
                        )
                    }
                }
            }

            if (showFilters) {
                TaskFilterDialog(
                    visible = showFilters,
                    projectId = projectId,
                    usersVm = usersVm,
                    initialFilters = filters,
                    onApply = { newFilters ->
                        filters = newFilters
                        showFilters = false
                    },
                    onDismiss = { showFilters = false }
                )
            }
        }
    }
}

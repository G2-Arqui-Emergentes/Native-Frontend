package com.example.taskmaster.views.layout.project

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import coil.compose.AsyncImage
import com.example.taskmaster.R
import com.example.taskmaster.views.layout.task.TaskFilterDialog
import com.example.taskmaster.viewmodel.data.tasks.TaskDto
import com.example.taskmaster.viewmodel.data.tasks.TaskPriority
import com.example.taskmaster.viewmodel.data.tasks.TaskStatus
import com.example.taskmaster.viewmodel.data.users.UserDto
import com.example.taskmaster.viewmodel.model.ProjectsViewModel
import com.example.taskmaster.viewmodel.model.TasksViewModel
import com.example.taskmaster.viewmodel.ui.users.UsersViewModel
import java.time.LocalDate
import java.time.format.DateTimeFormatter

private val TasksBackground = Color(0xFFF9FAFB)
private val TasksSurface = Color(0xFFF1F2F4)
private val TasksColumnSurface = Color(0xFFF4F5F7)
private val TasksCardSurface = Color.White
private val TasksBorder = Color(0xFFE5E7EB)
private val TasksTextPrimary = Color(0xFF111827)
private val TasksTextSecondary = Color(0xFF6B7280)
private val TasksBrand = Color(0xFFEC1926)
private val TasksPositive = Color(0xFF0F9E6E)
private val TasksDanger = Color(0xFFEE4445)
private val TasksWarning = Color(0xFFF59E0B)

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
    val currentBackStackEntry by nav.currentBackStackEntryAsState()

    var query by remember { mutableStateOf("") }
    var showFilters by remember { mutableStateOf(false) }
    var filters by remember { mutableStateOf(TaskFilters()) }
    var pendingDeleteTask by remember { mutableStateOf<TaskDto?>(null) }

    LaunchedEffect(projectId) {
        pvm.loadById(projectId)
        tvm.loadByProject(projectId)
        usersVm.loadMembersForProject(projectId)
    }

    LaunchedEffect(currentBackStackEntry?.destination?.route) {
        val route = currentBackStackEntry?.destination?.route.orEmpty()
        if (route.startsWith("projectTasks/")) {
            tvm.loadByProject(projectId)
        }
    }

    val membersById = remember(members) { members.associateBy { it.id } }
    val filteredTasks = remember(tasks, query, filters, membersById) {
        val formatter = DateTimeFormatter.ISO_LOCAL_DATE

        tasks
            .filter { task ->
                if (query.isBlank()) return@filter true

                val assigneeName = task.assignedUserIds.firstOrNull()
                    ?.let(membersById::get)
                    ?.let { "${it.name} ${it.lastName}" }
                    .orEmpty()

                listOf(
                    task.title,
                    task.description,
                    assigneeName,
                    priorityLabel(task.priority),
                    statusLabel(task.status)
                ).any { value -> value.contains(query, ignoreCase = true) }
            }
            .filter { task ->
                filters.priority?.let { priority ->
                    when (priority) {
                        PriorityFilter.HIGH -> task.priority == TaskPriority.HIGH
                        PriorityFilter.MEDIUM -> task.priority == TaskPriority.MEDIUM
                        PriorityFilter.LOW -> task.priority == TaskPriority.LOW
                    }
                } ?: true
            }
            .filter { task ->
                filters.status?.let { status ->
                    when (status) {
                        StatusFilter.TO_DO -> task.status == TaskStatus.TO_DO
                        StatusFilter.IN_PROGRESS -> task.status == TaskStatus.IN_PROGRESS
                        StatusFilter.DONE -> task.status == TaskStatus.DONE
                    }
                } ?: true
            }
            .filter { task ->
                filters.memberId?.let(task.assignedUserIds::contains) ?: true
            }
            .filter { task ->
                val taskEnd = task.endDate
                val fromOk = if (!filters.dateFrom.isNullOrBlank() && taskEnd.isNotBlank()) {
                    try {
                        LocalDate.parse(taskEnd.substring(0, 10), formatter) >=
                            LocalDate.parse(filters.dateFrom!!.substring(0, 10), formatter)
                    } catch (_: Exception) {
                        true
                    }
                } else {
                    true
                }

                val toOk = if (!filters.dateTo.isNullOrBlank() && taskEnd.isNotBlank()) {
                    try {
                        LocalDate.parse(taskEnd.substring(0, 10), formatter) <=
                            LocalDate.parse(filters.dateTo!!.substring(0, 10), formatter)
                    } catch (_: Exception) {
                        true
                    }
                } else {
                    true
                }

                fromOk && toOk
            }
    }

    if (pendingDeleteTask != null) {
        AlertDialog(
            onDismissRequest = { pendingDeleteTask = null },
            confirmButton = {
                TextButton(
                    onClick = {
                        val task = pendingDeleteTask ?: return@TextButton
                        pendingDeleteTask = null
                        tvm.deleteFromProject(projectId, task.taskId)
                    }
                ) {
                    Text("Delete", color = TasksDanger)
                }
            },
            dismissButton = {
                TextButton(onClick = { pendingDeleteTask = null }) {
                    Text("Cancel", color = TasksTextSecondary)
                }
            },
            title = { Text("Delete task") },
            text = { Text("Are you sure you want to delete this task?") }
        )
    }

    Scaffold(
        containerColor = TasksBackground,
        topBar = {
            Column {
                ProjectTopBar(
                    title = project?.name ?: "",
                    onBack = { nav.popBackStack() },
                    onNotificationsClick = { nav.navigate("notification") },
                    onProfileClick = { nav.navigate("profile") }
                )
                ProjectMiniTabs(
                    selected = ProjectSection.TASKS,
                    onSelect = {
                        when (it) {
                            ProjectSection.TASKS -> Unit
                            ProjectSection.STATS -> nav.navigate("projectStats/$projectId") { launchSingleTop = true }
                            ProjectSection.SETTINGS -> nav.navigate("projectSettings/$projectId") { launchSingleTop = true }
                        }
                    }
                )
            }
        }
    ) { inner ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(TasksBackground)
                .padding(inner)
                .padding(horizontal = 16.dp, vertical = 12.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "Tasks",
                        style = MaterialTheme.typography.headlineSmall.copy(
                            fontWeight = FontWeight.Bold,
                            color = TasksTextPrimary
                        )
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = project?.name ?: "Project board",
                        style = MaterialTheme.typography.bodyMedium,
                        color = TasksTextSecondary
                    )
                }

                IconButton(onClick = { nav.navigate("taskCreate/$projectId") }) {
                    Icon(
                        painter = painterResource(R.drawable.ic_add),
                        contentDescription = "Create task",
                        tint = TasksBrand,
                        modifier = Modifier.size(24.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                TextField(
                    value = query,
                    onValueChange = { query = it },
                    modifier = Modifier.weight(1f),
                    singleLine = true,
                    textStyle = MaterialTheme.typography.bodyLarge,
                    placeholder = {
                        Text(
                            "Search tasks",
                            style = MaterialTheme.typography.bodyLarge,
                            color = TasksTextSecondary
                        )
                    },
                    trailingIcon = {
                        Icon(
                            painter = painterResource(R.drawable.ic_search),
                            contentDescription = "Search",
                            tint = TasksTextSecondary,
                            modifier = Modifier.size(20.dp)
                        )
                    },
                    shape = RoundedCornerShape(24.dp),
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = TasksSurface,
                        unfocusedContainerColor = TasksSurface,
                        focusedTextColor = TasksTextPrimary,
                        unfocusedTextColor = TasksTextPrimary,
                        cursorColor = TasksBrand,
                        focusedIndicatorColor = Color.Transparent,
                        unfocusedIndicatorColor = Color.Transparent,
                        disabledIndicatorColor = Color.Transparent,
                        errorIndicatorColor = Color.Transparent
                    )
                )

                IconButton(
                    onClick = { showFilters = true },
                    modifier = Modifier
                        .size(44.dp)
                        .clip(RoundedCornerShape(14.dp))
                        .background(TasksSurface)
                        .border(1.dp, TasksBorder, RoundedCornerShape(14.dp))
                ) {
                    Icon(
                        painter = painterResource(R.drawable.ic_filter),
                        contentDescription = "Filter tasks",
                        tint = TasksTextPrimary,
                        modifier = Modifier.size(22.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Box(modifier = Modifier.fillMaxSize()) {
                when {
                    isLoading -> {
                        CircularProgressIndicator(
                            modifier = Modifier.align(Alignment.Center),
                            color = TasksBrand
                        )
                    }

                    !error.isNullOrBlank() -> {
                        Text(
                            text = error.orEmpty(),
                            color = TasksDanger,
                            modifier = Modifier.align(Alignment.Center)
                        )
                    }

                    tasks.isEmpty() -> {
                        EmptyTaskBoard(
                            onCreateTask = { nav.navigate("taskCreate/$projectId") }
                        )
                    }

                    else -> {
                        val columns = listOf(
                            TaskStatus.TO_DO,
                            TaskStatus.IN_PROGRESS,
                            TaskStatus.DONE
                        )

                        LazyRow(
                            horizontalArrangement = Arrangement.spacedBy(16.dp),
                            contentPadding = PaddingValues(end = 8.dp)
                        ) {
                            items(columns, key = { it.name }) { status ->
                                val tasksInColumn = filteredTasks.filter { it.status == status }
                                TaskColumn(
                                    status = status,
                                    tasks = tasksInColumn,
                                    membersById = membersById,
                                    onEdit = { task ->
                                        nav.navigate("taskEdit/$projectId/${task.taskId}")
                                    },
                                    onDelete = { task ->
                                        pendingDeleteTask = task
                                    }
                                )
                            }
                        }
                    }
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

@Composable
private fun EmptyTaskBoard(
    onCreateTask: () -> Unit
) {
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "No tasks yet",
            style = MaterialTheme.typography.headlineSmall.copy(
                fontWeight = FontWeight.SemiBold,
                color = TasksTextPrimary
            )
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "Create the first task for this project.",
            style = MaterialTheme.typography.bodyMedium,
            color = TasksTextSecondary
        )
        Spacer(modifier = Modifier.height(18.dp))
        Button(
            onClick = onCreateTask,
            colors = ButtonDefaults.buttonColors(containerColor = TasksBrand),
            shape = RoundedCornerShape(16.dp)
        ) {
            Text("Create task")
        }
    }
}

@Composable
private fun TaskColumn(
    status: TaskStatus,
    tasks: List<TaskDto>,
    membersById: Map<Long, UserDto>,
    onEdit: (TaskDto) -> Unit,
    onDelete: (TaskDto) -> Unit
) {
    Card(
        modifier = Modifier
            .width(312.dp)
            .heightIn(min = 420.dp, max = 560.dp),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = TasksColumnSurface),
        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = statusLabel(status),
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold,
                        color = TasksTextPrimary
                    ),
                    modifier = Modifier.weight(1f)
                )
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(999.dp))
                        .background(statusCountBackground(status))
                        .padding(horizontal = 10.dp, vertical = 5.dp)
                ) {
                    Text(
                        text = tasks.size.toString(),
                        style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.SemiBold),
                        color = statusCountColor(status)
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            if (tasks.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(180.dp)
                        .clip(RoundedCornerShape(18.dp))
                        .background(Color.White.copy(alpha = 0.65f))
                        .border(1.dp, TasksBorder, RoundedCornerShape(18.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "No tasks yet",
                        style = MaterialTheme.typography.bodyMedium,
                        color = TasksTextSecondary
                    )
                }
            } else {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(14.dp),
                    contentPadding = PaddingValues(top = 4.dp, bottom = 8.dp, end = 2.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    items(tasks, key = { it.taskId }) { task ->
                        KanbanTaskCard(
                            task = task,
                            assignee = task.assignedUserIds.firstOrNull()?.let(membersById::get),
                            onEdit = { onEdit(task) },
                            onDelete = { onDelete(task) }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun KanbanTaskCard(
    task: TaskDto,
    assignee: UserDto?,
    onEdit: () -> Unit,
    onDelete: () -> Unit
) {
    var showMenu by remember { mutableStateOf(false) }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 2.dp),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = TasksCardSurface),
        elevation = CardDefaults.cardElevation(defaultElevation = 5.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.Top
            ) {
                Text(
                    text = task.title,
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.SemiBold,
                        color = TasksTextPrimary
                    ),
                    modifier = Modifier.weight(1f),
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )

                Box {
                    IconButton(
                        onClick = { showMenu = true },
                        modifier = Modifier.size(28.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.MoreVert,
                            contentDescription = "Task options",
                            tint = TasksTextSecondary
                        )
                    }

                    DropdownMenu(
                        expanded = showMenu,
                        onDismissRequest = { showMenu = false }
                    ) {
                        DropdownMenuItem(
                            text = { Text("Edit") },
                            onClick = {
                                showMenu = false
                                onEdit()
                            }
                        )
                        DropdownMenuItem(
                            text = { Text("Delete") },
                            onClick = {
                                showMenu = false
                                onDelete()
                            }
                        )
                    }
                }
            }

            if (task.description.isNotBlank()) {
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = task.description,
                    style = MaterialTheme.typography.bodyMedium,
                    color = TasksTextSecondary,
                    maxLines = 3,
                    overflow = TextOverflow.Ellipsis
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                TaskAssigneeAvatar(assignee = assignee)
                Spacer(modifier = Modifier.width(10.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "Assigned",
                        style = MaterialTheme.typography.labelSmall,
                        color = TasksTextSecondary
                    )
                    Text(
                        text = assignee?.let { "${it.name} ${it.lastName}" } ?: "Unassigned",
                        style = MaterialTheme.typography.bodySmall.copy(
                            fontWeight = FontWeight.Medium,
                            color = TasksTextPrimary
                        ),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
                TaskPriorityBadge(priority = task.priority)
            }
        }
    }
}

@Composable
private fun TaskAssigneeAvatar(assignee: UserDto?) {
    if (assignee?.imageUrl.isNullOrBlank()) {
        Image(
            painter = painterResource(R.drawable.ic_user),
            contentDescription = "Assignee",
            modifier = Modifier
                .size(34.dp)
                .clip(CircleShape)
                .background(TasksSurface)
                .padding(7.dp)
        )
    } else {
        AsyncImage(
            model = assignee?.imageUrl,
            contentDescription = "Assignee",
            modifier = Modifier
                .size(34.dp)
                .clip(CircleShape),
            contentScale = ContentScale.Crop
        )
    }
}

@Composable
private fun TaskPriorityBadge(priority: TaskPriority) {
    val background = when (priority) {
        TaskPriority.HIGH -> TasksDanger.copy(alpha = 0.12f)
        TaskPriority.MEDIUM -> TasksWarning.copy(alpha = 0.16f)
        TaskPriority.LOW -> TasksPositive.copy(alpha = 0.12f)
    }
    val textColor = when (priority) {
        TaskPriority.HIGH -> TasksDanger
        TaskPriority.MEDIUM -> TasksWarning
        TaskPriority.LOW -> TasksPositive
    }

    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(999.dp))
            .background(background)
            .padding(horizontal = 10.dp, vertical = 6.dp)
    ) {
        Text(
            text = priorityLabel(priority),
            style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.SemiBold),
            color = textColor
        )
    }
}

private fun priorityLabel(priority: TaskPriority): String {
    return when (priority) {
        TaskPriority.HIGH -> "High"
        TaskPriority.MEDIUM -> "Medium"
        TaskPriority.LOW -> "Low"
    }
}

private fun statusLabel(status: TaskStatus): String {
    return when (status) {
        TaskStatus.TO_DO -> "To Do"
        TaskStatus.IN_PROGRESS -> "In Progress"
        TaskStatus.DONE -> "Done"
    }
}

private fun statusCountBackground(status: TaskStatus): Color {
    return when (status) {
        TaskStatus.TO_DO -> TasksBrand.copy(alpha = 0.10f)
        TaskStatus.IN_PROGRESS -> TasksWarning.copy(alpha = 0.14f)
        TaskStatus.DONE -> TasksPositive.copy(alpha = 0.12f)
    }
}

private fun statusCountColor(status: TaskStatus): Color {
    return when (status) {
        TaskStatus.TO_DO -> TasksBrand
        TaskStatus.IN_PROGRESS -> TasksWarning
        TaskStatus.DONE -> TasksPositive
    }
}

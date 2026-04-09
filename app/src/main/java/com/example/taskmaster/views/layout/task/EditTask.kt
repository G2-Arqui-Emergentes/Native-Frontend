package com.example.taskmaster.views.layout.task

import android.app.DatePickerDialog
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.example.taskmaster.R
import com.example.taskmaster.views.layout.project.ProjectTopBar
import com.example.taskmaster.viewmodel.data.tasks.TaskPriority
import com.example.taskmaster.viewmodel.data.tasks.TaskStatus
import com.example.taskmaster.viewmodel.data.tasks.TaskUpdateRequest
import com.example.taskmaster.viewmodel.model.ProjectsViewModel
import com.example.taskmaster.viewmodel.model.TasksViewModel
import com.example.taskmaster.viewmodel.ui.users.UsersViewModel
import java.util.Calendar

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditTask(
    nav: NavHostController,
    projectId: Long,
    taskId: Long,
    projectsVm: ProjectsViewModel = remember { ProjectsViewModel() },
    tasksVm: TasksViewModel = remember { TasksViewModel() },
    usersVm: UsersViewModel = remember { UsersViewModel() }
) {
    val isLoading by tasksVm.isLoading.collectAsState()
    val error by tasksVm.error.collectAsState()
    val project by projectsVm.current.collectAsState()
    val task by tasksVm.selected.collectAsState()
    val members by usersVm.members.collectAsState()

    // ------- ESTADO DEL FORM -------
    var title by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var startDate by remember { mutableStateOf("") }
    var endDate by remember { mutableStateOf("") }

    var priority by remember { mutableStateOf(TaskPriority.MEDIUM) }
    var priorityMenu by remember { mutableStateOf(false) }

    var status by remember { mutableStateOf(TaskStatus.TO_DO) }
    var statusMenu by remember { mutableStateOf(false) }

    var selectedMemberId by remember { mutableStateOf<Long?>(null) }
    var memberMenu by remember { mutableStateOf(false) }

    var initialized by remember { mutableStateOf(false) }

    // Cargar proyecto, tarea y miembros
    LaunchedEffect(projectId, taskId) {
        projectsVm.loadById(projectId)
        tasksVm.loadById(taskId)
        usersVm.loadMembersForProject(projectId)
    }

    // Rellenar campos cuando llegue la tarea (una sola vez)
    LaunchedEffect(task) {
        if (task != null && !initialized) {
            title = task!!.title
            description = task!!.description
            startDate = task!!.startDate.take(10)
            endDate   = task!!.endDate.take(10)

            // String -> enum
            val rawPriority = task!!.priority?.toString()?.uppercase()
            priority = when (rawPriority) {
                "HIGH"   -> TaskPriority.HIGH
                "LOW"    -> TaskPriority.LOW
                else     -> TaskPriority.MEDIUM
            }

            val rawStatus = task!!.status?.toString()?.uppercase()
            status = when (rawStatus) {
                "IN_PROGRESS" -> TaskStatus.IN_PROGRESS
                "DONE"        -> TaskStatus.DONE
                else          -> TaskStatus.TO_DO
            }

            selectedMemberId = task!!.assignedUserIds.firstOrNull()
            initialized = true
        }
    }

    Column(Modifier.fillMaxSize(), horizontalAlignment = Alignment.CenterHorizontally) {
        ProjectTopBar(title = project?.name ?: "Proyecto", onBack = { nav.popBackStack() })

        // Sub-topbar
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(MaterialTheme.colorScheme.secondaryContainer)
                .padding(horizontal = 12.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = { nav.popBackStack() }) {
                Icon(
                    painter = painterResource(R.drawable.ic_close),
                    contentDescription = "Cerrar",
                    tint = MaterialTheme.colorScheme.onSecondaryContainer
                )
            }
            Text(
                text = "Editar tarea",
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.onSecondaryContainer,
                textAlign = TextAlign.Center,
                modifier = Modifier.weight(1f)
            )
            Spacer(Modifier.width(48.dp))
        }

        Spacer(Modifier.height(8.dp))

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
        ) {
            LabeledField("Título") { PinkField(title) { title = it } }

            LabeledField("Descripción") {
                PinkField(description, singleLine = false) { description = it }
            }

            // ------- PRIORIDAD -------
            LabeledField("Prioridad") {
                ExposedDropdownMenuBox(
                    expanded = priorityMenu,
                    onExpandedChange = { priorityMenu = !priorityMenu }
                ) {
                    OutlinedTextField(
                        value = when (priority) {
                            TaskPriority.HIGH   -> "Alta"
                            TaskPriority.MEDIUM -> "Media"
                            TaskPriority.LOW    -> "Baja"
                        },
                        onValueChange = {},
                        readOnly = true,
                        modifier = Modifier
                            .menuAnchor()
                            .fillMaxWidth(),
                        singleLine = true,
                        shape = RoundedCornerShape(10.dp),
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(priorityMenu) }
                    )
                    ExposedDropdownMenu(
                        expanded = priorityMenu,
                        onDismissRequest = { priorityMenu = false }
                    ) {
                        DropdownMenuItem(text = { Text("Alta") }, onClick = {
                            priority = TaskPriority.HIGH; priorityMenu = false
                        })
                        DropdownMenuItem(text = { Text("Media") }, onClick = {
                            priority = TaskPriority.MEDIUM; priorityMenu = false
                        })
                        DropdownMenuItem(text = { Text("Baja") }, onClick = {
                            priority = TaskPriority.LOW; priorityMenu = false
                        })
                    }
                }
            }

            // ------- ESTADO -------
            LabeledField("Estado") {
                ExposedDropdownMenuBox(
                    expanded = statusMenu,
                    onExpandedChange = { statusMenu = !statusMenu }
                ) {
                    OutlinedTextField(
                        value = when (status) {
                            TaskStatus.TO_DO       -> "Por hacer"
                            TaskStatus.IN_PROGRESS -> "En progreso"
                            TaskStatus.DONE        -> "Completada"
                        },
                        onValueChange = {},
                        readOnly = true,
                        modifier = Modifier
                            .menuAnchor()
                            .fillMaxWidth(),
                        singleLine = true,
                        shape = RoundedCornerShape(10.dp),
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(statusMenu) }
                    )
                    ExposedDropdownMenu(
                        expanded = statusMenu,
                        onDismissRequest = { statusMenu = false }
                    ) {
                        DropdownMenuItem(text = { Text("Por hacer") }, onClick = {
                            status = TaskStatus.TO_DO; statusMenu = false
                        })
                        DropdownMenuItem(text = { Text("En progreso") }, onClick = {
                            status = TaskStatus.IN_PROGRESS; statusMenu = false
                        })
                        DropdownMenuItem(text = { Text("Completada") }, onClick = {
                            status = TaskStatus.DONE; statusMenu = false
                        })
                    }
                }
            }

            // ------- MIEMBRO -------
            LabeledField("Miembro asignado") {
                ExposedDropdownMenuBox(
                    expanded = memberMenu,
                    onExpandedChange = { memberMenu = !memberMenu }
                ) {
                    val selectedName = selectedMemberId?.let { id ->
                        members.firstOrNull { it.id == id }?.let { "${it.name} ${it.lastName}" }
                    } ?: "Sin asignar"

                    OutlinedTextField(
                        value = selectedName,
                        onValueChange = {},
                        readOnly = true,
                        modifier = Modifier
                            .menuAnchor()
                            .fillMaxWidth(),
                        singleLine = true,
                        shape = RoundedCornerShape(10.dp),
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(memberMenu) }
                    )

                    ExposedDropdownMenu(
                        expanded = memberMenu,
                        onDismissRequest = { memberMenu = false }
                    ) {
                        DropdownMenuItem(
                            text = { Text("Sin asignar") },
                            onClick = {
                                selectedMemberId = null
                                memberMenu = false
                            }
                        )
                        members.forEach { m ->
                            DropdownMenuItem(
                                text = { Text("${m.name} ${m.lastName}") },
                                onClick = {
                                    selectedMemberId = m.id
                                    memberMenu = false
                                }
                            )
                        }
                    }
                }
            }

            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                LabeledField("Fecha inicio", Modifier.weight(1f)) {
                    DateField(value = startDate) { startDate = it }
                }
                LabeledField("Fecha fin", Modifier.weight(1f)) {
                    DateField(value = endDate) { endDate = it }
                }
            }

            Spacer(Modifier.height(24.dp))

            Button(
                onClick = {
                    if (title.isNotBlank() && endDate.isNotBlank()) {
                        val req = TaskUpdateRequest(
                            title = title,
                            description = description,
                            startDate = startDate,
                            endDate = endDate,
                            status = status,            // 👈 enum, NO .name
                            priority = priority,        // 👈 enum, NO .name
                            assignedUserIds = selectedMemberId?.let { listOf(it) } ?: emptyList()
                        )
                        tasksVm.update(taskId, req)
                        nav.popBackStack()
                    }
                },
                shape = RoundedCornerShape(12.dp),
                enabled = !isLoading && task != null,
                modifier = Modifier
                    .align(Alignment.CenterHorizontally)
                    .widthIn(min = 160.dp)
            ) {
                if (isLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(18.dp),
                        strokeWidth = 2.dp,
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                    Spacer(Modifier.width(8.dp))
                }
                Text("Guardar cambios", style = MaterialTheme.typography.titleMedium)
            }

            if (!error.isNullOrBlank()) {
                Spacer(Modifier.height(12.dp))
                Text(error ?: "", color = MaterialTheme.colorScheme.error)
            }
        }
    }
}

/* ---------- helpers ---------- */

@Composable
private fun LabeledField(
    label: String,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    Column(modifier = modifier.padding(top = 8.dp, bottom = 10.dp)) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onBackground,
            modifier = Modifier.padding(start = 8.dp, bottom = 4.dp)
        )
        content()
    }
}

@Composable
private fun PinkField(
    value: String,
    singleLine: Boolean = true,
    onValueChange: (String) -> Unit
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        modifier = Modifier.fillMaxWidth(),
        singleLine = singleLine,
        shape = RoundedCornerShape(10.dp)
    )
}

@Composable
private fun DateField(value: String, onPick: (String) -> Unit) {
    val ctx = LocalContext.current
    val cal = Calendar.getInstance()

    fun openPicker() {
        DatePickerDialog(
            ctx,
            { _, y, m, d ->
                val mm = (m + 1).toString().padStart(2, '0')
                val dd = d.toString().padStart(2, '0')
                onPick("$y-$mm-$dd")
            },
            cal.get(Calendar.YEAR),
            cal.get(Calendar.MONTH),
            cal.get(Calendar.DAY_OF_MONTH)
        ).show()
    }

    OutlinedTextField(
        value = value,
        onValueChange = { },
        modifier = Modifier.fillMaxWidth(),
        singleLine = true,
        readOnly = true,
        trailingIcon = {
            Icon(
                painter = painterResource(R.drawable.ic_calendar),
                contentDescription = "Elegir fecha",
                modifier = Modifier
                    .size(22.dp)
                    .noRippleClickable { openPicker() },
                tint = MaterialTheme.colorScheme.onBackground
            )
        },
        shape = RoundedCornerShape(10.dp)
    )
}

private fun Modifier.noRippleClickable(onClick: () -> Unit): Modifier =
    composed {
        val interaction = remember { MutableInteractionSource() }
        clickable(interactionSource = interaction, indication = null, onClick = onClick)
    }

package com.example.taskmaster.views.layout.task

import android.app.DatePickerDialog
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
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
import androidx.compose.ui.composed
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.example.taskmaster.R
import com.example.taskmaster.views.layout.project.ProjectTopBar
import com.example.taskmaster.viewmodel.data.tasks.TaskCreateRequest
import com.example.taskmaster.viewmodel.data.tasks.TaskPriority
import com.example.taskmaster.viewmodel.data.tasks.TaskStatus
import com.example.taskmaster.viewmodel.data.users.UserDto
import com.example.taskmaster.viewmodel.model.ProjectsViewModel
import com.example.taskmaster.viewmodel.model.TasksViewModel
import com.example.taskmaster.viewmodel.ui.users.UsersViewModel
import java.util.Calendar

private val CreateTaskBackground = Color(0xFFF9FAFB)
private val CreateTaskSurface = Color(0xFFF4F5F7)
private val CreateTaskField = Color(0xFFF1F2F4)
private val CreateTaskBorder = Color(0xFFE5E7EB)
private val CreateTaskTextPrimary = Color(0xFF111827)
private val CreateTaskTextSecondary = Color(0xFF6B7280)
private val CreateTaskBrand = Color(0xFFEC1926)
private val CreateTaskPositive = Color(0xFF0F9E6E)
private val CreateTaskDanger = Color(0xFFEE4445)

@OptIn(androidx.compose.material3.ExperimentalMaterial3Api::class)
@Composable
fun CreateTask(
    nav: NavHostController,
    projectId: Long,
    projectsVm: ProjectsViewModel = remember { ProjectsViewModel() },
    tasksVm: TasksViewModel = remember { TasksViewModel() },
    usersVm: UsersViewModel = remember { UsersViewModel() }
) {
    val isLoading by tasksVm.isLoading.collectAsState()
    val taskError by tasksVm.error.collectAsState()
    val project by projectsVm.current.collectAsState()

    val members by usersVm.members.collectAsState()
    val membersLoading by usersVm.isLoading.collectAsState()
    val membersError by usersVm.error.collectAsState()

    LaunchedEffect(projectId) {
        projectsVm.loadById(projectId)
        usersVm.loadMembersForProject(projectId)
    }

    var title by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var endDate by remember { mutableStateOf("") }
    var priority by remember { mutableStateOf(TaskPriority.MEDIUM) }
    var priorityMenu by remember { mutableStateOf(false) }

    var memberMenu by remember { mutableStateOf(false) }
    var selectedMember by remember { mutableStateOf<UserDto?>(null) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(CreateTaskBackground)
    ) {
        ProjectTopBar(
            title = project?.name ?: "Project",
            onBack = { nav.popBackStack() },
            onNotificationsClick = { nav.navigate("notification") },
            onProfileClick = { nav.navigate("profile") }
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 16.dp, vertical = 12.dp)
        ) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = CreateTaskSurface),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 14.dp, vertical = 14.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(
                        onClick = { nav.popBackStack() },
                        modifier = Modifier
                            .size(42.dp)
                            .background(Color.White, RoundedCornerShape(14.dp))
                    ) {
                        Icon(
                            painter = painterResource(R.drawable.ic_close),
                            contentDescription = "Close",
                            tint = CreateTaskTextPrimary
                        )
                    }

                    Spacer(modifier = Modifier.width(12.dp))

                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Task",
                            style = MaterialTheme.typography.labelMedium.copy(
                                fontWeight = FontWeight.SemiBold
                            ),
                            color = CreateTaskBrand
                        )
                        Text(
                            text = "Create Task",
                            style = MaterialTheme.typography.headlineSmall.copy(
                                fontWeight = FontWeight.Bold
                            ),
                            color = CreateTaskTextPrimary
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(28.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 18.dp, vertical = 18.dp)
                ) {
                    Text(
                        text = "Task details",
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontWeight = FontWeight.Bold
                        ),
                        color = CreateTaskTextPrimary
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "Add the task information and assign it to a project member.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = CreateTaskTextSecondary
                    )

                    Spacer(modifier = Modifier.height(18.dp))

                    LabeledField("Title") {
                        StyledField(
                            value = title,
                            onValueChange = { title = it }
                        )
                    }

                    LabeledField("Description") {
                        StyledField(
                            value = description,
                            onValueChange = { description = it },
                            singleLine = false
                        )
                    }

                    LabeledField("Priority") {
                        ExposedDropdownMenuBox(
                            expanded = priorityMenu,
                            onExpandedChange = { priorityMenu = !priorityMenu }
                        ) {
                            OutlinedTextField(
                                value = priorityLabel(priority),
                                onValueChange = {},
                                readOnly = true,
                                modifier = Modifier
                                    .menuAnchor()
                                    .fillMaxWidth(),
                                singleLine = true,
                                textStyle = MaterialTheme.typography.bodySmall,
                                shape = RoundedCornerShape(18.dp),
                                trailingIcon = {
                                    ExposedDropdownMenuDefaults.TrailingIcon(priorityMenu)
                                },
                                colors = createTaskFieldColors()
                            )
                            ExposedDropdownMenu(
                                expanded = priorityMenu,
                                onDismissRequest = { priorityMenu = false }
                            ) {
                                DropdownMenuItem(
                                    text = { Text("High") },
                                    onClick = {
                                        priority = TaskPriority.HIGH
                                        priorityMenu = false
                                    }
                                )
                                DropdownMenuItem(
                                    text = { Text("Medium") },
                                    onClick = {
                                        priority = TaskPriority.MEDIUM
                                        priorityMenu = false
                                    }
                                )
                                DropdownMenuItem(
                                    text = { Text("Low") },
                                    onClick = {
                                        priority = TaskPriority.LOW
                                        priorityMenu = false
                                    }
                                )
                            }
                        }
                    }

                    LabeledField("Assign to") {
                        ExposedDropdownMenuBox(
                            expanded = memberMenu,
                            onExpandedChange = {
                                if (!membersLoading && members.isNotEmpty()) {
                                    memberMenu = !memberMenu
                                }
                            }
                        ) {
                            OutlinedTextField(
                                value = when {
                                    membersLoading -> "Loading members..."
                                    !membersError.isNullOrBlank() -> "Unable to load members"
                                    selectedMember != null -> "${selectedMember!!.name} ${selectedMember!!.lastName}"
                                    else -> "Select a member"
                                },
                                onValueChange = {},
                                readOnly = true,
                                modifier = Modifier
                                    .menuAnchor()
                                    .fillMaxWidth(),
                                singleLine = true,
                                enabled = !membersLoading && members.isNotEmpty(),
                                textStyle = MaterialTheme.typography.bodySmall,
                                shape = RoundedCornerShape(18.dp),
                                trailingIcon = {
                                    ExposedDropdownMenuDefaults.TrailingIcon(expanded = memberMenu)
                                },
                                colors = createTaskFieldColors()
                            )

                            ExposedDropdownMenu(
                                expanded = memberMenu,
                                onDismissRequest = { memberMenu = false }
                            ) {
                                members.forEach { member ->
                                    DropdownMenuItem(
                                        text = { Text("${member.name} ${member.lastName}") },
                                        onClick = {
                                            selectedMember = member
                                            memberMenu = false
                                        }
                                    )
                                }
                            }
                        }
                    }

                    LabeledField("End date") {
                        StyledDateField(
                            value = endDate,
                            onPick = { endDate = it }
                        )
                    }

                    Spacer(modifier = Modifier.height(22.dp))

                    Button(
                        onClick = {
                            if (title.isNotBlank() && endDate.isNotBlank()) {
                                val req = TaskCreateRequest(
                                    projectId = projectId,
                                    title = title,
                                    description = description,
                                    startDate = "",
                                    endDate = endDate,
                                    status = TaskStatus.TO_DO.name,
                                    priority = priority.name,
                                    assignedUserIds = selectedMember?.let { listOf(it.id) } ?: emptyList()
                                )
                                tasksVm.create(req)
                                nav.popBackStack()
                            }
                        },
                        enabled = !isLoading,
                        shape = RoundedCornerShape(18.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = CreateTaskBrand,
                            contentColor = Color.White
                        ),
                        modifier = Modifier
                            .align(Alignment.CenterHorizontally)
                            .fillMaxWidth()
                            .height(54.dp)
                    ) {
                        if (isLoading) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(18.dp),
                                strokeWidth = 2.dp,
                                color = Color.White
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                        }
                        Text(
                            text = "Create task",
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.SemiBold
                            )
                        )
                    }

                    if (!taskError.isNullOrBlank()) {
                        Spacer(modifier = Modifier.height(14.dp))
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(
                                    CreateTaskDanger.copy(alpha = 0.08f),
                                    RoundedCornerShape(16.dp)
                                )
                                .border(
                                    1.dp,
                                    CreateTaskDanger.copy(alpha = 0.22f),
                                    RoundedCornerShape(16.dp)
                                )
                                .padding(12.dp)
                        ) {
                            Text(
                                text = taskError.orEmpty(),
                                color = CreateTaskDanger,
                                style = MaterialTheme.typography.bodyMedium
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@Composable
private fun LabeledField(
    label: String,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    Column(modifier = modifier.padding(top = 8.dp, bottom = 10.dp)) {
        Text(
            text = label.uppercase(),
            style = MaterialTheme.typography.labelSmall.copy(
                fontWeight = FontWeight.SemiBold
            ),
            color = CreateTaskTextSecondary,
            modifier = Modifier.padding(start = 8.dp, bottom = 6.dp)
        )
        content()
    }
}

@Composable
private fun StyledField(
    value: String,
    onValueChange: (String) -> Unit,
    singleLine: Boolean = true
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        modifier = Modifier.fillMaxWidth(),
        singleLine = singleLine,
        minLines = if (singleLine) 1 else 4,
        shape = RoundedCornerShape(18.dp),
        colors = createTaskFieldColors()
    )
}

@Composable
private fun StyledDateField(
    value: String,
    onPick: (String) -> Unit
) {
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
        onValueChange = {},
        modifier = Modifier.fillMaxWidth(),
        singleLine = true,
        readOnly = true,
        trailingIcon = {
            Icon(
                painter = painterResource(R.drawable.ic_calendar),
                contentDescription = "Pick date",
                modifier = Modifier
                    .size(22.dp)
                    .noRippleClickable { openPicker() },
                tint = CreateTaskTextSecondary
            )
        },
        shape = RoundedCornerShape(18.dp),
        colors = createTaskFieldColors()
    )
}

@Composable
private fun createTaskFieldColors() = TextFieldDefaults.colors(
    focusedContainerColor = CreateTaskField,
    unfocusedContainerColor = CreateTaskField,
    disabledContainerColor = CreateTaskField,
    focusedTextColor = CreateTaskTextPrimary,
    unfocusedTextColor = CreateTaskTextPrimary,
    disabledTextColor = CreateTaskTextSecondary,
    cursorColor = CreateTaskBrand,
    focusedIndicatorColor = Color.Transparent,
    unfocusedIndicatorColor = Color.Transparent,
    disabledIndicatorColor = Color.Transparent
)

private fun priorityLabel(priority: TaskPriority): String {
    return when (priority) {
        TaskPriority.HIGH -> "High"
        TaskPriority.MEDIUM -> "Medium"
        TaskPriority.LOW -> "Low"
    }
}

private fun Modifier.noRippleClickable(onClick: () -> Unit): Modifier =
    composed {
        val interaction = remember { MutableInteractionSource() }
        clickable(interactionSource = interaction, indication = null, onClick = onClick)
    }

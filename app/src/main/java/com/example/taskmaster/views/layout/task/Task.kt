package com.example.taskmaster.views.layout.task

import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
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
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import coil.compose.AsyncImage
import com.example.taskmaster.R
import com.example.taskmaster.views.layout.common.AppTopHeader
import com.example.taskmaster.views.layout.project.EmptyProjects
import com.example.taskmaster.viewmodel.data.projects.ProjectDto
import com.example.taskmaster.viewmodel.model.ProjectsViewModel
import com.example.taskmaster.viewmodel.sharedPreferences.Prefs
import com.example.taskmaster.viewmodel.ui.users.UsersViewModel
import java.time.LocalDateTime
import java.time.OffsetDateTime
import java.time.ZoneId
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeParseException

private val ProjectsBackground = Color(0xFFF9FAFB)
private val ProjectsSurface = Color(0xFFF4F5F7)
private val ProjectsBorder = Color(0xFFE5E7EB)
private val ProjectsTextPrimary = Color(0xFF111827)
private val ProjectsTextSecondary = Color(0xFF6B7280)
private val ProjectsAccent = Color(0xFFEC1926)
private val ProjectsPositive = Color(0xFF0F9E6E)
private val ProjectsDanger = Color(0xFFEE4445)

@Composable
fun Task(
    context: Context,
    nav: NavHostController,
    projectsVm: ProjectsViewModel = remember { ProjectsViewModel() },
    userVm: UsersViewModel = remember { UsersViewModel() }
) {
    val clipboard = LocalClipboardManager.current
    val isLoading by projectsVm.isLoading.collectAsState()
    val error by projectsVm.error.collectAsState()
    val projects by projectsVm.projects.collectAsState()

    val user by userVm.user.collectAsState()
    val userLoading by userVm.isLoading.collectAsState()

    var query by remember { mutableStateOf("") }
    var pendingDeleteProject by remember { mutableStateOf<ProjectDto?>(null) }

    LaunchedEffect(Unit) {
        Prefs.loadEmail(context)?.let(userVm::loadByEmail)
    }

    LaunchedEffect(user?.id, user?.roles) {
        val currentUser = user ?: return@LaunchedEffect
        val isLeader = currentUser.roles.any { it.equals("ROLE_LEADER", ignoreCase = true) }
        if (isLeader) {
            projectsVm.loadByLeader(currentUser.id)
        } else {
            projectsVm.loadByMember()
        }
    }

    val filteredProjects = remember(projects, query) {
        if (query.isBlank()) {
            projects
        } else {
            projects.filter {
                it.name.contains(query, ignoreCase = true) ||
                    it.description.contains(query, ignoreCase = true) ||
                    it.key.contains(query, ignoreCase = true)
            }
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(ProjectsBackground)
    ) {
        if (pendingDeleteProject != null) {
            AlertDialog(
                onDismissRequest = { pendingDeleteProject = null },
                confirmButton = {
                    Text(
                        text = "Delete",
                        color = ProjectsDanger,
                        modifier = Modifier.clickable {
                            val projectToDelete = pendingDeleteProject ?: return@clickable
                            pendingDeleteProject = null
                            projectsVm.delete(projectToDelete.projectId)
                        }
                    )
                },
                dismissButton = {
                    Text(
                        text = "Cancel",
                        color = ProjectsTextSecondary,
                        modifier = Modifier.clickable { pendingDeleteProject = null }
                    )
                },
                title = { Text("Delete project") },
                text = { Text("Are you sure you want to delete this project?") }
            )
        }

        when {
            (isLoading && projects.isEmpty()) || (userLoading && user == null) -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(color = ProjectsAccent)
                }
            }

            else -> {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = androidx.compose.foundation.layout.PaddingValues(
                        start = 16.dp,
                        end = 16.dp,
                        top = 12.dp,
                        bottom = 24.dp
                    ),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    item {
                        AppTopHeader(
                            user = user,
                            onNotificationsClick = { nav.navigate("notification") },
                            onProfileClick = { nav.navigate("profile") }
                        )
                    }

                    item {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = "Projects",
                                style = MaterialTheme.typography.headlineSmall.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = ProjectsTextPrimary
                                ),
                                modifier = Modifier.weight(1f)
                            )
                            IconButton(onClick = { nav.navigate("projectCreate") }) {
                                Icon(
                                    painter = painterResource(R.drawable.ic_add),
                                    contentDescription = "Create project",
                                    tint = ProjectsAccent,
                                    modifier = Modifier.size(24.dp)
                                )
                            }
                        }
                    }

                    item {
                        TextField(
                            value = query,
                            onValueChange = { query = it },
                            modifier = Modifier
                                .fillMaxWidth()
                                .heightIn(min = 40.dp),
                            singleLine = true,
                            textStyle = MaterialTheme.typography.bodyLarge,
                            placeholder = {
                                Text(
                                    "Search projects",
                                    style = MaterialTheme.typography.bodyLarge,
                                    color = ProjectsTextSecondary
                                )
                            },
                            trailingIcon = {
                                Icon(
                                    painter = painterResource(R.drawable.ic_search),
                                    contentDescription = null,
                                    tint = ProjectsTextSecondary,
                                    modifier = Modifier.size(20.dp)
                                )
                            },
                            shape = RoundedCornerShape(24.dp),
                            colors = TextFieldDefaults.colors(
                                focusedContainerColor = ProjectsSurface,
                                unfocusedContainerColor = ProjectsSurface,
                                focusedTextColor = ProjectsTextPrimary,
                                unfocusedTextColor = ProjectsTextPrimary,
                                cursorColor = ProjectsAccent,
                                focusedIndicatorColor = Color.Transparent,
                                unfocusedIndicatorColor = Color.Transparent,
                                disabledIndicatorColor = Color.Transparent,
                                errorIndicatorColor = Color.Transparent
                            )
                        )
                    }

                    if (!error.isNullOrBlank()) {
                        item {
                            Text(
                                text = error.orEmpty(),
                                color = MaterialTheme.colorScheme.error,
                                style = MaterialTheme.typography.bodyMedium
                            )
                        }
                    }

                    if (projects.isEmpty()) {
                        item {
                            EmptyProjects(
                                onAddClick = { nav.navigate("projectCreate") }
                            )
                        }
                    } else if (filteredProjects.isEmpty()) {
                        item {
                            Text(
                                text = "No projects match your search.",
                                style = MaterialTheme.typography.bodyMedium,
                                color = ProjectsTextSecondary
                            )
                        }
                    } else {
                        items(
                            items = filteredProjects,
                            key = { it.projectId }
                        ) { project ->
                            ProjectListCard(
                                project = project,
                                onClick = { nav.navigate("projectTasks/${project.projectId}") },
                                onEdit = { nav.navigate("projectSettings/${project.projectId}") },
                                onDelete = { pendingDeleteProject = project },
                                onCopyCode = {
                                    clipboard.setText(AnnotatedString("#${project.key}"))
                                    Toast.makeText(context, "Codigo copiado", Toast.LENGTH_SHORT).show()
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun ProjectListCard(
    project: ProjectDto,
    onClick: () -> Unit,
    onEdit: () -> Unit,
    onDelete: () -> Unit,
    onCopyCode: () -> Unit
) {
    Card(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color.White)
                .padding(14.dp),
            verticalAlignment = Alignment.Top
        ) {
            val imageModel = if (project.imageUrl.isNullOrBlank()) {
                R.drawable.taskmaster_logoblanco
            } else {
                project.imageUrl
            }

            AsyncImage(
                model = imageModel,
                contentDescription = null,
                modifier = Modifier
                    .size(50.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(ProjectsSurface)
            )

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = project.name,
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.SemiBold,
                        color = ProjectsTextPrimary
                    )
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = project.description.ifBlank { project.key },
                    style = MaterialTheme.typography.bodySmall,
                    color = ProjectsTextSecondary,
                    maxLines = 2
                )
                Spacer(modifier = Modifier.height(8.dp))
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    ProjectBadge(
                        text = formatProjectDate(project.endDate),
                        background = ProjectsSurface,
                        textColor = ProjectsTextSecondary
                    )
                    ProjectBadge(
                        text = "#${project.key}",
                        background = ProjectsAccent.copy(alpha = 0.10f),
                        textColor = ProjectsAccent,
                        onClick = onCopyCode
                    )
                }
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column(
                verticalArrangement = Arrangement.spacedBy(8.dp),
                horizontalAlignment = Alignment.End
            ) {
                ProjectActionButton(
                    icon = Icons.Filled.Edit,
                    contentDescription = "Edit project",
                    tint = ProjectsAccent,
                    background = ProjectsAccent.copy(alpha = 0.10f),
                    onClick = onEdit
                )
                ProjectActionButton(
                    icon = Icons.Filled.Delete,
                    contentDescription = "Delete project",
                    tint = ProjectsDanger,
                    background = ProjectsDanger.copy(alpha = 0.10f),
                    onClick = onDelete
                )
            }
        }
    }
}

@Composable
private fun ProjectBadge(
    text: String,
    background: Color,
    textColor: Color,
    icon: (@Composable () -> Unit)? = null,
    onClick: (() -> Unit)? = null
) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(999.dp))
            .background(background)
            .then(
                if (onClick != null) Modifier.clickable(onClick = onClick) else Modifier
            )
            .padding(horizontal = 10.dp, vertical = 5.dp)
    ) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(6.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            icon?.invoke()
            Text(
                text = text,
                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.SemiBold),
                color = textColor
            )
        }
    }
}

@Composable
private fun ProjectActionButton(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    contentDescription: String,
    tint: Color,
    background: Color,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .size(34.dp)
            .clip(RoundedCornerShape(12.dp))
            .background(background)
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = icon,
            contentDescription = contentDescription,
            tint = tint,
            modifier = Modifier.size(18.dp)
        )
    }
}

private fun formatProjectDate(raw: String): String {
    val parsed = parseProjectDate(raw) ?: return raw
    return parsed.toLocalDate().toString()
}

private fun parseProjectDate(raw: String?): ZonedDateTime? {
    if (raw.isNullOrBlank()) return null
    return try {
        OffsetDateTime.parse(raw).toZonedDateTime()
    } catch (_: DateTimeParseException) {
        try {
            ZonedDateTime.parse(raw)
        } catch (_: DateTimeParseException) {
            try {
                LocalDateTime.parse(raw, DateTimeFormatter.ISO_LOCAL_DATE_TIME)
                    .atZone(ZoneId.systemDefault())
            } catch (_: DateTimeParseException) {
                null
            }
        }
    }
}

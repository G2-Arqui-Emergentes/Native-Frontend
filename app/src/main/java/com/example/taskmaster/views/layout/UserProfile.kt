package com.example.taskmaster.views.layout

import android.content.Context
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import coil.compose.AsyncImage
import com.example.taskmaster.R
import com.example.taskmaster.views.layout.common.AppTopHeader
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

private val TeamBackground = Color(0xFFF9FAFB)
private val TeamSurface = Color(0xFFF4F5F7)
private val TeamBorder = Color(0xFFE5E7EB)
private val TeamTextPrimary = Color(0xFF111827)
private val TeamTextSecondary = Color(0xFF6B7280)
private val TeamBrand = Color(0xFFEC1926)

@Composable
fun UserProfile(
    context: Context,
    nav: NavHostController,
    userVm: UsersViewModel = remember { UsersViewModel() },
    projectsVm: ProjectsViewModel = remember { ProjectsViewModel() }
) {
    val user by userVm.user.collectAsState()
    val userLoading by userVm.isLoading.collectAsState()
    val projects by projectsVm.projects.collectAsState()
    val projectsLoading by projectsVm.isLoading.collectAsState()
    val error by projectsVm.error.collectAsState()

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

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(TeamBackground)
    ) {
        when {
            (userLoading && user == null) || (projectsLoading && projects.isEmpty()) -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(color = TeamBrand)
                }
            }

            else -> {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(16.dp),
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
                        Column {
                            Text(
                                text = "Team",
                                style = MaterialTheme.typography.headlineSmall.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = TeamTextPrimary
                                )
                            )
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = "Select a project to view its member list.",
                                style = MaterialTheme.typography.bodyMedium,
                                color = TeamTextSecondary
                            )
                        }
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
                            EmptyTeamProjects()
                        }
                    } else {
                        items(
                            items = projects,
                            key = { it.projectId }
                        ) { project ->
                            TeamProjectCard(
                                project = project,
                                onClick = { nav.navigate("memberList/${project.projectId}") }
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun EmptyTeamProjects() {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 28.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "No projects available",
                style = MaterialTheme.typography.titleLarge.copy(
                    fontWeight = FontWeight.Bold,
                    color = TeamTextPrimary
                )
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Projects will appear here so you can open each member list.",
                style = MaterialTheme.typography.bodyMedium,
                color = TeamTextSecondary
            )
        }
    }
}

@Composable
fun TeamProjectCard(
    project: ProjectDto,
    onClick: () -> Unit
) {
    val imageModel = if (project.imageUrl.isNullOrBlank()) {
        R.drawable.taskmaster_logoblanco
    } else {
        project.imageUrl
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp),
        onClick = onClick
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            AsyncImage(
                model = imageModel,
                contentDescription = project.name,
                modifier = Modifier
                    .size(56.dp)
                    .clip(RoundedCornerShape(18.dp))
                    .background(TeamSurface),
                contentScale = ContentScale.Crop
            )

            Spacer(modifier = Modifier.width(14.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = project.name,
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.SemiBold,
                        color = TeamTextPrimary
                    )
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = project.description.ifBlank { "No description available" },
                    style = MaterialTheme.typography.bodySmall,
                    color = TeamTextSecondary,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(modifier = Modifier.height(10.dp))
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    TeamBadge(text = formatProjectDate(project.endDate))
                    TeamCodeBadge(text = "#${project.key}")
                }
            }
        }
    }
}

@Composable
private fun TeamBadge(text: String) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(999.dp))
            .background(TeamSurface)
            .border(1.dp, TeamBorder, RoundedCornerShape(999.dp))
            .padding(horizontal = 10.dp, vertical = 5.dp)
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Medium),
            color = TeamTextSecondary
        )
    }
}

@Composable
private fun TeamCodeBadge(text: String) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(999.dp))
            .background(TeamBrand.copy(alpha = 0.10f))
            .padding(horizontal = 10.dp, vertical = 5.dp)
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.SemiBold),
            color = TeamBrand
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

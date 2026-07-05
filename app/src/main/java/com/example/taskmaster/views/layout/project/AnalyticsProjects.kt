package com.example.taskmaster.views.layout.project

import android.content.Context
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.example.taskmaster.views.layout.TeamProjectCard
import com.example.taskmaster.views.layout.common.AppTopHeader
import com.example.taskmaster.viewmodel.model.ProjectsViewModel
import com.example.taskmaster.viewmodel.sharedPreferences.Prefs
import com.example.taskmaster.viewmodel.ui.users.UsersViewModel

private val AnalyticsBackground = Color(0xFFF9FAFB)
private val AnalyticsTextPrimary = Color(0xFF111827)
private val AnalyticsTextSecondary = Color(0xFF6B7280)
private val AnalyticsBrand = Color(0xFFEC1926)

@Composable
fun AnalyticsProjects(
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
            .background(AnalyticsBackground)
    ) {
        when {
            (userLoading && user == null) || (projectsLoading && projects.isEmpty()) -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(color = AnalyticsBrand)
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
                                text = "Analytics",
                                style = MaterialTheme.typography.headlineSmall.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = AnalyticsTextPrimary
                                )
                            )
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = "Select a project to open its analytics view.",
                                style = MaterialTheme.typography.bodyMedium,
                                color = AnalyticsTextSecondary
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
                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                colors = CardDefaults.cardColors(containerColor = Color.White)
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
                                            fontWeight = FontWeight.Bold
                                        ),
                                        color = AnalyticsTextPrimary
                                    )
                                    Spacer(modifier = Modifier.height(8.dp))
                                    Text(
                                        text = "Projects will appear here so you can open their analytics.",
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = AnalyticsTextSecondary
                                    )
                                }
                            }
                        }
                    } else {
                        items(
                            items = projects,
                            key = { it.projectId }
                        ) { project ->
                            TeamProjectCard(
                                project = project,
                                onClick = { nav.navigate("projectStats/${project.projectId}") }
                            )
                        }
                    }
                }
            }
        }
    }
}

package com.example.taskmaster.views.layout.project

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.example.taskmaster.viewmodel.model.ProjectsViewModel
import com.example.taskmaster.viewmodel.ui.users.UsersViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MemberList(
    nav: NavHostController,
    projectId: Long,
    usersVm: UsersViewModel = remember { UsersViewModel() },
    projectsVm: ProjectsViewModel = remember { ProjectsViewModel() }
) {
    val isLoading by usersVm.isLoading.collectAsState()
    val error by usersVm.error.collectAsState()
    val members by usersVm.members.collectAsState()

    LaunchedEffect(projectId) {
        usersVm.loadMembersForProject(projectId)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Lista de miembros") },
                navigationIcon = {
                    IconButton(onClick = { nav.popBackStack() }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Volver"
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.secondaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.onSecondaryContainer,
                    navigationIconContentColor = MaterialTheme.colorScheme.onSecondaryContainer
                )
            )
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 16.dp, vertical = 8.dp)
        ) {
            Text(
                text = "${members.size} miembros",
                style = MaterialTheme.typography.labelLarge,
                modifier = Modifier
                    .align(Alignment.CenterHorizontally)
                    .padding(vertical = 8.dp)
            )

            when {
                isLoading -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator()
                    }
                }

                !error.isNullOrBlank() -> {
                    Text(
                        text = error ?: "",
                        color = MaterialTheme.colorScheme.error,
                        modifier = Modifier.padding(12.dp)
                    )
                }

                members.isEmpty() -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("Aún no hay miembros en este proyecto")
                    }
                }

                else -> {

                    LazyColumn(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f),        // <- en vez de fillMaxSize()
                        verticalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        items(members) { m ->
                            MemberCard(
                                member = m,
                                onClick = {
                                    nav.navigate("userStats/${m.id}")
                                },
                                onRemoveClick = {
                                    projectsVm.removeMember(projectId, m.id)
                                    usersVm.loadMembersForProject(projectId)
                                }
                            )
                        }
                    }

                    Spacer(Modifier.height(12.dp))

                    Row(
                        modifier = Modifier.align(Alignment.CenterHorizontally)
                            .fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "¿Te quedaste sin espacio? ",
                            style = MaterialTheme.typography.bodySmall.copy(
                                color = MaterialTheme.colorScheme.onBackground
                            )

                        )
                        TextButton(
                            onClick = { nav.navigate("membership") }
                        ) {
                            Text(
                                text = "Mira nuestras membresías",
                                style = MaterialTheme.typography.bodySmall.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onBackground
                                )

                            )
                        }
                    }
                }
            }
        }
    }
}

package com.example.taskmaster.views.layout.project

import android.content.Context
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.example.taskmaster.R
import com.example.taskmaster.viewmodel.model.ProjectsViewModel
import com.example.taskmaster.viewmodel.sharedPreferences.Prefs
import com.example.taskmaster.viewmodel.ui.users.UsersViewModel

@Composable
fun Proyect(
    context: Context,
    nav: NavHostController,
    vm: ProjectsViewModel = remember { ProjectsViewModel() },
    userVm: UsersViewModel = remember { UsersViewModel() }
) {
    val isLoading by vm.isLoading.collectAsState()
    val error by vm.error.collectAsState()
    val projects by vm.projects.collectAsState()

    val userState by userVm.user.collectAsState()
    var currentUserId by remember { mutableStateOf<Long?>(null) }

    var query by remember { mutableStateOf("") }
    var queryText by remember { mutableStateOf("") }

    LaunchedEffect(Unit) {
        Prefs.loadEmail(context)?.let { email ->
            userVm.loadByEmail(email)
        }
        vm.loadAll()
    }

    LaunchedEffect(userState) {
        currentUserId = userState?.id
    }

    Column(modifier = Modifier.padding(horizontal = 16.dp)) {

        // Header
        Row(
            modifier = Modifier.padding(vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Proyectos",
                style = MaterialTheme.typography.displayMedium,
                color = MaterialTheme.colorScheme.onBackground,
                modifier = Modifier.weight(1f)
            )
            IconButton(onClick = { nav.navigate("projectCreate") }) {
                Image(
                    painter = painterResource(R.drawable.ic_add),
                    contentDescription = null,
                    colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.onBackground),
                    modifier = Modifier.size(24.dp)
                )
            }
        }

        TextField(
            value = queryText,
            onValueChange = {
                queryText = it
                query = it
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 6.dp, bottom = 12.dp)
                .heightIn(min = 40.dp),
            singleLine = true,
            textStyle = MaterialTheme.typography.bodyLarge,
            placeholder = {
                Text(
                    "Buscar proyectos",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                )
            },
            trailingIcon = {
                Icon(
                    painter = painterResource(R.drawable.ic_search),
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.size(20.dp)
                )
            },
            shape = RoundedCornerShape(24.dp),
            colors = TextFieldDefaults.colors(
                focusedContainerColor = MaterialTheme.colorScheme.secondaryContainer,
                unfocusedContainerColor = MaterialTheme.colorScheme.secondaryContainer,
                focusedTextColor = MaterialTheme.colorScheme.onSurface,
                unfocusedTextColor = MaterialTheme.colorScheme.onSurface,
                cursorColor = MaterialTheme.colorScheme.primary,
                focusedIndicatorColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent,
                disabledIndicatorColor = Color.Transparent,
                errorIndicatorColor = Color.Transparent
            )
        )

        when {
            isLoading || currentUserId == null -> {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            }

            error != null -> {
                Text(
                    text = error ?: "",
                    color = MaterialTheme.colorScheme.error,
                    modifier = Modifier.padding(12.dp)
                )
            }

            else -> {
                val uid = currentUserId!!

                val ownProjects = projects.filter { it.leaderId == uid }

                if (ownProjects.isEmpty()) {
                    EmptyProjects(onAddClick = { nav.navigate("projectCreate") })
                } else {
                    val filtered = ownProjects.filter {
                        it.name.contains(query, ignoreCase = true) ||
                                it.description.contains(query, ignoreCase = true) ||
                                it.key.contains(query, ignoreCase = true)
                    }

                    ProjectsList(
                        projects = filtered,
                        onProjectClick = { p ->
                            val id = p.projectId
                            nav.navigate("projectSettings/$id")
                        }
                    )
                }
            }
        }
    }
}

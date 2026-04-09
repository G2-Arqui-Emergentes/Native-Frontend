package com.example.taskmaster.views.nav

import android.content.Context
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavType
import androidx.navigation.compose.*
import androidx.navigation.navArgument
import com.example.taskmaster.R
import com.example.taskmaster.views.shared.Login
import com.example.taskmaster.views.shared.Register
import com.example.taskmaster.views.layout.task.Task
import com.example.taskmaster.views.layout.Calendar

import com.example.taskmaster.views.layout.notification.Notifiations
import com.example.taskmaster.views.layout.UserProfile
import com.example.taskmaster.views.layout.project.MemberList
import com.example.taskmaster.views.layout.project.Membership
import com.example.taskmaster.views.layout.project.Proyect
import com.example.taskmaster.views.layout.project.ProjectCreate
import com.example.taskmaster.views.layout.project.ProjectSettings
import com.example.taskmaster.views.layout.project.ProjectStats
import com.example.taskmaster.views.layout.project.ProjectTasks
import com.example.taskmaster.views.layout.project.UserStats
import com.example.taskmaster.views.layout.task.CreateTask
import com.example.taskmaster.views.layout.task.EditTask

data class BottomTab(val route: String, val icon: Int)

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun Navi(context: Context) {
    val nav = rememberNavController()

    // Tabs principales
    val tabsIcons = listOf(
        BottomTab("projects",     R.drawable.ic_proyect),
        BottomTab("tasks",        R.drawable.ic_task),
        BottomTab("calendar",     R.drawable.ic_calendar),
        BottomTab("notification", R.drawable.ic_bell),
        BottomTab("profile",      R.drawable.ic_user)
    )
    val baseRoutes = tabsIcons.map { it.route }.toSet()

    val backStackEntry by nav.currentBackStackEntryAsState()
    val currentRoute = backStackEntry?.destination?.route.orEmpty()

    val showBottomBar =
        currentRoute in baseRoutes ||
                currentRoute == "projectCreate" ||
                currentRoute == "membership" ||
                currentRoute.startsWith("projectSettings") ||
                currentRoute.startsWith("projectTasks") ||
                currentRoute.startsWith("projectStats")


    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        bottomBar = {
            if (showBottomBar) {
                NavigationBar(containerColor = MaterialTheme.colorScheme.secondaryContainer) {
                    tabsIcons.forEach { t ->
                        val selected = when (t.route) {
                            "projects" -> currentRoute == "projects" ||
                                    currentRoute == "projectCreate" ||
                                    currentRoute.startsWith("projectSettings/")
                            else -> currentRoute == t.route
                        }
                        NavigationBarItem(
                            selected = selected,
                            onClick = {
                                nav.navigate(t.route) {
                                    nav.graph.startDestinationRoute?.let { start ->
                                        popUpTo(start) { saveState = true }
                                    }
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            },
                            icon = {
                                Icon(
                                    painterResource(t.icon),
                                    contentDescription = null,
                                    modifier = Modifier.size(32.dp)
                                )
                            },
                            colors = NavigationBarItemDefaults.colors(
                                selectedIconColor   = MaterialTheme.colorScheme.primary,
                                unselectedIconColor = MaterialTheme.colorScheme.onSurface,
                                indicatorColor      = MaterialTheme.colorScheme.secondaryContainer
                            )
                        )
                    }
                }
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = nav,
            startDestination = "login",
            modifier = Modifier.padding(innerPadding)
        ) {
            composable("login")    { Login(context, nav) }
            composable("register") { Register(context, nav) }

            composable("projects") { Proyect(context,nav) }

            composable("projectCreate") { ProjectCreate(nav) }

            composable(
                route = "projectSettings/{projectId}",
                arguments = listOf(navArgument("projectId") { type = NavType.LongType })
            ) { entry ->
                val id = entry.arguments?.getLong("projectId") ?: return@composable
                ProjectSettings(nav, projectId = id)
            }
            composable(
                route = "projectTasks/{projectId}",
                arguments = listOf(navArgument("projectId") { type = NavType.LongType })
            ) { entry ->
                val id = entry.arguments?.getLong("projectId") ?: return@composable
                ProjectTasks(nav, projectId = id)
            }

            composable(
                route = "projectStats/{projectId}",
                arguments = listOf(navArgument("projectId") { type = NavType.LongType })
            ) { entry ->
                val id = entry.arguments?.getLong("projectId") ?: return@composable
                ProjectStats(nav, projectId = id)
            }
            composable("taskCreate/{projectId}",
                arguments = listOf(navArgument("projectId") { type = NavType.LongType })
            ) { backStack ->
                val pid = backStack.arguments?.getLong("projectId") ?: return@composable
                CreateTask(nav = nav, projectId = pid)
            }
            composable(
                route = "memberList/{projectId}",
                arguments = listOf(navArgument("projectId") { type = NavType.LongType })
            ) { entry ->
                val pid = entry.arguments?.getLong("projectId") ?: return@composable
                MemberList(nav = nav, projectId = pid)
            }
            composable(
                route = "taskEdit/{projectId}/{taskId}",
                arguments = listOf(
                    navArgument("projectId") { type = NavType.LongType },
                    navArgument("taskId") { type = NavType.LongType }
                )
            ) { entry ->
                val pid = entry.arguments?.getLong("projectId") ?: return@composable
                val tid = entry.arguments?.getLong("taskId") ?: return@composable
                EditTask(nav = nav, projectId = pid, taskId = tid)
            }
            composable(
                route = "userStats/{userId}"
            ) { backStackEntry ->
                val userId = backStackEntry.arguments?.getString("userId")?.toLong() ?: 0L
                UserStats(nav = nav, userId = userId)
            }


            composable("tasks")        { Task() }
            composable("calendar")     { Calendar() }
            composable("notification") { Notifiations() }
            composable("profile")      { UserProfile(context, nav) }
            composable("membership")   { Membership(nav) }

        }
    }
}

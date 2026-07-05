package com.example.taskmaster.views.nav

import android.content.Context
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
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
import com.example.taskmaster.views.layout.ProfileScreen
import com.example.taskmaster.views.layout.UserProfile
import com.example.taskmaster.views.layout.chatbot.ChatbotBottomSheet
import com.example.taskmaster.views.layout.project.AnalyticsProjects
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
import com.example.taskmaster.viewmodel.model.ChatbotViewModel

data class BottomTab(val route: String, val icon: Int, val label: String)

private val NavBackground = Color(0xFFF4F5F7)
private val NavSelected = Color(0xFF0F9E6E)
private val NavUnselected = Color(0xFF6B7280)
private val NavIndicator = Color(0xFFE7F6F0)
private val NavBorder = Color(0xFFE5E7EB)

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun Navi(context: Context) {
    val nav = rememberNavController()
    val chatbotVm = remember { ChatbotViewModel() }
    var showChatbot by remember { mutableStateOf(false) }

    // Tabs principales
    val tabsIcons = listOf(
        BottomTab("projects",     R.drawable.ic_dashboard, "Dashboard"),
        BottomTab("tasks",        R.drawable.ic_projects, "Projects"),
        BottomTab("calendar",     R.drawable.ic_calendar, "Calendar"),
        BottomTab("analytics",    R.drawable.ic_analytics, "Analytics"),
        BottomTab("team",         R.drawable.ic_team, "Team")
    )
    val baseRoutes = tabsIcons.map { it.route }.toSet()

    val backStackEntry by nav.currentBackStackEntryAsState()
    val currentRoute = backStackEntry?.destination?.route.orEmpty()
    val currentProjectId = backStackEntry?.arguments?.getLong("projectId")

    val showBottomBar =
        currentRoute in baseRoutes ||
                currentRoute == "notification" ||
                currentRoute == "profile" ||
                currentRoute == "projectCreate" ||
                currentRoute == "membership" ||
                currentRoute.startsWith("userStats") ||
                currentRoute.startsWith("projectSettings") ||
                currentRoute.startsWith("projectTasks") ||
                currentRoute.startsWith("projectStats")
    val showChatbotFab = currentRoute.isNotBlank() && currentRoute != "login" && currentRoute != "register"


    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        floatingActionButton = {
            if (showChatbotFab) {
                FloatingActionButton(
                    onClick = { showChatbot = true },
                    containerColor = Color(0xFFEC1926),
                    contentColor = Color.White,
                    shape = RoundedCornerShape(18.dp)
                ) {
                    Text("AI")
                }
            }
        },
        bottomBar = {
            if (showBottomBar) {
                Surface(
                    color = NavBackground,
                    tonalElevation = 0.dp,
                    shadowElevation = 10.dp,
                    shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp),
                    modifier = Modifier
                ) {
                    NavigationBar(
                        containerColor = NavBackground,
                        tonalElevation = 0.dp
                    ) {
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
                                    contentDescription = t.label,
                                    modifier = Modifier.size(32.dp)
                                )
                            },
                            label = {
                                Text(text = t.label)
                            },
                            colors = NavigationBarItemDefaults.colors(
                                selectedIconColor = NavSelected,
                                selectedTextColor = NavSelected,
                                unselectedIconColor = NavUnselected,
                                unselectedTextColor = NavUnselected,
                                indicatorColor = NavIndicator
                            )
                        )
                    }
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

            composable("projectCreate") { ProjectCreate(context, nav) }

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
                UserStats(context = context, nav = nav, userId = userId)
            }


            composable("tasks")        { Task(context, nav) }
            composable("calendar")     { Calendar(context, nav) }
            composable("analytics")    { AnalyticsProjects(context, nav) }
            composable("notification") { Notifiations(context, nav) }
            composable("profile")      { ProfileScreen(context, nav) }
            composable("team")         { UserProfile(context, nav) }
            composable("membership")   { Membership(nav) }

        }

        ChatbotBottomSheet(
            visible = showChatbot,
            projectId = currentProjectId,
            viewModel = chatbotVm,
            onDismiss = { showChatbot = false }
        )
    }
}

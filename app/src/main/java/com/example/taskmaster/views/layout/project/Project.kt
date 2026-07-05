package com.example.taskmaster.views.layout.project

import android.content.Context
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.LinearProgressIndicator
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
import com.example.taskmaster.viewmodel.data.ai.LeaderDashboardDto
import com.example.taskmaster.viewmodel.data.notifications.NotificationDto
import com.example.taskmaster.viewmodel.data.projects.ProjectDto
import com.example.taskmaster.viewmodel.data.tasks.TaskDto
import com.example.taskmaster.viewmodel.data.tasks.TaskStatus
import com.example.taskmaster.viewmodel.data.users.UserDto
import com.example.taskmaster.viewmodel.model.AiDashboardViewModel
import com.example.taskmaster.viewmodel.model.NotificationsViewModel
import com.example.taskmaster.viewmodel.model.ProjectsViewModel
import com.example.taskmaster.viewmodel.model.TasksViewModel
import com.example.taskmaster.viewmodel.sharedPreferences.Prefs
import com.example.taskmaster.viewmodel.ui.users.UsersViewModel
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.OffsetDateTime
import java.time.ZoneId
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeParseException
import kotlin.math.roundToInt

private val DashboardBackground = Color(0xFFF9FAFB)
private val DashboardSurfaceAlt = Color(0xFFF4F5F7)
private val DashboardBorder = Color(0xFFE5E7EB)
private val DashboardTextPrimary = Color(0xFF111827)
private val DashboardTextSecondary = Color(0xFF6B7280)
private val DashboardPositive = Color(0xFF0F9E6E)
private val DashboardNegative = Color(0xFFEE4445)
private val DashboardAccent = Color(0xFF111827)
private val DashboardMuted = Color(0xFF9CA3AF)
private val DashboardWarning = Color(0xFFF59E0B)

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun Proyect(
    context: Context,
    nav: NavHostController,
    vm: ProjectsViewModel = remember { ProjectsViewModel() },
    userVm: UsersViewModel = remember { UsersViewModel() },
    tasksVm: TasksViewModel = remember { TasksViewModel() },
    notificationsVm: NotificationsViewModel = remember { NotificationsViewModel() },
    aiVm: AiDashboardViewModel = remember { AiDashboardViewModel() }
) {
    val projects by vm.projects.collectAsState()
    val projectsLoading by vm.isLoading.collectAsState()
    val projectsError by vm.error.collectAsState()

    val user by userVm.user.collectAsState()
    val userLoading by userVm.isLoading.collectAsState()
    val userError by userVm.error.collectAsState()

    val tasks by tasksVm.tasks.collectAsState()
    val tasksLoading by tasksVm.isLoading.collectAsState()
    val tasksError by tasksVm.error.collectAsState()

    val notifications by notificationsVm.notifications.collectAsState()
    val notificationsLoading by notificationsVm.isLoading.collectAsState()
    val notificationsError by notificationsVm.error.collectAsState()

    val aiDashboard by aiVm.dashboard.collectAsState()
    val aiLoading by aiVm.isLoading.collectAsState()
    val aiError by aiVm.error.collectAsState()

    LaunchedEffect(Unit) {
        Prefs.loadEmail(context)?.let(userVm::loadByEmail)
        notificationsVm.loadMyNotifications()
        aiVm.loadLeaderDashboard()
    }

    LaunchedEffect(user?.id) {
        val leaderId = user?.id ?: return@LaunchedEffect
        vm.loadByLeader(leaderId)
    }

    LaunchedEffect(projects) {
        if (projects.isNotEmpty()) {
            tasksVm.loadByProjects(projects.map { it.projectId })
        }
    }

    val metrics = remember(projects, tasks, notifications, aiDashboard) {
        DashboardMetrics.fromData(
            projects = projects,
            tasks = tasks,
            notifications = notifications,
            aiDashboard = aiDashboard
        )
    }

    val isLoading = (userLoading && user == null) ||
        (projectsLoading && projects.isEmpty()) ||
        (tasksLoading && tasks.isEmpty() && projects.isNotEmpty()) ||
        (notificationsLoading && notifications.isEmpty()) ||
        (aiLoading && aiDashboard == null)

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(DashboardBackground)
    ) {
        if (isLoading) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(color = DashboardAccent)
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(18.dp)
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
                            text = "Welcome back, ${displayName(user)}!",
                            style = MaterialTheme.typography.headlineSmall.copy(
                                fontWeight = FontWeight.Bold,
                                color = DashboardTextPrimary
                            )
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = "Here is a summary of your current workspace.",
                            style = MaterialTheme.typography.bodyMedium,
                            color = DashboardTextSecondary
                        )
                    }
                }
                item { DashboardSummaryGrid(metrics) }
                item { SmartAnalyticsCard(metrics) }
                item { RecentActivitiesCard(metrics.activities) }

                if (!projectsError.isNullOrBlank()) {
                    item { DashboardMessage(projectsError.orEmpty()) }
                }
                if (!userError.isNullOrBlank()) {
                    item { DashboardMessage(userError.orEmpty()) }
                }
                if (!tasksError.isNullOrBlank()) {
                    item { DashboardMessage(tasksError.orEmpty()) }
                }
                if (!notificationsError.isNullOrBlank()) {
                    item { DashboardMessage(notificationsError.orEmpty()) }
                }
                if (!aiError.isNullOrBlank()) {
                    item { DashboardMessage(aiError.orEmpty()) }
                }
            }
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun DashboardSummaryGrid(metrics: DashboardMetrics) {
    BoxWithConstraints(modifier = Modifier.fillMaxWidth()) {
        val cardModifier = Modifier.width((maxWidth - 12.dp) / 2)
        FlowRow(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            maxItemsInEachRow = 2
        ) {
            MetricCard(
                title = "ACTIVE PROJECTS",
                value = metrics.activeProjects.toString(),
                subtitle = metrics.activeProjectsSubtitle,
                accent = DashboardPositive,
                modifier = cardModifier
            )
            MetricCard(
                title = "PENDING TASKS",
                value = metrics.pendingTasks.toString(),
                subtitle = metrics.pendingTasksSubtitle,
                progress = metrics.pendingTasksProgress,
                accent = DashboardWarning,
                modifier = cardModifier
            )
            MetricCard(
                title = "OVERDUE TASKS",
                value = metrics.overdueTasks.toString(),
                subtitle = metrics.overdueTasksSubtitle,
                accent = DashboardNegative,
                modifier = cardModifier
            )
            MetricCard(
                title = "AVERAGE TIME",
                value = metrics.averageTimeLabel,
                subtitle = "per task",
                accent = if (metrics.averageTimeLabel == "N/A") DashboardMuted else DashboardPositive,
                modifier = cardModifier
            )
        }
    }
}

@Composable
private fun MetricCard(
    title: String,
    value: String,
    subtitle: String,
    modifier: Modifier = Modifier,
    accent: Color,
    progress: Float? = null
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(22.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .border(1.dp, DashboardBorder, RoundedCornerShape(22.dp))
                .background(Color.White)
                .padding(16.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(10.dp)
                        .clip(CircleShape)
                        .background(accent)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = title,
                    style = MaterialTheme.typography.labelMedium.copy(
                        color = DashboardTextSecondary,
                        fontWeight = FontWeight.SemiBold
                    )
                )
            }
            Spacer(modifier = Modifier.height(14.dp))
            Text(
                text = value,
                style = MaterialTheme.typography.headlineMedium.copy(
                    color = if (title == "OVERDUE TASKS") DashboardNegative else DashboardTextPrimary,
                    fontWeight = FontWeight.Bold
                ),
                maxLines = 1
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodySmall,
                color = DashboardTextSecondary,
                minLines = 2
            )
            if (progress != null) {
                Spacer(modifier = Modifier.height(12.dp))
                LinearProgressIndicator(
                    progress = { progress },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(8.dp)
                        .clip(RoundedCornerShape(99.dp)),
                    color = accent,
                    trackColor = DashboardSurfaceAlt
                )
            }
        }
    }
}

@Composable
private fun SmartAnalyticsCard(metrics: DashboardMetrics) {
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                text = "Smart Analytics",
                style = MaterialTheme.typography.titleLarge.copy(
                    color = DashboardTextPrimary,
                    fontWeight = FontWeight.SemiBold
                )
            )
            Spacer(modifier = Modifier.width(10.dp))
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(999.dp))
                    .background(DashboardPositive.copy(alpha = 0.12f))
                    .border(1.dp, DashboardPositive.copy(alpha = 0.18f), RoundedCornerShape(999.dp))
                    .padding(horizontal = 10.dp, vertical = 5.dp)
            ) {
                Text(
                    text = "AI POWERED",
                    style = MaterialTheme.typography.labelSmall.copy(
                        color = DashboardPositive,
                        fontWeight = FontWeight.Bold
                    )
                )
            }
        }

        Card(
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, DashboardBorder, RoundedCornerShape(24.dp))
                    .padding(18.dp),
                verticalArrangement = Arrangement.spacedBy(18.dp)
            ) {
                Text(
                    text = "AI Risk Assessment",
                    style = MaterialTheme.typography.titleMedium.copy(
                        color = DashboardTextPrimary,
                        fontWeight = FontWeight.SemiBold
                    )
                )

                Text(
                    text = metrics.topRiskDescription,
                    style = MaterialTheme.typography.bodySmall,
                    color = DashboardTextSecondary
                )

                AnalyticsMetric(
                    label = "Probability of Delay",
                    value = metrics.delayProbability
                )
                AnalyticsMetric(
                    label = "Overall Efficiency",
                    value = metrics.overallEfficiency
                )

                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(
                        text = "AI Recommendation",
                        style = MaterialTheme.typography.labelLarge.copy(
                            color = DashboardTextPrimary,
                            fontWeight = FontWeight.SemiBold
                        )
                    )
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(16.dp))
                            .background(metrics.recommendationBackground)
                            .border(
                                1.dp,
                                metrics.recommendationAccent.copy(alpha = 0.22f),
                                RoundedCornerShape(16.dp)
                            )
                            .padding(14.dp)
                    ) {
                        Text(
                            text = metrics.aiRecommendation,
                            style = MaterialTheme.typography.bodyMedium,
                            color = DashboardTextSecondary
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun AnalyticsMetric(label: String, value: Int) {
    val accent = if (label == "Probability of Delay") DashboardNegative else DashboardPositive
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                text = label,
                style = MaterialTheme.typography.bodyMedium.copy(
                    color = DashboardTextPrimary,
                    fontWeight = FontWeight.Medium
                ),
                modifier = Modifier.weight(1f)
            )
            Text(
                text = "$value%",
                style = MaterialTheme.typography.bodyMedium.copy(
                    color = accent,
                    fontWeight = FontWeight.SemiBold
                )
            )
        }
        LinearProgressIndicator(
            progress = { value / 100f },
            modifier = Modifier
                .fillMaxWidth()
                .height(10.dp)
                .clip(RoundedCornerShape(99.dp)),
            color = accent,
            trackColor = DashboardSurfaceAlt
        )
    }
}

@Composable
private fun RecentActivitiesCard(activities: List<ActivityItem>) {
    Card(
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .border(1.dp, DashboardBorder, RoundedCornerShape(24.dp))
                .padding(18.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            Text(
                text = "Recent Activities",
                style = MaterialTheme.typography.titleMedium.copy(
                    color = DashboardTextPrimary,
                    fontWeight = FontWeight.SemiBold
                )
            )

            if (activities.isEmpty()) {
                Text(
                    text = "No recent activities yet.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = DashboardTextSecondary
                )
            } else {
                activities.take(5).forEach { activity ->
                    Row(verticalAlignment = Alignment.Top) {
                        Box(
                            modifier = Modifier
                                .padding(top = 6.dp)
                                .size(10.dp)
                                .clip(RoundedCornerShape(99.dp))
                                .background(DashboardPositive.copy(alpha = 0.72f))
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = activity.title,
                                style = MaterialTheme.typography.bodyMedium.copy(
                                    color = DashboardTextPrimary,
                                    fontWeight = FontWeight.SemiBold
                                ),
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = activity.description,
                                style = MaterialTheme.typography.bodySmall,
                                color = DashboardTextSecondary
                            )
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(
                            text = activity.relativeTime,
                            style = MaterialTheme.typography.bodySmall,
                            color = DashboardMuted
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun DashboardMessage(message: String) {
    Text(
        text = message,
        style = MaterialTheme.typography.bodyMedium,
        color = MaterialTheme.colorScheme.error
    )
}

private data class DashboardMetrics(
    val activeProjects: Int,
    val activeProjectsSubtitle: String,
    val pendingTasks: Int,
    val pendingTasksSubtitle: String,
    val pendingTasksProgress: Float,
    val overdueTasks: Int,
    val overdueTasksSubtitle: String,
    val averageTimeLabel: String,
    val delayProbability: Int,
    val overallEfficiency: Int,
    val aiRecommendation: String,
    val topRiskDescription: String,
    val recommendationAccent: Color,
    val recommendationBackground: Color,
    val activities: List<ActivityItem>
) {
    companion object {
        @RequiresApi(Build.VERSION_CODES.O)
        fun fromData(
            projects: List<ProjectDto>,
            tasks: List<TaskDto>,
            notifications: List<NotificationDto>,
            aiDashboard: LeaderDashboardDto?
        ): DashboardMetrics {
            val today = LocalDate.now()

            val activeProjects = projects.size
            val newProjectsThisMonth = projects.count {
                parseDate(it.startDate)?.toLocalDate()?.let { date ->
                    date.monthValue == today.monthValue && date.year == today.year
                } == true
            }

            val pendingTasks = tasks.count {
                it.status == TaskStatus.TO_DO || it.status == TaskStatus.IN_PROGRESS
            }

            val overdueTasks = tasks.count {
                val end = parseDate(it.endDate)?.toLocalDate()
                end != null && end.isBefore(today) && it.status != TaskStatus.DONE
            }

            val overdueThisMonth = tasks.count {
                val end = parseDate(it.endDate)?.toLocalDate()
                end != null &&
                    end.isBefore(today) &&
                    end.monthValue == today.monthValue &&
                    end.year == today.year &&
                    it.status != TaskStatus.DONE
            }

            val doneTasks = tasks.filter { it.status == TaskStatus.DONE }
            val averageTimeLabel = doneTasks.mapNotNull { task ->
                val start = parseDate(task.startDate)
                val end = parseDate(task.endDate)
                if (start != null && end != null && !end.isBefore(start)) {
                    java.time.Duration.between(start, end).toDays().toDouble()
                } else {
                    null
                }
            }.average().let { avg ->
                if (avg.isNaN()) "N/A" else "${formatOneDecimal(avg)} d"
            }

            val totalTasks = tasks.size.coerceAtLeast(1)
            val pendingProgress = (pendingTasks.toFloat() / totalTasks).coerceIn(0f, 1f)

            val delayProbability = aiDashboard?.topRiskProject?.delayRisk
                ?.roundToInt()
                ?.coerceIn(0, 100)
                ?: ((overdueTasks.toFloat() / totalTasks) * 100).roundToInt().coerceIn(0, 100)

            val overallEfficiency = aiDashboard?.topRiskProject?.overallEfficiency
                ?.roundToInt()
                ?.coerceIn(0, 100)
                ?: ((doneTasks.size.toFloat() / totalTasks) * 100).roundToInt().coerceIn(0, 100)

            val aiRecommendation = aiDashboard?.recommendations
                ?.firstOrNull()
                ?.takeIf { it.isNotBlank() }
                ?: "No AI recommendation available yet."

            val recommendationAccent = if (
                aiRecommendation.contains("delay", ignoreCase = true) ||
                aiRecommendation.contains("risk", ignoreCase = true) ||
                aiRecommendation.contains("overdue", ignoreCase = true) ||
                aiRecommendation.contains("block", ignoreCase = true)
            ) {
                DashboardNegative
            } else {
                DashboardPositive
            }

            val recommendationBackground = recommendationAccent.copy(alpha = 0.08f)

            val topRiskDescription = aiDashboard?.topRiskProject?.let { risk ->
                val projectName = risk.name?.takeIf { it.isNotBlank() } ?: "No project"
                val projectStatus = risk.status?.takeIf { it.isNotBlank() } ?: "No status"
                "Top risk: $projectName ($projectStatus)"
            } ?: "No AI risk assessment available yet."

            val highRiskCount = aiDashboard?.highRiskProjects ?: 0
            val overdueTasksSubtitle = if (highRiskCount > 0) {
                "$highRiskCount high risk"
            } else {
                "No high risk"
            }

            val activities = notifications
                .sortedByDescending { parseDate(it.sentAt) }
                .map {
                    ActivityItem(
                        title = it.title.ifBlank { "Activity" },
                        description = it.message.ifBlank { "No description available." },
                        relativeTime = relativeTime(it.sentAt)
                    )
                }

            return DashboardMetrics(
                activeProjects = activeProjects,
                activeProjectsSubtitle = "$newProjectsThisMonth created this month",
                pendingTasks = pendingTasks,
                pendingTasksSubtitle = "${(pendingProgress * 100).roundToInt()}% of active workload",
                pendingTasksProgress = pendingProgress,
                overdueTasks = overdueTasks,
                overdueTasksSubtitle = overdueTasksSubtitle,
                averageTimeLabel = averageTimeLabel,
                delayProbability = delayProbability,
                overallEfficiency = overallEfficiency,
                aiRecommendation = aiRecommendation,
                topRiskDescription = topRiskDescription,
                recommendationAccent = recommendationAccent,
                recommendationBackground = recommendationBackground,
                activities = activities
            )
        }

        @RequiresApi(Build.VERSION_CODES.O)
        private fun parseDate(raw: String?): ZonedDateTime? {
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

        @RequiresApi(Build.VERSION_CODES.O)
        private fun relativeTime(raw: String): String {
            val eventTime = parseDate(raw) ?: return ""
            val now = ZonedDateTime.now()
            val duration = java.time.Duration.between(eventTime, now)
            return when {
                duration.toMinutes() < 1 -> "now"
                duration.toHours() < 1 -> "${duration.toMinutes()}m"
                duration.toDays() < 1 -> "${duration.toHours()}h"
                duration.toDays() < 7 -> "${duration.toDays()}d"
                duration.toDays() < 30 -> "${duration.toDays() / 7}w"
                duration.toDays() < 365 -> "${duration.toDays() / 30}mo"
                else -> "${duration.toDays() / 365}y"
            }
        }

        private fun formatOneDecimal(value: Double): String = String.format("%.1f", value)
    }
}

private data class ActivityItem(
    val title: String,
    val description: String,
    val relativeTime: String
)

private fun displayName(user: UserDto?): String {
    val fullName = listOfNotNull(user?.name, user?.lastName)
        .map { it.trim() }
        .filter { it.isNotEmpty() }
        .joinToString(" ")
    return fullName.ifBlank { "there" }
}

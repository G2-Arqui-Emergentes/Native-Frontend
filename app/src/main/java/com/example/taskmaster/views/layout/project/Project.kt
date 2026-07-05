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
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.Icon
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.navigation.NavHostController
import coil.compose.AsyncImage
import com.example.taskmaster.R
import com.example.taskmaster.views.layout.common.AppTopHeader
import com.example.taskmaster.viewmodel.data.ai.LeaderDashboardDto
import com.example.taskmaster.viewmodel.data.ai.MemberPerformanceDto
import com.example.taskmaster.viewmodel.data.notifications.NotificationDto
import com.example.taskmaster.viewmodel.data.projects.ProjectDto
import com.example.taskmaster.viewmodel.data.repo.TasksRepository
import com.example.taskmaster.viewmodel.data.tasks.TaskDto
import com.example.taskmaster.viewmodel.data.tasks.TaskPriority
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
import kotlinx.coroutines.launch

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
private val DashboardInfoBlue = Color(0xFF2563EB)

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun Proyect(
    context: Context,
    nav: NavHostController,
    vm: ProjectsViewModel = remember { ProjectsViewModel() },
    userVm: UsersViewModel = remember { UsersViewModel() },
    teamVm: UsersViewModel = remember { UsersViewModel() },
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
    val teamMembers by teamVm.members.collectAsState()

    val tasks by tasksVm.tasks.collectAsState()
    val tasksLoading by tasksVm.isLoading.collectAsState()
    val tasksError by tasksVm.error.collectAsState()

    val notifications by notificationsVm.notifications.collectAsState()
    val notificationsLoading by notificationsVm.isLoading.collectAsState()
    val notificationsError by notificationsVm.error.collectAsState()

    val aiDashboard by aiVm.dashboard.collectAsState()
    val aiLoading by aiVm.isLoading.collectAsState()
    val aiError by aiVm.error.collectAsState()
    val reportScope = rememberCoroutineScope()
    val tasksRepo = remember { TasksRepository() }
    var selectedMemberReport by remember { mutableStateOf<MemberReportUiModel?>(null) }
    var isReportLoading by remember { mutableStateOf(false) }
    var reportError by remember { mutableStateOf<String?>(null) }

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
            teamVm.loadMembersForProjects(projects.map { it.projectId })
        }
    }

    val membersById = remember(teamMembers) { teamMembers.associateBy { it.id } }
    val memberPerformanceItems = remember(aiDashboard, teamMembers) {
        buildMemberPerformanceItems(
            aiDashboard = aiDashboard,
            membersById = membersById
        )
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
                item {
                    PerformancePerMemberCard(
                        members = memberPerformanceItems,
                        onGenerateReport = { member ->
                            reportError = null
                            isReportLoading = true
                            selectedMemberReport = null
                            reportScope.launch {
                                runCatching {
                                    val userTasks = tasksRepo.getByUser(member.userId)
                                    buildMemberReport(
                                        member = member,
                                        userTasks = userTasks,
                                        recommendations = aiDashboard?.recommendations.orEmpty()
                                    )
                                }.onSuccess { report ->
                                    selectedMemberReport = report
                                }.onFailure {
                                    reportError = it.message ?: "Unable to generate report."
                                }
                                isReportLoading = false
                            }
                        }
                    )
                }
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

    if (isReportLoading || reportError != null || selectedMemberReport != null) {
        MemberReportDialog(
            isLoading = isReportLoading,
            error = reportError,
            report = selectedMemberReport,
            onDismiss = {
                isReportLoading = false
                reportError = null
                selectedMemberReport = null
            }
        )
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
private fun PerformancePerMemberCard(
    members: List<MemberPerformanceUiModel>,
    onGenerateReport: (MemberPerformanceUiModel) -> Unit
) {
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
            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Text(
                    text = "Performance per Member",
                    style = MaterialTheme.typography.titleMedium.copy(
                        color = DashboardTextPrimary,
                        fontWeight = FontWeight.SemiBold
                    )
                )
                Text(
                    text = "Generate individual reports with AI suggestions",
                    style = MaterialTheme.typography.bodySmall,
                    color = DashboardTextSecondary
                )
            }

            if (members.isEmpty()) {
                Text(
                    text = "No member performance available yet.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = DashboardTextSecondary
                )
            } else {
                members.forEach { member ->
                    MemberPerformanceRow(
                        member = member,
                        onGenerateReport = { onGenerateReport(member) }
                    )
                }
            }
        }
    }
}

@Composable
private fun MemberPerformanceRow(
    member: MemberPerformanceUiModel,
    onGenerateReport: () -> Unit
) {
    Card(
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 5.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    if (member.performanceClass == PerformanceClass.REQUIRES_REVIEW) {
                        DashboardNegative.copy(alpha = 0.03f)
                    } else {
                        Color.White
                    }
                )
                .border(
                    1.dp,
                    if (member.performanceClass == PerformanceClass.REQUIRES_REVIEW) {
                        DashboardNegative.copy(alpha = 0.20f)
                    } else {
                        DashboardBorder
                    },
                    RoundedCornerShape(20.dp)
                )
                .padding(14.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                MemberAvatar(member)
                Spacer(modifier = Modifier.width(12.dp))
                Column(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Text(
                        text = member.name,
                        style = MaterialTheme.typography.bodyLarge.copy(
                            color = DashboardTextPrimary,
                            fontWeight = FontWeight.SemiBold
                        )
                    )
                    Text(
                        text = member.performanceHeadline,
                        style = MaterialTheme.typography.bodySmall,
                        color = DashboardTextSecondary
                    )
                }
                PerformanceBadge(member)
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                MemberMiniMetric(
                    iconRes = R.drawable.ic_total_task,
                    value = member.tasksCompleted.toString(),
                    label = "Completed",
                    tint = DashboardPositive,
                    modifier = Modifier.weight(1f)
                )
                MemberMiniMetric(
                    iconRes = R.drawable.ic_overdue_task,
                    value = member.tasksDelayed.toString(),
                    label = "Delayed",
                    tint = DashboardNegative,
                    modifier = Modifier.weight(1f)
                )
            }

            OutlinedButton(
                onClick = onGenerateReport,
                shape = RoundedCornerShape(16.dp),
                border = androidx.compose.foundation.BorderStroke(
                    1.dp,
                    DashboardInfoBlue.copy(alpha = 0.22f)
                ),
                colors = ButtonDefaults.outlinedButtonColors(
                    containerColor = Color(0xFFEFF6FF),
                    contentColor = DashboardInfoBlue
                ),
                contentPadding = PaddingValues(horizontal = 14.dp, vertical = 11.dp),
                modifier = Modifier.align(Alignment.End)
            ) {
                Icon(
                    painter = painterResource(R.drawable.ic_analytics),
                    contentDescription = null,
                    modifier = Modifier.size(18.dp),
                    tint = DashboardInfoBlue
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Generate Report",
                    style = MaterialTheme.typography.labelMedium.copy(
                        fontWeight = FontWeight.SemiBold
                    )
                )
            }
        }
    }
}

@Composable
private fun PerformanceBadge(member: MemberPerformanceUiModel) {
    val background = when (member.performanceClass) {
        PerformanceClass.HIGH -> DashboardPositive.copy(alpha = 0.12f)
        PerformanceClass.STABLE -> DashboardWarning.copy(alpha = 0.14f)
        PerformanceClass.REQUIRES_REVIEW -> DashboardNegative.copy(alpha = 0.12f)
    }

    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(999.dp))
            .background(background)
            .padding(horizontal = 10.dp, vertical = 7.dp)
    ) {
        Text(
            text = "${member.performanceLabel} ${member.score}%",
            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
            color = member.performanceColor
        )
    }
}

@Composable
private fun MemberMiniMetric(
    iconRes: Int,
    value: String,
    label: String,
    tint: Color,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .clip(RoundedCornerShape(16.dp))
            .background(DashboardSurfaceAlt)
            .padding(horizontal = 12.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            painter = painterResource(iconRes),
            contentDescription = null,
            tint = tint,
            modifier = Modifier.size(18.dp)
        )
        Spacer(modifier = Modifier.width(8.dp))
        Column {
            Text(
                text = value,
                style = MaterialTheme.typography.bodyMedium.copy(
                    fontWeight = FontWeight.Bold,
                    color = DashboardTextPrimary
                )
            )
            Text(
                text = label,
                style = MaterialTheme.typography.labelSmall,
                color = DashboardTextSecondary
            )
        }
    }
}

@Composable
private fun MemberAvatar(member: MemberPerformanceUiModel) {
    if (!member.imageUrl.isNullOrBlank()) {
        AsyncImage(
            model = member.imageUrl,
            contentDescription = member.name,
            modifier = Modifier
                .size(46.dp)
                .clip(CircleShape),
            contentScale = ContentScale.Crop
        )
    } else {
        Box(
            modifier = Modifier
                .size(46.dp)
                .clip(CircleShape)
                .background(DashboardSurfaceAlt),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = member.initials,
                style = MaterialTheme.typography.labelLarge.copy(
                    color = DashboardTextPrimary,
                    fontWeight = FontWeight.Bold
                )
            )
        }
    }
}

@Composable
private fun MemberReportDialog(
    isLoading: Boolean,
    error: String?,
    report: MemberReportUiModel?,
    onDismiss: () -> Unit
) {
    Dialog(onDismissRequest = onDismiss) {
        Card(
            shape = RoundedCornerShape(28.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState())
                    .padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Performance Report",
                            style = MaterialTheme.typography.titleLarge.copy(
                                fontWeight = FontWeight.Bold,
                                color = DashboardTextPrimary
                            )
                        )
                        Text(
                            text = "Individual report with AI suggestions",
                            style = MaterialTheme.typography.bodySmall,
                            color = DashboardTextSecondary
                        )
                    }
                    Text(
                        text = "Close",
                        style = MaterialTheme.typography.labelLarge.copy(
                            color = DashboardNegative,
                            fontWeight = FontWeight.SemiBold
                        ),
                        modifier = Modifier.clickable(onClick = onDismiss)
                    )
                }

                when {
                    isLoading -> {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(20.dp),
                                color = DashboardAccent,
                                strokeWidth = 2.dp
                            )
                            Text(
                                text = "Generating report...",
                                style = MaterialTheme.typography.bodyMedium,
                                color = DashboardTextSecondary
                            )
                        }
                    }

                    !error.isNullOrBlank() -> {
                        Text(
                            text = error,
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.error
                        )
                    }

                    report != null -> {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            MemberAvatar(report.member)
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Text(
                                    text = report.member.name,
                                    style = MaterialTheme.typography.titleMedium.copy(
                                        fontWeight = FontWeight.SemiBold,
                                        color = DashboardTextPrimary
                                    )
                                )
                                Text(
                                    text = report.member.performanceHeadline,
                                    style = MaterialTheme.typography.bodySmall,
                                    color = DashboardTextSecondary
                                )
                            }
                        }

                        Card(
                            shape = RoundedCornerShape(20.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = report.member.performanceColor.copy(alpha = 0.08f)
                            )
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 16.dp, vertical = 14.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    painter = painterResource(R.drawable.ic_analytics),
                                    contentDescription = null,
                                    tint = report.member.performanceColor,
                                    modifier = Modifier.size(20.dp)
                                )
                                Spacer(modifier = Modifier.width(10.dp))
                                Column {
                                    Text(
                                        text = "Performance score",
                                        style = MaterialTheme.typography.labelMedium.copy(
                                            fontWeight = FontWeight.SemiBold
                                        ),
                                        color = DashboardTextSecondary
                                    )
                                    Text(
                                        text = "${report.member.score}/100",
                                        style = MaterialTheme.typography.headlineSmall.copy(
                                            fontWeight = FontWeight.Bold
                                        ),
                                        color = DashboardTextPrimary
                                    )
                                }
                                Spacer(modifier = Modifier.weight(1f))
                                PerformanceBadge(report.member)
                            }
                        }

                        ReportMetricGrid(report)
                        ReportStatusBlock(report)
                        ReportAnalysisBlock(report)
                        ReportRecommendations(report.recommendations)
                    }
                }
            }
        }
    }
}

@Composable
private fun ReportMetricGrid(report: MemberReportUiModel) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Text(
            text = "Metrics",
            style = MaterialTheme.typography.labelLarge.copy(
                fontWeight = FontWeight.SemiBold,
                color = DashboardTextPrimary
            )
        )
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            ReportMetricRow(
                iconRes = R.drawable.ic_total_task,
                iconTint = DashboardPositive,
                title = "Completed tasks",
                value = report.tasksCompleted.toString()
            )
            ReportMetricRow(
                iconRes = R.drawable.ic_overdue_task,
                iconTint = DashboardNegative,
                title = "Delayed tasks",
                value = report.tasksDelayed.toString()
            )
            ReportMetricRow(
                iconRes = R.drawable.ic_task,
                iconTint = DashboardWarning,
                title = "Pending tasks",
                value = report.pendingTasks.toString()
            )
            ReportMetricRow(
                iconRes = R.drawable.ic_analytics,
                iconTint = DashboardInfoBlue,
                title = "In progress tasks",
                value = report.inProgressTasks.toString()
            )
            ReportMetricRow(
                iconRes = R.drawable.ic_overdue_task,
                iconTint = DashboardNegative,
                title = "Overdue tasks",
                value = report.overdueTasks.toString()
            )
            ReportMetricRow(
                iconRes = R.drawable.ic_task,
                iconTint = DashboardTextPrimary,
                title = "Dominant priority",
                value = report.dominantPriority
            )
        }
    }
}

@Composable
private fun ReportMetricRow(
    iconRes: Int,
    iconTint: Color,
    title: String,
    value: String
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .background(DashboardSurfaceAlt)
            .padding(horizontal = 12.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            painter = painterResource(iconRes),
            contentDescription = null,
            tint = iconTint,
            modifier = Modifier.size(18.dp)
        )
        Spacer(modifier = Modifier.width(10.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                style = MaterialTheme.typography.labelSmall,
                color = DashboardTextSecondary
            )
            Text(
                text = value,
                style = MaterialTheme.typography.bodyMedium.copy(
                    fontWeight = FontWeight.SemiBold
                ),
                color = DashboardTextPrimary
            )
        }
    }
}

@Composable
private fun ReportStatusBlock(report: MemberReportUiModel) {
    val accent = when (report.member.performanceClass) {
        PerformanceClass.HIGH -> DashboardPositive
        PerformanceClass.STABLE -> DashboardWarning
        PerformanceClass.REQUIRES_REVIEW -> DashboardNegative
    }

    Card(
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = accent.copy(alpha = 0.08f))
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .border(1.dp, accent.copy(alpha = 0.18f), RoundedCornerShape(20.dp))
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = "Status",
                style = MaterialTheme.typography.labelLarge.copy(
                    fontWeight = FontWeight.SemiBold,
                    color = accent
                )
            )
            Text(
                text = report.status.replace('_', ' '),
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Bold,
                    color = DashboardTextPrimary
                )
            )
            Text(
                text = report.member.performanceHeadline,
                style = MaterialTheme.typography.bodyMedium,
                color = DashboardTextSecondary
            )
        }
    }
}

@Composable
private fun ReportAnalysisBlock(report: MemberReportUiModel) {
    Card(
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = DashboardSurfaceAlt)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = "Analysis",
                style = MaterialTheme.typography.labelLarge.copy(
                    fontWeight = FontWeight.SemiBold,
                    color = DashboardInfoBlue
                )
            )
            Text(
                text = report.analysis,
                style = MaterialTheme.typography.bodyMedium,
                color = DashboardTextPrimary
            )
        }
    }
}

@Composable
private fun ReportRecommendations(recommendations: List<String>) {
    Card(
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = DashboardPositive.copy(alpha = 0.06f))
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .border(
                    1.dp,
                    DashboardPositive.copy(alpha = 0.14f),
                    RoundedCornerShape(20.dp)
                )
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Text(
                text = "Recommendations",
                style = MaterialTheme.typography.labelLarge.copy(
                    fontWeight = FontWeight.SemiBold,
                    color = DashboardPositive
                )
            )

            if (recommendations.isEmpty()) {
                Text(
                    text = "No recommendations available for this report.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = DashboardTextSecondary
                )
            } else {
                recommendations.forEach { recommendation ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(14.dp))
                            .background(Color.White.copy(alpha = 0.85f))
                            .padding(horizontal = 12.dp, vertical = 10.dp),
                        verticalAlignment = Alignment.Top
                    ) {
                        Box(
                            modifier = Modifier
                                .padding(top = 5.dp)
                                .size(8.dp)
                                .clip(CircleShape)
                                .background(DashboardPositive)
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(
                            text = recommendation,
                            style = MaterialTheme.typography.bodyMedium,
                            color = DashboardTextPrimary
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
            val now = ZonedDateTime.now()

            val activeProjects = projects.size
            val newProjectsThisMonth = projects.count {
                parseDashboardDate(it.startDate)?.toLocalDate()?.let { date ->
                    date.monthValue == today.monthValue && date.year == today.year
                } == true
            }

            val pendingTasks = tasks.count {
                it.status == TaskStatus.TO_DO || it.status == TaskStatus.IN_PROGRESS
            }

            val overdueTasks = tasks.count {
                val end = parseDashboardDate(it.endDate)
                end != null && end.isBefore(now) && it.status != TaskStatus.DONE
            }

            val overdueThisMonth = tasks.count {
                val end = parseDashboardDate(it.endDate)
                end != null &&
                    end.isBefore(now) &&
                    end.monthValue == today.monthValue &&
                    end.year == today.year &&
                    it.status != TaskStatus.DONE
            }

            val doneTasks = tasks.filter { it.status == TaskStatus.DONE }
            val averageTimeLabel = doneTasks.mapNotNull { task ->
                val start = parseDashboardDate(task.startDate)
                val end = parseDashboardDate(task.endDate)
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
                .sortedByDescending { parseDashboardDate(it.sentAt) }
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
        private fun relativeTime(raw: String): String {
            val eventTime = parseDashboardDate(raw) ?: return ""
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

@RequiresApi(Build.VERSION_CODES.O)
private fun parseDashboardDate(raw: String?): ZonedDateTime? {
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

private enum class PerformanceClass {
    HIGH,
    STABLE,
    REQUIRES_REVIEW
}

private data class MemberPerformanceUiModel(
    val userId: Long,
    val name: String,
    val imageUrl: String?,
    val tasksCompleted: Int,
    val tasksDelayed: Int,
    val performanceLabel: String,
    val performanceHeadline: String,
    val score: Int,
    val performanceClass: PerformanceClass,
    val performanceColor: Color,
    val initials: String
)

private data class MemberReportUiModel(
    val member: MemberPerformanceUiModel,
    val tasksCompleted: Int,
    val tasksDelayed: Int,
    val pendingTasks: Int,
    val inProgressTasks: Int,
    val overdueTasks: Int,
    val dominantPriority: String,
    val status: String,
    val analysis: String,
    val recommendations: List<String>
)

private fun buildMemberPerformanceItems(
    aiDashboard: LeaderDashboardDto?,
    membersById: Map<Long, UserDto>
): List<MemberPerformanceUiModel> {
    return aiDashboard?.memberPerformances
        ?.map { performance ->
            val member = membersById[performance.userId]
            val score = performance.score.roundToInt()
            val performanceClass = getPerformanceClass(performance.performanceLevel, score)
            MemberPerformanceUiModel(
                userId = performance.userId,
                name = member?.let { "${it.name} ${it.lastName}".trim() }
                    ?.takeIf { it.isNotBlank() }
                    ?: performance.userName,
                imageUrl = member?.imageUrl,
                tasksCompleted = performance.tasksCompleted,
                tasksDelayed = performance.tasksDelayed,
                performanceLabel = formatPerformanceLevel(performance.performanceLevel),
                performanceHeadline = performanceHeadline(performance.performanceLevel, score),
                score = score,
                performanceClass = performanceClass,
                performanceColor = when (performanceClass) {
                    PerformanceClass.HIGH -> DashboardPositive
                    PerformanceClass.STABLE -> DashboardWarning
                    PerformanceClass.REQUIRES_REVIEW -> DashboardNegative
                },
                initials = memberInitials(member?.let { "${it.name} ${it.lastName}".trim() } ?: performance.userName)
            )
        }
        ?.sortedByDescending { it.score }
        .orEmpty()
}

@RequiresApi(Build.VERSION_CODES.O)
private fun buildMemberReport(
    member: MemberPerformanceUiModel,
    userTasks: List<TaskDto>,
    recommendations: List<String>
): MemberReportUiModel {
    val pendingTasks = userTasks.count { it.status == TaskStatus.TO_DO }
    val inProgressTasks = userTasks.count { it.status == TaskStatus.IN_PROGRESS }
    val doneTasks = userTasks.count { it.status == TaskStatus.DONE }
    val overdueTasks = userTasks.count {
        val end = parseDashboardDate(it.endDate)
        end != null && end.isBefore(ZonedDateTime.now()) && it.status != TaskStatus.DONE
    }
    val dominantPriority = userTasks
        .groupingBy { it.priority }
        .eachCount()
        .maxByOrNull { it.value }
        ?.key
        ?.let(::priorityLabel)
        ?: "N/A"

    val status = when {
        member.score >= 85 && member.tasksDelayed == 0 && overdueTasks == 0 -> "GOOD_STANDING"
        member.score < 70 || member.tasksDelayed > 0 || overdueTasks > 0 -> "REQUIRES_REVIEW"
        else -> "STABLE"
    }

    val analysis = when {
        member.score >= 85 && member.tasksDelayed == 0 && overdueTasks == 0 ->
            "This member maintains strong performance and shows no relevant delays."
        member.score < 70 ->
            "This member shows low performance for the evaluated period and needs closer follow-up."
        member.tasksDelayed > 0 || overdueTasks > 0 ->
            "This member maintains acceptable performance, but delayed work could impact delivery."
        else ->
            "This member keeps stable performance. Continue monitoring tasks in progress and upcoming deadlines."
    }

    return MemberReportUiModel(
        member = member,
        tasksCompleted = if (member.tasksCompleted > 0) member.tasksCompleted else doneTasks,
        tasksDelayed = member.tasksDelayed,
        pendingTasks = pendingTasks,
        inProgressTasks = inProgressTasks,
        overdueTasks = overdueTasks,
        dominantPriority = dominantPriority,
        status = status,
        analysis = analysis,
        recommendations = recommendations
    )
}

private fun getPerformanceClass(level: String, score: Int): PerformanceClass {
    val normalized = level.uppercase()
    return when {
        normalized.contains("HIGH") || score >= 85 -> PerformanceClass.HIGH
        normalized.contains("LOW") || normalized.contains("RISK") || score < 70 -> PerformanceClass.REQUIRES_REVIEW
        else -> PerformanceClass.STABLE
    }
}

private fun formatPerformanceLevel(level: String): String {
    val normalized = level.trim()
    if (normalized.isBlank()) return "No score yet"
    return normalized
        .replace('_', ' ')
        .lowercase()
        .split(' ')
        .filter { it.isNotBlank() }
        .joinToString(" ") { token -> token.replaceFirstChar { it.uppercase() } }
}

private fun memberInitials(name: String): String {
    return name
        .split(Regex("\\s+"))
        .filter { it.isNotBlank() }
        .take(2)
        .joinToString("") { part -> part.take(1).uppercase() }
        .ifBlank { "M" }
}

private fun performanceHeadline(level: String, score: Int): String {
    val normalized = level.uppercase()
    return when {
        normalized.contains("HIGH") || score >= 85 -> "High performance"
        normalized.contains("LOW") || normalized.contains("RISK") || score < 70 -> "Needs review"
        normalized.contains("STABLE") -> "Stable performance"
        score <= 0 -> "No score available yet"
        else -> "Performance in progress"
    }
}

private fun priorityLabel(priority: TaskPriority): String {
    return when (priority) {
        TaskPriority.HIGH -> "HIGH"
        TaskPriority.MEDIUM -> "MEDIUM"
        TaskPriority.LOW -> "LOW"
    }
}

private fun displayName(user: UserDto?): String {
    val fullName = listOfNotNull(user?.name, user?.lastName)
        .map { it.trim() }
        .filter { it.isNotEmpty() }
        .joinToString(" ")
    return fullName
        .split(Regex("\\s+"))
        .firstOrNull()
        ?.takeIf { it.isNotBlank() }
        ?: "there"
}

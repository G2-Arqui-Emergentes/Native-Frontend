package com.example.taskmaster.views.layout.project

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.example.taskmaster.viewmodel.model.ProjectStatsViewModel
import com.example.taskmaster.viewmodel.model.ProjectsViewModel
import com.example.taskmaster.views.layout.stat.PriorityChart
import com.example.taskmaster.views.layout.stat.StatsCards
import com.example.taskmaster.views.layout.stat.TaskStatusOverview

private val StatsBackground = Color(0xFFF9FAFB)
private val StatsCard = Color(0xFFFFFFFF)
private val StatsMuted = Color(0xFFF4F5F7)
private val StatsTextPrimary = Color(0xFF111827)
private val StatsTextSecondary = Color(0xFF6B7280)
private val StatsBrand = Color(0xFFEC1926)
private val StatsPositive = Color(0xFF0F9E6E)

@Composable
fun ProjectStats(
    nav: NavHostController,
    projectId: Long,
    projectsVm: ProjectsViewModel = remember { ProjectsViewModel() },
    statsVm: ProjectStatsViewModel = remember { ProjectStatsViewModel() }
) {
    val project by projectsVm.current.collectAsState()
    val isLoading by statsVm.isLoading.collectAsState()
    val error by statsVm.error.collectAsState()
    val stats by statsVm.stats.collectAsState()

    LaunchedEffect(projectId) {
        projectsVm.loadById(projectId)
    }

    LaunchedEffect(projectId, project?.budget) {
        statsVm.loadProjectTasks(projectId, project?.budget ?: 0.0)
    }

    Scaffold(
        containerColor = StatsBackground,
        topBar = {
            Column {
                ProjectTopBar(
                    title = project?.name ?: "Statistics",
                    onBack = { nav.popBackStack() },
                    onNotificationsClick = { nav.navigate("notification") },
                    onProfileClick = { nav.navigate("profile") }
                )
                ProjectMiniTabs(
                    selected = ProjectSection.STATS,
                    onSelect = {
                        when (it) {
                            ProjectSection.TASKS -> nav.navigate("projectTasks/$projectId") { launchSingleTop = true }
                            ProjectSection.STATS -> Unit
                            ProjectSection.SETTINGS -> nav.navigate("projectSettings/$projectId") { launchSingleTop = true }
                        }
                    }
                )
            }
        }
    ) { inner ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(inner)
        ) {
            when {
                isLoading -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator(color = StatsBrand)
                    }
                }

                !error.isNullOrBlank() -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = error ?: "Unknown error",
                            color = MaterialTheme.colorScheme.error
                        )
                    }
                }

                else -> {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .verticalScroll(rememberScrollState())
                    ) {
                        Spacer(modifier = Modifier.height(13.dp))
                        StatsCards(stats = stats)
                        Spacer(modifier = Modifier.height(13.dp))
                        TaskStatusOverview(stats = stats)
                        Spacer(modifier = Modifier.height(13.dp))
                        PriorityChart(stats = stats)
                        Spacer(modifier = Modifier.height(13.dp))
                        BudgetOverviewCard(
                            totalBudget = stats.budget,
                            usedBudget = stats.usedBudget
                        )
                        Spacer(modifier = Modifier.height(13.dp))
                        WorstMemberCard(
                            worstMember = stats.worstMember,
                            overdueTasks = stats.overdueTasks
                        )
                        Spacer(modifier = Modifier.height(24.dp))
                    }
                }
            }
        }
    }
}

@Composable
private fun BudgetOverviewCard(
    totalBudget: Double,
    usedBudget: Double
) {
    val safeTotal = totalBudget.coerceAtLeast(0.0)
    val safeUsed = usedBudget.coerceIn(0.0, safeTotal)
    val unused = (safeTotal - safeUsed).coerceAtLeast(0.0)
    val progress = if (safeTotal > 0.0) (safeUsed / safeTotal).toFloat() else 0f

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = StatsCard),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier.padding(18.dp)
        ) {
            Text(
                text = "Budget Overview",
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                color = StatsTextPrimary
            )
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = "Used and available budget based on completed tasks.",
                style = MaterialTheme.typography.bodyMedium,
                color = StatsTextSecondary
            )
            Spacer(modifier = Modifier.height(18.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(18.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                BudgetDonutChart(progress = progress)
                Column(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    BudgetLegend(color = StatsBrand, label = "Used", value = formatBudget(safeUsed))
                    BudgetLegend(color = StatsPositive, label = "Unused", value = formatBudget(unused))
                    BudgetLegend(color = StatsTextSecondary, label = "Total", value = formatBudget(safeTotal))
                }
            }
        }
    }
}

@Composable
private fun BudgetDonutChart(progress: Float) {
    Box(
        modifier = Modifier.size(132.dp),
        contentAlignment = Alignment.Center
    ) {
        Canvas(modifier = Modifier.size(132.dp)) {
            drawArc(
                color = StatsMuted,
                startAngle = -90f,
                sweepAngle = 360f,
                useCenter = false,
                style = Stroke(width = 28f, cap = StrokeCap.Round)
            )
            drawArc(
                brush = Brush.sweepGradient(listOf(StatsBrand, StatsPositive, StatsBrand)),
                startAngle = -90f,
                sweepAngle = 360f * progress.coerceIn(0f, 1f),
                useCenter = false,
                style = Stroke(width = 28f, cap = StrokeCap.Round)
            )
        }
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = "${(progress.coerceIn(0f, 1f) * 100).toInt()}%",
                style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold),
                color = StatsTextPrimary
            )
            Text(
                text = "spent",
                style = MaterialTheme.typography.labelMedium,
                color = StatsTextSecondary
            )
        }
    }
}

@Composable
private fun BudgetLegend(
    color: Color,
    label: String,
    value: String
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        Box(
            modifier = Modifier
                .size(12.dp)
                .clip(CircleShape)
                .background(color)
        )
        Column {
            Text(
                text = label,
                style = MaterialTheme.typography.labelMedium,
                color = StatsTextSecondary
            )
            Text(
                text = value,
                style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.SemiBold),
                color = StatsTextPrimary
            )
        }
    }
}

@Composable
private fun WorstMemberCard(
    worstMember: String,
    overdueTasks: Int
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = StatsCard),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(18.dp),
            horizontalArrangement = Arrangement.spacedBy(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(52.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(StatsBrand.copy(alpha = 0.12f)),
                contentAlignment = Alignment.Center
            ) {
                Canvas(modifier = Modifier.size(22.dp)) {
                    drawCircle(color = StatsBrand, radius = size.minDimension / 2f, center = center)
                    drawLine(
                        color = Color.White,
                        start = Offset(center.x, center.y - 6f),
                        end = Offset(center.x, center.y + 4f),
                        strokeWidth = 4f
                    )
                    drawCircle(color = Color.White, radius = 2.5f, center = Offset(center.x, center.y + 9f))
                }
            }
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "Lowest performing member",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                    color = StatsTextPrimary
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = worstMember.ifBlank { "None" },
                    style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.SemiBold),
                    color = StatsBrand
                )
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = if (overdueTasks > 0) "$overdueTasks overdue tasks are still open." else "No overdue tasks detected right now.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = StatsTextSecondary
                )
            }
        }
    }
}

private fun formatBudget(value: Double): String = "$" + String.format("%.2f", value)

package com.example.taskmaster.views.layout

import android.content.Context
import android.graphics.Paint
import androidx.compose.animation.core.animate
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.clipPath
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.example.taskmaster.ui.theme.AlertRed
import com.example.taskmaster.ui.theme.Brownish900
import com.example.taskmaster.ui.theme.PriorityGreen
import com.example.taskmaster.ui.theme.PriorityYellow
import com.example.taskmaster.ui.theme.RedWine500
import com.example.taskmaster.ui.theme.RedWine600
import com.example.taskmaster.ui.theme.White
import com.example.taskmaster.viewmodel.data.tasks.TaskDto
import com.example.taskmaster.viewmodel.data.tasks.TaskPriority
import com.example.taskmaster.viewmodel.model.CalendarDay
import com.example.taskmaster.viewmodel.model.CalendarViewModel
import com.example.taskmaster.viewmodel.model.ProjectsViewModel
import com.example.taskmaster.viewmodel.sharedPreferences.Prefs
import com.example.taskmaster.viewmodel.ui.users.UsersViewModel
import com.example.taskmaster.views.layout.common.AppTopHeader
import kotlinx.coroutines.launch
import java.time.YearMonth
import java.time.format.TextStyle
import java.util.Locale

private const val CALENDAR_ROWS = 6
private const val CALENDAR_COLUMNS = 7

@Composable
fun Calendar(
    context: Context,
    nav: NavHostController,
    userVm: UsersViewModel = remember { UsersViewModel() },
    projectsVm: ProjectsViewModel = remember { ProjectsViewModel() },
    vm: CalendarViewModel = remember { CalendarViewModel() }
) {
    val isLoading by vm.isLoading.collectAsState()
    val error by vm.error.collectAsState()
    val selectedDate by vm.selectedDate.collectAsState()
    val currentMonth by vm.currentMonth.collectAsState()
    val calendarDays by vm.calendarDays.collectAsState(initial = emptyList())
    val selectedDateTasks by vm.selectedDateTasks.collectAsState(initial = emptyList())
    val user by userVm.user.collectAsState()
    val projects by projectsVm.projects.collectAsState()

    val locale = Locale.getDefault()
    val isSpanish = locale.language.startsWith("es")

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

    LaunchedEffect(projects) {
        val projectIds = projects.map { it.projectId }.filter { it > 0L }
        if (projectIds.isNotEmpty()) {
            vm.loadTasksByProjects(projectIds)
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF9FAFB))
            .padding(horizontal = 16.dp, vertical = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        AppTopHeader(
            user = user,
            onNotificationsClick = { nav.navigate("notification") },
            onProfileClick = { nav.navigate("profile") }
        )

        Text(
            text = if (isSpanish) "Calendario" else "Calendar",
            style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold),
            color = MaterialTheme.colorScheme.onBackground
        )

        CalendarHeader(
            currentMonth = currentMonth,
            locale = locale,
            isSpanish = isSpanish,
            onPreviousMonth = { vm.navigateToMonth(currentMonth.minusMonths(1)) },
            onNextMonth = { vm.navigateToMonth(currentMonth.plusMonths(1)) }
        )

        WeekDaysHeader(isSpanish = isSpanish)

        CalendarGrid(
            calendarDays = calendarDays,
            selectedDate = selectedDate,
            onDayClick = { day -> vm.selectDate(day.date) },
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(1.2f)
        )

        if (isLoading) {
            Box(Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
            }
        }

        error?.let { msg ->
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = AlertRed.copy(alpha = 0.1f))
            ) {
                Text(
                    text = msg,
                    modifier = Modifier.padding(16.dp),
                    color = AlertRed,
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }

        selectedDate?.let { date ->
            TasksForSelectedDay(
                selectedDate = date,
                tasks = selectedDateTasks,
                locale = locale,
                isSpanish = isSpanish
            )
        }
    }
}

@Composable
private fun CalendarHeader(
    currentMonth: YearMonth,
    locale: Locale,
    isSpanish: Boolean,
    onPreviousMonth: () -> Unit,
    onNextMonth: () -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        androidx.compose.material3.IconButton(onClick = onPreviousMonth) {
            androidx.compose.material3.Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                contentDescription = if (isSpanish) "Mes anterior" else "Previous month",
                tint = MaterialTheme.colorScheme.primary
            )
        }

        Text(
            text = "${currentMonth.month.getDisplayName(TextStyle.FULL, locale)} ${currentMonth.year}",
            style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
            color = MaterialTheme.colorScheme.onBackground,
            textAlign = TextAlign.Center
        )

        androidx.compose.material3.IconButton(onClick = onNextMonth) {
            androidx.compose.material3.Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                contentDescription = if (isSpanish) "Mes siguiente" else "Next month",
                tint = MaterialTheme.colorScheme.primary
            )
        }
    }
}

@Composable
private fun WeekDaysHeader(isSpanish: Boolean) {
    val weekDays = if (isSpanish) {
        listOf("Sun", "Mon", "Tue", "Wed", "Thu", "Fri", "Sat")
    } else {
        listOf("Sun", "Mon", "Tue", "Wed", "Thu", "Fri", "Sat")
    }

    Row(modifier = Modifier.fillMaxWidth()) {
        weekDays.forEach { day ->
            Text(
                text = day,
                modifier = Modifier.weight(1f),
                textAlign = TextAlign.Center,
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f)
            )
        }
    }
}

@Composable
private fun CalendarGrid(
    calendarDays: List<CalendarDay>,
    selectedDate: java.time.LocalDate?,
    onDayClick: (CalendarDay) -> Unit,
    modifier: Modifier = Modifier
) {
    var canvasSize by remember { mutableStateOf(Size.Zero) }
    var clickAnimationOffset by remember { mutableStateOf(Offset.Zero) }
    var animationRadius by remember { mutableStateOf(0f) }
    val scope = rememberCoroutineScope()
    val density = LocalDensity.current

    Canvas(
        modifier = modifier.pointerInput(calendarDays) {
            detectTapGestures(
                onTap = { offset ->
                    val column = (offset.x / canvasSize.width * CALENDAR_COLUMNS).toInt()
                    val row = (offset.y / canvasSize.height * CALENDAR_ROWS).toInt()
                    val dayIndex = row * CALENDAR_COLUMNS + column

                    if (dayIndex < calendarDays.size) {
                        onDayClick(calendarDays[dayIndex])
                        clickAnimationOffset = offset
                        scope.launch {
                            animate(0f, 225f, animationSpec = tween(300)) { value, _ ->
                                animationRadius = value
                            }
                            animationRadius = 0f
                        }
                    }
                }
            )
        }
    ) {
        val canvasHeight = size.height
        val canvasWidth = size.width
        canvasSize = Size(canvasWidth, canvasHeight)
        val ySteps = canvasHeight / CALENDAR_ROWS
        val xSteps = canvasWidth / CALENDAR_COLUMNS

        if (animationRadius > 0f) {
            val column = (clickAnimationOffset.x / canvasSize.width * CALENDAR_COLUMNS).toInt()
            val row = (clickAnimationOffset.y / canvasSize.height * CALENDAR_ROWS).toInt()

            val path = Path().apply {
                moveTo(column * xSteps, row * ySteps)
                lineTo((column + 1) * xSteps, row * ySteps)
                lineTo((column + 1) * xSteps, (row + 1) * ySteps)
                lineTo(column * xSteps, (row + 1) * ySteps)
                close()
            }

            clipPath(path) {
                drawCircle(
                    brush = Brush.radialGradient(
                        listOf(RedWine600.copy(alpha = 0.8f), RedWine600.copy(alpha = 0.2f)),
                        center = clickAnimationOffset,
                        radius = animationRadius + 0.1f
                    ),
                    radius = animationRadius + 0.1f,
                    center = clickAnimationOffset
                )
            }
        }

        drawRoundRect(
            RedWine600,
            cornerRadius = CornerRadius(25f, 25f),
            style = Stroke(width = 15f)
        )

        for (i in 1 until CALENDAR_ROWS) {
            drawLine(
                color = RedWine600,
                start = Offset(0f, ySteps * i),
                end = Offset(canvasWidth, ySteps * i),
                strokeWidth = 15f
            )
        }

        for (i in 1 until CALENDAR_COLUMNS) {
            drawLine(
                color = RedWine600,
                start = Offset(xSteps * i, 0f),
                end = Offset(xSteps * i, canvasHeight),
                strokeWidth = 15f
            )
        }

        val textHeight = with(density) { 18.dp.toPx() }
        calendarDays.forEachIndexed { index, day ->
            val column = index % CALENDAR_COLUMNS
            val row = index / CALENDAR_COLUMNS
            val textPositionX = xSteps * column + 15f
            val textPositionY = row * ySteps + textHeight + 15f

            val isSelected = selectedDate == day.date
            val textColor = when {
                isSelected -> White
                !day.isCurrentMonth -> RedWine500.copy(alpha = 0.5f)
                day.tasks.isNotEmpty() -> RedWine600
                else -> Brownish900
            }.toArgb()

            if (isSelected) {
                drawRoundRect(
                    RedWine600,
                    topLeft = Offset(column * xSteps + 8f, row * ySteps + 8f),
                    size = Size(xSteps - 16f, ySteps - 16f),
                    cornerRadius = CornerRadius(8f, 8f)
                )
            }

            if (!isSelected && day.tasks.isNotEmpty()) {
                drawRoundRect(
                    RedWine500.copy(alpha = 0.14f),
                    topLeft = Offset(column * xSteps + 8f, row * ySteps + 8f),
                    size = Size(xSteps - 16f, ySteps - 16f),
                    cornerRadius = CornerRadius(8f, 8f)
                )
                drawRoundRect(
                    color = RedWine500.copy(alpha = 0.40f),
                    topLeft = Offset(column * xSteps + 8f, row * ySteps + 8f),
                    size = Size(xSteps - 16f, ySteps - 16f),
                    cornerRadius = CornerRadius(8f, 8f),
                    style = Stroke(width = 3f)
                )
            }

            if (day.tasks.isNotEmpty()) {
                drawCircle(
                    color = when (day.tasks.maxByOrNull { it.priority.ordinal }?.priority) {
                        TaskPriority.HIGH -> AlertRed
                        TaskPriority.MEDIUM -> PriorityYellow
                        TaskPriority.LOW -> PriorityGreen
                        null -> RedWine600
                    },
                    radius = 5f,
                    center = Offset(column * xSteps + xSteps - 15f, row * ySteps + 15f)
                )
            }

            drawContext.canvas.nativeCanvas.drawText(
                day.day.toString(),
                textPositionX,
                textPositionY,
                Paint().apply {
                    textSize = textHeight
                    color = textColor
                    isFakeBoldText = isSelected || day.tasks.isNotEmpty()
                    isAntiAlias = true
                }
            )
        }
    }
}

@Composable
private fun TasksForSelectedDay(
    selectedDate: java.time.LocalDate,
    tasks: List<TaskDto>,
    locale: Locale,
    isSpanish: Boolean
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(10.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = if (isSpanish) {
                    "Tasks for ${selectedDate.dayOfMonth} ${selectedDate.month.getDisplayName(TextStyle.FULL, locale)}"
                } else {
                    "Tasks for ${selectedDate.dayOfMonth} ${selectedDate.month.getDisplayName(TextStyle.FULL, locale)}"
                },
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.onSurface
            )

            if (tasks.isEmpty()) {
                Text(
                    text = if (isSpanish) "No tasks scheduled for this day" else "No tasks scheduled for this day",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                    modifier = Modifier.padding(vertical = 8.dp)
                )
            } else {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.heightIn(max = 300.dp)
                ) {
                    items(tasks) { task ->
                        TaskItemForCalendar(task = task)
                    }
                }
            }
        }
    }
}

@Composable
private fun TaskItemForCalendar(task: TaskDto) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(8.dp),
        colors = CardDefaults.cardColors(
            containerColor = when (task.priority) {
                TaskPriority.HIGH -> AlertRed.copy(alpha = 0.1f)
                TaskPriority.MEDIUM -> PriorityYellow.copy(alpha = 0.1f)
                TaskPriority.LOW -> PriorityGreen.copy(alpha = 0.1f)
            }
        )
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(12.dp)
                    .background(
                        color = when (task.priority) {
                            TaskPriority.HIGH -> AlertRed
                            TaskPriority.MEDIUM -> PriorityYellow
                            TaskPriority.LOW -> PriorityGreen
                        },
                        shape = RoundedCornerShape(6.dp)
                    )
            )

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = task.title,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurface
                )

                if (task.description.isNotBlank()) {
                    Text(
                        text = task.description,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
                        maxLines = 2
                    )
                }

                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.padding(top = 4.dp)
                ) {
                    Surface(
                        shape = RoundedCornerShape(4.dp),
                        color = when (task.status.name) {
                            "TO_DO" -> RedWine500.copy(alpha = 0.2f)
                            "IN_PROGRESS" -> PriorityYellow.copy(alpha = 0.3f)
                            "DONE" -> PriorityGreen.copy(alpha = 0.3f)
                            "CANCELED" -> AlertRed.copy(alpha = 0.2f)
                            else -> MaterialTheme.colorScheme.surface
                        }
                    ) {
                        Text(
                            text = when (task.status.name) {
                                "TO_DO" -> "To do"
                                "IN_PROGRESS" -> "In progress"
                                "DONE" -> "Done"
                                "CANCELED" -> "Canceled"
                                else -> task.status.name
                            },
                            style = MaterialTheme.typography.labelSmall,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }

                    Surface(
                        shape = RoundedCornerShape(4.dp),
                        color = when (task.priority) {
                            TaskPriority.HIGH -> AlertRed.copy(alpha = 0.2f)
                            TaskPriority.MEDIUM -> PriorityYellow.copy(alpha = 0.2f)
                            TaskPriority.LOW -> PriorityGreen.copy(alpha = 0.2f)
                        }
                    ) {
                        Text(
                            text = when (task.priority) {
                                TaskPriority.HIGH -> "High"
                                TaskPriority.MEDIUM -> "Medium"
                                TaskPriority.LOW -> "Low"
                            },
                            style = MaterialTheme.typography.labelSmall,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                }
            }
        }
    }
}

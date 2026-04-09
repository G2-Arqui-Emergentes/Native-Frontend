package com.example.taskmaster.views.layout

import android.graphics.Paint
import androidx.compose.animation.core.animate
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.clipPath
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.taskmaster.ui.theme.*
import com.example.taskmaster.viewmodel.data.tasks.TaskDto
import com.example.taskmaster.viewmodel.data.tasks.TaskPriority
import com.example.taskmaster.viewmodel.model.CalendarDay
import com.example.taskmaster.viewmodel.model.CalendarViewModel
import kotlinx.coroutines.launch
import java.time.YearMonth
import java.time.format.TextStyle
import java.util.Locale

private const val CALENDAR_ROWS = 6
private const val CALENDAR_COLUMNS = 7

@Composable
fun Calendar(
    vm: CalendarViewModel = remember { CalendarViewModel() }
) {
    val isLoading by vm.isLoading.collectAsState()
    val error by vm.error.collectAsState()
    val selectedDate by vm.selectedDate.collectAsState()
    val currentMonth by vm.currentMonth.collectAsState()
    val calendarDays by vm.calendarDays.collectAsState(initial = emptyList())
    val selectedDateTasks by vm.selectedDateTasks.collectAsState(initial = emptyList())

    LaunchedEffect(Unit) { vm.loadAllTasks() }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            text = "Calendario",
            style = MaterialTheme.typography.displayMedium,
            color = MaterialTheme.colorScheme.onBackground,
            modifier = Modifier.padding(bottom = 4.dp)
        )

        CalendarHeader(
            currentMonth = currentMonth,
            onPreviousMonth = { vm.navigateToMonth(currentMonth.minusMonths(1)) },
            onNextMonth = { vm.navigateToMonth(currentMonth.plusMonths(1)) }
        )

        WeekDaysHeader()

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
            TasksForSelectedDay(selectedDate = date, tasks = selectedDateTasks)
        }
    }
}

@Composable
private fun CalendarHeader(
    currentMonth: YearMonth,
    onPreviousMonth: () -> Unit,
    onNextMonth: () -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        IconButton(onClick = onPreviousMonth) {
            Icon(
                imageVector = Icons.Default.ArrowBack,
                contentDescription = "Mes anterior",
                tint = MaterialTheme.colorScheme.primary
            )
        }

        Text(
            text = "${currentMonth.month.getDisplayName(TextStyle.FULL, Locale("es", "ES"))} ${currentMonth.year}",
            style = MaterialTheme.typography.displayMedium,
            color = MaterialTheme.colorScheme.onBackground,
            textAlign = TextAlign.Center
        )

        IconButton(onClick = onNextMonth) {
            Icon(
                imageVector = Icons.Default.ArrowForward,
                contentDescription = "Mes siguiente",
                tint = MaterialTheme.colorScheme.primary
            )
        }
    }
}

@Composable
private fun WeekDaysHeader() {
    val weekDays = listOf("Dom", "Lun", "Mar", "Mié", "Jue", "Vie", "Sáb")

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
        modifier = modifier
            .pointerInput(calendarDays) {
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

        // Dibujar animación de click
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
                        listOf(
                            RedWine600.copy(alpha = 0.8f),
                            RedWine600.copy(alpha = 0.2f)
                        ),
                        center = clickAnimationOffset,
                        radius = animationRadius + 0.1f
                    ),
                    radius = animationRadius + 0.1f,
                    center = clickAnimationOffset
                )
            }
        }

        // Dibujar bordes del calendario
        drawRoundRect(
            RedWine600,
            cornerRadius = CornerRadius(25f, 25f),
            style = Stroke(width = 15f)
        )

        // Dibujar líneas horizontales
        for (i in 1 until CALENDAR_ROWS) {
            drawLine(
                color = RedWine600,
                start = Offset(0f, ySteps * i),
                end = Offset(canvasWidth, ySteps * i),
                strokeWidth = 15f
            )
        }

        // Dibujar líneas verticales
        for (i in 1 until CALENDAR_COLUMNS) {
            drawLine(
                color = RedWine600,
                start = Offset(xSteps * i, 0f),
                end = Offset(xSteps * i, canvasHeight),
                strokeWidth = 15f
            )
        }

        // Dibujar números de días
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

            // Fondo para día seleccionado
            if (isSelected) {
                drawRoundRect(
                    RedWine600,
                    topLeft = Offset(column * xSteps + 8f, row * ySteps + 8f),
                    size = Size(xSteps - 16f, ySteps - 16f),
                    cornerRadius = CornerRadius(8f, 8f)
                )
            }

            // Indicador de tareas
            if (day.tasks.isNotEmpty()) {
                drawCircle(
                    color = when (day.tasks.maxByOrNull { it.priority.ordinal }?.priority) {
                        TaskPriority.HIGH -> AlertRed
                        TaskPriority.MEDIUM -> PriorityYellow
                        TaskPriority.LOW -> PriorityGreen
                        null -> RedWine600
                    },
                    radius = 4f,
                    center = Offset(
                        column * xSteps + xSteps - 15f,
                        row * ySteps + 15f
                    )
                )
            }

            drawContext.canvas.nativeCanvas.apply {
                drawText(
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
}

@Composable
private fun TasksForSelectedDay(
    selectedDate: java.time.LocalDate,
    tasks: List<TaskDto>
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
                text = "Tareas para ${selectedDate.dayOfMonth} de ${selectedDate.month.getDisplayName(TextStyle.FULL, Locale("es", "ES"))}",
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.onSurface
            )

            if (tasks.isEmpty()) {
                Text(
                    text = "No hay tareas programadas para este día",
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
            // Indicador de prioridad
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
                    // Estado de la tarea
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
                                "TO_DO" -> "Por hacer"
                                "IN_PROGRESS" -> "En progreso"
                                "DONE" -> "Completada"
                                "CANCELED" -> "Cancelada"
                                else -> task.status.name
                            },
                            style = MaterialTheme.typography.labelSmall,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }

                    // Prioridad
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
                                TaskPriority.HIGH -> "Alta"
                                TaskPriority.MEDIUM -> "Media"
                                TaskPriority.LOW -> "Baja"
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
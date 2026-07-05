package com.example.taskmaster.views.layout.task

import android.app.DatePickerDialog
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.example.taskmaster.R
import com.example.taskmaster.viewmodel.ui.users.UsersViewModel
import com.example.taskmaster.views.layout.project. PriorityFilter
import com.example.taskmaster.views.layout.project.StatusFilter
import com.example.taskmaster.views.layout.project.TaskFilters
import java.util.Calendar

@Composable
fun TaskFilterDialog(
    visible: Boolean,
    projectId: Long,
    usersVm: UsersViewModel,
    initialFilters: TaskFilters,
    onApply: (TaskFilters) -> Unit,
    onDismiss: () -> Unit
) {
    if (!visible) return

    LaunchedEffect(projectId) {
        usersVm.loadMembersForProject(projectId)
    }

    val members by usersVm.members.collectAsState()
    val membersLoading by usersVm.isLoading.collectAsState()
    val membersError by usersVm.error.collectAsState()

    var priority by remember(initialFilters) { mutableStateOf(initialFilters.priority) }
    var status by remember(initialFilters) { mutableStateOf(initialFilters.status) }
    var selectedMemberId by remember(initialFilters) { mutableStateOf(initialFilters.memberId) }
    var dateFrom by remember(initialFilters) { mutableStateOf(initialFilters.dateFrom ?: "") }
    var dateTo by remember(initialFilters) { mutableStateOf(initialFilters.dateTo ?: "") }

    Dialog(onDismissRequest = onDismiss) {
        Card(
            shape = MaterialTheme.shapes.large,
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surface
            ),
            modifier = Modifier.fillMaxWidth(0.9f)
        ) {
            Column(
                modifier = Modifier
                    .padding(horizontal = 20.dp, vertical = 16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Filters",
                    style = MaterialTheme.typography.titleLarge
                )

                Spacer(Modifier.height(12.dp))
                Column(Modifier.fillMaxWidth()) {
                    Text(
                        text = "Priority:",
                        style = MaterialTheme.typography.bodySmall,
                    )
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.fillMaxWidth()
                    ) {

                        Row(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            FilterRadioChip(
                                label = "High",
                                selected = priority == PriorityFilter.HIGH,
                                onClick = {
                                    priority =
                                        if (priority == PriorityFilter.HIGH) null
                                        else PriorityFilter.HIGH
                                }
                            )
                            FilterRadioChip(
                                label = "Medium",
                                selected = priority == PriorityFilter.MEDIUM,
                                onClick = {
                                    priority =
                                        if (priority == PriorityFilter.MEDIUM) null
                                        else PriorityFilter.MEDIUM
                                }
                            )
                            FilterRadioChip(
                                label = "Low",
                                selected = priority == PriorityFilter.LOW,
                                onClick = {
                                    priority =
                                        if (priority == PriorityFilter.LOW) null
                                        else PriorityFilter.LOW
                                }
                            )
                        }
                    }
                }


                Spacer(Modifier.height(12.dp))

                Column(Modifier.fillMaxWidth()) {
                    Text(
                        text = "Assignee:",
                        style = MaterialTheme.typography.bodySmall
                    )
                    Spacer(Modifier.height(4.dp))

                    var memberMenu by remember { mutableStateOf(false) }

                    Box {
                        val selectedMemberName = when {
                            membersLoading -> "Loading..."
                            !membersError.isNullOrBlank() -> "Error"
                            selectedMemberId == null -> "All"
                            else -> {
                                members.firstOrNull { it.id == selectedMemberId }?.let {
                                    "${it.name} ${it.lastName}"
                                } ?: "All"
                            }
                        }

                        OutlinedTextField(
                            value = selectedMemberName,
                            onValueChange = {},
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true,
                            readOnly = true,
                            trailingIcon = {
                                IconButton(
                                    onClick = {
                                        if (!membersLoading && members.isNotEmpty()) {
                                            memberMenu = !memberMenu
                                        }
                                    }
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.ArrowDropDown,
                                        contentDescription = "Select assignee"
                                    )
                                }
                            }
                        )

                        DropdownMenu(
                            expanded = memberMenu,
                            onDismissRequest = { memberMenu = false }
                        ) {
                            DropdownMenuItem(
                                text = { Text("All") },
                                onClick = {
                                    selectedMemberId = null
                                    memberMenu = false
                                }
                            )
                            members.forEach { m ->
                                DropdownMenuItem(
                                    text = { Text("${m.name} ${m.lastName}") },
                                    onClick = {
                                        selectedMemberId = m.id
                                        memberMenu = false
                                    }
                                )
                            }
                        }
                    }
                }

                Spacer(Modifier.height(12.dp))

                // -------- ESTADO --------
                Column(Modifier.fillMaxWidth()) {
                    Text(
                        text = "Status:",
                        style = MaterialTheme.typography.bodySmall
                    )
                    Spacer(Modifier.height(4.dp))

                    var statusMenu by remember { mutableStateOf(false) }

                    Box {
                        val statusLabel = when (status) {
                            null -> "All"
                            StatusFilter.TO_DO -> "To Do"
                            StatusFilter.IN_PROGRESS -> "In Progress"
                            StatusFilter.DONE -> "Done"
                        }

                        OutlinedTextField(
                            value = statusLabel,
                            onValueChange = {},
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true,
                            readOnly = true,
                            trailingIcon = {
                                IconButton(onClick = { statusMenu = !statusMenu },) {

                                    Icon(
                                        imageVector = Icons.Default.ArrowDropDown,
                                        contentDescription = "Select status",



                                    )
                                }
                            }
                        )

                        DropdownMenu(
                            expanded = statusMenu,
                            onDismissRequest = { statusMenu = false }
                        ) {
                            DropdownMenuItem(
                                text = { Text("All") },
                                onClick = {
                                    status = null
                                    statusMenu = false
                                }
                            )
                            DropdownMenuItem(
                                text = { Text("To Do") },
                                onClick = {
                                    status = StatusFilter.TO_DO
                                    statusMenu = false
                                }
                            )
                            DropdownMenuItem(
                                text = { Text("In Progress") },
                                onClick = {
                                    status = StatusFilter.IN_PROGRESS
                                    statusMenu = false
                                }
                            )
                            DropdownMenuItem(
                                text = { Text("Done") },
                                onClick = {
                                    status = StatusFilter.DONE
                                    statusMenu = false
                                }
                            )
                        }
                    }
                }

                Spacer(Modifier.height(12.dp))

                // -------- RANGO DE FECHAS --------
                Column(Modifier.fillMaxWidth()) {
                    Text(
                        text = "Date range:",
                        style = MaterialTheme.typography.bodySmall
                    )
                    Spacer(Modifier.height(4.dp))

                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        FilterDateField(
                            label = "Start date",
                            value = dateFrom,
                            onPick = { dateFrom = it },
                            modifier = Modifier.weight(1f)
                        )
                        FilterDateField(
                            label = "End date",
                            value = dateTo,
                            onPick = { dateTo = it },
                            modifier = Modifier.weight(1f)
                        )
                    }
                }

                Spacer(Modifier.height(16.dp))

                // -------- BOTONES --------
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    TextButton(
                        onClick = {
                            priority = null
                            status = null
                            selectedMemberId = null
                            dateFrom = ""
                            dateTo = ""
                        }
                    ) {
                        Text("Clear")
                    }
                    Spacer(Modifier.width(8.dp))
                    Button(
                        onClick = {
                            onApply(
                                TaskFilters(
                                    priority = priority,
                                    status = status,
                                    memberId = selectedMemberId,
                                    dateFrom = dateFrom.ifBlank { null },
                                    dateTo = dateTo.ifBlank { null }
                                )
                            )
                        }
                    ) {
                        Text("Apply")
                    }
                }
            }
        }
    }
}

/* ---------- helpers UI ---------- */

@Composable
private fun FilterRadioChip(
    label: String,
    selected: Boolean,
    onClick: () -> Unit
) {
    Row(
        verticalAlignment = Alignment.CenterVertically
    ) {
        RadioButton(
            selected = selected,
            onClick = onClick,
            colors = RadioButtonDefaults.colors(
                selectedColor = MaterialTheme.colorScheme.primary
            )
        )
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall
        )
    }
}

@Composable
private fun FilterDateField(
    label: String,
    value: String,
    onPick: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val ctx = LocalContext.current
    val cal = Calendar.getInstance()

    fun openPicker() {
        DatePickerDialog(
            ctx,
            { _, y, m, d ->
                val mm = (m + 1).toString().padStart(2, '0')
                val dd = d.toString().padStart(2, '0')
                onPick("$y-$mm-$dd")
            },
            cal.get(Calendar.YEAR),
            cal.get(Calendar.MONTH),
            cal.get(Calendar.DAY_OF_MONTH)
        ).show()
    }

    OutlinedTextField(
        value = value,
        onValueChange = {},
        modifier = modifier,
        singleLine = true,
        readOnly = true,
        label = { Text(label, style = MaterialTheme.typography.labelSmall) },
        trailingIcon = {
            IconButton(onClick = { openPicker() } ,modifier = Modifier.size(32.dp)) {
                Icon(
                    painter = painterResource(R.drawable.ic_calendar),
                    modifier = Modifier.size(18.dp),
                    contentDescription = "Pick date"
                )
            }
        }
          )

        }


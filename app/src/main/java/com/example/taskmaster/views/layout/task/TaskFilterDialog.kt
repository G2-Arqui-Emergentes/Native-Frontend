package com.example.taskmaster.views.layout.task

import android.app.DatePickerDialog
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.example.taskmaster.R
import com.example.taskmaster.viewmodel.ui.users.UsersViewModel
import com.example.taskmaster.views.layout.project.PriorityFilter
import com.example.taskmaster.views.layout.project.StatusFilter
import com.example.taskmaster.views.layout.project.TaskFilters
import java.util.Calendar

private val FilterBackground = Color(0xFFF9FAFB)
private val FilterSurface = Color(0xFFFFFFFF)
private val FilterMutedSurface = Color(0xFFF4F5F7)
private val FilterBorder = Color(0xFFE5E7EB)
private val FilterTextPrimary = Color(0xFF111827)
private val FilterTextSecondary = Color(0xFF6B7280)
private val FilterBrand = Color(0xFFEC1926)

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
            shape = RoundedCornerShape(28.dp),
            colors = CardDefaults.cardColors(containerColor = FilterBackground),
            elevation = CardDefaults.cardElevation(defaultElevation = 10.dp),
            modifier = Modifier.fillMaxWidth(0.92f)
        ) {
            Column(
                modifier = Modifier.padding(horizontal = 20.dp, vertical = 18.dp)
            ) {
                Text(
                    text = "Filters",
                    style = MaterialTheme.typography.headlineSmall.copy(
                        fontWeight = FontWeight.Bold
                    ),
                    color = FilterTextPrimary
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Refine the board results with the filters below.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = FilterTextSecondary
                )

                Spacer(modifier = Modifier.height(16.dp))

                FilterSection(title = "Priority") {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        FilterRadioChip(
                            label = "High",
                            selected = priority == PriorityFilter.HIGH,
                            onClick = {
                                priority =
                                    if (priority == PriorityFilter.HIGH) null else PriorityFilter.HIGH
                            }
                        )
                        FilterRadioChip(
                            label = "Medium",
                            selected = priority == PriorityFilter.MEDIUM,
                            onClick = {
                                priority =
                                    if (priority == PriorityFilter.MEDIUM) null else PriorityFilter.MEDIUM
                            }
                        )
                        FilterRadioChip(
                            label = "Low",
                            selected = priority == PriorityFilter.LOW,
                            onClick = {
                                priority =
                                    if (priority == PriorityFilter.LOW) null else PriorityFilter.LOW
                            }
                        )
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                FilterSection(title = "Assignee") {
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
                            shape = RoundedCornerShape(18.dp),
                            colors = filterFieldColors(),
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
                                        contentDescription = "Select assignee",
                                        tint = FilterTextSecondary
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
                            members.forEach { member ->
                                DropdownMenuItem(
                                    text = { Text("${member.name} ${member.lastName}") },
                                    onClick = {
                                        selectedMemberId = member.id
                                        memberMenu = false
                                    }
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                FilterSection(title = "Status") {
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
                            shape = RoundedCornerShape(18.dp),
                            colors = filterFieldColors(),
                            trailingIcon = {
                                IconButton(onClick = { statusMenu = !statusMenu }) {
                                    Icon(
                                        imageVector = Icons.Default.ArrowDropDown,
                                        contentDescription = "Select status",
                                        tint = FilterTextSecondary
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

                Spacer(modifier = Modifier.height(12.dp))

                FilterSection(title = "Date range") {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
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

                Spacer(modifier = Modifier.height(16.dp))

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
                        },
                        colors = ButtonDefaults.textButtonColors(
                            contentColor = FilterTextSecondary
                        )
                    ) {
                        Text("Clear")
                    }
                    Spacer(modifier = Modifier.size(8.dp))
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
                        },
                        shape = RoundedCornerShape(16.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = FilterBrand,
                            contentColor = Color.White
                        )
                    ) {
                        Text("Apply")
                    }
                }
            }
        }
    }
}

@Composable
private fun FilterSection(
    title: String,
    content: @Composable ColumnScope.() -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(22.dp),
        colors = CardDefaults.cardColors(containerColor = FilterSurface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(horizontal = 14.dp, vertical = 14.dp)
        ) {
            Text(
                text = title.uppercase(),
                style = MaterialTheme.typography.labelSmall.copy(
                    fontWeight = FontWeight.SemiBold
                ),
                color = FilterTextSecondary
            )
            Spacer(modifier = Modifier.height(10.dp))
            content()
        }
    }
}

@Composable
private fun FilterRadioChip(
    label: String,
    selected: Boolean,
    onClick: () -> Unit
) {
    FilterChip(
        selected = selected,
        onClick = onClick,
        label = {
            Text(
                text = label,
                style = MaterialTheme.typography.labelMedium.copy(
                    fontWeight = FontWeight.SemiBold
                )
            )
        },
        border = BorderStroke(
            1.dp,
            if (selected) FilterBrand.copy(alpha = 0.22f) else FilterBorder
        ),
        colors = FilterChipDefaults.filterChipColors(
            selectedContainerColor = FilterBrand.copy(alpha = 0.12f),
            selectedLabelColor = FilterBrand,
            containerColor = FilterSurface,
            labelColor = FilterTextSecondary
        )
    )
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
        shape = RoundedCornerShape(18.dp),
        colors = filterFieldColors(),
        label = {
            Text(
                text = label,
                style = MaterialTheme.typography.labelSmall,
                color = FilterTextSecondary
            )
        },
        trailingIcon = {
            IconButton(onClick = { openPicker() }, modifier = Modifier.size(32.dp)) {
                Icon(
                    painter = painterResource(R.drawable.ic_calendar),
                    modifier = Modifier.size(18.dp),
                    contentDescription = "Pick date",
                    tint = FilterTextSecondary
                )
            }
        }
    )
}

@Composable
private fun filterFieldColors() = OutlinedTextFieldDefaults.colors(
    focusedContainerColor = FilterMutedSurface,
    unfocusedContainerColor = FilterMutedSurface,
    disabledContainerColor = FilterMutedSurface,
    focusedBorderColor = FilterBrand.copy(alpha = 0.28f),
    unfocusedBorderColor = FilterBorder,
    disabledBorderColor = FilterBorder,
    focusedTextColor = FilterTextPrimary,
    unfocusedTextColor = FilterTextPrimary,
    disabledTextColor = FilterTextSecondary,
    cursorColor = FilterBrand
)

package com.example.taskmaster.views.layout.task

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.unit.dp
import com.example.taskmaster.viewmodel.data.tasks.TaskDto
import com.example.taskmaster.viewmodel.data.users.UserDto

@Composable
fun TaskList(
    tasks: List<TaskDto>,
    members: List<UserDto>,
    onTaskClick: (TaskDto) -> Unit = {}
) {
    val membersById = remember(members) { members.associateBy { it.id } }

    LazyColumn(verticalArrangement = Arrangement.spacedBy(10.dp)) {
        items(items = tasks, key = { it.taskId }) { task ->
            val assigneeId = task.assignedUserIds.firstOrNull()
            val member = assigneeId?.let { membersById[it] }

            CardTask(
                task = task,
                assigneeAvatarUrl = member?.imageUrl,
                assigneeName = member?.let { "${it.name} ${it.lastName}" },
                onClick = { onTaskClick(task) }
            )
        }
    }
}
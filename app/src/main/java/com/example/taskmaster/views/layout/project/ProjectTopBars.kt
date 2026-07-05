package com.example.taskmaster.views.layout.project

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.taskmaster.views.layout.common.AppTopHeader
import com.example.taskmaster.viewmodel.sharedPreferences.Prefs
import com.example.taskmaster.viewmodel.ui.users.UsersViewModel

enum class ProjectSection { TASKS, STATS, SETTINGS }

private val ProjectHeaderSurface = Color(0xFFF4F5F7)
private val ProjectHeaderBorder = Color(0xFFE5E7EB)
private val ProjectHeaderText = Color(0xFF111827)
private val ProjectHeaderMuted = Color(0xFF6B7280)
private val ProjectHeaderBrand = Color(0xFFEC1926)

@Composable
fun ProjectTopBar(
    title: String,
    onBack: () -> Unit,
    onNotificationsClick: () -> Unit = {},
    onProfileClick: () -> Unit = {},
    userVm: UsersViewModel = remember { UsersViewModel() }
) {
    val context = LocalContext.current
    val user by userVm.user.collectAsState()

    LaunchedEffect(Unit) {
        Prefs.loadEmail(context)?.let(userVm::loadByEmail)
    }

    Column {
        AppTopHeader(
            user = user,
            onNotificationsClick = onNotificationsClick,
            onProfileClick = onProfileClick,
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp)
        )

        Surface(
            color = MaterialTheme.colorScheme.background,
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
                    .clip(RoundedCornerShape(22.dp))
                    .background(ProjectHeaderSurface)
                    .border(1.dp, ProjectHeaderBorder, RoundedCornerShape(22.dp))
                    .padding(horizontal = 10.dp, vertical = 10.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    onClick = onBack,
                    modifier = Modifier
                        .size(40.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(Color.White)
                ) {
                    Icon(
                        Icons.Rounded.ArrowBack,
                        contentDescription = "Back",
                        tint = ProjectHeaderText
                    )
                }

                Spacer(modifier = Modifier.width(10.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "Project",
                        style = MaterialTheme.typography.labelMedium.copy(
                            fontWeight = FontWeight.SemiBold
                        ),
                        color = ProjectHeaderBrand
                    )
                    Text(
                        text = title.ifBlank { "Untitled project" },
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontWeight = FontWeight.Bold
                        ),
                        color = ProjectHeaderText
                    )
                }
            }
        }
    }
}

@Composable
fun ProjectMiniTabs(
    selected: ProjectSection,
    onSelect: (ProjectSection) -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 10.dp)
            .clip(RoundedCornerShape(20.dp))
            .background(ProjectHeaderSurface)
            .border(1.dp, ProjectHeaderBorder, RoundedCornerShape(20.dp))
            .padding(horizontal = 8.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        TabItem("Tasks", selected == ProjectSection.TASKS) { onSelect(ProjectSection.TASKS) }
        TabItem("Statistics", selected == ProjectSection.STATS) { onSelect(ProjectSection.STATS) }
        TabItem("Settings", selected == ProjectSection.SETTINGS) { onSelect(ProjectSection.SETTINGS) }
    }
}

@Composable
private fun TabItem(
    label: String,
    active: Boolean,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(14.dp))
            .background(if (active) Color.White else Color.Transparent)
            .border(
                width = if (active) 1.dp else 0.dp,
                color = if (active) ProjectHeaderBorder else Color.Transparent,
                shape = RoundedCornerShape(14.dp)
            )
            .clickable(onClick = onClick)
            .padding(horizontal = 14.dp, vertical = 10.dp)
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.titleSmall.copy(
                fontWeight = if (active) FontWeight.Bold else FontWeight.Medium
            ),
            color = if (active) ProjectHeaderText else ProjectHeaderMuted
        )
    }
}

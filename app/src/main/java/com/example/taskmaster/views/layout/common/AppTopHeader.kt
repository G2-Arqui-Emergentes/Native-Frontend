package com.example.taskmaster.views.layout.common

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.example.taskmaster.R
import com.example.taskmaster.viewmodel.data.users.UserDto

private val HeaderLogoRed = Color(0xFFEC1926)
private val HeaderSurface = Color(0xFFF4F5F7)
private val HeaderBorder = Color(0xFFE5E7EB)
private val HeaderTextPrimary = Color(0xFF111827)
private val HeaderTextSecondary = Color(0xFF6B7280)

@Composable
fun AppTopHeader(
    user: UserDto?,
    onNotificationsClick: () -> Unit,
    onProfileClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Image(
            painter = painterResource(R.drawable.taskmaster_logoblanco),
            contentDescription = "TaskMaster logo",
            modifier = Modifier.size(52.dp),
            colorFilter = ColorFilter.tint(HeaderLogoRed)
        )

        Spacer(modifier = Modifier.width(12.dp))

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = "TaskMaster",
                style = MaterialTheme.typography.titleLarge.copy(
                    fontWeight = FontWeight.Bold,
                    color = HeaderTextPrimary
                )
            )
            Text(
                text = resolveRoleLabel(user),
                style = MaterialTheme.typography.bodySmall,
                color = HeaderTextSecondary
            )
        }

        HeaderActionButton(
            iconRes = R.drawable.ic_notifications,
            contentDescription = "Notifications",
            onClick = onNotificationsClick
        )

        Spacer(modifier = Modifier.width(10.dp))

        Box(
            modifier = Modifier
                .size(44.dp)
                .clip(RoundedCornerShape(14.dp))
                .background(Color.White)
                .border(1.dp, HeaderBorder, RoundedCornerShape(14.dp))
                .shadow(6.dp, RoundedCornerShape(14.dp), ambientColor = HeaderLogoRed.copy(alpha = 0.08f))
                .clickable(onClick = onProfileClick),
            contentAlignment = Alignment.Center
        ) {
            AsyncImage(
                model = user?.imageUrl ?: R.drawable.ic_profile_placeholder,
                contentDescription = "Profile",
                modifier = Modifier
                    .size(40.dp)
                    .clip(RoundedCornerShape(12.dp)),
                contentScale = ContentScale.Crop
            )
        }
    }
}

@Composable
private fun HeaderActionButton(
    iconRes: Int,
    contentDescription: String,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .size(44.dp)
            .clip(RoundedCornerShape(14.dp))
            .background(HeaderSurface)
            .border(1.dp, HeaderBorder, RoundedCornerShape(14.dp))
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        Image(
            painter = painterResource(iconRes),
            contentDescription = contentDescription,
            modifier = Modifier.size(20.dp)
        )
    }
}

private fun resolveRoleLabel(user: UserDto?): String {
    return when {
        user == null -> ""
        user.roles.any { it.equals("ROLE_LEADER", ignoreCase = true) } -> "Leader"
        user.roles.any { it.equals("ROLE_MEMBER", ignoreCase = true) } -> "Member"
        else -> user.roles.firstOrNull()
            ?.removePrefix("ROLE_")
            ?.replaceFirstChar { it.uppercase() }
            .orEmpty()
    }
}

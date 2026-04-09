// views/layout/project/MemberCard.kt
package com.example.taskmaster.views.layout.project

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.example.taskmaster.R
import com.example.taskmaster.viewmodel.data.users.UserDto

@Composable
fun MemberCard(
    member: UserDto,
    onClick: () -> Unit = {},
    onRemoveClick: () -> Unit = {}
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        onClick = onClick
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Avatar
            AsyncImage(
                model = member.imageUrl ?: R.drawable.ic_profile_placeholder,
                contentDescription = null,
                modifier = Modifier
                    .size(56.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.secondaryContainer)
            )

            Spacer(Modifier.width(12.dp))

            // Nombre + email
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = "${member.name} ${member.lastName}",
                    style = MaterialTheme.typography.titleMedium
                )
                Text(
                    text = member.email,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                )
            }

            IconButton(onClick = onRemoveClick) {
                Icon(
                    imageVector = Icons.Default.Delete,
                    contentDescription = "Eliminar miembro"
                )
            }
        }
    }
}

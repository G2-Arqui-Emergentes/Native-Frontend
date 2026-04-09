package com.example.taskmaster.views.layout.project

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.example.taskmaster.R

@Composable
fun EmptyProjects(onAddClick: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 12.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Image(
            painter = painterResource(R.drawable.ic_empty_folder),
            contentDescription = null,
            modifier = Modifier
                .size(140.dp)
                .padding(top = 24.dp, bottom = 12.dp)
        )

        Text(
            text = "¡No cuentas con ningún proyecto, crea uno!",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onBackground,
            modifier = Modifier.padding(vertical = 8.dp)
        )

        Button(
            onClick = onAddClick,
            shape = RoundedCornerShape(10.dp)
        ) {
            Text(
                "Crea uno",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onPrimary
            )
        }
    }
}

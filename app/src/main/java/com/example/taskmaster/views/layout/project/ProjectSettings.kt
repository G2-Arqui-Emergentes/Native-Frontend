package com.example.taskmaster.views.layout.project

import android.app.DatePickerDialog
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import coil.compose.AsyncImage
import com.example.taskmaster.R
import com.example.taskmaster.viewmodel.model.ProjectsViewModel
import java.util.Calendar

@Composable
fun ProjectSettings(
    nav: NavHostController,
    projectId: Long,
    vm: ProjectsViewModel = remember { ProjectsViewModel() }
) {
    val isLoading by vm.isLoading.collectAsState()
    val error by vm.error.collectAsState()
    val project by vm.current.collectAsState()

    LaunchedEffect(projectId) { vm.loadById(projectId) }

    var name by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var budgetText by remember { mutableStateOf("") }
    var imageUrl by remember { mutableStateOf("") }
    var status by remember { mutableStateOf("PLANNED") }
    var startDate by remember { mutableStateOf("") }
    var endDate by remember { mutableStateOf("") }

    // diálogo para editar URL de imagen (igual que en Perfil)
    var showImageDialog by remember { mutableStateOf(false) }
    var tempUrl by remember { mutableStateOf("") }

    // diálogo para confirmar borrado
    var showDeleteDialog by remember { mutableStateOf(false) }

    LaunchedEffect(project) {
        project?.let {
            name        = it.name
            description = it.description
            budgetText  = it.budget.toString()
            imageUrl    = it.imageUrl.orEmpty()
            status      = it.status
            startDate   = it.startDate.take(10)
            endDate     = it.endDate.take(10)
        }
    }

    if (showImageDialog) {
        AlertDialog(
            onDismissRequest = { showImageDialog = false },
            confirmButton = {
                TextButton(onClick = {
                    imageUrl = tempUrl.trim()
                    showImageDialog = false
                }) { Text("Usar") }
            },
            dismissButton = {
                TextButton(onClick = { showImageDialog = false }) { Text("Cancelar") }
            },
            title = { Text("Cambiar imagen") },
            text = {
                Column {
                    Text("Pega el enlace (PNG/JPG):")
                    OutlinedTextField(
                        value = tempUrl,
                        onValueChange = { tempUrl = it },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
        )
    }

    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            confirmButton = {
                TextButton(
                    onClick = {
                        showDeleteDialog = false
                        vm.delete(projectId)
                        // después de borrar, vuelve a la lista de proyectos
                        nav.navigate("projects") {
                            popUpTo("projects") { inclusive = true }
                            launchSingleTop = true
                        }
                    }
                ) { Text("Borrar", color = MaterialTheme.colorScheme.error) }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteDialog = false }) {
                    Text("Cancelar")
                }
            },
            title = { Text("Borrar proyecto") },
            text = { Text("¿Estás seguro que deseas borrar este proyecto? Esta acción no se puede deshacer.") }
        )
    }

    Scaffold(
        topBar = {
            Column {
                ProjectTopBar(
                    title = project?.name ?: "",
                    onBack = { nav.popBackStack() }
                )
                ProjectMiniTabs(
                    selected = ProjectSection.SETTINGS,
                    onSelect = {
                        when (it) {
                            ProjectSection.TASKS    -> nav.navigate("projectTasks/$projectId") { launchSingleTop = true }
                            ProjectSection.STATS    -> nav.navigate("projectStats/$projectId") { launchSingleTop = true }
                            ProjectSection.SETTINGS -> Unit
                        }
                    }
                )
            }
        }
    ) { innerPadding ->

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 12.dp, vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // --------- Avatar + key ----------
            item {
                val imageModel = when {
                    imageUrl.isNotBlank() -> imageUrl
                    !project?.imageUrl.isNullOrBlank() -> project!!.imageUrl
                    else -> R.drawable.ic_profile_placeholder
                }

                AsyncImage(
                    model = imageModel,
                    contentDescription = null,
                    modifier = Modifier
                        .size(72.dp)
                        .clip(CircleShape)
                        .clickable {
                            tempUrl = imageUrl.ifBlank { project?.imageUrl.orEmpty() }
                            showImageDialog = true
                        }
                )
                Spacer(Modifier.height(4.dp))
                Text("#${project?.key.orEmpty()}", style = MaterialTheme.typography.labelLarge)
            }

            // --------- Card de datos ----------
            item {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color.White, RoundedCornerShape(16.dp))
                        .border(
                            width = 1.dp,
                            color = MaterialTheme.colorScheme.outline,
                            shape = RoundedCornerShape(16.dp)
                        )
                        .padding(10.dp)
                ) {
                    Label("Nombre del proyecto")
                    TextField(
                        value = name,
                        onValueChange = { name = it },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        shape = RoundedCornerShape(0.dp),
                        colors = fieldColors()
                    )
                    Divider(color = MaterialTheme.colorScheme.outline)

                    Label("Descripción")
                    TextField(
                        value = description,
                        onValueChange = { description = it },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(0.dp),
                        colors = fieldColors()
                    )
                    Divider(color = MaterialTheme.colorScheme.outline)

                    Label("Presupuesto")
                    TextField(
                        value = budgetText,
                        onValueChange = {
                            if (it.matches(Regex("""\d*\.?\d*"""))) budgetText = it
                        },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        shape = RoundedCornerShape(0.dp),
                        prefix = { Text("S/ ") },
                        colors = fieldColors()
                    )
                    Divider(color = MaterialTheme.colorScheme.outline)

                    Label("Estado")
                    var expanded by remember { mutableStateOf(false) }
                    Box {
                        TextField(
                            value = status,
                            onValueChange = {},
                            readOnly = true,
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { expanded = true },
                            singleLine = true,
                            shape = RoundedCornerShape(0.dp),
                            colors = fieldColors()
                        )
                        DropdownMenu(
                            expanded = expanded,
                            onDismissRequest = { expanded = false }
                        ) {
                            listOf("PLANNED", "IN_PROGRESS", "COMPLETED").forEach { s ->
                                DropdownMenuItem(
                                    onClick = { status = s; expanded = false },
                                    text = { Text(s) }
                                )
                            }
                        }
                    }
                    Divider(color = MaterialTheme.colorScheme.outline)

                    Row(
                        horizontalArrangement = Arrangement.spacedBy(10.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(Modifier.weight(1f)) {
                            Label("Fecha inicio")
                            DateField(
                                value = startDate,
                                onPick = { startDate = it },
                                enabled = false
                            )
                        }
                        Column(Modifier.weight(1f)) {
                            Label("Fecha fin")
                            DateField(
                                value = endDate,
                                onPick = { endDate = it }
                            )
                        }
                    }
                }
            }

            // --------- Botones inferiores ----------
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Button(
                        onClick = {
                            // 👉 ir a la lista de miembros del proyecto
                            nav.navigate("memberList/$projectId")
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.secondaryContainer,
                            contentColor = MaterialTheme.colorScheme.onSecondaryContainer
                        ),
                        enabled = !isLoading
                    ) { Text("Lista de miembros") }

                    Button(
                        onClick = {
                            val budgetDouble = budgetText.toDoubleOrNull()
                                ?: project?.budget ?: 0.0
                            vm.update(
                                id = projectId,
                                name = name,
                                description = description,
                                imageUrl = imageUrl,
                                budget = budgetDouble,
                                endDate = endDate,
                                status = status
                            )
                        },
                        enabled = !isLoading
                    ) { Text("Guardar Cambios") }
                }
            }

            // --------- Borrar proyecto ----------
            item {
                TextButton(
                    onClick = { showDeleteDialog = true },
                    enabled = !isLoading
                ) {
                    Text("Borrar proyecto", color = MaterialTheme.colorScheme.error)
                }
            }

            item { Spacer(Modifier.height(84.dp)) }

            if (!error.isNullOrBlank()) {
                item { Text(error ?: "", color = MaterialTheme.colorScheme.error) }
            }
        }
    }
}

@Composable
fun Label(text: String) {
    Text(
        text = text,
        style = MaterialTheme.typography.labelSmall,
        color = MaterialTheme.colorScheme.onSecondaryContainer,
        modifier = Modifier.padding(start = 6.dp, top = 6.dp, bottom = 4.dp)
    )
}

@Composable
fun fieldColors() = TextFieldDefaults.colors(
    focusedContainerColor = Color.Transparent,
    unfocusedContainerColor = Color.Transparent,
    disabledContainerColor = Color.Transparent,

    focusedTextColor = MaterialTheme.colorScheme.onSecondaryContainer,
    unfocusedTextColor = MaterialTheme.colorScheme.onSecondaryContainer,
    disabledTextColor = MaterialTheme.colorScheme.onSecondaryContainer.copy(alpha = 0.6f),

    focusedIndicatorColor = Color.Transparent,
    unfocusedIndicatorColor = Color.Transparent,
    disabledIndicatorColor = Color.Transparent,
    errorIndicatorColor = MaterialTheme.colorScheme.error,

    cursorColor = MaterialTheme.colorScheme.primary,
    focusedLabelColor = MaterialTheme.colorScheme.onSecondaryContainer,
    unfocusedLabelColor = MaterialTheme.colorScheme.onSecondaryContainer
)

@Composable
fun DateField(
    value: String,
    onPick: (String) -> Unit,
    enabled: Boolean = true
) {
    val ctx = LocalContext.current
    val cal = Calendar.getInstance()

    fun openDialog() {
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

    TextField(
        value = value,
        onValueChange = { },
        readOnly = true,
        enabled = enabled,
        trailingIcon = {
            if (enabled) {
                IconButton(onClick = { openDialog() }) {
                    Icon(Icons.Default.DateRange, contentDescription = null)
                }
            }
        },
        modifier = Modifier.fillMaxWidth(),
        singleLine = true,
        shape = RoundedCornerShape(0.dp),
        colors = fieldColors()
    )
}

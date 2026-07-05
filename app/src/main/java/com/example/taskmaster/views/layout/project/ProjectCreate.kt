package com.example.taskmaster.views.layout.project

import android.app.DatePickerDialog
import android.content.Context
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.example.taskmaster.R
import com.example.taskmaster.views.layout.common.AppTopHeader
import com.example.taskmaster.viewmodel.model.ProjectsViewModel
import com.example.taskmaster.viewmodel.sharedPreferences.Prefs
import com.example.taskmaster.viewmodel.ui.users.UsersViewModel
import java.util.Calendar

@Composable
fun ProjectCreate(
    context: Context,
    nav: NavHostController,
    vm: ProjectsViewModel = remember { ProjectsViewModel() },
    userVm: UsersViewModel = remember { UsersViewModel() }
) {
    val isLoading by vm.isLoading.collectAsState()
    val error by vm.error.collectAsState()
    val created by vm.created.collectAsState()
    val user by userVm.user.collectAsState()

    // state UI
    var name by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var imageUrl by remember { mutableStateOf("") }
    var budgetText by remember { mutableStateOf("") }
    var startDate by remember { mutableStateOf("") }
    var endDate by remember { mutableStateOf("") }

    LaunchedEffect(Unit) {
        Prefs.loadEmail(context)?.let(userVm::loadByEmail)
    }

    LaunchedEffect(created) {
        if (created != null) {
            vm.clearCreated()
            nav.popBackStack()
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        AppTopHeader(
            user = user,
            onNotificationsClick = { nav.navigate("notification") },
            onProfileClick = { nav.navigate("profile") },
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp)
        )

        // Top bar mockup
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(MaterialTheme.colorScheme.secondaryContainer)
                .padding(horizontal = 12.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = { nav.popBackStack() }) {
                Icon(
                    painter = painterResource(R.drawable.ic_close),
                    contentDescription = "Cerrar",
                    tint = MaterialTheme.colorScheme.onSecondaryContainer
                )
            }
            Text(
                text = "Crear proyecto",
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.onSecondaryContainer,
                textAlign = TextAlign.Center,
                modifier = Modifier.weight(1f)
            )
            Spacer(Modifier.width(48.dp))
        }

        Spacer(Modifier.height(8.dp))

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
        ) {
            LabeledField(label = "Nombre del proyecto") {
                PinkField(
                    value = name,
                    onValueChange = { name = it },
                    placeholder = null
                )
            }

            LabeledField(label = "Descripción") {
                PinkField(
                    value = description,
                    onValueChange = { description = it },
                    singleLine = false
                )
            }

            LabeledField(label = "Imagen (URL)") {
                PinkField(
                    value = imageUrl,
                    onValueChange = { imageUrl = it },
                    placeholder = "https://..."
                )
            }

            LabeledField(label = "Presupuesto") {
                PinkField(
                    value = budgetText,
                    onValueChange = {
                        if (it.matches(Regex("""\d*\.?\d*"""))) budgetText = it
                    },
                    prefix = { Text("S/") }
                )
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                LabeledField(
                    label = "Fecha inicio",
                    modifier = Modifier.weight(1f)
                ) {
                    DateField(
                        value = startDate,
                        onPick = { startDate = it }
                    )
                }
                LabeledField(
                    label = "Fecha fin",
                    modifier = Modifier.weight(1f)
                ) {
                    DateField(
                        value = endDate,
                        onPick = { endDate = it }
                    )
                }
            }

            Spacer(Modifier.height(24.dp))

            Button(
                onClick = {
                    val budget = budgetText.toDoubleOrNull()
                    if (name.isNotBlank() && endDate.isNotBlank()) {
                        vm.create(
                            name = name,
                            description = description,
                            imageUrl = imageUrl,
                            budget = budget,
                            endDate = endDate  // yyyy-MM-dd
                        )
                    }
                },
                shape = RoundedCornerShape(12.dp),
                enabled = !isLoading,
                modifier = Modifier
                    .align(Alignment.CenterHorizontally)
                    .widthIn(min = 160.dp)
            ) {
                if (isLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(18.dp),
                        strokeWidth = 2.dp,
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                    Spacer(Modifier.width(8.dp))
                }
                Text("Crear", style = MaterialTheme.typography.titleMedium)
            }

            if (!error.isNullOrBlank()) {
                Spacer(Modifier.height(12.dp))
                Text(error ?: "", color = MaterialTheme.colorScheme.error)
            }
        }
    }
}


@Composable
private fun LabeledField(
    label: String,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    Column(modifier = modifier.padding(top = 8.dp, bottom = 10.dp)) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onBackground,
            modifier = Modifier.padding(start = 8.dp, bottom = 4.dp)
        )
        content()
    }
}

@Composable
private fun PinkField(
    value: String,
    onValueChange: (String) -> Unit,
    singleLine: Boolean = true,
    placeholder: String? = null,
    prefix: @Composable (() -> Unit)? = null
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        modifier = Modifier
            .fillMaxWidth(),
        singleLine = singleLine,
        shape = RoundedCornerShape(10.dp),
        placeholder = {
            if (placeholder != null) {
                Text(
                    placeholder,
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                )
            }
        },
        prefix = prefix,

    )
}

@Composable
private fun DateField(
    value: String,
    onPick: (String) -> Unit
) {
    val cal = java.util.Calendar.getInstance()
    val ctx = LocalContext.current

    val openPicker = {
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
        onValueChange = { /* readOnly */ },
        modifier = Modifier.fillMaxWidth(),
        singleLine = true,
        readOnly = true,
        trailingIcon = {
            Icon(
                painter = painterResource(R.drawable.ic_calendar),
                contentDescription = "Elegir fecha",
                modifier = Modifier
                    .size(22.dp)
                    .padding(end = 2.dp)
                    .noRippleClickable { openPicker() }, // ver helper abajo
                tint = MaterialTheme.colorScheme.onBackground
            )
        },
        shape = RoundedCornerShape(10.dp),

    )
}


@Composable
private fun Modifier.noRippleClickable(onClick: () -> Unit): Modifier =
    composed{
        val interaction = remember { MutableInteractionSource() }
        this.clickable(
            interactionSource = interaction,
            indication = null,
            onClick = onClick
        )
    }

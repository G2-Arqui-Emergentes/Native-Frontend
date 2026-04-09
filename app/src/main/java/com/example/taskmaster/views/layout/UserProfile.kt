package com.example.taskmaster.views.layout

import android.content.Context
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import coil.compose.AsyncImage
import com.example.taskmaster.R
import com.example.taskmaster.viewmodel.data.net.TokenStore
import com.example.taskmaster.viewmodel.sharedPreferences.Prefs
import com.example.taskmaster.viewmodel.ui.users.UsersViewModel

@Composable
fun UserProfile(
    context: Context,
    nav: NavHostController,
    vm: UsersViewModel = remember { UsersViewModel() }
) {
    val isLoading by vm.isLoading.collectAsState()
    val error by vm.error.collectAsState()
    val user by vm.user.collectAsState()
    var password by remember { mutableStateOf("") }
    var showPass by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        Prefs.loadEmail(context)?.let { vm.loadByEmail(it) }
        password = Prefs.loadPassword(context) ?: ""
    }

    // estado editable
    var username by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var salaryText by remember { mutableStateOf("") }
    var imageUrl by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(user) {
        user?.let {
            username = listOfNotNull(it.name, it.lastName).joinToString(" ")
            email = it.email
            salaryText = it.salary?.toString() ?: ""
            imageUrl = it.imageUrl
        }
    }

    var showImageDialog by remember { mutableStateOf(false) }
    var tempUrl by remember { mutableStateOf(imageUrl.orEmpty()) }

    if (showImageDialog) {
        AlertDialog(
            onDismissRequest = { showImageDialog = false },
            confirmButton = {
                TextButton(onClick = {
                    imageUrl = tempUrl.ifBlank { null }
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

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Perfil",
            style = MaterialTheme.typography.displayMedium,
            color = MaterialTheme.colorScheme.onBackground,
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 8.dp)
        )

        Spacer(Modifier.height(4.dp))

        Box(
            Modifier.fillMaxWidth(),
            contentAlignment = Alignment.Center
        ) {
            AsyncImage(
                model = imageUrl ?: R.drawable.ic_profile_placeholder,
                contentDescription = "Avatar",
                modifier = Modifier
                    .size(140.dp)
                    .clip(CircleShape)
                    .background(Color(0xFFEAEAEA))
                    .clickable {
                        tempUrl = imageUrl.orEmpty()
                        showImageDialog = true
                    },
                contentScale = ContentScale.Crop
            )
        }

        Spacer(Modifier.height(20.dp))

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color.White, RoundedCornerShape(16.dp))
                .border(
                    width = 1.dp,
                    color = MaterialTheme.colorScheme.outline, // Brownish900
                    shape = RoundedCornerShape(16.dp)
                )
                .padding(horizontal = 16.dp, vertical = 12.dp)
        ) {
            Label("UserName")
            TextField(
                value = username,
                onValueChange = { username = it },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                shape = RoundedCornerShape(0.dp),
                colors = fieldColors()
            )

            Divider(color = MaterialTheme.colorScheme.outline, thickness = 1.dp)

            Label("Email")
            TextField(
                value = email,
                onValueChange = {},
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                enabled = false,
                shape = RoundedCornerShape(0.dp),
                colors = fieldColors(disabled = true)
            )

            Divider(color = MaterialTheme.colorScheme.outline, thickness = 1.dp)

            Label("Contraseña")
            TextField(
                value = password,
                onValueChange = {},
                readOnly = true,
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                visualTransformation = if (showPass) {
                    VisualTransformation.None
                } else {
                    PasswordVisualTransformation()
                },
                trailingIcon = {
                    IconButton(onClick = { showPass = !showPass }) {
                        Image(
                            modifier = Modifier.size(25.dp),
                            painter = painterResource(
                                if (showPass) R.drawable.visibilityoff else R.drawable.visibility
                            ),
                            contentDescription = null
                        )
                    }
                },
                shape = RoundedCornerShape(0.dp),
                colors = fieldColors()
            )

            Divider(color = MaterialTheme.colorScheme.outline, thickness = 1.dp)

            Label("Pago por Hora")
            TextField(
                value = salaryText,
                onValueChange = { salaryText = it },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                shape = RoundedCornerShape(0.dp),
                colors = fieldColors()
            )
        }
        // *********** FIN COLUMN DEL FORMULARIO ***********

        Spacer(Modifier.height(24.dp))

        Row(
            Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Button(
                onClick = { /* aún no activo */ },
                enabled = true, // para que se vea mejor en el diseño
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.secondaryContainer, // Blush100
                    contentColor = MaterialTheme.colorScheme.onSecondaryContainer    // Brownish900
                )
            ) { Text("Unirte a un proyecto") }

            Button(
                onClick = {
                    val salary = salaryText.toDoubleOrNull()
                    vm.updateProfile(username = username, imageUrl = imageUrl, salary = salary)
                },
                enabled = !isLoading,
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary,  // A62424
                    contentColor = MaterialTheme.colorScheme.onPrimary   // White
                )
            ) {
                if (isLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(18.dp),
                        strokeWidth = 2.dp,
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                    Spacer(Modifier.width(8.dp))
                }
                Text("Guardar Cambios")
            }
        }

        Spacer(Modifier.height(20.dp))

        TextButton(
            onClick = {
                Prefs.clearToken(context)
                TokenStore.token = null
                nav.navigate("login") {
                    popUpTo("projects") { inclusive = true }
                    launchSingleTop = true
                }
            }
        ) {
            Text("Cerrar sesión", color = MaterialTheme.colorScheme.error)
        }

        if (!error.isNullOrBlank()) {
            Spacer(Modifier.height(8.dp))
            Text(error ?: "", color = MaterialTheme.colorScheme.error)
        }
    }
}

@Composable
private fun Label(text: String) {
    Text(
        text = text,
        style = MaterialTheme.typography.labelSmall,
        color = MaterialTheme.colorScheme.onSecondaryContainer,
        modifier = Modifier.padding(start = 4.dp, top = 6.dp, bottom = 4.dp)
    )
}

@Composable
private fun fieldColors(disabled: Boolean = false): TextFieldColors {
    val baseText = MaterialTheme.colorScheme.onSecondaryContainer // marrón
    val disabledText = if (disabled) baseText.copy(alpha = 0.6f) else baseText

    return TextFieldDefaults.colors(
        // Fondo transparente para que se vea el blanco del card
        focusedContainerColor = Color.Transparent,
        unfocusedContainerColor = Color.Transparent,
        disabledContainerColor = Color.Transparent,

        // Texto marrón
        focusedTextColor = baseText,
        unfocusedTextColor = baseText,
        disabledTextColor = disabledText,

        // Indicador/underline invisible (ya usamos Divider)
        focusedIndicatorColor = Color.Transparent,
        unfocusedIndicatorColor = Color.Transparent,
        disabledIndicatorColor = Color.Transparent,
        errorIndicatorColor = MaterialTheme.colorScheme.error,

        // Cursor y selección
        cursorColor = MaterialTheme.colorScheme.primary,

        // Labels (por si algún campo usa label interna)
        focusedLabelColor = baseText,
        unfocusedLabelColor = baseText
    )
}

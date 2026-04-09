// com/example/taskmaster/views/shared/Register.kt
package com.example.taskmaster.views.shared

import android.content.Context
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Email
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.example.taskmaster.R
import com.example.taskmaster.viewmodel.model.AuthViewModel
import com.example.taskmaster.viewmodel.sharedPreferences.Prefs

@Composable
fun Register(context: Context, nav: NavHostController) {

    val vm = remember { AuthViewModel() }
    val isLoading by vm.isLoading.collectAsState()
    val error by vm.error.collectAsState()
    val signedUpUser by vm.signedUpUser.collectAsState()


    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var username by remember { mutableStateOf("") }
    var passVisible by remember { mutableStateOf(false) }


    LaunchedEffect(signedUpUser) {
        if (signedUpUser != null) {
            Prefs.saveEmailAndPassword(context, email, password)
            nav.popBackStack()
        }
    }

    Surface(Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
        Column(Modifier.fillMaxSize()) {
            Box(contentAlignment = Alignment.TopCenter) {
                Image(
                    modifier = Modifier
                        .fillMaxWidth()
                        .fillMaxHeight(0.50f),
                    painter = painterResource(id = R.drawable.shape),
                    contentDescription = null,
                    contentScale = ContentScale.FillBounds
                )

                Column(
                    modifier = Modifier
                        .padding(top = 35.dp)
                        .fillMaxSize(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Icon(
                        modifier = Modifier
                            .size(140.dp)
                            .padding(top = 45.dp),
                        painter = painterResource(id = R.drawable.taskmaster_logoblanco),
                        contentDescription = null
                    )

                    Spacer(Modifier.height(20.dp))
                    Text("TaskMaster", style = MaterialTheme.typography.headlineLarge,
                        color = MaterialTheme.colorScheme.onBackground)

                    Spacer(Modifier.height(120.dp))
                    Text("Crea una cuenta", style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onBackground)

                    Spacer(Modifier.height(45.dp))

                    // Correo
                    TextField(
                        value = email,
                        onValueChange = { email = it },
                        modifier = Modifier
                            .padding(horizontal = 10.dp, vertical = 8.dp)
                            .width(350.dp),
                        placeholder = {
                            Text("Correo", style = MaterialTheme.typography.bodyLarge,
                                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f))
                        },
                        trailingIcon = {
                            Icon(imageVector = Icons.Filled.Email, contentDescription = null,
                                tint = MaterialTheme.colorScheme.onSurface)
                        },
                        shape = RoundedCornerShape(12.dp),
                        colors = TextFieldDefaults.colors(
                            focusedTextColor = MaterialTheme.colorScheme.onSurface,
                            unfocusedTextColor = MaterialTheme.colorScheme.onSurface,
                            cursorColor = MaterialTheme.colorScheme.primary,
                            focusedContainerColor = MaterialTheme.colorScheme.secondaryContainer,
                            unfocusedContainerColor = MaterialTheme.colorScheme.secondaryContainer
                        )
                    )

                    // Contraseña
                    TextField(
                        value = password,
                        onValueChange = { password = it },
                        modifier = Modifier
                            .padding(horizontal = 10.dp, vertical = 8.dp)
                            .width(350.dp),
                        placeholder = {
                            Text("Contraseña", style = MaterialTheme.typography.bodyLarge,
                                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f))
                        },
                        visualTransformation = if (passVisible) VisualTransformation.None else PasswordVisualTransformation(),
                        trailingIcon = {
                            IconButton(onClick = { passVisible = !passVisible }) {
                                Image(
                                    modifier = Modifier.size(25.dp),
                                    painter = painterResource(
                                        if (passVisible) R.drawable.visibilityoff else R.drawable.visibility
                                    ),
                                    contentDescription = null
                                )
                            }
                        },
                        shape = RoundedCornerShape(12.dp),
                        colors = TextFieldDefaults.colors(
                            focusedTextColor = MaterialTheme.colorScheme.onSurface,
                            unfocusedTextColor = MaterialTheme.colorScheme.onSurface,
                            cursorColor = MaterialTheme.colorScheme.primary,
                            focusedContainerColor = MaterialTheme.colorScheme.secondaryContainer,
                            unfocusedContainerColor = MaterialTheme.colorScheme.secondaryContainer
                        )
                    )

                    // Username
                    TextField(
                        value = username,
                        onValueChange = { username = it },
                        modifier = Modifier
                            .padding(horizontal = 10.dp, vertical = 8.dp)
                            .width(350.dp),
                        placeholder = {
                            Text("Username", style = MaterialTheme.typography.bodyLarge,
                                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f))
                        },
                        trailingIcon = {
                            Icon(imageVector = Icons.Filled.AccountCircle, contentDescription = null,
                                tint = MaterialTheme.colorScheme.onSurface)
                        },
                        shape = RoundedCornerShape(12.dp),
                        colors = TextFieldDefaults.colors(
                            focusedTextColor = MaterialTheme.colorScheme.onSurface,
                            unfocusedTextColor = MaterialTheme.colorScheme.onSurface,
                            cursorColor = MaterialTheme.colorScheme.primary,
                            focusedContainerColor = MaterialTheme.colorScheme.secondaryContainer,
                            unfocusedContainerColor = MaterialTheme.colorScheme.secondaryContainer
                        )
                    )

                    Spacer(Modifier.height(25.dp))

                    Button(
                        onClick = {
                            if (username.isNotBlank() && email.isNotBlank() && password.isNotBlank()) {
                                vm.signUpUsername(username, email, password)
                            }
                        },
                        modifier = Modifier.width(180.dp),
                        enabled = !isLoading
                    ) {
                        if (isLoading) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(18.dp),
                                strokeWidth = 2.dp,
                                color = MaterialTheme.colorScheme.onPrimary
                            )
                            Spacer(Modifier.width(10.dp))
                        }
                        Text(
                            text = if (isLoading) "Creando..." else "Crear cuenta",
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.onPrimary
                        )
                    }

                    if (!error.isNullOrBlank()) {
                        Spacer(Modifier.height(12.dp))
                        Text(
                            text = error ?: "",
                            color = MaterialTheme.colorScheme.error,
                            style = MaterialTheme.typography.bodySmall
                        )
                    }

                    Spacer(Modifier.height(20.dp))
                    Row(
                        modifier = Modifier.padding(5.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("Ya tienes una cuenta?",
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.onBackground)
                        Text(" Ingresa",
                            style = MaterialTheme.typography.labelLarge,
                            color = MaterialTheme.colorScheme.onBackground,
                            modifier = Modifier.clickable { nav.popBackStack() })
                    }
                }
            }
        }
    }
}

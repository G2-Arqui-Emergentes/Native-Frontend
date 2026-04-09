package com.example.taskmaster.views.shared

import android.content.Context
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.example.taskmaster.R
import com.example.taskmaster.viewmodel.data.net.TokenStore
import com.example.taskmaster.viewmodel.model.AuthViewModel
import com.example.taskmaster.viewmodel.sharedPreferences.Prefs
import androidx.compose.foundation.layout.BoxWithConstraints

@Composable
fun Login(context: Context, nav: NavHostController) {

    val vm = remember { AuthViewModel() }
    val isLoading by vm.isLoading.collectAsState()
    val error by vm.error.collectAsState()
    val token by vm.token.collectAsState()

    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var passVisible by remember { mutableStateOf(false) }
    var rememberMe by remember { mutableStateOf(true) }

    LaunchedEffect(Unit) {
        Prefs.loadEmail(context)?.let { email = it }
        Prefs.loadPassword(context)?.let { password = it }
    }

    var justPressedLogin by remember { mutableStateOf(false) }
    LaunchedEffect(token) {
        val t = token
        if (justPressedLogin && !t.isNullOrBlank()) {
            if (rememberMe) {
                Prefs.saveAll(context, email, password, t)
            } else {
                Prefs.saveEmailAndPassword(context, email, password)
                Prefs.clearToken(context)
            }
            TokenStore.token = t
            nav.navigate("projects") {
                popUpTo("login") { inclusive = true }
                launchSingleTop = true
            }
            justPressedLogin = false
        }
    }

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background,
    ) {

        BoxWithConstraints(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .imePadding()
        ) {
            val isExpanded = maxWidth >= 840.dp
            val isMedium = maxWidth in 600.dp..839.dp

            val horizontalPadding = when {
                isExpanded -> 48.dp
                isMedium -> 32.dp
                else -> 16.dp
            }

            val contentMaxWidth = when {
                isExpanded -> 520.dp
                isMedium -> 440.dp
                else -> maxWidth - horizontalPadding * 2
            }

            // Figura más abajo (pico profundo)
            val headerHeight = when {
                isExpanded -> 0.dp
                maxHeight < 600.dp -> maxHeight * 0.42f
                maxHeight < 720.dp -> maxHeight * 0.46f
                else -> maxHeight * 0.50f
            }

            val vSpace = when {
                maxHeight < 640.dp -> 12.dp
                maxHeight < 720.dp -> 16.dp
                else -> 24.dp
            }

            val boxMaxWidth = this.maxWidth
            val logoSizeDp = if (boxMaxWidth < 360.dp) 96.dp else 112.dp

            if (isExpanded) {
                // Dos columnas en pantallas grandes
                Row(Modifier.fillMaxSize()) {
                    // Panel visual
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxHeight()
                    ) {
                        Image(
                            modifier = Modifier.fillMaxSize(),
                            painter = painterResource(id = R.drawable.shape),
                            contentDescription = null,
                            contentScale = ContentScale.Crop
                        )
                        Column(
                            modifier = Modifier
                                .align(Alignment.TopCenter)
                                .padding(top = 24.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Icon(
                                modifier = Modifier.size(140.dp),
                                painter = painterResource(id = R.drawable.taskmaster_logoblanco),
                                contentDescription = null
                            )
                            Spacer(Modifier.height(12.dp))
                            Text(
                                text = "TaskMaster",
                                style = MaterialTheme.typography.headlineLarge,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }
                    }

                    FormColumn(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxHeight()
                            .verticalScroll(rememberScrollState())
                            .padding(horizontal = horizontalPadding, vertical = 24.dp),
                        contentMaxWidth = contentMaxWidth,
                        vSpace = vSpace,
                        topPadding = vSpace,
                        isLoading = isLoading,
                        error = error,
                        email = email,
                        onEmailChange = { email = it },
                        password = password,
                        onPasswordChange = { password = it },
                        passVisible = passVisible,
                        onTogglePass = { passVisible = !passVisible },
                        rememberMe = rememberMe,
                        onRememberChange = { rememberMe = it },
                        onLogin = {
                            if (email.isNotBlank() && password.isNotBlank()) {
                                justPressedLogin = true
                                vm.signIn(email, password)
                            }
                        },
                        onRegisterClick = { nav.navigate("register") }
                    )
                }
            } else {
                // Layout de una columna para móviles
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState())
                ) {
                    HeaderBrand(
                        height = headerHeight,
                        logoSize = logoSizeDp
                    )


                    val formPullUp = 32.dp
                    FormColumn(
                        modifier = Modifier
                            .fillMaxWidth()
                            .offset(y = -formPullUp) // ← mueve todo hacia arriba
                            .padding(horizontal = horizontalPadding, vertical = 24.dp),
                        contentMaxWidth = contentMaxWidth,
                        vSpace = vSpace,
                        topPadding = 2.dp,
                        isLoading = isLoading,
                        error = error,
                        email = email,
                        onEmailChange = { email = it },
                        password = password,
                        onPasswordChange = { password = it },
                        passVisible = passVisible,
                        onTogglePass = { passVisible = !passVisible },
                        rememberMe = rememberMe,
                        onRememberChange = { rememberMe = it },
                        onLogin = {
                            if (email.isNotBlank() && password.isNotBlank()) {
                                justPressedLogin = true
                                vm.signIn(email, password)
                            }
                        },
                        onRegisterClick = { nav.navigate("register") }
                    )
                }
            }
        }
    }
}

@Composable
private fun HeaderBrand(
    height: Dp,
    logoSize: Dp
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(height)
    ) {
        Image(
            modifier = Modifier.fillMaxSize(),
            painter = painterResource(id = R.drawable.shape),
            contentDescription = null,
            contentScale = ContentScale.FillBounds
        )
        Column(
            modifier = Modifier
                .align(Alignment.TopCenter)
                .padding(top = 16.dp), // logo más arriba
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                modifier = Modifier.size(logoSize),
                painter = painterResource(id = R.drawable.taskmaster_logoblanco),
                contentDescription = null
            )
            Spacer(Modifier.height(8.dp))
            Text(
                text = "TaskMaster",
                style = MaterialTheme.typography.headlineLarge,
                color = MaterialTheme.colorScheme.onSurface
            )
        }
    }
}

@Composable
private fun FormColumn(
    modifier: Modifier,
    contentMaxWidth: Dp,
    vSpace: Dp,
    topPadding: Dp,                // 👈 nuevo: espacio antes del título
    isLoading: Boolean,
    error: String?,
    email: String,
    onEmailChange: (String) -> Unit,
    password: String,
    onPasswordChange: (String) -> Unit,
    passVisible: Boolean,
    onTogglePass: () -> Unit,
    rememberMe: Boolean,
    onRememberChange: (Boolean) -> Unit,
    onLogin: () -> Unit,
    onRegisterClick: () -> Unit
) {
    Box(modifier = modifier, contentAlignment = Alignment.TopCenter) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .widthIn(max = contentMaxWidth),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(Modifier.height(topPadding))    // ← controla la posición del título
            Text(
                text = "Inicia Sesión",
                style = MaterialTheme.typography.headlineMedium,
                color = MaterialTheme.colorScheme.onBackground
            )
            Spacer(Modifier.height(vSpace))

            // EMAIL
            TextField(
                value = email,
                onValueChange = onEmailChange,
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                placeholder = {
                    Text(
                        text = "Correo",
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                    )
                },
                trailingIcon = {
                    Icon(
                        imageVector = Icons.Filled.Email,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onSurface
                    )
                },
                shape = RoundedCornerShape(12.dp),
                colors = TextFieldDefaults.colors(
                    focusedTextColor = MaterialTheme.colorScheme.onSurface,
                    unfocusedTextColor = MaterialTheme.colorScheme.onSurface,
                    cursorColor = MaterialTheme.colorScheme.primary,
                    focusedContainerColor = MaterialTheme.colorScheme.secondaryContainer,
                    unfocusedContainerColor = MaterialTheme.colorScheme.secondaryContainer,
                    focusedPlaceholderColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                    unfocusedPlaceholderColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                )
            )

            Spacer(Modifier.height(vSpace))

            // PASSWORD
            TextField(
                value = password,
                onValueChange = onPasswordChange,
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                placeholder = {
                    Text(
                        text = "Contraseña",
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                    )
                },
                visualTransformation = if (passVisible) VisualTransformation.None else PasswordVisualTransformation(),
                trailingIcon = {
                    IconButton(onClick = onTogglePass) {
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
                    unfocusedContainerColor = MaterialTheme.colorScheme.secondaryContainer,
                    focusedPlaceholderColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                    unfocusedPlaceholderColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                )
            )

            Spacer(Modifier.height(vSpace))

            if (!error.isNullOrBlank()) {
                Text(
                    text = error!!,
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodyMedium
                )
                Spacer(Modifier.height(vSpace))
            }

            Button(
                onClick = onLogin,
                modifier = Modifier.fillMaxWidth(),
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
                    text = if (isLoading) "Ingresando..." else "Iniciar Sesión",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onPrimary
                )
            }

            Spacer(Modifier.height(vSpace))

            // Centrado
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center,
                modifier = Modifier.fillMaxWidth()
            ) {
                Switch(
                    checked = rememberMe,
                    onCheckedChange = onRememberChange
                )
                Spacer(Modifier.width(12.dp))
                Text(
                    text = "Recordar credenciales",
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onBackground
                )
            }

            Spacer(Modifier.height(vSpace * 1.2f))

            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = "¿No tienes una cuenta? ",
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onBackground
                )
                Text(
                    "Crea una",
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.onBackground,
                    modifier = Modifier.clickable { onRegisterClick() }
                )
            }
        }
    }
}

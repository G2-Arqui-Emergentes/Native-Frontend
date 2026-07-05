package com.example.taskmaster.views.layout

import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material.icons.outlined.Email
import androidx.compose.material.icons.outlined.Lock
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import coil.compose.AsyncImage
import com.example.taskmaster.R
import com.example.taskmaster.viewmodel.sharedPreferences.Prefs
import com.example.taskmaster.viewmodel.ui.users.UsersViewModel
import com.example.taskmaster.views.layout.common.AppTopHeader

private val ProfileBackground = Color(0xFFF9FAFB)
private val ProfileSurface = Color(0xFFFFFFFF)
private val ProfileMuted = Color(0xFFF4F5F7)
private val ProfileBorder = Color(0xFFE5E7EB)
private val ProfileTextPrimary = Color(0xFF111827)
private val ProfileTextSecondary = Color(0xFF6B7280)
private val ProfileBrand = Color(0xFFEC1926)
private val ProfilePositive = Color(0xFF0F9E6E)
private val ProfileDanger = Color(0xFFEE4445)
private val ProfileWarm = Color(0xFFFFF1F2)
private val ProfileBlue = Color(0xFFEFF6FF)

private enum class ProfileThemeMode { LIGHT, DARK, SYSTEM }

@Composable
fun ProfileScreen(
    context: Context,
    nav: NavHostController,
    userVm: UsersViewModel = remember { UsersViewModel() }
) {
    val user by userVm.user.collectAsState()
    val isLoading by userVm.isLoading.collectAsState()
    val error by userVm.error.collectAsState()
    val locale = LocalConfiguration.current.locales[0]
    val isSpanish = locale?.language?.startsWith("es") == true

    var themeMode by rememberSaveable { mutableStateOf(ProfileThemeMode.SYSTEM) }
    var emailAlertsEnabled by rememberSaveable { mutableStateOf(true) }
    var pushNotificationsEnabled by rememberSaveable { mutableStateOf(true) }
    var desktopBannerEnabled by rememberSaveable { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        Prefs.loadEmail(context)?.let(userVm::loadByEmail)
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(ProfileBackground)
    ) {
        when {
            isLoading && user == null -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(color = ProfileBrand)
                }
            }

            else -> {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    item {
                        AppTopHeader(
                            user = user,
                            onNotificationsClick = { nav.navigate("notification") },
                            onProfileClick = {}
                        )
                    }

                    item {
                        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                            Text(
                                text = if (isSpanish) "Perfil" else "Profile",
                                style = MaterialTheme.typography.headlineSmall.copy(
                                    fontWeight = FontWeight.Bold
                                ),
                                color = ProfileTextPrimary
                            )
                            Text(
                                text = if (isSpanish) {
                                    "Configura tu cuenta, apariencia y seguridad."
                                } else {
                                    "Manage your account, appearance and security."
                                },
                                style = MaterialTheme.typography.bodyMedium,
                                color = ProfileTextSecondary
                            )
                        }
                    }

                    item {
                        ProfileHeroCard(
                            fullName = listOfNotNull(user?.name, user?.lastName)
                                .joinToString(" ")
                                .ifBlank { user?.email ?: "" },
                            role = resolveProfileRole(user?.roles.orEmpty()),
                            imageUrl = user?.imageUrl,
                            isSpanish = isSpanish,
                            onEditPhoto = {
                                showPendingMessage(
                                    context,
                                    if (isSpanish) "Cambio de foto disponible pronto." else "Photo update will be available soon."
                                )
                            },
                            onLogout = {
                                Prefs.clearToken(context)
                                nav.navigate("login") {
                                    popUpTo(0) { inclusive = true }
                                    launchSingleTop = true
                                }
                            }
                        )
                    }

                    item { SectionTitle(if (isSpanish) "Apariencia" else "Appearance") }

                    item {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            AppearanceOptionCard(
                                title = if (isSpanish) "Modo claro" else "Light Mode",
                                indicatorColor = Color(0xFFFFD166),
                                selected = themeMode == ProfileThemeMode.LIGHT,
                                modifier = Modifier.weight(1f),
                                onClick = { themeMode = ProfileThemeMode.LIGHT }
                            )
                            AppearanceOptionCard(
                                title = if (isSpanish) "Modo oscuro" else "Dark Mode",
                                indicatorColor = Color(0xFF111827),
                                selected = themeMode == ProfileThemeMode.DARK,
                                modifier = Modifier.weight(1f),
                                onClick = { themeMode = ProfileThemeMode.DARK }
                            )
                            AppearanceOptionCard(
                                title = if (isSpanish) "Sistema" else "System Default",
                                indicatorColor = ProfileBrand,
                                selected = themeMode == ProfileThemeMode.SYSTEM,
                                modifier = Modifier.weight(1f),
                                onClick = { themeMode = ProfileThemeMode.SYSTEM }
                            )
                        }
                    }

                    item { SectionTitle(if (isSpanish) "Seguridad" else "Security") }

                    item {
                        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                            SecurityItem(
                                title = if (isSpanish) "Cambiar contraseña" else "Change Password",
                                subtitle = if (isSpanish) "Última actualización hace 4 meses" else "Last updated 4 months ago",
                                icon = Icons.Outlined.Lock,
                                onClick = {
                                    showPendingMessage(
                                        context,
                                        if (isSpanish) "Flujo de cambio de contraseña aún no disponible." else "Change password flow is not available yet."
                                    )
                                }
                            )
                            SecurityItem(
                                title = if (isSpanish) "Autenticación de dos factores" else "Two-Factor Authentication",
                                subtitle = if (isSpanish) "Estado: Activo" else "Status: Active",
                                icon = Icons.Outlined.Notifications,
                                onClick = {
                                    showPendingMessage(
                                        context,
                                        if (isSpanish) "Flujo de autenticación de dos factores aún no disponible." else "Two-factor authentication flow is not available yet."
                                    )
                                }
                            )
                        }
                    }

                    item { SectionTitle(if (isSpanish) "Notificaciones" else "Notifications") }

                    item {
                        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                            NotificationPreferenceItem(
                                title = if (isSpanish) "Alertas por correo" else "Email Alerts",
                                icon = Icons.Outlined.Email,
                                checked = emailAlertsEnabled,
                                onCheckedChange = { emailAlertsEnabled = it }
                            )
                            NotificationPreferenceItem(
                                title = if (isSpanish) "Notificaciones push" else "Push Notifications",
                                icon = Icons.Outlined.Notifications,
                                checked = pushNotificationsEnabled,
                                onCheckedChange = { pushNotificationsEnabled = it }
                            )
                            NotificationPreferenceItem(
                                title = if (isSpanish) "Banner de escritorio" else "Desktop Banner",
                                icon = Icons.Outlined.Notifications,
                                checked = desktopBannerEnabled,
                                onCheckedChange = { desktopBannerEnabled = it }
                            )
                        }
                    }

                    if (!error.isNullOrBlank()) {
                        item {
                            Text(
                                text = error.orEmpty(),
                                color = MaterialTheme.colorScheme.error,
                                style = MaterialTheme.typography.bodyMedium
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun ProfileHeroCard(
    fullName: String,
    role: String,
    imageUrl: String?,
    isSpanish: Boolean,
    onEditPhoto: () -> Unit,
    onLogout: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(28.dp),
        colors = CardDefaults.cardColors(containerColor = ProfileSurface),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    Brush.verticalGradient(
                        listOf(ProfileWarm, ProfileSurface, ProfileSurface)
                    )
                )
                .padding(horizontal = 22.dp, vertical = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(contentAlignment = Alignment.BottomEnd) {
                Box(
                    modifier = Modifier
                        .size(116.dp)
                        .clip(CircleShape)
                        .background(ProfileBrand.copy(alpha = 0.08f)),
                    contentAlignment = Alignment.Center
                ) {
                    AsyncImage(
                        model = imageUrl ?: R.drawable.ic_profile_placeholder,
                        contentDescription = "Profile",
                        modifier = Modifier
                            .size(104.dp)
                            .clip(CircleShape)
                            .border(3.dp, Color.White, CircleShape)
                            .background(ProfileMuted),
                        contentScale = ContentScale.Crop
                    )
                }
                Box(
                    modifier = Modifier
                        .size(38.dp)
                        .clip(CircleShape)
                        .background(ProfileBrand)
                        .border(3.dp, Color.White, CircleShape)
                        .shadow(10.dp, CircleShape, ambientColor = ProfileBrand.copy(alpha = 0.25f))
                        .clickable(onClick = onEditPhoto),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Outlined.Edit,
                        contentDescription = "Edit photo",
                        tint = Color.White,
                        modifier = Modifier.size(16.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = fullName,
                style = MaterialTheme.typography.headlineSmall.copy(
                    fontWeight = FontWeight.Bold
                ),
                color = ProfileTextPrimary
            )

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = role,
                style = MaterialTheme.typography.bodyMedium,
                color = ProfileTextSecondary
            )

            Spacer(modifier = Modifier.height(16.dp))

            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                ProfileTag(
                    text = if (isSpanish) "Cuenta activa" else "Active account",
                    background = ProfilePositive.copy(alpha = 0.12f),
                    content = ProfilePositive
                )
                ProfileTag(
                    text = if (isSpanish) "Perfil personal" else "Personal profile",
                    background = ProfileBrand.copy(alpha = 0.10f),
                    content = ProfileBrand
                )
            }

            Spacer(modifier = Modifier.height(14.dp))

            Card(
                modifier = Modifier.clickable(onClick = onLogout),
                shape = RoundedCornerShape(999.dp),
                colors = CardDefaults.cardColors(containerColor = ProfileDanger.copy(alpha = 0.10f))
            ) {
                Text(
                    text = if (isSpanish) "Cerrar sesión" else "Log out",
                    style = MaterialTheme.typography.bodyMedium.copy(
                        fontWeight = FontWeight.SemiBold
                    ),
                    color = ProfileDanger,
                    modifier = Modifier.padding(horizontal = 18.dp, vertical = 10.dp)
                )
            }
        }
    }
}

@Composable
private fun SectionTitle(title: String) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        Box(
            modifier = Modifier
                .size(width = 6.dp, height = 22.dp)
                .clip(RoundedCornerShape(999.dp))
                .background(ProfileBrand)
        )
        Text(
            text = title,
            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
            color = ProfileTextPrimary
        )
    }
}

@Composable
private fun AppearanceOptionCard(
    title: String,
    indicatorColor: Color,
    selected: Boolean,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Card(
        modifier = modifier.clickable(onClick = onClick),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (selected) ProfileBrand.copy(alpha = 0.10f) else ProfileSurface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .border(
                    width = 1.dp,
                    color = if (selected) ProfileBrand else ProfileBorder,
                    shape = RoundedCornerShape(18.dp)
                )
                .padding(horizontal = 10.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(18.dp)
                    .clip(CircleShape)
                    .background(
                        if (selected) indicatorColor else indicatorColor.copy(alpha = 0.12f)
                    )
                    .border(
                        width = if (selected) 0.dp else 1.dp,
                        color = if (selected) Color.Transparent else ProfileBorder,
                        shape = CircleShape
                    )
            )
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.SemiBold),
                    color = if (selected) ProfileBrand else ProfileTextPrimary
                )
            }
            Box(
                modifier = Modifier
                    .size(14.dp)
                    .clip(CircleShape)
                    .background(if (selected) ProfileBrand else Color.Transparent)
                    .border(1.dp, if (selected) ProfileBrand else ProfileBorder, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                if (selected) {
                    Text(
                        text = "•",
                        color = Color.White,
                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold)
                    )
                }
            }
        }
    }
}

@Composable
private fun SecurityItem(
    title: String,
    subtitle: String,
    icon: ImageVector,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(22.dp),
        colors = CardDefaults.cardColors(containerColor = ProfileSurface),
        elevation = CardDefaults.cardElevation(defaultElevation = 3.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(42.dp)
                    .clip(RoundedCornerShape(14.dp))
                    .background(ProfileBlue),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = title,
                    tint = ProfileBrand,
                    modifier = Modifier.size(20.dp)
                )
            }
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.SemiBold),
                    color = ProfileTextPrimary
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.bodySmall,
                    color = ProfileTextSecondary
                )
            }
            Text(
                text = ">",
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                color = ProfileTextSecondary
            )
        }
    }
}

@Composable
private fun NotificationPreferenceItem(
    title: String,
    icon: ImageVector,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(22.dp),
        colors = CardDefaults.cardColors(containerColor = ProfileSurface),
        elevation = CardDefaults.cardElevation(defaultElevation = 3.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 14.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(42.dp)
                    .clip(RoundedCornerShape(14.dp))
                    .background(ProfileWarm),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = title,
                    tint = ProfileBrand,
                    modifier = Modifier.size(20.dp)
                )
            }
            Text(
                text = title,
                style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.SemiBold),
                color = ProfileTextPrimary,
                modifier = Modifier.weight(1f)
            )
            Switch(
                checked = checked,
                onCheckedChange = onCheckedChange,
                colors = SwitchDefaults.colors(
                    checkedThumbColor = Color.White,
                    checkedTrackColor = ProfilePositive,
                    uncheckedThumbColor = Color.White,
                    uncheckedTrackColor = ProfileBorder,
                    uncheckedBorderColor = ProfileBorder
                )
            )
        }
    }
}

@Composable
private fun ProfileTag(
    text: String,
    background: Color,
    content: Color
) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(999.dp))
            .background(background)
            .padding(horizontal = 12.dp, vertical = 6.dp)
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.SemiBold),
            color = content
        )
    }
}

private fun resolveProfileRole(roles: List<String>): String {
    return when {
        roles.any { it.equals("ROLE_LEADER", ignoreCase = true) } -> "Leader"
        roles.any { it.equals("ROLE_MEMBER", ignoreCase = true) } -> "Member"
        else -> roles.firstOrNull()
            ?.removePrefix("ROLE_")
            ?.replaceFirstChar { it.uppercase() }
            .orEmpty()
    }
}

private fun showPendingMessage(context: Context, message: String) {
    Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
}

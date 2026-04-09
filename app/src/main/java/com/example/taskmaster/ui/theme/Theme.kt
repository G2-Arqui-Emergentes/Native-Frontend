package com.example.taskmaster.ui.theme

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import com.example.taskmaster.ui.theme.RedWine500

private val DarkColorScheme = darkColorScheme(
    // Color de marca para acciones principales (Botón "Crear", FAB, etc.)
    primary = RedWine600,
    onPrimary = White,

    // Fondo general de la app osea los toolbars
    background = Black,
    onBackground = White,

    // Superficie para tarjetas, las cards por ahora y a veces los input
    surface = CardGray,
    onSurface = Brownish900,

    // Acentos secundarios para el color de la tabla y borde de tarjetas
    secondary = Blush100,
    onSecondary = White,

    // Contenedor secundario, lo mismo que background xd, pero para tener orden lo meto
    secondaryContainer = Coral300,
    onSecondaryContainer = White,

    // Borde de inputs y contenedores delineados
    outline = Blush100,

    // Estados de error
    error = AlertRed,
    onError = White,

    // Resaltar, como olvidaste algo o para que el usuario lo mire
    tertiary = AlertRed,
    onTertiary = Blush100,
)

private val LightColorScheme = lightColorScheme(
    // Color de marca para acciones principales (Botón "Crear", FAB, etc.)
    primary = RedWine600,
    onPrimary = White,

    // Fondo general de la app osea los toolbars
    background = White,
    onBackground = Black,

    // Superficie para tarjetas, las cards por ahora y a veces los input
    surface = CardGray,
    onSurface = Black,

    // Acentos secundarios para el color de la tabla y borde de tarjetas
    secondary = RedWine500,
    onSecondary = White,

    // Contenedor secundario, lo mismo que background xd, pero para tener orden lo meto
    secondaryContainer = Blush100,
    onSecondaryContainer = Brownish900,

    // Borde de inputs y contenedores delineados
    outline = Brownish900,

    // Estados de error/alerta
    error = AlertRed,
    onError = White,

    // Resaltar, como olvidaste algo o para que el usuario lo mire
    tertiary = AlertRed,
    onTertiary = Brownish900,

)

@Composable
fun TaskMasterTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),

    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    val colorScheme =
        if (dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            val ctx = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(ctx) else dynamicLightColorScheme(ctx)
        } else {
            if (darkTheme) DarkColorScheme else LightColorScheme
        }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
package com.example.taskmaster.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.example.taskmaster.R

// Set of Material typography styles to start with
val SulphurPoint = FontFamily(
    Font(R.font.sulphurpoint_light,   weight = FontWeight.Light),   // 300
    Font(R.font.sulphurpoint_regular, weight = FontWeight.Normal),  // 400
    Font(R.font.sulphurpoint_bold,    weight = FontWeight.Bold),    // 700
)
val BodySans = FontFamily(
    Font(R.font.ms_reference_sans_serif, weight = FontWeight.Normal)
)
val Typography = Typography(
    // -------- TÍTULOS (Sulphur Point) --------
    displayLarge  = TextStyle(fontFamily = SulphurPoint, fontWeight = FontWeight.Bold,   fontSize = 57.sp, lineHeight = 64.sp),
    displayMedium = TextStyle(fontFamily = SulphurPoint, fontWeight = FontWeight.Bold,   fontSize = 45.sp, lineHeight = 52.sp),
    displaySmall  = TextStyle(fontFamily = SulphurPoint, fontWeight = FontWeight.Bold,   fontSize = 36.sp, lineHeight = 44.sp),

    headlineLarge = TextStyle(fontFamily = SulphurPoint, fontWeight = FontWeight.Bold,   fontSize = 32.sp, lineHeight = 40.sp),
    headlineMedium= TextStyle(fontFamily = SulphurPoint, fontWeight = FontWeight.Normal, fontSize = 28.sp, lineHeight = 36.sp),
    headlineSmall = TextStyle(fontFamily = SulphurPoint, fontWeight = FontWeight.Light,  fontSize = 24.sp, lineHeight = 32.sp),

    titleLarge    = TextStyle(fontFamily = SulphurPoint, fontWeight = FontWeight.Normal, fontSize = 22.sp, lineHeight = 28.sp),
    titleMedium   = TextStyle(fontFamily = SulphurPoint, fontWeight = FontWeight.Light,  fontSize = 16.sp, lineHeight = 24.sp),
    titleSmall    = TextStyle(fontFamily = SulphurPoint, fontWeight = FontWeight.Light,  fontSize = 14.sp, lineHeight = 20.sp),

    // -------- CUERPO & LABELS (MS Reference / Inter) --------
    bodyLarge  = TextStyle(fontFamily = BodySans, fontWeight = FontWeight.Normal, fontSize = 24.sp, lineHeight = 24.sp),
    bodyMedium = TextStyle(fontFamily = BodySans, fontWeight = FontWeight.Normal, fontSize = 16.sp, lineHeight = 20.sp),
    bodySmall  = TextStyle(fontFamily = BodySans, fontWeight = FontWeight.Normal, fontSize = 14.sp, lineHeight = 16.sp),

    labelLarge = TextStyle(fontFamily = SulphurPoint, fontWeight = FontWeight.Bold, fontSize = 16.sp, lineHeight = 20.sp),
    labelMedium= TextStyle(fontFamily = SulphurPoint, fontWeight = FontWeight.Normal, fontSize = 14.sp, lineHeight = 16.sp),
    labelSmall = TextStyle(fontFamily = BodySans, fontWeight = FontWeight.Normal, fontSize = 12.sp, lineHeight = 16.sp, letterSpacing = 0.5.sp),
)
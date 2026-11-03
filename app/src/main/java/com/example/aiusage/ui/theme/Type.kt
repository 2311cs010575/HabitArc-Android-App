package com.example.aiusage.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.googlefonts.Font
import androidx.compose.ui.text.googlefonts.GoogleFont
import androidx.compose.ui.unit.sp
import com.example.aiusage.R

// Google Font Provider Configuration
val provider = GoogleFont.Provider(
    providerAuthority = "com.google.android.gms.fonts",
    providerPackage = "com.google.android.gms",
    certificates = R.array.com_google_android_gms_fonts_certs
)

// Font Families
val OutfitFont = GoogleFont("Outfit")
val OutfitFontFamily = FontFamily(
    Font(googleFont = OutfitFont, fontProvider = provider)
)

val InterFont = GoogleFont("Inter")
val InterFontFamily = FontFamily(
    Font(googleFont = InterFont, fontProvider = provider)
)

// Mapped Typography to Google Stitch specs
val Typography = Typography(
    displayLarge = TextStyle(
        fontFamily = OutfitFontFamily,
        fontWeight = FontWeight.SemiBold, // 600
        fontSize = 48.sp,
        lineHeight = 56.sp,
        letterSpacing = (-0.96).sp // -0.02em
    ),
    headlineLarge = TextStyle(
        fontFamily = OutfitFontFamily,
        fontWeight = FontWeight.SemiBold, // 600
        fontSize = 32.sp,
        lineHeight = 40.sp,
        letterSpacing = (-0.32).sp // -0.01em
    ),
    headlineMedium = TextStyle(
        fontFamily = OutfitFontFamily,
        fontWeight = FontWeight.SemiBold, // 600
        fontSize = 24.sp, // headline-lg-mobile
        lineHeight = 32.sp,
        letterSpacing = (-0.24).sp // -0.01em
    ),
    titleLarge = TextStyle(
        fontFamily = OutfitFontFamily,
        fontWeight = FontWeight.Medium, // 500
        fontSize = 20.sp, // title-md
        lineHeight = 28.sp
    ),
    bodyLarge = TextStyle(
        fontFamily = InterFontFamily,
        fontWeight = FontWeight.Normal, // 400
        fontSize = 18.sp, // body-lg
        lineHeight = 28.sp
    ),
    bodyMedium = TextStyle(
        fontFamily = InterFontFamily,
        fontWeight = FontWeight.Normal, // 400
        fontSize = 16.sp, // body-md
        lineHeight = 24.sp
    ),
    labelLarge = TextStyle(
        fontFamily = InterFontFamily,
        fontWeight = FontWeight.Medium, // 500
        fontSize = 14.sp, // label-md
        lineHeight = 20.sp,
        letterSpacing = 0.14.sp // 0.01em
    ),
    labelSmall = TextStyle(
        fontFamily = InterFontFamily,
        fontWeight = FontWeight.SemiBold, // 600
        fontSize = 12.sp, // label-sm
        lineHeight = 16.sp,
        letterSpacing = 0.6.sp // 0.05em
    )
)

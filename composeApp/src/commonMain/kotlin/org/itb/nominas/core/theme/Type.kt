package org.itb.nominas.core.theme

import org.jetbrains.compose.resources.Font
import androidx.compose.material3.Typography
import androidx.compose.runtime.Composable
import androidx.compose.ui.text.font.FontFamily
import nominas.composeapp.generated.resources.Inter_Regular
import nominas.composeapp.generated.resources.JetBrainsMono_Regular
import nominas.composeapp.generated.resources.Outfit_Regular

import nominas.composeapp.generated.resources.Res

@Composable
fun getTypography(): Typography {

    val displayFontFamily = FontFamily(
        Font(
            Res.font.Outfit_Regular
        )
    )

    val bodyFontFamily = FontFamily(
        Font(Res.font.Inter_Regular)
    )

    val codeFontFamily = FontFamily(
        Font(Res.font.JetBrainsMono_Regular)
    )

    val baseline = Typography()

    return Typography(
        displayLarge = baseline.displayLarge.copy(fontFamily = displayFontFamily),
        displayMedium = baseline.displayMedium.copy(fontFamily = displayFontFamily),
        displaySmall = baseline.displaySmall.copy(fontFamily = displayFontFamily),
        headlineLarge = baseline.headlineLarge.copy(fontFamily = displayFontFamily),
        headlineMedium = baseline.headlineMedium.copy(fontFamily = displayFontFamily),
        headlineSmall = baseline.headlineSmall.copy(fontFamily = displayFontFamily),
        titleLarge = baseline.titleLarge.copy(fontFamily = displayFontFamily),
        titleMedium = baseline.titleMedium.copy(fontFamily = displayFontFamily),
        titleSmall = baseline.titleSmall.copy(fontFamily = displayFontFamily),
        bodyLarge = baseline.bodyLarge.copy(fontFamily = bodyFontFamily),
        bodyMedium = baseline.bodyMedium.copy(fontFamily = bodyFontFamily),
        bodySmall = baseline.bodySmall.copy(fontFamily = bodyFontFamily),
        labelLarge = baseline.labelLarge.copy(fontFamily = bodyFontFamily),
        labelMedium = baseline.labelMedium.copy(fontFamily = bodyFontFamily),
        labelSmall = baseline.labelSmall.copy(fontFamily = bodyFontFamily),
    )

}
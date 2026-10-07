package com.example.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val VsCodeColorScheme = darkColorScheme(
    primary = VsCodeBlue,
    onPrimary = Color.White,
    primaryContainer = VsCodeAccent,
    onPrimaryContainer = Color.White,
    secondary = SyntaxType,
    onSecondary = Color(0xFF003730),
    secondaryContainer = Color(0xFF1E3A34),
    onSecondaryContainer = SyntaxType,
    tertiary = SyntaxKeyword,
    onTertiary = Color.White,
    background = VsCodeBg,
    onBackground = VsCodeTextPrimary,
    surface = VsCodeSurface,
    onSurface = VsCodeTextPrimary,
    surfaceVariant = VsCodeSurfaceVariant,
    onSurfaceVariant = VsCodeTextSecondary,
    outline = VsCodeBorder,
    error = VsCodeRed,
    onError = Color.White
)

private val TokyoNightColorScheme = darkColorScheme(
    primary = TokyoNightPrimary,
    onPrimary = Color(0xFF1A1B26),
    primaryContainer = Color(0xFF283457),
    onPrimaryContainer = TokyoNightCyan,
    secondary = TokyoNightCyan,
    onSecondary = Color(0xFF1A1B26),
    tertiary = TokyoNightMagenta,
    background = TokyoNightBg,
    onBackground = Color(0xFFA9B1D6),
    surface = TokyoNightSurface,
    onSurface = Color(0xFFC0CAF5),
    surfaceVariant = Color(0xFF2F354D),
    onSurfaceVariant = Color(0xFF7AA2F7),
    outline = Color(0xFF414868),
    error = VsCodeRed,
    onError = Color.White
)

private val MonokaiColorScheme = darkColorScheme(
    primary = MonokaiYellow,
    onPrimary = Color(0xFF272822),
    primaryContainer = Color(0xFF3E3D32),
    onPrimaryContainer = MonokaiYellow,
    secondary = MonokaiCyan,
    onSecondary = Color(0xFF272822),
    tertiary = MonokaiPink,
    background = MonokaiBg,
    onBackground = Color(0xFFF8F8F2),
    surface = MonokaiSurface,
    onSurface = Color(0xFFF8F8F2),
    surfaceVariant = Color(0xFF383830),
    onSurfaceVariant = Color(0xFF75715E),
    outline = Color(0xFF49483E),
    error = MonokaiPink,
    onError = Color.White
)

private val GitHubDarkColorScheme = darkColorScheme(
    primary = GitHubDarkPrimary,
    onPrimary = Color.White,
    primaryContainer = Color(0xFF1F6FEB),
    onPrimaryContainer = Color.White,
    secondary = GitHubDarkAccent,
    onSecondary = Color.White,
    tertiary = Color(0xFFD2A8FF),
    background = GitHubDarkBg,
    onBackground = Color(0xFFC9D1D9),
    surface = GitHubDarkSurface,
    onSurface = Color(0xFFC9D1D9),
    surfaceVariant = Color(0xFF21262D),
    onSurfaceVariant = Color(0xFF8B949E),
    outline = GitHubDarkBorder,
    error = Color(0xFFF85149),
    onError = Color.White
)

private val DraculaColorScheme = darkColorScheme(
    primary = DraculaPurple,
    onPrimary = DraculaBg,
    primaryContainer = DraculaCurrentLine,
    onPrimaryContainer = DraculaForeground,
    secondary = DraculaCyan,
    onSecondary = DraculaBg,
    tertiary = DraculaPink,
    background = DraculaBg,
    onBackground = DraculaForeground,
    surface = DraculaSurface,
    onSurface = DraculaForeground,
    surfaceVariant = DraculaCurrentLine,
    onSurfaceVariant = DraculaComment,
    outline = DraculaCurrentLine,
    error = DraculaRed,
    onError = Color.White
)

private val OneDarkColorScheme = darkColorScheme(
    primary = OneDarkBlue,
    onPrimary = Color.White,
    primaryContainer = OneDarkSurface,
    onPrimaryContainer = OneDarkBlue,
    secondary = OneDarkGreen,
    onSecondary = Color.Black,
    tertiary = OneDarkPurple,
    background = OneDarkBg,
    onBackground = Color(0xFFABB2BF),
    surface = OneDarkSurface,
    onSurface = Color(0xFFABB2BF),
    surfaceVariant = Color(0xFF2C313A),
    onSurfaceVariant = Color(0xFF5C6370),
    outline = OneDarkBorder,
    error = OneDarkCoral,
    onError = Color.White
)

private val SynthwaveColorScheme = darkColorScheme(
    primary = SynthwavePink,
    onPrimary = SynthwaveBg,
    primaryContainer = SynthwaveSurface,
    onPrimaryContainer = SynthwaveCyan,
    secondary = SynthwaveCyan,
    onSecondary = SynthwaveBg,
    tertiary = SynthwaveYellow,
    background = SynthwaveBg,
    onBackground = Color(0xFFEDE2FE),
    surface = SynthwaveSurface,
    onSurface = Color(0xFFEDE2FE),
    surfaceVariant = Color(0xFF34294F),
    onSurfaceVariant = SynthwavePink,
    outline = SynthwaveBorder,
    error = Color(0xFFFF4A85),
    onError = Color.White
)

private val SolarizedDarkColorScheme = darkColorScheme(
    primary = SolarizedCyan,
    onPrimary = SolarizedBg,
    primaryContainer = SolarizedSurface,
    onPrimaryContainer = SolarizedBlue,
    secondary = SolarizedGreen,
    onSecondary = SolarizedBg,
    tertiary = SolarizedYellow,
    background = SolarizedBg,
    onBackground = Color(0xFF93A1A1),
    surface = SolarizedSurface,
    onSurface = Color(0xFF93A1A1),
    surfaceVariant = Color(0xFF0A4452),
    onSurfaceVariant = Color(0xFF586E75),
    outline = SolarizedBorder,
    error = Color(0xFFDC322F),
    onError = Color.White
)

private val LightModernColorScheme = androidx.compose.material3.lightColorScheme(
    primary = LightModernPrimary,
    onPrimary = Color.White,
    primaryContainer = Color(0xFFCCE4F7),
    onPrimaryContainer = LightModernPrimary,
    secondary = Color(0xFF008000),
    onSecondary = Color.White,
    tertiary = Color(0xFFAF00DB),
    background = LightModernBg,
    onBackground = LightModernTextPrimary,
    surface = LightModernSurface,
    onSurface = LightModernTextPrimary,
    surfaceVariant = LightModernSurfaceVariant,
    onSurfaceVariant = LightModernTextSecondary,
    outline = LightModernBorder,
    error = VsCodeRed,
    onError = Color.White
)

@Composable
fun StfCodeTheme(
    themeName: String = "dark_modern",
    content: @Composable () -> Unit
) {
    val selectedScheme = when (themeName.lowercase()) {
        "tokyo_night", "tokyo night" -> TokyoNightColorScheme
        "monokai_pro", "monokai", "monokai pro" -> MonokaiColorScheme
        "github_dark", "github", "github dark" -> GitHubDarkColorScheme
        "dracula" -> DraculaColorScheme
        "one_dark_pro", "onedark", "one dark", "one dark pro" -> OneDarkColorScheme
        "synthwave_84", "synthwave", "cyberpunk" -> SynthwaveColorScheme
        "solarized_dark", "solarized", "solarized dark" -> SolarizedDarkColorScheme
        "light_modern", "light", "light modern" -> LightModernColorScheme
        else -> VsCodeColorScheme
    }

    MaterialTheme(
        colorScheme = selectedScheme,
        typography = Typography,
        content = content
    )
}

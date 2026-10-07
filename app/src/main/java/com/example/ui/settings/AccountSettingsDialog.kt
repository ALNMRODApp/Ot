package com.example.ui.settings

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.automirrored.filled.WrapText
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.FormatSize
import androidx.compose.material.icons.filled.Numbers
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.UserProfile
import com.example.ui.theme.DraculaBg
import com.example.ui.theme.DraculaPurple
import com.example.ui.theme.GitHubDarkPrimary
import com.example.ui.theme.LightModernBg
import com.example.ui.theme.LightModernPrimary
import com.example.ui.theme.MonokaiBg
import com.example.ui.theme.MonokaiYellow
import com.example.ui.theme.OneDarkBg
import com.example.ui.theme.OneDarkBlue
import com.example.ui.theme.SolarizedBg
import com.example.ui.theme.SolarizedCyan
import com.example.ui.theme.SyntaxComment
import com.example.ui.theme.SyntaxType
import com.example.ui.theme.SynthwaveBg
import com.example.ui.theme.SynthwavePink
import com.example.ui.theme.TokyoNightPrimary
import com.example.ui.theme.VsCodeBg
import com.example.ui.theme.VsCodeBlue
import com.example.ui.theme.VsCodeBorder
import com.example.ui.theme.VsCodeRed
import com.example.ui.theme.VsCodeSurface
import com.example.ui.theme.VsCodeSurfaceVariant
import com.example.ui.theme.VsCodeTextPrimary
import com.example.ui.theme.VsCodeTextSecondary

data class ThemeOption(
    val id: String,
    val name: String,
    val description: String,
    val swatchColor: Color,
    val bgColor: Color
)

val THEME_OPTIONS = listOf(
    ThemeOption("dark_modern", "Dark Modern", "Official VS Code dark experience", VsCodeBlue, Color(0xFF181818)),
    ThemeOption("dracula", "Dracula Official", "Vibrant gothic purple & pink tones", DraculaPurple, DraculaBg),
    ThemeOption("tokyo_night", "Tokyo Night", "Neo-Tokyo neon blue & violet dusk", TokyoNightPrimary, Color(0xFF1A1B26)),
    ThemeOption("monokai_pro", "Monokai Pro", "Warm amber, magenta, high-contrast", MonokaiYellow, MonokaiBg),
    ThemeOption("one_dark_pro", "One Dark Pro", "Atom & VS Code legendary favorite", OneDarkBlue, OneDarkBg),
    ThemeOption("synthwave_84", "Synthwave '84", "Retro-futuristic neon cyberpunk glow", SynthwavePink, SynthwaveBg),
    ThemeOption("github_dark", "GitHub Dark", "Minimalist sleek slate & blue", GitHubDarkPrimary, Color(0xFF0D1117)),
    ThemeOption("solarized_dark", "Solarized Dark", "Teal, cyan, and amber classic", SolarizedCyan, SolarizedBg),
    ThemeOption("light_modern", "Light Modern", "Clean, high-readability day theme", LightModernPrimary, LightModernBg)
)

val ACCENT_PRESETS = listOf(
    "#007ACC" to "VS Blue",
    "#00D8D6" to "Cyan",
    "#BD93F9" to "Purple",
    "#FF7EDB" to "Pink",
    "#50FA7B" to "Green",
    "#FF9E64" to "Orange",
    "#E6DB74" to "Gold"
)

@Composable
fun AccountSettingsDialog(
    userProfile: UserProfile?,
    currentTheme: String,
    onSelectTheme: (String) -> Unit,
    fontSize: Int = 13,
    onFontSizeChange: (Int) -> Unit = {},
    showLineNumbers: Boolean = true,
    onToggleLineNumbers: () -> Unit = {},
    wordWrap: Boolean = false,
    onToggleWordWrap: () -> Unit = {},
    accentColorHex: String = "#007ACC",
    onSelectAccentColor: (String) -> Unit = {},
    onDismiss: () -> Unit,
    onSignOut: () -> Unit
) {
    val scrollState = rememberScrollState()

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(28.dp)
                            .clip(CircleShape)
                            .background(VsCodeBlue),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Palette,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Design & Settings",
                        fontSize = 17.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }

                IconButton(onClick = onDismiss, modifier = Modifier.size(28.dp)) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Close",
                        tint = VsCodeTextSecondary,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(scrollState),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                // Section 1: Themes Gallery
                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Icon(Icons.Default.Palette, contentDescription = null, tint = VsCodeBlue, modifier = Modifier.size(15.dp))
                        Text(
                            text = "COLOR THEME GALLERY",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = VsCodeTextSecondary,
                            letterSpacing = 1.sp
                        )
                    }

                    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        THEME_OPTIONS.forEach { theme ->
                            val isSelected = currentTheme.equals(theme.id, ignoreCase = true)
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(if (isSelected) VsCodeSurfaceVariant else VsCodeSurface.copy(alpha = 0.5f))
                                    .border(
                                        1.5.dp,
                                        if (isSelected) theme.swatchColor else VsCodeBorder.copy(alpha = 0.35f),
                                        RoundedCornerShape(8.dp)
                                    )
                                    .clickable { onSelectTheme(theme.id) }
                                    .padding(horizontal = 10.dp, vertical = 8.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                                    modifier = Modifier.weight(1f)
                                ) {
                                    // Dual Color Swatch (BG + Accent)
                                    Box(
                                        modifier = Modifier
                                            .size(24.dp)
                                            .clip(RoundedCornerShape(6.dp))
                                            .background(theme.bgColor)
                                            .border(1.dp, Color.White.copy(alpha = 0.2f), RoundedCornerShape(6.dp)),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Box(
                                            modifier = Modifier
                                                .size(10.dp)
                                                .clip(CircleShape)
                                                .background(theme.swatchColor)
                                        )
                                    }

                                    Column {
                                        Text(
                                            text = theme.name,
                                            fontSize = 12.sp,
                                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                            color = if (isSelected) Color.White else VsCodeTextPrimary
                                        )
                                        Text(
                                            text = theme.description,
                                            fontSize = 10.sp,
                                            color = VsCodeTextSecondary,
                                            lineHeight = 13.sp
                                        )
                                    }
                                }

                                if (isSelected) {
                                    Box(
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(4.dp))
                                            .background(theme.swatchColor.copy(alpha = 0.2f))
                                            .padding(horizontal = 6.dp, vertical = 2.dp)
                                    ) {
                                        Text(
                                            text = "ACTIVE",
                                            fontSize = 9.sp,
                                            fontFamily = FontFamily.Monospace,
                                            fontWeight = FontWeight.Bold,
                                            color = theme.swatchColor
                                        )
                                    }
                                }
                            }
                        }
                    }
                }

                // Section 2: Accent Color Customization
                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    Text(
                        text = "STATUS BAR & UI ACCENT",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = VsCodeTextSecondary,
                        letterSpacing = 1.sp
                    )

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .horizontalScroll(rememberScrollState()),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        ACCENT_PRESETS.forEach { (hex, _) ->
                            val color = try {
                                Color(android.graphics.Color.parseColor(hex))
                            } catch (e: Exception) {
                                VsCodeBlue
                            }
                            val isSelected = accentColorHex.equals(hex, true)
                            Box(
                                modifier = Modifier
                                    .size(30.dp)
                                    .clip(CircleShape)
                                    .background(color)
                                    .border(2.dp, if (isSelected) Color.White else Color.Transparent, CircleShape)
                                    .clickable { onSelectAccentColor(hex) },
                                contentAlignment = Alignment.Center
                            ) {
                                if (isSelected) {
                                    Icon(Icons.Default.Check, contentDescription = null, tint = Color.Black, modifier = Modifier.size(16.dp))
                                }
                            }
                        }
                    }
                }

                // Section 3: Editor Preferences (Font Size, Line Numbers, Word Wrap)
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(
                        text = "EDITOR PREFERENCES",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = VsCodeTextSecondary,
                        letterSpacing = 1.sp
                    )

                    // Font Size Selector
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(8.dp))
                            .background(VsCodeSurfaceVariant)
                            .padding(10.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            Icon(Icons.Default.FormatSize, contentDescription = null, tint = VsCodeBlue, modifier = Modifier.size(16.dp))
                            Text("Font Size", fontSize = 12.sp, color = VsCodeTextPrimary)
                        }

                        Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            listOf(11 to "11", 13 to "13", 15 to "15", 18 to "18").forEach { (size, label) ->
                                val isSelected = fontSize == size
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(6.dp))
                                        .background(if (isSelected) VsCodeBlue else VsCodeSurface)
                                        .clickable { onFontSizeChange(size) }
                                        .padding(horizontal = 8.dp, vertical = 4.dp)
                                ) {
                                    Text(
                                        text = label,
                                        fontSize = 11.sp,
                                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                        color = if (isSelected) Color.White else VsCodeTextSecondary
                                    )
                                }
                            }
                        }
                    }

                    // Line Numbers Toggle
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(8.dp))
                            .background(VsCodeSurfaceVariant)
                            .padding(horizontal = 10.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            Icon(Icons.Default.Numbers, contentDescription = null, tint = SyntaxComment, modifier = Modifier.size(16.dp))
                            Text("Show Line Numbers", fontSize = 12.sp, color = VsCodeTextPrimary)
                        }

                        Switch(
                            checked = showLineNumbers,
                            onCheckedChange = { onToggleLineNumbers() },
                            colors = SwitchDefaults.colors(
                                checkedThumbColor = Color.White,
                                checkedTrackColor = VsCodeBlue
                            )
                        )
                    }

                    // Word Wrap Toggle
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(8.dp))
                            .background(VsCodeSurfaceVariant)
                            .padding(horizontal = 10.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            Icon(Icons.AutoMirrored.Filled.WrapText, contentDescription = null, tint = SyntaxType, modifier = Modifier.size(16.dp))
                            Text("Soft Word Wrap", fontSize = 12.sp, color = VsCodeTextPrimary)
                        }

                        Switch(
                            checked = wordWrap,
                            onCheckedChange = { onToggleWordWrap() },
                            colors = SwitchDefaults.colors(
                                checkedThumbColor = Color.White,
                                checkedTrackColor = VsCodeBlue
                            )
                        )
                    }
                }

                // Section 4: Profile Information Card
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = VsCodeSurfaceVariant)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(42.dp)
                                .clip(CircleShape)
                                .background(VsCodeBlue),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Person,
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier.size(24.dp)
                            )
                        }

                        Spacer(modifier = Modifier.width(12.dp))

                        Column {
                            Text(
                                text = userProfile?.displayName?.ifBlank { "Developer" } ?: "Developer",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                            Text(
                                text = userProfile?.email ?: "Signed in via Firebase",
                                fontSize = 11.sp,
                                color = VsCodeTextSecondary
                            )
                            Text(
                                text = "STF Code Cloud Synced",
                                fontSize = 10.sp,
                                color = SyntaxType,
                                fontFamily = FontFamily.Monospace,
                                modifier = Modifier.padding(top = 2.dp)
                            )
                        }
                    }
                }

                // Sign Out Button
                Button(
                    onClick = onSignOut,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(40.dp)
                        .testTag("sign_out_button"),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = VsCodeRed.copy(alpha = 0.2f),
                        contentColor = VsCodeRed
                    ),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.Logout,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Sign Out of STF Code", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                }
            }
        },
        confirmButton = {},
        containerColor = VsCodeSurface,
        shape = RoundedCornerShape(16.dp)
    )
}

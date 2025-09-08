package com.ma7moud3ly.quran.features.settings

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicText
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ma7moud3ly.quran.managers.FontsManager
import com.ma7moud3ly.quran.model.AppFont
import com.ma7moud3ly.quran.model.AppSettings
import com.ma7moud3ly.quran.model.ReadingSettings
import com.ma7moud3ly.quran.model.RecitationSettings
import com.ma7moud3ly.quran.model.VersesMode
import com.ma7moud3ly.quran.ui.AppTheme
import com.ma7moud3ly.quran.ui.DialogHeader
import com.ma7moud3ly.quran.ui.MyDialog
import com.ma7moud3ly.quran.ui.MySurface
import com.ma7moud3ly.quran.ui.MySurfaceColumn
import com.ma7moud3ly.quran.ui.MySurfaceRow
import com.ma7moud3ly.quran.ui.hafsSmartFamily
import org.jetbrains.compose.resources.DrawableResource
import org.jetbrains.compose.resources.Font
import org.jetbrains.compose.resources.StringResource
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.ui.tooling.preview.Preview
import quran.composeapp.generated.resources.Res
import quran.composeapp.generated.resources.continues_verses
import quran.composeapp.generated.resources.multiple_verses
import quran.composeapp.generated.resources.settings_font_size
import quran.composeapp.generated.resources.settings_font_type
import quran.composeapp.generated.resources.settings_mode
import quran.composeapp.generated.resources.settings_mode_continues
import quran.composeapp.generated.resources.settings_mode_multiple
import quran.composeapp.generated.resources.settings_mode_single
import quran.composeapp.generated.resources.settings_night
import quran.composeapp.generated.resources.settings_of_reading
import quran.composeapp.generated.resources.settings_of_recitation
import quran.composeapp.generated.resources.settings_save
import quran.composeapp.generated.resources.single_verse


@Preview
@Composable
private fun SettingsScreenPreview() {
    AppTheme {
        SettingsScreenContent(
            darkModeEnabled = true,
            reading = true,
            settings = ReadingSettings(),
            fonts = FontsManager().getFonts(),
            uiEvents = {},
        )
    }
}

@Preview
@Composable
private fun SettingsScreenPreviewLight() {
    AppTheme(darkTheme = false) {
        SettingsScreenContent(
            darkModeEnabled = false,
            reading = true,
            settings = ReadingSettings(),
            fonts = FontsManager().getFonts(),
            uiEvents = {},
        )
    }
}


@Composable
fun SettingsScreenContent(
    darkModeEnabled: Boolean,
    reading: Boolean = false,
    settings: AppSettings,
    fonts: List<AppFont>,
    uiEvents: (SettingsEvents) -> Unit
) {

    val fontSize = remember { mutableStateOf(settings.font.fontSize) }
    val fontType = remember { mutableStateOf(settings.font) }
    val versesMode = remember { mutableStateOf(settings.versesMode) }

    fun saveSettings() {
        val newFont = fontType.value.copy(fontSize = fontSize.value)
        val newSettings = if (reading) ReadingSettings(
            font = newFont,
            versesMode = versesMode.value
        ) else RecitationSettings(
            font = newFont,
            versesMode = versesMode.value
        )
        uiEvents(SettingsEvents.SaveSettings(newSettings))
    }

    MyDialog(
        space = 16.dp,
        onDismissRequest = { uiEvents(SettingsEvents.OnBack) },
        modifier = Modifier
            .padding(16.dp)
            .verticalScroll(rememberScrollState()),
        header = {
            DialogHeader(
                text = stringResource(
                    if (reading) Res.string.settings_of_reading
                    else Res.string.settings_of_recitation
                ),
                textAlign = TextAlign.Center,
                icon = Icons.Default.Close,
                onBack = { uiEvents(SettingsEvents.OnBack) }
            )
        },
        footer = {
            DialogFooter(
                title = Res.string.settings_save,
                onClick = ::saveSettings
            )
        }
    ) {
        SectionDarkMode(
            enabled = darkModeEnabled,
            onToggle = { uiEvents(SettingsEvents.ToggleDarkMode) }
        )
        SectionVersesMode(
            versesMode = versesMode,
            allowContinuesMode = reading
        )
        SectionFontSize(fontSize)
        SectionFontType(fonts = fonts, fontType = fontType)
    }
}


@Composable
private fun SectionDarkMode(
    enabled: Boolean,
    onToggle: () -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        SettingsLabel(Res.string.settings_night)
        Spacer(modifier = Modifier.weight(1f))
        Switch(
            checked = enabled,
            colors = SwitchDefaults.colors(
                checkedThumbColor = Color.White,
                checkedTrackColor = MaterialTheme.colorScheme.secondary,
                uncheckedThumbColor = MaterialTheme.colorScheme.onPrimary,
                uncheckedTrackColor = Color.Transparent,
                uncheckedBorderColor = MaterialTheme.colorScheme.onPrimary
            ),
            onCheckedChange = { onToggle() }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun SectionFontSize(
    fontSize: MutableState<TextUnit>
) {
    val minFontSize = 20.sp
    val maxFontSize = 99.sp

    Column(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            SettingsLabel(Res.string.settings_font_size)
            Box(Modifier.weight(1f)) {
                Text(
                    "(",
                    modifier = Modifier.align(Alignment.CenterStart)
                )
                Slider(
                    value = fontSize.value.value,
                    onValueChange = { fontSize.value = it.sp },
                    valueRange = minFontSize.value..maxFontSize.value,
                    steps = ((maxFontSize.value - minFontSize.value) / 2 - 1).toInt(), // Optional: for discrete steps
                    modifier = Modifier.fillMaxWidth(),
                    colors = SliderDefaults.colors(
                        activeTickColor = Color.Transparent,
                        inactiveTickColor = Color.Transparent,
                        activeTrackColor = Color.Transparent,
                        inactiveTrackColor = Color.Transparent
                    ),
                    thumb = {
                        Surface(
                            shape = CircleShape,
                            color = MaterialTheme.colorScheme.onPrimary
                        ) {
                            Text(
                                text = " ${fontSize.value.value.toInt()}",
                                style = MaterialTheme.typography.labelMedium,
                                fontFamily = FontFamily.SansSerif,
                                color = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.padding(2.dp)
                            )
                        }
                    }
                )
                Text(
                    ")",
                    modifier = Modifier.align(Alignment.CenterEnd)
                )
            }

        }
        Spacer(modifier = Modifier.height(8.dp))
        MySurface(
            modifier = Modifier.fillMaxWidth()
                .height(200.dp),
            contentAlignment = Alignment.Center,
            color = MaterialTheme.colorScheme.surface.copy(
                alpha = 0.5f
            )
        ) {
            Text(
                text = "بِسْمِ اللَّهِ",
                color = MaterialTheme.colorScheme.onPrimary,
                fontFamily = hafsSmartFamily(),
                fontSize = fontSize.value,
                lineHeight = fontSize.value,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

@Composable
private fun SectionFontType(
    fontType: MutableState<AppFont>,
    fonts: List<AppFont>
) {

    Column(modifier = Modifier.fillMaxWidth()) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            SettingsLabel(Res.string.settings_font_type)
            Text(
                text = "بِسْمِ اللَّهِ الرَّحْمَنِ الرَّحِيمِ ",
                modifier = Modifier.weight(1f),
                fontFamily = FontFamily(Font(fontType.value.fontType)),
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onPrimary,
                textAlign = TextAlign.Center,
                fontSize = 25.sp
            )
        }
        Spacer(modifier = Modifier.height(8.dp))
        MySurfaceColumn(
            modifier = Modifier.fillMaxWidth().padding(8.dp),
            space = 0.dp,
            color = MaterialTheme.colorScheme.surface.copy(
                alpha = 0.5f
            )
        ) {
            fonts.forEach {
                Surface(
                    onClick = { fontType.value = it },
                    color = Color.Transparent,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    BasicText(
                        text = it.name,
                        modifier = Modifier.fillMaxWidth().padding(8.dp),
                        style = MaterialTheme.typography.bodySmall.copy(
                            textAlign = TextAlign.Start,
                            fontWeight = FontWeight.Medium,
                            color = if (it.id == fontType.value.id)
                                MaterialTheme.colorScheme.secondary
                            else
                                MaterialTheme.colorScheme.onPrimary,
                        ),
                        maxLines = 1
                    )
                }
            }
        }
    }
}

@Composable
private fun SectionVersesMode(
    versesMode: MutableState<VersesMode>,
    allowContinuesMode: Boolean
) {

    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        SettingsLabel(Res.string.settings_mode)
        ItemReadingMode(
            value = Res.string.settings_mode_single,
            icon = Res.drawable.single_verse,
            selected = versesMode.value == VersesMode.Single,
            onClick = { versesMode.value = VersesMode.Single }
        )
        ItemReadingMode(
            value = Res.string.settings_mode_multiple,
            icon = Res.drawable.multiple_verses,
            selected = versesMode.value == VersesMode.Multiple,
            onClick = { versesMode.value = VersesMode.Multiple }
        )
        if (allowContinuesMode) ItemReadingMode(
            value = Res.string.settings_mode_continues,
            icon = Res.drawable.continues_verses,
            selected = versesMode.value == VersesMode.Continues,
            onClick = { versesMode.value = VersesMode.Continues }
        )
    }
}

@Composable
private fun ItemReadingMode(
    value: StringResource,
    icon: DrawableResource,
    selected: Boolean,
    onClick: () -> Unit
) {
    MySurfaceRow(
        border = if (selected) BorderStroke(
            width = 1.dp,
            color = MaterialTheme.colorScheme.secondary
        ) else null,
        cornerRadius = 8.dp,
        modifier = Modifier.fillMaxWidth().padding(8.dp),
        onClick = onClick
    ) {
        Text(
            text = stringResource(value),
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onPrimary
        )
        Spacer(modifier = Modifier.weight(1f))
        Icon(
            painter = painterResource(icon),
            tint = MaterialTheme.colorScheme.onPrimary,
            contentDescription = "",
            modifier = Modifier.size(24.dp)
        )
    }
}


@Composable
fun SettingsLabel(value: StringResource) {
    Text(
        text = "۞ ${stringResource(value)}",
        style = MaterialTheme.typography.titleSmall,
        fontSize = 18.sp,
        fontWeight = FontWeight.Medium,
        color = MaterialTheme.colorScheme.onPrimary
    )
}


@Composable
private fun DialogFooter(
    title: StringResource,
    enabled: () -> Boolean = { true },
    modifier: Modifier = Modifier.padding(8.dp),
    onClick: () -> Unit
) {
    Box(modifier = modifier) {
        Button(
            onClick = onClick,
            shape = RoundedCornerShape(8.dp),
            modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.secondary,
                contentColor = MaterialTheme.colorScheme.onSecondary
            ),
            enabled = enabled()
        ) {
            Text(
                text = stringResource(title),
                style = MaterialTheme.typography.bodyLarge
            )
        }
    }
}
package com.example.playlistmaker.settings.ui

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.fragment.app.Fragment
import com.example.playlistmaker.R
import com.example.playlistmaker.ui.PlaylistMakerTheme
import com.example.playlistmaker.ui.YP_Blue
import com.example.playlistmaker.ui.YP_Gray
import com.example.playlistmaker.ui.YP_Light_Blue
import com.example.playlistmaker.ui.Light_Gray
import org.koin.androidx.viewmodel.ext.android.activityViewModel

class SettingsFragment : Fragment() {

    private val settingsViewModel: SettingsViewModel by activityViewModel()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return ComposeView(requireContext()).apply {
            setContent {
                val darkThemeEnabled by settingsViewModel.isDarkTheme.observeAsState(false)

                PlaylistMakerTheme(darkTheme = darkThemeEnabled) {
                    Surface(
                        modifier = Modifier.fillMaxSize(),
                        color = MaterialTheme.colorScheme.background
                    ) {
                        SettingsScreen(
                            isDarkTheme = darkThemeEnabled,
                            onThemeChanged = { checked ->
                                settingsViewModel.switchTheme(checked)
                            }
                        )
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    isDarkTheme: Boolean,
    onThemeChanged: (Boolean) -> Unit
) {
    val context = LocalContext.current

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = stringResource(R.string.Settings),
                        style = MaterialTheme.typography.titleLarge,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background,
                    titleContentColor = MaterialTheme.colorScheme.onSurface
                )
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            ThemeSwitch(
                checked = isDarkTheme,
                onCheckedChange = onThemeChanged
            )

            SettingsRow(
                label = stringResource(R.string.Share),
                icon = R.drawable.share,
                onClick = {
                    val shareIntent = Intent(Intent.ACTION_SEND).apply {
                        type = "text/plain"
                        putExtra(Intent.EXTRA_TEXT, context.getString(R.string.shareLink))
                    }
                    context.startActivity(Intent.createChooser(shareIntent, null))
                }
            )

            SettingsRow(
                label = stringResource(R.string.support),
                icon = R.drawable.support,
                onClick = {
                    val mailIntent = Intent(Intent.ACTION_SENDTO).apply {
                        data = Uri.parse("mailto:")
                        putExtra(Intent.EXTRA_EMAIL, arrayOf(context.getString(R.string.mailAgreement)))
                        putExtra(Intent.EXTRA_SUBJECT, context.getString(R.string.themeMail))
                        putExtra(Intent.EXTRA_TEXT, context.getString(R.string.mailSupport))
                    }
                    context.startActivity(mailIntent)
                }
            )

            SettingsRow(
                label = stringResource(R.string.Agreement),
                icon = R.drawable.arrow,
                onClick = {
                    val intent = Intent(Intent.ACTION_VIEW, Uri.parse(context.getString(R.string.AgreementUrl)))
                    context.startActivity(intent)
                }
            )
        }
    }
}

@Composable
fun ThemeSwitch(
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    val colors = SwitchDefaults.colors(
        checkedThumbColor = YP_Blue,
        uncheckedThumbColor = YP_Gray,
        checkedTrackColor = YP_Light_Blue,
        uncheckedTrackColor = Light_Gray,
        uncheckedBorderColor = androidx.compose.ui.graphics.Color.Transparent
    )

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 24.dp, start = 16.dp, end = 16.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = stringResource(R.string.Dark_theme),
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurface
        )

        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange,
            colors = colors
        )
    }
}

@Composable
fun SettingsRow(
    label: String,
    icon: Int? = null,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .heightIn(min = 61.dp)
            .clickable { onClick() }
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurface
        )

        if (icon != null) {
            CompositionLocalProvider(LocalContentColor provides MaterialTheme.colorScheme.onSurfaceVariant) {
                Icon(
                    painter = painterResource(id = icon),
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        }
    }
}

@Preview(showBackground = true, name = "Светлая тема")
@Composable
fun SettingsPreviewLight() {
    PlaylistMakerTheme(darkTheme = false) {
        Surface(color = MaterialTheme.colorScheme.background) {
            SettingsScreen(isDarkTheme = false, onThemeChanged = {})
        }
    }
}

@Preview(showBackground = true, name = "Темная тема")
@Composable
fun SettingsPreviewDark() {
    PlaylistMakerTheme(darkTheme = true) {
        Surface(color = MaterialTheme.colorScheme.background) {
            SettingsScreen(isDarkTheme = true, onThemeChanged = {})
        }
    }
}
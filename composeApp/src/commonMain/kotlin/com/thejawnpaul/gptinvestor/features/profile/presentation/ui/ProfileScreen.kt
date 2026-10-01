package com.thejawnpaul.gptinvestor.features.profile.presentation.ui

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Help
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.PrivacyTip
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import com.thejawnpaul.gptinvestor.Res
import com.thejawnpaul.gptinvestor.activity
import com.thejawnpaul.gptinvestor.appearance
import com.thejawnpaul.gptinvestor.are_you_sure_you_want_to_sign_out
import com.thejawnpaul.gptinvestor.cancel
import com.thejawnpaul.gptinvestor.features.investor.presentation.ui.ThemeDropdown
import com.thejawnpaul.gptinvestor.features.profile.presentation.state.ProfileUiState
import com.thejawnpaul.gptinvestor.features.profile.presentation.viewmodel.ProfileEvent
import com.thejawnpaul.gptinvestor.history
import com.thejawnpaul.gptinvestor.more
import com.thejawnpaul.gptinvestor.notifications
import com.thejawnpaul.gptinvestor.preferences
import com.thejawnpaul.gptinvestor.privacy
import com.thejawnpaul.gptinvestor.profile
import com.thejawnpaul.gptinvestor.queries
import com.thejawnpaul.gptinvestor.questions_count
import com.thejawnpaul.gptinvestor.saved
import com.thejawnpaul.gptinvestor.saved_picks
import com.thejawnpaul.gptinvestor.saved_tidbits
import com.thejawnpaul.gptinvestor.settings
import com.thejawnpaul.gptinvestor.sign_out
import com.thejawnpaul.gptinvestor.theme.GPTInvestorTheme
import com.thejawnpaul.gptinvestor.theme.LocalGPTInvestorColors
import com.thejawnpaul.gptinvestor.upgrade_cta_subtitle
import com.thejawnpaul.gptinvestor.upgrade_to_premium
import com.thejawnpaul.gptinvestor.watching
import org.jetbrains.compose.resources.stringResource

@Composable
fun ProfileScreen(state: ProfileUiState, onEvent: (ProfileEvent) -> Unit, modifier: Modifier = Modifier) {
    val customColors = LocalGPTInvestorColors.current

    Scaffold(modifier = modifier, contentWindowInsets = WindowInsets(0), topBar = {
        Text(
            text = stringResource(Res.string.profile),
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.SemiBold,
            modifier = Modifier.statusBarsPadding().fillMaxWidth().padding(start = 16.dp, end = 16.dp, bottom = 16.dp)
        )
    }) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                UserHeader(
                    userName = state.userName,
                    accentColor = customColors.accentColors.allAccent,
                    accentBgColor = customColors.accentColors.allAccent20
                )
            }

            if (!state.isPremiumUser) {
                item {
                    UpgradeToPremiumCard(
                        accentColor = customColors.accentColors.allAccent,
                        onClick = { onEvent(ProfileEvent.UpgradeToPremiumClicked) }
                    )
                }
            }

            item {
                StatsRow(
                    watchingCount = state.watchingCount,
                    savedCount = state.savedTidbitsCount,
                    queryCount = state.queryCount,
                    borderColor = customColors.utilColors.borderBright10,
                    secondaryTextColor = customColors.textColors.secondary50
                )
            }

            item {
                SectionHeader(
                    title = stringResource(Res.string.activity).uppercase(),
                    color = customColors.textColors.secondary50
                )
            }

            item {
                Surface(
                    shape = RoundedCornerShape(16.dp),
                    tonalElevation = 0.dp,
                    color = MaterialTheme.colorScheme.surface
                ) {
                    Column {
                        ProfileMenuRow(
                            icon = Icons.Filled.History,
                            label = stringResource(Res.string.history),
                            trailingText = stringResource(Res.string.questions_count, state.queryCount),
                            onClick = { onEvent(ProfileEvent.HistoryClicked) }
                        )
                        HorizontalDivider(modifier = Modifier)
                        ProfileMenuRow(
                            icon = Icons.Filled.Bookmark,
                            label = stringResource(Res.string.saved_picks),
                            trailingText = state.savedPicksCountString,
                            onClick = { onEvent(ProfileEvent.SavedPicksClicked) }
                        )
                        HorizontalDivider(modifier = Modifier)
                        ProfileMenuRow(
                            icon = Icons.Filled.Description,
                            label = stringResource(Res.string.saved_tidbits),
                            trailingText = state.savedTidbitsCountString,
                            onClick = { onEvent(ProfileEvent.SavedTidbitsClicked) }
                        )
                    }
                }
            }

            item {
                SectionHeader(
                    title = stringResource(Res.string.preferences).uppercase(),
                    color = customColors.textColors.secondary50
                )
            }

            item {
                Surface(
                    shape = RoundedCornerShape(16.dp),
                    tonalElevation = 0.dp,
                    color = MaterialTheme.colorScheme.surface
                ) {
                    Column {
                        ProfileMenuRow(
                            icon = Icons.Filled.Notifications,
                            label = stringResource(Res.string.notifications),
                            trailingText = if (state.isNotificationsEnabled) "On" else "Off",
                            onClick = { onEvent(ProfileEvent.NotificationsClicked) }
                        )
                        HorizontalDivider(modifier = Modifier)
                        var expanded by remember { mutableStateOf(false) }
                        Box(modifier = Modifier.fillMaxWidth()) {
                            ProfileMenuRow(
                                icon = Icons.Filled.Palette,
                                label = stringResource(Res.string.appearance),
                                trailingText = stringResource(state.currentTheme),
                                onClick = { expanded = true },
                                showChevron = true
                            )
                            ThemeDropdown(
                                modifier = Modifier.align(Alignment.CenterEnd),
                                expanded = expanded,
                                onExpandedChange = { expanded = it },
                                onClick = { onEvent(ProfileEvent.ChangeTheme(it)) },
                                options = state.themeList,
                                selectedOption = state.currentTheme
                            )
                        }
                        HorizontalDivider(modifier = Modifier)
                        ProfileMenuRow(
                            icon = Icons.Filled.PrivacyTip,
                            label = stringResource(Res.string.privacy),
                            onClick = { onEvent(ProfileEvent.PrivacyClicked) }
                        )
                    }
                }
            }

            item {
                SectionHeader(
                    title = stringResource(Res.string.settings).uppercase(),
                    color = customColors.textColors.secondary50
                )
            }

            item {
                Surface(
                    shape = RoundedCornerShape(16.dp),
                    tonalElevation = 0.dp,
                    color = MaterialTheme.colorScheme.surface
                ) {
                    ProfileMenuRow(
                        icon = Icons.AutoMirrored.Filled.Help,
                        label = stringResource(Res.string.settings),
                        showChevron = true,
                        onClick = { onEvent(ProfileEvent.SettingsClicked) }
                    )
                }
            }

            item {
                SectionHeader(
                    title = stringResource(Res.string.more).uppercase(),
                    color = customColors.textColors.secondary50
                )
            }

            item {
                Surface(
                    shape = RoundedCornerShape(16.dp),
                    tonalElevation = 0.dp,
                    color = MaterialTheme.colorScheme.surface
                ) {
                    ProfileMenuRow(
                        icon = Icons.AutoMirrored.Filled.Logout,
                        label = stringResource(Res.string.sign_out),
                        iconBgColor = MaterialTheme.colorScheme.errorContainer,
                        iconTint = MaterialTheme.colorScheme.error,
                        labelColor = MaterialTheme.colorScheme.error,
                        showChevron = false,
                        onClick = { onEvent(ProfileEvent.SignOutClicked) }
                    )
                }
            }

            item { Spacer(Modifier.height(8.dp)) }
        }
    }

    if (state.showSignOutConfirmation) {
        AlertDialog(
            onDismissRequest = { onEvent(ProfileEvent.DismissSignOutDialog) },
            title = { Text(stringResource(Res.string.sign_out)) },
            text = { Text(stringResource(Res.string.are_you_sure_you_want_to_sign_out)) },
            confirmButton = {
                TextButton(onClick = { onEvent(ProfileEvent.ConfirmSignOut) }) {
                    Text(stringResource(Res.string.sign_out), color = MaterialTheme.colorScheme.error)
                }
            },
            dismissButton = {
                TextButton(onClick = { onEvent(ProfileEvent.DismissSignOutDialog) }) {
                    Text(stringResource(Res.string.cancel))
                }
            }
        )
    }
}

@Composable
private fun UserHeader(userName: String, accentColor: Color, accentBgColor: Color, modifier: Modifier = Modifier) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier.fillMaxWidth()
    ) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .size(56.dp)
                .clip(CircleShape)
                .background(accentBgColor)
        ) {
            Text(
                text = userName.firstOrNull()?.uppercaseChar()?.toString() ?: "",
                style = MaterialTheme.typography.titleLarge,
                color = accentColor,
                fontWeight = FontWeight.Bold
            )
        }
        Spacer(Modifier.width(16.dp))
        Text(
            text = userName,
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.SemiBold
        )
    }
}

@Composable
private fun UpgradeToPremiumCard(accentColor: Color, onClick: () -> Unit, modifier: Modifier = Modifier) {
    Surface(
        shape = RoundedCornerShape(16.dp),
        color = accentColor,
        modifier = modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 20.dp)
        ) {
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .size(44.dp)
                    .clip(CircleShape)
                    .background(Color.White.copy(alpha = 0.15f))
            ) {
                Icon(
                    imageVector = Icons.Filled.AutoAwesome,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(22.dp)
                )
            }
            Spacer(Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = stringResource(Res.string.upgrade_to_premium),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
                Text(
                    text = stringResource(Res.string.upgrade_cta_subtitle),
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.White.copy(alpha = 0.85f)
                )
            }
            Icon(
                imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                contentDescription = null,
                tint = Color.White
            )
        }
    }
}

@Composable
private fun StatsRow(
    watchingCount: Int,
    savedCount: Int,
    queryCount: Int,
    borderColor: Color,
    secondaryTextColor: Color,
    modifier: Modifier = Modifier
) {
    Surface(
        shape = RoundedCornerShape(16.dp),
        color = MaterialTheme.colorScheme.surface,
        border = BorderStroke(1.dp, borderColor),
        modifier = modifier.fillMaxWidth()
    ) {
        Row(
            horizontalArrangement = Arrangement.SpaceEvenly,
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 20.dp)
        ) {
            StatItem(
                count = watchingCount,
                label = stringResource(Res.string.watching).uppercase(),
                secondaryTextColor = secondaryTextColor
            )
            StatItem(
                count = savedCount,
                label = stringResource(Res.string.saved).uppercase(),
                secondaryTextColor = secondaryTextColor
            )
            StatItem(
                count = queryCount,
                label = stringResource(Res.string.queries).uppercase(),
                secondaryTextColor = secondaryTextColor
            )
        }
    }
}

@Composable
private fun StatItem(count: Int, label: String, secondaryTextColor: Color, modifier: Modifier = Modifier) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier
    ) {
        Text(
            text = count.toString(),
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold
        )
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = secondaryTextColor
        )
    }
}

@Composable
private fun SectionHeader(title: String, color: Color, modifier: Modifier = Modifier) {
    Text(
        text = title,
        style = MaterialTheme.typography.labelSmall,
        color = color,
        fontWeight = FontWeight.Medium,
        modifier = modifier.padding(horizontal = 4.dp)
    )
}

@Composable
private fun ProfileMenuRow(
    icon: ImageVector,
    label: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    trailingText: String? = null,
    iconBgColor: Color = LocalGPTInvestorColors.current.utilColors.borderBright10,
    iconTint: Color = MaterialTheme.colorScheme.onSurfaceVariant,
    labelColor: Color = MaterialTheme.colorScheme.onSurface,
    showChevron: Boolean = true
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 14.dp)
    ) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .size(40.dp)
                .clip(RoundedCornerShape(size = 12.dp))
                .background(iconBgColor)
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = iconTint,
                modifier = Modifier.size(20.dp)
            )
        }
        Spacer(Modifier.width(14.dp))
        Text(
            text = label,
            style = MaterialTheme.typography.bodyLarge,
            color = labelColor,
            modifier = Modifier.weight(1f)
        )
        if (trailingText != null) {
            Text(
                text = trailingText,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(Modifier.width(4.dp))
        }
        if (showChevron) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@PreviewLightDark
@Composable
private fun ProfileScreenPreview() {
    GPTInvestorTheme {
        ProfileScreen(
            state = ProfileUiState(
                userName = "John Doe",
                queryCount = 42,
                showSignOutConfirmation = false
            ),
            onEvent = {}
        )
    }
}

@PreviewLightDark
@Composable
private fun ProfileScreenSignOutConfirmationPreview() {
    GPTInvestorTheme {
        ProfileScreen(
            state = ProfileUiState(
                userName = "John Doe",
                queryCount = 42,
                showSignOutConfirmation = true
            ),
            onEvent = {}
        )
    }
}

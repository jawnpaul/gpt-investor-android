package com.thejawnpaul.gptinvestor.features.profile.presentation.ui

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.thejawnpaul.gptinvestor.features.profile.presentation.state.ProfileUiState
import com.thejawnpaul.gptinvestor.features.profile.presentation.viewmodel.ProfileEvent
import com.thejawnpaul.gptinvestor.theme.LocalGPTInvestorColors

@Composable
fun ProfileScreen(
    state: ProfileUiState,
    onEvent: (ProfileEvent) -> Unit,
    modifier: Modifier = Modifier
) {
    val customColors = LocalGPTInvestorColors.current

    Scaffold(modifier = modifier) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item { Spacer(Modifier.height(8.dp)) }

            item {
                Text(
                    text = "Profile",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier.fillMaxWidth()
                )
            }

            item {
                UserHeader(
                    userName = state.userName,
                    accentColor = customColors.accentColors.allAccent,
                    accentBgColor = customColors.accentColors.allAccent20
                )
            }

            item {
                UpgradeToPremiumCard(
                    accentColor = customColors.accentColors.allAccent,
                    onClick = { onEvent(ProfileEvent.UpgradeToPremiumClicked) }
                )
            }

            item {
                StatsRow(
                    watchingCount = 0,
                    savedCount = 0,
                    queryCount = state.queryCount,
                    borderColor = customColors.utilColors.borderBright10,
                    secondaryTextColor = customColors.textColors.secondary50
                )
            }

            item {
                SectionHeader(
                    title = "ACTIVITY",
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
                            label = "History",
                            trailingText = "${state.queryCount} questions",
                            onClick = { onEvent(ProfileEvent.HistoryClicked) }
                        )
                        HorizontalDivider(modifier = Modifier.padding(horizontal = 16.dp))
                        ProfileMenuRow(
                            icon = Icons.Filled.Bookmark,
                            label = "Saved picks",
                            trailingText = "0",
                            onClick = { onEvent(ProfileEvent.SavedPicksClicked) }
                        )
                        HorizontalDivider(modifier = Modifier.padding(horizontal = 16.dp))
                        ProfileMenuRow(
                            icon = Icons.Filled.Description,
                            label = "Saved tidbits",
                            trailingText = "0",
                            onClick = { onEvent(ProfileEvent.SavedTidbitsClicked) }
                        )
                    }
                }
            }

            item {
                SectionHeader(
                    title = "PREFERENCES",
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
                            label = "Notifications",
                            trailingText = "On",
                            onClick = { onEvent(ProfileEvent.NotificationsClicked) }
                        )
                        HorizontalDivider(modifier = Modifier.padding(horizontal = 16.dp))
                        ProfileMenuRow(
                            icon = Icons.Filled.Palette,
                            label = "Appearance",
                            trailingText = "Light",
                            onClick = { onEvent(ProfileEvent.AppearanceClicked) }
                        )
                        HorizontalDivider(modifier = Modifier.padding(horizontal = 16.dp))
                        ProfileMenuRow(
                            icon = Icons.Filled.PrivacyTip,
                            label = "Privacy",
                            onClick = { onEvent(ProfileEvent.PrivacyClicked) }
                        )
                    }
                }
            }

            item {
                SectionHeader(
                    title = "MORE",
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
                        label = "Sign out",
                        iconBgColor = MaterialTheme.colorScheme.errorContainer,
                        iconTint = MaterialTheme.colorScheme.error,
                        labelColor = MaterialTheme.colorScheme.error,
                        showChevron = false,
                        onClick = { onEvent(ProfileEvent.SignOutClicked) }
                    )
                }
            }

            item {
                Text(
                    text = "GPT Investor · v2.4.0",
                    style = MaterialTheme.typography.labelSmall,
                    color = customColors.textColors.secondary50,
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp)
                )
            }

            item { Spacer(Modifier.height(8.dp)) }
        }
    }

    if (state.showSignOutConfirmation) {
        AlertDialog(
            onDismissRequest = { onEvent(ProfileEvent.DismissSignOutDialog) },
            title = { Text("Sign out") },
            text = { Text("Are you sure you want to sign out?") },
            confirmButton = {
                TextButton(onClick = { onEvent(ProfileEvent.ConfirmSignOut) }) {
                    Text("Sign out", color = MaterialTheme.colorScheme.error)
                }
            },
            dismissButton = {
                TextButton(onClick = { onEvent(ProfileEvent.DismissSignOutDialog) }) {
                    Text("Cancel")
                }
            }
        )
    }
}

@Composable
private fun UserHeader(
    userName: String,
    accentColor: Color,
    accentBgColor: Color,
    modifier: Modifier = Modifier
) {
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
private fun UpgradeToPremiumCard(
    accentColor: Color,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
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
                    text = "Upgrade to Premium",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
                Text(
                    text = "Unlimited watchlist, Thesis Tracker, and more",
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
            StatItem(count = watchingCount, label = "WATCHING", secondaryTextColor = secondaryTextColor)
            StatItem(count = savedCount, label = "SAVED", secondaryTextColor = secondaryTextColor)
            StatItem(count = queryCount, label = "QUERIES", secondaryTextColor = secondaryTextColor)
        }
    }
}

@Composable
private fun StatItem(
    count: Int,
    label: String,
    secondaryTextColor: Color,
    modifier: Modifier = Modifier
) {
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
private fun SectionHeader(
    title: String,
    color: Color,
    modifier: Modifier = Modifier
) {
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
    iconBgColor: Color = MaterialTheme.colorScheme.surfaceVariant,
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
                .clip(CircleShape)
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

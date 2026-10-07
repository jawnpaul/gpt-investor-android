package com.thejawnpaul.gptinvestor.features.profile.presentation.ui

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
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import com.thejawnpaul.gptinvestor.Res
import com.thejawnpaul.gptinvestor.baseline_bookmarks_24
import com.thejawnpaul.gptinvestor.features.investor.presentation.ui.ThemeDropdown
import com.thejawnpaul.gptinvestor.features.profile.presentation.viewmodel.ProfileEvent
import com.thejawnpaul.gptinvestor.features.profile.presentation.viewmodel.ProfileUiState
import com.thejawnpaul.gptinvestor.history
import com.thejawnpaul.gptinvestor.ic_logout
import com.thejawnpaul.gptinvestor.ic_profile
import com.thejawnpaul.gptinvestor.ic_saved_tidbit
import com.thejawnpaul.gptinvestor.ic_timer
import com.thejawnpaul.gptinvestor.profile
import com.thejawnpaul.gptinvestor.saved_picks
import com.thejawnpaul.gptinvestor.saved_tidbits
import com.thejawnpaul.gptinvestor.settings
import com.thejawnpaul.gptinvestor.sign_in_action
import com.thejawnpaul.gptinvestor.sign_in_to_save_stocks
import com.thejawnpaul.gptinvestor.sign_out
import com.thejawnpaul.gptinvestor.theme
import com.thejawnpaul.gptinvestor.theme.GPTInvestorTheme
import com.thejawnpaul.gptinvestor.theme.bodyChatBody
import com.thejawnpaul.gptinvestor.you_re_browsing_as_a_guest
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.resources.vectorResource

@Composable
fun ProfileScreen(state: ProfileUiState, onEvent: (ProfileEvent) -> Unit, modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .statusBarsPadding()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 16.dp)
    ) {
        Text(
            text = stringResource(Res.string.profile),
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 16.dp)
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(56.dp)
                    .clip(CircleShape)
                    .background(color = MaterialTheme.colorScheme.surfaceContainer),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = (state.user?.ifBlank { "A" }?.take(1) ?: "A").uppercase(),
                    style = MaterialTheme.typography.titleLarge,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }

            Text(
                text = when (state.user) {
                    "null" -> "Anonymous"
                    else -> state.user ?: "Anonymous"
                },
                style = MaterialTheme.typography.bodyChatBody,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                color = MaterialTheme.colorScheme.onSurface
            )
        }

        Spacer(modifier = Modifier.height(16.dp))
        HorizontalDivider()
        Spacer(modifier = Modifier.height(8.dp))

        if (state.isGuestSession) {
            Text(
                text = stringResource(Res.string.you_re_browsing_as_a_guest),
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurface
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = stringResource(Res.string.sign_in_to_save_stocks),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            ProfileItem(
                icon = vectorResource(Res.drawable.ic_profile),
                label = stringResource(Res.string.sign_in_action),
                onClick = { onEvent(ProfileEvent.GoToSignUp) }
            )
        } else {
            ProfileItem(
                icon = vectorResource(Res.drawable.ic_timer),
                label = stringResource(Res.string.history),
                onClick = { onEvent(ProfileEvent.GoToHistory) }
            )
            ProfileItem(
                icon = vectorResource(Res.drawable.baseline_bookmarks_24),
                label = stringResource(Res.string.saved_picks),
                onClick = { onEvent(ProfileEvent.GoToSavedPicks) }
            )
            ProfileItem(
                icon = vectorResource(Res.drawable.ic_saved_tidbit),
                label = stringResource(Res.string.saved_tidbits),
                onClick = { onEvent(ProfileEvent.GoToSavedTidbits) }
            )
            ProfileItem(
                icon = Icons.Default.Settings,
                label = stringResource(Res.string.settings),
                onClick = { onEvent(ProfileEvent.GoToSettings) }
            )
        }

        HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = stringResource(Res.string.theme),
                style = MaterialTheme.typography.bodyLarge
            )

            ThemeDropdown(
                modifier = Modifier,
                onClick = { onEvent(ProfileEvent.ChangeTheme(it)) },
                options = listOf("Light", "Dark", "System"),
                selectedOption = state.theme ?: "Dark"
            )
        }

        if (!state.isGuestSession) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onEvent(ProfileEvent.SignOut) }
                    .padding(vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = stringResource(Res.string.sign_out),
                    style = MaterialTheme.typography.bodyLarge
                )

                Icon(
                    painter = painterResource(Res.drawable.ic_logout),
                    contentDescription = null
                )
            }
        }
    }
}

@Composable
private fun ProfileItem(icon: ImageVector, label: String, onClick: () -> Unit, modifier: Modifier = Modifier) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = label,
            modifier = Modifier.size(24.dp),
            tint = MaterialTheme.colorScheme.onSurface
        )
        Spacer(modifier = Modifier.width(32.dp))
        Text(
            text = label,
            color = MaterialTheme.colorScheme.onSurface,
            style = MaterialTheme.typography.bodyLarge
        )
    }
}

@PreviewLightDark
@Composable
private fun ProfileScreenPreview() {
    GPTInvestorTheme {
        Surface {
            ProfileScreen(
                state = ProfileUiState(user = "John Doe", theme = "Light"),
                onEvent = {}
            )
        }
    }
}

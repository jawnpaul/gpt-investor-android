package com.thejawnpaul.gptinvestor.features.profile.presentation.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.Login
import androidx.compose.material.icons.automirrored.outlined.Logout
import androidx.compose.material.icons.outlined.Description
import androidx.compose.material.icons.outlined.Palette
import androidx.compose.material.icons.outlined.VerifiedUser
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewLightDark
import com.thejawnpaul.gptinvestor.Res
import com.thejawnpaul.gptinvestor.activity
import com.thejawnpaul.gptinvestor.appearance
import com.thejawnpaul.gptinvestor.cancel
import com.thejawnpaul.gptinvestor.create_an_account
import com.thejawnpaul.gptinvestor.features.investor.presentation.ui.AppearanceMenu
import com.thejawnpaul.gptinvestor.features.profile.presentation.state.ProfileUiState
import com.thejawnpaul.gptinvestor.features.profile.presentation.viewmodel.ProfileEvent
import com.thejawnpaul.gptinvestor.history
import com.thejawnpaul.gptinvestor.notifications
import com.thejawnpaul.gptinvestor.preferences
import com.thejawnpaul.gptinvestor.privacy
import com.thejawnpaul.gptinvestor.profile
import com.thejawnpaul.gptinvestor.profile_free_plan
import com.thejawnpaul.gptinvestor.profile_guest
import com.thejawnpaul.gptinvestor.profile_premium_plan
import com.thejawnpaul.gptinvestor.profile_questions
import com.thejawnpaul.gptinvestor.profile_see_premium
import com.thejawnpaul.gptinvestor.profile_upgrade_body
import com.thejawnpaul.gptinvestor.profile_upgrade_title
import com.thejawnpaul.gptinvestor.questions_count
import com.thejawnpaul.gptinvestor.saved
import com.thejawnpaul.gptinvestor.saved_picks
import com.thejawnpaul.gptinvestor.saved_tidbits
import com.thejawnpaul.gptinvestor.settings
import com.thejawnpaul.gptinvestor.settings_off
import com.thejawnpaul.gptinvestor.settings_on
import com.thejawnpaul.gptinvestor.sign_in_action
import com.thejawnpaul.gptinvestor.sign_in_to_save_stocks
import com.thejawnpaul.gptinvestor.sign_out
import com.thejawnpaul.gptinvestor.sign_out_body
import com.thejawnpaul.gptinvestor.sign_out_title
import com.thejawnpaul.gptinvestor.theme.GPTInvestorTheme
import com.thejawnpaul.gptinvestor.theme.GiEdge
import com.thejawnpaul.gptinvestor.theme.GiMotion
import com.thejawnpaul.gptinvestor.theme.GiRadius
import com.thejawnpaul.gptinvestor.theme.GiSpace
import com.thejawnpaul.gptinvestor.theme.GiTheme
import com.thejawnpaul.gptinvestor.theme.components.GiAvatar
import com.thejawnpaul.gptinvestor.theme.components.GiAvatarSize
import com.thejawnpaul.gptinvestor.theme.components.GiBeamStyle
import com.thejawnpaul.gptinvestor.theme.components.GiButton
import com.thejawnpaul.gptinvestor.theme.components.GiButtonSize
import com.thejawnpaul.gptinvestor.theme.components.GiButtonVariant
import com.thejawnpaul.gptinvestor.theme.components.GiDialog
import com.thejawnpaul.gptinvestor.theme.components.GiDivider
import com.thejawnpaul.gptinvestor.theme.components.GiHaptics
import com.thejawnpaul.gptinvestor.theme.components.GiIcons
import com.thejawnpaul.gptinvestor.theme.components.GiLargeTopBar
import com.thejawnpaul.gptinvestor.theme.components.GiProTag
import com.thejawnpaul.gptinvestor.theme.components.GiText
import com.thejawnpaul.gptinvestor.theme.components.GiTextStyle
import com.thejawnpaul.gptinvestor.theme.components.giBeam
import com.thejawnpaul.gptinvestor.watching
import kotlinx.coroutines.delay
import org.jetbrains.compose.resources.stringResource

private const val PREMIUM_BEAM_STRENGTH = 0.6f

private const val PREMIUM_PULSE_MS = 6_400L
private const val LARGE_TEXT_FONT_SCALE = 1.5f

@Composable
fun ProfileScreen(state: ProfileUiState, onEvent: (ProfileEvent) -> Unit, modifier: Modifier = Modifier) {
    val haptics = LocalHapticFeedback.current
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(GiTheme.colors.bg)
    ) {
        GiLargeTopBar(title = stringResource(Res.string.profile))
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .verticalScroll(rememberScrollState())
                .padding(start = GiSpace.gutter, end = GiSpace.gutter, top = GiSpace.x3, bottom = GiSpace.sectionGap),
            verticalArrangement = Arrangement.spacedBy(GiSpace.sectionGap)
        ) {
            if (state.isGuestSession) {
                GuestAccountCard(onSignIn = { onEvent(ProfileEvent.SignInClicked) })
            } else {
                UserHeader(userName = state.userName, isPremium = state.isPremiumUser)
                if (!state.isPremiumUser) {
                    PremiumCard(onClick = { onEvent(ProfileEvent.UpgradeToPremiumClicked) })
                }
                StatsRow(state = state, onEvent = onEvent)
                ActivitySection(state = state, onEvent = onEvent)
            }

            PreferencesSection(state = state, onEvent = onEvent)

            if (!state.isGuestSession) {
                AccountSection(title = stringResource(Res.string.settings)) {
                    AccountSettingsRow(
                        icon = GiIcons.gearSix,
                        label = stringResource(Res.string.settings),
                        onClick = { onEvent(ProfileEvent.SettingsClicked) }
                    )
                    GiDivider()
                    AccountSettingsRow(
                        icon = Icons.AutoMirrored.Outlined.Logout,
                        label = stringResource(Res.string.sign_out),
                        showCaret = false,
                        destructive = true,
                        onClick = { onEvent(ProfileEvent.SignOutClicked) }
                    )
                }
            }
        }
    }

    if (state.showSignOutConfirmation) {
        GiDialog(
            title = stringResource(Res.string.sign_out_title),
            body = stringResource(Res.string.sign_out_body),
            confirmText = stringResource(Res.string.sign_out),
            onConfirm = {
                haptics.performHapticFeedback(GiHaptics.Reject)
                onEvent(ProfileEvent.ConfirmSignOut)
            },
            dismissText = stringResource(Res.string.cancel),
            onDismiss = { onEvent(ProfileEvent.DismissSignOutDialog) },
            icon = Icons.AutoMirrored.Outlined.Logout,
            destructive = false
        )
    }
}

@Composable
private fun ActivitySection(state: ProfileUiState, onEvent: (ProfileEvent) -> Unit) {
    AccountSection(title = stringResource(Res.string.activity)) {
        AccountSettingsRow(
            icon = GiIcons.clock,
            label = stringResource(Res.string.history),
            value = stringResource(Res.string.questions_count, state.queryCount),
            onClick = { onEvent(ProfileEvent.HistoryClicked) }
        )
        GiDivider()
        AccountSettingsRow(
            icon = GiIcons.bookmarkSimple,
            label = stringResource(Res.string.saved_picks),
            value = state.savedPicksCountString,
            onClick = { onEvent(ProfileEvent.SavedPicksClicked) }
        )
        GiDivider()
        AccountSettingsRow(
            icon = Icons.Outlined.Description,
            label = stringResource(Res.string.saved_tidbits),
            value = state.savedTidbitsCountString,
            onClick = { onEvent(ProfileEvent.SavedTidbitsClicked) }
        )
    }
}

@Composable
private fun PreferencesSection(state: ProfileUiState, onEvent: (ProfileEvent) -> Unit) {
    var appearanceExpanded by remember { mutableStateOf(false) }
    AccountSection(title = stringResource(Res.string.preferences)) {
        AccountSettingsRow(
            icon = GiIcons.bell,
            label = stringResource(Res.string.notifications),
            value = stringResource(
                if (state.isNotificationsEnabled) Res.string.settings_on else Res.string.settings_off
            ),
            onClick = { onEvent(ProfileEvent.NotificationsClicked) }
        )
        GiDivider()
        Box(modifier = Modifier.fillMaxWidth()) {
            AccountSettingsRow(
                icon = Icons.Outlined.Palette,
                label = stringResource(Res.string.appearance),
                value = stringResource(state.currentTheme),
                highlighted = appearanceExpanded,
                onClick = { appearanceExpanded = true }
            )
            AppearanceMenu(
                expanded = appearanceExpanded,
                selectedOption = state.currentTheme,
                options = state.themeList,
                onSelect = { onEvent(ProfileEvent.ChangeTheme(it)) },
                onDismiss = { appearanceExpanded = false }
            )
        }
        GiDivider()
        AccountSettingsRow(
            icon = Icons.Outlined.VerifiedUser,
            label = stringResource(Res.string.privacy),
            onClick = { onEvent(ProfileEvent.PrivacyClicked) }
        )
    }
}

@Composable
private fun UserHeader(userName: String, isPremium: Boolean) {
    Row(
        modifier = Modifier.fillMaxWidth().semantics(mergeDescendants = true) {},
        horizontalArrangement = Arrangement.spacedBy(GiSpace.x3),
        verticalAlignment = Alignment.CenterVertically
    ) {
        GiAvatar(initials = userName.firstOrNull()?.uppercaseChar()?.toString(), size = GiAvatarSize.Large)
        Column(modifier = Modifier.weight(1f)) {
            GiText(text = userName, style = GiTextStyle.SectionTitle)
            GiText(
                text = stringResource(if (isPremium) Res.string.profile_premium_plan else Res.string.profile_free_plan),
                style = GiTextStyle.MetaRelaxed
            )
        }
    }
}

@Composable
private fun PremiumCard(onClick: () -> Unit) {
    var beamActive by rememberSaveable { mutableStateOf(true) }
    LaunchedEffect(Unit) {
        delay(PREMIUM_PULSE_MS)
        beamActive = false
    }
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .giBeam(
                active = beamActive,
                shape = GiRadius.md,
                strength = PREMIUM_BEAM_STRENGTH,
                style = GiBeamStyle.Pulse
            )
            .clip(GiRadius.md)
            .background(GiTheme.colors.violetSoft)
            .padding(horizontal = GiSpace.x4, vertical = GiSpace.x3),
        verticalArrangement = Arrangement.spacedBy(GiSpace.x3)
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(GiSpace.x1)) {
            GiProTag()
            GiText(text = stringResource(Res.string.profile_upgrade_title), style = GiTextStyle.SectionTitle)
        }
        GiText(text = stringResource(Res.string.profile_upgrade_body), style = GiTextStyle.BodySmall)
        GiButton(text = stringResource(Res.string.profile_see_premium), onClick = onClick, size = GiButtonSize.Small)
    }
}

@Composable
private fun StatsRow(state: ProfileUiState, onEvent: (ProfileEvent) -> Unit) {
    val tiles = @Composable { tileModifier: Modifier ->
        StatTile(label = stringResource(Res.string.watching), count = state.watchingCount, modifier = tileModifier)
        StatTile(
            label = stringResource(Res.string.saved),
            count = state.savedTidbitsCount,
            onClick = { onEvent(ProfileEvent.SavedTidbitsClicked) },
            modifier = tileModifier
        )
        StatTile(
            label = stringResource(Res.string.profile_questions),
            count = state.queryCount,
            onClick = { onEvent(ProfileEvent.HistoryClicked) },
            modifier = tileModifier
        )
    }
    if (isLargeText()) {
        Column(verticalArrangement = Arrangement.spacedBy(GiSpace.x3)) { tiles(Modifier.fillMaxWidth()) }
    } else {
        Row(horizontalArrangement = Arrangement.spacedBy(GiSpace.x3)) { tiles(Modifier.weight(1f)) }
    }
}

@Composable
private fun StatTile(label: String, count: Int, modifier: Modifier = Modifier, onClick: (() -> Unit)? = null) {
    val colors = GiTheme.colors
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    Column(
        modifier = modifier
            .clip(GiRadius.md)
            .background(colors.raised)
            .border(GiEdge.width, colors.line, GiRadius.md)
            .then(
                if (onClick != null) {
                    Modifier.clickable(
                        interactionSource = interactionSource,
                        indication = null,
                        role = Role.Button,
                        onClick = onClick
                    )
                } else {
                    Modifier
                }
            )
            .background(if (isPressed) colors.ink.copy(alpha = GiMotion.PRESSED_ALPHA) else Color.Transparent)
            .semantics(mergeDescendants = true) {}
            .padding(GiSpace.x3),
        verticalArrangement = Arrangement.spacedBy(GiSpace.x1)
    ) {
        GiText(text = label, style = GiTextStyle.Meta)
        Text(
            text = count.toString(),
            style = GiTheme.type.statNumber.merge(TextStyle(fontFeatureSettings = "tnum")),
            color = colors.ink,
            maxLines = 1
        )
    }
}

@Composable
private fun GuestAccountCard(onSignIn: () -> Unit) {
    val colors = GiTheme.colors
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(GiRadius.md)
            .background(colors.raised)
            .border(GiEdge.width, colors.line, GiRadius.md)
            .padding(GiSpace.cardPad),
        verticalArrangement = Arrangement.spacedBy(GiSpace.x4)
    ) {
        Row(
            modifier = Modifier.semantics(mergeDescendants = true) {},
            horizontalArrangement = Arrangement.spacedBy(GiSpace.x3),
            verticalAlignment = Alignment.CenterVertically
        ) {
            GiAvatar(initials = null, size = GiAvatarSize.Large)
            Column(modifier = Modifier.weight(1f)) {
                GiText(text = stringResource(Res.string.profile_guest), style = GiTextStyle.SectionTitle)
                GiText(text = stringResource(Res.string.sign_in_to_save_stocks), style = GiTextStyle.MetaRelaxed)
            }
        }
        Column(verticalArrangement = Arrangement.spacedBy(GiSpace.x1)) {
            GiButton(
                text = stringResource(Res.string.sign_in_action),
                onClick = onSignIn,
                modifier = Modifier.fillMaxWidth(),
                leadingIcon = Icons.AutoMirrored.Outlined.Login
            )
            GiButton(
                text = stringResource(Res.string.create_an_account),
                onClick = onSignIn,
                modifier = Modifier.fillMaxWidth(),
                variant = GiButtonVariant.Text
            )
        }
    }
}

@Composable
private fun isLargeText(): Boolean = LocalDensity.current.fontScale >= LARGE_TEXT_FONT_SCALE

@PreviewLightDark
@Preview(name = "200%", fontScale = 2f)
@Composable
private fun ProfileScreenPreview() {
    GPTInvestorTheme {
        ProfileScreen(
            state = ProfileUiState(
                userName = "Diogo",
                queryCount = 3,
                savedTidbitsCount = 2,
                isNotificationsEnabled = true
            ),
            onEvent = {}
        )
    }
}

@PreviewLightDark
@Composable
private fun ProfileScreenSignOutPreview() {
    GPTInvestorTheme {
        ProfileScreen(
            state = ProfileUiState(
                userName = "Diogo",
                queryCount = 3,
                showSignOutConfirmation = true
            ),
            onEvent = {
            }
        )
    }
}

@PreviewLightDark
@Composable
private fun GuestProfileScreenPreview() {
    GPTInvestorTheme {
        ProfileScreen(state = ProfileUiState(isGuestSession = true), onEvent = {})
    }
}

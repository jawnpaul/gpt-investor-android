package com.thejawnpaul.gptinvestor.core.navigation

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.thejawnpaul.gptinvestor.R
import com.thejawnpaul.gptinvestor.features.authentication.presentation.AuthenticationUIState
import com.thejawnpaul.gptinvestor.theme.LocalGPTInvestorColors
import com.thejawnpaul.gptinvestor.theme.bodyChatBody

@Composable
fun NavDrawerContent(onCloseDrawer: () -> Unit, onEvent: (NavDrawerEvent) -> Unit, onAction: (NavDrawerAction) -> Unit, state: AuthenticationUIState) {
    val gptInvestorColors = LocalGPTInvestorColors.current

    Column(
        modifier = Modifier
            .fillMaxHeight()
            .width(300.dp)
            .background(MaterialTheme.colorScheme.surface)
            .padding(16.dp)
    ) {
        // Header
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 24.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = stringResource(R.string.app_name),
                style = MaterialTheme.typography.headlineSmall
            )
        }

        HorizontalDivider()
        Spacer(modifier = Modifier.height(16.dp))

        // Drawer items
        DrawerItem(
            modifier = Modifier,
            icon = ImageVector.vectorResource(R.drawable.baseline_bookmarks_24),
            label = stringResource(R.string.saved_picks),
            onClick = {
                // Navigate to saved picks
                onAction(NavDrawerAction.OnGoToSavedPicks)
                onCloseDrawer()
            }
        )

        DrawerItem(
            modifier = Modifier,
            icon = Icons.Default.Settings,
            label = stringResource(R.string.settings),
            onClick = {
                onAction(NavDrawerAction.OnGoToSettings)
                onCloseDrawer()
            }
        )

        Spacer(modifier = Modifier.weight(1f))

        // Bottom section
        Column(
            modifier = Modifier.navigationBarsPadding()
        ) {
            var expanded by remember { mutableStateOf(false) }

            // Divider
            HorizontalDivider(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 16.dp),
                thickness = 2.dp
            )

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable(indication = null, interactionSource = null, onClick = {
                        expanded = !expanded
                    }),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    // Image
                    if (state.user != null) {
                        if (state.user.photoUrl != null) {
                            Surface(
                                modifier = Modifier.size(40.dp),
                                shape = CircleShape
                            ) {
                                AsyncImage(
                                    model = state.user.photoUrl,
                                    modifier = Modifier.fillMaxSize(),
                                    contentDescription = null,
                                    contentScale = ContentScale.Crop
                                )
                            }
                        } else {
                            Surface(
                                modifier = Modifier.size(40.dp),
                                shape = CircleShape
                            ) {
                                Text(text = state.user.displayName?.first().toString())
                            }
                        }
                    } else {
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .clip(CircleShape)
                                .background(color = MaterialTheme.colorScheme.surfaceContainer),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(text = "A")
                        }
                    }

                    // Text
                    Text(
                        text = state.user?.displayName ?: "Anonymous",
                        style = MaterialTheme.typography.bodyChatBody
                    )
                }

                IconButton(modifier = Modifier.size(24.dp), onClick = {
                    expanded = !expanded
                }) {
                    Icon(
                        imageVector = if (expanded) Icons.Default.KeyboardArrowDown else Icons.Default.KeyboardArrowUp,
                        contentDescription = null
                    )
                }
            }

            AnimatedVisibility(visible = expanded) {
                OutlinedCard(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 16.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable(indication = null, interactionSource = null, onClick = {
                                    onEvent(NavDrawerEvent.SignOut)
                                    onCloseDrawer()
                                }),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = stringResource(R.string.sign_out),
                                style = MaterialTheme.typography.labelLarge
                            )

                            Icon(
                                painter = painterResource(R.drawable.ic_logout),
                                contentDescription = null
                            )
                        }

                        HorizontalDivider(modifier = Modifier.fillMaxWidth(), thickness = 2.dp)

                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable(
                                    indication = null,
                                    interactionSource = null,
                                    onClick = {}
                                ),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = stringResource(R.string.delete_account),
                                style = MaterialTheme.typography.labelLarge,
                                color = gptInvestorColors.redColors.allRed
                            )
                            Icon(
                                painter = painterResource(R.drawable.ic_delete_profile),
                                contentDescription = null,
                                tint = gptInvestorColors.redColors.allRed
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun DrawerItem(modifier: Modifier, icon: ImageVector, label: String, onClick: () -> Unit) {
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
            modifier = Modifier.size(24.dp)
        )
        Spacer(modifier = Modifier.width(32.dp))
        Text(
            text = label,
            style = MaterialTheme.typography.bodyLarge
        )
    }
}

sealed interface NavDrawerEvent {
    data object SignOut : NavDrawerEvent
}

sealed interface NavDrawerAction {
    data object OnGoToSettings : NavDrawerAction
    data object OnGoToSavedPicks : NavDrawerAction
}

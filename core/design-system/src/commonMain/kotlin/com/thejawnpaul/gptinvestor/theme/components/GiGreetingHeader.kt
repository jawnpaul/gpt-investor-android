package com.thejawnpaul.gptinvestor.theme.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import com.thejawnpaul.gptinvestor.theme.GiRadius
import com.thejawnpaul.gptinvestor.theme.GiSpace
import com.thejawnpaul.gptinvestor.theme.GiTheme

// Home's top bar: optional date line over the greeting, avatar on the right linking to Profile.
@Composable
fun GiGreetingHeader(
    greeting: String,
    avatarInitials: String?,
    avatarContentDescription: String,
    onAvatarClick: () -> Unit,
    modifier: Modifier = Modifier,
    overline: String? = null
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(GiSpace.x3),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(2.dp)) {
            if (overline != null) {
                Text(text = overline, style = GiTheme.type.meta, color = GiTheme.colors.muted)
            }
            Text(
                text = greeting,
                style = GiTheme.type.screenTitle,
                color = GiTheme.colors.ink,
                maxLines = if (isLargeText()) 2 else 1,
                overflow = TextOverflow.Ellipsis
            )
        }
        GiAvatar(
            initials = avatarInitials,
            modifier = Modifier
                .clip(GiRadius.full)
                .clickable(role = Role.Button, onClick = onAvatarClick)
                .semantics { contentDescription = avatarContentDescription }
        )
    }
}

@PreviewLightDark
@Preview(name = "200%", fontScale = 2f)
@Composable
private fun GiGreetingHeaderPreview() {
    GiPreviewSurface {
        GiGreetingHeader(
            greeting = "Good afternoon, Diogo",
            avatarInitials = "D",
            avatarContentDescription = "Profile",
            onAvatarClick = {},
            overline = "Wednesday, 14 October"
        )
    }
}

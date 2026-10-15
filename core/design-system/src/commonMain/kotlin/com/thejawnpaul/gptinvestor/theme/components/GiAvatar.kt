package com.thejawnpaul.gptinvestor.theme.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.thejawnpaul.gptinvestor.theme.GiRadius
import com.thejawnpaul.gptinvestor.theme.GiTheme

@Composable
fun GiAvatar(initials: String?, modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .size(44.dp)
            .clip(GiRadius.full)
            .background(GiTheme.colors.violetSoft),
        contentAlignment = Alignment.Center
    ) {
        if (initials.isNullOrBlank()) {
            Icon(
                imageVector = GiIcons.user,
                contentDescription = null,
                modifier = Modifier.size(20.dp),
                tint = GiTheme.colors.violet
            )
        } else {
            Text(
                text = initials,
                style = GiTheme.type.companyName.copy(fontSize = 16.sp, lineHeight = 24.sp),
                color = GiTheme.colors.violet,
                maxLines = 1
            )
        }
    }
}

@PreviewLightDark
@Preview(name = "200%", fontScale = 2f)
@Composable
private fun GiAvatarPreview() {
    GiPreviewSurface {
        GiAvatar(initials = "AO")
    }
}

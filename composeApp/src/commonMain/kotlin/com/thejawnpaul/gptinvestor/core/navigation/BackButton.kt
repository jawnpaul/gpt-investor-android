package com.thejawnpaul.gptinvestor.core.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.thejawnpaul.gptinvestor.Res
import com.thejawnpaul.gptinvestor.back
import com.thejawnpaul.gptinvestor.theme.components.GiIconButton
import com.thejawnpaul.gptinvestor.theme.components.GiIcons
import org.jetbrains.compose.resources.stringResource

// Same back affordance as GiTopBar: caret-left in a 44 icon button on both platforms.
@Composable
fun BackButton(onClick: () -> Unit, modifier: Modifier = Modifier) {
    GiIconButton(
        icon = GiIcons.caretLeft,
        contentDescription = stringResource(Res.string.back),
        onClick = onClick,
        modifier = modifier
    )
}

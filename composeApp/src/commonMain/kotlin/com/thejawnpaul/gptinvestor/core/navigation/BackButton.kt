package com.thejawnpaul.gptinvestor.core.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import com.thejawnpaul.gptinvestor.Res
import com.thejawnpaul.gptinvestor.back
import com.thejawnpaul.gptinvestor.core.platform.isIos
import com.thejawnpaul.gptinvestor.ic_chevron_left
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.resources.vectorResource

@Composable
fun BackButton(onClick: () -> Unit, modifier: Modifier = Modifier) {
    val icon: ImageVector = if (isIos) {
        vectorResource(Res.drawable.ic_chevron_left)
    } else {
        Icons.AutoMirrored.Filled.ArrowBack
    }

    IconButton(onClick = onClick, modifier = modifier) {
        Icon(imageVector = icon, contentDescription = stringResource(Res.string.back))
    }
}

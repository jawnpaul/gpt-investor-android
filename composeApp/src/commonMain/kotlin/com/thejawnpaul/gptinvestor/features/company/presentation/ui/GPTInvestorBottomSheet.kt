package com.thejawnpaul.gptinvestor.features.company.presentation.ui

import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.BottomSheetDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.mohamedrejeb.calf.ui.sheet.AdaptiveBottomSheet
import com.mohamedrejeb.calf.ui.sheet.rememberAdaptiveSheetState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GptInvestorBottomSheet(
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier,
    isFullScreen: Boolean = false,
    skipPartiallyExpanded: Boolean = true,
    content: @Composable ColumnScope.() -> Unit
) {
    val sheetState = rememberAdaptiveSheetState(skipPartiallyExpanded = skipPartiallyExpanded)
    AdaptiveBottomSheet(
        containerColor = MaterialTheme.colorScheme.background,
        adaptiveSheetState = sheetState,
        modifier = modifier then if (isFullScreen) {
            Modifier.fillMaxSize()
        } else {
            Modifier
        },
        onDismissRequest = {
            onDismiss()
        },
        dragHandle = if (!isFullScreen) {
            {
                BottomSheetDefaults.DragHandle(
                    height = 4.dp,
                    width = 72.dp,
                    shape = RoundedCornerShape(30.dp)
                )
            }
        } else {
            null
        }
    ) {
        content()
    }
}

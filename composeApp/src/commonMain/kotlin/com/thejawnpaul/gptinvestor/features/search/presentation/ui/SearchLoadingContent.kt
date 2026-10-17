package com.thejawnpaul.gptinvestor.features.search.presentation.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.LiveRegionMode
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.liveRegion
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import com.thejawnpaul.gptinvestor.Res
import com.thejawnpaul.gptinvestor.discover_loading_search
import com.thejawnpaul.gptinvestor.theme.GiIconSize
import com.thejawnpaul.gptinvestor.theme.GiSpace
import com.thejawnpaul.gptinvestor.theme.components.GiDivider
import com.thejawnpaul.gptinvestor.theme.components.GiListGroup
import com.thejawnpaul.gptinvestor.theme.components.GiSkeleton
import org.jetbrains.compose.resources.stringResource

@Composable
fun SearchLoadingContent(modifier: Modifier = Modifier) {
    val loading = stringResource(Res.string.discover_loading_search)
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = GiSpace.gutter, vertical = GiSpace.sectionGap)
            .semantics {
                contentDescription = loading
                liveRegion = LiveRegionMode.Polite
            },
        verticalArrangement = Arrangement.spacedBy(GiSpace.titleToContent)
    ) {
        GiSkeleton(modifier = Modifier.size(width = 64.dp, height = GiSpace.x3).clearAndSetSemantics {})
        GiListGroup(modifier = Modifier.clearAndSetSemantics {}) {
            repeat(4) { index ->
                if (index > 0) GiDivider()
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(min = GiSpace.stockRow)
                        .padding(horizontal = GiSpace.x4, vertical = GiSpace.x3),
                    horizontalArrangement = Arrangement.spacedBy(GiSpace.x3),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    GiSkeleton(modifier = Modifier.size(GiIconSize.xl))
                    Column(verticalArrangement = Arrangement.spacedBy(GiSpace.x2)) {
                        GiSkeleton(modifier = Modifier.size(width = 140.dp, height = 14.dp))
                        GiSkeleton(modifier = Modifier.size(width = 56.dp, height = GiSpace.x2_5))
                    }
                }
            }
        }
    }
}

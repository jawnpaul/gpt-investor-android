package com.thejawnpaul.gptinvestor.features.discover

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.thejawnpaul.gptinvestor.R
import com.thejawnpaul.gptinvestor.theme.GPTInvestorTheme
import com.thejawnpaul.gptinvestor.theme.LocalGPTInvestorColors

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchBarCustom(modifier: Modifier, query: String, placeHolder: String, onQueryChange: (newQuery: String) -> Unit, onSearch: (query: String) -> Unit, onClose: () -> Unit) {
    val gptInvestorColors = LocalGPTInvestorColors.current

    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(corner = CornerSize(20.dp)),
        border = BorderStroke(
            2.dp,
            MaterialTheme.colorScheme.outlineVariant
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                modifier = Modifier.size(16.dp),
                painter = painterResource(R.drawable.ic_search),
                contentDescription = null
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                BasicTextField(
                    modifier = Modifier.weight(1F),
                    value = query,
                    onValueChange = onQueryChange,
                    maxLines = 1,
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Text,
                        imeAction = ImeAction.Search
                    ),
                    keyboardActions = KeyboardActions(
                        onSearch = {
                            onSearch(query)
                        }
                    ),
                    decorationBox = { innerTextField ->
                        Box {
                            if (query.trim().isBlank()) {
                                Text(
                                    text = placeHolder,
                                    style = MaterialTheme.typography.titleSmall,
                                    color = gptInvestorColors.textColors.secondary50
                                )
                            }
                            innerTextField()
                        }
                    },
                    textStyle = MaterialTheme.typography.bodyMedium.copy(
                        color = MaterialTheme.colorScheme.onSurface
                    ),
                    cursorBrush = SolidColor(MaterialTheme.colorScheme.onSurface)
                )

                Surface(
                    modifier = Modifier
                        .padding(start = 8.dp)
                        .size(16.dp),
                    shape = CircleShape,
                    border = BorderStroke(
                        width = 1.dp,
                        color = MaterialTheme.colorScheme.onSurface
                    ),
                    onClick = onClose
                ) {
                    Image(
                        painter = painterResource(R.drawable.outline_close_small_24),
                        contentDescription = null,
                        colorFilter = ColorFilter.tint(color = MaterialTheme.colorScheme.onSurface)
                    )
                }
            }
        }
    }
}

@Preview
@Composable
fun SearchPreview(modifier: Modifier = Modifier) {
    GPTInvestorTheme {
        SearchBarCustom(
            modifier = Modifier,
            query = "",
            onQueryChange = {},
            placeHolder = "Search any company",
            onSearch = {},
            onClose = {}
        )
    }
}

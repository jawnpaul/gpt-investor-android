package com.thejawnpaul.gptinvestor.features.discover

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.SearchBar
import androidx.compose.material3.SearchBarDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import com.thejawnpaul.gptinvestor.ui.theme.GPTInvestorTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchBarCustom(modifier: Modifier = Modifier, query: String, placeHolder: String, onQueryChange: (newQuery: String) -> Unit, onSearch: (query: String) -> Unit) {
    SearchBar(
        modifier = modifier,
        inputField = {
            SearchBarDefaults.InputField(
                query = query,
                onQueryChange = onQueryChange,
                onSearch = onSearch,
                expanded = false,
                onExpandedChange = { },
                placeholder = { Text(placeHolder, maxLines = 1, overflow = TextOverflow.Clip) },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) }
            )
        },
        expanded = false,
        onExpandedChange = { }
    ) {}
}

@Preview
@Composable
fun SearchPreview(modifier: Modifier = Modifier) {
    GPTInvestorTheme {
        SearchBarCustom(
            query = "",
            onQueryChange = {},
            placeHolder = "Search companies",
            onSearch = {}
        )
    }
}

package com.thejawnpaul.gptinvestor.features.investor.presentation.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.FilledIconButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.thejawnpaul.gptinvestor.R
import com.thejawnpaul.gptinvestor.ui.theme.GPTInvestorTheme

@Composable
fun InputBar(
    input: String,
    contentPadding: PaddingValues,
    sendEnabled: Boolean,
    onInputChanged: (String) -> Unit,
    onSendClick: () -> Unit,
    modifier: Modifier = Modifier,
    placeholder: String,
    shouldRequestFocus: Boolean = false
) {
    Column(modifier) {
        val focusRequester = remember { FocusRequester() }

        HorizontalDivider(modifier = Modifier.fillMaxWidth())

        LaunchedEffect(shouldRequestFocus) {
            if (shouldRequestFocus) {
                focusRequester.requestFocus()
            }
        }

        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            shape = RoundedCornerShape(corner = CornerSize(8.dp)),
            color = MaterialTheme.colorScheme.background
        ) {
            Row(
                modifier = Modifier
                    .padding(contentPadding)
                    .padding(end = 8.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                TextField(
                    value = input,
                    onValueChange = onInputChanged,
                    modifier = Modifier
                        .weight(1f)
                        .defaultMinSize(minHeight = 56.dp)
                        .focusRequester(focusRequester),
                    keyboardOptions = KeyboardOptions(
                        capitalization = KeyboardCapitalization.Sentences,
                        imeAction = ImeAction.Send
                    ),
                    keyboardActions = KeyboardActions(onSend = { onSendClick() }),
                    placeholder = {
                        Text(
                            text = placeholder,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    },
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = MaterialTheme.colorScheme.background,
                        unfocusedContainerColor = MaterialTheme.colorScheme.background,
                        focusedIndicatorColor = Color.Transparent,
                        unfocusedIndicatorColor = Color.Transparent,
                        disabledIndicatorColor = Color.Transparent
                    )
                )

                FilledIconButton(
                    onClick = onSendClick,
                    modifier = Modifier.size(40.dp),
                    enabled = sendEnabled
                ) {
                    Icon(
                        imageVector = ImageVector.vectorResource(R.drawable.send_icon),
                        contentDescription = null
                    )
                }
            }
        }
    }
}

@Preview
@Composable
fun InputBarPreview(modifier: Modifier = Modifier) {
    GPTInvestorTheme {
        Surface(modifier = Modifier.fillMaxSize()) {
            Column(modifier.fillMaxSize()) {
                InputBar(
                    input = "",
                    contentPadding = PaddingValues(0.dp),
                    sendEnabled = false,
                    onInputChanged = {},
                    onSendClick = {},
                    modifier = Modifier,
                    placeholder = "Ask anything about stocks"
                )
            }
        }
    }
}

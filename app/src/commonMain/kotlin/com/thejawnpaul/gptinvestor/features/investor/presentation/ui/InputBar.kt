package com.thejawnpaul.gptinvestor.features.investor.presentation.ui

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.thejawnpaul.gptinvestor.theme.GPTInvestorTheme
import com.thejawnpaul.gptinvestor.theme.bodyChatBody
import gptinvestor.app.generated.resources.Res
import gptinvestor.app.generated.resources.ic_send
import org.jetbrains.compose.resources.painterResource

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
    Box(
        modifier
            .fillMaxWidth()
            .padding(16.dp)
            .clip(RoundedCornerShape(16.dp))
            .border(
                width = 1.dp,
                brush = Brush.horizontalGradient(
                    colors = listOf(
                        Color(0xFFF947F6),
                        Color(0xFF0095FF)
                    )
                ),
                shape = RoundedCornerShape(16.dp)
            )
    ) {
        Column(Modifier) {
            val focusRequester = remember { FocusRequester() }

            HorizontalDivider(modifier = Modifier.fillMaxWidth())

            LaunchedEffect(shouldRequestFocus) {
                if (shouldRequestFocus) {
                    focusRequester.requestFocus()
                }
            }

            Surface(
                modifier = Modifier
                    .fillMaxWidth(),
                shape = RoundedCornerShape(corner = CornerSize(16.dp)),
                color = MaterialTheme.colorScheme.background
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
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
                                overflow = TextOverflow.Ellipsis,
                                style = MaterialTheme.typography.bodyMedium
                            )
                        },
                        colors = TextFieldDefaults.colors(
                            focusedContainerColor = Color.Transparent,
                            unfocusedContainerColor = Color.Transparent,
                            focusedIndicatorColor = Color.Transparent,
                            unfocusedIndicatorColor = Color.Transparent,
                            disabledIndicatorColor = Color.Transparent
                        ),
                        textStyle = MaterialTheme.typography.bodyChatBody
                    )

                    IconButton(onClick = onSendClick) {
                        Icon(
                            painter = painterResource(Res.drawable.ic_send),
                            contentDescription = "Send"
                        )
                    }
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

package com.thejawnpaul.gptinvestor.features.investor.presentation.ui.component

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import com.thejawnpaul.gptinvestor.Res
import com.thejawnpaul.gptinvestor.baseline_arrow_upward_24
import com.thejawnpaul.gptinvestor.input_logo
import com.thejawnpaul.gptinvestor.theme.GPTInvestorTheme
import com.thejawnpaul.gptinvestor.theme.LocalGPTInvestorColors
import com.thejawnpaul.gptinvestor.theme.bodyChatBody
import org.jetbrains.compose.resources.painterResource

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun QuestionInput(
    onSendClick: () -> Unit,
    modifier: Modifier = Modifier,
    text: String = "",
    hint: String = "",
    onTextChange: (String) -> Unit = {}
) {
    val gptInvestorColors = LocalGPTInvestorColors.current

    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(16.dp)
            .clip(RoundedCornerShape(28.dp))
            .background(color = MaterialTheme.colorScheme.surface)
            .border(
                width = 1.dp,
                color = gptInvestorColors.utilColors.borderBright10,
                shape = RoundedCornerShape(28.dp)
            )
            .padding(start = 12.dp, end = 8.dp, top = 4.dp, bottom = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Image(
            modifier = Modifier.size(18.dp),
            painter = painterResource(Res.drawable.input_logo),
            colorFilter = ColorFilter.tint(color = MaterialTheme.colorScheme.primary),
            contentDescription = null
        )

        TextField(
            value = text,
            onValueChange = onTextChange,
            modifier = Modifier
                .weight(1f)
                .defaultMinSize(minHeight = 48.dp),
            keyboardOptions = KeyboardOptions(
                capitalization = KeyboardCapitalization.Sentences,
                imeAction = ImeAction.Send
            ),
            keyboardActions = KeyboardActions(onSend = { onSendClick() }),
            placeholder = {
                Text(
                    text = hint,
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

        Surface(
            shape = CircleShape,
            color = MaterialTheme.colorScheme.tertiary
        ) {
            IconButton(
                modifier = Modifier,
                onClick = onSendClick
            ) {
                Icon(
                    painter = painterResource(Res.drawable.baseline_arrow_upward_24),
                    contentDescription = "Send"
                )
            }
        }
    }
}

@PreviewLightDark
@Composable
private fun QuestionInputPreview() {
    GPTInvestorTheme {
        Surface {
            QuestionInput(
                onSendClick = {},
                modifier = Modifier,
                text = "Lorem ipsum di color amet lorem lorem color ",
                hint = "Ask a follow-up question..."
            )
        }
    }
}

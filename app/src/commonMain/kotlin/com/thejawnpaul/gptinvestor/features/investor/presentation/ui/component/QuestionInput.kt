package com.thejawnpaul.gptinvestor.features.investor.presentation.ui.component

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import com.thejawnpaul.gptinvestor.features.conversation.domain.model.AnotherModel
import com.thejawnpaul.gptinvestor.features.conversation.domain.model.AvailableModel
import com.thejawnpaul.gptinvestor.features.conversation.domain.model.DefaultModel
import com.thejawnpaul.gptinvestor.theme.GPTInvestorTheme
import com.thejawnpaul.gptinvestor.theme.LocalGPTInvestorColors
import com.thejawnpaul.gptinvestor.theme.bodyChatBody
import gptinvestor.app.generated.resources.Res
import gptinvestor.app.generated.resources.ic_arrow_down
import gptinvestor.app.generated.resources.ic_send
import gptinvestor.app.generated.resources.input_logo
import gptinvestor.app.generated.resources.outline_close_small_24
import gptinvestor.app.generated.resources.select_model
import gptinvestor.app.generated.resources.upgrade
import gptinvestor.app.generated.resources.waitlisted
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun QuestionInput(
    modifier: Modifier,
    text: String = "",
    hint: String = "",
    onTextChange: (String) -> Unit = {},
    onSendClicked: () -> Unit,
    availableModels: List<AvailableModel> = emptyList(),
    selectedModel: AvailableModel = DefaultModel(),
    onModelChange: (AvailableModel) -> Unit = {}
) {
    val gptInvestorColors = LocalGPTInvestorColors.current

    Box(
        modifier = modifier
            .fillMaxWidth()
            .padding(16.dp)
            .clip(RoundedCornerShape(16.dp))
            .background(color = MaterialTheme.colorScheme.surface)
            .border(
                width = 2.dp,
                color = gptInvestorColors.utilColors.borderBright10,
                shape = RoundedCornerShape(16.dp)
            )

    ) {
        Column(
            modifier = Modifier
                .padding(start = 16.dp, end = 16.dp, bottom = 16.dp)
                .fillMaxWidth()
        ) {
            Row(
                modifier = Modifier.fillMaxWidth()
            ) {
                TextField(
                    value = text,
                    onValueChange = onTextChange,
                    modifier = Modifier
                        .weight(1f)
                        .defaultMinSize(minHeight = 56.dp),
                    keyboardOptions = KeyboardOptions(
                        capitalization = KeyboardCapitalization.Sentences,
                        imeAction = ImeAction.Send
                    ),
                    keyboardActions = KeyboardActions(onSend = { onSendClicked() }),
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
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Surface(
                    color = gptInvestorColors.utilColors.borderBright10,
                    shape = RoundedCornerShape(20.dp)
                ) {
                    // Model list drop down
                    ModelListDropDown(
                        modifier = Modifier,
                        options = availableModels,
                        selectedOption = selectedModel,
                        onOptionSelected = onModelChange
                    )
                }

                IconButton(onClick = {
                    onSendClicked()
                }) {
                    Icon(
                        painter = painterResource(Res.drawable.ic_send),
                        contentDescription = "Send"
                    )
                }
            }
        }
    }
}

@Composable
private fun ModelListDropDown(modifier: Modifier, options: List<AvailableModel>, selectedOption: AvailableModel, onOptionSelected: (AvailableModel) -> Unit) {
    var expanded by remember { mutableStateOf(false) }

    Box(
        modifier = modifier,
        contentAlignment = Alignment.Center
    ) {
        val gptInvestorColors = LocalGPTInvestorColors.current

        Row(
            modifier = Modifier
                .padding(12.dp)
                .clickable(indication = null, interactionSource = null, onClick = {
                    expanded = !expanded
                }),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // Image
            Image(
                modifier = Modifier.size(16.dp),
                painter = painterResource(Res.drawable.input_logo),
                colorFilter = ColorFilter.tint(color = MaterialTheme.colorScheme.onSurface),
                contentDescription = null
            )

            // Text
            Text(
                text = selectedOption.modelTitle,
                style = MaterialTheme.typography.labelMedium
            )

            Image(
                painter = painterResource(Res.drawable.ic_arrow_down),
                contentDescription = null,
                colorFilter = ColorFilter.tint(color = MaterialTheme.colorScheme.onSurface)
            )
        }

        DropdownMenu(
            modifier = Modifier
                .defaultMinSize(minWidth = 272.dp)
                .background(
                    color = gptInvestorColors.utilColors.borderBright10
                ),
            expanded = expanded,
            onDismissRequest = { expanded = false },
            offset = DpOffset(0.dp, 70.dp),
            shape = RoundedCornerShape(20.dp)
        ) {
            Column {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = stringResource(Res.string.select_model),
                        style = MaterialTheme.typography.labelMedium,
                        color = gptInvestorColors.textColors.secondary50
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
                        onClick = { expanded = !expanded }
                    ) {
                        Image(
                            painter = painterResource(Res.drawable.outline_close_small_24),
                            contentDescription = null,
                            colorFilter = ColorFilter.tint(color = MaterialTheme.colorScheme.onSurface)
                        )
                    }
                }
            }

            options.forEach { model ->
                SingleModelItem(
                    modifier = Modifier.padding(start = 16.dp),
                    model = model,
                    onModelChange = {
                        onOptionSelected(it)
                        expanded = false
                    }
                )
                Spacer(modifier = Modifier.size(8.dp))
            }
        }
    }
}

@Composable
fun SingleModelItem(modifier: Modifier, model: AvailableModel, onModelChange: (AvailableModel) -> Unit) {
    val gptInvestorColors = LocalGPTInvestorColors.current

    when (model) {
        is DefaultModel -> {
            Column(
                modifier = modifier
                    .fillMaxWidth()
                    .clickable(indication = null, interactionSource = null, onClick = {
                        onModelChange(model)
                    }),
                horizontalAlignment = Alignment.Start,
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text(text = model.modelTitle, style = MaterialTheme.typography.bodyMedium)
                Text(
                    text = model.modelSubtitle,
                    style = MaterialTheme.typography.bodySmall,
                    color = gptInvestorColors.textColors.secondary50
                )
            }
        }

        is AnotherModel -> {
            Column(
                modifier = modifier
                    .fillMaxWidth()
                    .clickable(indication = null, interactionSource = null, onClick = {
                        onModelChange(model)
                    }),
                horizontalAlignment = Alignment.Start,
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(text = model.modelTitle, style = MaterialTheme.typography.bodyMedium)
                    if (model.canUpgrade) {
                        Surface(
                            shape = RoundedCornerShape(20.dp),
                            border = BorderStroke(
                                width = 1.dp,
                                brush = Brush.horizontalGradient(
                                    colors = listOf(
                                        Color(0xFFF947F6),
                                        Color(0xFF0095FF)
                                    )
                                )
                            ),
                            color = MaterialTheme.colorScheme.onSurface
                        ) {
                            Text(
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                text = stringResource(Res.string.upgrade),
                                style = MaterialTheme.typography.labelMedium.copy(
                                    brush = Brush.horizontalGradient(
                                        colors = listOf(
                                            Color(0xFFF947F6),
                                            Color(0xFF0095FF)
                                        )
                                    )
                                )
                            )
                        }
                    }

                    if (model.isUserOnWaitlist == true) {
                        Text(
                            text = stringResource(Res.string.waitlisted),
                            style = MaterialTheme.typography.bodySmall.copy(
                                brush = Brush.horizontalGradient(
                                    colors = listOf(
                                        Color(0xFFF947F6),
                                        Color(0xFF0095FF)
                                    )
                                )
                            )
                        )
                    }
                }
                Text(
                    text = model.modelSubtitle,
                    style = MaterialTheme.typography.bodySmall,
                    color = gptInvestorColors.textColors.secondary50
                )
            }
        }
    }
}

@PreviewLightDark()
@Composable
fun QuestionInputPreview() {
    GPTInvestorTheme {
        Surface {
            QuestionInput(
                onSendClicked = {},
                modifier = Modifier,
                hint = "Ask me a question about stocks"
            )
        }
    }
}

package com.thejawnpaul.gptinvestor.features.conversation.presentation.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.ime
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.union
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewLightDark
import com.thejawnpaul.gptinvestor.Res
import com.thejawnpaul.gptinvestor.ask_me_anything_about_markets
import com.thejawnpaul.gptinvestor.back
import com.thejawnpaul.gptinvestor.features.conversation.domain.model.AvailableModel
import com.thejawnpaul.gptinvestor.features.conversation.domain.model.DefaultConversation
import com.thejawnpaul.gptinvestor.features.conversation.domain.model.DefaultModel
import com.thejawnpaul.gptinvestor.features.conversation.domain.model.DefaultPrompt
import com.thejawnpaul.gptinvestor.features.guest.presentation.TopGuestLabel
import com.thejawnpaul.gptinvestor.send
import com.thejawnpaul.gptinvestor.theme.GPTInvestorTheme
import com.thejawnpaul.gptinvestor.theme.GiIconSize
import com.thejawnpaul.gptinvestor.theme.GiSpace
import com.thejawnpaul.gptinvestor.theme.GiTheme
import com.thejawnpaul.gptinvestor.theme.components.GiAskComposer
import com.thejawnpaul.gptinvestor.theme.components.GiComposerBar
import com.thejawnpaul.gptinvestor.theme.components.GiDivider
import com.thejawnpaul.gptinvestor.theme.components.GiEyebrow
import com.thejawnpaul.gptinvestor.theme.components.GiIconTile
import com.thejawnpaul.gptinvestor.theme.components.GiIconTileSize
import com.thejawnpaul.gptinvestor.theme.components.GiIconTileTone
import com.thejawnpaul.gptinvestor.theme.components.GiIcons
import com.thejawnpaul.gptinvestor.theme.components.GiListGroup
import com.thejawnpaul.gptinvestor.theme.components.GiListRow
import com.thejawnpaul.gptinvestor.theme.components.GiText
import com.thejawnpaul.gptinvestor.theme.components.GiTextStyle
import com.thejawnpaul.gptinvestor.theme.components.GiTopBar
import com.thejawnpaul.gptinvestor.try_asking
import com.thejawnpaul.gptinvestor.what_do_you_want_to_know
import org.jetbrains.compose.resources.stringResource

private const val VISIBLE_PROMPTS = 3
private const val COMPOSER_LARGE_TEXT_LINES = 3

@Composable
fun DefaultConversationScreen(
    conversation: DefaultConversation,
    isGuest: Boolean,
    onPromptClick: (prompt: DefaultPrompt) -> Unit,
    onNavigateUp: () -> Unit,
    inputQuery: String,
    onInputQueryChange: (String) -> Unit,
    onSendClick: () -> Unit,
    onSignUpClick: () -> Unit,
    availableModels: List<AvailableModel>,
    selectedModel: AvailableModel,
    onModelChange: (AvailableModel) -> Unit,
    onUpgradeModel: (showBottomSheet: Boolean, modelId: String) -> Unit,
    modifier: Modifier = Modifier
) {
    val keyboardController = LocalSoftwareKeyboardController.current
    val prompts = remember(conversation.prompts) { conversation.prompts.take(VISIBLE_PROMPTS) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(GiTheme.colors.bg)
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            GiTopBar(
                title = null,
                onBack = onNavigateUp,
                backContentDescription = stringResource(Res.string.back),
                scrolled = !isGuest
            )
            if (isGuest) {
                TopGuestLabel(
                    onClick = onSignUpClick,
                    modifier = Modifier
                        .padding(horizontal = GiSpace.gutter)
                        .padding(bottom = GiSpace.x3)
                )
                GiDivider()
            }
        }

        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth(),
            contentAlignment = Alignment.Center
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = GiSpace.gutter, vertical = GiSpace.x6),
                verticalArrangement = Arrangement.spacedBy(GiSpace.sectionGap),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Column(
                    verticalArrangement = Arrangement.spacedBy(GiSpace.x4),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    GiIconTile(icon = GiIcons.mark, size = GiIconTileSize.Large, tone = GiIconTileTone.Violet)
                    GiText(
                        text = stringResource(Res.string.what_do_you_want_to_know),
                        modifier = Modifier.semantics { heading() },
                        style = GiTextStyle.PageTitle,
                        textAlign = TextAlign.Center
                    )
                }
                if (prompts.isNotEmpty()) {
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        verticalArrangement = Arrangement.spacedBy(GiSpace.titleToContent)
                    ) {
                        GiEyebrow(text = stringResource(Res.string.try_asking))
                        GiListGroup {
                            prompts.forEachIndexed { index, prompt ->
                                if (index > 0) GiDivider()
                                GiListRow(
                                    title = prompt.title,
                                    onClick = { onPromptClick(prompt) },
                                    leading = {
                                        GiIconTile(icon = GiIcons.sparkle, size = GiIconTileSize.Small)
                                    },
                                    trailing = {
                                        Icon(
                                            imageVector = GiIcons.caretRight,
                                            contentDescription = null,
                                            modifier = Modifier.size(GiIconSize.sm),
                                            tint = GiTheme.colors.muted
                                        )
                                    }
                                )
                            }
                        }
                    }
                }
            }
        }

        GiComposerBar(
            modifier = Modifier.windowInsetsPadding(WindowInsets.navigationBars.union(WindowInsets.ime)),
            docked = true
        ) {
            GiAskComposer(
                value = inputQuery,
                onValueChange = onInputQueryChange,
                onSend = {
                    keyboardController?.hide()
                    onSendClick()
                },
                placeholder = stringResource(Res.string.ask_me_anything_about_markets),
                sendContentDescription = stringResource(Res.string.send),
                floating = false,
                largeTextMaxLines = COMPOSER_LARGE_TEXT_LINES
            )
        }
    }
}

@PreviewLightDark
@Preview(name = "200%", fontScale = 2f)
@Composable
private fun DefaultConversationScreenPreview() {
    GPTInvestorTheme {
        DefaultConversationScreen(
            conversation = DefaultConversation(
                prompts = listOf(
                    DefaultPrompt(title = "What's moving today?", query = "What's moving today?"),
                    DefaultPrompt(title = "Explain P/E ratio", query = "Explain P/E ratio")
                )
            ),
            isGuest = true,
            onPromptClick = {},
            onNavigateUp = {},
            inputQuery = "",
            onInputQueryChange = {},
            onSendClick = {},
            onSignUpClick = {},
            availableModels = emptyList(),
            selectedModel = DefaultModel(),
            onModelChange = {},
            onUpgradeModel = { _, _ -> }
        )
    }
}

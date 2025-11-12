package com.thejawnpaul.gptinvestor.features.conversation.presentation.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.ime
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.unit.dp
import com.thejawnpaul.gptinvestor.features.conversation.domain.model.AvailableModel
import com.thejawnpaul.gptinvestor.features.conversation.domain.model.DefaultConversation
import com.thejawnpaul.gptinvestor.features.conversation.domain.model.DefaultPrompt
import com.thejawnpaul.gptinvestor.features.investor.presentation.ui.component.QuestionInput
import gptinvestor.app.generated.resources.Res
import gptinvestor.app.generated.resources.ask_anything_about_stocks
import gptinvestor.app.generated.resources.back
import org.jetbrains.compose.resources.stringResource

@Composable
fun DefaultConversationScreen(
    modifier: Modifier,
    conversation: DefaultConversation,
    onPromptClicked: (prompt: DefaultPrompt) -> Unit,
    onNavigateUp: () -> Unit,
    inputQuery: String,
    onInputQueryChanged: (String) -> Unit,
    onSendClick: () -> Unit,
    availableModels: List<AvailableModel>,
    selectedModel: AvailableModel,
    onModelChange: (AvailableModel) -> Unit,
    onUpgradeModel: (showBottomSheet: Boolean, modelId: String) -> Unit
) {
    val keyboardController = LocalSoftwareKeyboardController.current

    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onNavigateUp) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Default.ArrowBack,
                        contentDescription = stringResource(Res.string.back)
                    )
                }
            }
        },
        bottomBar = {
            QuestionInput(
                modifier = Modifier
                    .fillMaxWidth()
                    .windowInsetsPadding(
                        insets = WindowInsets.ime
                    ),
                onSendClicked = {
                    keyboardController?.hide()
                    onSendClick()
                },
                hint = stringResource(
                    Res.string.ask_anything_about_stocks
                ),
                onTextChange = { input ->
                    onInputQueryChanged(input)
                },
                text = inputQuery,
                availableModels = availableModels,
                selectedModel = selectedModel,
                onModelChange = {
                    if (it.canUpgrade) {
                        onUpgradeModel.invoke(true, it.modelId)
                        return@QuestionInput
                    }
                    onModelChange(it)
                }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
        ) {
            HorizontalDivider(modifier = Modifier.fillMaxWidth())

            Spacer(modifier = Modifier.weight(0.8f))

            DefaultPrompts(
                modifier = Modifier
                    .weight(1.2f)
                    .padding(horizontal = 8.dp)
                    .align(Alignment.CenterHorizontally),
                prompts = conversation.prompts,
                onClick = onPromptClicked
            )
        }
    }
}

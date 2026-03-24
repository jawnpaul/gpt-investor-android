package com.thejawnpaul.gptinvestor.features.authentication.presentation

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import com.thejawnpaul.gptinvestor.Res
import com.thejawnpaul.gptinvestor.continue_as_guest
import com.thejawnpaul.gptinvestor.features.component.GPTInvestorButton
import com.thejawnpaul.gptinvestor.gpt_investor
import com.thejawnpaul.gptinvestor.ic_logo
import com.thejawnpaul.gptinvestor.mesh_background
import com.thejawnpaul.gptinvestor.sign_up
import com.thejawnpaul.gptinvestor.theme.linkMedium
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource

@Composable
fun DefaultAuthenticationScreen(
    onEvent: (DefaultAuthenticationEvent) -> Unit,
    modifier: Modifier = Modifier,
    loading: Boolean = false
) {
    Scaffold(modifier = modifier) {
        Box(modifier = Modifier.fillMaxSize()) {
            if (loading) {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
            }

            // Wavy Background Image (Bottom)
            Image(
                painter = painterResource(Res.drawable.mesh_background),
                contentDescription = null,
                contentScale = ContentScale.Fit,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 160.dp)
                    .fillMaxHeight(0.4f)
                    .align(Alignment.BottomCenter)
                    .alpha(0.5f)

            )

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Spacer(modifier = Modifier.weight(1.3f))

                Box(contentAlignment = Alignment.Center) {
                    Image(
                        painter = painterResource(Res.drawable.ic_logo),
                        contentDescription = "GPT Investor Logo",
                        modifier = Modifier
                            .size(60.dp)
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = stringResource(Res.string.gpt_investor),
                    style = MaterialTheme.typography.titleLarge
                )

                Spacer(modifier = Modifier.weight(1f))

                Column(
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    GPTInvestorButton(
                        modifier = Modifier.fillMaxWidth(),
                        onClick = { onEvent(DefaultAuthenticationEvent.SignUp) },
                        text = stringResource(Res.string.sign_up),
                        enabled = !loading
                    )

                    Text(
                        modifier = Modifier
                            .clickable(
                                indication = null,
                                interactionSource = null,
                                onClick = { if (!loading)onEvent(DefaultAuthenticationEvent.GuestLogin) }
                            ),
                        text = stringResource(Res.string.continue_as_guest),
                        textDecoration = TextDecoration.Underline,
                        style = MaterialTheme.typography.linkMedium
                    )
                }
                Spacer(modifier = Modifier.weight(0.5f))
            }
        }
    }
}

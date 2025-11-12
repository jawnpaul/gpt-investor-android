package com.thejawnpaul.gptinvestor.features.onboarding.presentation

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
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import com.thejawnpaul.gptinvestor.theme.linkMedium
import gptinvestor.app.generated.resources.Res
import gptinvestor.app.generated.resources.get_actionable_stock_picks_everyday
import gptinvestor.app.generated.resources.mesh_background
import gptinvestor.app.generated.resources.next
import gptinvestor.app.generated.resources.onboarding_image_one
import gptinvestor.app.generated.resources.onboarding_image_two
import gptinvestor.app.generated.resources.skip
import gptinvestor.app.generated.resources.your_personal_financial_guru
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource

@Composable
fun OnboardingScreen(modifier: Modifier, onFinishOnboarding: () -> Unit) {
    Scaffold(
        modifier = modifier
    ) { innerPadding ->

        var currentScreen by remember { mutableIntStateOf(0) }

        Box(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
        ) {
            Image(
                painter = painterResource(Res.drawable.mesh_background),
                contentDescription = null,
                contentScale = ContentScale.Fit,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 160.dp)
                    .fillMaxHeight(0.4f)
                    .align(Alignment.BottomCenter)
                    .alpha(0.4f)

            )

            Column(
                modifier = Modifier
                    .fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Spacer(modifier = Modifier.weight(0.4f))

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(400.dp)
                ) {
                    val imageResource = when (currentScreen) {
                        0 -> Res.drawable.onboarding_image_one
                        1 -> Res.drawable.onboarding_image_two
                        else -> Res.drawable.onboarding_image_one
                    }

                    Image(
                        painter = painterResource(imageResource),
                        contentDescription = null,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 0.dp),
                        contentScale = ContentScale.Crop
                    )
                }

                Spacer(modifier = Modifier.weight(0.2f))

                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(32.dp)
                ) {
                    val text = when (currentScreen) {
                        0 -> stringResource(Res.string.your_personal_financial_guru)
                        1 -> stringResource(Res.string.get_actionable_stock_picks_everyday)
                        else -> stringResource(Res.string.your_personal_financial_guru)
                    }

                    Text(
                        modifier = Modifier.fillMaxWidth(),
                        text = text,
                        style = MaterialTheme.typography.titleLarge,
                        textAlign = TextAlign.Center
                    )

                    // Button
                    Button(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(52.dp)
                            .padding(horizontal = 16.dp),
                        shape = RoundedCornerShape(corner = CornerSize(20.dp)),
                        onClick = {
                            currentScreen = currentScreen + 1
                            if (currentScreen > 1) {
                                onFinishOnboarding()
                            }
                        }
                    ) {
                        Text(
                            text = stringResource(Res.string.next).uppercase(),
                            style = MaterialTheme.typography.titleMedium
                        )
                    }

                    // Text
                    Text(
                        modifier = Modifier
                            .align(Alignment.CenterHorizontally)
                            .clickable(
                                indication = null,
                                interactionSource = null,
                                onClick = onFinishOnboarding
                            ),
                        text = stringResource(Res.string.skip),
                        textDecoration = TextDecoration.Underline,
                        style = MaterialTheme.typography.linkMedium
                    )
                }
                Spacer(modifier = Modifier.weight(0.2f))
            }
        }
    }
}

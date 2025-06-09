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
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import com.thejawnpaul.gptinvestor.R
import com.thejawnpaul.gptinvestor.theme.linkMedium

@Composable
fun OnboardingScreen(modifier: Modifier, onFinishOnboarding: () -> Unit) {
    Surface(
        modifier = modifier.fillMaxSize()
    ) {
        var currentScreen by remember { mutableStateOf(0) }

        Box(
            modifier = Modifier.fillMaxSize()
        ) {
            Image(
                painter = painterResource(id = R.drawable.wavy_background),
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
                        0 -> R.drawable.onboarding_image_one
                        1 -> R.drawable.onboarding_image_two
                        else -> R.drawable.onboarding_image_one
                    }

                    Image(
                        painter = painterResource(id = imageResource),
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
                        0 -> stringResource(R.string.your_personal_financial_guru)
                        1 -> stringResource(R.string.stay_ahead_with_top_stock_picks)
                        else -> stringResource(R.string.your_personal_financial_guru)
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
                            text = stringResource(R.string.next).uppercase(),
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
                        text = stringResource(R.string.skip),
                        textDecoration = TextDecoration.Underline,
                        style = MaterialTheme.typography.linkMedium
                    )
                }
                Spacer(modifier = Modifier.weight(0.2f))
            }
        }
    }
}

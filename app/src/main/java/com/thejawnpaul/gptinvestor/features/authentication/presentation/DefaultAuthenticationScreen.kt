package com.thejawnpaul.gptinvestor.features.authentication.presentation

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.blur
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.thejawnpaul.gptinvestor.R
import com.thejawnpaul.gptinvestor.theme.linkMedium
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

@Composable
fun DefaultAuthenticationScreen(modifier: Modifier, onAuthSuccess: () -> Unit, onAuthFailure: () -> Unit, authViewModel: AuthenticationViewModel = hiltViewModel()) {
    var showDialog by remember { mutableStateOf(false) }
    val context = LocalContext.current

    val scope = rememberCoroutineScope()

    LaunchedEffect(Unit) {
        authViewModel.actions.onEach { action ->
            when (action) {
                is AuthenticationAction.OnLogin -> {
                }

                is AuthenticationAction.OnSignUp -> {
                    if (action.message.contains("success", ignoreCase = true)) {
                        onAuthSuccess()
                    } else {
                        onAuthFailure()
                    }
                }
            }
        }.launchIn(scope)
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(color = MaterialTheme.colorScheme.background)
            .clickable(interactionSource = null, indication = null, onClick = {
                if (showDialog) {
                    showDialog = false
                }
            })
    ) {
        if (showDialog) {
            NewAuthenticationScreen(
                modifier = Modifier
                    .align(Alignment.Center)
                    .padding(horizontal = 16.dp),
                onAuthenticationComplete = { message ->
                    if (message.contains("success", ignoreCase = true)) {
                        onAuthSuccess()
                        showDialog = false
                    } else {
                        onAuthFailure()
                        showDialog = false
                    }
                }
            )

            Column(
                modifier = Modifier.blur(radius = 8.dp)
            ) {
                Box(modifier = Modifier.fillMaxSize()) {
                    // Wavy Background Image (Bottom)
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
                            .fillMaxSize()
                            .padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Spacer(modifier = Modifier.weight(1.3f))

                        Box(contentAlignment = Alignment.Center) {
                            Box(
                                modifier = Modifier
                                    .size(60.dp)
                            )
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        Text(
                            text = stringResource(R.string.gpt_investor),
                            style = MaterialTheme.typography.titleLarge
                        )

                        Spacer(modifier = Modifier.weight(1f))

                        Column {
                            //
                            Surface(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(52.dp)
                                    .clickable(
                                        interactionSource = null,
                                        indication = null,
                                        onClick = {}
                                    ),
                                color = Color.White,
                                shape = RoundedCornerShape(corner = CornerSize(20.dp))

                            ) {
                                Box(
                                    contentAlignment = Alignment.Center
                                ) {
                                    Row(
                                        horizontalArrangement = Arrangement.spacedBy(16.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Image(
                                            modifier = Modifier.size(20.dp),
                                            painter = painterResource(R.drawable.google_icon),
                                            contentDescription = null
                                        )
                                        Text(
                                            text = stringResource(R.string.continue_with_google),
                                            style = MaterialTheme.typography.titleMedium
                                        )
                                    }
                                }
                            }

                            Text(
                                modifier = Modifier
                                    .padding(top = 32.dp)
                                    .align(Alignment.CenterHorizontally)
                                    .clickable(
                                        indication = null,
                                        interactionSource = null,
                                        onClick = {
                                        }
                                    ),
                                text = stringResource(R.string.continue_with_email),
                                textDecoration = TextDecoration.Underline,
                                style = MaterialTheme.typography.linkMedium
                            )
                        }
                        Spacer(modifier = Modifier.weight(0.5f))
                    }
                }
            }
        } else {
            Surface(
                modifier = Modifier
                    .fillMaxSize()
            ) {
                Box(modifier = Modifier.fillMaxSize()) {
                    // Wavy Background Image (Bottom)
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
                            .fillMaxSize()
                            .padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Spacer(modifier = Modifier.weight(1.3f))

                        Box(contentAlignment = Alignment.Center) {
                            Image(
                                painter = painterResource(id = R.drawable.ic_logo),
                                contentDescription = "GPT Investor Logo",
                                modifier = Modifier
                                    .size(60.dp)
                            )
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        Text(
                            text = stringResource(R.string.gpt_investor),
                            style = MaterialTheme.typography.titleLarge
                        )

                        Spacer(modifier = Modifier.weight(1f))

                        Column {
                            // continue with google button
                            Surface(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(52.dp),
                                color = Color.White,
                                shape = RoundedCornerShape(corner = CornerSize(20.dp)),
                                onClick = {
                                    authViewModel.handleEvent(
                                        AuthenticationEvent.SignUpWithGoogle(
                                            context = context
                                        )
                                    )
                                }
                            ) {
                                Box(
                                    contentAlignment = Alignment.Center
                                ) {
                                    Row(
                                        horizontalArrangement = Arrangement.spacedBy(16.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Image(
                                            modifier = Modifier.size(20.dp),
                                            painter = painterResource(R.drawable.google_icon),
                                            contentDescription = null
                                        )
                                        Text(
                                            text = stringResource(R.string.continue_with_google),
                                            style = MaterialTheme.typography.titleMedium
                                        )
                                    }
                                }
                            }

                            Text(
                                modifier = Modifier
                                    .padding(top = 32.dp)
                                    .align(Alignment.CenterHorizontally)
                                    .clickable(
                                        indication = null,
                                        interactionSource = null,
                                        onClick = {
                                            showDialog = true
                                        }
                                    ),
                                text = stringResource(R.string.continue_with_email),
                                textDecoration = TextDecoration.Underline,
                                style = MaterialTheme.typography.linkMedium
                            )
                        }
                        Spacer(modifier = Modifier.weight(0.5f))
                    }
                }
            }
        }
    }
}

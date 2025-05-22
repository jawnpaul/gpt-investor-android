package com.thejawnpaul.gptinvestor.features.authentication.presentation

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import com.thejawnpaul.gptinvestor.R
import com.thejawnpaul.gptinvestor.features.component.GPTInvestorButton
import com.thejawnpaul.gptinvestor.theme.GPTInvestorTheme
import com.thejawnpaul.gptinvestor.theme.linkMedium

@Composable
fun LoginScreen(
    modifier: Modifier,
    email: String,
    password: String,
    enableButton: Boolean,
    onEmailChange: (String) -> Unit,
    onPasswordChange: (String) -> Unit,
    onLoginClick: () -> Unit,
    onSignUpClick: () -> Unit
) {
    var passwordHidden by remember { mutableStateOf(true) }

    OutlinedCard(
        modifier
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Center,
                text = stringResource(R.string.login),
                style = MaterialTheme.typography.headlineMedium
            )

            // Input
            OutlinedTextField(
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Email,
                    imeAction = ImeAction.Next
                ),
                value = email,
                onValueChange = onEmailChange,
                label = {
                    Text(text = stringResource(R.string.email_address))
                },
                modifier = Modifier.fillMaxWidth(),
                leadingIcon = {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_profile),
                        contentDescription = null
                    )
                }
            )
            // Input
            OutlinedTextField(
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                value = password,
                onValueChange = onPasswordChange,
                label = {
                    Text(text = stringResource(R.string.password))
                },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                visualTransformation = if (passwordHidden) PasswordVisualTransformation() else VisualTransformation.None,
                trailingIcon = {
                    IconButton(onClick = {
                        passwordHidden = !passwordHidden
                    }) {
                        val visibilityIcon =
                            if (passwordHidden) R.drawable.outline_visibility_24 else R.drawable.baseline_visibility_off_24

                        Icon(
                            painter = painterResource(id = visibilityIcon),
                            contentDescription = null
                        )
                    }
                },
                leadingIcon = {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_lock),
                        contentDescription = null
                    )
                }
            )

            // Done button
            GPTInvestorButton(
                modifier = Modifier.align(Alignment.CenterHorizontally),
                text = stringResource(R.string.login),
                enabled = enableButton,
                onClick = onLoginClick
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    modifier = Modifier.padding(end = 8.dp),
                    text = stringResource(R.string.don_t_have_an_account),
                    style = MaterialTheme.typography.bodyMedium
                )

                Text(
                    modifier = Modifier.clickable(
                        indication = null,
                        interactionSource = null,
                        onClick = onSignUpClick
                    ),
                    text = stringResource(R.string.sign_up),
                    textDecoration = TextDecoration.Underline,
                    style = MaterialTheme.typography.linkMedium
                )
            }
        }
    }
}

@PreviewLightDark()
@Composable
fun LoginPreview() {
    GPTInvestorTheme {
        Surface {
            LoginScreen(
                modifier = Modifier,
                email = "",
                password = "",
                enableButton = false,
                onEmailChange = {},
                onPasswordChange = {},
                onLoginClick = {},
                onSignUpClick = {}
            )
        }
    }
}

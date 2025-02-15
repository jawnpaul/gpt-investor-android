package com.thejawnpaul.gptinvestor.features.authentication.presentation

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.thejawnpaul.gptinvestor.R

@Composable
fun AuthenticationScreen(viewModel: AuthenticationViewModel = hiltViewModel(), onSignInSuccess: () -> Unit) {
    val authState by viewModel.authState.collectAsStateWithLifecycle()
    val isUserSignedIn = authState.isUserSignedIn

    val signInLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { result ->
        viewModel.handleSignInResult(result)
    }

    LaunchedEffect(isUserSignedIn) {
        if (isUserSignedIn) {
            onSignInSuccess()
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        if (authState.loading) {
            CircularProgressIndicator()
        } else if (authState.errorMessage != null) {
            Text(
                text = "Error: ${authState.errorMessage}",
                color = MaterialTheme.colorScheme.error
            )
            Spacer(modifier = Modifier.height(16.dp))
            Button(
                onClick = {
                    viewModel.signIn(signInLauncher)
                }
            ) {
                Text(stringResource(R.string.retry_sign_in))
            }
        } else {
            Button(
                onClick = {
                    viewModel.signIn(signInLauncher)
                }
            ) {
                Text(stringResource(R.string.sign_in))
            }
        }
    }
}

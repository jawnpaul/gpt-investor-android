package com.thejawnpaul.gptinvestor.features.splash

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.thejawnpaul.gptinvestor.R
import com.thejawnpaul.gptinvestor.ui.theme.BottomSheetShape
import kotlinx.coroutines.delay

private const val SPLASH_WAIT_TIME: Long = 2000

@Composable
fun CustomSplashScreen(onSplashScreenFinished: () -> Unit) {
    var startAnimation by remember { mutableStateOf(false) }
    val alphaAnim = animateFloatAsState(
        targetValue = if (startAnimation) 1f else 0f,
        animationSpec = tween(durationMillis = 3000),
        label = "label"
    )

    LaunchedEffect(key1 = true) {
        startAnimation = true
        delay(4000)
        onSplashScreenFinished()
    }

    Surface(modifier = Modifier.fillMaxSize()) {
        Column(modifier = Modifier.fillMaxSize()) {
            Surface(
                shape = BottomSheetShape,
                modifier = Modifier
                    .weight(0.5f)
                    .padding(horizontal = 16.dp)
            ) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Image(
                        modifier = Modifier.fillMaxSize(),
                        painter = painterResource(R.drawable.interlaced),
                        contentDescription = null,
                        contentScale = ContentScale.Crop
                    )

                    Image(
                        painter = painterResource(R.drawable.asset_3_1),
                        contentDescription = null,
                        modifier = Modifier
                            .size(250.dp)
                            .alpha(alphaAnim.value)
                    )
                }
            }

            Text(
                modifier = Modifier
                    .weight(0.2f)
                    .padding(horizontal = 16.dp, vertical = 16.dp)
                    .alpha(alphaAnim.value),
                text = stringResource(R.string.informed_investment_decision_powered_by_ai),
                style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.SemiBold)
            )
        }
    }
}

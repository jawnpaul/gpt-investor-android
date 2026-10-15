package com.thejawnpaul.gptinvestor.theme

import android.provider.Settings
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext

// Android has no reduce-motion switch; "Remove animations" sets the animator duration scale to 0.
@Composable
actual fun isReducedMotionEnabled(): Boolean =
    Settings.Global.getFloat(LocalContext.current.contentResolver, Settings.Global.ANIMATOR_DURATION_SCALE, 1f) == 0f

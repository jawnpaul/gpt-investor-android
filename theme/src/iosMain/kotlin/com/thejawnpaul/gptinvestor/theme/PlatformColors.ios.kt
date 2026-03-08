package com.thejawnpaul.gptinvestor.theme

import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.Color
import kotlinx.cinterop.ExperimentalForeignApi
import platform.CoreGraphics.CGRectMake
import platform.UIKit.UIApplication
import platform.UIKit.UIColor
import platform.UIKit.UINavigationBar
import platform.UIKit.UIStatusBarStyleDarkContent
import platform.UIKit.UIStatusBarStyleLightContent
import platform.UIKit.UIView
import platform.UIKit.UIWindow
import platform.UIKit.setStatusBarStyle
import platform.UIKit.statusBarManager

@OptIn(ExperimentalForeignApi::class)
@Composable
private fun statusBarView(): UIView {
    val statusBarView = remember { UIView() }

    SideEffect {
        val keyWindow: UIWindow? = UIApplication.sharedApplication.windows
            .firstOrNull { (it as? UIWindow)?.isKeyWindow() == true } as? UIWindow

        // Get the status bar manager from the window's scene
        val statusBarManager = keyWindow?.windowScene?.statusBarManager

        // Use the exact frame provided by the system
        val statusFrame = statusBarManager?.statusBarFrame ?: CGRectMake(0.0, 0.0, 0.0, 0.0)

        statusBarView.tag = 3848245L
        statusBarView.layer.zPosition = 999999.0

        // Apply the system-provided frame
        statusBarView.setFrame(statusFrame)

        if (statusBarView.superview == null) {
            keyWindow?.addSubview(statusBarView)
        }
    }
    return statusBarView
}

@Composable
internal actual fun SetPlatformColors(
    statusBarColor: Color,
    navigationBarColor: Color,
    useDarkTheme: Boolean
) {
    val statusBar = statusBarView()
    /**
     * TODO: Keep this into account on the app migration phase
     * Ensure that your Info.plist has View controller-based status bar appearance set to
     * Boolean -> NO. Otherwise, UIApplication.sharedApplication.setStatusBarStyle will be ignored by iOS.
     */
    SideEffect {
        statusBar.backgroundColor = statusBarColor.toUIColor()
        UINavigationBar.appearance().backgroundColor = navigationBarColor.toUIColor()
        UIApplication.sharedApplication.setStatusBarStyle(
            if (useDarkTheme) UIStatusBarStyleLightContent else UIStatusBarStyleDarkContent,
            animated = true
        )
    }
}

private fun Color.toUIColor(): UIColor = UIColor(
    red = this.red.toDouble(),
    green = this.green.toDouble(),
    blue = this.blue.toDouble(),
    alpha = this.alpha.toDouble()
)
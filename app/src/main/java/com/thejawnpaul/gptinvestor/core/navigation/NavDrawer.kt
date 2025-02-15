package com.thejawnpaul.gptinvestor.core.navigation

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.thejawnpaul.gptinvestor.R
import com.thejawnpaul.gptinvestor.features.authentication.presentation.AuthenticationViewModel

@Composable
fun NavDrawerContent(navController: NavHostController, onCloseDrawer: () -> Unit, authenticationViewModel: AuthenticationViewModel = hiltViewModel()) {
    Column(
        modifier = Modifier
            .fillMaxHeight()
            .width(300.dp)
            .background(MaterialTheme.colorScheme.surface)
            .padding(16.dp)
    ) {
        // Header
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 24.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = stringResource(R.string.app_name),
                style = MaterialTheme.typography.headlineSmall
            )
        }

        HorizontalDivider()
        Spacer(modifier = Modifier.height(16.dp))

        // Drawer items
        DrawerItem(
            icon = ImageVector.vectorResource(R.drawable.baseline_bookmarks_24),
            label = stringResource(R.string.saved_picks),
            onClick = {
                // Navigate to saved picks
                navController.navigate(Screen.SavedTopPicksScreen.route)
                onCloseDrawer()
            }
        )

        DrawerItem(
            icon = Icons.Default.Settings,
            label = stringResource(R.string.settings),
            onClick = {
                // Navigate to settings
                onCloseDrawer()
            }
        )

        Spacer(modifier = Modifier.weight(1f))

        // Bottom section
        DrawerItem(
            icon = ImageVector.vectorResource(R.drawable.baseline_logout_24),
            label = stringResource(R.string.sign_out),
            onClick = {
                // Handle sign out
                authenticationViewModel.signOut()
                onCloseDrawer()
            }
        )
    }
}

@Composable
private fun DrawerItem(icon: ImageVector, label: String, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = label,
            modifier = Modifier.size(24.dp)
        )
        Spacer(modifier = Modifier.width(32.dp))
        Text(
            text = label,
            style = MaterialTheme.typography.bodyLarge
        )
    }
}

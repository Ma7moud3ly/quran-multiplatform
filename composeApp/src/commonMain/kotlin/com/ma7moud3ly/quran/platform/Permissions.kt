package com.ma7moud3ly.quran.platform

import androidx.compose.runtime.Composable

/**
 * A composable function that remembers the state of notifications permissions.
 *
 * This function provides a [PermissionsState] object that can be used to request notifications permissions,
 * request them manually (e.g., by opening the app settings), and check if the permissions are granted.
 *
 * @return A [PermissionsState] object that represents the current state of notifications permissions.
 */
@Composable
expect fun rememberNotificationsPermissionsState(): PermissionsState

class PermissionsState(
    val request: (onResult: ((granted: Boolean) -> Unit)?) -> Unit,
    val requestManually: (onResult: ((granted: Boolean) -> Unit)?) -> Unit,
    val isGranted: () -> Boolean
)
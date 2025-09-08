package com.ma7moud3ly.quran.platform

import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.provider.Settings
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.core.app.ActivityCompat


@Composable
actual fun rememberNotificationsPermissionsState(): PermissionsState {
    // Get the current context.
    val context = LocalContext.current
    val permissionsList = arrayOf("android.permission.POST_NOTIFICATIONS")
    // Store the result callback for the permission request.
    var onResult by remember { mutableStateOf<((Boolean) -> Unit)?>(null) }
    // Store the result callback for the manual permission request.
    var onManualResult by remember { mutableStateOf<((Boolean) -> Unit)?>(null) }

    /**
     * Checks if all the permissions are granted.
     * This function iterates through all the permissions and checks if each one is granted.
     *
     * @return True if all the permissions are granted, false otherwise.
     */
    fun permissionsGranted(permissions: Array<String>): Boolean {
        return permissions.all {
            ActivityCompat.checkSelfPermission(context, it) ==
                    PackageManager.PERMISSION_GRANTED
        }
    }

    // Create a launcher for the permission request.
    val permissionsLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { result: Map<String, Boolean> ->
        // Check if the permissions were granted.
        var granted = false
        permissionsList.forEach { granted = granted || result[it] ?: false }
        // Invoke the result callback.
        onResult?.invoke(granted)
    }

    // Create a launcher for the manual permission request.
    val activityResultLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) {
        // Invoke the result callback with the current permission status.
        onManualResult?.invoke(permissionsGranted(permissionsList))
    }

    // Create and remember the PermissionsState object.
    return remember {
        PermissionsState(
            // Request permissions.
            request = { callback ->
                onResult = callback
                // Launch the permission request.
                permissionsLauncher.launch(permissionsList)
            },
            // Request permissions manually.
            requestManually = { result ->
                // Store the result callback.
                onManualResult = result
                // Create an intent to launch the app settings screen.
                val uri = Uri.fromParts("package", context.packageName, null)
                val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
                    this.data = uri
                }
                // Launch the intent.
                activityResultLauncher.launch(intent)
            },
            // Check if permissions are granted.
            isGranted = {
                permissionsGranted(permissionsList)
            }
        )
    }
}

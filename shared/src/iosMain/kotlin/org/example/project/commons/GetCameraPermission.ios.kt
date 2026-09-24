package org.example.project.commons

import androidx.compose.runtime.Composable
import platform.AVFoundation.AVAuthorizationStatusAuthorized
import platform.AVFoundation.AVCaptureDevice
import platform.AVFoundation.AVMediaTypeVideo
import platform.AVFoundation.authorizationStatusForMediaType
import platform.AVFoundation.requestAccessForMediaType

@Composable
actual fun GetCameraPermission(
    permissionGranted: (Boolean) -> Unit,
    showPermissionRationale: () -> Unit
) {
    AVCaptureDevice.requestAccessForMediaType(
        mediaType = AVMediaTypeVideo
    ) { granted ->
        permissionGranted(granted)
    }
}
class CheckCameraPermIOS():CheckCameraPermGranted{
    override fun checkCameraPermGranted(): Boolean {
        return AVCaptureDevice.authorizationStatusForMediaType(
            AVMediaTypeVideo
        ) == AVAuthorizationStatusAuthorized
    }
}
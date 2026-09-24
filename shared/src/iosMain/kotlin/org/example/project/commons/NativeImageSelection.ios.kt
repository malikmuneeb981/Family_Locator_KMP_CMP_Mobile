package org.example.project.commons

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.uikit.LocalUIViewController
import platform.Foundation.base64EncodedStringWithOptions
import platform.PhotosUI.PHPickerConfiguration
import platform.PhotosUI.PHPickerFilter
import platform.PhotosUI.PHPickerResult
import platform.PhotosUI.PHPickerViewController
import platform.PhotosUI.PHPickerViewControllerDelegateProtocol
import platform.darwin.NSObject
import platform.darwin.dispatch_async
import platform.darwin.dispatch_get_main_queue

@Composable
actual fun SelectImage(
    selectedImage: (path: String,base64String: String) -> Unit
) {
    val currentSelectedImage = rememberUpdatedState(selectedImage)
    val viewController = LocalUIViewController.current

    val delegate = remember {
        object : NSObject(), PHPickerViewControllerDelegateProtocol {
            override fun picker(picker: PHPickerViewController, didFinishPicking: List<*>) {
                picker.dismissViewControllerAnimated(true, completion = null)
                val result = didFinishPicking.firstOrNull() as? PHPickerResult ?: return
                val itemProvider = result.itemProvider

                if (itemProvider.hasItemConformingToTypeIdentifier("public.image")) {
                    itemProvider.loadDataRepresentationForTypeIdentifier(
                        typeIdentifier = "public.image"
                    ) { data, _ ->
                        if (data != null) {
                            val base64String = data.base64EncodedStringWithOptions(0u)
                            dispatch_async(dispatch_get_main_queue()) {
                                currentSelectedImage.value(data.toString(),base64String)
                            }
                        }
                    }
                }
            }
        }
    }

    LaunchedEffect(Unit) {
        val configuration = PHPickerConfiguration().apply {
            filter = PHPickerFilter.imagesFilter
            selectionLimit = 1
        }
        val picker = PHPickerViewController(configuration = configuration)
        picker.delegate = delegate
        viewController.presentViewController(picker, animated = true, completion = null)
    }
}
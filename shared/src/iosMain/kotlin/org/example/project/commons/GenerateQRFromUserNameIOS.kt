package org.example.project.commons

import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.toComposeImageBitmap
import kotlinx.cinterop.BetaInteropApi
import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.addressOf
import kotlinx.cinterop.usePinned
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.withContext
import org.jetbrains.skia.Image
import platform.CoreGraphics.CGAffineTransformMakeScale
import platform.CoreGraphics.CGRectGetHeight
import platform.CoreGraphics.CGRectGetWidth
import platform.CoreImage.CIContext
import platform.CoreImage.CIFilter
import platform.CoreImage.createCGImage
import platform.CoreImage.filterWithName
import platform.Foundation.NSData
import platform.Foundation.create
import platform.Foundation.setValue
import platform.UIKit.UIImage
import platform.UIKit.UIImagePNGRepresentation
import platform.posix.memcpy

class GenerateQRFromUserNameIOS : GenerateQRFromUserName {
    @OptIn(ExperimentalForeignApi::class, BetaInteropApi::class)
    override suspend fun generateQR(userName: String, size: Int): ImageBitmap? = withContext(Dispatchers.IO) {
        try {
            if (userName.isBlank()) return@withContext null

            val filter = CIFilter.filterWithName("CIQRCodeGenerator") ?: return@withContext null
            filter.setDefaults()

            val bytes = userName.encodeToByteArray()
            val data = bytes.usePinned { pinned ->
                NSData.create(bytes = pinned.addressOf(0), length = bytes.size.toULong())
            }

            (filter as platform.darwin.NSObject).setValue(value = data, forKey = "inputMessage")
            (filter as platform.darwin.NSObject).setValue(value = "M", forKey = "inputCorrectionLevel")

            val outputImage = filter.outputImage ?: return@withContext null

            // Scale to desired size (CIQRCodeGenerator outputs 1 pixel per module by default)
            val extentWidth = CGRectGetWidth(outputImage.extent)
            val extentHeight = CGRectGetHeight(outputImage.extent)
            val scaleX = size.toDouble() / extentWidth
            val scaleY = size.toDouble() / extentHeight
            val transform = CGAffineTransformMakeScale(scaleX, scaleY)
            val scaledImage = outputImage.imageByApplyingTransform(transform)

            val context = CIContext()
            val cgImage = context.createCGImage(scaledImage, fromRect = scaledImage.extent) ?: return@withContext null
            val uiImage = UIImage.imageWithCGImage(cgImage)
            val pngData = UIImagePNGRepresentation(uiImage) ?: return@withContext null

            val byteArray = ByteArray(pngData.length.toInt())
            byteArray.usePinned { pinned ->
                memcpy(pinned.addressOf(0), pngData.bytes, pngData.length)
            }

            Image.makeFromEncoded(byteArray).toComposeImageBitmap()
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
}

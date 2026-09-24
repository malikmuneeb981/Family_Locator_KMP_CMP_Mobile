package org.example.project.commons

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.UIKitView
import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.readValue
import platform.AVFoundation.AVCaptureConnection
import platform.AVFoundation.AVCaptureDevice
import platform.AVFoundation.AVCaptureDeviceInput
import platform.AVFoundation.AVCaptureMetadataOutput
import platform.AVFoundation.AVCaptureMetadataOutputObjectsDelegateProtocol
import platform.AVFoundation.AVCaptureOutput
import platform.AVFoundation.AVCaptureSession
import platform.AVFoundation.AVCaptureVideoPreviewLayer
import platform.AVFoundation.AVLayerVideoGravityResizeAspectFill
import platform.AVFoundation.AVMediaTypeVideo
import platform.AVFoundation.AVMetadataMachineReadableCodeObject
import platform.AVFoundation.AVMetadataObjectTypeQRCode
import platform.CoreGraphics.CGRectZero
import platform.UIKit.UIView
import platform.darwin.NSObject
import platform.darwin.dispatch_get_main_queue

@Composable
actual fun NativeQRScanner(
    modifier: Modifier ,
    onQRScannedResult:(String)-> Unit
) {
    UIKitView(
        modifier = modifier,
        factory = {
            QrScannerView(
                onQrScanned = onQRScannedResult
            )
        },
        update = {
            // Nothing needed here
        }
    )
}

@OptIn(ExperimentalForeignApi::class)
class QrScannerView(
    private val onQrScanned: (String) -> Unit
) : UIView(frame = CGRectZero.readValue()) {

    private val captureSession = AVCaptureSession()

    private lateinit var previewLayer: AVCaptureVideoPreviewLayer

    init {
        setupCamera()
    }

    private fun setupCamera() {

        captureSession.beginConfiguration()

        val videoDevice =
            AVCaptureDevice.defaultDeviceWithMediaType(
                AVMediaTypeVideo
            )

        if (videoDevice == null) {
            println("Camera not available")
            return
        }

        val input = AVCaptureDeviceInput.deviceInputWithDevice(
            device = videoDevice,
            error = null
        )

        if (input == null) {
            println("Unable to create camera input")
            return
        }

        if (captureSession.canAddInput(input)) {
            captureSession.addInput(input)
        }

        val metadataOutput = AVCaptureMetadataOutput()

        if (captureSession.canAddOutput(metadataOutput)) {
            captureSession.addOutput(metadataOutput)

            metadataOutput.setMetadataObjectsDelegate(
                objectsDelegate = QrMetadataDelegate(onQrScanned),
                queue = dispatch_get_main_queue()
            )

            metadataOutput.metadataObjectTypes = listOf(
                AVMetadataObjectTypeQRCode
            )
        }

        captureSession.commitConfiguration()

        previewLayer = AVCaptureVideoPreviewLayer(
            session = captureSession
        )

        previewLayer.videoGravity =
            AVLayerVideoGravityResizeAspectFill

        layer.addSublayer(previewLayer)

        captureSession.startRunning()
    }

    override fun layoutSubviews() {
        super.layoutSubviews()

        previewLayer.frame = bounds
    }
}
class QrMetadataDelegate(
    private val onQrScanned: (String) -> Unit
) : NSObject(),
    AVCaptureMetadataOutputObjectsDelegateProtocol {

    override fun captureOutput(
        output: AVCaptureOutput,
        didOutputMetadataObjects: List<*>,
        fromConnection: AVCaptureConnection
    ) {

        val metadataObject =
            didOutputMetadataObjects
                .firstOrNull()
                    as? AVMetadataMachineReadableCodeObject

        val value = metadataObject?.stringValue

        if (!value.isNullOrEmpty()) {

            println("QR RESULT: $value")

            onQrScanned(value)
        }
    }
}
package org.example.project.commons

import platform.UIKit.UIPasteboard

class AppUtilsIOS(): AppUtils {
    override fun nativeCopyTextToClipboard(text: String) {
        UIPasteboard.generalPasteboard.string = text
    }
}
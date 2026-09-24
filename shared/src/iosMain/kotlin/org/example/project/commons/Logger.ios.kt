package org.example.project.commons

import platform.Foundation.NSLog

actual fun logger(tag: String, message: String) {
    NSLog("IOS $tag $message",)
}
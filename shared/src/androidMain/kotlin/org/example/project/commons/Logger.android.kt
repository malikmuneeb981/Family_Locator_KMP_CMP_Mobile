package org.example.project.commons

import android.util.Log

actual fun logger(tag: String, message: String) {
    Log.e("Android $tag",message)
}
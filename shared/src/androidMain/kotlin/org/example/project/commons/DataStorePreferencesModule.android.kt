package org.example.project.commons

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import org.example.project.utils.DATASTORE_FILE_NAME
import org.example.project.utils.createDataStore

fun createAndroidDataStore(context: Context): DataStore<Preferences> {
    return createDataStore {
        context.filesDir.resolve(DATASTORE_FILE_NAME).absolutePath
    }
}

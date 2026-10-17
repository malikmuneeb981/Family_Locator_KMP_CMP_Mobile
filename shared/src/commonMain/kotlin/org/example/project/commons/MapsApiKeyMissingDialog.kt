package org.example.project.commons

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue

/**
 * Shown instead of the map when no Google Maps API key has been configured,
 * so the app keeps working (without a map) instead of crashing.
 */
@Composable
fun MapsApiKeyMissingDialog() {
    var show by remember { mutableStateOf(true) }
    if (!show) return
    AlertDialog(
        onDismissRequest = { show = false },
        title = { Text("Google Maps API key required") },
        text = {
            Text(
                "You need a Google Maps API key to access Google Maps in this app. " +
                    "Add your own key (see the README) and rebuild the app."
            )
        },
        confirmButton = { TextButton(onClick = { show = false }) { Text("OK") } }
    )
}

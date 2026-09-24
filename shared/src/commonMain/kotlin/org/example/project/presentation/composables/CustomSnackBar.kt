package org.example.project.presentation.composables

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.translate.speech.to.text.dictionary.instant.voice.translator.presentation.composables.AppText

@Composable
fun CustomSnackBar(
    message: String,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
            .background(
                color = Color(0xFF3268F0),
                shape = RoundedCornerShape(12.dp)
            )
            .padding(
                horizontal = 16.dp,
                vertical = 12.dp
            )
    ) {
        AppText(
            text = message,
            color = Color.White,
            fontSize = 14.sp
        )
    }
}
@Preview
@Composable
fun CustomSnackBarPrev(){
    CustomSnackBar(message = "Text Copied ....")
}
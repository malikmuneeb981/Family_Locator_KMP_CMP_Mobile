package com.translate.speech.to.text.dictionary.instant.voice.translator.presentation.composables

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.sp
import familylocatorkmp.shared.generated.resources.Res
import familylocatorkmp.shared.generated.resources.inter_regular
import org.jetbrains.compose.resources.Font
import org.jetbrains.compose.resources.FontResource
import org.jetbrains.compose.resources.stringResource

@Composable
fun AppText(
    modifier: Modifier = Modifier,
    text: String,
    fontStyle: FontStyle = FontStyle.Normal,
    fontWeight: FontWeight = FontWeight.Normal,
    fontSize: TextUnit = 15.sp,
    color: Color = Color(0xFF03040D),
    textAlign: TextAlign = TextAlign.Center,
    textDecoration: TextDecoration = TextDecoration.None,
    font: FontResource = Res.font.inter_regular,
    autoResize: Boolean = false,
    textLines: Int = Int.MAX_VALUE,
    minLines: Int = 1,
    style: TextStyle = TextStyle.Default
) {
    // Auto-Resize Text
    var currentSize by remember { mutableStateOf(fontSize) }
    var ready by remember { mutableStateOf(false) }

    Text(
        text = text,
        fontStyle = fontStyle,
        fontWeight = fontWeight,
        fontSize = currentSize,
        modifier = modifier,
        color = color,
        textAlign = textAlign,
        textDecoration = textDecoration,
        maxLines = textLines,
        minLines = minLines,
        softWrap = true,
        overflow = TextOverflow.Ellipsis,
        style = style,
        fontFamily = FontFamily(Font(font)),
        onTextLayout = { result ->
            if (autoResize){
                if (!ready && result.didOverflowHeight) {
                    val newSize = currentSize * 0.92f     // shrink 8% each step
                    if (newSize < 10.sp) {               // stop shrinking at 10sp (safe minimum)
                        ready = true
                    } else {
                        currentSize = newSize
                    }
                } else {
                    ready = true
                }
            }
        }
    )
}


@Preview
@Composable
private fun AppTextPrev() {

    AppText(text = "Hello", textLines = 1,
        autoResize = true
        )

}
package org.example.project.presentation.composables

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.translate.speech.to.text.dictionary.instant.voice.translator.presentation.composables.AppText
import familylocatorkmp.shared.generated.resources.Res
import familylocatorkmp.shared.generated.resources.inter_semibold

@Composable
fun AppSecondaryBtn(
    text: String,
    modifier: Modifier = Modifier,
    onClick:()-> Unit
){
    Box(modifier = modifier.clip(
        shape = RoundedCornerShape(10.dp)
    ).border(
        width = 1.dp,
        color = Color(0xFF3268F0),
        shape = RoundedCornerShape(10.dp)
    ).clickable(onClick = {
        onClick()
    })){
        AppText(text = text,
            color = Color(0xFF3268F0),
            font= Res.font.inter_semibold,
            modifier = Modifier.fillMaxWidth().padding(
                vertical = 18.dp,
                horizontal = 10.dp
            ))
    }
}

@Preview
@Composable
fun AppSecondaryBtnPreview(){
    AppSecondaryBtn(
        text = "asdasdadbdakdbsaskbdakdbakdbadkabkb", onClick = {

        }
    )
}
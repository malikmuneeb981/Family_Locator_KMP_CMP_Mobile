package org.example.project.presentation.dialogs

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.translate.speech.to.text.dictionary.instant.voice.translator.presentation.composables.AppText
import familylocatorkmp.shared.generated.resources.Res
import familylocatorkmp.shared.generated.resources.ic_avatar
import familylocatorkmp.shared.generated.resources.inter_medium
import familylocatorkmp.shared.generated.resources.inter_regular
import familylocatorkmp.shared.generated.resources.inter_semibold
import org.example.project.domain.models.app.GenericDialogData
import org.example.project.presentation.composables.AppMainBtn
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.vectorResource

@Composable
fun GenericDialog(genericDialogData: GenericDialogData,
                  onDismiss:()-> Unit,onRightPositiveBtnClick:()-> Unit,
                  onLeftNegativeBtnClick:()-> Unit, ){
    Dialog(onDismissRequest = {
        onDismiss()
    }){
        Column(modifier = Modifier.fillMaxWidth().background(
            color = Color.White, shape = RoundedCornerShape(16.dp)
        ).padding(
            all = 20.dp,
        ),
            verticalArrangement = Arrangement.spacedBy(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally){
            Image(
                painterResource(genericDialogData.image),
                contentDescription = null,
                modifier = Modifier.size(100.dp))
            AppText(text = genericDialogData.text, modifier = Modifier.fillMaxWidth(),
                font = Res.font.inter_semibold,
                fontSize = 20.sp)
            AppText(text = genericDialogData.secondaryText, modifier = Modifier.fillMaxWidth(),
                font = Res.font.inter_regular,
                fontSize = 14.sp,
                color = Color(0xFF666666))

            Column(modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(
                    10.dp
                )){
                DialogSecondaryBtn(text = genericDialogData.leftNegativeBtnText,
                    modifier = Modifier.fillMaxWidth(), onClick = {
                        onLeftNegativeBtnClick()
                    })
                DialogPositiveBtn(text = genericDialogData.rightPositiveBtnText,
                    modifier = Modifier.fillMaxWidth(), onClick = {
                        onRightPositiveBtnClick()
                    })
            }

        }
   }
}
@Composable
fun DialogPositiveBtn(
    text: String,
    modifier: Modifier = Modifier,
    onClick:()-> Unit
){
    Box(modifier = modifier.clip(
        shape = RoundedCornerShape(10.dp)
    ).background(
        color = Color(0xFF3268F0)
    ).clickable(indication = null,
        interactionSource = remember { MutableInteractionSource() },onClick = {
            onClick()
        })){
        AppText(text = text,
            color = Color.White,
            font= Res.font.inter_semibold,
            modifier = Modifier.padding(
                vertical = 11.dp,
                horizontal = 10.dp
            ).align(Alignment.Center))
    }
}
@Composable
fun DialogSecondaryBtn(
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
                vertical = 10.dp,
                horizontal = 10.dp
            ))
    }
}
@Preview
@Composable
fun GenericDialogPrev(){
    GenericDialog(genericDialogData = GenericDialogData(
        image = Res.drawable.ic_avatar,
        text = "lndalsdnadnal",
        secondaryText = "lndalsdnadnaldnadlakdffwwwfwf",
        rightPositiveBtnText = "ldasndaldnaldna",
        leftNegativeBtnText = "wioworworyowr"
    ), onLeftNegativeBtnClick = {

    }, onRightPositiveBtnClick = {

    }, onDismiss = {

    })
}
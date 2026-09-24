package org.example.project.presentation.composables

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.translate.speech.to.text.dictionary.instant.voice.translator.presentation.composables.AppText
import familylocatorkmp.shared.generated.resources.Res
import familylocatorkmp.shared.generated.resources.ic_locationshare
import org.example.project.commons.GetNativeLatLongInfo
import org.example.project.commons.logger
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.resources.vectorResource
import org.koin.compose.getKoin

@Composable
fun MarkerInfo(lat: Double, long: Double, shareLocationClick:()->Unit) {
    val addressFromLatLong = getKoin().get<GetNativeLatLongInfo>()
    var locationInfo by remember { mutableStateOf("")
    }
    LaunchedEffect(lat,long){
        locationInfo = addressFromLatLong.getLatLongInfo(lat = lat, long = long).toString()
    }
    Column(
        modifier = Modifier
            .widthIn(min = 180.dp)
            .background(Color.White, shape = RoundedCornerShape(10.dp))
            .padding(
                top = 16.dp, bottom = 13.dp, start = 15.dp, end = 15.dp
            ),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {

        AppText(text = "You’re here", color = Color(0xFF3268F0), fontWeight = FontWeight.SemiBold, fontSize = 16.sp)
        if (locationInfo.isNotEmpty()) {
            AppText(
                text = locationInfo,
                fontWeight = FontWeight.Medium,
                fontSize = 12.sp,
                modifier = Modifier.padding(top = 5.dp)
            )
        }
        Row (verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(7.dp),
            modifier = Modifier
                .padding(top = 12.dp)
                .background(
                    color = Color(0xFF3268F0), shape =
                    RoundedCornerShape(8.dp)
                )
                .padding(
                    vertical = 10.dp,
                    horizontal = 8.dp
                )
                .clickable {
                    logger("Location Click", "Click")
                    shareLocationClick()
                }){

            Image(imageVector = vectorResource(Res.drawable.ic_locationshare),
                contentDescription = null)
            AppText(text = "Share Location", fontWeight = FontWeight.Medium, fontSize = 12.sp, color = Color.White)
        }
    }

}

@Preview
@Composable
private fun MarkerInfoPrev() {
    MarkerInfo(lat = 34.34343, long = 34.43343, shareLocationClick = {

    })
}
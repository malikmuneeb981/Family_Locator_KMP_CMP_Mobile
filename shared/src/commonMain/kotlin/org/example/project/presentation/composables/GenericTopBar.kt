package org.example.project.presentation.composables

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.translate.speech.to.text.dictionary.instant.voice.translator.presentation.composables.AppText
import familylocatorkmp.shared.generated.resources.Res
import familylocatorkmp.shared.generated.resources.ic_nav_back
import familylocatorkmp.shared.generated.resources.ic_scan_qr
import familylocatorkmp.shared.generated.resources.inter_semibold
import org.jetbrains.compose.resources.vectorResource

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GenericTopBar(
    text: String,
    onBackClick: () -> Unit,
    showScan: Boolean,
    onScanClick: () -> Unit = {}
) {
    CenterAlignedTopAppBar(
        navigationIcon = {
            Image(
                vectorResource(Res.drawable.ic_nav_back),
                contentDescription = null,
                modifier = Modifier.clickable(
                    indication = null,
                    interactionSource = remember { MutableInteractionSource() },
                    onClick = {
                        onBackClick()
                    }
                )
            )
        },
        title = {
            AppText(
                text = text,
                font = Res.font.inter_semibold,
                fontSize = 20.sp
            )
        },
        actions = {
            if (showScan) {
                Image(
                    vectorResource(Res.drawable.ic_scan_qr),
                    contentDescription = null,
                    modifier = Modifier.clickable(
                        indication = null,
                        interactionSource = remember { MutableInteractionSource() },
                        onClick = {
                            onScanClick()
                        }
                    )
                )
            }
        },
        modifier = Modifier.padding(
            horizontal = 15.dp
        ),
        colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
            containerColor = Color.Transparent
        )
    )
}
@Preview
@Composable
fun GenericTopBarPrev(){
    GenericTopBar(text = "Add Friend",
        onBackClick = {

        }, showScan = true,
        onScanClick = {

        }
        )
}
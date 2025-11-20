package be.hcpl.android.filmtag.ui.view

import androidx.compose.foundation.layout.Arrangement.Absolute.spacedBy
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp

data class FrameUiModel(
    val number: Int,
    val frameNumber: String,
    val dateTaken: String,
    val apertureAndShutter: String,
    val frameNotes: String,
    val flashExposure: Boolean,
)

@Composable
fun FrameView(
    uiModel: FrameUiModel,
    modifier: Modifier = Modifier,
) {

    Column(
        modifier = modifier
            .padding(8.dp)
            .heightIn(min = 48.dp)
            .fillMaxWidth(),
    ) {

        // First line: frame number, aperture, shutter speed
        Row(
            horizontalArrangement = spacedBy(8.dp),
            modifier = Modifier,
        ) {
            Text(text = uiModel.frameNumber)
            Text(
                text = uiModel.dateTaken,
                textAlign = TextAlign.Center,
                modifier = Modifier.weight(1f),
            )
            Text(
                text = uiModel.apertureAndShutter,
            )
            if(uiModel.flashExposure){
                // flash exposure indication
                Text(text = "(F)")
            }

        }
        // Second line: notes
        Row() {
            Text(text = uiModel.frameNotes)
        }

    }
}
package be.hcpl.android.filmtag.ui.view

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement.Absolute.spacedBy
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import be.hcpl.android.filmtag.EditFrameFragment
import be.hcpl.android.filmtag.model.Frame
import be.hcpl.android.filmtag.ui.AppTheme
import be.hcpl.android.filmtag.util.TextUtil
import org.intellij.lang.annotations.JdkConstants
import java.util.Calendar

@Composable
fun FrameView(
    frame: Frame,
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
            modifier = Modifier

        ) {
            Text(text = TextUtil.formatFrameNumber(frame.number))
            Text(
                text = frame.dateTaken?.let { TextUtil.formatDate(it) } ?: "-",
                textAlign = TextAlign.Center,
                modifier = Modifier

                    .weight(1f)
            )
            Text(
                text = TextUtil.formatApertureAndShutter(
                    frame.aperture,
                    frame.shutter,
                    frame.isLongExposure
                )
            )
        }
        // Second line: notes
        Row() {
            Text(text = frame.notes ?: "-")
        }

    }
}

@Preview(showBackground = true)
@Composable
private fun Preview() {
    AppTheme {
        FrameView(
            Frame(
                isLongExposure = true,
                notes = "some notes for this frame",
                number = 12,
                shutter = 250,
                aperture = 5.6,
                pathToImage = null,
                location = null,
                tags = listOf("tag1", "tag2"),
                dateTaken = Calendar.getInstance().timeInMillis,
            )
        )
    }
}
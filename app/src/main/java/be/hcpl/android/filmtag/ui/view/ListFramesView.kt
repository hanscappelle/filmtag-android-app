package be.hcpl.android.filmtag.ui.view

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import be.hcpl.android.filmtag.model.Frame
import be.hcpl.android.filmtag.model.Roll

@Composable
fun ListFramesView(
    roll: Roll,
    frames: List<Frame>,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier,
    ) {

        Text(
            text = roll.toString(), // TODO better visualise this
            color = MaterialTheme.colorScheme.inverseOnSurface,
            style = MaterialTheme.typography.titleLarge,
            modifier = Modifier
                .background(color = MaterialTheme.colorScheme.inverseSurface)
                .padding(16.dp)
                .fillMaxWidth(),
        )

        LazyColumn {
            frames.forEachIndexed { index, frame ->
                item(key = index) {
                    FrameView(frame)
                }
            }
        }

    }
}
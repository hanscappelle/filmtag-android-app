package be.hcpl.android.filmtag.ui.view

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import be.hcpl.android.filmtag.model.Frame
import be.hcpl.android.filmtag.model.Roll

@Composable
fun ListFramesView(
    roll: Roll,
    frames: List<Frame>,
    onSelect: (Int) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier,
    ) {

        RollDetailView(roll)

        LazyColumn {
            frames.forEachIndexed { index, frame ->
                item(key = index) {
                    FrameView(
                        frame = frame,
                        modifier = Modifier.clickable {
                            onSelect(frame.number)
                        }
                    )
                }
            }
        }

    }
}
package be.hcpl.android.filmtag.ui.view

import android.text.TextUtils
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement.Absolute.spacedBy
import be.hcpl.android.filmtag.R
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.text.input.TextFieldLineLimits
import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.material3.Checkbox
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import be.hcpl.android.filmtag.model.Roll
import be.hcpl.android.filmtag.ui.AppScaffold


data class EditRollViewState(
    val roll: Roll, // this roll is only used for the initial state values
) {
    val filmTypeState = TextFieldState(initialText = roll.type.orEmpty())
    val isoState = TextFieldState(initialText = "${roll.speed}")
    val framesState = TextFieldState(initialText = "${roll.frames}")
    val tagsState = TextFieldState(initialText = TextUtils.join(", ", roll.tags))
    val notesState = TextFieldState(initialText = roll.notes.orEmpty())
}

@Composable
fun EditRollView(
    viewState: EditRollViewState,
    modifier: Modifier = Modifier,
) {
    Column(
        verticalArrangement = spacedBy(16.dp),
        modifier = modifier.padding(16.dp),
    ) {

        TextField(
            state = viewState.filmTypeState,
            label = { Text(text = stringResource(R.string.label_film_type)) },
            //placeholder = { Text(text = "John Doe") },
            modifier = Modifier.fillMaxWidth(),
            //singleLine = true,
        )

        Row(
            horizontalArrangement = spacedBy(16.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {

            TextField(
                state = viewState.isoState,
                label = { Text(text = stringResource(R.string.label_roll_speed)) },
                keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number),
                modifier = Modifier.weight(0.6f),
            )

            TextField(
                state = viewState.framesState,
                label = { Text(text = stringResource(R.string.label_roll_frames)) },
                keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number),
                modifier = Modifier.weight(0.4f),
            )
        }

        TextField(
            state = viewState.tagsState,
            label = { Text(text = stringResource(R.string.label_frame_tags)) },
            modifier = Modifier.fillMaxWidth(),
        )

        Row(
            horizontalArrangement = spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Checkbox(
                checked = viewState.roll.isDeveloped,
                onCheckedChange = {
                    // TODO handle state changes here
                },
                modifier = Modifier,
            )
            Text(
                text = stringResource(R.string.label_film_developed),
                modifier = Modifier.clickable{
                    // TODO handle state change here
                }
            )
        }

        TextField(
            state = viewState.notesState,
            label = { Text(text = stringResource(R.string.label_frame_notes)) },
            lineLimits = TextFieldLineLimits.MultiLine(
                minHeightInLines = 6,
            ),
            modifier = Modifier.fillMaxWidth(),
        )

    }
}

@Preview(showBackground = true)
@Composable
private fun Preview() {
    AppScaffold { padding ->
        EditRollView(
            EditRollViewState(Roll()),
            Modifier.padding(padding),
        )
    }

}
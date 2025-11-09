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
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import be.hcpl.android.filmtag.model.Frame
import be.hcpl.android.filmtag.model.Location
import be.hcpl.android.filmtag.model.Roll
import be.hcpl.android.filmtag.ui.AppScaffold
import be.hcpl.android.filmtag.ui.AppTheme
import be.hcpl.android.filmtag.util.TextUtil


data class EditFrameViewState(
    val roll: Roll, // this roll is only used for the initial state values
    val frame: Frame,
) {
    val speedState = TextFieldState(initialText = "${frame.shutter}")
    val apertureState = TextFieldState(initialText = "${frame.aperture}")
    val tagsState = TextFieldState(initialText = TextUtils.join(TextUtil.TAG_SEPARATOR, frame.tags))
    val notesState = TextFieldState(initialText = frame.notes.orEmpty())
    val checkedState = mutableStateOf(frame.isLongExposure)
    // TODO add time selection (requested feature)
}

@Composable
fun EditFrameView(
    viewState: EditFrameViewState,
    modifier: Modifier = Modifier,
    onSelectDate: () -> Unit,
    onShowLocation: (Location?) -> Unit,
    onUpdateLocation: () -> Unit,
) {
    Column(
        verticalArrangement = spacedBy(16.dp),
        modifier = modifier.padding(16.dp),
    ) {

        Row() {
            Text(
                text = "${stringResource(R.string.label_frame_number)} ${viewState.frame.number}",
                style = MaterialTheme.typography.titleLarge,
                modifier = Modifier.weight(1f),
            )
            Text(
                text = if (viewState.frame.dateTaken != null) {
                    TextUtil.formatDate(viewState.frame.dateTaken ?: System.currentTimeMillis())
                } else stringResource(R.string.select_date),
                modifier = Modifier.clickable {
                    onSelectDate()
                }
            )
        }

        Row(
            horizontalArrangement = spacedBy(16.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {

            TextField(
                state = viewState.speedState,
                label = { Text(text = stringResource(R.string.label_frame_shutter)) },
                keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number),
                modifier = Modifier.weight(0.5f),
            )

            TextField(
                state = viewState.apertureState,
                label = { Text(text = stringResource(R.string.label_aperture)) },
                keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number),
                modifier = Modifier.weight(0.5f),
            )
        }

        Row(
            horizontalArrangement = spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Checkbox(
                checked = viewState.checkedState.value,
                onCheckedChange = {
                    viewState.checkedState.value = !(viewState.checkedState.value)
                },
                modifier = Modifier,
            )
            Text(
                text = stringResource(R.string.long_exposure),
                modifier = Modifier.clickable {
                    viewState.checkedState.value = !(viewState.checkedState.value)
                }
            )
        }

        TextField(
            state = viewState.tagsState,
            label = { Text(text = stringResource(R.string.label_frame_tags)) },
            modifier = Modifier.fillMaxWidth(),
        )

        TextField(
            state = viewState.notesState,
            label = { Text(text = stringResource(R.string.label_frame_notes)) },
            lineLimits = TextFieldLineLimits.MultiLine(
                minHeightInLines = 6,
            ),
            modifier = Modifier.fillMaxWidth(),
        )

        Row() {
            Text(
                text = stringResource(R.string.label_location),
                modifier = Modifier.clickable{
                    onShowLocation(viewState.frame.location)
                }
            )
            Text(
                text = "${viewState.frame.location}",
                modifier = Modifier.clickable{
                    onShowLocation(viewState.frame.location)
                }
            )
            Text(
                text = stringResource(R.string.action_location),
                modifier = Modifier.clickable{
                    onUpdateLocation()
                }
            )
        }

    }
}

@Preview(showBackground = true)
@Composable
private fun Preview() {
    AppScaffold { padding ->
        EditFrameView(
            EditFrameViewState(
                roll = Roll(),
                frame = Frame(),
            ),
            Modifier.padding(padding),
            onSelectDate = {},
            onUpdateLocation = {},
            onShowLocation = {},
        )
    }

}
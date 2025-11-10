package be.hcpl.android.filmtag.ui.view

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement.Absolute.spacedBy
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.text.input.TextFieldLineLimits
import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Checkbox
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import be.hcpl.android.filmtag.R
import be.hcpl.android.filmtag.model.Frame
import be.hcpl.android.filmtag.model.Location
import be.hcpl.android.filmtag.model.Roll


data class EditFrameViewState(
    val roll: Roll, // this roll is only used for the initial state values
    val frame: Frame,
    val formattedShutter: String? = null,
    val formattedAperture: String? = null,
    val formattedTags: String? = null,
) {
    val speedState = TextFieldState(initialText = formattedShutter.orEmpty())
    val apertureState = TextFieldState(initialText = formattedAperture.orEmpty())
    val tagsState = TextFieldState(initialText = formattedTags.orEmpty())
    val notesState = TextFieldState(initialText = frame.notes.orEmpty())
    val checkedState = mutableStateOf(frame.isLongExposure)
    // TODO add time selection (requested feature)
}

@Composable
fun EditFrameView(
    viewState: EditFrameViewState, // all form inputs
    formattedDate: String?, // formatted date
    formattedLocation: String?, // formatted location
    modifier: Modifier = Modifier,
    onSelectDate: () -> Unit,
    onShowLocation: (Location?) -> Unit,
    onUpdateLocation: () -> Unit,
) {
    Column(
        verticalArrangement = spacedBy(16.dp),
        modifier = modifier
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
    ) {

        Row() {
            Text(
                text = "${stringResource(R.string.label_frame_number)} ${viewState.frame.number}",
                style = MaterialTheme.typography.titleLarge,
                modifier = Modifier.weight(1f),
            )
            Text(
                text = if (formattedDate != null) {
                    formattedDate
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

        TextField(
            state = viewState.tagsState,
            label = { Text(text = stringResource(R.string.label_frame_tags)) },
            modifier = Modifier.fillMaxWidth(),
        )

        TextField(
            state = viewState.notesState,
            label = { Text(text = stringResource(R.string.label_frame_notes)) },
            lineLimits = TextFieldLineLimits.MultiLine(
                minHeightInLines = 4,
                maxHeightInLines = 4,
            ),
            modifier = Modifier.fillMaxWidth(),
        )

        Row(
            horizontalArrangement = spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Icon(
                painter = painterResource(R.drawable.ic_add_location),
                contentDescription = stringResource(R.string.label_location),
                modifier = Modifier
                    .size(42.dp)
                    .padding(8.dp)
                    .clickable {
                        onUpdateLocation()
                    }
            )
            formattedLocation?.let { formattedLocation ->
                Text(
                    text = formattedLocation,
                    maxLines = 2,
                    modifier = Modifier
                        .weight(0.6f)
                        .clickable {
                            onShowLocation(viewState.frame.location)
                        }
                )
            }?: Text(
                text = stringResource(R.string.label_location),
            )
            Text(
                text = "(${stringResource(R.string.action_location)})",
                textDecoration = TextDecoration.Underline,
                modifier = Modifier
                    .weight(0.3f)
                    .clickable {
                        onUpdateLocation()
                    }
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

    }
}

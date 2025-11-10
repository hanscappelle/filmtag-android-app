package be.hcpl.android.filmtag.ui.view

import androidx.compose.foundation.layout.Arrangement.Absolute.spacedBy
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import be.hcpl.android.filmtag.R


data class EditSettingsViewState(
    val defaultIso: String,
    val defaultFrameCount: String,
    val defaultShutter: String,
    val defaultAperture: String,
) {
    val isoState = TextFieldState(initialText = defaultIso)
    val frameState = TextFieldState(initialText = defaultFrameCount)
    val shutterState = TextFieldState(initialText = defaultShutter)
    val apertureState = TextFieldState(initialText = defaultAperture)

    // TODO also create a limitation on the text shown in frame overview?
}

@Composable
fun EditSettingsView(
    viewState: EditSettingsViewState,
    modifier: Modifier = Modifier,
) {
    Column(
        verticalArrangement = spacedBy(16.dp),
        modifier = modifier
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
    ) {

        TextField(
            state = viewState.isoState,
            label = { Text(text = stringResource(R.string.pref_title_default_iso)) },
            keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number),
            modifier = Modifier.fillMaxWidth(),
        )

        TextField(
            state = viewState.frameState,
            label = { Text(text = stringResource(R.string.pref_title_default_frames)) },
            keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number),
            modifier = Modifier.fillMaxWidth(),
        )

        TextField(
            state = viewState.shutterState,
            label = { Text(text = stringResource(R.string.pref_title_default_shutter)) },
            keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number),
            modifier = Modifier.fillMaxWidth(),
        )

        TextField(
            state = viewState.apertureState,
            label = { Text(text = stringResource(R.string.pref_title_default_aperture)) },
            keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number),
            modifier = Modifier.fillMaxWidth(),
        )

    }
}

package be.hcpl.android.filmtag.ui.view

import androidx.compose.foundation.Image
import be.hcpl.android.filmtag.R
import androidx.compose.foundation.layout.Row
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import be.hcpl.android.filmtag.model.Roll

@Composable
fun RollView(
    roll: Roll,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier,
    ) {
        //Image(
        //Icon(
        //    imageVector = Icons.Filled.Lock,
        //    contenteDescription = stringResource(R.string.description_locked_unlocked),
        //)
        //)
        Text(
            text = roll.type ?: "...", // TODO create uiModel in between that holds this kind of placeholders
        )
    }
}
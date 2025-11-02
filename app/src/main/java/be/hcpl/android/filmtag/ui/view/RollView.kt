package be.hcpl.android.filmtag.ui.view

import android.R.attr.spacing
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement.Absolute.spacedBy
import androidx.compose.foundation.layout.Column
import be.hcpl.android.filmtag.R
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import be.hcpl.android.filmtag.model.Roll

@Composable
fun RollView(
    roll: Roll,
    modifier: Modifier = Modifier,
) {
    Column(
        verticalArrangement = spacedBy(4.dp),
        modifier = modifier
            .padding(8.dp)
            .heightIn(min = 48.dp)
            .fillMaxWidth(),
    ) {

        Row(
            horizontalArrangement = spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier,
        ) {
            Icon(
                imageVector = Icons.Filled.Lock,
                contentDescription = stringResource(R.string.description_locked_unlocked),
                modifier = Modifier
                    // mark developed items with a lighter text color
                    .alpha(if(roll.isDeveloped) 1f else 0.2f)
            )
            Text(
                text = roll.type?.ifEmpty { "..." } ?: "...", // TODO create uiModel in between that holds this kind of placeholders
                style = MaterialTheme.typography.titleLarge,
                modifier = Modifier
                    .weight(1f),
            )
        }
        Row(horizontalArrangement = spacedBy(8.dp)) {
            Text(
                text = stringResource(R.string.label_roll_speed) + " " + roll.speed,
                modifier = Modifier
                    .weight(1f),
            )
            Text(
                text = roll.frames.toString() + " " + stringResource(R.string.label_roll_frames),
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun Preview() {
    RollView(
        roll = Roll(
            id = 1000L,
            type = "Roll Type Info",
            speed = 200,
            frames = 36,
            notes = "some roll specific notes",
            isDeveloped = false,
            tags = listOf("tag1", "tag2", "tag3"),
        )
    )
}

@Preview(showBackground = true)
@Composable
private fun PreviewEmpty() {
    RollView(
        roll = Roll(
            id = 1000L,
            type = null,
            speed = 200,
            frames = 36,
            notes = "some roll specific notes",
            isDeveloped = false,
            tags = listOf("tag1", "tag2", "tag3"),
        )
    )
}
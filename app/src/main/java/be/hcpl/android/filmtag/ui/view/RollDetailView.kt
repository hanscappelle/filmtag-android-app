package be.hcpl.android.filmtag.ui.view

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement.Absolute.spacedBy
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import be.hcpl.android.filmtag.model.Roll

@Composable
fun RollDetailView(
    roll: Roll,
    modifier: Modifier = Modifier,
) {
    Column(modifier) {

        Text(
            text = "${roll.type ?: "..."} @ ${roll.speed} # ${roll.frames}",
            color = MaterialTheme.colorScheme.inverseOnSurface,
            style = MaterialTheme.typography.titleLarge,
            modifier = modifier
                .background(color = MaterialTheme.colorScheme.inverseSurface)
                .padding(16.dp)
                .fillMaxWidth(),
        )
        Row(
            horizontalArrangement = spacedBy(8.dp),
            modifier = Modifier.padding(8.dp),
        ) {
            roll.tags.forEach { tag ->
                Text(text = tag)
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun Preview() {
    RollDetailView(
        Roll(
            type = "film type",
            speed = 200,
            frames = 24,
            notes = "notes",
            isDeveloped = true,
            tags = listOf("tag1", "tag2", "tag3")
        ),
    )
}

@Preview(showBackground = true)
@Composable
private fun PreviewDefaults() {
    RollDetailView(
        Roll(),
    )
}
package be.hcpl.android.filmtag.ui.activity

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.ui.Modifier
import androidx.core.bundle.bundleOf
import be.hcpl.android.filmtag.ui.AppScaffold
import be.hcpl.android.filmtag.ui.view.FrameView
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf
import kotlin.getValue

class FilmRollActivity : ComponentActivity() {

    private val viewModel: FilmRollViewModel by viewModel(
        parameters = { parametersOf(intent.getLongExtra(KEY_FILM_ROLL, -1)) }
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        viewModel.state.observe(this, ::handleState)
        viewModel.events.observe(this, ::handleEvent)
    }

    private fun handleState(state: FilmRollViewModel.State) {
        setContent {
            AppScaffold { innerPadding ->
                LazyColumn(
                    modifier = Modifier
                        .padding(innerPadding),
                ) {
                    state.frames.forEachIndexed { index, frame ->
                        item(key = index) {
                            FrameView(frame)
                        }
                    }
                }

            }
        }
    }

    private fun handleEvent(event: FilmRollViewModel.Event) {

    }

    companion object {
        const val KEY_FILM_ROLL = "KEY_FILM_ROLL"

        fun bundleWith(rollId: Long) = bundleOf(KEY_FILM_ROLL to rollId)
    }
}
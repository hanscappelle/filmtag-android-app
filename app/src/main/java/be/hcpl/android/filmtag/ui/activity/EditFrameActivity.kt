package be.hcpl.android.filmtag.ui.activity


import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.ui.Modifier
import be.hcpl.android.filmtag.R
import be.hcpl.android.filmtag.ui.Action
import be.hcpl.android.filmtag.ui.ActionId
import be.hcpl.android.filmtag.ui.AppScaffold
import be.hcpl.android.filmtag.ui.activity.EditRollViewModel.Event
import be.hcpl.android.filmtag.ui.activity.FilmRollActivity.Companion.KEY_FILM_ROLL
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf

class EditFrameActivity : ComponentActivity() {

    private val viewModel: EditFrameViewModel by viewModel(
        parameters = { parametersOf(intent.getIntExtra(KEY_FRAME_ID, -1)) }
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel.state.observe(this, ::handleState)
        viewModel.events.observe(this, ::handleEvent)
    }

    private fun handleState(state: EditFrameViewModel.State) {
        setContent {
            AppScaffold(
                actions = listOf(

                    // TODO more actions needed here

                    Action(
                        iconRes = R.drawable.ic_action_close,
                        textRes = R.string.action_close,
                        actionId = ActionId.Close,
                    ),
                ),
                handleAction = ::handleAction,
            ) { innerPadding ->
                // TODO render edit frame form with state
                Box(modifier = Modifier.padding(innerPadding)) {
                    Text("selected frame number is ${state.frameId}")
                }
            }
        }
    }

    private fun handleAction(actionId: ActionId) {
        when (actionId) {
            ActionId.Close -> finish()
            else -> TODO()
        }
    }

    private fun handleEvent(event: EditFrameViewModel.Event) {
        when (event) {
            Event.Close -> finish()
        }
    }

    companion object {
        const val KEY_FRAME_ID = "KEY_FRAME_ID"
    }
}

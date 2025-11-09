package be.hcpl.android.filmtag.ui.activity


import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import be.hcpl.android.filmtag.R
import be.hcpl.android.filmtag.ui.Action
import be.hcpl.android.filmtag.ui.ActionId
import be.hcpl.android.filmtag.ui.AppScaffold
import be.hcpl.android.filmtag.ui.activity.FilmRollActivity.Companion.KEY_FILM_ROLL_ID
import be.hcpl.android.filmtag.ui.view.DatePickerModal
import be.hcpl.android.filmtag.ui.view.EditFrameView
import be.hcpl.android.filmtag.ui.view.RollDetailView
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf

class EditFrameActivity : ComponentActivity() {

    private val viewModel: EditFrameViewModel by viewModel(
        parameters = {
            parametersOf(
                intent.getLongExtra(KEY_FILM_ROLL_ID, -1L),
                intent.getIntExtra(KEY_FRAME_ID, -1),
            )
        }
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
                    Action(
                        iconRes = R.drawable.ic_action_check,
                        textRes = R.string.action_create,
                        actionId = ActionId.Create,
                    ),
                    Action(
                        iconRes = R.drawable.ic_action_close,
                        textRes = R.string.action_close,
                        actionId = ActionId.Close,
                    ),
                ),
                handleAction = ::handleAction,
            ) { innerPadding ->

                // modal date picker
                val showDatePicker = remember { mutableStateOf(false) }
                if (showDatePicker.value) {
                    DatePickerModal(
                        onDateSelected = { selectedDate ->
                            viewModel.updateSelectedDate(selectedDate)
                            showDatePicker.value = false
                        },
                        onDismiss = {
                            showDatePicker.value = false
                        },
                    )
                }

                // edit form fields populated from state
                Column(modifier = Modifier.padding(innerPadding)) {
                    RollDetailView(state.roll)
                    EditFrameView(
                        viewState = state.editFrameState,
                        onSelectDate = {
                            showDatePicker.value = true
                        }
                    )
                }
            }
        }
    }

    private fun handleAction(actionId: ActionId) {
        when (actionId) {
            ActionId.Close -> finish()
            ActionId.Create -> viewModel.saveFrame()
            else -> TODO()
        }
    }

    private fun handleEvent(event: EditFrameViewModel.Event) {
        when (event) {
            EditFrameViewModel.Event.Close -> finish()
        }
    }

    companion object {
        const val KEY_FRAME_ID = "KEY_FRAME_ID"
    }
}

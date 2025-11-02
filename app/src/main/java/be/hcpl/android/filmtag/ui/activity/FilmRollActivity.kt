package be.hcpl.android.filmtag.ui.activity

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.appcompat.app.AlertDialog
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.ui.Modifier
import be.hcpl.android.filmtag.R
import be.hcpl.android.filmtag.ui.Action
import be.hcpl.android.filmtag.ui.ActionId
import be.hcpl.android.filmtag.ui.AppScaffold
import be.hcpl.android.filmtag.ui.view.FrameView
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf
import kotlin.getValue

class FilmRollActivity : ComponentActivity() {

    private val viewModel: FilmRollViewModel by viewModel(
        parameters = { parametersOf(intent.getLongExtra(KEY_FILM_ROLL, -1L)) }
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        viewModel.state.observe(this, ::handleState)
        viewModel.events.observe(this, ::handleEvent)
    }

    private fun handleState(state: FilmRollViewModel.State) {
        setContent {
            AppScaffold(
                actions = listOf(
                    Action(
                        iconRes = if (state.roll?.isDeveloped == true) R.drawable.ic_lock_closed else R.drawable.ic_lock_open,
                        textRes = R.string.action_edit,
                        actionId = ActionId.Update,
                    ),
                    Action(
                        iconRes = R.drawable.ic_action_edit,
                        textRes = R.string.action_edit,
                        actionId = ActionId.Create,
                    ),
                    Action(
                        iconRes = R.drawable.ic_action_delete_forever,
                        textRes = R.string.action_delete,
                        actionId = ActionId.Delete,
                    ),
                    Action(
                        iconRes = R.drawable.ic_action_close,
                        textRes = R.string.action_close,
                        actionId = ActionId.Close,
                    ),
                ),
                handleAction = ::handleAction,
            ) { innerPadding ->
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
        when (event) {
            is FilmRollViewModel.Event.EditRoll -> {
                val intent = Intent(this, EditRollActivity::class.java).apply {
                    putExtra(KEY_FILM_ROLL, event.rollId)
                }
                startActivity(intent)
            }

            FilmRollViewModel.Event.Close -> finish()
        }
    }

    private fun handleAction(actionId: ActionId) {
        when (actionId) {
            ActionId.Close -> finish()
            ActionId.Create -> viewModel.preparedEditRoll()
            ActionId.Delete -> confirmDeleteRoll()
            ActionId.Update -> viewModel.toggleLocked()
            else -> TODO()
        }
    }

    private fun confirmDeleteRoll() {
        // confirmation needed before delete here...
        AlertDialog.Builder(this)
            .setMessage(R.string.msg_delete_complete_film_roll)
            .setPositiveButton(R.string.label_yes) { _, _ ->
                viewModel.deleteRoll()
            }.setNegativeButton(R.string.label_no) { _, _ -> }.show()

    }

    /*

    private fun updateFrame(index: Int) {
        //findNavController().navigate(
        //    R.id.action_edit_frame, bundleOf(
        //        EditFrameFragment.KEY_FRAMES to frames as ArrayList<*>,
         ////       EditFrameFragment.KEY_FRAME_IDX to index,
          //      EditFrameFragment.KEY_ROLL to filmRoll
          //  )
       // )
    }

     */

    companion object {
        const val KEY_FILM_ROLL = "KEY_FILM_ROLL"
    }
}
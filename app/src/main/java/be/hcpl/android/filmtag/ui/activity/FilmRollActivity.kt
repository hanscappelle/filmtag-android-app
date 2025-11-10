package be.hcpl.android.filmtag.ui.activity

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.appcompat.app.AlertDialog
import androidx.compose.foundation.layout.padding
import androidx.compose.ui.Modifier
import be.hcpl.android.filmtag.R
import be.hcpl.android.filmtag.ui.Action
import be.hcpl.android.filmtag.ui.ActionId
import be.hcpl.android.filmtag.ui.AppScaffold
import be.hcpl.android.filmtag.ui.activity.EditFrameActivity.Companion.KEY_FRAME_ID
import be.hcpl.android.filmtag.ui.view.ListFramesView
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf
import kotlin.getValue

class FilmRollActivity : ComponentActivity() {

    private val viewModel: FilmRollViewModel by viewModel(
        parameters = { parametersOf(intent.getLongExtra(KEY_FILM_ROLL_ID, -1L)) }
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        viewModel.state.observe(this, ::handleState)
        viewModel.events.observe(this, ::handleEvent)
    }

    override fun onResume() {
        super.onResume()
        viewModel.onResume()
    }

    private fun handleState(state: FilmRollViewModel.State) {
        setContent {
            AppScaffold(
                actions = listOf(
                    Action(
                        iconRes = if (state.roll.isDeveloped) R.drawable.ic_lock_closed else R.drawable.ic_lock_open,
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
                        iconRes = R.drawable.ic_action_download,
                        textRes = R.string.action_export,
                        actionId = ActionId.Export,
                    ),
                    Action(
                        iconRes = R.drawable.ic_action_close,
                        textRes = R.string.action_close,
                        actionId = ActionId.Close,
                    ),
                ),
                handleAction = ::handleAction,
            ) { innerPadding ->
                ListFramesView(
                    roll = state.roll,
                    frames = state.frames,
                    modifier = Modifier
                        .padding(innerPadding),
                    onSelect = { index -> viewModel.prepareEditFrame(index) },
                )
            }
        }
    }

    private fun onFrameSelected(rollId: Long, frameId: Int) {
        val intent = Intent(this, EditFrameActivity::class.java).apply {
            putExtra(KEY_FILM_ROLL_ID, rollId)
            putExtra(KEY_FRAME_ID, frameId)
        }
        startActivity(intent)
    }

    private fun handleEvent(event: FilmRollViewModel.Event) {
        when (event) {
            is FilmRollViewModel.Event.EditRoll -> {
                val intent = Intent(this, EditRollActivity::class.java).apply {
                    putExtra(KEY_FILM_ROLL_ID, event.rollId)
                }
                startActivity(intent)
            }

            FilmRollViewModel.Event.Close -> finish()
            is FilmRollViewModel.Event.EditFrame -> onFrameSelected(event.rollId, event.frameId)
            is FilmRollViewModel.Event.ExportText -> finishShare(event.text)
        }
    }

    private fun finishShare(exportedFormat: String) {
        val sharingIntent = Intent(Intent.ACTION_SEND)
        sharingIntent.type = "text/plain"
        sharingIntent.putExtra(Intent.EXTRA_SUBJECT, "FilmTag data export")
        sharingIntent.putExtra(
            Intent.EXTRA_TEXT,
            exportedFormat,
        )
        startActivity(
            Intent.createChooser(
                sharingIntent,
                resources.getString(R.string.action_export)
            )
        )
    }

    private fun handleAction(actionId: ActionId) {
        when (actionId) {
            ActionId.Close -> finish()
            ActionId.Create -> viewModel.preparedEditRoll()
            ActionId.Delete -> confirmDeleteRoll()
            ActionId.Update -> viewModel.toggleLocked()
            ActionId.Export -> viewModel.prepareExport()
            else -> Unit
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

    companion object {
        const val KEY_FILM_ROLL_ID = "KEY_FILM_ROLL_ID"
    }
}
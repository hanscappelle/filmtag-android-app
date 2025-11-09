package be.hcpl.android.filmtag.ui.activity

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AlertDialog.*
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.ui.Modifier
import be.hcpl.android.filmtag.R
import be.hcpl.android.filmtag.ui.Action
import be.hcpl.android.filmtag.ui.ActionId
import be.hcpl.android.filmtag.ui.AppScaffold
import be.hcpl.android.filmtag.ui.activity.FilmRollActivity.Companion.KEY_FILM_ROLL_ID
import be.hcpl.android.filmtag.ui.activity.MainViewModel.Event.ShowToggleLock
import be.hcpl.android.filmtag.ui.view.RollView
import org.koin.androidx.viewmodel.ext.android.viewModel
import kotlin.getValue

/**
 * main entry point of app
 */
@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
class MainActivity : ComponentActivity() {

    private val viewModel: MainViewModel by viewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        viewModel.state.observe(this, ::handleState)
        viewModel.events.observe(this, ::handleEvent)

        handleIntentData()
    }

    override fun onResume() {
        super.onResume()
        viewModel.onResume()
    }

    private fun handleState(state: MainViewModel.State) {
        setContent {
            AppScaffold(
                actions = listOf(
                    // TODO restore settings...
                    // android:title="@string/action_settings"
                    // FIXME restore filtering in content here
                    //android:id="@+id/action_search"
                    Action(
                        iconRes = R.drawable.ic_action_add,
                        textRes = R.string.action_add,
                        actionId = ActionId.Create,
                    ),
                    Action(
                        iconRes = R.drawable.ic_action_download,
                        textRes = R.string.action_export,
                        actionId = ActionId.Export,
                    ),
                    Action(
                        iconRes = R.drawable.ic_action_help,
                        textRes = R.string.msg_first_view_help,
                        actionId = ActionId.Help,
                    ),
                    Action(
                        iconRes = R.drawable.ic_action_info,
                        textRes = R.string.action_about,
                        actionId = ActionId.Info,
                    ),
                ),
                handleAction = { actionId -> handleAction(actionId) }
            ) { innerPadding ->
                LazyColumn(
                    modifier = Modifier
                        .padding(innerPadding),
                ) {
                    state.rolls.forEachIndexed { index, roll ->
                        item(key = roll.id) {
                            RollView(
                                roll = roll,
                                modifier = Modifier.combinedClickable(
                                    onClick = { showRollDetails(roll.id) },
                                    onLongClick = { showToggleLock(roll.id) },
                                )
                            )
                        }
                    }
                }
            }
        }
    }

    private fun handleEvent(event: MainViewModel.Event) {
        when (event) {
            is ShowToggleLock -> {
                val optionText = if (event.isDeveloped == true) R.string.option_roll_unlock else R.string.option_roll_lock
                Builder(this)
                    .setMessage(R.string.msg_lock_complete_film_roll)
                    .setPositiveButton(optionText) { _, _ ->
                        viewModel.toggleLock(event.rollId)
                    }.setNegativeButton(R.string.option_cancel) { _, _ -> Unit }.show()
            }

            is MainViewModel.Event.ShareConfig -> finishShareConfig(event.exportedFormat)
            is MainViewModel.Event.ImportResult -> Toast.makeText(this, event.textRes, Toast.LENGTH_SHORT).show()
        }
    }

    private fun handleAction(actionId: ActionId) {
        when (actionId) {
            ActionId.Create -> startActivity(Intent(this, EditRollActivity::class.java))
            ActionId.Export -> shareConfig()
            ActionId.Help -> showHelp()
            ActionId.Info -> startActivity(Intent(this, AboutActivity::class.java))
            ActionId.Close -> finish()
            else -> TODO()
        }
    }

    private fun showRollDetails(rollId: Long) {
        val intent = Intent(this, FilmRollActivity::class.java).apply {
            putExtra(KEY_FILM_ROLL_ID, rollId)
        }
        startActivity(intent)
    }

    private fun showToggleLock(rollId: Long) {
        // optional lock or unlock for film here
        viewModel.showToggleLock(rollId)
    }

    private fun showHelp() {
        AlertDialog.Builder(this)
            .setMessage(R.string.msg_first_view_help)
            .setPositiveButton(R.string.label_ok) { _, _ -> }
            .show()
    }

    private fun shareConfig() {
        viewModel.prepareShareConfig()
    }

    private fun finishShareConfig(exportedFormat: String) {
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

    private fun handleIntentData() {
        // check for intent data here
        // Get intent, action and MIME type
        val intent = intent
        val action = intent.action
        val type = intent.type

        if (Intent.ACTION_SEND == action && type != null) {
            if ("text/plain" == type) {
                handleSharedConfig(intent) // Handle text being sent
            }
        }
    }

    private fun handleSharedConfig(intent: Intent) {
        var sharedText: String? = intent.getStringExtra(Intent.EXTRA_TEXT)
        if (sharedText == null) {
            Toast.makeText(this, R.string.err_missing_data, Toast.LENGTH_SHORT).show()
        }
        viewModel.handleSharedConfig(sharedText)
    }

}

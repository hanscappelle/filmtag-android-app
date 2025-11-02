package be.hcpl.android.filmtag.ui.activity

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.appcompat.app.AlertDialog.Builder
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.ui.Modifier
import androidx.core.os.bundleOf
import be.hcpl.android.filmtag.R
import be.hcpl.android.filmtag.ui.activity.MainViewModel.Event.ShowToggleLock
import be.hcpl.android.filmtag.ui.AppScaffold
import be.hcpl.android.filmtag.ui.activity.FilmRollActivity.Companion.KEY_FILM_ROLL
import be.hcpl.android.filmtag.ui.view.RollView
import be.hcpl.android.filmtag.util.StorageUtil
import org.koin.androidx.viewmodel.ext.android.viewModel
import kotlin.getValue

/**
 * main entry point of app
 */
@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
class MainActivity : ComponentActivity() {

    // TODO removed
    val prefs: SharedPreferences? = null

    private val viewModel: MainViewModel by viewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // TODO check if needed
        //enableEdgeToEdge()

        viewModel.state.observe(this, ::handleState)
        viewModel.events.observe(this, ::handleEvent)

        // TODO restore fab if desired

        handleIntentData()
    }

    private fun handleState(state: MainViewModel.State) {
        setContent {
            AppScaffold { innerPadding ->
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
                    }.setNegativeButton(R.string.option_roll_cancel) { _, _ -> Unit }.show()
            }

        }
    }

    private fun showRollDetails(rollId: Long) {
        val intent = Intent(this, FilmRollActivity::class.java).apply {
            putExtra(KEY_FILM_ROLL, rollId)
        }
        startActivity(intent)
    }

    private fun showToggleLock(rollId: Long) {
        // optional lock or unlock for film here
        viewModel.showToggleLock(rollId)
    }

    /*
    //TODO convert actions

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        // update options based on search enabled or not
        //if (!searchViewEnabled)
        inflater.inflate(R.menu.rolls, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val id = item.itemId
        return if (id == R.id.action_add) {
           // findNavController().navigate(R.id.action_add_roll)
            true
        } else if (id == R.id.action_info) {
            showInfo()
            true
        } else if (id == R.id.action_export) {
            shareConfig()
            true
        } else if (id == R.id.action_import) {
            importConfig()
            true
            //} else if (id == R.id.action_search) {
            //    toggleSearchView()
        } else {
            super.onOptionsItemSelected(item)
        }
    }

    private fun showInfo() {
        AlertDialog.Builder(requireContext())
            .setMessage(R.string.msg_first_view_help)
            .setPositiveButton(R.string.ok) { _, _ -> }
            .show()
    }

    private fun importConfig() {
        val builder = AlertDialog.Builder(activity)
        builder.setMessage(R.string.info_import_export)
            .setCancelable(true)
            .setPositiveButton(R.string.ok) { dialog, _ ->
                dialog.dismiss()
            }
        val alert = builder.create()
        alert.show()
    }

    private fun shareConfig() {
        val sharingIntent = Intent(Intent.ACTION_SEND)
        sharingIntent.type = "text/plain"
        sharingIntent.putExtra(Intent.EXTRA_SUBJECT, "FilmTag data export")
        sharingIntent.putExtra(
            Intent.EXTRA_TEXT,
            StorageUtil.getExportDataFormattedAsText(activity as MainActivity)
        )
        startActivity(
            Intent.createChooser(
                sharingIntent,
                resources.getString(R.string.action_export)
            )
        )
    }

     */


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

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        return when (item.itemId) {
            R.id.action_settings -> {
                //navController.navigate(R.id.action_settings)
                true
            }

            R.id.action_about -> {
                //navController.navigate(R.id.action_about)
                true
            }

            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun handleSharedConfig(intent: Intent) {
        var sharedText: String? = intent.getStringExtra(Intent.EXTRA_TEXT)
        if (sharedText == null) {
            Toast.makeText(this, R.string.err_missing_data, Toast.LENGTH_SHORT).show()
        }
        // TODO also move to viewModel

        // remove everything before the { character indicating proper formatted text, this was
        // required for use with Google Note for example where the title was in front
        sharedText = sharedText?.substring(sharedText.indexOf("{")).orEmpty()

        // try to import data here
        try {
            // try parsing data
            val data = StorageUtil.parseDataExportFormat(sharedText)
            StorageUtil.storeDataExportFormat(this, data)
            Toast.makeText(this, R.string.info_data_imported, Toast.LENGTH_SHORT).show()
        } catch (_: Exception) {
            Toast.makeText(this, R.string.err_import_failed, Toast.LENGTH_SHORT).show()
        }

    }

}

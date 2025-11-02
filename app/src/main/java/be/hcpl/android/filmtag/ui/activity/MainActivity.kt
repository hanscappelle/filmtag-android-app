package be.hcpl.android.filmtag.ui.activity

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.ui.Modifier
import be.hcpl.android.filmtag.R
import be.hcpl.android.filmtag.ui.view.RollView
import be.hcpl.android.filmtag.util.StorageUtil
import org.koin.androidx.viewmodel.ext.android.viewModel
import kotlin.getValue

/**
 * main entry point of app
 */
@OptIn(ExperimentalMaterial3Api::class)
class MainActivity : ComponentActivity() {

    // TODO removed
    val prefs: SharedPreferences? = null

    private val viewModel: MainViewModel by viewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // TODO check if needed
        //enableEdgeToEdge()

        viewModel.state.observe(this, ::handleState)
        //viewModel.events.observe(this, ::handleEvent)

        // TODO restore fab if desired

        handleIntentData()
    }

    private fun handleState(state: MainViewModel.State) {
        setContent {
            Scaffold(
                /*
                // TODO check if top bar is needed or we stick to activity
                // android:theme="@style/Theme.AppCompat.DayNight.NoActionBar"
                topBar = {
                    TopAppBar(
                        colors = TopAppBarDefaults.topAppBarColors(
                            containerColor = MaterialTheme.colorScheme.primaryContainer,
                            titleContentColor = MaterialTheme.colorScheme.primary,
                        ),
                        title = {
                            Text("Small Top App Bar")
                        }
                    )
                },*/
            ) { innerPadding ->

                // TODO move to a separate screen Composable
                LazyColumn(
                    modifier = Modifier.padding(innerPadding)
                ) {
                    state.rolls.forEachIndexed { index, roll ->
                        item(key = index) {
                            RollView(roll = roll)
                        }
                    }

                }

            }


        }
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
        // TODO move to viewModel

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

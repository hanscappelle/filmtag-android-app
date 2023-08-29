package be.hcpl.android.filmtag

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.preference.PreferenceManager
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import be.hcpl.android.filmtag.databinding.ActivityMainBinding

import be.hcpl.android.filmtag.template.TemplateFragment
import be.hcpl.android.filmtag.util.CommonUtil
import be.hcpl.android.filmtag.util.StorageUtil
import com.google.android.material.snackbar.Snackbar

/**
 * main entry point of app
 */
class MainActivity : AppCompatActivity() {

    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var binding: ActivityMainBinding

    /**
     * use for preferences, app preferences, only kept here
     * @return
     */
    var prefs: SharedPreferences? = null
        private set


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbar)

        val navController = findNavController(R.id.nav_host_fragment_content_main)
        appBarConfiguration = AppBarConfiguration(navController.graph)
        setupActionBarWithNavController(navController, appBarConfiguration)

        binding.fab.setOnClickListener { view ->
            Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                .setAction("Action", null).show()
        }

        // load the prefs here
        prefs = PreferenceManager.getDefaultSharedPreferences(applicationContext)

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
            R.id.action_settings -> true
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_fragment_content_main)
        return navController.navigateUp(appBarConfiguration)
                || super.onSupportNavigateUp()
    }

    private fun handleSharedConfig(intent: Intent) {
        var sharedText: String? = intent.getStringExtra(Intent.EXTRA_TEXT)
        if (sharedText == null) {
            Toast.makeText(this, R.string.err_missing_data, Toast.LENGTH_SHORT).show()
        }

        // remove everything before the { character indicating proper formatted text, this was
        // required for use with Google Note for example where the title was in front
        sharedText = sharedText!!.substring(sharedText.indexOf("{"))

        // try to import the data here
        try {
            // try parsing data
            val data = StorageUtil.parseDataExportFormat(sharedText)
            StorageUtil.storeDataExportFormat(this, data)
            Toast.makeText(this, R.string.info_data_imported, Toast.LENGTH_SHORT).show()
        } catch (e: Exception) {
            Toast.makeText(this, R.string.err_import_failed, Toast.LENGTH_SHORT).show()
        }

    }

    private val initialFragment: Fragment
        // FIXME restore get() = FilmRollListFragment.newInstance()
        get() = AboutFragment.newInstance()

    /**
     * use for changing currently visible fragment
     * @param fragment
     */
    fun switchContent(fragment: Fragment) {
        // store current fragment
        //content = fragment
        // switch content with history
        //supportFragmentManager
        //    .beginTransaction()
        //    .replace(R.id.container, fragment)
        //    .addToBackStack(fragment.javaClass.simpleName)
        //    .commit()
        // false by default
        //setHomeAsUp(false)
        // also hide keyboard here
        CommonUtil.hideSoftKeyboard(this)
    }

    override fun onBackPressed() {
        //if (content != null && content is TemplateFragment) {
        //    if ((content as TemplateFragment).onBackPressed())
        //        return
        //}
        // hide keyboard here also
        CommonUtil.hideSoftKeyboard(this)
        // finish if content is the film Roll overview fragment
        //if (supportFragmentManager.findFragmentById(R.id.container) is FilmRollListFragment) {
        //    finish()
        //} else {
        //    super.onBackPressed()
        //}
    }

    fun setHomeAsUp(enable: Boolean) {
        //if (supportActionBar != null) {
        //    supportActionBar!!.setDisplayHomeAsUpEnabled(enable)
            //getSupportActionBar().setHomeButtonEnabled(true);
        //}
    }

    companion object {

        private val KEY_CURRENT_CONTENT = "current_content"
    }

}

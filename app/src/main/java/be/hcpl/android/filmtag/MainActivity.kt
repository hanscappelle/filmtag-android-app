package be.hcpl.android.filmtag

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.preference.PreferenceManager
import android.support.v4.app.Fragment
import android.support.v7.app.AppCompatActivity
import android.widget.Toast

import be.hcpl.android.filmtag.template.TemplateFragment
import be.hcpl.android.filmtag.util.CommonUtil
import be.hcpl.android.filmtag.util.StorageUtil
import butterknife.ButterKnife

import kotlinx.android.synthetic.main.activity_main.*

/**
 * main entry point of app
 */
class MainActivity : AppCompatActivity() {

    /**
     * use for preferences, app preferences, only kept here
     * @return
     */
    var prefs: SharedPreferences? = null
        private set

    /**
     * the currently visible fragment
     */
    private var content: Fragment? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        ButterKnife.bind(this)

        // As we're using a Toolbar, we should retrieve it and set it
        // to be our ActionBar
        // thanks to kotlin android extensions we can import this from layout
        // Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar)

        // load the prefs here
        prefs = PreferenceManager.getDefaultSharedPreferences(applicationContext)

        val fm = supportFragmentManager
        // always starts with the same initial fragment
        //        fm.beginTransaction().replace(R.id.container, FilmRollListFragment.newInstance()).commit();
        // restore last fragment state if possible
        if (savedInstanceState != null) {
            content = fm.getFragment(savedInstanceState, KEY_CURRENT_CONTENT)
        }

        switchContent(initialFragment)

        // check for intent data here
        // Get intent, action and MIME typegit checkout simplified-frame-list
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
        get() = FilmRollListFragment.newInstance()

    /**
     * use for changing currently visible fragment
     * @param fragment
     */
    fun switchContent(fragment: Fragment) {
        // store current fragment
        content = fragment
        // switch content with history
        supportFragmentManager.beginTransaction().replace(R.id.container, fragment).addToBackStack(fragment.javaClass.simpleName).commit()
        // false by default
        setHomeAsUp(false)
        // also hide keyboard here
        CommonUtil.hideSoftKeyboard(this)
    }

    override fun onBackPressed() {
        if (content != null && content is TemplateFragment) {
            if ((content as TemplateFragment).onBackPressed())
                return
        }
        // hide keyboard here also
        CommonUtil.hideSoftKeyboard(this)
        // finish if content is the film Roll overview fragment
        if (supportFragmentManager.findFragmentById(R.id.container) is FilmRollListFragment) {
            finish()
        } else {
            super.onBackPressed()
        }
    }

    fun setHomeAsUp(enable: Boolean) {
        if (supportActionBar != null) {
            supportActionBar!!.setDisplayHomeAsUpEnabled(enable)
            //getSupportActionBar().setHomeButtonEnabled(true);
        }
    }

    companion object {

        private val KEY_CURRENT_CONTENT = "current_content"
    }

}

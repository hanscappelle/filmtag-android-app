package be.hcpl.android.filmtag

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.support.v7.preference.PreferenceFragmentCompat
import android.support.v7.widget.SearchView
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.widget.AdapterView

import be.hcpl.android.filmtag.adapter.FilmRollAdapter
import be.hcpl.android.filmtag.model.Roll
import be.hcpl.android.filmtag.template.TemplateFragment
import be.hcpl.android.filmtag.util.StorageUtil
import kotlinx.android.synthetic.main.fragment_roll_overview.*


/**
 * an overview of rolls created earlier + option to add new roll of film
 *
 *
 * Created by hcpl on 30/07/15.
 */
class FilmRollListFragment : TemplateFragment() {

    // TODO delete film from overview directly (swipe? long press, ...)

    private var mAdapter: FilmRollAdapter? = null

    private var searchView: SearchView? = null

    override val layoutResourceId: Int
        get() = R.layout.fragment_roll_overview

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // prepare the adapter for that list
        mAdapter = FilmRollAdapter(activity)
        list_rolls.adapter = mAdapter

        list_rolls.onItemClickListener = AdapterView.OnItemClickListener { _, _, i, _ ->
            showRollDetails(mAdapter!!.getItem(i))
        }

        // parent activity
        val mainActivity = activity as MainActivity

        // enable the view manually
        searchView = SearchView(mainActivity.supportActionBar!!.themedContext)
        searchView!!.setIconifiedByDefault(false)
        mainActivity.supportActionBar!!.customView = searchView
        // not enabled by default
        mainActivity.supportActionBar!!.setDisplayShowCustomEnabled(searchViewEnabled)
        // text listeners
        searchView!!.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String): Boolean {
                // filter data
                mAdapter!!.filter.filter(query)
                return true
            }

            override fun onQueryTextChange(newText: String): Boolean {
                if ("" == newText) {
                    // clear results
                    mAdapter!!.filter.filter(null)
                    return true
                }
                return false
            }
        })
        // not in use
        searchView!!.setOnCloseListener {
            mAdapter!!.filter.filter(null)
            true
        }
        // when editing and back used fiest focus goes away
        searchView!!.setOnQueryTextFocusChangeListener { _, b ->
            if (!b) {
                val query = searchView!!.query
                if (query != null && query.length > 0) {
                    mAdapter!!.filter.filter(query)
                } else {
                    mAdapter!!.filter.filter(null)
                    toggleSearchView()
                }
            }
        }

    }

    override fun onResume() {
        super.onResume()

        // retrieve list of frames here
        refreshData()
    }

    private fun refreshData() {
        val rolls = StorageUtil.getAllRolls(activity as MainActivity)
        // update adapter
        mAdapter!!.clear()
        mAdapter!!.addAll(rolls)
        mAdapter!!.notifyDataSetChanged()
    }


    private fun showRollDetails(roll: Roll) {
        // show frames on selection
        (activity as MainActivity).switchContent(FilmFrameListFragment.newInstance(roll))
    }

    // create new roll option is in main activity

    override fun onCreateOptionsMenu(menu: Menu?, inflater: MenuInflater?) {
        // update options based on search enabled or not
        if (!searchViewEnabled)
            inflater!!.inflate(R.menu.rolls, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        val id = item!!.itemId
        if (id == R.id.action_add) {
            createNewRoll()
            return true
        } else if (id == R.id.action_export) {
            shareConfig()
            return true
        } else if (id == R.id.action_import) {
            importConfig()
            return true
        } else if (id == R.id.action_about) {
            (activity as MainActivity).switchContent(AboutFragment.newInstance())
            return true
        } else if (id == R.id.action_search) {
            toggleSearchView()
        } else if (id == R.id.action_settings) {
            (activity as MainActivity).switchContent(PrefsFragment())
        }
        return super.onOptionsItemSelected(item)
    }

    class PrefsFragment : PreferenceFragmentCompat() {
        override fun onCreatePreferences(bundle: Bundle?, s: String?) {
            // Load the preferences from an XML resource
            addPreferencesFromResource(R.xml.preferences)
        }
    }

    private var searchViewEnabled = false

    /**
     * helper for showing/hiding the searchview in the toolbar
     */
    private fun toggleSearchView() {
        // parent activity
        val activity = activity as MainActivity
        // toggle value
        searchViewEnabled = !searchViewEnabled
        // and apply
        activity.supportActionBar!!.setDisplayShowCustomEnabled(searchViewEnabled)
        activity.supportActionBar!!.setDisplayShowTitleEnabled(!searchViewEnabled)
        // when showing hide the other menu options + override back handling
        getActivity().invalidateOptionsMenu()
    }

    private fun createNewRoll() {
        (activity as MainActivity).switchContent(EditRollFragment.newInstance())
    }

    private fun importConfig() {
        val builder = AlertDialog.Builder(activity)
        builder.setMessage(R.string.info_import_export)
                .setCancelable(true)
                .setPositiveButton(R.string.ok) { dialog, id ->
                    //do things
                    dialog.dismiss()
                }
        val alert = builder.create()
        alert.show()
    }

    private fun shareConfig() {
        val sharingIntent = Intent(Intent.ACTION_SEND)
        sharingIntent.type = "text/plain"
        sharingIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, "FilmTag data export")
        sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, StorageUtil.getExportDataFormattedAsText(activity as MainActivity))
        startActivity(Intent.createChooser(sharingIntent, resources.getString(R.string.action_export)))
    }

    override fun onBackPressed(): Boolean {
        if (searchViewEnabled) {
            mAdapter!!.filter.filter(null)
            toggleSearchView()
            return true
        }
        return false
    }

    companion object {

        fun newInstance(): FilmRollListFragment {
            val args = Bundle()
            val fragment = FilmRollListFragment()
            fragment.arguments = args
            return fragment
        }
    }
}

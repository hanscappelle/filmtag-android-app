package be.hcpl.android.filmtag

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.widget.AdapterView
import android.widget.ListView
import android.widget.TextView
import androidx.appcompat.widget.SearchView
import androidx.core.os.bundleOf
import androidx.core.view.isVisible
import androidx.navigation.fragment.findNavController
import androidx.preference.PreferenceFragmentCompat

import be.hcpl.android.filmtag.adapter.FilmRollAdapter
import be.hcpl.android.filmtag.model.Roll
import be.hcpl.android.filmtag.template.TemplateFragment
import be.hcpl.android.filmtag.util.StorageUtil

/**
 * an overview of rolls created earlier + option to add new roll of film
 *
 * Created by hcpl on 30/07/15.
 */
class FilmRollListFragment : TemplateFragment() {

    // TODO delete film from overview directly (swipe? long press, ...)

    private var mAdapter: FilmRollAdapter? = null

    private lateinit var searchView: SearchView
    private lateinit var listView: ListView
    private lateinit var emptyInfo: TextView

    override val layoutResourceId: Int
        get() = R.layout.fragment_roll_overview

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        listView = view.findViewById(R.id.list_rolls)
        emptyInfo = view.findViewById(R.id.empty_info)

        // prepare the adapter for that list
        mAdapter = FilmRollAdapter(requireContext())
        listView.adapter = mAdapter
        listView.onItemClickListener = AdapterView.OnItemClickListener { _, _, i, _ ->
            showRollDetails(mAdapter!!.getItem(i))
        }

        // enable search view in toolbar
        setUpSearchView();
    }

    private fun setUpSearchView() {

        // FIXME filtering is broken
        // parent activity
        val mainActivity = activity as MainActivity

        searchView = SearchView(requireContext())
        searchView.setIconifiedByDefault(false)
        mainActivity.supportActionBar?.customView = searchView
        mainActivity.supportActionBar?.setDisplayShowCustomEnabled(searchViewEnabled)
        // text listeners
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String): Boolean {
                // filter data
                mAdapter?.filter?.filter(query)
                return true
            }

            override fun onQueryTextChange(newText: String): Boolean {
                if ("" == newText) {
                    // clear results
                    mAdapter?.filter?.filter(null)
                    return true
                }
                return false
            }
        })
        // not in use
        searchView.setOnCloseListener {
            mAdapter?.filter?.filter(null)
            true
        }
        // when editing and back used first focus goes away
        searchView.setOnQueryTextFocusChangeListener { _, b ->
            if (!b) {
                val query = searchView.query
                if (query != null && query.isNotEmpty()) {
                    mAdapter?.filter?.filter(query)
                } else {
                    mAdapter?.filter?.filter(null)
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
        mAdapter?.clear()
        mAdapter?.addAll(rolls)
        mAdapter?.notifyDataSetChanged()
        // show empty info when needed
        emptyInfo.isVisible = rolls.isEmpty()
        listView.isVisible = rolls.isNotEmpty()
    }


    private fun showRollDetails(roll: Roll) {
        // show frames on selection
        val bundle = bundleOf("roll" to roll)
        findNavController().navigate(R.id.action_detail, bundle)
    }

    // create new roll option is in main activity

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        // update options based on search enabled or not
        if (!searchViewEnabled)
            inflater.inflate(R.menu.rolls, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val id = item.itemId
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
        //} else if (id == R.id.action_search) {
        //    toggleSearchView()
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
        activity.supportActionBar?.setDisplayShowCustomEnabled(searchViewEnabled)
        activity.supportActionBar?.setDisplayShowTitleEnabled(!searchViewEnabled)
        // when showing hide the other menu options + override back handling
        requireActivity().invalidateOptionsMenu()
    }

    private fun createNewRoll() {
        findNavController().navigate(R.id.action_add_roll)
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
        sharingIntent.putExtra(Intent.EXTRA_TEXT, StorageUtil.getExportDataFormattedAsText(activity as MainActivity))
        startActivity(Intent.createChooser(sharingIntent, resources.getString(R.string.action_export)))
    }

    override fun onBackPressed(): Boolean {
        if (searchViewEnabled) {
            mAdapter?.filter?.filter(null)
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

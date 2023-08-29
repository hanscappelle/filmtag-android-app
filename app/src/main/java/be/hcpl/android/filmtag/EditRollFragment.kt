package be.hcpl.android.filmtag

import android.os.Bundle
import android.preference.PreferenceManager
import android.text.TextUtils
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.CheckBox
import android.widget.EditText
import android.widget.Toast

import java.util.Arrays

import be.hcpl.android.filmtag.model.Roll
import be.hcpl.android.filmtag.template.TemplateFragment
import be.hcpl.android.filmtag.util.StorageUtil

/**
 * Created by hcpl on 1/08/15.
 */
class EditRollFragment : TemplateFragment() {

    private var roll: Roll? = null

    private lateinit var edit_type: AutoCompleteTextView
    private lateinit var edit_notes: EditText
    private lateinit var edit_exposed: EditText
    private lateinit var edit_frames: EditText
    private lateinit var check_developed: CheckBox
    private lateinit var edit_tags: EditText

    override val layoutResourceId: Int
        get() = R.layout.fragment_form_roll

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putSerializable(KEY_EDIT_ROLL, roll)
    }

    override fun onViewStateRestored(savedInstanceState: Bundle?) {
        super.onViewStateRestored(savedInstanceState)
        if (savedInstanceState != null)
            roll = savedInstanceState.getSerializable(KEY_EDIT_ROLL) as Roll?
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
        val args = arguments
        if (args != null) {
            roll = args.getSerializable(KEY_EDIT_ROLL) as Roll?
        }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.new_film, menu)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        edit_type = view.findViewById(R.id.edit_type)
        edit_notes = view.findViewById(R.id.edit_notes)
        edit_exposed = view.findViewById(R.id.edit_exposed)
        edit_frames = view.findViewById(R.id.edit_frames)
        check_developed = view.findViewById(R.id.check_developed)
        edit_tags = view.findViewById(R.id.edit_tags)

        // prefill data if possible
        if (roll != null) {
            edit_type.setText(roll!!.type)
            edit_notes.setText(roll!!.notes)
            if (roll!!.speed != 0)
                edit_exposed.setText(roll!!.speed.toString())
            if (roll!!.frames != 0)
                edit_frames.setText(roll!!.frames.toString())
            check_developed.isChecked = roll!!.isDeveloped
            // populate the tags here
            if (!roll!!.tags.isEmpty())
                edit_tags.setText(TextUtils.join(" ", roll!!.tags))
        } else {
            // have preferences for this
            val prefs = PreferenceManager.getDefaultSharedPreferences(context)
            edit_exposed.setText(prefs.getString("key_default_iso", 200.toString()))
            edit_frames.setText(prefs.getString("key_default_frames", 36.toString()))
        }// populate with defaults here

        // autocomplete
        val adapter = ArrayAdapter(requireContext(),
                android.R.layout.simple_dropdown_item_1line, typeSuggestions)
        edit_type.setAdapter(adapter)
    }

    private val typeSuggestions: Array<String>
        get() {
            val rolls = StorageUtil.getAllRolls(activity as MainActivity)
            val existingTypes = arrayOfNulls<String>(rolls.size)
            for (i in rolls.indices) {
                existingTypes[i] = rolls[i].type
            }
            return existingTypes as Array<String>
        }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item!!.itemId) {
            R.id.action_create -> {
                createNewItem()
                return true
            }
            android.R.id.home -> {
                backToOverview()
                return true
            }
        }
        return false
    }

    private fun backToOverview() {
        // popping first is one option, going back in stack is better
        val activity = activity as MainActivity
        activity.supportFragmentManager.popBackStackImmediate()
        activity.supportFragmentManager.popBackStackImmediate()
        if (roll == null)
            activity.switchContent(FilmRollListFragment.newInstance())
        else {
            activity.switchContent(FilmFrameListFragment.newInstance(roll!!))
        }
    }

    private fun createNewItem() {
        var newRoll = false
        // insert the new item
        if (roll == null) {
            roll = Roll()
            newRoll = true
        }
        roll!!.type = edit_type.text.toString()
        roll!!.notes = edit_notes.text.toString()
        roll!!.isDeveloped = check_developed.isChecked
        roll!!.tags = Arrays.asList(*TextUtils.split(edit_tags.text.toString(), " "))
        try {
            roll!!.speed = Integer.parseInt(edit_exposed.text.toString())
        } catch (nfe: NumberFormatException) {
            Toast.makeText(activity, R.string.err_parsing_failed, Toast.LENGTH_SHORT).show()
        }

        try {
            roll!!.frames = Integer.parseInt(edit_frames.text.toString())
        } catch (nfe: NumberFormatException) {
            Toast.makeText(activity, R.string.err_parsing_failed, Toast.LENGTH_SHORT).show()
        }

        // store new roll
        if (newRoll)
            StorageUtil.addNewRoll(activity as MainActivity, roll!!)
        else
            StorageUtil.updateRoll(activity as MainActivity, roll!!)


        // navigate to overview
        backToOverview()
    }

    override fun onResume() {
        super.onResume()
        (activity as MainActivity).setHomeAsUp(true)
    }

    companion object {

        private val KEY_EDIT_ROLL = "edit_roll"

        /**
         * use for creating new rolls
         */
        fun newInstance(): EditRollFragment {
            val args = Bundle()
            val fragment = EditRollFragment()
            fragment.arguments = args
            return fragment
        }

        /**
         * use for editing existing rolls
         */
        fun newInstance(roll: Roll?): EditRollFragment {
            val args = Bundle()
            args.putSerializable(KEY_EDIT_ROLL, roll)
            val fragment = EditRollFragment()
            fragment.arguments = args
            return fragment
        }
    }

}

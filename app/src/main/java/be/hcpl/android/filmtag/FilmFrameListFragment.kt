package be.hcpl.android.filmtag

import android.os.Bundle
import android.support.v7.app.AlertDialog
import android.text.TextUtils
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.widget.AdapterView
import android.widget.LinearLayout
import android.widget.ListView
import android.widget.TextView

import be.hcpl.android.filmtag.adapter.FrameAdapter
import be.hcpl.android.filmtag.model.Frame
import be.hcpl.android.filmtag.model.Roll
import be.hcpl.android.filmtag.template.TemplateFragment
import be.hcpl.android.filmtag.util.StorageUtil
import butterknife.Bind
import kotlinx.android.synthetic.main.fragment_roll_detail.*

class FilmFrameListFragment : TemplateFragment() {

    private var filmRoll: Roll? = null

    @Bind(R.id.text_roll_details)
    internal var detailTextView: TextView? = null
    @Bind(R.id.list_frames)
    internal var framesListView: ListView? = null

    @Bind(R.id.wrapper_tags)
    internal var wrapperTagsView: LinearLayout? = null

    private var mAdapter: FrameAdapter? = null

    // TODO double reference?
    private var frames: MutableList<Frame>? = null

    override fun onSaveInstanceState(outState: Bundle?) {
        super.onSaveInstanceState(outState)
        outState!!.putSerializable(KEY_FILM_ROLL, filmRoll)
    }

    override fun onViewStateRestored(savedInstanceState: Bundle?) {
        super.onViewStateRestored(savedInstanceState)
        if (savedInstanceState != null) {
            filmRoll = savedInstanceState.getSerializable(KEY_FILM_ROLL) as Roll
        }
    }

    override val layoutResourceId: Int
        get() = R.layout.fragment_roll_detail

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
        val args = arguments
        if (args != null) {
            filmRoll = args.getSerializable(KEY_FILM_ROLL) as Roll
        }
    }

    override fun onResume() {
        super.onResume()
        updateFramesForFilm()
        (activity as MainActivity).setHomeAsUp(true)
    }

    private fun updateFramesForFilm() {

        if (filmRoll != null) {
            frames = StorageUtil.getFramesForFilm(activity as MainActivity, filmRoll!!)
            // if the film doesn't have frames yet add them based on the number specified
            if (frames!!.isEmpty()) {
                for (i in 0..filmRoll!!.frames) {
                    val frame = Frame()
                    frame.number = i
                    frames!!.add(frame)
                }
            }

            // update list data
            mAdapter!!.clear()
            mAdapter!!.addAll(frames!!)
            mAdapter!!.notifyDataSetChanged()
        }

    }

    override fun onViewCreated(
            view: View?,
            savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // show roll details on top
        if (filmRoll != null && detailTextView != null) {
            text_roll.text = filmRoll!!.toString()
            if (TextUtils.isEmpty(filmRoll!!.notes)) {
                detailTextView!!.visibility = View.GONE
            } else {
                detailTextView!!.text = filmRoll!!.notes
                detailTextView!!.visibility = View.VISIBLE
            }
            // also load tags here
            if (!filmRoll!!.tags.isEmpty()) {
                wrapperTagsView!!.visibility = View.VISIBLE
                for (tag in filmRoll!!.tags) {
                    val tv = TextView(context)
                    tv.text = tag
                    tv.setAllCaps(true)
                    tv.setPadding(10, 0, 10, 0) // TODO proper units needed here?
                    tv.textSize = 14f // TODO proper units needed here?
                    wrapperTagsView!!.addView(tv)
                }
            } else {
                wrapperTagsView!!.visibility = View.GONE
            }
        }

        // and populate list with frame data
        mAdapter = FrameAdapter(activity)
        framesListView!!.adapter = mAdapter

        framesListView!!.onItemClickListener = AdapterView.OnItemClickListener { adapterView, view, i, l -> updateFrame(i) }
    }

    private fun updateFrame(index: Int) {
        (activity as MainActivity).switchContent(EditFrameFragment.newInstance(filmRoll, frames, index))
    }

    override fun onCreateOptionsMenu(
            menu: Menu?,
            inflater: MenuInflater?) {
        inflater!!.inflate(R.menu.frames, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when (item!!.itemId) {
            R.id.action_delete -> {
                deleteCurrentFilmRoll()
                return true
            }
            R.id.action_edit -> {
                editCurrentFilmRoll()
                return true
            }
            android.R.id.home -> {
                // back to overview without change
                backToOverview()
                return true
            }
        }
        return false
    }

    private fun editCurrentFilmRoll() {
        (activity as MainActivity).switchContent(EditRollFragment.newInstance(filmRoll))
    }

    private fun backToOverview() {
        (activity as MainActivity).switchContent(FilmRollListFragment.newInstance())
    }

    private fun deleteCurrentFilmRoll() {
        // confirmation needed before delete here...
        AlertDialog.Builder(activity)
                //.setTitle(R.string.label_confirm)
                .setMessage(R.string.msg_delete_complete_film_roll)
                .setPositiveButton(R.string.label_yes) { dialogInterface, i ->
                    StorageUtil.deleteRoll(activity as MainActivity, filmRoll!!)
                    // navigate back
                    dialogInterface.dismiss()
                    backToOverview()
                }.setNegativeButton(R.string.label_no) { dialogInterface, i -> dialogInterface.dismiss() }.show()

    }

    companion object {

        val KEY_FILM_ROLL = "roll"

        fun newInstance(roll: Roll): FilmFrameListFragment {
            val args = Bundle()
            args.putSerializable(KEY_FILM_ROLL, roll)
            val fragment = FilmFrameListFragment()
            fragment.arguments = args
            return fragment
        }
    }
}

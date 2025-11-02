package be.hcpl.android.filmtag.ui.activity

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.ui.Modifier
import be.hcpl.android.filmtag.R
import be.hcpl.android.filmtag.ui.Action
import be.hcpl.android.filmtag.ui.ActionId
import be.hcpl.android.filmtag.ui.AppScaffold
import be.hcpl.android.filmtag.ui.view.FrameView
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf
import kotlin.getValue

class FilmRollActivity : ComponentActivity() {

    private val viewModel: FilmRollViewModel by viewModel(
        parameters = { parametersOf(intent.getLongExtra(KEY_FILM_ROLL, -1L)) }
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        viewModel.state.observe(this, ::handleState)
        viewModel.events.observe(this, ::handleEvent)
    }

    private fun handleState(state: FilmRollViewModel.State) {
        setContent {
            AppScaffold(
                actions = listOf(
                    Action(
                        iconRes = R.drawable.ic_action_edit,
                        textRes = R.string.action_edit,
                        actionId = ActionId.Create,
                    ),
                    Action(
                        iconRes = R.drawable.ic_action_close,
                        textRes = R.string.action_close,
                        actionId = ActionId.Close,
                    ),
                ),
                handleAction = ::handleAction,
            ) { innerPadding ->
                LazyColumn(
                    modifier = Modifier
                        .padding(innerPadding),
                ) {
                    state.frames.forEachIndexed { index, frame ->
                        item(key = index) {
                            FrameView(frame)
                        }
                    }
                }

            }
        }
    }

    private fun handleEvent(event: FilmRollViewModel.Event) {
        when (event) {
            is FilmRollViewModel.Event.EditRoll -> {
                val intent = Intent(this, EditRollActivity::class.java).apply {
                    putExtra(KEY_FILM_ROLL, event.rollId)
                }
                startActivity(intent)
            }
        }
    }

    private fun handleAction(actionId: ActionId) {
        when (actionId) {
            ActionId.Close -> finish()
            ActionId.Create -> viewModel.preparedEditRoll()
            ActionId.Export -> TODO()
            ActionId.Help -> TODO()
            ActionId.Info -> TODO()
        }
    }

    /*

    list_frames.onItemClickListener = AdapterView.OnItemClickListener { _, _, i, _ ->
            updateFrame(i)
        }
    }

    private fun updateFrame(index: Int) {
        //findNavController().navigate(
        //    R.id.action_edit_frame, bundleOf(
        //        EditFrameFragment.KEY_FRAMES to frames as ArrayList<*>,
         ////       EditFrameFragment.KEY_FRAME_IDX to index,
          //      EditFrameFragment.KEY_ROLL to filmRoll
          //  )
       // )
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.frames, menu)
        // apply alpha on locked or not
        this.menu = menu
        updateLockedIndication()
    }

    private var menu: Menu? = null

    private fun updateLockedIndication() {
        menu?.findItem(R.id.action_lock)?.apply {
            icon?.alpha = if (filmRoll?.isDeveloped == true) 255 else 51
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_lock -> {
                toggleFilmLocked()
                updateLockedIndication()
                true
            }

            R.id.action_delete -> {
                deleteCurrentFilmRoll()
                true
            }

            R.id.action_edit -> {
                editCurrentFilmRoll()
                true
            }

            android.R.id.home -> {
                // always navigate back here to prevent loop with edit and other views
                backToOverview()
                true
            }

            else -> false
        }
    }

    private fun toggleFilmLocked() {
        filmRoll?.let { roll ->
            roll.isDeveloped = !roll.isDeveloped
            StorageUtil.updateRoll(activity as MainActivity, roll)
        }
    }

    private fun editCurrentFilmRoll() {
     //   findNavController().navigate(R.id.action_edit_roll, bundleOf(KEY_FILM_ROLL to filmRoll))
    }

    private fun backToOverview() {
       // findNavController().navigate(R.id.action_home)
    }

    private fun deleteCurrentFilmRoll() {
        // confirmation needed before delete here...
        AlertDialog.Builder(requireContext())
            //.setTitle(R.string.label_confirm)
            .setMessage(R.string.msg_delete_complete_film_roll)
            .setPositiveButton(R.string.label_yes) { dialogInterface, _ ->
                StorageUtil.deleteRoll(activity as MainActivity, filmRoll!!)
                // navigate back
                dialogInterface.dismiss()
                backToOverview()
            }.setNegativeButton(R.string.label_no) { dialogInterface, _ -> dialogInterface.dismiss() }.show()

    }

     */

    companion object {
        const val KEY_FILM_ROLL = "KEY_FILM_ROLL"
    }
}
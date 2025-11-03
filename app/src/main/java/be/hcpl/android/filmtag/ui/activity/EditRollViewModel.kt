package be.hcpl.android.filmtag.ui.activity

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import be.hcpl.android.filmtag.domain.repository.FilmRollRepository
import be.hcpl.android.filmtag.domain.repository.SharedPreferencesProvider
import be.hcpl.android.filmtag.model.Roll
import be.hcpl.android.filmtag.ui.view.EditRollViewState

class EditRollViewModel(
    private val selectedRollId: Long,
    private val sharedPreferencesProvider: SharedPreferencesProvider,
    private val filmRollRepository: FilmRollRepository,
) : ViewModel() {

    val state = MutableLiveData<State>()
    val events = MutableLiveData<Event>()

    init {
        val selectedRoll = filmRollRepository.getRollById(selectedRollId) ?: Roll()
        state.postValue(
            State(
                rollId = selectedRollId,
                roll =  selectedRoll,
                editFormState = EditRollViewState(selectedRoll),
            )
        )
    }
        // have preferences for this
        //val prefs = PreferenceManager.getDefaultSharedPreferences(context)
        //edit_exposed.setText(prefs.getString("key_default_iso", 200.toString()))
        //edit_frames.setText(prefs.getString("key_default_frames", 36.toString()))

    fun saveChanges() {
        // TODO needs some input validation here
        val roll = Roll(
            id = selectedRollId,
            type = state.value?.editFormState?.filmTypeState?.text.toString(),
            speed = state.value?.editFormState?.isoState?.text.toString().toInt(),
            frames = state.value?.editFormState?.framesState?.text.toString().toInt(),
            notes = state.value?.editFormState?.notesState?.text.toString(),
            isDeveloped = state.value?.editFormState?.checkedState?.value == true,
            tags = listOf(),//TODO handle tags here (need to parse?),
        )
        if (selectedRollId == -1L) {
            // this is a new item
            filmRollRepository.addNewRoll(roll)
        } else {
            // update an existing item
            filmRollRepository.updateRoll(roll)
        }
        events.postValue(Event.Close)

    }

    /*
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
            } catch (_: Exception) {
                Toast.makeText(activity, R.string.err_parsing_failed, Toast.LENGTH_SHORT).show()
            }

            try {
                roll!!.frames = Integer.parseInt(edit_frames.text.toString())
            } catch (_: Exception) {
                Toast.makeText(activity, R.string.err_parsing_failed, Toast.LENGTH_SHORT).show()
            }

            // store new roll
            if (newRoll)
                StorageUtil.addNewRoll(activity as MainActivity, roll!!)
            else
                StorageUtil.updateRoll(activity as MainActivity, roll!!)

            // navigate to overview
            //toOverviewOrDetail()
        }
    */

    data class State(
        val rollId: Long = -1,
        val roll: Roll = Roll(),
        val editFormState: EditRollViewState = EditRollViewState(Roll()),
    )

    sealed class Event{
        data object Close : Event()
    }
}
package be.hcpl.android.filmtag.ui.activity

import android.text.TextUtils
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import be.hcpl.android.filmtag.domain.FilmRollRepository
import be.hcpl.android.filmtag.domain.SharedPreferencesProvider
import be.hcpl.android.filmtag.model.Roll
import be.hcpl.android.filmtag.ui.tranformer.InputTransformer
import be.hcpl.android.filmtag.ui.view.EditRollViewState
import be.hcpl.android.filmtag.ui.tranformer.TextTransformer

class EditRollViewModel(
    private val selectedRollId: Long,
    private val sharedPreferencesProvider: SharedPreferencesProvider,
    private val filmRollRepository: FilmRollRepository,
    textTransformer: TextTransformer,
    private val inputTransformer: InputTransformer,
) : ViewModel() {

    val state = MutableLiveData<State>()
    val events = MutableLiveData<Event>()

    init {
        val selectedRoll = filmRollRepository.getRollById(selectedRollId) ?: Roll()
        state.postValue(
            State(
                rollId = selectedRollId,
                roll = selectedRoll,
                editState = EditRollViewState(
                    roll = selectedRoll,
                    currentTags = textTransformer.formatTags(selectedRoll.tags),
                ),
            )
        )
    }
    // TODO restore preferences for this
    //val prefs = PreferenceManager.getDefaultSharedPreferences(context)
    //edit_exposed.setText(prefs.getString("key_default_iso", 200.toString()))
    //edit_frames.setText(prefs.getString("key_default_frames", 36.toString()))

    fun saveChanges() {
        val roll = Roll(
            id = selectedRollId,
            type = state.value?.editState?.filmTypeState?.text.toString(),
            speed = inputTransformer.sanitizeInt(state.value?.editState?.isoState?.text),
            frames = inputTransformer.sanitizeInt(state.value?.editState?.framesState?.text),
            notes = state.value?.editState?.notesState?.text.toString(),
            isDeveloped = state.value?.editState?.checkedState?.value == true,
            tags = inputTransformer.sanitizeList(state.value?.editState?.tagsState?.text),
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

    data class State(
        val rollId: Long = -1,
        val roll: Roll = Roll(),
        val editState: EditRollViewState = EditRollViewState(
            roll = Roll(),
            currentTags = "",
        ),
    )

    sealed class Event {
        data object Close : Event()
    }
}
package be.hcpl.android.filmtag.ui.activity

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import be.hcpl.android.filmtag.domain.FilmRollRepository
import be.hcpl.android.filmtag.domain.SettingsRepository
import be.hcpl.android.filmtag.model.Roll
import be.hcpl.android.filmtag.ui.tranformer.InputTransformer
import be.hcpl.android.filmtag.ui.tranformer.TextTransformer
import be.hcpl.android.filmtag.ui.view.EditRollViewState

class EditRollViewModel(
    private val selectedRollId: Long,
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
                    initialFrameCount = textTransformer.formatFrameCount(selectedRoll.frames),
                    initialIso = textTransformer.formatIso(selectedRoll.speed),
                    formattedTags = textTransformer.formatTags(selectedRoll.tags),
                ),
            )
        )
    }

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
        ),
    )

    sealed class Event {
        data object Close : Event()
    }
}
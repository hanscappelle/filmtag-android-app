package be.hcpl.android.filmtag.ui.activity

import be.hcpl.android.filmtag.R
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import be.hcpl.android.filmtag.domain.FilmRollRepository
import be.hcpl.android.filmtag.model.Frame
import be.hcpl.android.filmtag.model.Roll
import be.hcpl.android.filmtag.ui.view.EditFrameViewState

class EditFrameViewModel(
    private val selectedRollId: Long,
    private val selectedFrameId: Int,
    private val filmRollRepository: FilmRollRepository,
) : ViewModel() {

    val state = MutableLiveData<State>()
    val events = MutableLiveData<Event>()

    var currentRoll: Roll? = null
    var currentFrame: Frame? = null

    init {
        refreshData()
    }

    private fun refreshData() {
        currentRoll = filmRollRepository.getRollById(selectedRollId) ?: Roll()
        // TODO needs safeguard here for index?
        currentFrame = filmRollRepository.getFramesForFilm(selectedRollId)[selectedFrameId]
        updateUiState()
    }

    private fun updateUiState() {
        currentRoll?.let { roll ->
            currentFrame?.let { frame ->
                state.postValue(
                    State(
                        roll = roll,
                        frame = frame,
                        editFrameState = EditFrameViewState(
                            roll,
                            frame,
                        )
                    )
                )
            }
        }
    }

    fun saveFrame(close: Boolean = true) {
        if( currentRoll?.isDeveloped == true ){
            // inform user this film roll is locked
            events.postValue(Event.Message(R.string.msg_roll_is_locked))
        } else {
            // TODO needs some input validation here
            val frame = Frame(
                number = selectedFrameId,
                shutter = state.value?.editFrameState?.speedState?.text.toString().toInt(),
                aperture = state.value?.editFrameState?.apertureState?.text.toString().toDouble(),
                notes = state.value?.editFrameState?.notesState?.text.toString(),
                isLongExposure = state.value?.editFrameState?.checkedState?.value == true,
                dateTaken = currentFrame?.dateTaken,
                tags = listOf(),//TODO handle tags here (need to parse?),
                // TODO location: Location? = null,
            )
            // update an existing item
            filmRollRepository.updateFrame(selectedRollId, frame)
            if (close) events.postValue(Event.Close)
        }
    }

    fun updateSelectedDate(date: Long?) {
        currentFrame = currentFrame?.copy(dateTaken = date)
        updateUiState()
    }

    data class State(
        val roll: Roll,
        val frame: Frame,
        val editFrameState: EditFrameViewState,
    )

    sealed class Event {
        data object Close : Event()
        data class Message(val resourceId: Int): Event()
    }
}
package be.hcpl.android.filmtag.ui.activity

import android.net.Uri
import android.text.TextUtils
import be.hcpl.android.filmtag.R
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import be.hcpl.android.filmtag.domain.FilmRollRepository
import be.hcpl.android.filmtag.model.Frame
import be.hcpl.android.filmtag.model.Location
import be.hcpl.android.filmtag.model.Roll
import be.hcpl.android.filmtag.ui.activity.EditFrameViewModel.Event.ShowOnMap
import be.hcpl.android.filmtag.ui.tranformer.InputTransformer
import be.hcpl.android.filmtag.ui.view.EditFrameViewState
import be.hcpl.android.filmtag.ui.tranformer.TextTransformer

class EditFrameViewModel(
    private val selectedRollId: Long,
    private val selectedFrameId: Int,
    private val filmRollRepository: FilmRollRepository,
    private val textTransformer: TextTransformer,
    private val inputTransformer: InputTransformer,
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
        currentFrame = filmRollRepository.getFramesForFilm(selectedRollId).getOrNull(selectedFrameId)
        updateUiState()
    }

    private fun updateUiState() {
        currentRoll?.let { roll ->
            currentFrame?.let { frame ->
                state.postValue(
                    State(
                        roll = roll,
                        frame = frame,
                        editState = EditFrameViewState(
                            roll = roll,
                            frame = frame,
                            currentTags = TextUtils.join(textTransformer.TAG_SEPARATOR, frame.tags),
                            formattedDate = frame.dateTaken?.let { textTransformer.formatDate(it) }
                        )
                    )
                )
            }
        }
    }

    fun saveFrame(close: Boolean = true) {
        if (currentRoll?.isDeveloped == true) {
            // inform user this film roll is locked
            events.postValue(Event.Message(R.string.msg_roll_is_locked))
        } else {
            val frame = Frame(
                number = selectedFrameId,
                shutter = inputTransformer.sanitizeInt(state.value?.editState?.speedState?.text.toString()),
                aperture = inputTransformer.sanitizeDouble(state.value?.editState?.apertureState?.text.toString()),
                notes = state.value?.editState?.notesState?.text.toString(),
                isLongExposure = state.value?.editState?.checkedState?.value == true,
                dateTaken = currentFrame?.dateTaken,
                tags = state.value?.editState?.tagsState?.text?.split(textTransformer.TAG_SEPARATOR) ?: emptyList(),
                location = currentFrame?.location,
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

    fun toggleLocked() {
        currentRoll?.let { roll ->
            roll.isDeveloped = !roll.isDeveloped
            filmRollRepository.updateRoll(roll)
            updateUiState()
        }
    }

    fun updateLocation(location: Location) {
        currentFrame = currentFrame?.copy(location = location)
        updateUiState()
    }

    fun prepareShowLocation(location: Location?) {
        location?.let {
            val geoLocation = Uri.parse("geo: ${location.latitude},${location.longitude}")
            events.postValue(ShowOnMap(geoLocation))
        }
    }

    data class State(
        val roll: Roll,
        val frame: Frame,
        val editState: EditFrameViewState,
    )

    sealed class Event {
        data object Close : Event()
        data class Message(val resourceId: Int) : Event()
        data class ShowOnMap(val uri: Uri) : Event()
    }
}
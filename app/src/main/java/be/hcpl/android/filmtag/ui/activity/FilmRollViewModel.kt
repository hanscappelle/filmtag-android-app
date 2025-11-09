package be.hcpl.android.filmtag.ui.activity

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import be.hcpl.android.filmtag.domain.FilmRollRepository
import be.hcpl.android.filmtag.model.Frame
import be.hcpl.android.filmtag.model.Roll
import be.hcpl.android.filmtag.ui.activity.FilmRollViewModel.Event.Close
import be.hcpl.android.filmtag.ui.activity.FilmRollViewModel.Event.EditRoll

class FilmRollViewModel(
    private val selectedRollId: Long,
    private val filmRollRepository: FilmRollRepository,
) : ViewModel() {

    val state = MutableLiveData<State>()
    val events = MutableLiveData<Event>()

    var currentRoll: Roll? = null

    init {
        refreshData()
    }

    private fun refreshData() {
        filmRollRepository.getRollById(selectedRollId)?.let { roll ->
            currentRoll = roll
            val preparedFrames = filmRollRepository.getFramesForFilm(selectedRollId)
                //.toMutableList().also {
                //if (it.isEmpty()) {
                //    repeat(roll.frames) { counter ->
                //        it.add(Frame(number = counter + 1))
                //    }
                //}
            //}
            state.postValue(
                State(
                    roll = roll,
                    frames = preparedFrames.toList(),
                )
            )
        } ?: state.postValue(State(Roll())) // some empty state
    }

    fun preparedEditRoll() {
        currentRoll?.let { roll ->
            events.postValue(EditRoll(roll.id))
        }
    }

    fun deleteRoll() {
        currentRoll?.let { roll ->
            filmRollRepository.deleteRoll(roll)
            events.postValue(Close)
        }
    }

    fun toggleLocked() {
        currentRoll?.let { roll ->
            roll.isDeveloped = !roll.isDeveloped
            filmRollRepository.updateRoll(roll)
            refreshData()
        }
    }

    fun onResume() {
        refreshData()
    }

    fun prepareEditFrame(frameIndex: Int) {
        currentRoll?.let { roll ->
            events.postValue(Event.EditFrame(roll.id, frameIndex))
        }
    }

    data class State(
        val roll: Roll,
        val frames: List<Frame> = emptyList(),
    )

    sealed class Event {
        data class EditRoll(val rollId: Long) : Event()
        data class EditFrame(val rollId: Long, val frameId: Int) : Event()
        data object Close : Event()
    }
}
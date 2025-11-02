package be.hcpl.android.filmtag.ui.activity

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import be.hcpl.android.filmtag.domain.repository.FilmRollRepository
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
        filmRollRepository.getRollById(selectedRollId)?.let { roll ->
            currentRoll = roll
            state.postValue(
                State(
                    roll = roll,
                    frames = filmRollRepository.getFramesForFilm(selectedRollId)
                )
            )
        } ?: state.postValue(State()) // some empty state
    }

    fun preparedEditRoll() {
        events.postValue(EditRoll(currentRoll?.id))
    }

    fun deleteRoll() {
        currentRoll?.let { roll ->
            filmRollRepository.deleteRoll(roll)
            events.postValue(Close)
        }
    }

    data class State(
        val roll: Roll? = null,
        val frames: List<Frame> = emptyList(),
    )

    sealed class Event {
        data class EditRoll(val rollId: Long?) : Event()
        data object Close : Event()
    }
}
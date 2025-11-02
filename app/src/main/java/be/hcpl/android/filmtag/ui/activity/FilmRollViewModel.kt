package be.hcpl.android.filmtag.ui.activity

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import be.hcpl.android.filmtag.domain.repository.FilmRollRepository
import be.hcpl.android.filmtag.model.Frame
import be.hcpl.android.filmtag.model.Roll

class FilmRollViewModel(
    private val selectedRollId: Long,
    private val filmRollRepository: FilmRollRepository,
) : ViewModel() {

    val state = MutableLiveData<State>()
    val events = MutableLiveData<Event>()

    init {
        filmRollRepository.getRollById(selectedRollId)?.let { roll ->
            state.postValue(
                State(
                    roll = roll,
                    frames = filmRollRepository.getFramesForFilm(selectedRollId)
                )
            )
        } ?: state.postValue(State()) // some empty state
    }

    data class State(
        val roll: Roll? = null,
        val frames: List<Frame> = emptyList(),
    )

    sealed class Event {
    }
}
package be.hcpl.android.filmtag.ui.activity

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import be.hcpl.android.filmtag.domain.repository.FilmRollRepository
import be.hcpl.android.filmtag.model.Roll

class MainViewModel(
    private val filmRollRepository: FilmRollRepository,
) : ViewModel() {

    val state = MutableLiveData<State>()
    val events = MutableLiveData<Event>()

    init {
        refreshData()
    }

    private fun refreshData() {
        state.postValue(
            State(
                rolls = filmRollRepository.getAllRolls(),
            )
        )
    }

    fun showToggleLock(rollId: Long) {
        filmRollRepository.getRollById(rollId)?.let { roll ->
            events.postValue(
                Event.ShowToggleLock(
                    rollId = rollId,
                    isDeveloped = roll.isDeveloped,
                )
            )
        }
    }

    fun toggleLock(rollId: Long) {
        filmRollRepository.getRollById(rollId)?.let { roll ->
            roll.isDeveloped = !roll.isDeveloped
            filmRollRepository.updateRoll(roll)
            refreshData()
        }
    }

    data class State(
        val rolls: List<Roll> = emptyList<Roll>(),
    )

    sealed class Event {
        data class ShowToggleLock(
            val rollId: Long,
            val isDeveloped: Boolean,
        ) : Event()

    }
}


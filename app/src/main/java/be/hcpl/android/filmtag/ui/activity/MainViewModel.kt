package be.hcpl.android.filmtag.ui.activity

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import be.hcpl.android.filmtag.domain.repository.FilmRollRepository
import be.hcpl.android.filmtag.model.Roll

class MainViewModel(
    private val filmRollRepository: FilmRollRepository,
) : ViewModel() {

    val state = MutableLiveData<State>()

    init {
        state.postValue(State(
            rolls = filmRollRepository.getAllRolls(),
        ))
    }



    data class State(
        val rolls: List<Roll> = emptyList<Roll>()
    )
}


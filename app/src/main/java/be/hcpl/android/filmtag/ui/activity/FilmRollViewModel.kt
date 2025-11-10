package be.hcpl.android.filmtag.ui.activity

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import be.hcpl.android.filmtag.domain.FilmRollRepository
import be.hcpl.android.filmtag.model.Roll
import be.hcpl.android.filmtag.ui.activity.FilmRollViewModel.Event.Close
import be.hcpl.android.filmtag.ui.activity.FilmRollViewModel.Event.EditRoll
import be.hcpl.android.filmtag.ui.activity.FilmRollViewModel.Event.ExportText
import be.hcpl.android.filmtag.ui.transformer.FrameUiModelTransformer
import be.hcpl.android.filmtag.ui.transformer.TextTransformer
import be.hcpl.android.filmtag.ui.view.FrameUiModel

class FilmRollViewModel(
    private val selectedRollId: Long,
    private val filmRollRepository: FilmRollRepository,
    private val frameUiModelTransformer: FrameUiModelTransformer,
    private val textTransformer: TextTransformer,
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
            val preparedFrames = filmRollRepository.getFramesForFilm(selectedRollId).map {
                frameUiModelTransformer.transform(it)
            }
            state.postValue(
                State(
                    roll = roll,
                    frames = preparedFrames,
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

    fun prepareExport() {
        currentRoll?.let { roll ->
            val roll = textTransformer.formatRoll(roll)
            val frames = filmRollRepository.getFramesForFilm(selectedRollId).map { frame ->
                textTransformer.formatFrame(frame)
            }
            events.postValue(
                ExportText(
                    text = "$roll\n\n" + frames.joinToString("\n")
                )
            )
        }
    }

    data class State(
        val roll: Roll,
        val frames: List<FrameUiModel> = emptyList(),
    )

    sealed class Event {
        data class EditRoll(val rollId: Long) : Event()
        data class EditFrame(val rollId: Long, val frameId: Int) : Event()
        data class ExportText(val text: String) : Event()
        data object Close : Event()
    }
}
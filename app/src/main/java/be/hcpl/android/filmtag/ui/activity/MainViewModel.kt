package be.hcpl.android.filmtag.ui.activity

import android.widget.Toast
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import be.hcpl.android.filmtag.R
import be.hcpl.android.filmtag.domain.repository.FilmRollRepository
import be.hcpl.android.filmtag.model.Roll
import be.hcpl.android.filmtag.ui.activity.MainViewModel.Event.ImportResult
import be.hcpl.android.filmtag.util.StorageUtil

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

    fun prepareShareConfig() {
        filmRollRepository.exportDataFormattedAsText().let {
            events.postValue(Event.ShareConfig(exportedFormat = it))
        }
    }

    fun handleSharedConfig(sharedText: String?) {
        // remove everything before the { character indicating proper formatted text, this was
        // required for use with Google Note for example where the title was in front
        val sharedText = sharedText?.substring(sharedText.indexOf("{")).orEmpty()

        // try to import data here
        try {
            // try parsing data
            val data = filmRollRepository.parseDataExportFormat(sharedText)
            filmRollRepository.storeDataExportFormat(data)
            events.postValue(ImportResult(R.string.info_data_imported))
        } catch (_: Exception) {
            events.postValue(ImportResult(R.string.err_import_failed))
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

        data class ShareConfig(
            val exportedFormat: String,
        ) : Event()

        data class ImportResult(
            val textRes: Int,
        ) : Event()
    }
}


package be.hcpl.android.filmtag.ui.activity

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import be.hcpl.android.filmtag.domain.repository.FilmRollRepository
import be.hcpl.android.filmtag.domain.repository.SharedPreferencesProvider
import be.hcpl.android.filmtag.model.Roll

class EditRollViewModel(
    private val selectedRollId: Long,
    //private val sharedPreferencesProvider: SharedPreferencesProvider,
    private val filmRollRepository: FilmRollRepository,
) : ViewModel() {

    val state = MutableLiveData<State>()

    init {
        state.postValue(
            State(
                rollId = selectedRollId,
                roll = filmRollRepository.getRollById(selectedRollId) ?: Roll(),
            )
        )

        // have preferences for this
        //val prefs = PreferenceManager.getDefaultSharedPreferences(context)
        //edit_exposed.setText(prefs.getString("key_default_iso", 200.toString()))
        //edit_frames.setText(prefs.getString("key_default_frames", 36.toString()))
    }

    fun saveChanges() {
        if (selectedRollId == -1L) {
            // this is a new item
        } else {
            // update an existing item
        }

    }
    /*
        private fun createNewItem() {
            var newRoll = false
            // insert the new item
            if (roll == null) {
                roll = Roll()
                newRoll = true
            }
            roll!!.type = edit_type.text.toString()
            roll!!.notes = edit_notes.text.toString()
            roll!!.isDeveloped = check_developed.isChecked
            roll!!.tags = Arrays.asList(*TextUtils.split(edit_tags.text.toString(), " "))
            try {
                roll!!.speed = Integer.parseInt(edit_exposed.text.toString())
            } catch (_: Exception) {
                Toast.makeText(activity, R.string.err_parsing_failed, Toast.LENGTH_SHORT).show()
            }

            try {
                roll!!.frames = Integer.parseInt(edit_frames.text.toString())
            } catch (_: Exception) {
                Toast.makeText(activity, R.string.err_parsing_failed, Toast.LENGTH_SHORT).show()
            }

            // store new roll
            if (newRoll)
                StorageUtil.addNewRoll(activity as MainActivity, roll!!)
            else
                StorageUtil.updateRoll(activity as MainActivity, roll!!)

            // navigate to overview
            //toOverviewOrDetail()
        }
    */

    data class State(
        val rollId: Long = -1,
        val roll: Roll = Roll(),
    )
}
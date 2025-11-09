package be.hcpl.android.filmtag.domain

import be.hcpl.android.filmtag.model.DataExportFormat
import be.hcpl.android.filmtag.model.Frame
import be.hcpl.android.filmtag.model.Roll
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class FilmRollRepository(
    private val gson: Gson,
    private val sharedPreferencesProvider: SharedPreferencesProvider,
) {

    fun getAllRolls(): List<Roll> {
        // get the items
        val rollsData = sharedPreferencesProvider.sharedPreferences.getString(KEY_FILM_ROLLS, "[]")
        // convert using gson
        return gson.fromJson(rollsData, listOfRollsType)
    }

    fun getRollById(rollId: Long): Roll? {
        return getAllRolls().find { it.id == rollId }
    }

    fun addNewRoll(roll: Roll) {
        val rolls = getAllRolls().toMutableList()
        rolls.add(roll)
        updateRolls(rolls)
    }

    fun updateRoll(roll: Roll) {
        val rolls = getAllRolls()
        updateRolls(rolls.map {
            if (it.id == roll.id) {
                roll// modified roll goes here
            } else {
                it
            }
        })
    }

    private fun updateRolls(rolls: List<Roll>) {
        sharedPreferencesProvider.sharedPreferences.edit().putString(KEY_FILM_ROLLS, gson.toJson(rolls, listOfRollsType))?.apply()
    }

    fun getFramesForFilm(rollId: Long): List<Frame> {
        val framesData = sharedPreferencesProvider.sharedPreferences.getString(KEY_FILM_ROLLS + rollId, "[]")
        return gson.fromJson(framesData, listOfFramesType)
    }

    fun parseDataExportFormat(sharedText: String): DataExportFormat {
        return gson.fromJson(sharedText, DataExportFormat::class.java)
    }

    fun storeDataExportFormat(data: DataExportFormat) {
        // check if something to import here
        val rolls = data.rolls ?: return
        // store all new rolls
        addRolls(rolls)
        // and for each roll store the new frames also (skip non existing rolls for datacleaning purpose)
        data.rolls?.let {
            for (roll in data.rolls) {
                data.frames?.get(roll.id)?.let { framesForRoll ->
                    updateFrames(roll, framesForRoll)
                }
            }
        }
    }

    private fun updateFrames(filmRoll: Roll, frames: List<Frame>) {
        sharedPreferencesProvider.sharedPreferences.edit()?.putString(
            KEY_FILM_ROLLS + filmRoll
                .id, gson.toJson(frames, listOfFramesType)
        )?.apply()
    }

    private fun addRolls(roll: List<Roll>) {
        val rolls = getAllRolls()
        updateRolls(rolls + roll)
    }

    fun exportDataFormattedAsText(): String {
        // get all current rolls
        val rolls = getAllRolls()
        val frames = HashMap<Long, List<Frame>>(36)
        // and set frames for all rolls
        for (roll in rolls) {
            frames.put(roll.id, getFramesForFilm(roll.id))
        }

        // prepare data object
        val data = DataExportFormat()
        data.rolls = rolls
        data.frames = frames
        return gson.toJson(data)
    }

    fun deleteRoll(roll: Roll) {
        val rolls = getAllRolls().toMutableList()
        rolls.remove(roll)
        // also delete all frames for that roll at this point
        deleteFramesForRoll(roll)
        updateRolls(rolls)
    }

    private fun deleteFramesForRoll(roll: Roll) {
        sharedPreferencesProvider.sharedPreferences.edit()?.remove(KEY_FILM_ROLLS + roll.id)?.apply()
    }

    fun updateFrame(rollId: Long, frame: Frame) {
        val frames = getFramesForFilm(rollId)
        getRollById(rollId)?.let { roll ->
            updateFrames(roll, frames.map {
                if (it.number == frame.number) {
                    frame
                } else {
                    it
                }
            })
        }

    }

    companion object {
        private val listOfRollsType = object : TypeToken<List<Roll>>() {}.type
        private val listOfFramesType = object : TypeToken<List<Frame>>() {}.type
        const val KEY_FILM_ROLLS = "rolls"
    }

}
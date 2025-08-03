package be.hcpl.android.filmtag.util

import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

import java.util.HashMap

import be.hcpl.android.filmtag.MainActivity
import be.hcpl.android.filmtag.model.DataExportFormat
import be.hcpl.android.filmtag.model.Frame
import be.hcpl.android.filmtag.model.Roll

object StorageUtil {

    private val listOfRollsType = object : TypeToken<List<Roll>>() {

    }.type

    private val listOfFramesType = object : TypeToken<List<Frame>>() {

    }.type

    private val gson = Gson()

    private val KEY_FILM_ROLLS = "rolls"

    fun getAllRolls(activity: MainActivity): MutableList<Roll> {
        // get the items
        val rollsData = activity.prefs?.getString(KEY_FILM_ROLLS, "[]")
        // convert using gson
        return gson.fromJson(rollsData, listOfRollsType)
    }

    // for internal use only
    private fun updateRolls(activity: MainActivity, rolls: List<Roll>) {
        activity.prefs!!.edit().putString(KEY_FILM_ROLLS, gson.toJson(rolls, listOfRollsType)).apply()
    }

    fun deleteRoll(activity: MainActivity, roll: Roll) {
        val rolls = getAllRolls(activity)
        rolls.remove(roll)
        // also delete all frames for that roll at this point
        deleteFramesForRoll(activity, roll)
        updateRolls(activity, rolls)
    }

    private fun deleteFramesForRoll(activity: MainActivity, roll: Roll) {
        activity.prefs!!.edit().remove(KEY_FILM_ROLLS + roll.id).apply()
    }

    fun getFramesForFilm(activity: MainActivity, filmRoll: Roll): MutableList<Frame> {
        // get the items
        val framesData = activity.prefs!!.getString(KEY_FILM_ROLLS + filmRoll.id, "[]")
        // convert using gson
        return gson.fromJson(framesData, listOfFramesType)
    }

    fun updateFrames(activity: MainActivity, filmRoll: Roll, frames: List<Frame>) {
        activity.prefs!!.edit().putString(KEY_FILM_ROLLS + filmRoll
                .id, gson.toJson(frames, listOfFramesType)).apply()
    }

    fun addNewRoll(activity: MainActivity, roll: Roll) {
        val rolls = getAllRolls(activity)
        rolls.add(roll)
        updateRolls(activity, rolls)
    }

    fun updateRoll(activity: MainActivity, roll: Roll) {
        val rolls = getAllRolls(activity)
        rolls[rolls.indexOf(roll)] = roll
        updateRolls(activity, rolls)
    }

    private fun addRolls(activity: MainActivity, roll: List<Roll>) {
        val rolls = getAllRolls(activity)
        rolls.addAll(roll)
        updateRolls(activity, rolls)
    }

    fun parseDataExportFormat(sharedText: String): DataExportFormat {
        return gson.fromJson(sharedText, DataExportFormat::class.java)
    }

    fun storeDataExportFormat(mainActivity: MainActivity, data: DataExportFormat) {
        // check if something to import here
        val rolls = data.rolls ?: return
        // store all new rolls
        addRolls(mainActivity, rolls)
        // and for each roll store the new frames also (skip non existing rolls for datacleaning purpose)
        for (roll in data.rolls!!) {
            val framesForRoll = data.frames!![roll.id]
            if (framesForRoll != null) {
                updateFrames(mainActivity, roll, framesForRoll)
            }
        }
    }

    fun getExportDataFormattedAsText(activity: MainActivity): String {
        // get all current rolls
        val rolls = getAllRolls(activity)
        val frames = HashMap<Long, List<Frame>>(36)
        // and set frames for all rolls
        for (roll in rolls) {
            frames.put(roll.id, getFramesForFilm(activity, roll))
        }

        // prepare data object
        val data = DataExportFormat()
        data.rolls = rolls
        data.frames = frames
        return gson.toJson(data)
    }
}

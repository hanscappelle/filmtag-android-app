package be.hcpl.android.filmtag.util

import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

import java.util.HashMap

import be.hcpl.android.filmtag.ui.activity.MainActivity
import be.hcpl.android.filmtag.model.DataExportFormat
import be.hcpl.android.filmtag.model.Frame
import be.hcpl.android.filmtag.model.Roll

object StorageUtil {

    val listOfRollsType = object : TypeToken<List<Roll>>() {

    }.type

    val listOfFramesType = object : TypeToken<List<Frame>>() {

    }.type


    const val KEY_FILM_ROLLS = "rolls"

    fun getAllRolls(activity: MainActivity): MutableList<Roll> {
        // get the items
        //val rollsData = activity.prefs?.getString(KEY_FILM_ROLLS, "[]")
        // convert using gson
        return emptyList<Roll>().toMutableList()//gson.fromJson(rollsData, listOfRollsType)
    }

    // for internal use only
    private fun updateRolls(activity: MainActivity, rolls: List<Roll>) {
        //activity.prefs?.edit()?.putString(KEY_FILM_ROLLS, gson.toJson(rolls, listOfRollsType))?.apply()
    }

    fun updateFrames(activity: MainActivity, filmRoll: Roll, frames: List<Frame>) {
        //activity.prefs?.edit()?.putString(KEY_FILM_ROLLS + filmRoll
        //        .id, gson.toJson(frames, listOfFramesType))?.apply()
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

}

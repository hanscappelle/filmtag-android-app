package be.hcpl.android.filmtag.domain.repository

import be.hcpl.android.filmtag.model.Roll
import be.hcpl.android.filmtag.util.StorageUtil.KEY_FILM_ROLLS
import be.hcpl.android.filmtag.util.StorageUtil.listOfRollsType
import com.google.gson.Gson

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

    fun updateRolls(rolls: List<Roll>) {
        sharedPreferencesProvider.sharedPreferences.edit().putString(KEY_FILM_ROLLS, gson.toJson(rolls, listOfRollsType))?.apply()
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

}
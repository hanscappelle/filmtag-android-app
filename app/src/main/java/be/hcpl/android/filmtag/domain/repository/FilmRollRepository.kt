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

}
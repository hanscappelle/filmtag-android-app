package be.hcpl.android.filmtag.domain.repository

import android.content.Context
import android.content.SharedPreferences
import androidx.preference.PreferenceManager

class SharedPreferencesProvider(
    applicationContext: Context,
) {

    var sharedPreferences: SharedPreferences = PreferenceManager.getDefaultSharedPreferences(applicationContext)

}

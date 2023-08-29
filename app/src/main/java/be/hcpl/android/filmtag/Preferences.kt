package be.hcpl.android.filmtag

import android.os.Bundle
import androidx.preference.PreferenceFragmentCompat

class Preferences : PreferenceFragmentCompat() {
    override fun onCreatePreferences(bundle: Bundle?, s: String?) {
        // Load the preferences from an XML resource
        addPreferencesFromResource(R.xml.preferences)
    }
}
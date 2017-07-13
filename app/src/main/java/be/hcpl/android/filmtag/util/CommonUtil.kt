package be.hcpl.android.filmtag.util

import android.app.Activity
import android.view.inputmethod.InputMethodManager

/**
 * Created by hcpl on 6/08/15.
 */
object CommonUtil {

    fun hideSoftKeyboard(activity: Activity?) {
        val currentWindow = activity?.currentFocus?.windowToken ?: return

        val inputMethodManager = activity.getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
        inputMethodManager.hideSoftInputFromWindow(currentWindow, 0)
    }
}

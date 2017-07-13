package be.hcpl.android.filmtag.util

import android.app.Activity
import android.view.inputmethod.InputMethodManager

/**
 * Created by hcpl on 6/08/15.
 */
object CommonUtil {

    fun hideSoftKeyboard(activity: Activity?) {
        // avoid nullpointers here
        if (activity == null || activity.currentFocus == null || activity.currentFocus!!.windowToken == null)
            return
        val inputMethodManager = activity.getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
        inputMethodManager.hideSoftInputFromWindow(activity.currentFocus!!.windowToken, 0)
    }
}

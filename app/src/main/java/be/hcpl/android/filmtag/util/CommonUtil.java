package be.hcpl.android.filmtag.util;

import android.app.Activity;
import android.view.inputmethod.InputMethodManager;

/**
 * Created by hcpl on 6/08/15.
 */
public class CommonUtil {

    public static void hideSoftKeyboard(Activity activity) {
        // avoid nullpointers here
        if (activity == null || activity.getCurrentFocus() == null || activity.getCurrentFocus().getWindowToken() == null)
            return;
        InputMethodManager inputMethodManager = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(activity.getCurrentFocus().getWindowToken(), 0);
    }
}

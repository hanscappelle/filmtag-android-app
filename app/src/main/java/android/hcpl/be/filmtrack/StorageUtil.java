package android.hcpl.be.filmtrack;

import android.content.SharedPreferences;
import android.hcpl.be.filmtrack.model.Roll;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.List;

/**
 * Created by hcpl on 1/08/15.
 */
public class StorageUtil {

    //    Type listOfTestObject = new TypeToken<List<TestObject>>(){}.getType();
//    String s = gson.toJson(list, listOfTestObject);
//    List<TestObject> list2 = gson.fromJson(s, listOfTestObject);
    public static final Type listOfRolls = new TypeToken<List<Roll>>() {
    }.getType();

    public static final Gson gson = new Gson();

    public static final String KEY_FILM_ROLLS = "rolls";

    public static List<Roll> getAllRolls(MainActivity activity) {
        // get the items
        String rollsData = activity.getPrefs().getString(KEY_FILM_ROLLS, "[]");
        // convert using gson
        List<Roll> rolls = gson.fromJson(rollsData, listOfRolls);
        return rolls;
    }

    public static void updateRolls(MainActivity activity, List<Roll> rolls) {
        activity.getPrefs().edit().putString(KEY_FILM_ROLLS, gson.toJson(rolls, listOfRolls)).commit();
    }
}

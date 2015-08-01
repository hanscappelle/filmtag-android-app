package android.hcpl.be.filmtag.util;

import android.hcpl.be.filmtag.MainActivity;
import android.hcpl.be.filmtag.model.Frame;
import android.hcpl.be.filmtag.model.Roll;

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
    public static final Type listOfRollsType = new TypeToken<List<Roll>>() {
    }.getType();

    public static final Type listOfFramesType = new TypeToken<List<Frame>>() {
    }.getType();

    public static final Gson gson = new Gson();

    public static final String KEY_FILM_ROLLS = "rolls";

    public static List<Roll> getAllRolls(MainActivity activity) {
        // get the items
        String rollsData = activity.getPrefs().getString(KEY_FILM_ROLLS, "[]");
        // convert using gson
        List<Roll> rolls = gson.fromJson(rollsData, listOfRollsType);
        return rolls;
    }

    /**
     * for internal use only
     *
     * @param activity
     * @param rolls
     */
    private static void updateRolls(MainActivity activity, List<Roll> rolls) {
        activity.getPrefs().edit().putString(KEY_FILM_ROLLS, gson.toJson(rolls, listOfRollsType)).commit();
    }

    public static void deleteRoll(MainActivity activity, Roll roll) {
        List<Roll> rolls = StorageUtil.getAllRolls(activity);
        rolls.remove(roll);
        // also delete all frames for that roll at this point
        deleteFramesForRoll(activity, roll);
        updateRolls(activity, rolls);
    }

    private static void deleteFramesForRoll(MainActivity activity, Roll roll) {
        activity.getPrefs().edit().remove(KEY_FILM_ROLLS + roll.getId()).commit();
    }

    public static List<Frame> getFramesForFilm(MainActivity activity, Roll filmRoll) {
        // get the items
        String framesData = activity.getPrefs().getString(KEY_FILM_ROLLS + filmRoll.getId(), "[]");
        // convert using gson
        return gson.fromJson(framesData, listOfFramesType);
    }

    public static void updateFrames(MainActivity activity, Roll filmRoll, List<Frame> frames) {
        activity.getPrefs().edit().putString(KEY_FILM_ROLLS + filmRoll
                .getId(), gson.toJson(frames, listOfFramesType)).commit();
    }


    public static void addNewRoll(MainActivity activity, Roll roll) {
        List<Roll> rolls = getAllRolls(activity);
        rolls.add(roll);
        updateRolls(activity, rolls);
    }
}

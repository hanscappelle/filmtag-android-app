package be.hcpl.android.filmtag.util;

import java.text.DecimalFormat;

public class TextUtil {

    private static DecimalFormat frameFormat = new DecimalFormat("00");
    private static DecimalFormat apertureFormat = new DecimalFormat("0.#");

    public static String formatFrameNumber(int frameNumber) {
        return "#" + frameFormat.format(frameNumber);
    }

    public static String formatAperture(double aperture) {
        return "f/" + apertureFormat.format(aperture);
    }

    public static String formatShutter(int shutter, boolean longExposure) {
        return (longExposure ? "" : "1/") + shutter + " s";
    }
}

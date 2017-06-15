package be.hcpl.android.filmtag.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Frame implements Serializable {

    /**
     * The value that signifies unspecified aperture or shutter value
     */
    public static final int EMPTY_VALUE = 0;

    /**
     * check for long exposures so we can show full seconds
     */
    private boolean longExposure = false;

    /**
     * notes for this frame
     */
    private String notes;

    /**
     * the frame number, set by the system based on the number of frames of a film roll
     */
    private int number;

    /**
     * the shutter speed used for this frame
     */
    private int shutter = EMPTY_VALUE;

    /**
     * the aperture value used for this frame
     */
    private double aperture = EMPTY_VALUE;

    /**
     * path to selected preview image
     */
    private String pathToImage;

    /**
     * where the picture was taken
     */
    private Location location;

    /**
     * tags for frame
     */
    private List<String> tags = new ArrayList();

    public Frame() {
    }

    public Frame(String notes, int number, int shutter, double aperture) {
        this.notes = notes;
        this.number = number;
        this.shutter = shutter;
        this.aperture = aperture;
    }

    public Location getLocation() {
        return location;
    }

    public void setLocation(Location location) {
        this.location = location;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public int getNumber() {
        return number;
    }

    public void setNumber(int number) {
        this.number = number;
    }

    public int getShutter() {
        return shutter;
    }

    public void setShutter(int shutter) {
        this.shutter = shutter;
    }

    public double getAperture() {
        return aperture;
    }

    public void setAperture(double aperture) {
        this.aperture = aperture;
    }

    public List<String> getTags() {
        return tags;
    }

    public void setTags(List<String> tags) {
        this.tags = tags;
    }

    @Override
    public String toString() {
        return new StringBuilder(String.valueOf(number)).append(" - (s) ").append(String.valueOf(shutter)).append(" - (a) f/").append(String.valueOf(aperture)).toString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Frame)) return false;

        Frame frame = (Frame) o;

        return getNumber() == frame.getNumber();

    }

    @Override
    public int hashCode() {
        return getNumber();
    }

    public String getPathToImage() {
        return pathToImage;
    }

    public void setPathToImage(String pathToImage) {
        this.pathToImage = pathToImage;
    }

    public boolean isLongExposure() {
        return longExposure;
    }

    public void setLongExposure(final boolean longExposure) {
        this.longExposure = longExposure;
    }
}

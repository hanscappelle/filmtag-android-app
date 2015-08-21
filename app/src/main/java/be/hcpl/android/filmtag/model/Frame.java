package be.hcpl.android.filmtag.model;

import java.io.Serializable;

/**
 * Created by hcpl on 1/08/15.
 */
public class Frame implements Serializable {

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
    private int shutter;

    /**
     * the aperture value used for this frame
     */
    private double aperture;

    /**
     * path to selected preview image
     */
    private String pathToImage;

    /**
     * where the picture was taken
     */
    private Location location;

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
}

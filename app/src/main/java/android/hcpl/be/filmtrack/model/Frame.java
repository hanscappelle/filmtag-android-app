package android.hcpl.be.filmtrack.model;

import java.io.Serializable;

/**
 * Created by hcpl on 1/08/15.
 */
public class Frame implements Serializable {

    private String notes;

    private int number, shutter;

    private double aperture;

    public Frame() {
    }

    public Frame(String notes, int number, int shutter, double aperture) {
        this.notes = notes;
        this.number = number;
        this.shutter = shutter;
        this.aperture = aperture;
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

        if (getNumber() != frame.getNumber()) return false;
        if (getShutter() != frame.getShutter()) return false;
        if (Double.compare(frame.getAperture(), getAperture()) != 0) return false;
        return getNotes().equals(frame.getNotes());

    }

    @Override
    public int hashCode() {
        int result;
        long temp;
        result = getNotes().hashCode();
        result = 31 * result + getNumber();
        result = 31 * result + getShutter();
        temp = Double.doubleToLongBits(getAperture());
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        return result;
    }
}

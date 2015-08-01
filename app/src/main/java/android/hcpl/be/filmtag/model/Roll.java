package android.hcpl.be.filmtag.model;

import java.io.Serializable;

/**
 * Created by hcpl on 1/08/15.
 */
public class Roll implements Serializable{

    // by adding an ID we can delete items by ID and change them
    private long id;

    private String type;

    private int speed, frames;

    private String notes;

    public Roll() {
    }

    public Roll(String type, int speed, int frames) {
        this.type = type;
        this.speed = speed;
        this.frames = frames;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public int getSpeed() {
        return speed;
    }

    public void setSpeed(int speed) {
        this.speed = speed;
    }

    public int getFrames() {
        return frames;
    }

    public void setFrames(int frames) {
        this.frames = frames;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    @Override
    public String toString() {
        return new StringBuilder(type).append(" @ ISO ").append(speed).append(" # ").append(String.valueOf(frames)).toString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Roll)) return false;

        Roll roll = (Roll) o;

        return getId() == roll.getId();

    }

    @Override
    public int hashCode() {
        return (int) (getId() ^ (getId() >>> 32));
    }
}

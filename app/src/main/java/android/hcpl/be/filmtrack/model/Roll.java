package android.hcpl.be.filmtrack.model;

import java.io.Serializable;

/**
 * Created by hcpl on 1/08/15.
 */
public class Roll implements Serializable{

    // TODO
//    private long id;

    private String type;

    private int speed, frames;

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

    @Override
    public String toString() {
        return new StringBuilder(type).append(" @ ISO ").append(speed).append(" # ").append(String.valueOf(frames)).toString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Roll)) return false;

        Roll roll = (Roll) o;

        if (getSpeed() != roll.getSpeed()) return false;
        if (getFrames() != roll.getFrames()) return false;
        return getType().equals(roll.getType());

    }

    @Override
    public int hashCode() {
        int result = getType().hashCode();
        result = 31 * result + getSpeed();
        result = 31 * result + getFrames();
        return result;
    }
}

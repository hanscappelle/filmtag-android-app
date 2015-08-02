package be.hcpl.android.filmtag.model;

import java.util.List;
import java.util.Map;

/**
 * Created by hcpl on 2/08/15.
 */
public class DataExportFormat {

    private List<Roll> rolls;

    private Map<Long, List<Frame>> frames;

    public List<Roll> getRolls() {
        return rolls;
    }

    public void setRolls(List<Roll> rolls) {
        this.rolls = rolls;
    }

    public Map<Long, List<Frame>> getFrames() {
        return frames;
    }

    public void setFrames(Map<Long, List<Frame>> frames) {
        this.frames = frames;
    }
}

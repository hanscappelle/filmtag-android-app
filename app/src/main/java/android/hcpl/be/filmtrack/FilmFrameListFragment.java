package android.hcpl.be.filmtrack;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by jd41256 on 30/07/15.
 */
public class FilmFrameListFragment extends Fragment {

    public static FilmFrameListFragment newInstance() {
        Bundle args = new Bundle();
        FilmFrameListFragment fragment = new FilmFrameListFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_roll_overview, container);
    }

    // TODO show roll details on top

    //TODO allow for editing each frame
}

package android.hcpl.be.filmtrack;

import android.os.Bundle;
import android.support.v4.app.Fragment;

/**
 * Created by jd41256 on 30/07/15.
 */
public class FilmRollListFragment extends Fragment {

    public static FilmRollListFragment newInstance() {

        Bundle args = new Bundle();

        FilmRollListFragment fragment = new FilmRollListFragment();
        fragment.setArguments(args);
        return fragment;
    }
}

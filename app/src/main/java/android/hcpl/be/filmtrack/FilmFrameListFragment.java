package android.hcpl.be.filmtrack;

import android.hcpl.be.filmtrack.model.Roll;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

/**
 * Created by jd41256 on 30/07/15.
 */
public class FilmFrameListFragment extends Fragment {

    public static final String KEY_FILM_ROLL = "roll";

    private Roll filmRoll;

    private TextView detailTextView;

    private ListView framesListView;

    public static FilmFrameListFragment newInstance(Roll roll) {
        Bundle args = new Bundle();
        args.putSerializable(KEY_FILM_ROLL, roll);
        FilmFrameListFragment fragment = new FilmFrameListFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putSerializable(KEY_FILM_ROLL, filmRoll);
    }

    @Override
    public void onViewStateRestored(@Nullable Bundle savedInstanceState) {
        super.onViewStateRestored(savedInstanceState);
        if (savedInstanceState != null)
            filmRoll = (Roll) savedInstanceState.getSerializable(KEY_FILM_ROLL);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_roll_detail, container, false);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Bundle args = getArguments();
        if (args != null) {
            filmRoll = (Roll) args.getSerializable(KEY_FILM_ROLL);
        }
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        detailTextView = (TextView) view.findViewById(R.id.text_roll_details);
        framesListView = (ListView) view.findViewById(R.id.list_frames);

        // show roll details on top
        if (filmRoll != null && detailTextView != null) {
            detailTextView.setText(filmRoll.toString());
        }

        // TODO and populate list with frame data

    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.frames, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.action_delete:
                deleteCurrentFilmRoll();
                return true;
        }
        return false;
    }

    private void deleteCurrentFilmRoll() {
        // TODO confirmation needed before delete here...

    }

    //TODO allow for editing each frame
}

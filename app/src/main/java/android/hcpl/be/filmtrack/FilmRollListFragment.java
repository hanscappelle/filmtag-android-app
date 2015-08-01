package android.hcpl.be.filmtrack;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

/**
 * an overview of rolls created earlier + option to add new roll of film
 *
 * Created by hcpl on 30/07/15.
 */
public class FilmRollListFragment extends Fragment {

    private ListView mListView;

    public static FilmRollListFragment newInstance() {

        Bundle args = new Bundle();

        FilmRollListFragment fragment = new FilmRollListFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_roll_overview, container, false);
    }

    // TODO populate list from settings

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mListView = (ListView)view.findViewById(R.id.list_rolls);

    }


    // TODO create new roll option

    // TODO show frames on selection
}

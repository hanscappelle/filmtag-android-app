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
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.List;

/**
 * an overview of rolls created earlier + option to add new roll of film
 *
 * Created by hcpl on 30/07/15.
 */
public class FilmRollListFragment extends Fragment {

    // TODO delete film from overview directly (swipe? long press, ...)

    private ListView mListView;

    private ArrayAdapter<Roll> mAdapter;

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

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mListView = (ListView)view.findViewById(R.id.list_rolls);

        // prepare the adapter for that list
        mAdapter = new ArrayAdapter<Roll>(getActivity(), android.R.layout.simple_list_item_1);
        mListView.setAdapter(mAdapter);

        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                showRollDetails(mAdapter.getItem(i));
            }
        });

    }

    @Override
    public void onResume() {
        super.onResume();

        // retrieve list of frames here
        refreshData();
    }

    private void refreshData() {

        List<Roll> rolls = StorageUtil.getAllRolls((MainActivity) getActivity());
        // update adapter
        mAdapter.clear();
        mAdapter.addAll(rolls);
        mAdapter.notifyDataSetChanged();
    }


    private void showRollDetails(Roll roll) {
        // show frames on selection
        ((MainActivity)getActivity()).switchContent(FilmFrameListFragment.newInstance(roll));
        // TODO provide proper up navigation and history for back...
    }

    // create new roll option is in main activity

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
//        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.rolls, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_add) {
            createNewRoll();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void createNewRoll() {
        ((MainActivity)getActivity()).switchContent(NewRollFragment.newInstance());
    }

}

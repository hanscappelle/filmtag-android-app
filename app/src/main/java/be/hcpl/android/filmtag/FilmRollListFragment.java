package be.hcpl.android.filmtag;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.SearchView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import java.util.List;

import be.hcpl.android.filmtag.adapter.FilmRollAdapter;
import be.hcpl.android.filmtag.model.Roll;
import be.hcpl.android.filmtag.util.StorageUtil;

/**
 * an overview of rolls created earlier + option to add new roll of film
 * <p/>
 * Created by hcpl on 30/07/15.
 */
public class FilmRollListFragment extends Fragment {

    // TODO delete film from overview directly (swipe? long press, ...)

    private ListView mListView;

    private FilmRollAdapter mAdapter;

    private SearchView searchView;

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

        mListView = (ListView) view.findViewById(R.id.list_rolls);

        // prepare the adapter for that list
        mAdapter = new FilmRollAdapter(getActivity());
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
        ((MainActivity) getActivity()).switchContent(FilmFrameListFragment.newInstance(roll));
    }

    // create new roll option is in main activity

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.rolls, menu);

        // parent activity
        MainActivity activity = (MainActivity) getActivity();

        // enable the view manually
        searchView = new SearchView(activity.getSupportActionBar().getThemedContext());
        searchView.setIconifiedByDefault(false);
        activity.getSupportActionBar().setCustomView(searchView);
        // not enabled by default
        activity.getSupportActionBar().setDisplayShowCustomEnabled(searchViewEnabled);
        // enable filter view on list
//        mListView.setTextFilterEnabled(searchViewEnabled);
        // text listeners
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                // filter data
                mAdapter.getFilter().filter(query);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                if( "".equals(newText)){
                    // clear results
                    mAdapter.getFilter().filter(null);
                    return true;
                }
                return false;
            }
        });
        // not in use
        searchView.setOnCloseListener(new SearchView.OnCloseListener() {
            @Override
            public boolean onClose() {
                // remove filter on data
//                mListView.setTextFilterEnabled(false);
                mAdapter.getFilter().filter(null);
                return true;
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_add) {
            createNewRoll();
            return true;
        } else if (id == R.id.action_export) {
            shareConfig();
            return true;
        } else if (id == R.id.action_import) {
            importConfig();
            return true;
        } else if (id == R.id.action_about) {
            ((MainActivity) getActivity()).switchContent(AboutFragment.newInstance());
            return true;
        } else if (id == R.id.action_search) {
            toggleSearchView();
        }
        return super.onOptionsItemSelected(item);
    }

    private boolean searchViewEnabled = false;

    private void toggleSearchView() {
        // parent activity
        MainActivity activity = (MainActivity) getActivity();
        // toggle value
        searchViewEnabled = !searchViewEnabled;
        // and apply
        activity.getSupportActionBar().setDisplayShowCustomEnabled(searchViewEnabled);
        // enable filter view on list
//        mListView.setTextFilterEnabled(searchViewEnabled);
    }

    private void createNewRoll() {
        ((MainActivity) getActivity()).switchContent(EditRollFragment.newInstance());
    }

    private void importConfig() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setMessage(R.string.info_import_export)
                .setCancelable(true)
                .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        //do things
                        dialog.dismiss();
                    }
                });
        AlertDialog alert = builder.create();
        alert.show();
    }

    private void shareConfig() {
        Intent sharingIntent = new Intent(Intent.ACTION_SEND);
        sharingIntent.setType("text/plain");
        sharingIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, "FilmTag data export");
        sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, StorageUtil.getExportDataFormattedAsText((MainActivity) getActivity()));
        startActivity(Intent.createChooser(sharingIntent, getResources().getString(R.string.action_export)));
    }

}

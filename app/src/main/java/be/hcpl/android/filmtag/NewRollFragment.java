package be.hcpl.android.filmtag;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import be.hcpl.android.filmtag.model.Roll;
import be.hcpl.android.filmtag.util.StorageUtil;

/**
 * Created by hcpl on 1/08/15.
 */
public class NewRollFragment extends Fragment {

    // TODO also implement an edit option, delete is done from overview

    private EditText editType, editSpeed, editFrames, editNotes;

    public static NewRollFragment newInstance() {
        Bundle args = new Bundle();
        NewRollFragment fragment = new NewRollFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_form_roll, container, false);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.new_film, menu);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        editType = (EditText) view.findViewById(R.id.edit_type);
        editSpeed = (EditText) view.findViewById(R.id.edit_exposed);
        editFrames = (EditText) view.findViewById(R.id.edit_frames);
        editNotes = (EditText) view.findViewById(R.id.edit_notes);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_create:
                createNewItem();
                return true;
            case android.R.id.home:
                backToOverview();
                return true;
        }
        return false;
    }

    private void backToOverview() {
        ((MainActivity) getActivity()).switchContent(FilmRollListFragment.newInstance());
    }

    private void createNewItem() {
        // insert the new item
        Roll roll = new Roll();
        roll.setType(editType.getText().toString());
        roll.setNotes(editNotes.getText().toString());
        try {
            roll.setSpeed(Integer.parseInt(editSpeed.getText().toString()));
            roll.setFrames(Integer.parseInt(editFrames.getText().toString()));
        } catch (NumberFormatException nfe) {
            Toast.makeText(getActivity(), R.string.err_parsing_failed, Toast.LENGTH_SHORT).show();
        }

        // store new roll
        StorageUtil.addNewRoll((MainActivity) getActivity(), roll);

        // navigate to overview
        backToOverview();
    }

    @Override
    public void onResume() {
        super.onResume();
        ((MainActivity) getActivity()).setHomeAsUp(true);
    }
}

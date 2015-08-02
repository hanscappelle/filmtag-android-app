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
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.Toast;

import java.util.List;

import be.hcpl.android.filmtag.model.Roll;
import be.hcpl.android.filmtag.util.StorageUtil;

/**
 * Created by hcpl on 1/08/15.
 */
public class EditRollFragment extends Fragment {

    private static final String KEY_EDIT_ROLL = "edit_roll";
    private AutoCompleteTextView editType;
    private EditText editSpeed, editFrames, editNotes;

    private Roll roll;

    /**
     * use for creating new rolls
     *
     * @return
     */
    public static EditRollFragment newInstance() {
        Bundle args = new Bundle();
        EditRollFragment fragment = new EditRollFragment();
        fragment.setArguments(args);
        return fragment;
    }

    /**
     * use for editing existing rolls
     *
     * @return
     */
    public static EditRollFragment newInstance(Roll roll) {
        Bundle args = new Bundle();
        args.putSerializable(KEY_EDIT_ROLL, roll);
        EditRollFragment fragment = new EditRollFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putSerializable(KEY_EDIT_ROLL, roll);
    }

    @Override
    public void onViewStateRestored(@Nullable Bundle savedInstanceState) {
        super.onViewStateRestored(savedInstanceState);
        if (savedInstanceState != null)
            roll = (Roll) savedInstanceState.getSerializable(KEY_EDIT_ROLL);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);

        Bundle args = getArguments();
        if (args != null) {
            roll = (Roll) args.getSerializable(KEY_EDIT_ROLL);
        }
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

        editType = (AutoCompleteTextView) view.findViewById(R.id.edit_type);
        editSpeed = (EditText) view.findViewById(R.id.edit_exposed);
        editFrames = (EditText) view.findViewById(R.id.edit_frames);
        editNotes = (EditText) view.findViewById(R.id.edit_notes);

        // prefill data if possible
        if (roll != null) {
            editType.setText(roll.getType());
            editNotes.setText(roll.getNotes());
            if (roll.getSpeed() != 0)
                editSpeed.setText(String.valueOf(roll.getSpeed()));
            if (roll.getFrames() != 0)
                editFrames.setText(String.valueOf(roll.getFrames()));
        }

        // autocomplete
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(),
                android.R.layout.simple_dropdown_item_1line, getTypeSuggestions());
        editType.setAdapter(adapter);
    }

    private String[] getTypeSuggestions() {
        List<Roll> rolls = StorageUtil.getAllRolls(((MainActivity) getActivity()));
        if (rolls == null)
            return new String[]{};
        String[] existingTypes = new String[rolls.size()];
        for (int i = 0; i < rolls.size(); i++) {
            existingTypes[i] = rolls.get(i).getType();
        }
        return existingTypes;

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
        if (roll == null)
            ((MainActivity) getActivity()).switchContent(FilmRollListFragment.newInstance());
        else
            ((MainActivity) getActivity()).switchContent(FilmFrameListFragment.newInstance(roll));

    }

    private void createNewItem() {
        boolean newRoll = false;
        // insert the new item
        if (roll == null) {
            roll = new Roll();
            newRoll = true;
        }
        roll.setType(editType.getText().toString());
        roll.setNotes(editNotes.getText().toString());
        try {
            roll.setSpeed(Integer.parseInt(editSpeed.getText().toString()));
        } catch (NumberFormatException nfe) {
            Toast.makeText(getActivity(), R.string.err_parsing_failed, Toast.LENGTH_SHORT).show();
        }
        try {
            roll.setFrames(Integer.parseInt(editFrames.getText().toString()));
        } catch (NumberFormatException nfe) {
            Toast.makeText(getActivity(), R.string.err_parsing_failed, Toast.LENGTH_SHORT).show();
        }

        // store new roll
        if (newRoll)
            StorageUtil.addNewRoll((MainActivity) getActivity(), roll);
        else
            StorageUtil.updateRoll((MainActivity) getActivity(), roll);


        // navigate to overview
        backToOverview();
    }

    @Override
    public void onResume() {
        super.onResume();
        ((MainActivity) getActivity()).setHomeAsUp(true);
    }
}

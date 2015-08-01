package android.hcpl.be.filmtrack;

import android.hcpl.be.filmtrack.model.Frame;
import android.hcpl.be.filmtrack.model.Roll;
import android.hcpl.be.filmtrack.util.StorageUtil;
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

import java.util.ArrayList;
import java.util.List;

/**
 * Created by hcpl on 1/08/15.
 */
public class EditFrameFragment extends Fragment {

    private static final String KEY_FRAME_IDX = "frame_index";
    private static final String KEY_FRAMES = "frames";
    private static final String KEY_ROLL = "roll";

    // TODO this needs to be replaced by inline editing options for list instead

    private EditText editShutter, editAperture, editNotes;

    private Roll roll;

    private Frame selectedFrame;

    private List<Frame> frames;


    public static EditFrameFragment newInstance(Roll roll, List<Frame> frames, int frame) {

        Bundle args = new Bundle();
        args.putSerializable(KEY_FRAMES, (ArrayList)frames);
        args.putInt(KEY_FRAME_IDX, frame);
        args.putSerializable(KEY_ROLL, roll);
        EditFrameFragment fragment = new EditFrameFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        Bundle args = getArguments();
        if( args != null ){
            frames = (List<Frame>)args.getSerializable(KEY_FRAMES);
            selectedFrame = frames.get(args.getInt(KEY_FRAME_IDX));
            roll = (Roll)args.getSerializable(KEY_ROLL);
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putSerializable(KEY_FRAMES, (ArrayList) frames);
        outState.putInt(KEY_FRAME_IDX, frames.indexOf(selectedFrame));
        outState.putSerializable(KEY_ROLL, roll);
    }

    @Override
    public void onViewStateRestored(@Nullable Bundle savedInstanceState) {
        super.onViewStateRestored(savedInstanceState);
        if( savedInstanceState != null ){
            frames = (List<Frame>)savedInstanceState.getSerializable(KEY_FRAMES);
            selectedFrame = frames.get(savedInstanceState.getInt(KEY_FRAME_IDX));
            roll = (Roll)savedInstanceState.getSerializable(KEY_ROLL);
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_form_frame, container, false);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.update_frame, menu);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        editAperture = (EditText) view.findViewById(R.id.edit_aperture);
        editShutter = (EditText) view.findViewById(R.id.edit_shutter);
        editNotes = (EditText) view.findViewById(R.id.edit_notes);

        if( selectedFrame != null ){
            ((EditText)view.findViewById(R.id.edit_number)).setText(String.valueOf(selectedFrame.getNumber()));
            editAperture.setText(String.valueOf(selectedFrame.getAperture()));
            editShutter.setText(String.valueOf(selectedFrame.getShutter()));
            editNotes.setText(selectedFrame.getNotes());
        }

        // TODO implement autocomplete
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_update:
                updateItem();
                return true;
        }
        return false;
    }

    private void updateItem() {
        // update values
        selectedFrame.setNotes(editNotes.getText().toString());
        try {
            selectedFrame.setAperture(Double.parseDouble(editAperture.getText().toString()));
            selectedFrame.setShutter(Integer.parseInt(editShutter.getText().toString()));
        } catch (NumberFormatException nfe) {
            Toast.makeText(getActivity(), R.string.err_parsing_failed, Toast.LENGTH_SHORT).show();
        }

        // store
        StorageUtil.updateFrames((MainActivity) getActivity(), roll, frames);

        // navigate back to overview
        ((MainActivity) getActivity()).switchContent(FilmFrameListFragment.newInstance(roll));
    }
}

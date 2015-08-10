package be.hcpl.android.filmtag;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import be.hcpl.android.filmtag.adapter.FrameAdapter;
import be.hcpl.android.filmtag.model.Frame;
import be.hcpl.android.filmtag.model.Roll;
import be.hcpl.android.filmtag.util.StorageUtil;

/**
 * Created by jd41256 on 30/07/15.
 */
public class FilmFrameListFragment extends Fragment {

    public static final String KEY_FILM_ROLL = "roll";

    private Roll filmRoll;

    private TextView detailTextView;

    private ListView framesListView;

    private FrameAdapter mAdapter;

    // TODO double
    private List<Frame> frames;

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
        setHasOptionsMenu(true);
        Bundle args = getArguments();
        if (args != null) {
            filmRoll = (Roll) args.getSerializable(KEY_FILM_ROLL);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        updateFramesForFilm();
        ((MainActivity)getActivity()).setHomeAsUp(true);
    }

    private void updateFramesForFilm() {

        if (filmRoll != null) {
            frames = StorageUtil.getFramesForFilm(((MainActivity) getActivity()), filmRoll);
            // if the film doesn't have frames yet add them based on the number specified
            if (frames.isEmpty()) {
                for (int i = 0; i <= filmRoll.getFrames(); i++) {
                    Frame frame = new Frame();
                    frame.setNumber(i);
                    frames.add(frame);
                }
            }

            // update list data
            mAdapter.clear();
            mAdapter.addAll(frames);
            mAdapter.notifyDataSetChanged();
        }

    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        detailTextView = (TextView) view.findViewById(R.id.text_roll_details);
        framesListView = (ListView) view.findViewById(R.id.list_frames);

        // show roll details on top
        if (filmRoll != null && detailTextView != null) {
            detailTextView.setText(Html.fromHtml(
                    new StringBuilder(filmRoll.toString()).append("<p/>")
                            .append(filmRoll.getNotes() != null ? filmRoll.getNotes() : getString(R.string.label_no_notes)).toString()));
        }

        // and populate list with frame data
        mAdapter = new FrameAdapter(getActivity());
        framesListView.setAdapter(mAdapter);

        framesListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                updateFrame(i);
            }
        });
    }

    private void updateFrame(int index) {
        ((MainActivity)getActivity()).switchContent(EditFrameFragment.newInstance(filmRoll, frames, index));
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.frames, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_delete:
                deleteCurrentFilmRoll();
                return true;
            case R.id.action_edit:
                editCurrentFilmRoll();
                return true;
            case android.R.id.home:
                // back to overview without change
                backToOverview();
                return true;
        }
        return false;
    }

    private void editCurrentFilmRoll() {
        ((MainActivity) getActivity()).switchContent(EditRollFragment.newInstance(filmRoll));
    }

    private void backToOverview() {
        ((MainActivity) getActivity()).switchContent(FilmRollListFragment.newInstance());
    }

    private void deleteCurrentFilmRoll() {
        // confirmation needed before delete here...
        new AlertDialog.Builder(getActivity())
                //.setTitle(R.string.label_confirm)
                .setMessage(R.string.msg_delete_complete_film_roll)
                .setPositiveButton(R.string.label_yes, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        StorageUtil.deleteRoll((MainActivity) getActivity(), filmRoll);
                        // navigate back
                        dialogInterface.dismiss();
                        backToOverview();
                    }
                }).setNegativeButton(R.string.label_no, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        }).show();


    }
}

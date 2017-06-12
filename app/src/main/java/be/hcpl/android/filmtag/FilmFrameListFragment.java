package be.hcpl.android.filmtag;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.text.Html;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import java.util.List;

import be.hcpl.android.filmtag.adapter.FrameAdapter;
import be.hcpl.android.filmtag.model.Frame;
import be.hcpl.android.filmtag.model.Roll;
import be.hcpl.android.filmtag.template.TemplateFragment;
import be.hcpl.android.filmtag.util.StorageUtil;
import butterknife.Bind;

public class FilmFrameListFragment extends TemplateFragment {

    public static final String KEY_FILM_ROLL = "roll";

    private Roll filmRoll;

    @Bind(R.id.text_roll)
    TextView filmTextView;
    @Bind(R.id.text_roll_details)
    TextView detailTextView;
    @Bind(R.id.list_frames)
    ListView framesListView;

    @Bind(R.id.wrapper_tags)
    LinearLayout wrapperTagsView;

    private FrameAdapter mAdapter;

    // TODO double reference?
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
        if (savedInstanceState != null) {
            filmRoll = (Roll) savedInstanceState.getSerializable(KEY_FILM_ROLL);
        }
    }

    @Override
    protected int getLayoutResourceId() {
        return R.layout.fragment_roll_detail;
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
        ((MainActivity) getActivity()).setHomeAsUp(true);
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
    public void onViewCreated(
            View view,
            @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // show roll details on top
        if (filmRoll != null && detailTextView != null && detailTextView != null) {
            filmTextView.setText(filmRoll.toString());
            if(TextUtils.isEmpty(filmRoll.getNotes())){
                detailTextView.setVisibility(View.GONE);
            } else {
                detailTextView.setText(filmRoll.getNotes());
                detailTextView.setVisibility(View.VISIBLE);
            }
            // also load tags here
            if (filmRoll.getTags() != null && !filmRoll.getTags().isEmpty()) {
                wrapperTagsView.setVisibility(View.VISIBLE);
                for (String tag : filmRoll.getTags()) {
                    TextView tv = new TextView(getContext());
                    tv.setText(tag);
                    tv.setAllCaps(true);
                    tv.setPadding(10, 0, 10, 0); // TODO proper units needed here?
                    tv.setTextSize(14); // TODO proper units needed here?
                    wrapperTagsView.addView(tv);
                }
            } else {
                wrapperTagsView.setVisibility(View.GONE);
            }
        }

        // and populate list with frame data
        mAdapter = new FrameAdapter(getActivity());
        framesListView.setAdapter(mAdapter);

        framesListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(
                    AdapterView<?> adapterView,
                    View view,
                    int i,
                    long l) {
                updateFrame(i);
            }
        });
    }

    private void updateFrame(int index) {
        ((MainActivity) getActivity()).switchContent(EditFrameFragment.newInstance(filmRoll, frames, index));
    }

    @Override
    public void onCreateOptionsMenu(
            Menu menu,
            MenuInflater inflater) {
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
                    public void onClick(
                            DialogInterface dialogInterface,
                            int i) {
                        StorageUtil.deleteRoll((MainActivity) getActivity(), filmRoll);
                        // navigate back
                        dialogInterface.dismiss();
                        backToOverview();
                    }
                }).setNegativeButton(R.string.label_no, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(
                    DialogInterface dialogInterface,
                    int i) {
                dialogInterface.dismiss();
            }
        }).show();

    }
}

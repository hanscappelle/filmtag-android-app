package be.hcpl.android.filmtag;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import be.hcpl.android.filmtag.model.Frame;
import be.hcpl.android.filmtag.model.Roll;
import be.hcpl.android.filmtag.template.TemplateFragment;
import be.hcpl.android.filmtag.util.StorageUtil;

/**
 * Created by hcpl on 1/08/15.
 */
public class EditFrameFragment extends TemplateFragment {

    // all image related code from http://developer.android.com/training/camera/photobasics.html

    private static final String KEY_FRAME_IDX = "frame_index";
    private static final String KEY_FRAMES = "frames";
    private static final String KEY_ROLL = "roll";

    // TODO this needs to be replaced by inline editing options for list instead

    private EditText editShutter, editAperture, editNotes;

    private Roll roll;

    private Frame selectedFrame;

    private List<Frame> frames;

    private ImageView imagePreview, imagePreviewIndicator;

    public static EditFrameFragment newInstance(Roll roll, List<Frame> frames, int frame) {
        Bundle args = new Bundle();
        args.putSerializable(KEY_FRAMES, (ArrayList) frames);
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
        if (args != null) {
            frames = (List<Frame>) args.getSerializable(KEY_FRAMES);
            selectedFrame = frames.get(args.getInt(KEY_FRAME_IDX));
            roll = (Roll) args.getSerializable(KEY_ROLL);
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
        if (savedInstanceState != null) {
            frames = (List<Frame>) savedInstanceState.getSerializable(KEY_FRAMES);
            selectedFrame = frames.get(savedInstanceState.getInt(KEY_FRAME_IDX));
            roll = (Roll) savedInstanceState.getSerializable(KEY_ROLL);
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
        imagePreview = (ImageView) view.findViewById(R.id.image_preview);
        imagePreviewIndicator = (ImageView) view.findViewById(R.id.image_preview_indicator);

        if (selectedFrame != null) {
            ((TextView) view.findViewById(R.id.edit_number)).setText(String.valueOf(selectedFrame.getNumber()));
            if (selectedFrame.getAperture() != 0)
                editAperture.setText(String.valueOf(selectedFrame.getAperture()));
            if (selectedFrame.getShutter() != 0)
                editShutter.setText(String.valueOf(selectedFrame.getShutter()));
            editNotes.setText(selectedFrame.getNotes());
            loadImagePreview(selectedFrame);
            markImageAvailable();
        }

        // TODO implement autocomplete
    }

    private void markImageAvailable() {
        imagePreviewIndicator.setImageDrawable(selectedFrame.getPathToImage() != null ?
                ContextCompat.getDrawable(getActivity(), R.drawable.ic_action_image_photo_camera_primary) :
                ContextCompat.getDrawable(getActivity(), R.drawable.ic_action_image_photo_camera_silver));
    }

    private void loadImagePreview(Frame selectedFrame) {
        if (selectedFrame.getPathToImage() != null) {
            try {
                BitmapFactory.Options options = new BitmapFactory.Options();
                options.inSampleSize = 4;
                options.inJustDecodeBounds = false;
                Bitmap bm = BitmapFactory.decodeFile(selectedFrame.getPathToImage(), options);
                imagePreview.setImageBitmap(bm);
            } catch (Exception e) {
                // ignore any exceptions here
                Log.e(getTag(), "failed to load image from configured path", e);

            }
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_update:
                updateItem();
                return true;
            case android.R.id.home:
                backToOverview();
                return true;
            case R.id.action_camera:
                dispatchTakePictureIntent();
                return true;
        }
        return false;
    }

    static final int REQUEST_IMAGE_CAPTURE = 1;

    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // Ensure that there's a camera activity to handle the intent
        if (takePictureIntent.resolveActivity(getActivity().getPackageManager()) != null) {
            // Create the File where the photo should go
            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException ex) {
                // Error occurred while creating the File
                Log.e(getTag(), "failed to create temp image file", ex);
                Toast.makeText(getActivity(), R.string.error_creating_image, Toast.LENGTH_SHORT).show();
            }
            // Continue only if the File was successfully created
            if (photoFile != null) {
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT,
                        Uri.fromFile(photoFile));
                startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
            }
        }
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == Activity.RESULT_OK) {
//            Bundle extras = data.getExtras();
//            Bitmap imageBitmap = (Bitmap) extras.get("data");
            // show the image
//            imagePreview.setImageBitmap(imageBitmap);
            loadImagePreview(selectedFrame);
            markImageAvailable();
        }
    }

    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );

        // Save a file: path for use with ACTION_VIEW intents
        selectedFrame.setPathToImage(image.getAbsolutePath());
        return image;
    }

    private void updateItem() {
        // update values
        selectedFrame.setNotes(editNotes.getText().toString());
        try {
            selectedFrame.setAperture(Double.parseDouble(editAperture.getText().toString()));
        } catch (NumberFormatException nfe) {
            Toast.makeText(getActivity(), R.string.err_parsing_failed, Toast.LENGTH_SHORT).show();
        }
        try {
            selectedFrame.setShutter(Integer.parseInt(editShutter.getText().toString()));
        } catch (NumberFormatException nfe) {
            Toast.makeText(getActivity(), R.string.err_parsing_failed, Toast.LENGTH_SHORT).show();
        }

        // store
        StorageUtil.updateFrames((MainActivity) getActivity(), roll, frames);

        // navigate back to overview
        backToOverview();
    }

    private void backToOverview() {
        ((MainActivity) getActivity()).switchContent(FilmFrameListFragment.newInstance(roll));
    }

    @Override
    public void onResume() {
        super.onResume();
        ((MainActivity) getActivity()).setHomeAsUp(true);
    }
}

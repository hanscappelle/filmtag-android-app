package be.hcpl.android.filmtag;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import be.hcpl.android.filmtag.model.Frame;
import be.hcpl.android.filmtag.model.Roll;
import be.hcpl.android.filmtag.template.TemplateFragment;
import be.hcpl.android.filmtag.util.StorageUtil;
import butterknife.Bind;

/**
 * Created by hcpl on 1/08/15.
 */
public class EditFrameFragment extends TemplateFragment {

    // all image related code from http://developer.android.com/training/camera/photobasics.html

    private static final String KEY_FRAME_IDX = "frame_index";
    private static final String KEY_FRAMES = "frames";
    private static final String KEY_ROLL = "roll";
    private static final int MY_PERMISSIONS_REQUEST_LOCATION = 100;
    private static final int MY_PERMISSIONS_REQUEST_STORAGE = 200;
    private static final int REQUEST_IMAGE_CAPTURE = 1;

    @Bind(R.id.edit_shutter)
    EditText editShutter;
    @Bind(R.id.edit_aperture)
    EditText editAperture;
    @Bind(R.id.edit_notes)
    EditText editNotes;
    @Bind(R.id.long_exposure)
    CheckBox checkLongExposure;

    @Bind(R.id.image_preview)
    ImageView imagePreview;
    @Bind(R.id.image_preview_indicator)
    ImageView imagePreviewIndicator;
    @Bind(R.id.image_location_indicator)
    ImageView imageLocationIndicator;

    @Bind(R.id.edit_tags)
    EditText editTags;

    @Bind(R.id.text_location)
    TextView locationView;

    // model object of a film or roll with a number of frames exposed at a given value
    private Roll roll;

    private Frame selectedFrame;

    private List<Frame> frames;

    private static boolean locationPermissionRequested = false;
    private static boolean storagePermissionRequested = false;
    private static boolean storagePermissionRequestedForPreview = false;

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
    protected int getLayoutResourceId() {
        return R.layout.fragment_form_frame;
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

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.update_frame, menu);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        if (selectedFrame != null) {
            ((TextView) view.findViewById(R.id.edit_number)).setText(String.valueOf(selectedFrame.getNumber()));
            if (selectedFrame.getAperture() != 0)
                editAperture.setText(String.valueOf(selectedFrame.getAperture()));
            if (selectedFrame.getShutter() != 0)
                editShutter.setText(String.valueOf(selectedFrame.getShutter()));
            checkLongExposure.setChecked(selectedFrame.isLongExposure());
            editNotes.setText(selectedFrame.getNotes());
            // populate the tags here
            if (selectedFrame.getTags() != null && !selectedFrame.getTags().isEmpty())
                editTags.setText(TextUtils.join(" ", selectedFrame.getTags()));
            loadImagePreview();
            showLocation();
        }

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getContext());
        editShutter.setHint(prefs.getString("key_default_shutter", String.valueOf(60)));
        editAperture.setHint(prefs.getString("key_default_apertures", String.valueOf(4)));

        // TODO implement autocomplete
    }

    private void markImageAvailable() {
        imagePreviewIndicator.setImageDrawable(selectedFrame.getPathToImage() != null ?
                ContextCompat.getDrawable(getActivity(), R.drawable.ic_action_image_photo_camera_primary) :
                ContextCompat.getDrawable(getActivity(), R.drawable.ic_action_image_photo_camera_silver));
    }

    private void markLocationAvailable() {
        imageLocationIndicator.setImageDrawable(selectedFrame.getLocation() != null ?
                ContextCompat.getDrawable(getActivity(), R.drawable.ic_action_device_gps_primary) :
                ContextCompat.getDrawable(getActivity(), R.drawable.ic_action_device_gps_silver));
        if (selectedFrame.getLocation() != null) {
            View.OnClickListener onClickListener = new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    showMap(Uri.parse("geo:" + selectedFrame.getLocation().getLatitude() + "," + selectedFrame.getLocation().getLongitude()));
                }
            };
            imageLocationIndicator.setOnClickListener(onClickListener);
            locationView.setOnClickListener(onClickListener);
        }
    }

    private void showMap(Uri geoLocation) {
        if (geoLocation == null)
            return;
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setData(geoLocation);
        if (intent.resolveActivity(getActivity().getPackageManager()) != null) {
            startActivity(intent);
        }
    }

    private void loadImagePreview() {
        // mark image available first so that if we don't have permission it's still marked
        markImageAvailable();
        // and if path set try loading after permission check
        if (selectedFrame.getPathToImage() != null) {
            // for this storage permission also required
            if (ContextCompat.checkSelfPermission(getActivity(),
                    Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                if( !storagePermissionRequestedForPreview ) {
                    storagePermissionRequestedForPreview = true;
                    requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                            MY_PERMISSIONS_REQUEST_STORAGE);
                }
                return;
            }
            try {
                BitmapFactory.Options options = new BitmapFactory.Options();
                options.inSampleSize = 4;
                options.inJustDecodeBounds = false;
                Bitmap bm = BitmapFactory.decodeFile(selectedFrame.getPathToImage(), options);
                imagePreview.setImageBitmap(bm);
                // and try updating location
//                extractLocationFromFile();
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
            case R.id.action_location:
                getLocation();
                return true;
        }
        return false;
    }


    private void dispatchTakePictureIntent() {
        // check permissions first
        if (ContextCompat.checkSelfPermission(getActivity(),
                Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {

            // TODO ignore rationale for now
//            if (ActivityCompat.shouldShowRequestPermissionRationale(getActivity(),
//                    Manifest.permission.ACCESS_FINE_LOCATION)) { //... } else {

            // No explanation needed, we can request the permission.
            if( !storagePermissionRequested ) {
                storagePermissionRequested = true;
                requestPermissions(
                        new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                        MY_PERMISSIONS_REQUEST_STORAGE);
            }
            return;

        }

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
            loadImagePreview();
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

//    private void extractLocationFromFile() throws IOException {
//        ExifInterface exif = new ExifInterface(selectedFrame.getPathToImage());
//        String lat = exif.getAttribute(ExifInterface.TAG_GPS_LATITUDE);
//        String lon = exif.getAttribute(ExifInterface.TAG_GPS_LONGITUDE);
//        if (lat != null && lon != null) {
//            locationView.setText(getString(R.string.label_location) + " " + lat + " " + lon);
//        }
//    }

    private void getLocation() {
        if (ContextCompat.checkSelfPermission(getActivity(),
                Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            // TODO ignore rationale for now
//            if (ActivityCompat.shouldShowRequestPermissionRationale(getActivity(),
//                    Manifest.permission.ACCESS_FINE_LOCATION)) { //... } else {

            // No explanation needed, we can request the permission.
            if( !locationPermissionRequested ){
            locationPermissionRequested = true;
            requestPermissions(
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    MY_PERMISSIONS_REQUEST_LOCATION);
            }
            return;
        }
        registerLocationListener(LocationManager.GPS_PROVIDER);
    }

    /**
     * register location listeners
     *
     * @param provider
     */
    private void registerLocationListener(final String provider) {
        if (ContextCompat.checkSelfPermission(getActivity(),
                Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        // remove previous listener first
        unregisterListener();
        // get current location to provide as defaults into
        // field
        LocationManager locationManager = (LocationManager) getActivity()
                .getSystemService(Context.LOCATION_SERVICE);
        // begin by getting the last known location
        Location fetchedLocationDetails = locationManager.getLastKnownLocation(provider);
        if (fetchedLocationDetails != null) {
            // update current location
            if (selectedFrame != null) {
                selectedFrame.setLocation(new be.hcpl.android.filmtag.model.Location(fetchedLocationDetails.getLatitude(), fetchedLocationDetails.getLongitude()));
                showLocation();
            }
        }
        // and start listening in order to update the location when more
        // information is retrieved
        // Register the listener with the Location Manager to receive location
        // updates
        locationManager
                .requestLocationUpdates(provider, 0, 0, locationListener);
    }

    /**
     * unregister location listeners
     */
    private void unregisterListener() {
        if (ContextCompat.checkSelfPermission(getActivity(),
                Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        // get current location to provide as defaults into
        // field
        LocationManager locationManager = (LocationManager) getActivity()
                .getSystemService(Context.LOCATION_SERVICE);
        // remove previous listener first
        locationManager.removeUpdates(locationListener);
    }

    @Override
    public void onPause() {
        super.onPause();
        unregisterListener();
    }

    /**
     * listener for updating location when more data is found
     */
    private LocationListener locationListener = new LocationListener() {
        @Override
        public void onLocationChanged(final Location location) {
            // Called when a new location is found by the selected location
            // provider.
            if (selectedFrame != null) {
                selectedFrame.setLocation(new be.hcpl.android.filmtag.model.Location(location.getLatitude(), location.getLongitude()));
                // set on screen
                showLocation();
            }
        }

        @Override
        public void onStatusChanged(final String provider, final int status,
                                    final Bundle extras) {
            // nothing so far
        }

        @Override
        public void onProviderEnabled(final String provider) {
            // nothing so far
        }

        @Override
        public void onProviderDisabled(final String provider) {
            Toast.makeText(getActivity(), R.string.err_location_disabled, Toast.LENGTH_SHORT).show();
        }
    };

    private void showLocation() {
        if (selectedFrame != null && selectedFrame.getLocation() != null) {
            be.hcpl.android.filmtag.model.Location location = selectedFrame.getLocation();
            locationView.setText(getString(R.string.label_location) + " " + location.getLatitude() + " " + location.getLongitude());
        }
        markLocationAvailable();
    }

    private void updateItem() {
        // update values
        selectedFrame.setNotes(editNotes.getText().toString());

        try {
            selectedFrame.setAperture(Double.parseDouble(
                    getFieldTextOrDefault(editAperture, "key_default_apertures", String.valueOf(4))
            ));
        } catch (NumberFormatException nfe) {
            Toast.makeText(getActivity(), R.string.err_parsing_failed, Toast.LENGTH_SHORT).show();
        }

        try {
            selectedFrame.setShutter(Integer.parseInt(
                    getFieldTextOrDefault(editShutter, "key_default_shutter", String.valueOf(60))
            ));
        } catch (NumberFormatException nfe) {
            Toast.makeText(getActivity(), R.string.err_parsing_failed, Toast.LENGTH_SHORT).show();
        }
        selectedFrame.setLongExposure(checkLongExposure.isChecked());
        selectedFrame.setTags(Arrays.asList(TextUtils.split(editTags.getText().toString(), " ")));

        // store
        StorageUtil.updateFrames((MainActivity) getActivity(), roll, frames);

        // navigate back to overview
        backToOverview();
    }

    private String getFieldTextOrDefault(EditText field, String defaultKey, String hardDefault) {
        String text = field.getText().toString();
        if (text.equals("")) {
            SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getContext());
            return prefs.getString(defaultKey, hardDefault);
        }
        else {
            return text;
        }
    }

    private void backToOverview() {
        ((MainActivity) getActivity()).switchContent(FilmFrameListFragment.newInstance(roll));
    }

    @Override
    public void onResume() {
        super.onResume();
        ((MainActivity) getActivity()).setHomeAsUp(true);
        if (locationPermissionRequested) {
            getLocation();
            locationPermissionRequested = false;
        }
        if (storagePermissionRequested) {
            dispatchTakePictureIntent();
            storagePermissionRequested = false;
        }
        if( storagePermissionRequestedForPreview){
            loadImagePreview();
            storagePermissionRequestedForPreview = false;
        }
    }
}

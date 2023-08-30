package be.hcpl.android.filmtag

import android.Manifest
import android.app.Activity
import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.preference.PreferenceManager
import android.provider.MediaStore
import android.text.TextUtils
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.widget.CheckBox
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.core.os.bundleOf
import androidx.navigation.fragment.findNavController
import be.hcpl.android.filmtag.FilmFrameListFragment.Companion.KEY_FILM_ROLL
import be.hcpl.android.filmtag.model.Frame
import be.hcpl.android.filmtag.model.Roll
import be.hcpl.android.filmtag.template.TemplateFragment
import be.hcpl.android.filmtag.util.StorageUtil
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.Arrays
import java.util.Date


/**
 * Created by hcpl on 1/08/15.
 */
class EditFrameFragment : TemplateFragment() {

    // model object of a film or roll with a number of frames exposed at a given value
    private var roll: Roll? = null

    private var selectedFrame: Frame? = null
    private var previousFrame: Frame? = null

    private var frames: List<Frame>? = null

    // views
    private lateinit var edit_aperture: EditText
    private lateinit var edit_shutter: EditText
    private lateinit var long_exposure: CheckBox
    private lateinit var edit_notes: EditText
    private lateinit var edit_tags: EditText
    private lateinit var text_location: TextView
    private lateinit var image_location_indicator: ImageView
    private lateinit var image_preview: ImageView

    override val layoutResourceId: Int
        get() = R.layout.fragment_form_frame

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
        restoreState(arguments)
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putSerializable(KEY_FRAMES, frames as ArrayList<*>?)
        outState.putInt(KEY_FRAME_IDX, frames!!.indexOf(selectedFrame))
        outState.putSerializable(KEY_ROLL, roll)
    }

    override fun onViewStateRestored(savedInstanceState: Bundle?) {
        super.onViewStateRestored(savedInstanceState)
        restoreState(savedInstanceState)
    }

    private fun restoreState(state: Bundle?) {
        if (state != null) {
            frames = state.getSerializable(KEY_FRAMES) as List<Frame>
            val selectedFrameIndex = state.getInt(KEY_FRAME_IDX)
            selectedFrame = frames!![selectedFrameIndex]
            previousFrame = if (selectedFrameIndex > 0) frames!![selectedFrameIndex - 1] else null
            roll = state.getSerializable(KEY_ROLL) as Roll
        }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.update_frame, menu)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        edit_aperture = view.findViewById(R.id.edit_aperture)
        edit_shutter = view.findViewById(R.id.edit_shutter)
        long_exposure = view.findViewById(R.id.long_exposure)
        edit_notes = view.findViewById(R.id.edit_notes)
        edit_tags = view.findViewById(R.id.edit_tags)

        image_location_indicator = view.findViewById(R.id.image_location_indicator)
        image_preview = view.findViewById(R.id.image_preview)

        text_location = view.findViewById(R.id.text_location)

        if (selectedFrame != null) {
            (view.findViewById(R.id.edit_number) as TextView).text = selectedFrame!!.number.toString()
            if (selectedFrame!!.aperture != 0.0)
                edit_aperture.setText(selectedFrame!!.aperture.toString())
            if (selectedFrame!!.shutter != 0)
                edit_shutter.setText(selectedFrame!!.shutter.toString())
            long_exposure.isChecked = selectedFrame!!.isLongExposure
            edit_notes.setText(selectedFrame!!.notes)
            // populate the tags here
            if (selectedFrame!!.tags.isNotEmpty())
                edit_tags.setText(TextUtils.join(" ", selectedFrame!!.tags))
            loadImagePreview() // load from frame storage
            showLocation()
        }

        updateHints()

        // TODO implement autocomplete for inputs
    }

    // Uses previous frame values for aperture & shutter as hints if available
    // Otherwise uses default aperture & shutter from settings
    // Also, when previous frame has long-exposure checked, check it automatically
    private fun updateHints() {
        val prefs = PreferenceManager.getDefaultSharedPreferences(context)

        if (previousFrame != null && previousFrame!!.aperture != Frame.EMPTY_VALUE.toDouble()) {
            edit_aperture.hint = previousFrame!!.aperture.toString()
        } else {
            edit_aperture.hint = prefs.getString("key_default_apertures", 4.toString())
        }

        if (previousFrame != null && previousFrame!!.shutter != Frame.EMPTY_VALUE) {
            edit_shutter.hint = previousFrame!!.shutter.toString()
        } else {
            edit_shutter.hint = prefs.getString("key_default_shutter", 60.toString())
        }

        if (previousFrame != null && previousFrame!!.isLongExposure) {
            long_exposure.isChecked = true
        }
    }

    private fun markLocationAvailable() {
        image_location_indicator.setImageDrawable(if (selectedFrame!!.location != null)
            ContextCompat.getDrawable(requireContext(), R.drawable.ic_action_device_gps_primary)
        else
            ContextCompat.getDrawable(requireContext(), R.drawable.ic_action_device_gps_silver))
        if (selectedFrame!!.location != null) {
            val onClickListener = View.OnClickListener { showMap(Uri.parse("geo:" + selectedFrame!!.location!!.latitude + "," + selectedFrame!!.location!!.longitude)) }
            image_location_indicator.setOnClickListener(onClickListener)
            text_location.setOnClickListener(onClickListener)
        }
    }

    private fun showMap(geoLocation: Uri?) {
        if (geoLocation == null)
            return
        val intent = Intent(Intent.ACTION_VIEW)
        intent.data = geoLocation
        if (intent.resolveActivity(requireContext().packageManager) != null) {
            startActivity(intent)
        }
    }

    private fun loadImagePreview() {
        // and if path set try loading after permission check
        if (selectedFrame?.pathToImage != null) {
            try {
                val options = BitmapFactory.Options()
                options.inSampleSize = 4
                options.inJustDecodeBounds = false
                val bm = BitmapFactory.decodeFile(selectedFrame?.pathToImage, options)
                image_preview.setImageBitmap(bm)
            } catch (e: Exception) {
                // ignore any exceptions here
                handleImageError()
            }
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.action_update -> {
                updateItem()
                return true
            }
            android.R.id.home -> {
                backToDetail()
                return true
            }
            R.id.action_camera -> {
                dispatchTakePictureIntent()
                return true
            }
            R.id.action_location -> {
                getLocation()
                return true
            }
        }
        return false
    }

    private fun dispatchTakePictureIntent() {
        val takePictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        try {
            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE)
        } catch (e: ActivityNotFoundException) {
            // display error state to the user
            handleImageError();
        }
    }

    private fun handleImageError() {
        Toast.makeText(requireContext(), R.string.error_creating_image, Toast.LENGTH_LONG).show()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == Activity.RESULT_OK) {
            loadImagePreviewFromIntent(data)
        }
    }

    private fun loadImagePreviewFromIntent(data: Intent?) {
        val imageBitmap = data?.extras?.get("data") as Bitmap
        image_preview.setImageBitmap(imageBitmap);
        val file = createImageFile()
        try {
            FileOutputStream(file.absolutePath).use { out ->
                imageBitmap.compress(Bitmap.CompressFormat.JPEG, 100, out)
            }
            // save image path at this point
            StorageUtil.updateFrames(activity as MainActivity, roll!!, frames!!)
        } catch (e: IOException) {
            handleImageError()
        }
    }

    @Throws(IOException::class)
    private fun createImageFile(): File {
        // Create an image file name
        val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())
        val imageFileName = "JPEG_" + timeStamp + "_"
        val storageDir = Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES)
        val image = File.createTempFile(
                imageFileName, /* prefix */
                ".jpg", /* suffix */
                storageDir      /* directory */
        )

        // Save a file: path for use with ACTION_VIEW intents
        selectedFrame!!.pathToImage = image.absolutePath
        return image
    }

    private fun getLocation() {
        if (ContextCompat.checkSelfPermission(requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            // No explanation needed, we can request the permission.
            if (!locationPermissionRequested) {
                locationPermissionRequested = true
                requestPermissions(
                        arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                        MY_PERMISSIONS_REQUEST_LOCATION)
            }
            return
        }
        registerLocationListener(LocationManager.GPS_PROVIDER)
    }

    private fun registerLocationListener(provider: String) {
        if (ContextCompat.checkSelfPermission(requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return
        }
        // remove previous listener first
        unregisterListener()
        // get current location to provide as defaults into
        // field
        val locationManager = requireContext().getSystemService(Context.LOCATION_SERVICE) as LocationManager
        // begin by getting the last known location
        val fetchedLocationDetails = locationManager.getLastKnownLocation(provider)
        if (fetchedLocationDetails != null) {
            // update current location
            if (selectedFrame != null) {
                selectedFrame!!.location = be.hcpl.android.filmtag.model.Location(fetchedLocationDetails.latitude, fetchedLocationDetails.longitude)
                showLocation()
            }
        }
        // and start listening in order to update the location when more
        // information is retrieved
        // Register the listener with the Location Manager to receive location
        // updates
        locationManager
                .requestLocationUpdates(provider, 0, 0f, locationListener)
    }

    private fun unregisterListener() {
        if (ContextCompat.checkSelfPermission(requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return
        }
        // get current location to provide as defaults into
        // field
        val locationManager = requireContext().getSystemService(Context.LOCATION_SERVICE) as LocationManager
        // remove previous listener first
        locationManager.removeUpdates(locationListener)
    }

    override fun onPause() {
        super.onPause()
        unregisterListener()
    }

    /**
     * listener for updating location when more data is found
     */
    private val locationListener = object : LocationListener {
        override fun onLocationChanged(location: Location) {
            // Called when a new location is found by the selected location
            // provider.
            if (selectedFrame != null) {
                selectedFrame!!.location = be.hcpl.android.filmtag.model.Location(location.latitude, location.longitude)
                // set on screen
                showLocation()
            }
        }

        override fun onStatusChanged(provider: String, status: Int,
                                     extras: Bundle) {
            // nothing so far
        }

        override fun onProviderEnabled(provider: String) {
            // nothing so far
        }

        override fun onProviderDisabled(provider: String) {
            Toast.makeText(activity, R.string.err_location_disabled, Toast.LENGTH_SHORT).show()
        }
    }

    private fun showLocation() {
        if (selectedFrame != null && selectedFrame!!.location != null) {
            val location = selectedFrame!!.location
            text_location.text = getString(R.string.label_location) + " " + location!!.latitude + " " + location.longitude
        }
        markLocationAvailable()
    }

    private fun updateItem() {
        // update values
        selectedFrame!!.notes = edit_notes.text.toString()

        try {
            selectedFrame!!.aperture = java.lang.Double.parseDouble(getFieldTextOrHint(edit_aperture))
        } catch (nfe: NumberFormatException) {
            Toast.makeText(activity, R.string.err_parsing_failed, Toast.LENGTH_SHORT).show()
        }

        try {
            selectedFrame!!.shutter = Integer.parseInt(getFieldTextOrHint(edit_shutter))
        } catch (nfe: NumberFormatException) {
            Toast.makeText(activity, R.string.err_parsing_failed, Toast.LENGTH_SHORT).show()
        }

        selectedFrame!!.isLongExposure = long_exposure.isChecked
        selectedFrame!!.tags = Arrays.asList(*TextUtils.split(edit_tags.text.toString(), " "))

        // store
        StorageUtil.updateFrames(activity as MainActivity, roll!!, frames!!)

        // navigate back to overview
        backToDetail()
    }

    private fun getFieldTextOrHint(field: EditText): String {
        val text = field.text.toString()
        return if (TextUtils.isEmpty(text)) {
            field.hint.toString()
        } else {
            text
        }
    }

    private fun backToDetail() {
        findNavController().navigate(R.id.action_detail, bundleOf(KEY_FILM_ROLL to roll))
    }

    override fun onResume() {
        super.onResume()
        if (locationPermissionRequested) {
            getLocation()
            locationPermissionRequested = false
        }
    }

    companion object {

        // all image related code from http://developer.android.com/training/camera/photobasics.html

        const val KEY_FRAME_IDX = "frame_index"
        const val KEY_FRAMES = "frames"
        const val KEY_ROLL = "roll"

        private val MY_PERMISSIONS_REQUEST_LOCATION = 100
        private val MY_PERMISSIONS_REQUEST_STORAGE = 200
        private val REQUEST_IMAGE_CAPTURE = 300

        private var locationPermissionRequested = false
        private var storagePermissionRequested = false
        private var storagePermissionRequestedForPreview = false


        fun newInstance(roll: Roll?, frames: List<Frame>?, frame: Int): EditFrameFragment {
            val args = Bundle()
            args.putSerializable(KEY_FRAMES, frames as ArrayList<*>)
            args.putInt(KEY_FRAME_IDX, frame)
            args.putSerializable(KEY_ROLL, roll)
            val fragment = EditFrameFragment()
            fragment.arguments = args
            return fragment
        }
    }
}

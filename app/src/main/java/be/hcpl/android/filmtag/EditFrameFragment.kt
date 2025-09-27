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
import android.widget.DatePicker
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.core.content.ContextCompat.getDrawable
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import be.hcpl.android.filmtag.FilmFrameListFragment.Companion.KEY_FILM_ROLL
import be.hcpl.android.filmtag.model.Frame
import be.hcpl.android.filmtag.model.Roll
import be.hcpl.android.filmtag.util.StorageUtil
import com.google.android.material.datepicker.MaterialDatePicker
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.Arrays
import java.util.Calendar
import java.util.Date
import java.util.Locale


/**
 * Created by hcpl on 1/08/15.
 */
class EditFrameFragment : Fragment(R.layout.fragment_form_frame) {

    // model object of a film or roll with a number of frames exposed at a given value
    private var roll: Roll? = null

    private var selectedFrame: Frame? = null
    private var previousFrame: Frame? = null

    private var frames: List<Frame>? = null

    // views
    private lateinit var editAperture: EditText
    private lateinit var editShutter: EditText
    private lateinit var editExposure: CheckBox
    private lateinit var editNotes: EditText
    private lateinit var editTags: EditText
    private lateinit var textNumber: TextView
    private lateinit var textLocation: TextView
    private lateinit var iconLocation: ImageView
    private lateinit var imagePreview: ImageView
    private lateinit var dateView: TextView

    // date picker
    private lateinit var datePicker: MaterialDatePicker<Long>
    val dateFormatter = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())

    private fun initDatePickerWith(date: Long) = MaterialDatePicker.Builder.datePicker()
        .setTitleText(getString(R.string.select_date))
        .setSelection(date)
        .build()

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
        state?.let {
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

        // find all views
        editAperture = view.findViewById(R.id.edit_aperture)
        editShutter = view.findViewById(R.id.edit_shutter)
        editExposure = view.findViewById(R.id.long_exposure)
        editNotes = view.findViewById(R.id.edit_notes)
        editTags = view.findViewById(R.id.edit_tags)
        iconLocation = view.findViewById(R.id.image_location_indicator)
        imagePreview = view.findViewById(R.id.image_preview)
        textLocation = view.findViewById(R.id.text_location)
        textNumber = view.findViewById(R.id.edit_number)
        dateView = view.findViewById(R.id.edit_date)

        // populate with frame details
        selectedFrame?.let {
            textNumber.text = "${it.number}"
            if (it.aperture != 0.0)
                editAperture.setText(it.aperture.toString())
            if (it.shutter != 0)
                editShutter.setText(it.shutter.toString())
            editExposure.isChecked = it.isLongExposure
            editNotes.setText(it.notes)
            // populate the tags here
            if (it.tags.isNotEmpty())
                editTags.setText(TextUtils.join(" ", it.tags))
            loadImagePreview() // load from frame storage
            showLocation()
            // update date
            val validDate = it.dateTaken?: Calendar.getInstance().timeInMillis
            dateView.setText(dateFormatter.format(validDate))
            datePicker = initDatePickerWith(validDate)
            dateView.setOnClickListener{
                datePicker.show(childFragmentManager, TAG_DATEPICKER)
            }
            datePicker.addOnPositiveButtonClickListener {
                // Respond to positive button click.
                dateView.text = dateFormatter.format(datePicker.selection)
            }
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
            editAperture.hint = previousFrame!!.aperture.toString()
        } else {
            editAperture.hint = prefs.getString("key_default_apertures", 4.toString())
        }

        if (previousFrame != null && previousFrame!!.shutter != Frame.EMPTY_VALUE) {
            editShutter.hint = previousFrame!!.shutter.toString()
        } else {
            editShutter.hint = prefs.getString("key_default_shutter", 60.toString())
        }

        if (previousFrame != null && previousFrame!!.isLongExposure) {
            editExposure.isChecked = true
        }
    }

    private fun updateLocationViews() {
        selectedFrame?.location?.let {
            iconLocation.setImageDrawable(
                getDrawable(requireContext(), R.drawable.ic_action_device_gps_primary)
            )
            textLocation.setOnClickListener {
                showOnMap(Uri.parse("geo: ${selectedFrame?.location?.latitude},${selectedFrame?.location?.longitude}"))
            }
        } ?: {
            // no known location set
            iconLocation.setImageDrawable(
                getDrawable(requireContext(), R.drawable.ic_action_device_gps_silver)
            )
            textLocation.setOnClickListener { getLocation() }
        }
    }

    private fun showOnMap(geoLocation: Uri?) {
        geoLocation?.let {
            val intent = Intent(Intent.ACTION_VIEW)
            intent.data = geoLocation
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
                imagePreview.setImageBitmap(bm)
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
        imagePreview.setImageBitmap(imageBitmap);
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
            Environment.DIRECTORY_PICTURES
        )
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
        if (ContextCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {

            // No explanation needed, we can request the permission.
            if (!locationPermissionRequested) {
                locationPermissionRequested = true
                requestPermissions(
                    arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                    MY_PERMISSIONS_REQUEST_LOCATION
                )
            }
            return
        }
        registerLocationListener(LocationManager.GPS_PROVIDER)
    }

    private fun registerLocationListener(provider: String) {
        if (ContextCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            return
        }
        // remove previous listener first
        unregisterListener()
        // get current location to provide as defaults into
        // field
        val locationManager =
            requireContext().getSystemService(Context.LOCATION_SERVICE) as LocationManager
        // begin by getting the last known location
        val fetchedLocationDetails = locationManager.getLastKnownLocation(provider)
        if (fetchedLocationDetails != null) {
            // update current location
            if (selectedFrame != null) {
                selectedFrame!!.location = be.hcpl.android.filmtag.model.Location(
                    fetchedLocationDetails.latitude,
                    fetchedLocationDetails.longitude
                )
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
        if (ContextCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            return
        }
        // get current location to provide as defaults into
        // field
        val locationManager =
            requireContext().getSystemService(Context.LOCATION_SERVICE) as LocationManager
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
                selectedFrame!!.location =
                    be.hcpl.android.filmtag.model.Location(location.latitude, location.longitude)
                // set on screen
                showLocation()
            }
        }

        override fun onStatusChanged(
            provider: String, status: Int,
            extras: Bundle
        ) {
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
        selectedFrame?.location?.let { location ->
            textLocation.text =
                "${getString(R.string.label_location)} ${location.latitude} ${location.longitude}"
            updateItem(false) // also update location in storage autom.
        }
        updateLocationViews()
    }

    private fun updateItem(navigateBack: Boolean = true) {
        // update values
        selectedFrame!!.notes = editNotes.text.toString()

        try {
            selectedFrame!!.aperture =
                java.lang.Double.parseDouble(getFieldTextOrHint(editAperture))
        } catch (_: NumberFormatException) {
            Toast.makeText(activity, R.string.err_parsing_failed, Toast.LENGTH_SHORT).show()
        }

        try {
            selectedFrame!!.shutter = Integer.parseInt(getFieldTextOrHint(editShutter))
        } catch (_: NumberFormatException) {
            Toast.makeText(activity, R.string.err_parsing_failed, Toast.LENGTH_SHORT).show()
        }
        try {
            selectedFrame!!.dateTaken = dateView.text?.let { dateFormatter.parse(it.toString()).time }
        } catch (_: NumberFormatException) {
            //fail silently for date
            // Toast.makeText(activity, R.string.err_parsing_failed, Toast.LENGTH_SHORT).show()
        }
        selectedFrame!!.isLongExposure = editExposure.isChecked
        selectedFrame!!.tags = Arrays.asList(*TextUtils.split(editTags.text.toString(), " "))

        // store
        StorageUtil.updateFrames(activity as MainActivity, roll!!, frames!!)

        // navigate back to overview
        if (navigateBack) backToDetail()
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
        const val TAG_DATEPICKER = "datePicker"

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

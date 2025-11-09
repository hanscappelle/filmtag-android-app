package be.hcpl.android.filmtag.ui.activity


import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.net.Uri
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.appcompat.app.AlertDialog
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.core.content.ContextCompat
import be.hcpl.android.filmtag.R
import be.hcpl.android.filmtag.ui.Action
import be.hcpl.android.filmtag.ui.ActionId
import be.hcpl.android.filmtag.ui.AppScaffold
import be.hcpl.android.filmtag.ui.activity.EditFrameViewModel.Event
import be.hcpl.android.filmtag.ui.activity.FilmRollActivity.Companion.KEY_FILM_ROLL_ID
import be.hcpl.android.filmtag.ui.view.DatePickerModal
import be.hcpl.android.filmtag.ui.view.EditFrameView
import be.hcpl.android.filmtag.ui.view.RollDetailView
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf

class EditFrameActivity : ComponentActivity() {

    private val viewModel: EditFrameViewModel by viewModel(
        parameters = {
            parametersOf(
                intent.getLongExtra(KEY_FILM_ROLL_ID, -1L),
                intent.getIntExtra(KEY_FRAME_ID, -1),
            )
        }
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel.state.observe(this, ::handleState)
        viewModel.events.observe(this, ::handleEvent)
    }

    private fun handleState(state: EditFrameViewModel.State) {
        setContent {
            AppScaffold(
                actions = listOf(
                    Action(
                        iconRes = if (state.roll.isDeveloped) R.drawable.ic_lock_closed else R.drawable.ic_lock_open,
                        textRes = R.string.action_edit,
                        actionId = ActionId.Update,
                    ),
                    Action(
                        iconRes = R.drawable.ic_action_check,
                        textRes = R.string.action_create,
                        actionId = ActionId.Create,
                    ),
                    Action(
                        iconRes = R.drawable.ic_action_close,
                        textRes = R.string.action_close,
                        actionId = ActionId.Close,
                    ),
                ),
                handleAction = ::handleAction,
            ) { innerPadding ->

                // modal date picker
                val showDatePicker = remember { mutableStateOf(false) }
                if (showDatePicker.value) {
                    DatePickerModal(
                        onDateSelected = { selectedDate ->
                            viewModel.updateSelectedDate(selectedDate)
                            showDatePicker.value = false
                        },
                        onDismiss = {
                            showDatePicker.value = false
                        },
                    )
                }

                // edit form fields populated from state
                Column(modifier = Modifier.padding(innerPadding)) {
                    RollDetailView(state.roll)
                    EditFrameView(
                        viewState = state.editState,
                        onSelectDate = {
                            showDatePicker.value = true
                        },
                        onShowLocation = { location ->
                            viewModel.prepareShowLocation(location)
                        },
                        onUpdateLocation = {
                            confirmUpdateLocation()
                        }
                    )
                }
            }
        }
    }

    private fun handleAction(actionId: ActionId) {
        when (actionId) {
            ActionId.Close -> finish()
            ActionId.Create -> viewModel.saveFrame()
            ActionId.Update -> viewModel.toggleLocked()
            else -> Unit
        }
    }

    private fun handleEvent(event: Event) {
        when (event) {
            Event.Close -> finish()
            is Event.Message -> Toast.makeText(this, event.resourceId, Toast.LENGTH_SHORT).show()
            is Event.ShowOnMap -> showOnMap(event.uri)
        }
    }

    private fun confirmUpdateLocation() {
        AlertDialog.Builder(this)
            .setMessage(R.string.msg_confirm_update_location)
            .setPositiveButton(R.string.label_yes) { _, _ ->
                getLocation()
            }.setNegativeButton(R.string.label_no) { _, _ -> }.show()
    }

    // region lifecycle

    override fun onPause() {
        super.onPause()
        unregisterListener()
    }

    override fun onResume() {
        super.onResume()
        if (locationPermissionRequested) {
            getLocation()
            locationPermissionRequested = false
        }
    }

    // endregion

    // region location related code here

    private var locationPermissionRequested = false

    private fun showOnMap(geoLocation: Uri?) {
        geoLocation?.let {
            val intent = Intent(Intent.ACTION_VIEW)
            intent.data = geoLocation
            startActivity(intent)
        }
    }

    private fun getLocation() {
        if (ContextCompat.checkSelfPermission(
                this,
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
        registerLocationListener()
    }

    private fun registerLocationListener() {
        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            return
        }
        val provider = LocationManager.GPS_PROVIDER
        // remove previous listener first
        unregisterListener()
        // get current location to provide as defaults into
        // field
        val locationManager = getSystemService(LOCATION_SERVICE) as LocationManager
        // begin by getting the last known location
        val fetchedLocationDetails = locationManager.getLastKnownLocation(provider)
        if (fetchedLocationDetails != null) {
            // update current location
            viewModel.updateLocation(be.hcpl.android.filmtag.model.Location(
                    fetchedLocationDetails.latitude,
                    fetchedLocationDetails.longitude
                )
            )
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
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            return
        }
        // get current location to provide as defaults into
        // field
        val locationManager = getSystemService(LOCATION_SERVICE) as LocationManager
        // remove previous listener first
        locationManager.removeUpdates(locationListener)
    }

    /**
     * listener for updating location when more data is found
     */
    private val locationListener = object : LocationListener {
        override fun onLocationChanged(location: Location) {
            // Called when a new location is found by the selected location provider.
            viewModel.updateLocation(be.hcpl.android.filmtag.model.Location(location.latitude, location.longitude))
        }

        override fun onStatusChanged(
            provider: String, status: Int,
            extras: Bundle,
        ) = Unit // nothing so far

        override fun onProviderEnabled(provider: String) = Unit // nothing so far

        override fun onProviderDisabled(provider: String) {
            Toast.makeText(this@EditFrameActivity, R.string.err_location_disabled, Toast.LENGTH_SHORT).show()
        }
    }

    // endregion

    companion object {
        const val KEY_FRAME_ID = "KEY_FRAME_ID"
        const val MY_PERMISSIONS_REQUEST_LOCATION = 100
    }
}

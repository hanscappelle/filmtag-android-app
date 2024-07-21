package be.hcpl.android.filmtag.model

import androidx.annotation.Keep
import java.io.Serializable

/**
 * Created by hcpl on 21/08/15.
 */
@Keep
class Location : Serializable {

    val latitude: Double
    val longitude: Double

    constructor(latitude: Double, longitude: Double) {
        this.latitude = latitude
        this.longitude = longitude
    }
}

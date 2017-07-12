package be.hcpl.android.filmtag.model

import java.io.Serializable

/**
 * Created by hcpl on 21/08/15.
 */
class Location : Serializable {

    var latitude: Double = 0.0
    var longitude: Double = 0.0

    constructor(latitude: Double, longitude: Double) {
        this.latitude = latitude
        this.longitude = longitude
    }
}

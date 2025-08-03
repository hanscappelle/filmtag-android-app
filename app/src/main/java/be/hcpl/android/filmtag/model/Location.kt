package be.hcpl.android.filmtag.model

import androidx.annotation.Keep
import java.io.Serializable

@Keep
data class Location(
    val latitude: Double,
    val longitude: Double,
) : Serializable

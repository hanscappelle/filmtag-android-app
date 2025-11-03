package be.hcpl.android.filmtag.model

import androidx.annotation.Keep
import java.io.Serializable
import java.util.ArrayList

@Keep
data class Roll(
    var id: Long = -1,
    var type: String? = null,
    var speed: Int = 200,
    var frames: Int = 36,
    var notes: String? = null,
    var isDeveloped: Boolean = false,
    var tags: List<String> = ArrayList(),
) : Serializable {

    init {
        id = if (id > 0) id else System.currentTimeMillis() // generates unique ID for all objects created
    }

    override fun toString(): String {
        return StringBuilder(type.orEmpty()).append(" @ ISO ").append(speed).append(" # ").append(frames.toString()).toString()
    }

    override fun equals(o: Any?): Boolean {
        return if (this === o) true
        else (o as? Roll)?.id == id

    }

    override fun hashCode(): Int {
        return (id xor id.ushr(32)).toInt()
    }
}

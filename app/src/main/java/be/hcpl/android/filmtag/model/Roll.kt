package be.hcpl.android.filmtag.model

import androidx.annotation.Keep
import java.io.Serializable
import java.util.ArrayList

@Keep
data class Roll(
    var id: Long = 0,
    var type: String? = null,
    var speed: Int = 200,
    var frames: Int = 36,
    var notes: String? = null,
    var isDeveloped: Boolean = false,
    var tags: List<String> = ArrayList(),
) : Serializable {

    init {
        id = System.currentTimeMillis() // generates unique ID for all objects created
    }

    override fun toString(): String {
        return StringBuilder(type).append(" @ ISO ").append(speed).append(" # ").append(frames.toString()).toString()
    }

    override fun equals(o: Any?): Boolean {
        if (this === o) return true
        if (o !is Roll) return false

        val roll = o as Roll?

        return id == roll!!.id

    }

    override fun hashCode(): Int {
        return (id xor id.ushr(32)).toInt()
    }
}

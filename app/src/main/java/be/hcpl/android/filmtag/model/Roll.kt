package be.hcpl.android.filmtag.model

import java.io.Serializable
import java.util.ArrayList

/**
 * Created by hcpl on 1/08/15.
 */
class Roll() : Serializable {

    /**
     * by adding an ID we can delete items by ID and change them
     */
    var id: Long = 0

    /**
     * type indication of film roll, think of brand
     */
    var type: String? = null

    /**
     * ISO speed of film roll
     */
    var speed = 200

    /**
     * number of frames of film roll, note that this is used to get the number of frames available
     * for recording settings
     */
    var frames = 36

    /**
     * notes for this film roll
     */
    var notes: String? = null

    /**
     * if film was developed or not
     */
    var isDeveloped: Boolean = false

    /**
     * a collection of tags for this item
     */
    var tags: List<String> = ArrayList()

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

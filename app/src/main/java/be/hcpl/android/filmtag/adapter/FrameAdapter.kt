package be.hcpl.android.filmtag.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView
import be.hcpl.android.filmtag.EditFrameFragment

import java.util.ArrayList

import be.hcpl.android.filmtag.R
import be.hcpl.android.filmtag.model.Frame
import be.hcpl.android.filmtag.util.TextUtil

class FrameAdapter(context: Context) : BaseAdapter() {

    private val items = ArrayList<Frame>()

    private val mInflater: LayoutInflater

    init {
        mInflater = LayoutInflater.from(context)
    }

    override fun getCount(): Int {
        return items.size
    }

    override fun getItem(i: Int): Frame {
        return items[i]
    }

    override fun getItemId(i: Int): Long {
        return i.toLong()
    }

    override fun getView(
            i: Int,
            convertView: View?,
            viewGroup: ViewGroup): View? {

        val frame = items[i] ?: return convertView

        // no need to continue if we have no data

        var rowView: View? = convertView
        if (rowView == null) {
            rowView = mInflater.inflate(R.layout.list_item_frame, viewGroup, false)

            val holder = ViewHolder()
            holder.textFrame = rowView!!.findViewById(R.id.text_frame) as TextView
            holder.textDate = rowView!!.findViewById(R.id.text_date) as TextView
            holder.textApertureAndShutter = rowView.findViewById(R.id.text_aperture_and_shutter) as TextView
            holder.textNotes = rowView.findViewById(R.id.text_notes) as TextView
            rowView.tag = holder
        }
        val holder = rowView.tag as ViewHolder

        // First line: frame number, aperture, shutter speed
        holder.textFrame!!.text = TextUtil.formatFrameNumber(frame.number)
        holder.textDate!!.text = frame.dateTaken?.let { EditFrameFragment.dateFormatter.format(it) }
        holder.textApertureAndShutter!!.text = formatApertureAndShutter(
                frame.aperture,
                frame.shutter,
                frame.isLongExposure)
        // Second line: notes
        holder.textNotes!!.text = frame.notes

        return rowView
    }

    private fun formatApertureAndShutter(aperture: Double, shutter: Int, longExposure: Boolean): String {
        var str = ""
        if (aperture != Frame.EMPTY_VALUE.toDouble()) {
            str += TextUtil.formatAperture(aperture)
        }
        if (aperture != Frame.EMPTY_VALUE.toDouble() && shutter != Frame.EMPTY_VALUE) {
            str += " - "
        }
        if (shutter != Frame.EMPTY_VALUE) {
            str += TextUtil.formatShutter(shutter, longExposure)
        }
        return str
    }

    fun clear() {
        items.clear()
    }

    fun addAll(frames: List<Frame>) {
        items.addAll(frames)
    }

    private inner class ViewHolder {

        internal var textApertureAndShutter: TextView? = null
        internal var textFrame: TextView? = null
        internal var textDate: TextView? = null
        internal var textNotes: TextView? = null
    }
}
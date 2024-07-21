package be.hcpl.android.filmtag.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.CheckBox
import android.widget.Filter
import android.widget.Filterable
import android.widget.TextView

import java.util.ArrayList

import be.hcpl.android.filmtag.R
import be.hcpl.android.filmtag.model.Roll

/**
 * Created by jd41256 on 10/08/15.
 */
class FilmRollAdapter(private val mContext: Context) : BaseAdapter(), Filterable {

    private var items = ArrayList<Roll>()

    private var unFilteredList = ArrayList<Roll>()

    private val mInflater: LayoutInflater = LayoutInflater.from(mContext)

    override fun getCount() = items.size

    override fun getItem(i: Int) = items[i]

    override fun getItemId(i: Int) = i.toLong()

    override fun getView(i: Int, convertView: View?, viewGroup: ViewGroup): View? {

        val roll = items[i] ?: return convertView

        // no need to continue if we have no data

        var rowView: View? = convertView
        if (rowView == null) {
            rowView = mInflater.inflate(R.layout.list_item_roll, viewGroup, false)

            val holder = ViewHolder()
            holder.textFrames = rowView.findViewById(R.id.text_frames)
            holder.textType = rowView.findViewById(R.id.text_type)
            holder.textSpeed = rowView.findViewById(R.id.text_speed)
            holder.textFrames = rowView.findViewById(R.id.text_frames)
            holder.developed = rowView.findViewById(R.id.check_developed)
            rowView.tag = holder
        }
        val holder = rowView?.tag as ViewHolder

        // format data, still on single line for now
        holder.textType?.text = roll.type?.ifEmpty { "..." }
        holder.textSpeed?.text = mContext.resources.getString(R.string.label_roll_speed) +
                " " + roll.speed
        holder.textFrames?.text = roll.frames.toString() + " " +
                mContext.resources.getString(R.string.label_roll_frames)

        // mark developed items with a lighter text color
        holder.developed?.isChecked = roll.isDeveloped

        return rowView
    }

    fun clear() {
        items.clear()
        // backup all data first
        unFilteredList = ArrayList(items)
    }

    fun addAll(rolls: List<Roll>) {
        items.addAll(rolls)
        // backup all data first
        unFilteredList = ArrayList(items)
    }

    override fun getFilter(): Filter {

        return object : Filter() {

            override fun publishResults(constraint: CharSequence?, results: FilterResults) {

                // then perform filtering on data
                items = results.values as ArrayList<Roll>
                this@FilmRollAdapter.notifyDataSetChanged()
            }

            override fun performFiltering(constraint: CharSequence?): FilterResults {
                val filteredResults = getFilteredResults(constraint)
                val results = FilterResults()
                results.values = filteredResults
                return results
            }

            private fun getFilteredResults(constraint: CharSequence?): List<Roll> {
                val results = ArrayList<Roll>()
                // only filter if data set
                if (constraint != null) {
                    for (roll in unFilteredList) {
                        // search on film types
                        if (roll.type?.contains(constraint.toString()) == true)
                            results.add(roll)
                        else if (roll.tags.contains(constraint.toString()))
                            results.add(roll)// and on tags
                        // skip description, too much text to search on
                    }
                } else {
                    results.addAll(unFilteredList)
                }// reset to unfiltered list of all data
                return results
            }

        }
    }

    private inner class ViewHolder {
        var developed: CheckBox? = null
        var textFrames: TextView? = null
        var textSpeed: TextView? = null
        var textType: TextView? = null
    }
}

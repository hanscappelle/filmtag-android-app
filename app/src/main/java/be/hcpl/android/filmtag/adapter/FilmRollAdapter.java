package be.hcpl.android.filmtag.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import be.hcpl.android.filmtag.R;
import be.hcpl.android.filmtag.model.Roll;
import be.hcpl.android.filmtag.util.TextUtil;

/**
 * Created by jd41256 on 10/08/15.
 */
public class FilmRollAdapter extends BaseAdapter implements Filterable {

    private List<Roll> items = new ArrayList();

    private List<Roll> unFilteredList = new ArrayList();

    private Context mContext;

    private LayoutInflater mInflater;

    public FilmRollAdapter(final Context context) {
        this(context, new ArrayList<Roll>());
    }

    public FilmRollAdapter(final Context context, final List<Roll> rollList) {
        mContext = context;
        items = rollList;
        // backup all data first
        unFilteredList = new ArrayList<>(items);
        mInflater = LayoutInflater.from(mContext);
    }

    @Override
    public int getCount() {
        return items.size();
    }

    @Override
    public Roll getItem(int i) {
        return items.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View convertView, ViewGroup viewGroup) {

        final Roll roll = items.get(i);

        // no need to continue if we have no data
        if (roll == null)
            return convertView;

        View rowView = convertView;
        if (rowView == null) {
            rowView = mInflater.inflate(R.layout.list_item_roll, viewGroup, false);

            final ViewHolder holder = new ViewHolder();
            holder.textType = (TextView) rowView.findViewById(R.id.text_type);
            holder.textSpeed = (TextView) rowView.findViewById(R.id.text_speed);
            holder.textFrames = (TextView) rowView.findViewById(R.id.text_frames);
            rowView.setTag(holder);
        }
        final ViewHolder holder = (ViewHolder) rowView.getTag();

        // format data, still on single line for now
        holder.textType.setText(roll.getType());
        holder.textSpeed.setText(new StringBuilder(mContext.getResources()
                .getString(R.string.label_roll_speed)).append(TextUtil.SPACE)
                .append(roll.getSpeed()));
        holder.textFrames.setText(roll.getFrames() + " " +
                mContext.getResources().getString(R.string.label_roll_frames));

        // mark developed items with a lighter text color
        if (roll.isDeveloped())
            holder.textType.setTextColor(mContext.getResources().getColor(R.color.secondary_text));
        else
            holder.textType.setTextColor(mContext.getResources().getColor(R.color.primary_text));

        return rowView;
    }

    public void clear() {
        items.clear();
        // backup all data first
        unFilteredList = new ArrayList<>(items);
    }

    public void addAll(List<Roll> rolls) {
        items.addAll(rolls);
        // backup all data first
        unFilteredList = new ArrayList<>(items);
    }

    @Override
    public Filter getFilter() {

        return new Filter() {

            @SuppressWarnings("unchecked")
            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {

                // then perform filtering on data
                items = (List<Roll>) results.values;
                FilmRollAdapter.this.notifyDataSetChanged();
            }

            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                List<Roll> filteredResults = getFilteredResults(constraint);
                FilterResults results = new FilterResults();
                results.values = filteredResults;
                return results;
            }

            private List<Roll> getFilteredResults(CharSequence constraint) {
                final List<Roll> results = new ArrayList<Roll>();
                // only filter if data set
                if (constraint != null) {
                    for (Roll roll : unFilteredList) {
                        // search on film types
                        if (roll != null && roll.getType() != null && roll.getType().contains(constraint.toString()))
                            results.add(roll);
                        // and on tags
                        else if (roll != null && roll.getTags() != null && roll.getTags().contains(constraint.toString()))
                            results.add(roll);
                        // skip description, too much text to search on
                    }
                }
                // reset to unfiltered list of all data
                else {
                    results.addAll(unFilteredList);
                }
                return results;
            }

        };
    }

    class ViewHolder {
        TextView textFrames, textSpeed, textType;
    }
}

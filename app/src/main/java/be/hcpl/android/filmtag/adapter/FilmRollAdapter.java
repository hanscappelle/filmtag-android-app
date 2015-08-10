package be.hcpl.android.filmtag.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import be.hcpl.android.filmtag.model.Roll;

/**
 * Created by jd41256 on 10/08/15.
 */
public class FilmRollAdapter extends BaseAdapter {

    private List<Roll> items = new ArrayList();

    private Context mContext;

    private LayoutInflater mInflater;

    public FilmRollAdapter(final Context context) {
        this(context, new ArrayList<Roll>());
    }

    public FilmRollAdapter(final Context context, final List<Roll> rollList) {
        mContext = context;
        items = rollList;
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
            rowView = mInflater.inflate(android.R.layout.simple_list_item_1, viewGroup, false);

            final ViewHolder holder = new ViewHolder();
            holder.textView = (TextView) rowView.findViewById(android.R.id.text1);
            rowView.setTag(holder);
        }
        final ViewHolder holder = (ViewHolder) rowView.getTag();

        // format data, still on single line for now
        holder.textView.setText(
                new StringBuilder(roll.getType()).append(" @ ISO ").append(roll.getSpeed())
                        .append(" # ").append(String.valueOf(roll.getFrames())).toString()
        );

        return rowView;
    }

    public void clear() {
        items.clear();
    }

    public void addAll(List<Roll> rolls) {
        items.addAll(rolls);
    }

    class ViewHolder {
        TextView textView;
    }
}

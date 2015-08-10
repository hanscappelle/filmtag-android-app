package be.hcpl.android.filmtag.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import be.hcpl.android.filmtag.model.Frame;

/**
 * Created by jd41256 on 10/08/15.
 */
public class FrameAdapter extends BaseAdapter {

    private List<Frame> items = new ArrayList();

    private Context mContext;

    private LayoutInflater mInflater;

    private DecimalFormat format = new DecimalFormat("00");

    public FrameAdapter(final Context context) {
        this(context, new ArrayList<Frame>());
    }

    public FrameAdapter(final Context context, final List<Frame> list) {
        mContext = context;
        items = list;
        mInflater = LayoutInflater.from(mContext);
    }

    @Override
    public int getCount() {
        return items.size();
    }

    @Override
    public Frame getItem(int i) {
        return items.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View convertView, ViewGroup viewGroup) {

        final Frame frame = items.get(i);

        // no need to continue if we have no data
        if( frame == null )
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
                new StringBuilder(format.format(frame.getNumber())).append(" - (s) ")
                        .append(String.valueOf(frame.getShutter())).append(" - (a) f/")
                        .append(String.valueOf(frame.getAperture())).toString()
        );

        return rowView;
    }

    public void clear() {
        items.clear();
    }

    public void addAll(List<Frame> frames) {
        items.addAll(frames);
    }

    class ViewHolder {
        TextView textView;
    }
}
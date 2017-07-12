package be.hcpl.android.filmtag.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import be.hcpl.android.filmtag.R;
import be.hcpl.android.filmtag.model.Frame;
import be.hcpl.android.filmtag.util.TextUtil;

public class FrameAdapter extends BaseAdapter {

    private List<Frame> items = new ArrayList<>();

    private LayoutInflater mInflater;

    public FrameAdapter(final Context context) {
        mInflater = LayoutInflater.from(context);
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
    public View getView(
            int i,
            View convertView,
            ViewGroup viewGroup) {

        final Frame frame = items.get(i);

        // no need to continue if we have no data
        if (frame == null) {
            return convertView;
        }

        View rowView = convertView;
        if (rowView == null) {
            rowView = mInflater.inflate(R.layout.list_item_frame, viewGroup, false);

            final ViewHolder holder = new ViewHolder();
            holder.textFrame = (TextView) rowView.findViewById(R.id.text_frame);
            holder.textApertureAndShutter = (TextView) rowView.findViewById(R.id.text_aperture_and_shutter);
            holder.textNotes = (TextView) rowView.findViewById(R.id.text_notes);
            rowView.setTag(holder);
        }
        final ViewHolder holder = (ViewHolder) rowView.getTag();

        // First line: frame number, aperture, shutter speed
        holder.textFrame.setText(TextUtil.formatFrameNumber(frame.getNumber()));
        holder.textApertureAndShutter.setText(formatApertureAndShutter(
                frame.getAperture(),
                frame.getShutter(),
                frame.isLongExposure()));
        // Second line: notes
        holder.textNotes.setText(frame.getNotes());

        return rowView;
    }

    private String formatApertureAndShutter(double aperture, int shutter, boolean longExposure) {
        String str = "";
        if (aperture != Frame.EMPTY_VALUE) {
            str += TextUtil.formatAperture(aperture);
        }
        if (aperture != Frame.EMPTY_VALUE && shutter != Frame.EMPTY_VALUE) {
            str += " - ";
        }
        if (shutter != Frame.EMPTY_VALUE) {
            str += TextUtil.formatShutter(shutter, longExposure);
        }
        return str;
    }

    public void clear() {
        items.clear();
    }

    public void addAll(List<Frame> frames) {
        items.addAll(frames);
    }

    class ViewHolder {

        TextView textApertureAndShutter, textFrame, textNotes;
    }
}
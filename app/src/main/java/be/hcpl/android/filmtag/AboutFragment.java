package be.hcpl.android.filmtag;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.Html;
import android.text.util.Linkify;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import be.hcpl.android.filmtag.template.TemplateFragment;

/**
 * Created by hcpl on 6/08/15.
 */
public class AboutFragment extends TemplateFragment {

    private static final String SYSTEM_LINE_SEPARATOR = System.getProperty("line.separator");

    public static AboutFragment newInstance() {
        Bundle args = new Bundle();
        AboutFragment fragment = new AboutFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    protected int getLayoutResourceId() {
        return R.layout.fragment_about;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        String aboutText = readFile(getResources().openRawResource(R.raw.about));
        // add version
        aboutText = aboutText.replace("{version}", BuildConfig.VERSION_NAME);
        // set text
        TextView textView = ((TextView)view.findViewById(R.id.text_about));
        textView.setText(Html.fromHtml(aboutText));
        // and make clickable
        Linkify.addLinks(textView, Linkify.ALL);
    }

    // TODO move to utils instead
    private String readFile(InputStream stream) {
        BufferedReader in = null;

        try {
            in = new BufferedReader(new InputStreamReader(stream));
            StringBuilder buffer = new StringBuilder();

            String e;
            while((e = in.readLine()) != null) {
                buffer.append(e).append(SYSTEM_LINE_SEPARATOR);
            }

            StringBuilder var5 = buffer;
            return var5.toString();
        } catch (IOException var8) {
            ;
        } finally {
            closeStream(in);
        }

        return "";
    }

    private void closeStream(Closeable stream) {
        if(stream != null) {
            try {
                stream.close();
            } catch (IOException var2) {
                ;
            }
        }

    }
}

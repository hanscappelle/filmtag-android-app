package be.hcpl.android.filmtag.template;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import butterknife.ButterKnife;

/**
 * Created by hcpl on 20/08/15.
 */
public abstract class TemplateFragment extends Fragment {

    /**
     * enables us to handle back pressed actions in fragment
     * @return
     */
    public boolean onBackPressed() {
        return false;
    }

//    /**
//     * enables dispatching activity results to fragments
//     *
//     * @param requestCode
//     * @param resultCode
//     * @param data
//     */
//    public boolean onActivityResult(int requestCode, int resultCode, Intent data) {
//        return false;
    // TODO handle permissions & other
//    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(getLayoutResourceId(), container, false);
        ButterKnife.bind(this, view);
        return view;
    }

    protected abstract int getLayoutResourceId();
}

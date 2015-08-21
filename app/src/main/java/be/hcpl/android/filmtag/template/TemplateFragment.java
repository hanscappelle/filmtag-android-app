package be.hcpl.android.filmtag.template;

import android.content.Intent;
import android.support.v4.app.Fragment;

/**
 * Created by hcpl on 20/08/15.
 */
public class TemplateFragment extends Fragment {

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
//    }
}

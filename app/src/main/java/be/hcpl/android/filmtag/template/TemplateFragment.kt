package be.hcpl.android.filmtag.template

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment

/**
 * Created by hcpl on 20/08/15.
 */
abstract class TemplateFragment : Fragment() {

    /**
     * enables us to handle back pressed actions in fragment
     */
    open fun onBackPressed(): Boolean {
        return false
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(layoutResourceId, container, false)
    }

    protected abstract val layoutResourceId: Int
}

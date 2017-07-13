package be.hcpl.android.filmtag.template

import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import butterknife.ButterKnife

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

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater!!.inflate(layoutResourceId, container, false)
        ButterKnife.bind(this, view)
        return view
    }

    protected abstract val layoutResourceId: Int
}

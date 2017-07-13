package be.hcpl.android.filmtag


import android.os.Bundle
import android.support.v4.app.Fragment
import android.text.Html
import android.text.util.Linkify
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView

import java.io.BufferedReader
import java.io.Closeable
import java.io.IOException
import java.io.InputStream
import java.io.InputStreamReader

import be.hcpl.android.filmtag.template.TemplateFragment

/**
 * Created by hcpl on 6/08/15.
 */
class AboutFragment : TemplateFragment() {

    override val layoutResourceId: Int
        get() = R.layout.fragment_about

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        var aboutText = readFile(resources.openRawResource(R.raw.about))
        // add version
        aboutText = aboutText.replace("{version}", BuildConfig.VERSION_NAME)
        // set text
        val textView = view!!.findViewById(R.id.text_about) as TextView
        textView.text = Html.fromHtml(aboutText)
        // and make clickable
        Linkify.addLinks(textView, Linkify.ALL)
    }

    // TODO move to utils instead
    private fun readFile(stream: InputStream): String {
        var `in`: BufferedReader? = null

        try {
            `in` = BufferedReader(InputStreamReader(stream))
            val buffer = StringBuilder()

            var e: String
            while ((e = `in`.readLine()) != null) {
                buffer.append(e).append(SYSTEM_LINE_SEPARATOR)
            }

            return buffer.toString()
        } catch (e: IOException) {
        } finally {
            closeStream(`in`)
        }

        return ""
    }

    private fun closeStream(stream: Closeable?) {
        if (stream != null) {
            try {
                stream.close()
            } catch (e: IOException) {
            }

        }

    }

    companion object {

        private val SYSTEM_LINE_SEPARATOR = System.getProperty("line.separator")

        fun newInstance(): AboutFragment {
            val args = Bundle()
            val fragment = AboutFragment()
            fragment.arguments = args
            return fragment
        }
    }
}

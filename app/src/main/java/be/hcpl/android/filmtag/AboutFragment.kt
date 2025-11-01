package be.hcpl.android.filmtag


import android.os.Bundle
import android.text.Html
import android.text.util.Linkify
import android.view.View
import android.widget.TextView
import androidx.fragment.app.Fragment

import java.io.BufferedReader
import java.io.Closeable
import java.io.InputStream
import java.io.InputStreamReader

/**
 * Created by hcpl on 6/08/15.
 */
class AboutFragment : Fragment(R.layout.fragment_about) {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        var aboutText = readFile(resources.openRawResource(R.raw.about))
        // add version
        aboutText = aboutText.replace("{version}", BuildConfig.VERSION_NAME)
        // set text
        val textView = view.findViewById(R.id.text_about) as? TextView
        textView?.apply {
            text = Html.fromHtml(aboutText)
            // and make clickable
            Linkify.addLinks(textView, Linkify.ALL)
        }
    }

    // TODO move to utils instead
    private fun readFile(stream: InputStream): String {
        var input: BufferedReader? = null

        try {
            input = BufferedReader(InputStreamReader(stream))
            val buffer = StringBuilder()

            var e: String? = input.readLine()
            while (e != null) {
                buffer.append(e).append(SYSTEM_LINE_SEPARATOR)
                e = input.readLine()
            }

            return buffer.toString()
        } catch (_: Exception) {
        } finally {
            closeStream(input)
        }

        return ""
    }

    private fun closeStream(stream: Closeable?) {
        if (stream != null) {
            try {
                stream.close()
            } catch (_: Exception) {
            }

        }

    }

    companion object {

        private val SYSTEM_LINE_SEPARATOR = System.lineSeparator()

    }
}

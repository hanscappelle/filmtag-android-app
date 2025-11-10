package be.hcpl.android.filmtag.ui.activity

import android.app.Application
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import be.hcpl.android.filmtag.BuildConfig
import be.hcpl.android.filmtag.R
import be.hcpl.android.filmtag.ui.tranformer.TextTransformer
import java.io.BufferedReader
import java.io.Closeable
import java.io.InputStream
import java.io.InputStreamReader

class AboutViewModel(
    context: Application,
    private val textTransformer: TextTransformer,
) : ViewModel() {

    val state = MutableLiveData<State>()

    val SYSTEM_LINE_SEPARATOR = System.lineSeparator()

    init {
        var aboutText = readFile(context.resources.openRawResource(R.raw.about))
        // add version
        aboutText = aboutText.replace("{version}", BuildConfig.VERSION_NAME)
        // publish
        state.postValue(State(aboutText = aboutText))
    }

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

    data class State(
        val aboutText: String,
    )
}
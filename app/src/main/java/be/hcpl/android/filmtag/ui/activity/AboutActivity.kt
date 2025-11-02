package be.hcpl.android.filmtag.ui.activity


import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Box
import androidx.compose.material3.Text
import be.hcpl.android.filmtag.ui.AppScaffold
import org.koin.androidx.viewmodel.ext.android.viewModel
import androidx.compose.foundation.layout.padding
import androidx.compose.ui.Modifier

class AboutActivity : ComponentActivity() {

    private val viewModel: AboutViewModel by viewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        viewModel.state.observe(this, ::handleState)
    }

    private fun handleState(state: AboutViewModel.State) {
        setContent {
            AppScaffold { innerPadding ->
                Box(
                    modifier = Modifier.padding(innerPadding),
                ) {
                    Text(
                        text = state.aboutText,
                    )
                }
                // TODO restore linkify here
                //textView?.apply {
                //    text = Html.fromHtml(aboutText)
                //    // and make clickable
                //    Linkify.addLinks(textView, Linkify.ALL)
            }
        }
    }


}

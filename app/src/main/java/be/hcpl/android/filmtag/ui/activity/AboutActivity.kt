package be.hcpl.android.filmtag.ui.activity


import android.os.Bundle
import android.text.Html
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Box
import androidx.compose.material3.Text
import be.hcpl.android.filmtag.ui.AppScaffold
import org.koin.androidx.viewmodel.ext.android.viewModel
import androidx.compose.foundation.layout.padding
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.fromHtml
import androidx.compose.ui.unit.dp
import be.hcpl.android.filmtag.R
import be.hcpl.android.filmtag.ui.Action
import be.hcpl.android.filmtag.ui.ActionId

class AboutActivity : ComponentActivity() {

    private val viewModel: AboutViewModel by viewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        viewModel.state.observe(this, ::handleState)
    }

    private fun handleState(state: AboutViewModel.State) {
        setContent {
            AppScaffold(
                actions = listOf(
                    Action(
                        iconRes = R.drawable.ic_action_close,
                        textRes = R.string.action_close,
                        actionId = ActionId.Close,
                    ),
                ),
                handleAction = ::handleAction,
            ) { innerPadding ->
                Box(
                    modifier = Modifier.padding(innerPadding),
                ) {
                    Text(
                        text = AnnotatedString.Companion.fromHtml(
                            state.aboutText,
                        ),
                        modifier = Modifier.padding(16.dp),
                    )
                }
            }
        }
    }


    private fun handleAction(actionId: ActionId){
        when(actionId){
            ActionId.Close -> finish()
            ActionId.Create -> TODO()
            ActionId.Export -> TODO()
            ActionId.Help -> TODO()
            ActionId.Info -> TODO()
        }
    }
}

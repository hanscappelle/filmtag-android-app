package be.hcpl.android.filmtag.ui.activity

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.padding
import androidx.compose.ui.Modifier
import be.hcpl.android.filmtag.R
import be.hcpl.android.filmtag.ui.Action
import be.hcpl.android.filmtag.ui.ActionId
import be.hcpl.android.filmtag.ui.AppScaffold
import be.hcpl.android.filmtag.ui.activity.SettingsViewModel.Event
import be.hcpl.android.filmtag.ui.activity.SettingsViewModel.State
import be.hcpl.android.filmtag.ui.view.EditSettingsView
import org.koin.androidx.viewmodel.ext.android.viewModel
import kotlin.getValue

class SettingsActivity: ComponentActivity() {

    private val viewModel: SettingsViewModel by viewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel.state.observe(this, ::handleState)
        viewModel.events.observe(this, ::handleEvent)
    }

    private fun handleState(state: State) {
        setContent {
            AppScaffold(
                actions = listOf(
                    Action(
                        iconRes = R.drawable.ic_action_check,
                        textRes = R.string.action_create,
                        actionId = ActionId.Create,
                    ),
                    Action(
                        iconRes = R.drawable.ic_action_close,
                        textRes = R.string.action_close,
                        actionId = ActionId.Close,
                    ),
                ),
                handleAction = ::handleAction,
            ) { innerPadding ->
                EditSettingsView(
                    modifier = Modifier.padding(innerPadding),
                    viewState = state.editState,
                )
            }
        }
    }

    private fun handleAction(actionId: ActionId) {
        when (actionId) {
            ActionId.Close -> finish()
            ActionId.Create -> viewModel.saveSettings()
            else -> Unit
        }
    }

    private fun handleEvent(event: Event){
        when(event){
            Event.Close -> finish()
        }
    }

}
package be.hcpl.android.filmtag.ui.activity


import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import be.hcpl.android.filmtag.ui.AppScaffold
import org.koin.androidx.viewmodel.ext.android.viewModel
import androidx.compose.foundation.layout.padding
import androidx.compose.ui.Modifier
import be.hcpl.android.filmtag.R
import be.hcpl.android.filmtag.ui.Action
import be.hcpl.android.filmtag.ui.ActionId
import be.hcpl.android.filmtag.ui.activity.FilmRollActivity.Companion.KEY_FILM_ROLL
import be.hcpl.android.filmtag.ui.view.EditRollView
import be.hcpl.android.filmtag.ui.view.EditRollViewState
import org.koin.core.parameter.parametersOf

class EditRollActivity : ComponentActivity() {

    private val viewModel: EditRollViewModel by viewModel(
        parameters = { parametersOf(intent.getLongExtra(KEY_FILM_ROLL, -1L)) }
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        viewModel.state.observe(this, ::handleState)
    }

    private fun handleState(state: EditRollViewModel.State) {
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
                EditRollView(
                    modifier = Modifier.padding(innerPadding),
                    //roll = state.roll,
                    viewState = EditRollViewState(state.roll),
                )
            }
        }
    }





    private fun handleAction(actionId: ActionId) {
        when (actionId) {
            ActionId.Close -> finish()
            ActionId.Create -> viewModel.saveChanges()
            ActionId.Export -> TODO()
            ActionId.Help -> TODO()
            ActionId.Info -> TODO()
        }
    }
}

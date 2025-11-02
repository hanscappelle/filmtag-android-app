package be.hcpl.android.filmtag.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import be.hcpl.android.filmtag.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppScaffold(
    actions: List<Action> = emptyList(),
    handleAction: (ActionId) -> Unit = {},
    content: @Composable (PaddingValues) -> Unit,
) {
    AppTheme {
        Scaffold(
            topBar = {
                TopAppBar(
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = MaterialTheme.colorScheme.surfaceContainer,
                        titleContentColor = MaterialTheme.colorScheme.onSurface,
                    ),
                    title = {
                        Text(stringResource(R.string.app_name))
                    },
                    actions = {
                        actions.forEach { action ->
                            ActionView(
                                action = action,
                                onClick = { handleAction(action.actionId) },
                            )
                        }
                    },
                )
            }
        ) { innerPadding ->
            content(innerPadding)
        }
    }
}

@Composable
fun ActionView(
    action: Action,
    onClick: () -> Unit,
) {
    Icon(
        painter = painterResource(action.iconRes),
        contentDescription = stringResource(action.textRes),
        modifier = Modifier
            .size(48.dp)
            .padding(8.dp)
            .clickable(onClick = onClick)
    )
}

data class Action(
    val iconRes: Int,
    val textRes: Int,
    val actionId: ActionId,
)

sealed class ActionId {
    object Info : ActionId()
    object Help : ActionId()
    object Create : ActionId()
    object Export : ActionId()
    object Close : ActionId()
    object Delete : ActionId()
    object Update : ActionId()
}
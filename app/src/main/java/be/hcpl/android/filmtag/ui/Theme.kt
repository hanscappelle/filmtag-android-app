package be.hcpl.android.filmtag.ui

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable

@Composable
fun AppTheme(
    //useDarkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable() () -> Unit,
) {

    // forced to dark theme for now
    val colors = darkColorScheme()

    MaterialTheme(
        colorScheme = colors,
        content = content
    )

    // fix system bar colors
    //val view = LocalView.current
    //val window = (view.context as Activity).window
    // here change the status bar element color
    //val isLandscape = LocalConfiguration.current.orientation == Configuration.ORIENTATION_LANDSCAPE
    //if (!isLandscape)
    //    WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = true
}
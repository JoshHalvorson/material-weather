package dev.joshhalvorson.materialweather.ui.components

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import com.google.accompanist.swiperefresh.SwipeRefresh
import com.google.accompanist.swiperefresh.SwipeRefreshIndicator
import com.google.accompanist.swiperefresh.SwipeRefreshState

/**
 * Since the new pullRefresh modifier requires the use of the compose material library and this
 * project uses the material3 library, this just wraps the deprecated accompanist library so we
 * don't need to add a bunch of overlapping material components.
 */
@Composable
fun PullRefresh(
    swipeRefreshState: SwipeRefreshState,
    onRefresh: () -> Unit,
    content: @Composable () -> Unit
) {
    SwipeRefresh(
        state = swipeRefreshState,
        onRefresh = onRefresh,
        content = content,
        indicator = { state, trigger ->
            SwipeRefreshIndicator(
                state = state,
                refreshTriggerDistance = trigger,
                scale = true,
                backgroundColor = MaterialTheme.colorScheme.inversePrimary,
                contentColor = MaterialTheme.colorScheme.onPrimaryContainer
            )
        }
    )
}
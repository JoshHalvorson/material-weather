package dev.joshhalvorson.materialweather.ui.components

import androidx.compose.runtime.Composable
import com.google.accompanist.swiperefresh.SwipeRefreshState

/**
 * Since the new pullRefresh modifier requires the use of the compose material library and this
 * project uses the material3 library, this just wraps the deprecated accompanist library so we
 * don't need to add a bunch of overlapping material components.
 */
@Composable
fun rememberPullRefreshState(isRefreshing: Boolean): SwipeRefreshState {
    return rememberPullRefreshState(isRefreshing, onRefresh =)
}
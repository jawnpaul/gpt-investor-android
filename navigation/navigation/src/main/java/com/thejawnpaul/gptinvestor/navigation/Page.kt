package com.thejawnpaul.gptinvestor.navigation

import android.os.Bundle
import androidx.compose.runtime.Composable

class Page(val route: Route, val content: @Composable (Bundle) -> Unit)

fun page(route: String, content: @Composable PageContext.() -> Unit) =
  Page(route = Route(route), content = { content(PageContext(it)) })

class PageContext(private val bundle: Bundle) {
  fun arg(key: String) = bundle.getString(key)
}

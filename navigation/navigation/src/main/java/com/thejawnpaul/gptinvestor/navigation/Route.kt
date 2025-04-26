package com.thejawnpaul.gptinvestor.navigation

@JvmInline
value class Route(val path: String) {
  init {
    require(path.isNotEmpty()) { "Route path cannot be blank" }
  }
}

val Route.args: List<String>
  get() = path.split("/").filter { it.startsWith("{") && it.endsWith("}") }

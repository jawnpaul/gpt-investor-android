package com.thejawnpaul.gptinvestor.navigation

interface Navigator {
  fun navigate(route: String)

  fun navigateUp()
}

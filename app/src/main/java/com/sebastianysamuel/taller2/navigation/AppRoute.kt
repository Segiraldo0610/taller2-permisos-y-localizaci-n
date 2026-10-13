package com.sebastianysamuel.taller2.navigation

import androidx.navigation3.runtime.NavKey
import kotlinx.serialization.Serializable

@Serializable
data object HomeRoute : NavKey

@Serializable
data object MediaRoute : NavKey

@Serializable
data object MapRoute : NavKey

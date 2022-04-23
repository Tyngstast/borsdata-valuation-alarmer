package com.github.tyngstast.borsdatavaluationalarmer.android.ui.common

import androidx.compose.runtime.Composable
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext

@Composable
@ReadOnlyComposable
fun stringResourceByName(id: String): String {
    LocalConfiguration.current
    val context = LocalContext.current
    val resources = context.resources
    val resourceId = resources.getIdentifier(id, "string", context.packageName)

    if (resourceId == 0) {
        return id
    }

    return resources.getString(resourceId)
}

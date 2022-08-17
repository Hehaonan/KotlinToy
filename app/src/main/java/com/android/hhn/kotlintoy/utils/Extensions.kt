package com.android.hhn.kotlintoy.utils

import android.content.res.Resources

/**
 * UI
 */
val Number.dp2px get() = (toFloat() * Resources.getSystem().displayMetrics.density).toInt()
val Number.px2dp get() = (toFloat() / Resources.getSystem().displayMetrics.density).toInt()
val Number.sp2px get() = (toFloat() * Resources.getSystem().displayMetrics.scaledDensity).toInt()
val Number.dp2p get() = (toFloat() * Resources.getSystem().displayMetrics.density)

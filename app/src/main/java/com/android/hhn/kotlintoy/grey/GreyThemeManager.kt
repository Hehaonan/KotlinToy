package com.android.hhn.kotlintoy.grey

import android.graphics.ColorMatrix
import android.graphics.ColorMatrixColorFilter
import android.graphics.Paint
import android.view.View
import android.view.Window

/**
 * @author hehaonan @ Zhihu Inc.
 * @since 2022-12-20
 */
object GreyThemeManager {

    private val paint by lazy {
        Paint().also {
            it.colorFilter = ColorMatrixColorFilter(
                ColorMatrix().apply {
                    setSaturation(0F)
                }
            )
        }
    }

    private fun render(window: Window) = render(window.decorView)
    private fun render(view: View) = view.setLayerType(View.LAYER_TYPE_HARDWARE, paint)

    @JvmStatic
    fun useGrayTheme(view: View) {
        render(view)
    }

    @JvmStatic
    fun useGrayTheme(window: Window) {
        render(window)
    }

}
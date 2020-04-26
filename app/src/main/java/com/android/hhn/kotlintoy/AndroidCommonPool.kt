package com.android.hhn.kotlintoy

import android.os.AsyncTask
import kotlinx.coroutines.CoroutineDispatcher
import kotlin.coroutines.CoroutineContext

/**
 * Author: haonan.he ;<p/>
 * Date: 2020-02-12,12:18 ;<p/>
 * Description: ;<p/>
 * Other: ;
 */

/**
 * Created by ZhangTao on 18/7/11.
 */
object AndroidCommonPool : CoroutineDispatcher() {
    override fun dispatch(context: CoroutineContext, block: Runnable) {
        AsyncTask.THREAD_POOL_EXECUTOR.execute(block)
    }
}

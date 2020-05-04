package com.android.hhn.kotlintoy

import android.animation.Animator
import android.animation.ObjectAnimator
import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.animation.doOnCancel
import androidx.core.animation.doOnEnd
import androidx.core.animation.doOnStart
import androidx.core.content.edit
import androidx.core.text.isDigitsOnly
import androidx.core.util.lruCache
import androidx.core.view.forEach
import com.android.hhn.kotlintoy.coroutines.CommonUsage
import com.android.hhn.kotlintoy.file.TestJava.main
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.coroutines.*
import okhttp3.OkHttpClient
import okhttp3.Request
import org.w3c.dom.Text
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.coroutineContext

class MainActivity : AppCompatActivity() {

    private val TAG: String = "MainActivity"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        main_start_request_btn.setOnClickListener {
            GlobalScope.launch(Dispatchers.Main) {
                main_content_tv.text = "loading..."
                // delay(500)
                main_content_tv.text = getData()
            }
        }
        main_content_tv.setOnClickListener {
            clearData(it as TextView)
        }

        //testCoroutine()

        // testKTX()

    }

    private val mOkHttpClient = OkHttpClient()
    private val mRequest = Request.Builder().url("https://www.baidu.com").get().build()

    private suspend fun getData(): String? {
        val job = GlobalScope.async(AndroidCommonPool) {
            mOkHttpClient.newCall(mRequest).execute().body()?.string()
        }
        return job.await() // null 也可以
    }

    fun clearData(textView: TextView) {
        textView.text = "clear data"
    }

//    private fun testCoroutine() {
//        Log.d(TAG, "主线程id：${Thread.currentThread().id}")
//        runBlocking {
//            delay(1000)
//            Log.d(TAG, "协程执行结束 -- 线程id：${Thread.currentThread().id}")
//        }
//        Log.d(TAG, "主线程执行结束")
//
//        Log.d(TAG, "主线程id：${Thread.currentThread().id}")
//        val job = GlobalScope.launch(Dispatchers.Default, CoroutineStart.LAZY) {
//            delay(2000)
//            Log.d(TAG, "协程执行结束 -- 线程id：${Thread.currentThread().id}")
//        }.start()
//        Log.d(TAG, "主线程执行结束")
//    }
//
//    fun testKTX() {
//        getSharedPreferences("Space", Context.MODE_PRIVATE).edit(true) {
//            putInt("key-int", 5)
//            putString("key-str", "test")
//        }
//        main_ll.forEach {
//            if (it is TextView) {
//                it.text = "from KTX"
//            }
//        }
//        println("2342".isDigitsOnly())
//
////        // Animator
////        val animator = ObjectAnimator.ofFloat(main_content_tv, "alpha", 1.0f, 0.2f)
////        // 常规
////        animator.addListener(object : Animator.AnimatorListener {
////            override fun onAnimationRepeat(animation: Animator?) {
////            }
////
////            override fun onAnimationEnd(animation: Animator?) {
////            }
////
////            override fun onAnimationCancel(animation: Animator?) {
////            }
////
////            override fun onAnimationStart(animation: Animator?) {
////            }
////        })
////        // KTX
////        animator.doOnStart { }
////        animator.doOnEnd { }
////        animator.doOnCancel { }
//
////        // 常规
////        handler.postDelayed({
////            // runnable.run()
////        }, 1000L)
////        // KTX
////        handler.postDelayed(1000L) {
////            // runnable.run()
////        }
//    }

}

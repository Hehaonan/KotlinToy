package com.android.hhn.kotlintoy

import android.animation.Animator
import android.animation.ObjectAnimator
import android.content.Context
import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.animation.doOnCancel
import androidx.core.animation.doOnEnd
import androidx.core.animation.doOnStart
import androidx.core.content.edit
import androidx.core.text.isDigitsOnly
import androidx.core.util.lruCache
import androidx.core.view.forEach
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import okhttp3.OkHttpClient
import okhttp3.Request

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        main_content_tv.text = "loading..."
        getData(main_content_tv)

        getSharedPreferences("Space", Context.MODE_PRIVATE).edit(true) {
            putInt("key-int", 5)
            putString("key-str", "test")
        }
        main_ll.forEach {
            if (it is TextView) {
                it.text = "from KTX"
            }
        }
        println("2342".isDigitsOnly())

        // Animator
        val animator = ObjectAnimator.ofFloat(main_content_tv, "alpha", 1.0f, 0.2f)
        // 常规
        animator.addListener(object : Animator.AnimatorListener {
            override fun onAnimationRepeat(animation: Animator?) {
            }

            override fun onAnimationEnd(animation: Animator?) {
            }

            override fun onAnimationCancel(animation: Animator?) {
            }

            override fun onAnimationStart(animation: Animator?) {
            }
        })
        // KTX
        animator.doOnStart { }
        animator.doOnEnd { }
        animator.doOnCancel { }

//        // 常规
//        handler.postDelayed({
//            // runnable.run()
//        }, 1000L)
//        // KTX
//        handler.postDelayed(1000L) {
//            // runnable.run()
//        }


    }

    private val mOkHttpClient = OkHttpClient()
    private val mRequest = Request.Builder().url("https://www.baidu.com").get().build()


    fun getData(textView: TextView) = runBlocking {
        launch {
            val job = async(AndroidCommonPool) {
                mOkHttpClient.newCall(mRequest).execute().body()?.string()
            }
            textView.text = job.await()
        }
    }

    fun contentClick(textView: View) {
        //main_content_tv.text = "clear"
    }


}

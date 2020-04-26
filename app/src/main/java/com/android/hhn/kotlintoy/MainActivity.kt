package com.android.hhn.kotlintoy

import android.content.Context
import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.edit
import androidx.core.text.isDigitsOnly
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

        //val lrcCache = lruCache(10)

//        main_ll.forEach {
//            if (it is TextView) {
//                it.text = "from RTX"
//            }
//        }

        println("2342".isDigitsOnly())
        // val name = getSystemService(this.localClassName)
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

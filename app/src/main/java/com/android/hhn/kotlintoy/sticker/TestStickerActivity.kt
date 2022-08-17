package com.android.hhn.kotlintoy.sticker

import android.app.AlertDialog
import android.graphics.*
import android.graphics.Bitmap.CompressFormat
import android.graphics.drawable.GradientDrawable
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.android.hhn.kotlintoy.R
import com.android.hhn.kotlintoy.sticker.view.ColorPickerView
import com.android.hhn.kotlintoy.sticker.view.ColorPickerView.OnColorChangedListener
import com.android.hhn.kotlintoy.sticker.view.StickerItem
import com.android.hhn.kotlintoy.sticker.view.StickerView
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.io.OutputStream

class TestStickerActivity : AppCompatActivity(), View.OnClickListener {
    private var mBackView: ImageView? = null//背景图
    private var mStickerView: StickerView? = null//添加贴画的图层
    private var mTextDialog: AlertDialog? = null //输入文本dialog
    private var mDialogInput: EditText? = null//输入文本的ed
    private var mTextColor = Color.BLACK //最终的text的颜色
    var defaultHeight = 0
    var defaultWidth = 0 // 屏幕宽高
    private var mDrawImage: View? = null//最终保存图片区域

    private var mTestTv: TextView? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.kt_activity_sticker)
        val currentDisplay = windowManager.defaultDisplay
        defaultHeight = currentDisplay.height
        defaultWidth = currentDisplay.width
        initView()
    }

    private fun initView() {
        mDrawImage = findViewById(R.id.create_iamge)
        mTestTv = findViewById(R.id.test_text)
        mStickerView = findViewById(R.id.id_stickerew)
        mBackView = findViewById(R.id.im_back)
        mBackView?.setOnClickListener {
            Toast.makeText(
                this, "sticker_bg", Toast.LENGTH_SHORT
            ).show()
        }
        findViewById<View>(R.id.add_image).setOnClickListener(this)
        findViewById<View>(R.id.add_text).setOnClickListener(this)
        findViewById<View>(R.id.btn_save).setOnClickListener(this)
        setImage()
    }

    override fun onClick(v: View) {
        when (v.id) {
            R.id.add_image -> {
                val imageBitmap = BitmapFactory.decodeResource(resources, R.mipmap.kt_sticker_item)
                mStickerView!!.addBitImage(imageBitmap, StickerItem.STICKER_TYPE_IMAGE)
            }
            R.id.add_text -> {
                createTextDialog()
            }
            R.id.btn_save -> {
                mStickerView!!.prepareSave()
                mDrawImage!!.isDrawingCacheEnabled = true
                mDrawImage!!.buildDrawingCache()
                var bm = mDrawImage!!.drawingCache
                bm = Bitmap.createBitmap(bm, 0, 0, bm.width, bm.height)
                mDrawImage!!.destroyDrawingCache()
                try {
                    val uri = saveBitmap2File(bm, ".jpg")
                    Toast.makeText(
                        this@TestStickerActivity, "保存成功，路径" + uri.path, Toast.LENGTH_LONG
                    ).show()
                } catch (e: IOException) {
                    e.printStackTrace()
                }
            }
        }
    }

    private val changeLine = true

    //输入文本的dialog
    private fun createTextDialog() {
        val builder = AlertDialog.Builder(this)
        val view = LayoutInflater.from(this)
            .inflate(R.layout.kt_sticker_dialog_input, null) as LinearLayout
        mDialogInput = view.findViewById<View>(R.id.ed_dialog_input) as EditText
        mDialogInput!!.setText("文字贴纸、👌🏻、文字贴纸")
        val colorPickerView = ColorPickerView(this, defaultWidth / 2, defaultWidth / 2)
        colorPickerView.listener = colorChangedListener
        view.addView(colorPickerView)
        mTextDialog = builder.setView(view).setTitle("请输入内容").setPositiveButton(
            "确定"
        ) { dialog, which ->
            val text = mDialogInput!!.text.toString()
            val finalBm: Bitmap
            if (changeLine) {
                mTestTv!!.isDrawingCacheEnabled = true
                mTestTv!!.buildDrawingCache()
                val temp = mTestTv!!.drawingCache
                finalBm = Bitmap.createBitmap(temp, 0, 0, temp.width, temp.height)
                mTestTv!!.destroyDrawingCache()
            } else {
                val dm = resources.displayMetrics
                val density = dm.density // 屏幕密度
                finalBm = Bitmap.createBitmap(
                    (text.length * density * 35).toInt(),
                    (40 * density).toInt(), Bitmap.Config.ARGB_8888
                )
                val canvas = Canvas(finalBm)
                val paint = Paint()
                paint.color = mTextColor
                paint.textSize = 35 * density
                canvas.drawText(text, 0f, 35 * density, paint)
            }
            mStickerView!!.addBitImage(finalBm, StickerItem.STICKER_TYPE_NORMAL_TEXT)
        }.setNegativeButton("取消", null).create()
        mTextDialog?.show()
    }

    var colorChangedListener: OnColorChangedListener = object : OnColorChangedListener {
        override fun colorChanged(color: Int) {
            mTextColor = color
            mDialogInput!!.setTextColor(color)
            // 修改颜色
            val gd = mTestTv!!.background as GradientDrawable
            gd.setColor(color)
            // 修改文字
            val text = mDialogInput!!.text.toString()
            mTestTv!!.text = text
            mTestTv!!.gravity = Gravity.RIGHT
        }
    }

    //加载图片
    fun setImage() {
        var bitmap: Bitmap?
        //由于返回的图像可能太大而无法完全加载到内存中。系统有限制，需要处理。
        val bitmapFactoryOptions = BitmapFactory.Options()
        bitmapFactoryOptions.inJustDecodeBounds = true ///只是为获取原始图片的尺寸，而不返回Bitmap对象
        try {
            bitmap =
                BitmapFactory.decodeResource(resources, R.drawable.sticker_bg, bitmapFactoryOptions)
            val outHeight = bitmapFactoryOptions.outHeight
            val outWidth = bitmapFactoryOptions.outWidth
            val heightRatio =
                Math.ceil((outHeight.toFloat() / defaultHeight).toDouble()).toInt()
            val widthRatio =
                Math.ceil((outWidth.toFloat() / defaultWidth).toDouble()).toInt()
            if (heightRatio > 1 || widthRatio > 1) {
                if (heightRatio > widthRatio) {
                    bitmapFactoryOptions.inSampleSize = heightRatio
                } else {
                    bitmapFactoryOptions.inSampleSize = widthRatio
                }
            }
            bitmapFactoryOptions.inJustDecodeBounds = false
            bitmap =
                BitmapFactory.decodeResource(resources, R.drawable.sticker_bg, bitmapFactoryOptions)
            mBackView!!.setImageBitmap(bitmap)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    //保存图片
    @Throws(IOException::class)
    fun saveBitmap2File(bmp: Bitmap, mimeType: String): Uri {
        var format = ".jpg"
        var compressFormat = CompressFormat.JPEG
        if (mimeType.contains("png")) {
            format = ".png"
            compressFormat = CompressFormat.PNG
        }
        val dir =
            Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM).absolutePath +
                    File.separator
        val fileName = dir + System.currentTimeMillis() + format
        val stream: OutputStream = FileOutputStream(fileName)
        bmp.compress(compressFormat, 100, stream)
        stream.flush()
        stream.close()
        return Uri.fromFile(File(fileName))
    }
}
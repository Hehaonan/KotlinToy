package com.android.hhn.kotlintoy.sticker.view

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.*
import android.view.MotionEvent
import android.view.View

@SuppressLint("ViewConstructor")
class ColorPickerView(context: Context?, height: Int, width: Int) :
    View(context) {
    private val mPaint //渐变色环画笔
            : Paint
    private val mCenterPaint //中间圆画笔
            : Paint
    private val mLinePaint //分隔线画笔
            : Paint
    private val mRectPaint //渐变方块画笔
            : Paint
    private var rectShader //渐变方块渐变图像
            : Shader? = null
    private val rectLeft //渐变方块左x坐标
            : Float
    private val rectTop //渐变方块右x坐标
            : Float
    private val rectRight //渐变方块上y坐标
            : Float
    private val rectBottom: Float//渐变方块下y坐标
    private val mCircleColors //渐变色环颜色
            : IntArray
    private val mRectColors //渐变方块颜色
            : IntArray
    private val mHeight //View高
            : Int
    private val mWidth //View宽
            : Int
    private val r //色环半径(paint中部)
            : Float
    private val centerRadius //中心圆半径
            : Float
    private var downInCircle = true //按在渐变环上
    private var downInRect //按在渐变方块上
            = false
    private var highlightCenter //高亮
            = false
    private var highlightCenterLittle //微亮
            = false
    private val mInitialColor = Color.BLACK //初始颜色
    var listener: OnColorChangedListener? = null

    override fun onDraw(canvas: Canvas) {
        //移动中心
        canvas.translate((mWidth / 2).toFloat(), (mHeight / 2 - 50).toFloat())
        //画中心圆
        canvas.drawCircle(0f, 0f, centerRadius, mCenterPaint)
        //是否显示中心圆外的小圆环
        if (highlightCenter || highlightCenterLittle) {
            val c = mCenterPaint.color
            mCenterPaint.style = Paint.Style.STROKE
            if (highlightCenter) {
                mCenterPaint.alpha = 0xFF
            } else if (highlightCenterLittle) {
                mCenterPaint.alpha = 0x90
            }
            canvas.drawCircle(
                0f, 0f,
                centerRadius + mCenterPaint.strokeWidth, mCenterPaint
            )
            mCenterPaint.style = Paint.Style.FILL
            mCenterPaint.color = c
        }
        //画色环
        canvas.drawOval(RectF(-r, -r, r, r), mPaint)
        //画黑白渐变块
        if (downInCircle) {
            mRectColors[1] = mCenterPaint.color
        }
        rectShader =
            LinearGradient(rectLeft, 0f, rectRight, 0f, mRectColors, null, Shader.TileMode.MIRROR)
        mRectPaint.shader = rectShader
        canvas.drawRect(rectLeft, rectTop, rectRight, rectBottom, mRectPaint)
        val offset = mLinePaint.strokeWidth / 2
        canvas.drawLine(
            rectLeft - offset, rectTop - offset * 2,
            rectLeft - offset, rectBottom + offset * 2, mLinePaint
        ) //左
        canvas.drawLine(
            rectLeft - offset * 2, rectTop - offset,
            rectRight + offset * 2, rectTop - offset, mLinePaint
        ) //上
        canvas.drawLine(
            rectRight + offset, rectTop - offset * 2,
            rectRight + offset, rectBottom + offset * 2, mLinePaint
        ) //右
        canvas.drawLine(
            rectLeft - offset * 2, rectBottom + offset,
            rectRight + offset * 2, rectBottom + offset, mLinePaint
        ) //下
        super.onDraw(canvas)
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        val x = event.x - mWidth / 2
        val y = event.y - mHeight / 2 + 50
        val inCircle = inColorCircle(
            x, y,
            r + mPaint.strokeWidth / 2, r - mPaint.strokeWidth / 2
        )
        val inCenter = inCenter(x, y, centerRadius)
        val inRect = inRect(x, y)
        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                downInCircle = inCircle
                downInRect = inRect
                highlightCenter = inCenter
                if (downInCircle && inCircle) { //down按在渐变色环内, 且move也在渐变色环内
                    val angle = Math.atan2(y.toDouble(), x.toDouble()).toFloat()
                    var unit = (angle / (2 * Math.PI)).toFloat()
                    if (unit < 0) {
                        unit += 1f
                    }
                    mCenterPaint.color = interpCircleColor(mCircleColors, unit)
                } else if (downInRect && inRect) { //down在渐变方块内, 且move也在渐变方块内
                    mCenterPaint.color = interpRectColor(mRectColors, x)
                }
                if (highlightCenter && inCenter || highlightCenterLittle && inCenter) { //点击中心圆, 当前移动在中心圆
                    highlightCenter = true
                    highlightCenterLittle = false
                } else if (highlightCenter || highlightCenterLittle) { //点击在中心圆, 当前移出中心圆
                    highlightCenter = false
                    highlightCenterLittle = true
                } else {
                    highlightCenter = false
                    highlightCenterLittle = false
                }
                invalidate()
            }
            MotionEvent.ACTION_MOVE -> {
                if (downInCircle && inCircle) {
                    val angle = Math.atan2(y.toDouble(), x.toDouble()).toFloat()
                    var unit = (angle / (2 * Math.PI)).toFloat()
                    if (unit < 0) {
                        unit += 1f
                    }
                    mCenterPaint.color = interpCircleColor(mCircleColors, unit)
                } else if (downInRect && inRect) {
                    mCenterPaint.color = interpRectColor(mRectColors, x)
                }
                if (highlightCenter && inCenter || highlightCenterLittle && inCenter) {
                    highlightCenter = true
                    highlightCenterLittle = false
                } else if (highlightCenter || highlightCenterLittle) {
                    highlightCenter = false
                    highlightCenterLittle = true
                } else {
                    highlightCenter = false
                    highlightCenterLittle = false
                }
                invalidate()
            }
            MotionEvent.ACTION_UP -> {
                if (highlightCenter && inCenter) { //点击在中心圆, 且当前启动在中心圆
                    if (listener != null) {
                        listener!!.colorChanged(mCenterPaint.color)
                        //  TODO 去掉dismiss
                    }
                }
                if (downInCircle) {
                    downInCircle = false
                }
                if (downInRect) {
                    downInRect = false
                }
                if (highlightCenter) {
                    highlightCenter = false
                }
                if (highlightCenterLittle) {
                    highlightCenterLittle = false
                }
                invalidate()
            }
        }
        return true
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(mWidth, mHeight)
    }

    /**
     * 坐标是否在色环上
     *
     * @param x         坐标
     * @param y         坐标
     * @param outRadius 色环外半径
     * @param inRadius  色环内半径
     * @return
     */
    private fun inColorCircle(x: Float, y: Float, outRadius: Float, inRadius: Float): Boolean {
        val outCircle = Math.PI * outRadius * outRadius
        val inCircle = Math.PI * inRadius * inRadius
        val fingerCircle = Math.PI * (x * x + y * y)
        return if (fingerCircle < outCircle && fingerCircle > inCircle) {
            true
        } else {
            false
        }
    }

    /**
     * 坐标是否在中心圆上
     *
     * @param x            坐标
     * @param y            坐标
     * @param centerRadius 圆半径
     * @return
     */
    private fun inCenter(x: Float, y: Float, centerRadius: Float): Boolean {
        val centerCircle = Math.PI * centerRadius * centerRadius
        val fingerCircle = Math.PI * (x * x + y * y)
        return if (fingerCircle < centerCircle) {
            true
        } else {
            false
        }
    }

    /**
     * 坐标是否在渐变色中
     */
    private fun inRect(x: Float, y: Float): Boolean {
        return if (x <= rectRight && x >= rectLeft && y <= rectBottom && y >= rectTop) {
            true
        } else {
            false
        }
    }

    /**
     * 获取圆环上颜色
     */
    private fun interpCircleColor(colors: IntArray, unit: Float): Int {
        if (unit <= 0) {
            return colors[0]
        }
        if (unit >= 1) {
            return colors[colors.size - 1]
        }
        var p = unit * (colors.size - 1)
        val i = p.toInt()
        p -= i.toFloat()

        // now p is just the fractional part [0...1) and i is the index
        val c0 = colors[i]
        val c1 = colors[i + 1]
        val a = ave(Color.alpha(c0), Color.alpha(c1), p)
        val r = ave(Color.red(c0), Color.red(c1), p)
        val g = ave(Color.green(c0), Color.green(c1), p)
        val b = ave(Color.blue(c0), Color.blue(c1), p)
        return Color.argb(a, r, g, b)
    }

    /**
     * 获取渐变块上颜色
     */
    private fun interpRectColor(colors: IntArray, x: Float): Int {
        val a: Int
        val r: Int
        val g: Int
        val b: Int
        val c0: Int
        val c1: Int
        val p: Float
        if (x < 0) {
            c0 = colors[0]
            c1 = colors[1]
            p = (x + rectRight) / rectRight
        } else {
            c0 = colors[1]
            c1 = colors[2]
            p = x / rectRight
        }
        a = ave(Color.alpha(c0), Color.alpha(c1), p)
        r = ave(Color.red(c0), Color.red(c1), p)
        g = ave(Color.green(c0), Color.green(c1), p)
        b = ave(Color.blue(c0), Color.blue(c1), p)
        return Color.argb(a, r, g, b)
    }

    private fun ave(s: Int, d: Int, p: Float): Int {
        return s + Math.round(p * (d - s))
    }

    /**
     * 回调接口
     */
    interface OnColorChangedListener {
        fun colorChanged(color: Int)
    }

    init {
        mHeight = height - 36
        mWidth = width
        minimumHeight = height - 36
        minimumWidth = width

        //渐变色环参数
        mCircleColors = intArrayOf(
            -0x10000, -0xff01, -0xffff01,
            -0xff0001, -0xff0100, -0x100, -0x10000
        )
        val s: Shader = SweepGradient(0f, 0f, mCircleColors, null)
        mPaint = Paint(Paint.ANTI_ALIAS_FLAG)
        mPaint.shader = s
        mPaint.style = Paint.Style.STROKE
        mPaint.strokeWidth = 50f
        r = width / 2 * 0.7f - mPaint.strokeWidth * 0.5f

        //中心圆参数
        mCenterPaint = Paint(Paint.ANTI_ALIAS_FLAG)
        mCenterPaint.color = mInitialColor
        mCenterPaint.strokeWidth = 5f
        centerRadius = (r - mPaint.strokeWidth / 2) * 0.7f

        //边框参数
        mLinePaint = Paint(Paint.ANTI_ALIAS_FLAG)
        mLinePaint.color = Color.parseColor("#72A1D1")
        mLinePaint.strokeWidth = 4f

        //黑白渐变参数
        mRectColors = intArrayOf(-0x1000000, mCenterPaint.color, -0x1)
        mRectPaint = Paint(Paint.ANTI_ALIAS_FLAG)
        mRectPaint.strokeWidth = 5f
        rectLeft = -r - mPaint.strokeWidth * 0.5f
        rectTop = r + mPaint.strokeWidth * 0.5f + mLinePaint.strokeMiter * 0.5f + 15
        rectRight = r + mPaint.strokeWidth * 0.5f
        rectBottom = rectTop + 50
    }
}
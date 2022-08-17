package com.android.hhn.kotlintoy.sticker.view

import android.content.Context
import android.graphics.*
import android.view.View
import com.android.hhn.kotlintoy.R
import com.android.hhn.kotlintoy.utils.dp2px

/**
 * @author hehaonan @ Zhihu Inc.
 * @since 2022-08-01
 */
class StickerItem(
    context: Context, //当前Id
    val itemId: Int, //当前type
    private val mStickerType: Int
) {
    private var HELP_BOX_PADDING = 8 // 工具框内边距 dp值
    private var BUTTON_WIDTH = 18 // 工具按钮宽度 dp值
    private var STROKE_WIDTH = 1 // 工具框边框粗细 dp值
    private var STROKE_RADIUS = 4 // 工具框边框圆角 dp值
    private var DASH_HEIGHT = 5 // 工具框虚线框长度 dp值
    private var DASH_INTERVAL = 3 // 工具框虚线框间隔 dp值
    private var BORDER_PROTECTION = 40 // 边框保护区 dp值
    private var mBitmap // 要绘制的图
            : Bitmap? = null
    private var mBorderRectF // 边框区域
            : RectF? = null
    private var srcRect // 原始图片坐标
            : RectF? = null
    var dstRect // 绘制目标坐标
            : RectF? = null
    var helpBox //工具框的坐标区域
            : RectF? = null
    private var helpToolsRect // 单个工具按钮区域
            : RectF? = null
    private var mDeleteRect // 删除按钮位置
            : RectF? = null
    private var mRotateRect // 旋转按钮位置
            : RectF? = null
    private var mEditRect // 编辑按钮位置
            : RectF? = null
    var detectDeleteRect //检测删除按钮旋转范围
            : RectF? = null
    var detectRotateRect //检测旋转按钮旋转范围
            : RectF? = null
    var detectEditRect //检测编辑按钮旋转范围
            : RectF? = null
    var matrix // 变化矩阵
            : Matrix? = null
    private var mRotateAngle = 0f
    var isShowHelpTools = false // 是否绘制工具框
    private val dstPaint: Paint
    private val mHelpBoxPaint = Paint()
    private var mInitWidth // 加入屏幕时原始宽度
            = 0f
    private val greenPaint // debug画笔
            : Paint

    init {
        // 设置样式属性
        HELP_BOX_PADDING = HELP_BOX_PADDING.dp2px
        BUTTON_WIDTH = BUTTON_WIDTH.dp2px / 2 // 绘制只需要一半的距离
        STROKE_WIDTH = STROKE_WIDTH.dp2px
        STROKE_RADIUS = STROKE_RADIUS.dp2px
        DASH_HEIGHT = DASH_HEIGHT.dp2px
        DASH_INTERVAL = DASH_INTERVAL.dp2px
        BORDER_PROTECTION = BORDER_PROTECTION.dp2px
        mHelpBoxPaint.color = Color.WHITE
        mHelpBoxPaint.style = Paint.Style.STROKE
        mHelpBoxPaint.isAntiAlias = true
        mHelpBoxPaint.strokeWidth = STROKE_WIDTH.toFloat()
        mHelpBoxPaint.pathEffect =
            DashPathEffect(floatArrayOf(DASH_HEIGHT.toFloat(), DASH_INTERVAL.toFloat()), 0f)
        dstPaint = Paint()
        dstPaint.color = Color.RED
        dstPaint.alpha = 120
        greenPaint = Paint()
        greenPaint.color = Color.GREEN
        greenPaint.alpha = 120
        if (mDeleteBm == null) {
            mDeleteBm =
                BitmapFactory.decodeResource(context.resources, R.mipmap.kt_sticker_item_delete)
        }
        if (mRotateBm == null) {
            mRotateBm =
                BitmapFactory.decodeResource(context.resources, R.mipmap.kt_sticker_item_roate)
        }
        if (mEditBm == null) {
            mEditBm = BitmapFactory.decodeResource(context.resources, R.mipmap.kt_sticker_item_edit)
        }
    }

    fun draw(canvas: Canvas) {
        canvas.drawBitmap(mBitmap!!, matrix!!, null) // 贴图元素绘制
        if (mDebug) {
            canvas.drawRect(dstRect!!, dstPaint)
        }
        if (isShowHelpTools) { // 绘制辅助工具线
            canvas.save()
            canvas.rotate(mRotateAngle, helpBox!!.centerX(), helpBox!!.centerY())
            canvas.drawRoundRect(
                helpBox!!,
                STROKE_RADIUS.toFloat(),
                STROKE_RADIUS.toFloat(),
                mHelpBoxPaint
            ) //draw
            val helpRect = Rect()
            helpToolsRect!!.roundOut(helpRect)
            // 绘制工具按钮
            canvas.drawBitmap(
                mDeleteBm!!, helpRect,
                mDeleteRect!!, null
            )
            canvas.drawBitmap(
                mRotateBm!!, helpRect,
                mRotateRect!!, null
            )
            if (isShowEditTool) {
                canvas.drawBitmap(
                    mEditBm!!, helpRect,
                    mEditRect!!, null
                )
            }
            canvas.restore()
            if (mDebug) {
                canvas.drawRect(mDeleteRect!!, dstPaint)
                canvas.drawRect(mRotateRect!!, dstPaint)
                canvas.drawRect(mEditRect!!, dstPaint)
                canvas.drawRect(detectRotateRect!!, greenPaint)
                canvas.drawRect(detectDeleteRect!!, greenPaint)
                canvas.drawRect(detectEditRect!!, greenPaint)
            }
        }
    }

    /**
     * @return 文字编辑按钮是否显示，不显示不响应点击事件
     */
    val isShowEditTool: Boolean
        get() = mStickerType != STICKER_TYPE_IMAGE

    fun init(addBit: Bitmap, parentView: View) {
        mBitmap = addBit
        srcRect = RectF(0f, 0f, addBit.width.toFloat(), addBit.height.toFloat())
        mBorderRectF = RectF(
            0f, 0f, parentView.width.toFloat(),
            parentView.height.toFloat()
        )
        val bitWidth = Math.min(addBit.width, parentView.width shr 1)
        val bitHeight = bitWidth * addBit.height / addBit.width
        val left = (parentView.width shr 1) - (bitWidth shr 1)
        val top = (parentView.height shr 1) - (bitHeight shr 1)
        dstRect = RectF(
            left.toFloat(), top.toFloat(), (left + bitWidth).toFloat(),
            (top + bitHeight).toFloat()
        )
        matrix = Matrix()
        matrix!!.postTranslate(dstRect!!.left, dstRect!!.top)
        matrix!!.postScale(
            bitWidth.toFloat() / addBit.width,
            bitHeight.toFloat() / addBit.height, dstRect!!.left,
            dstRect!!.top
        )
        mInitWidth = dstRect!!.width() // 记录原始宽度
        // item.matrix.setScale((float)bitWidth/addBit.getWidth(),(float)bitHeight/addBit.getHeight());
        isShowHelpTools = true
        helpBox = RectF(dstRect)
        updateHelpBoxRect()
        helpToolsRect = RectF(
            0f, 0f,
            mDeleteBm!!.width.toFloat(),
            mDeleteBm!!.height.toFloat()
        )
        mDeleteRect = RectF(
            helpBox!!.right - BUTTON_WIDTH, helpBox!!.top - BUTTON_WIDTH,
            helpBox!!.right + BUTTON_WIDTH, helpBox!!.top + BUTTON_WIDTH
        )
        mRotateRect = RectF(
            helpBox!!.right - BUTTON_WIDTH, helpBox!!.bottom - BUTTON_WIDTH,
            helpBox!!.right + BUTTON_WIDTH, helpBox!!.bottom + BUTTON_WIDTH
        )
        mEditRect = RectF(
            helpBox!!.left - BUTTON_WIDTH, helpBox!!.top - BUTTON_WIDTH,
            helpBox!!.left + BUTTON_WIDTH, helpBox!!.top + BUTTON_WIDTH
        )
        detectRotateRect = RectF(mRotateRect)
        detectDeleteRect = RectF(mDeleteRect)
        detectEditRect = RectF(mEditRect)
    }

    //绘制边框大小
    private fun updateHelpBoxRect() {
        helpBox!!.left -= HELP_BOX_PADDING.toFloat()
        helpBox!!.right += HELP_BOX_PADDING.toFloat()
        helpBox!!.top -= HELP_BOX_PADDING.toFloat()
        helpBox!!.bottom += HELP_BOX_PADDING.toFloat()
    }

    private val mTempRect0 = RectF()

    // r0 是否包含 r1
    private fun diyContains(r0: RectF?, r1: RectF?): Boolean {
        mTempRect0.set(r0!!)
        val w = Math.abs(r1!!.width() - BORDER_PROTECTION)
        val h = Math.abs(r1.height() - BORDER_PROTECTION)
        mTempRect0.inset(-w, -h)
        return mTempRect0.contains(r1)
    }

    /**
     * 位置更新
     *
     * @param dx
     * @param dy
     */
    fun updatePos(dx: Float, dy: Float) {
        dstRect!!.offset(dx, dy)
        // 处理边界保护
        if (!diyContains(mBorderRectF, dstRect)) {
            dstRect!!.offset(-dx, -dy)
            return
        }
        matrix!!.postTranslate(dx, dy) // 记录到矩阵中

        // 工具按钮随之移动
        helpBox!!.offset(dx, dy)
        mDeleteRect!!.offset(dx, dy)
        mRotateRect!!.offset(dx, dy)
        mEditRect!!.offset(dx, dy)
        detectRotateRect!!.offset(dx, dy)
        detectDeleteRect!!.offset(dx, dy)
        detectEditRect!!.offset(dx, dy)
    }

    /**
     * 旋转 缩放 更新
     *
     * @param dx
     * @param dy
     */
    fun updateRotateAndScale(
        oldx: Float, oldy: Float,
        dx: Float, dy: Float, isNeedScale: Boolean
    ) {
        val c_x = dstRect!!.centerX()
        val c_y = dstRect!!.centerY()
        val x = detectRotateRect!!.centerX()
        val y = detectRotateRect!!.centerY()

        // float x = oldx;
        // float y = oldy;
        val n_x = x + dx
        val n_y = y + dy
        val xa = x - c_x
        val ya = y - c_y
        val xb = n_x - c_x
        val yb = n_y - c_y
        val srcLen = Math.sqrt((xa * xa + ya * ya).toDouble()).toFloat()
        val curLen = Math.sqrt((xb * xb + yb * yb).toDouble()).toFloat()

        // System.out.println("srcLen--->" + srcLen + "   curLen---->" + curLen);
        val scale = curLen / srcLen // 计算缩放比
        val newWidth = dstRect!!.width() * scale
        val newScale = newWidth / mInitWidth
        if (newScale < MIN_SCALE || newScale > MAX_SCALE) { //缩放值检测
            return
        }
        if (isNeedScale) {
            matrix!!.postScale(scale, scale, dstRect!!.centerX(), dstRect!!.centerY()) // 存入scale矩阵
            // this.matrix.postRotate(5, this.dstRect.centerX(),
            // this.dstRect.centerY());
            scaleRect(dstRect, scale) // 缩放目标矩形
        }

        // 重新计算工具箱坐标
        helpBox!!.set(dstRect!!)
        updateHelpBoxRect() // 重新计算
        mRotateRect!!.offsetTo(helpBox!!.right - BUTTON_WIDTH, helpBox!!.bottom - BUTTON_WIDTH)
        mDeleteRect!!.offsetTo(helpBox!!.right - BUTTON_WIDTH, helpBox!!.top - BUTTON_WIDTH)
        mEditRect!!.offsetTo(helpBox!!.left - BUTTON_WIDTH, helpBox!!.top - BUTTON_WIDTH)
        detectRotateRect!!.offsetTo(helpBox!!.right - BUTTON_WIDTH, helpBox!!.bottom - BUTTON_WIDTH)
        detectDeleteRect!!.offsetTo(helpBox!!.right - BUTTON_WIDTH, helpBox!!.top - BUTTON_WIDTH)
        detectEditRect!!.offsetTo(helpBox!!.left - BUTTON_WIDTH, helpBox!!.top - BUTTON_WIDTH)
        val cos = ((xa * xb + ya * yb) / (srcLen * curLen)).toDouble()
        if (cos > 1 || cos < -1) return
        var angle = Math.toDegrees(Math.acos(cos)).toFloat()

        // 拉普拉斯定理
        val calMatrix = xa * yb - xb * ya // 行列式计算 确定转动方向
        val flag = if (calMatrix > 0) 1 else -1
        angle = flag * angle
        mRotateAngle += angle
        matrix!!.postRotate(angle, dstRect!!.centerX(), dstRect!!.centerY())
        rotateRect(detectRotateRect, dstRect!!.centerX(), dstRect!!.centerY(), mRotateAngle)
        rotateRect(detectDeleteRect, dstRect!!.centerX(), dstRect!!.centerY(), mRotateAngle)
        rotateRect(detectEditRect, dstRect!!.centerX(), dstRect!!.centerY(), mRotateAngle)
    }

    companion object {
        const val STICKER_TYPE_NORMAL_TEXT = 1
        const val STICKER_TYPE_ART_TEXT = 2
        const val STICKER_TYPE_IMAGE = 3
        private const val MIN_SCALE = 0.5f // 最小缩放比例
        private const val MAX_SCALE = 5f // 最大缩放比例
        private var mDeleteBm: Bitmap? = null
        private var mRotateBm: Bitmap? = null
        private var mEditBm: Bitmap? = null
        private const val mDebug = false // debug模式

        /**
         * 缩放指定矩形
         *
         * @param rect
         * @param scale
         */
        private fun scaleRect(rect: RectF?, scale: Float) {
            val w = rect!!.width()
            val h = rect.height()
            val newW = scale * w
            val newH = scale * h
            val dx = (newW - w) / 2
            val dy = (newH - h) / 2
            rect.left -= dx
            rect.top -= dy
            rect.right += dx
            rect.bottom += dy
        }

        /**
         * 矩形绕指定点旋转
         *
         * @param rect        按钮区域
         * @param rotateAngle 旋转角度
         */
        private fun rotateRect(rect: RectF?, centerX: Float, centerY: Float, rotateAngle: Float) {
            val x = rect!!.centerX()
            val y = rect.centerY()
            val sinA =
                Math.sin(Math.toRadians(rotateAngle.toDouble())).toFloat()
            val cosA =
                Math.cos(Math.toRadians(rotateAngle.toDouble())).toFloat()
            val newX = centerX + (x - centerX) * cosA - (y - centerY) * sinA
            val newY = centerY + (y - centerY) * cosA + (x - centerX) * sinA
            val dx = newX - x
            val dy = newY - y
            rect.offset(dx, dy)

            // float w = rect.width();
            // float h = rect.height();
            // rect.left = newX;
            // rect.top = newY;
            // rect.right = newX + w;
            // rect.bottom = newY + h;
        }
    }
}
package com.android.hhn.kotlintoy.sticker.view
import android.content.Context
import android.graphics.*
import android.util.Log
import com.android.hhn.kotlintoy.R
import com.android.hhn.kotlintoy.utils.dp2px
import kotlin.math.*

class StickerItem(
    context: Context, val itemId: Int, val stickerType: Int
) {
    companion object {
        const val STICKER_TYPE_NORMAL_TEXT = 1 // 普通文字
        const val STICKER_TYPE_ART_TEXT = 2 // 花字
        const val STICKER_TYPE_IMAGE = 3 // 图片
        const val MIN_SCALE = 0.5f // 最小缩放比例
        const val MAX_SCALE = 5.5f // 最大缩放比例
        const val STICKER_IMAGE_WIDTH = 118 // 图片贴纸的显示宽度 dp值
    }

    //====== 可配置样式 =====
    private var mHelpBoxPadding = 8.dp2px // 工具框内边距 dp值
    private var mButtonWidth = 18.dp2px // 工具按钮宽度 dp值
    private var mButtonMargin = 3.dp2px // 工具按钮宽度外边距 dp值
    private var mStrokeWidth = 1.dp2px // 工具框边框粗细 dp值
    private var mStrokeRadius = 4.dp2px // 工具框边框圆角 dp值
    private var mDashHeight = 5.dp2px // 工具框虚线框长度 dp值
    private var mDashInterval = 3.dp2px // 工具框虚线框间隔 dp值
    private var mBorderProtection = 40.dp2px // 边框保护区 dp值

    //====== 私有属性 =====
    private var mContentBitmap: Bitmap? = null// 内容区Bitmap
    private lateinit var mParentView: StickerView // 父容器
    private lateinit var mMatrix: Matrix// 变化矩阵
    private val mHelpBoxPaint = Paint() // 绘制工具栏画笔

    private var mHelpToolsRect: RectF? = null // 单个工具按钮区域
    private var mDeleteBm: Bitmap? = null // 删除按钮
    private var mRotateBm: Bitmap? = null // 旋转按钮
    private var mEditBm: Bitmap? = null // 编辑按钮
    private var mDeleteRect: RectF? = null // 删除按钮位置
    private var mRotateRect: RectF? = null // 旋转按钮位置
    private var mEditRect: RectF? = null // 编辑按钮位置

    private var mInitWidth = 0f // 加入屏幕时原始宽度
    private var mInitCenterX = 0f // 初始化中心点
    private var mInitCenterY = 0f // 初始化中心点
    private var mFinalCenterDx = 0f //最终移动多少x
    private var mFinalCenterDy = 0f //最终移动多少y

    //====== debug模式 =====
    private val mDebug = false // debug模式
    private lateinit var mRedPaint: Paint  // debug:内容区域
    private lateinit var mGreenPaint: Paint // debug:工具按钮区域

    //====== 对外暴露 =====
    private var helpBox: RectF = RectF() //工具框的坐标区域
    var contentRect: RectF? = null // 绘制内容区域坐标
        private set
    var detectDeleteRect: RectF? = null //检测删除按钮的点击区域
        private set
    var detectRotateRect: RectF? = null //检测旋转按钮的点击区域
        private set
    var detectEditRect: RectF? = null //检测编辑按钮旋的点击区域
        private set
    var rotateAngle = 0f // 旋转角度
        private set
    var finalScale = 1f // 最终缩放倍数 [MIN_SCALE,MAX_SCALE]之间取值
        private set
    var isShowHelpTools = false // 是否绘制工具框
    var isVisible = true // 当前item是否显示

    @JvmField
    var model: Any? = null// item携带的业务数据

    init {
        mButtonWidth /= 2 // 按钮绘制只需要一半的距离
        // 设置工具栏样式
        mHelpBoxPaint.color = Color.WHITE
        mHelpBoxPaint.style = Paint.Style.STROKE
        mHelpBoxPaint.isAntiAlias = true
        mHelpBoxPaint.strokeWidth = mStrokeWidth.toFloat()
        mHelpBoxPaint.pathEffect = DashPathEffect( //虚线样式
            floatArrayOf(mDashHeight.toFloat(), mDashInterval.toFloat()), 0f
        )
        mDeleteBm = BitmapFactory.decodeResource(
            context.resources, R.mipmap.kt_sticker_item_delete
        )
        mRotateBm = BitmapFactory.decodeResource(
            context.resources, R.mipmap.kt_sticker_item_roate
        )
        mEditBm = BitmapFactory.decodeResource(
            context.resources, R.mipmap.kt_sticker_item_edit
        )
        if (mDebug) {
            mRedPaint = Paint().apply {
                this.color = Color.RED
                this.alpha = 120
            }
            mGreenPaint = Paint().apply {
                this.color = Color.GREEN
                this.alpha = 120
            }
        }
    }

    fun init(bitmap: Bitmap, parentView: StickerView) {
        this.mParentView = parentView
        mContentBitmap = bitmap
        mMatrix = Matrix()
        val bitWidth = min(bitmap.width, parentView.width shr 1)
        val bitHeight = bitWidth * bitmap.height / bitmap.width
        val left = (parentView.width shr 1) - (bitWidth shr 1)
        val top = (parentView.height shr 1) - (bitHeight shr 1)
        contentRect = RectF(
            left.toFloat(), top.toFloat(), (left + bitWidth).toFloat(), (top + bitHeight).toFloat()
        )
        mMatrix.postTranslate(contentRect!!.left, contentRect!!.top)
        mMatrix.postScale(
            bitWidth.toFloat() / bitmap.width,
            bitHeight.toFloat() / bitmap.height, contentRect!!.left, contentRect!!.top
        )
        mInitWidth = contentRect!!.width() // 记录原始宽度
        mInitCenterX = contentRect!!.centerX()
        mInitCenterY = contentRect!!.centerY()
        isShowHelpTools = true
        updateHelpBoxRect(contentRect!!)
        // 初始化工具按钮的位置
        mHelpToolsRect = RectF(
            0f, 0f, mDeleteBm!!.width.toFloat(), mDeleteBm!!.height.toFloat()
        )
        mDeleteRect = RectF(
            helpBox.right - mButtonWidth, helpBox.top - mButtonWidth,
            helpBox.right + mButtonWidth, helpBox.top + mButtonWidth
        )
        mRotateRect = RectF(
            helpBox.right - mButtonWidth, helpBox.bottom - mButtonWidth,
            helpBox.right + mButtonWidth, helpBox.bottom + mButtonWidth
        )
        mEditRect = RectF(
            helpBox.left - mButtonWidth, helpBox.top - mButtonWidth,
            helpBox.left + mButtonWidth, helpBox.top + mButtonWidth
        )
        detectRotateRect = RectF(mRotateRect)
        detectDeleteRect = RectF(mDeleteRect)
        detectEditRect = RectF(mEditRect)
        // 扩大点击区域
        detectRotateRect!!.inset(-mButtonMargin.toFloat(), -mButtonMargin.toFloat())
        detectDeleteRect!!.inset(-mButtonMargin.toFloat(), -mButtonMargin.toFloat())
        detectEditRect!!.inset(-mButtonMargin.toFloat(), -mButtonMargin.toFloat())
    }

    fun draw(canvas: Canvas) {
        canvas.drawBitmap(mContentBitmap!!, mMatrix, null) // 贴图元素绘制
        if (mDebug) {
            canvas.drawRect(contentRect!!, mRedPaint)
        }
        if (isShowHelpTools) { // 绘制辅助工具线
            canvas.save()
            canvas.rotate(rotateAngle, helpBox.centerX(), helpBox.centerY())
            canvas.drawRoundRect(
                helpBox,
                mStrokeRadius.toFloat(),
                mStrokeRadius.toFloat(),
                mHelpBoxPaint
            ) //draw
            val helpRect = Rect()
            mHelpToolsRect!!.roundOut(helpRect)
            // 绘制工具按钮
            canvas.drawBitmap(mDeleteBm!!, helpRect, mDeleteRect!!, null)
            canvas.drawBitmap(mRotateBm!!, helpRect, mRotateRect!!, null)
            if (isShowEditTool) {
                canvas.drawBitmap(mEditBm!!, helpRect, mEditRect!!, null)
            }
            canvas.restore()
            if (mDebug) {
                canvas.drawRect(mDeleteRect!!, mRedPaint)
                canvas.drawRect(mRotateRect!!, mRedPaint)
                canvas.drawRect(mEditRect!!, mRedPaint)
                canvas.drawRect(detectRotateRect!!, mGreenPaint)
                canvas.drawRect(detectDeleteRect!!, mGreenPaint)
                canvas.drawRect(detectEditRect!!, mGreenPaint)
            }
        }
    }

    /**
     * @return 文字编辑按钮是否显示，不显示不响应点击事件
     */
    val isShowEditTool: Boolean
        get() = stickerType != STICKER_TYPE_IMAGE

    fun updateItem() { // 用上次记录的值 重新做放大 旋转 位移
        mMatrix.postScale(finalScale, finalScale, contentRect!!.centerX(), contentRect!!.centerY())
        scaleRect(contentRect!!, finalScale) // 缩放内容Rect
        updateAllRectByScale() // 缩放其他按钮

        updateRotateInternal(rotateAngle, rotateAngle)// 更新旋转角度

        val dx = mFinalCenterDx
        val dy = mFinalCenterDy
        contentRect!!.offset(dx, dy)
        updatePositionInternal(dx, dy)// 更新位置
    }

    //外部控制缩放大小,目前由TextStickerPanel.SeekBar控制
    fun updateScale(deltaScale: Float) {
        var deltaS = deltaScale
        val old = finalScale
        val newWidth = contentRect!!.width() * deltaS
        finalScale = newWidth / mInitWidth
        if (old == finalScale) { // 不需要再更新
            return
        }
        if (finalScale < MIN_SCALE) { //缩放值检测
            deltaS = MIN_SCALE / old
            finalScale = MIN_SCALE
        } else if (finalScale > MAX_SCALE) {
            deltaS = MAX_SCALE / old
            finalScale = MAX_SCALE
        }

        if (!updateScaleInternal(deltaS)) { // 更新缩放比例
            return // 不能缩放直接return
        }
        updateAllRectByScale() // 缩放其他按钮

        updateAllRectByRotate(rotateAngle) // 更新旋转角度
    }

    private val mTempRect0 = RectF()

    // r0 是否包含 r1
    private fun diyContains(r0: RectF, r1: RectF): Boolean {
        mTempRect0.set(r0)
        val w = abs(r1.width() - mBorderProtection)
        val h = abs(r1.height() - mBorderProtection)
        mTempRect0.inset(-w, -h)
        return mTempRect0.contains(r1)
    }

    // 从StickerView中获取边框
    private val borderRectF: RectF
        get() {
            if (mParentView.borderRectF.isEmpty) {
                return RectF(0f, 0f, mParentView.width.toFloat(), mParentView.height.toFloat())
            }
            return mParentView.borderRectF
        }

    /**
     * 位置更新
     *
     * @param dx 增量x
     * @param dy 增量y
     */
    fun updatePosition(dx: Float, dy: Float) {
        contentRect!!.offset(dx, dy)
        // 处理边界保护
        if (!diyContains(borderRectF, contentRect!!)) {
            contentRect!!.offset(-dx, -dy)
            return
        }
        updatePositionInternal(dx, dy)
        // 记录下中心点的差值
        mFinalCenterDx = helpBox.centerX() - mInitCenterX
        mFinalCenterDy = helpBox.centerY() - mInitCenterY
    }

    /**
     * 同时更新旋转、缩放
     *
     * @param dx
     * @param dy
     */
    fun updateRotateAndScale(dx: Float, dy: Float, isNeedScale: Boolean, isNeedRotate: Boolean) {
        val cX = contentRect!!.centerX()
        val cY = contentRect!!.centerY()
        val x = detectRotateRect!!.centerX()
        val y = detectRotateRect!!.centerY()

        val nX = x + dx
        val nY = y + dy
        val xa = x - cX
        val ya = y - cY
        val xb = nX - cX
        val yb = nY - cY
        val srcLen = sqrt((xa * xa + ya * ya).toDouble()).toFloat()
        val curLen = sqrt((xb * xb + yb * yb).toDouble()).toFloat()
        val scale = curLen / srcLen // 计算缩放比
        val newWidth = contentRect!!.width() * scale
        finalScale = newWidth / mInitWidth
        if (finalScale < MIN_SCALE || finalScale > MAX_SCALE) { //缩放值检测
            return
        }
        if (isNeedScale) {
            val isCanScale = updateScaleInternal(scale)
        }
        updateAllRectByScale() // 缩放其他按钮

        var angle = 0f
        if (isNeedRotate) {
            val cos = ((xa * xb + ya * yb) / (srcLen * curLen)).toDouble()
            if (cos > 1 || cos < -1) return
            angle = Math.toDegrees(acos(cos)).toFloat()
            // 拉普拉斯定理
            val calMatrix = xa * yb - xb * ya // 行列式计算 确定转动方向
            val flag = if (calMatrix > 0) 1 else -1
            angle *= flag
        }
        rotateAngle += angle
        updateRotateInternal(angle, rotateAngle)
    }

    private fun updateRotateInternal(deltaAngle: Float, finalAngle: Float) {
        mMatrix.postRotate(deltaAngle, contentRect!!.centerX(), contentRect!!.centerY())
        updateAllRectByRotate(finalAngle)
    }

    private fun updateAllRectByRotate(finalAngle: Float) {
        rotateRect(detectRotateRect, contentRect!!.centerX(), contentRect!!.centerY(), finalAngle)
        rotateRect(detectDeleteRect, contentRect!!.centerX(), contentRect!!.centerY(), finalAngle)
        rotateRect(detectEditRect, contentRect!!.centerX(), contentRect!!.centerY(), finalAngle)
    }

    private fun updatePositionInternal(dx: Float, dy: Float) {
        mMatrix.postTranslate(dx, dy) // 记录到矩阵中
        helpBox.offset(dx, dy) // 移动其他工具按钮
        mDeleteRect!!.offset(dx, dy)
        mRotateRect!!.offset(dx, dy)
        mEditRect!!.offset(dx, dy)
        detectRotateRect!!.offset(dx, dy)
        detectDeleteRect!!.offset(dx, dy)
        detectEditRect!!.offset(dx, dy)
    }

    // 只缩放内容Rect return true:缩放不会超出编辑
    private fun updateScaleInternal(deltaScale: Float): Boolean {
        val tempM = Matrix()
        val tempR = RectF(contentRect)
        tempM.postScale(deltaScale, deltaScale, tempR.centerX(), tempR.centerY())
        scaleRect(tempR, deltaScale)
        if (!diyContains(borderRectF, tempR)) { // 用临时Rect判断下缩放是否会超出边界
            return false
        }
        // 不会缩放出边界再用真实的Rect去缩放
        mMatrix.postScale(
            deltaScale, deltaScale, contentRect!!.centerX(), contentRect!!.centerY()
        ) // 存入scale矩阵
        scaleRect(contentRect!!, deltaScale) // 缩放内容Rect
        return true
    }

    private fun updateAllRectByScale() { // 根据内容Rect重新计算其他功能Rect
        updateHelpBoxRect(contentRect!!) // 重新计算工具栏Rect
        updateAllButtonRect()// 重新计算所有按钮Rect
    }

    private fun updateHelpBoxRect(contentRectF: RectF) { //更新工具栏大小
        helpBox.set(contentRectF)
        helpBox.left -= mHelpBoxPadding.toFloat()
        helpBox.right += mHelpBoxPadding.toFloat()
        helpBox.top -= mHelpBoxPadding.toFloat()
        helpBox.bottom += mHelpBoxPadding.toFloat()
    }

    private fun updateAllButtonRect() { // 更新所有按钮的位置
        mRotateRect!!.offsetTo(helpBox.right - mButtonWidth, helpBox.bottom - mButtonWidth)
        mDeleteRect!!.offsetTo(helpBox.right - mButtonWidth, helpBox.top - mButtonWidth)
        mEditRect!!.offsetTo(helpBox.left - mButtonWidth, helpBox.top - mButtonWidth)
        detectRotateRect = RectF(mRotateRect)
        detectDeleteRect = RectF(mDeleteRect)
        detectEditRect = RectF(mEditRect)
        // 扩大点击区域
        detectRotateRect!!.inset(-mButtonMargin.toFloat(), -mButtonMargin.toFloat())
        detectDeleteRect!!.inset(-mButtonMargin.toFloat(), -mButtonMargin.toFloat())
        detectEditRect!!.inset(-mButtonMargin.toFloat(), -mButtonMargin.toFloat())
    }

    /**
     * 缩放指定矩形
     *
     * @param rect
     * @param scale
     */
    private fun scaleRect(rect: RectF, scale: Float) {
        val w = rect.width()
        val h = rect.height()
        val newW = scale * w
        val newH = scale * h
        val dx = (newW - w) / 2
        val dy = (newH - h) / 2
        rect.inset(-dx, -dy)
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
        val sinA = sin(Math.toRadians(rotateAngle.toDouble())).toFloat()
        val cosA = cos(Math.toRadians(rotateAngle.toDouble())).toFloat()
        val newX = centerX + (x - centerX) * cosA - (y - centerY) * sinA
        val newY = centerY + (y - centerY) * cosA + (x - centerX) * sinA
        val dx = newX - x
        val dy = newY - y
        rect.offset(dx, dy)
    }

    fun clickHelpBoxRect(x: Float, y: Float): Boolean {
        if (helpBox.isEmpty) {
            return false
        }
        val isClickHelpBoxRect: Boolean
        if (rotateAngle != 0f) {
            // 旋转后判断点是否在四边形内
            val tempM = Matrix()
            tempM.setRotate(rotateAngle, helpBox.centerX(), helpBox.centerY())
            val curPoint = PointF(x, y)
            val pointLT = getMapPoints(tempM, helpBox.left, helpBox.top)
            val pointRT = getMapPoints(tempM, helpBox.right, helpBox.top)
            val pointLB = getMapPoints(tempM, helpBox.left, helpBox.bottom)
            val pointRB = getMapPoints(tempM, helpBox.right, helpBox.bottom)
            isClickHelpBoxRect = pointInRect(curPoint, pointLT, pointRT, pointLB, pointRB)
        } else {
            isClickHelpBoxRect = helpBox.contains(x, y)
        }
        return isClickHelpBoxRect
    }

    /**
     * 获取旋转后的坐标点
     *
     * @param matrix 转换矩阵
     * @param x      左坐标
     * @param y      右坐标
     */
    private fun getMapPoints(matrix: Matrix, x: Float, y: Float): PointF {
        val floats = floatArrayOf(x, y)
        matrix.mapPoints(floats)
        return PointF(floats[0], floats[1])
    }

    /**
     * 点是否落在四边形内
     *
     * @param curPoint 点击位置
     * @param pointLT  左上顶点
     * @param pointRT  右上顶点
     * @param pointLB  左下顶点
     * @param pointRB  右下顶点
     * @return true 在四边形内；false 不在四边形内
     */
    private fun pointInRect(
        curPoint: PointF,
        pointLT: PointF, pointRT: PointF, pointLB: PointF, pointRB: PointF
    ): Boolean {
        val nCount = 4
        val rectPoints = arrayOf(pointLT, pointLB, pointRB, pointRT)
        var nCross = 0
        for (i in 0 until nCount) {
            //依次取相邻的两个点
            val pStart = rectPoints[i]
            val pEnd = rectPoints[(i + 1) % nCount]

            //相邻的两个点是平行于x轴的，肯定不相交，忽略
            if (pStart.y == pEnd.y) continue

            //交点在pStart,pEnd的延长线上，pCur肯定不会与pStart.pEnd相交，忽略
            if (curPoint.y < min(pStart.y, pEnd.y) || curPoint.y > max(pStart.y, pEnd.y)
            ) continue

            //求当前点和x轴的平行线与pStart,pEnd直线的交点的x坐标
            val x = (curPoint.y - pStart.y).toDouble() * (pEnd.x - pStart.x).toDouble() /
                    (pEnd.y - pStart.y).toDouble() + pStart.x

            //若x坐标大于当前点的坐标，则有交点
            if (x > curPoint.x) nCross++
        }

        // 单边交点为偶数，点在多边形之外
        return nCross % 2 == 1
    }

    data class LocationInfo(
        val left: Float,// 左边距，内容区域left-边框区域left，可以是负数，单位px
        val top: Float,// 上边距，内容区域top-边框区域top，可以是负数，单位px
        val width: Float,// 宽度，单位px
        val height: Float// 高度，单位px
    )

    // 获取item位置信息
    fun getLocationInfo(): LocationInfo? {
        if (contentRect == null) return null
        val left = contentRect!!.left - borderRectF.left
        val top = contentRect!!.top - borderRectF.top
        return LocationInfo(left, top, contentRect!!.width(), contentRect!!.height())
    }

    override fun toString(): String {
        return "StickerItem{" +
                " itemId=" + itemId + ", helpBox=" + helpBox.toString() + ", mRotateAngle=" + rotateAngle +
                ", finalScale=" + finalScale + ", stickerType=" + stickerType + ", isVisible=" + isVisible +
                '}'
    }
}
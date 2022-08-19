package com.android.hhn.kotlintoy.sticker.view

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.RectF
import android.util.AttributeSet
import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.widget.Toast
import kotlin.math.abs

class StickerView(context: Context, attrs: AttributeSet?) : View(context, attrs) {
    companion object {
        private const val TAG = "StickerView"
        private const val STATUS_IDLE = 0
        private const val STATUS_MOVE = 1 // 移动状态
        private const val STATUS_DELETE = 2 // 删除状态
        private const val STATUS_ROTATE = 3 // 图片旋转状态
        private const val STATUS_EDIT = 4 // 图片旋转状态
    }

    private var mItemCount = 0 // 已加入Item的数量
    private var mCurrentStatus = 0 // 当前状态
    private var mCurrentItem: StickerItem? = null // 当前操作的贴图数据
    private var oldX = 0f
    private var oldY = 0f
    private val itemBank: LinkedHashMap<Int, StickerItem> = LinkedHashMap() // 存贮每层贴图数据
    var borderRectF: RectF = RectF() // 贴纸图层的边框区域
        private set // 外部使用 setStickerBorderRectF() 设置边框区域

    init {
        init()
    }

    private fun init() {
        mCurrentStatus = STATUS_IDLE
    }

    fun addStickerItem(addBit: Bitmap?, stickerType: Int) {
        if (addBit == null) {
            Log.d(TAG, "addBit is null")
            return
        }
        val id = ++mItemCount
        val item = StickerItem(context, id, stickerType)
        item.init(addBit, this)
        if (mCurrentItem != null) {
            mCurrentItem!!.isShowHelpTools = false
        }
        mCurrentItem = item // 赋值为当前的item
        itemBank[id] = item
        this.invalidate() // 重绘视图
    }

    fun deleteTextSticker(itemId: Int) {
        if (itemId < 1) {
            Log.d(TAG, "deleteTextSticker itemId 不存在")
        }
        val dItem = itemBank.remove(itemId)
        setAllStickerVisible(true)
        invalidate()
        if (dItem == null) {
            Log.d(TAG, "deleteTextSticker item 不存在")
        }
    }

    fun addTextStickerItem(addBit: Bitmap?, stickerType: Int, model: StickerItem?): Int {
        if (addBit == null || model == null) {
            Log.d(TAG, "addStickerItem bitmap is null || model is null")
            return -1
        }
        var item = itemBank[model.itemId]
        val id: Int
        if (item == null) {
            id = ++mItemCount
            item = StickerItem(this.context, id, stickerType)
            item.init(addBit, this)
        } else {
            id = model.itemId
            item.init(addBit, this)
            item.updateItem()
            item.isShowHelpTools = true
        }
        item.model = model
        mCurrentItem = item // 赋值为当前的item
        itemBank[id] = item
        hiddenStickerById(id)
        this.invalidate() // 重绘视图
        return id
    }

    private fun hiddenStickerById(itemId: Int) {
        itemBank.values.map {
            it.isVisible = it.itemId == itemId
        }
    }

    fun setAllStickerVisible(isShow: Boolean) {
        itemBank.values.map {
            it.isVisible = isShow
            it.isShowHelpTools = false
        }
        invalidate()
    }

    fun updateScaleById(itemId: Int, deltaScale: Float) {
        if (itemId < 1) {
            Log.d(TAG, "updateScaleById itemId 不存在")
        }
        val item = itemBank[itemId]
        if (item != null) {
            item.updateScale(deltaScale)
        } else {
            Log.d(TAG, "updateScaleById item 不存在")
        }
        invalidate()
    }

    val isEmpty: Boolean
        get() {
            Log.d(TAG, "hasItem size=" + itemBank.size)
            return itemBank.isEmpty()
        }

    fun setStickerBorderRectF(rectF: RectF) {
        if (rectF.isEmpty) return
        val oldDiff = abs((borderRectF.top - rectF.top).toInt())
        Log.d(TAG, "setBorderRectF 移动item oldDiff=$oldDiff")
        borderRectF.set(rectF)
        for (item in itemBank.values) {
            if (!borderRectF.contains(item.contentRect!!) && oldDiff > 0) {
                Log.d(TAG, "setBorderRectF 移动item=" + item.itemId)
                if (item.contentRect!!.top < borderRectF.top) {
                    item.updatePos(0f, oldDiff.toFloat())
                    Log.d(TAG, "setBorderRectF 向下移动item")
                } else if (item.contentRect!!.bottom > borderRectF.bottom) {
                    Log.d(TAG, "setBorderRectF 向下移动item")
                    item.updatePos(0f, -oldDiff.toFloat())
                }
            }
        }
        invalidate()
    }

    /**
     * 绘制客户页面
     */
    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        itemBank.values.filter { it.isVisible }.map {
            it.draw(canvas)
        }
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        var isHandle = super.onTouchEvent(event) // 是否向下传递事件标志 true为消耗
        val action = event.action
        val x = event.x
        val y = event.y
        when (action and MotionEvent.ACTION_MASK) {
            MotionEvent.ACTION_DOWN -> {
                var selectedItem: StickerItem? = null // 临时选中的item
                var clickToolItem: StickerItem? = null // 点击工具按钮的item
                for (item in itemBank.values) {
                    if (!item.isVisible) continue
                    if (item.detectDeleteRect!!.contains(x, y)) { // 点击了删除
                        isHandle = true
                        mCurrentStatus = STATUS_DELETE
                        selectedItem = null //重叠item不影响选中逻辑
                        clickToolItem = item
                    } else if (item.detectRotateRect!!.contains(x, y)) { // 点击了旋转按钮
                        isHandle = true
                        oldX = x
                        oldY = y
                        mCurrentStatus = STATUS_ROTATE
                        selectedItem = null //重叠item不影响选中逻辑
                        clickToolItem = item
                    } else if (item.detectEditRect!!.contains(x, y)) { // 点击了编辑
                        if (!item.isShowEditTool) { // 非文字编辑、工具栏隐藏，不能点击
                            break
                        }
                        isHandle = true
                        selectedItem = null
                        clickToolItem = item
                        mCurrentStatus = STATUS_EDIT
                    } else if (item.clickHelpBoxRect(x, y)) { // 点击了内容区域
                        isHandle = true
                        selectedItem = item // 点击会重叠，先标记一下，没有后续删除等操作，才认为是选中逻辑
                        clickToolItem = null
                    }
                }
                // 没有贴图被选择、不是编辑文字状态
                if (!isHandle && mCurrentStatus == STATUS_IDLE) {
                    if (mCurrentItem != null) {
                        mCurrentItem!!.isShowHelpTools = false
                        mCurrentItem = null
                        invalidate()
                    }
                    handleInterceptScrollCallback(false)
                    handleToolsStatusCallback(mCurrentStatus, null)
                }
                if (selectedItem != null) { // 被选中一张贴图，设置为移动模式
                    isHandle = true
                    oldX = x
                    oldY = y
                    mCurrentStatus = STATUS_MOVE
                    handleInterceptScrollCallback(true)
                    changeSelectedItem(selectedItem)
                    invalidate() //点中当前item，显示出工具栏
                }
                clickToolItem?.let { showSelectedOrHandleClick(it, mCurrentStatus) }
            }
            MotionEvent.ACTION_MOVE -> {
                isHandle = true
                if (mCurrentStatus == STATUS_MOVE) { // 移动贴图
                    val dx = x - oldX
                    val dy = y - oldY
                    if (mCurrentItem != null) {
                        mCurrentItem!!.updatePos(dx, dy)
                        invalidate()
                    }
                    oldX = x
                    oldY = y
                } else if (mCurrentStatus == STATUS_ROTATE) { // 旋转 缩放图片操作
                    val dx = x - oldX
                    val dy = y - oldY
                    if (mCurrentItem != null) {
                        mCurrentItem!!.updateRotateAndScale(
                            dx, dy, isNeedScale = true, isNeedRotate = true
                        ) // 旋转
                        invalidate()
                    }
                    oldX = x
                    oldY = y
                }
            }
            MotionEvent.ACTION_CANCEL, MotionEvent.ACTION_UP -> {
                isHandle = false
                mCurrentStatus = STATUS_IDLE
            }
        }
        return isHandle
    }

    // 点击工具按钮时，先显示 或者 直接响应点击事件
    private fun showSelectedOrHandleClick(clickItem: StickerItem?, currentStatus: Int) {
        if (clickItem == null) return
        if (mCurrentItem == null || mCurrentItem!!.itemId != clickItem.itemId) { // 未选中、选中的不是当前item
            changeSelectedItem(clickItem) //切换当前item为选中状态
            handleInterceptScrollCallback(true)
        } else {
            when (currentStatus) {
                STATUS_DELETE -> {
                    itemBank.remove(clickItem.itemId)
                    mCurrentStatus = STATUS_IDLE // 返回空闲状态
                    handleInterceptScrollCallback(false)
                }
                STATUS_EDIT -> {
                    mCurrentStatus = STATUS_IDLE // 返回空闲状态
                    Toast.makeText(context, "edit", Toast.LENGTH_SHORT).show()
                }
                STATUS_ROTATE -> {}
                else -> {}
            }
            handleToolsStatusCallback(currentStatus, clickItem)
        }
        invalidate()
    }

    // 当前的item(可能为空)切换为选中的item
    private fun changeSelectedItem(selected: StickerItem) {
        if (mCurrentItem != null) { //之前的item，隐藏工具栏
            mCurrentItem!!.isShowHelpTools = false
        }
        mCurrentItem = selected
        mCurrentItem!!.isShowHelpTools = true
    }

    fun clear() {
        itemBank.clear()
        this.invalidate()
    }

    //准备保存保存，去掉辅助框
    fun prepareSave() {
        itemBank.values.map {
            it.isShowHelpTools = false
        }
        invalidate()
    }

    private var mInterceptor: InterceptScrollEventCallback? = null
    fun setInterceptScrollEventCallback(callback: InterceptScrollEventCallback?) {
        mInterceptor = callback
    }

    private fun handleInterceptScrollCallback(isNeedIntercept: Boolean) {
        if (mInterceptor != null) {
            mInterceptor!!.requestInterceptScrollEvent(isNeedIntercept)
        }
    }

    /**
     * 请求外部拦截滚动事件回调
     */
    interface InterceptScrollEventCallback {
        /**
         * 请求外部拦截滚动事件
         *
         * @param isNeedIntercept true:需要外部拦截滑动事件，保证StickerView可以正常滑动
         */
        fun requestInterceptScrollEvent(isNeedIntercept: Boolean)
    }

    // 工具栏状态回调
    interface ToolsStatusCallback {
        fun onEditClick(item: StickerItem?)
        fun onDeleteClick(item: StickerItem?)
        fun onUnSelected()
    }

    private var mToolsStatusCallback: ToolsStatusCallback? = null
    fun setToolsStatusCallback(callback: ToolsStatusCallback?) {
        mToolsStatusCallback = callback
    }

    private fun handleToolsStatusCallback(status: Int, item: StickerItem?) {
        if (mToolsStatusCallback != null) {
            when (status) {
                STATUS_DELETE -> mToolsStatusCallback!!.onDeleteClick(item)
                STATUS_EDIT -> mToolsStatusCallback!!.onEditClick(item)
                STATUS_IDLE -> mToolsStatusCallback!!.onUnSelected()
                else -> {}
            }
        }
    }
}
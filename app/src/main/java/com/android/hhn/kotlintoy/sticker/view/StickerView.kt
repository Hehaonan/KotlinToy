package com.android.hhn.kotlintoy.sticker.view

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import android.widget.Toast

/**
 * @author hehaonan @ Zhihu Inc.
 * @since 2022-08-01
 */
class StickerView : View {
    private var mContext: Context? = null
    private var mItemCount // 已加入Item的数量
            = 0
    private var mCurrentStatus // 当前状态
            = 0
    private var mCurrentItem // 当前操作的贴图数据
            : StickerItem? = null
    private var oldX = 0f
    private var oldY = 0f
    private val itemBank: LinkedHashMap<Int, StickerItem> = LinkedHashMap() // 存贮每层贴图数据

    companion object {
        private const val STATUS_IDLE = 0
        private const val STATUS_MOVE = 1 // 移动状态
        private const val STATUS_DELETE = 2 // 删除状态
        private const val STATUS_ROTATE = 3 // 图片旋转状态
        private const val STATUS_EDIT = 4 // 图片旋转状态
    }

    constructor(context: Context) : super(context) {
        init(context)
    }

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
        init(context)
    }

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    ) {
        init(context)
    }

    private fun init(context: Context) {
        mContext = context
        mCurrentStatus = STATUS_IDLE
    }

    fun addBitImage(addBit: Bitmap, stickerType: Int) {
        val id = ++mItemCount
        val item = StickerItem(this.context, id, stickerType)
        item.init(addBit, this)
        if (mCurrentItem != null) {
            mCurrentItem!!.isShowHelpTools = false
        }
        mCurrentItem = item // 赋值为当前的item
        itemBank[id] = item
        this.invalidate() // 重绘视图
    }

    /**
     * 绘制客户页面
     */
    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        for (id in itemBank.keys) {
            val item = itemBank[id]
            item!!.draw(canvas)
        }
    }

    override fun onSizeChanged(w: Int, h: Int, oldW: Int, oldH: Int) {
        super.onSizeChanged(w, h, oldW, oldH)
    }

    private var mIsEditText = false // 是否是编辑文字状态，编辑文字时只能显示一个item且编辑框不能取消

    /**
     * 是否是编辑文字状态，编辑文字时只能显示一个item且编辑框不能取消
     *
     * @param isEditText
     */
    fun setIsEditText(isEditText: Boolean) {
        mIsEditText = isEditText
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
                    } else if (item.helpBox!!.contains(x, y)) { // 点击了内容区域
                        selectedItem = item // 点击会重叠，先标记一下，没有后续删除等操作，才认为是选中逻辑
                        clickToolItem = null
                    }
                }
                // 没有贴图被选择、不是编辑文字状态
                if (!isHandle && mCurrentItem != null && mCurrentStatus == STATUS_IDLE && !mIsEditText) {
                    mCurrentItem!!.isShowHelpTools = false
                    mCurrentItem = null
                    invalidate()
                }
                if (selectedItem != null) { // 被选中一张贴图，设置为移动模式
                    isHandle = true
                    oldX = x
                    oldY = y
                    mCurrentStatus = STATUS_MOVE
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
                        mCurrentItem!!.updateRotateAndScale(oldX, oldY, dx, dy, true) // 旋转
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
        } else {
            when (currentStatus) {
                STATUS_DELETE -> {
                    itemBank.remove(clickItem.itemId)
                    mCurrentStatus = STATUS_IDLE // 返回空闲状态
                }
                STATUS_EDIT -> {
                    mCurrentStatus = STATUS_IDLE // 返回空闲状态
                    Toast.makeText(mContext, "edit", Toast.LENGTH_SHORT).show()
                }
                STATUS_ROTATE -> {}
                else -> {}
            }
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
        if (itemBank.isNullOrEmpty()) {
            for (id in itemBank.keys) {
                itemBank[id]!!.isShowHelpTools = false
            }
            invalidate()
        }
    }
}
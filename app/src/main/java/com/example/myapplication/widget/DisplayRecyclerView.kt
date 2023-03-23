package com.example.myapplication.widget

import android.content.Context
import android.graphics.Bitmap
import android.util.AttributeSet
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.constraintlayout.widget.ConstraintSet
import androidx.core.view.marginTop
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.myapplication.R
import com.example.myapplication.model.DisplayItems
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

sealed class CardState {
    object Idle : CardState()
    data class Forward(val bitmap: Bitmap) : CardState()
    data class Backward(val bitmap: Bitmap) : CardState()
    data class ShowComment(val position: Int) : CardState()
    object Like : CardState()
}

class DisplayRecyclerView : RecyclerView,
    CardFunction.DisplayCardFunction {
    constructor(context: Context) : super(context)
    constructor(context: Context, attrs: AttributeSet) : super(context, attrs)
    constructor(context: Context, attrs: AttributeSet, defs: Int) : super(context, attrs, defs)

    private val _displayFlow: MutableStateFlow<CardState> = MutableStateFlow(CardState.Idle)

    private lateinit var lastLeftChild: View

    private val displayPictures: ArrayList<Bitmap> = arrayListOf()
    private val displayIcons: ArrayList<Bitmap> = arrayListOf()
    private val displayComments: ArrayList<String> = arrayListOf()

    override fun onFinishInflate() {
        super.onFinishInflate()

        layoutManager = LinearLayoutManager(context, HORIZONTAL, false)

        adapter = DisplayItemAdapter()
    }

    override fun onScrollStateChanged(state: Int) {
        super.onScrollStateChanged(state)

        Log.d("RRR", "onScrollStateChanged state: $state")
        if (state == SCROLL_STATE_IDLE) {
            _displayFlow.value = CardState.Idle
        }
    }

    override fun onScrolled(dx: Int, dy: Int) {
        super.onScrolled(dx, dy)

        if (dx == 0) {
            return
        }

        val leftChild = getChildAt(0)

        if (lastLeftChild != leftChild) {
            _displayFlow.update {
                if (dx > 0) {
                    CardState.Forward(displayPictures[lastLeftChild.id])
                } else {
                    CardState.Backward(displayPictures[leftChild.id])
                }
            }

            lastLeftChild.layoutParams?.height = this.height / 3 * 2
            (lastLeftChild.layoutParams as? MarginLayoutParams)?.topMargin = this.height / 6

            lastLeftChild = leftChild.also {
                it.findViewById<ImageView>(R.id.image).layoutParams.height = this.height
                it.findViewById<RecyclerView>(R.id.content).visibility = GONE

                it.layoutParams.height = this.height
                (it.layoutParams as MarginLayoutParams).topMargin = 0
            }
            requestLayout()
        }
    }

    override fun updateItem(items: DisplayItems) {
        displayPictures.addAll(items.pictures)
        displayIcons.addAll(items.icons)
        displayComments.addAll(items.comments)
        (adapter as DisplayItemAdapter).updateItem()
    }

    override fun getStateFlow(): StateFlow<CardState> = _displayFlow.asStateFlow()

    override fun adjustLayout(newPosition: Int, isExtend: Boolean) {
        val set = ConstraintSet()
        set.clone((parent as ConstraintLayout))
        set.connect(
            this.id,
            ConstraintSet.START,
            (parent as ConstraintLayout).id,
            ConstraintSet.START,
            if (isExtend) {
                context.resources.getDimension(R.dimen.display_view_with_stack_margin_start).toInt()
            } else {
                context.resources.getDimension(R.dimen.display_view_without_stack_margin_start)
                    .toInt()
            }
        )
        set.applyTo(parent as ConstraintLayout)
        smoothScrollToPosition(newPosition)
    }

    override fun showComment(position: Int) {
        Log.d("RRR", "lockLayout: $position")
        smoothScrollToPosition(position)

        val image = lastLeftChild.findViewById<ImageView>(R.id.image)
        image.layoutParams.height = this.height / 2

        val content = lastLeftChild.findViewById<RecyclerView>(R.id.content)
        content.visibility = VISIBLE
        content.layoutManager = LinearLayoutManager(context, VERTICAL, false)
        content.adapter = CommentItemAdapter()
        requestLayout()
    }

    inner class DisplayItemAdapter : Adapter<ViewHolder>() {

        override fun onCreateViewHolder(parent: ViewGroup, position: Int): ViewHolder {
            return LayoutInflater.from(parent.context)
                .inflate(R.layout.display_recycle_view_item, parent, false).apply {
                    this.layoutParams.width = 900
                    if (position == 0) {
                        lastLeftChild = this
                        this.layoutParams.height = parent.height
                        (this.layoutParams as MarginLayoutParams).topMargin = 0
                    } else {
                        this.layoutParams.height = parent.height / 3 * 2
                        (this.layoutParams as MarginLayoutParams).topMargin = parent.height / 6
                    }
                }.let {
                    DisplayItemHolder(it).also { holder ->
                        holder.like.setOnClickListener {
                            if (holder.itemView == lastLeftChild) {
                                _displayFlow.value = CardState.Like
                            }
                        }
                        holder.comment.setOnClickListener {
                            if (holder.itemView == lastLeftChild) {
                                _displayFlow.value = CardState.ShowComment(position)
                            }
                        }
                    }
                }
        }

        override fun getItemCount(): Int {
            return displayPictures.size
        }

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            (holder as DisplayItemHolder).setBackground(displayPictures[position])
            holder.itemView.id = position
        }

        override fun getItemViewType(position: Int): Int {
            return position
        }

        fun updateItem() {
            notifyItemRangeChanged(0, itemCount)
        }
    }

    class DisplayItemHolder(itemView: View) : ViewHolder(itemView) {

        private val image: ImageView = itemView.findViewById(R.id.image)
        val like: ImageView = itemView.findViewById(R.id.like)
        val comment: ImageView = itemView.findViewById(R.id.comment)

        fun setBackground(bitmap: Bitmap) {
            image.apply {
                addOnLayoutChangeListener { _, left, top, right, bottom, _, _, _, _ ->
                    bitmap.reconfigure(
                        right - left,
                        bottom - top,
                        Bitmap.Config.ARGB_8888
                    )
                    setImageBitmap(bitmap)
                }
            }
        }
    }

    inner class CommentItemAdapter : Adapter<ViewHolder>() {

        override fun onCreateViewHolder(parent: ViewGroup, position: Int): ViewHolder {
            return LayoutInflater.from(parent.context)
                .inflate(R.layout.comment_recycle_view_item, parent, false).apply {
                    this.layoutParams.height = parent.height / 4
                }.let {
                    CommentItemHodler(it)
                }
        }

        override fun getItemCount(): Int {
            return displayIcons.size
        }

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            (holder as CommentItemHodler).icon.setImageBitmap(displayIcons[position])
            holder.comment.text = displayComments[position]
        }
    }

    class CommentItemHodler(itemView: View) : ViewHolder(itemView) {

        val icon: ImageView = itemView.findViewById(R.id.icon)
        val comment: TextView = itemView.findViewById(R.id.comment)
    }
}
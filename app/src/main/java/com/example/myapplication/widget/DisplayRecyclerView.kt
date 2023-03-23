package com.example.myapplication.widget

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Color
import android.util.AttributeSet
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.myapplication.R

class DisplayRecyclerView : RecyclerView {
    constructor(context: Context) : super(context)
    constructor(context: Context, attrs: AttributeSet) : super(context, attrs)
    constructor(context: Context, attrs: AttributeSet, defs: Int) : super(context, attrs, defs)

    interface CardCallback {
        fun like() {}
        fun loadComment() {}
        fun forward(position: Int) {}
        fun backward(position: Int) {}
    }

    var callback: CardCallback? = null

    private var lastLeftChild: View? = null

    override fun onFinishInflate() {
        super.onFinishInflate()

        layoutManager = LinearLayoutManager(context, HORIZONTAL, false)
        adapter = DisplayItemAdapter()
    }

    override fun onScrolled(dx: Int, dy: Int) {
        super.onScrolled(dx, dy)

        val leftChild = getChildAt(0)
        if (lastLeftChild != leftChild) {
            if (dx > 0) {
                callback?.forward(leftChild.id)
            } else {
                callback?.backward(leftChild.id)
            }
            lastLeftChild?.let {
                it.layoutParams.height = this.height / 3 * 2
                (it.layoutParams as MarginLayoutParams).topMargin = this.height / 6
            }
            lastLeftChild = leftChild
            leftChild.layoutParams.height = this.height
            (leftChild.layoutParams as MarginLayoutParams).topMargin = 0
            requestLayout()
        }
    }

    class DisplayItemAdapter : Adapter<ViewHolder>() {
        private val pictures: ArrayList<Bitmap> = arrayListOf()

        private var parentHeight: Int = -1

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            return LayoutInflater.from(parent.context)
                .inflate(R.layout.display_recycle_view_item, parent, false).apply {
                    this.layoutParams.width = 900
                    parentHeight = parent.height
                    if (viewType == 0) {
                        this.layoutParams.height = parentHeight
                        (this.layoutParams as MarginLayoutParams).topMargin = 0
                    } else {
                        this.layoutParams.height = parentHeight / 3 * 2
                        (this.layoutParams as MarginLayoutParams).topMargin = parentHeight / 6
                    }
                }.let {
                    DisplayItemHolder(it)
                }
        }

        override fun getItemCount(): Int {
            return pictures.size
        }

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            (holder as DisplayItemHolder).setBackground(pictures[position])
            holder.itemView.id = position
        }

        override fun getItemViewType(position: Int): Int {
            return position
        }

        fun updateItem(pictures: ArrayList<Bitmap>) {
            this.pictures.addAll(pictures)
            notifyItemRangeChanged(0, itemCount)
        }
    }

    class DisplayItemHolder(itemView: View) : ViewHolder(itemView) {

        fun setBackground(bitmap: Bitmap) {
            itemView.findViewById<ImageView>(R.id.image).setImageBitmap(bitmap)
            itemView.findViewById<ImageView>(R.id.image).setBackgroundColor(Color.BLACK)
        }
    }
}
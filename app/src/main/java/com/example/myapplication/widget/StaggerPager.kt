package com.example.myapplication.widget

import android.content.Context
import android.graphics.Bitmap
import android.util.AttributeSet
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.viewpager.widget.PagerAdapter
import androidx.viewpager.widget.ViewPager
import com.example.myapplication.R

class StaggerPager : ViewPager {
    constructor(context: Context) : super(context)
    constructor(context: Context, attrs: AttributeSet) : super(context, attrs)

    override fun onFinishInflate() {
        super.onFinishInflate()

        adapter = StaggerItemAdapter()
        offscreenPageLimit = ITEM_COUNT
        setPageTransformer(false, StaggerTransformer())
    }

    inner class StaggerItemAdapter : PagerAdapter() {
        private val allPictures: ArrayList<Bitmap> = arrayListOf()
        private val staggerPictures: ArrayList<Bitmap> = arrayListOf()

        override fun getCount(): Int {
            return ITEM_COUNT
        }

        override fun isViewFromObject(view: View, `object`: Any): Boolean {
            return view == `object`
        }

        override fun instantiateItem(container: ViewGroup, position: Int): Any {
            Log.d("NEU", "instantiateItem: $position")
            return LayoutInflater.from(container.context).inflate(
                R.layout.stagger_pager_view_item, container, false
            ).apply {
                val paddingStart = context.resources.getDimension(R.dimen.card_padding_start)
                val paddingVertical = context.resources.getDimension(R.dimen.card_padding_vertical)


                this.layoutParams.width = container.width
                this.layoutParams.height = container.height
                this.setPadding(
                    (paddingStart * position / ITEM_COUNT).toInt(),
                    (paddingVertical * (3 - position) / ITEM_COUNT).toInt(),
                    0,
                    (paddingVertical * (3 - position) / ITEM_COUNT).toInt()
                )

                val imageView = this.findViewById<ImageView>(R.id.image)
                if (staggerPictures.size != 0) {
                    imageView.setImageBitmap(staggerPictures[position])
                }
            }.also {
                container.addView(it)
            }
        }

        override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {
            Log.d("NEU", "destroyItem: $position")
            container.removeView(`object` as View)
        }

        override fun getItemPosition(`object`: Any): Int {
            return super.getItemPosition(`object`)
        }

        fun updateItem(pictures: ArrayList<Bitmap>) {
            this.allPictures.clear()
            this.allPictures.addAll(pictures)
        }

        fun setPosition(position: Int) {
            Log.d("NEU", "setPosition: $position")
            val new = allPictures[position]
            if (staggerPictures.contains(new)) {
                staggerPictures.remove(allPictures[position + 1])
                if (position >= ITEM_COUNT) {
                    staggerPictures.add(allPictures[position - ITEM_COUNT])
                }
            } else {
                staggerPictures.add(allPictures[position])
                if (position >= ITEM_COUNT) {
                    staggerPictures.remove(allPictures[position - ITEM_COUNT])
                }
            }
            notifyDataSetChanged()
        }
    }

    class StaggerTransformer : PageTransformer {
        override fun transformPage(page: View, position: Float) {
            if (position < 0) {
                page.translationX = -page.width * position
            }
        }
    }

    companion object {
        private const val ITEM_COUNT = 4
    }
}
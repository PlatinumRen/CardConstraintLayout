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
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

sealed class StaggerShared {
    data class Open(val position: Int) : StaggerShared()

    data class Close(val position: Int): StaggerShared()
}

class StaggerPager : ViewPager, CardFunction.StaggerCardFunction {
    constructor(context: Context) : super(context)
    constructor(context: Context, attrs: AttributeSet) : super(context, attrs)

    private val staggerPictures: ArrayList<Bitmap> = arrayListOf()

    private val _staggerShare: MutableSharedFlow<StaggerShared> = MutableSharedFlow()

    private var updateShareJob: Job? = null

    override fun onFinishInflate() {
        super.onFinishInflate()

        setPageTransformer(false, StaggerTransformer())
    }

    override fun addPicture(picture: Bitmap) {
        Log.d("NEU", "addPicture: $picture")
        if (staggerPictures.add(picture)) {
            visibility = VISIBLE
            updateItem()
            currentItem = staggerPictures.size - 1
        }
    }

    override fun removePicture(picture: Bitmap) {
        Log.d("NEU", "removePicture: $picture")
        if (staggerPictures.remove(picture)) {
            updateItem()
        }
        if (staggerPictures.size == 0) {
            visibility = GONE
        } else {
            currentItem = staggerPictures.size - 1
        }
    }

    override fun getSharedFlow(): SharedFlow<StaggerShared> {
        // Noop.
        return _staggerShare.asSharedFlow()
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()

        updateShareJob?.cancel()
        updateShareJob = null
    }

    private fun updateItem() {
        adapter = StaggerItemAdapter(staggerPictures)
        offscreenPageLimit = staggerPictures.size
    }

    class StaggerTransformer : PageTransformer {
        override fun transformPage(page: View, position: Float) {
            if (position < 0) {
                page.translationX = -page.width * position
            }
        }
    }

    inner class StaggerItemAdapter(
        private val staggerPictures: ArrayList<Bitmap>
    ) : PagerAdapter() {

        override fun getCount(): Int {
            return staggerPictures.size
        }

        override fun isViewFromObject(view: View, `object`: Any): Boolean {
            return view == `object`
        }

        override fun instantiateItem(container: ViewGroup, position: Int): Any {
            Log.d("NEU", "instantiateItem position: $position")
            return LayoutInflater.from(container.context).inflate(
                R.layout.stagger_pager_view_item, container, false
            ).apply {
                val paddingStart = context.resources.getDimension(R.dimen.card_padding_start)
                val paddingVertical = context.resources.getDimension(R.dimen.card_padding_vertical)


                this.layoutParams.width = container.width
                this.layoutParams.height = container.height

                this.setPadding(
                    (paddingStart * position / count).toInt(),
                    (paddingVertical * (count - position - 1) / count).toInt(),
                    0,
                    (paddingVertical * (count - position - 1) / count).toInt()
                )

                val imageView = this.findViewById<ImageView>(R.id.image)
                imageView.addOnLayoutChangeListener { _, left, top, right, bottom, _, _, _, _ ->
                    val bitmap = staggerPictures[position]
                    bitmap.reconfigure(
                        right - left,
                        bottom - top,
                        Bitmap.Config.ARGB_8888
                    )
                    imageView.setImageBitmap(bitmap)
                }
            }.also {
                if (position == staggerPictures.size - 1) {
                    it.setOnClickListener {
                        updateShareJob = CoroutineScope(Dispatchers.Main).launch {
                            _staggerShare.emit(StaggerShared.Close(position))
                        }
                    }
                    it.setOnLongClickListener {
                        updateShareJob = CoroutineScope(Dispatchers.Main).launch {
                            _staggerShare.emit(StaggerShared.Open(position))
                        }
                        true
                    }
                }
                container.addView(it)
            }
        }

        override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {
            Log.d("NEU", "destroyItem: $position")
            container.removeView(`object` as View)
        }
    }
}
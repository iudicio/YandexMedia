package com.example.yandexmedia.presentation.ui.player

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.RectF
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import androidx.appcompat.content.res.AppCompatResources
import androidx.core.graphics.drawable.toBitmap
import com.example.yandexmedia.R

class PlaybackButtonView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    private val paint = Paint(Paint.ANTI_ALIAS_FLAG)
    private val imageRect = RectF()

    private var playBitmap: Bitmap? = null
    private var pauseBitmap: Bitmap? = null
    private var isPlaying = false

    init {
        context.theme.obtainStyledAttributes(
            attrs,
            R.styleable.PlaybackButtonView,
            defStyleAttr,
            0
        ).apply {
            try {
                val playImageResId = getResourceId(
                    R.styleable.PlaybackButtonView_playImage,
                    0
                )
                val pauseImageResId = getResourceId(
                    R.styleable.PlaybackButtonView_pauseImage,
                    0
                )

                playBitmap = getBitmapFromResource(playImageResId)
                pauseBitmap = getBitmapFromResource(pauseImageResId)
            } finally {
                recycle()
            }
        }

        isClickable = true
        isFocusable = true
    }

    override fun onSizeChanged(
        width: Int,
        height: Int,
        oldWidth: Int,
        oldHeight: Int
    ) {
        super.onSizeChanged(width, height, oldWidth, oldHeight)
        imageRect.set(
            0f,
            0f,
            width.toFloat(),
            height.toFloat()
        )
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        val bitmap = if (isPlaying) {
            pauseBitmap
        } else {
            playBitmap
        }

        bitmap?.let {
            canvas.drawBitmap(
                it,
                null,
                imageRect,
                paint
            )
        }
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        if (!isEnabled) {
            return false
        }

        return when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                true
            }

            MotionEvent.ACTION_UP -> {
                togglePlaybackState()
                performClick()
                true
            }

            else -> {
                super.onTouchEvent(event)
            }
        }
    }

    override fun performClick(): Boolean {
        super.performClick()
        return true
    }

    fun setPlaying(isPlaying: Boolean) {
        if (this.isPlaying == isPlaying) {
            return
        }

        this.isPlaying = isPlaying
        invalidate()
    }

    fun togglePlaybackState() {
        setPlaying(!isPlaying)
    }

    private fun getBitmapFromResource(resourceId: Int): Bitmap? {
        if (resourceId == 0) {
            return null
        }

        val drawable = AppCompatResources.getDrawable(
            context,
            resourceId
        ) ?: return null

        val width = drawable.intrinsicWidth.takeIf { it > 0 }
            ?: DEFAULT_ICON_SIZE
        val height = drawable.intrinsicHeight.takeIf { it > 0 }
            ?: DEFAULT_ICON_SIZE

        return drawable.toBitmap(
            width,
            height,
            Bitmap.Config.ARGB_8888
        )
    }

    private companion object {
        const val DEFAULT_ICON_SIZE = 100
    }
}
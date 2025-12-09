package com.example.playlistmaker.player.ui.customView

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.RectF
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import androidx.annotation.AttrRes
import androidx.annotation.StyleRes
import androidx.core.graphics.drawable.toBitmap
import com.example.playlistmaker.R

class PlaybackButtonView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    @AttrRes defStyleAttr: Int = 0,
    @StyleRes defStyleRes: Int = 0,
) : View(context, attrs, defStyleAttr, defStyleRes) {

    private var playBitmap: Bitmap? = null
    private var pauseBitmap: Bitmap? = null

    private val imageRect = RectF()

    var isPlaying: Boolean = false
        set(value) {
            if (field != value) {
                field = value
                invalidate()
            }
        }

    private var onPlaybackStateChangeListener: ((isPlaying: Boolean) -> Unit)? = null


    init {
        context.theme.obtainStyledAttributes(
            attrs,
            R.styleable.PlaybackButtonView,
            defStyleAttr,
            defStyleRes
        ).apply {
            try {

                playBitmap = getDrawable(R.styleable.PlaybackButtonView_playIcon)?.toBitmap()
                pauseBitmap = getDrawable(R.styleable.PlaybackButtonView_pauseIcon)?.toBitmap()
                isPlaying = getBoolean(R.styleable.PlaybackButtonView_initialIsPlaying, false)

            } finally {
                recycle()
            }
        }
        invalidate()
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        imageRect.set(0f, 0f, w.toFloat(), h.toFloat())
    }

    override fun onDraw(canvas: Canvas) {
        val currentBitmap = if (isPlaying) pauseBitmap else playBitmap
        currentBitmap?.let {
            canvas.drawBitmap(it, null, imageRect, null)
        }
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        return when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                true
            }

            MotionEvent.ACTION_UP -> {
                performClick()
                true
            }

            else -> super.onTouchEvent(event)
        }
    }

    override fun performClick(): Boolean {
        super.performClick()

        isPlaying = !isPlaying

        onPlaybackStateChangeListener?.invoke(isPlaying)

        return true
    }

    fun setPlaybackState(playing: Boolean) {
        isPlaying = playing
    }

    fun setOnPlaybackStateChangeListener(listener: (isPlaying: Boolean) -> Unit) {
        this.onPlaybackStateChangeListener = listener
    }
}
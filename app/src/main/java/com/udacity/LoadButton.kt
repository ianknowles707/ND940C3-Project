package com.udacity

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.View
import kotlin.properties.Delegates

class LoadButton @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {
    private var currentWidth = 0
    private var maxWidth = 0
    private var buttonHeight = 0
    private var buttonText = ""

    private lateinit var rect: Rect

    private val paint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.FILL
        textAlign = Paint.Align.CENTER
        textSize = 55.0f
        typeface = Typeface.DEFAULT_BOLD
    }

    var buttonState: ButtonState by Delegates.observable<ButtonState>(ButtonState.Completed) { p, old, new ->
        when (new) {
            ButtonState.Loading -> {
                buttonText = resources.getString(R.string.download_button_loading)
                invalidate()
            }

//            ButtonState.Clicked -> {
//
//            }

            ButtonState.Completed -> {
                buttonText = resources.getString(R.string.download_button_default)
                invalidate()

            }
        }
    }

    init {
        isClickable = true
        buttonState = ButtonState.Completed
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        maxWidth = w
        buttonHeight = h
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        var x = (maxWidth / 2).toFloat()
        var y = (buttonHeight / 2).toFloat()

        rect = Rect(0, 0, maxWidth, buttonHeight)
        paint.color = Color.LTGRAY
        canvas?.drawRect(rect, paint)

        paint.color=Color.BLUE
        canvas?.drawText(buttonText, x, y, paint)
    }
}
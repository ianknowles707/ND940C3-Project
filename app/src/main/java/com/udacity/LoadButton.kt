package com.udacity

import android.animation.ValueAnimator
import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.View
import kotlin.properties.Delegates

class LoadButton @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {
    private var currentWidth = 0f
    private var maxWidth = 0f
    private var buttonHeight = 0f
    private var buttonText = ""

    //Define the button base - lazy ensure it will be set when called (after onSizeChanged)
    private val rectBase by lazy {
        RectF(0f, 0f, maxWidth, buttonHeight)
    }

    private val rectAnimated by lazy {
        RectF(0f, 0f, currentWidth, buttonHeight)
    }

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
                animateLoadButton()
            }

            ButtonState.Clicked -> {

            }

            ButtonState.Completed -> {
                buttonText = resources.getString(R.string.download_button_default)
                currentWidth = 0f
            }
        }
        invalidate()
    }

    init {
        isClickable = true
        buttonState = ButtonState.Completed
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        maxWidth = w.toFloat()
        buttonHeight = h.toFloat()
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        val x = (maxWidth / 2)
        val y = (buttonHeight / 2)

        paint.color = Color.GRAY
        canvas?.drawRect(rectBase, paint)

        paint.color = Color.BLUE
        canvas?.drawRect(rectAnimated, paint)

        paint.color = Color.WHITE
        canvas?.drawText(buttonText, x, y, paint)
    }

    private fun animateLoadButton() {
        val animator = ValueAnimator.ofFloat(0f, maxWidth)
        animator.duration = 2000
        animator.repeatMode = ValueAnimator.RESTART
        animator.repeatCount = ValueAnimator.INFINITE
        animator.addUpdateListener { value ->
            currentWidth = value.animatedValue as Float
            invalidate()
        }
        animator.start()
    }
}
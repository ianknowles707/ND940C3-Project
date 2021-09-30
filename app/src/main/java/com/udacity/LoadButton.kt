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

    //Define variables for the animation
    private var currentWidth = 0f
    private var maxWidth = 0f
    private var buttonHeight = 0f
    private var buttonText = ""
    private lateinit var animator: ValueAnimator
    private var animationRunning = false

    //Define the button base - lazy ensure it will be set when called (after onSizeChanged)
    //This will not change so can be fixed values
    private val rectBase by lazy {
        RectF(0f, 0f, maxWidth, buttonHeight)
    }

    //Define RectF object to draw the loading bar. Values will need to change
    //so will be passed later
    private lateinit var rectAnimated: RectF

    private val paint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.FILL
        textAlign = Paint.Align.CENTER
        textSize = 55.0f
        typeface = Typeface.DEFAULT_BOLD
    }

    //Set up actions for the custom button based on button state
    var buttonState: ButtonState by Delegates.observable<ButtonState>(ButtonState.Completed) { p, old, new ->
        when (new) {
            ButtonState.Loading -> {
                //Change text on button, start the animations
                buttonText = resources.getString(R.string.download_button_loading)
                animateLoadButton()
            }

            ButtonState.Clicked -> {

            }

            ButtonState.Completed -> {
                //Reset default text, cancel animation and reset to starting state
                buttonText = resources.getString(R.string.download_button_default)
                if (animationRunning) {
                    animator.end()
                }
                currentWidth = 0f
                rectAnimated = RectF(0f, 0f, currentWidth, buttonHeight)
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
        animator = ValueAnimator.ofFloat(0f, maxWidth)
        animator.duration = 2000
        animator.repeatMode = ValueAnimator.RESTART
        animator.repeatCount = ValueAnimator.INFINITE
        animator.addUpdateListener { value ->
            currentWidth = value.animatedValue as Float
            rectAnimated = RectF(0f, 0f, currentWidth, buttonHeight)
            invalidate()
        }
        animator.start()
        animationRunning = true
    }

    private fun stopAnimation() {
        animator.end()
        animationRunning = false
    }
}
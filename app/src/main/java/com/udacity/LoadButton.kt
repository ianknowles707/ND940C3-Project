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
    private var angle = 0f
    private var radius = 0f
    private var maxWidth = 0f
    private var buttonHeight = 0f

    private var rectX = 0f
    private var rectY = 0f
    private var circleLeft = 0f
    private var circleRight = 0f
    private var circleTop = 0f
    private var circleBottom = 0f

    private var buttonText = ""
    private lateinit var animator: ValueAnimator
    private lateinit var animatorCircle: ValueAnimator

    private var animationRunning = false

    //Define the button base - lazy ensure it will be set when called (after onSizeChanged)
    //This will not change so can be fixed values
    private val rectBase by lazy {
        RectF(0f, 0f, maxWidth, buttonHeight)
    }

    //Define RectF object to draw the loading bar. Values will need to change
    //so will be passed later
    private lateinit var rectAnimated: RectF

    private lateinit var circleAnimated: RectF

    private val paint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.FILL
        textAlign = Paint.Align.CENTER
        textSize = 55.0f
        typeface = Typeface.DEFAULT_BOLD
    }

    //Set up actions for the custom button based on button state
    var buttonState: ButtonState by Delegates.observable<ButtonState>(ButtonState.Completed) { _, _, new ->
        when (new) {
            ButtonState.Loading -> {
                //Change text on button, start the animations
                buttonText = resources.getString(R.string.download_button_loading)
                animateAll()
            }

            ButtonState.Clicked -> {

            }

            ButtonState.Completed -> {
                //Reset default text, cancel animation and reset to starting state
                buttonText = resources.getString(R.string.download_button_default)
                if (animationRunning) {
                    animator.end()
                    animatorCircle.end()
                }
                currentWidth = 0f
                angle = 0f

                rectAnimated = RectF(0f, 0f, currentWidth, buttonHeight)
                circleAnimated = RectF(circleLeft, circleTop, circleRight, circleBottom)
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
        radius = buttonHeight / 4
        rectX = (maxWidth / 2)
        rectY = (buttonHeight / 2)
        circleLeft = maxWidth - 3 * radius
        circleRight = maxWidth - radius
        circleTop = radius
        circleBottom = buttonHeight - radius
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)

        paint.color = Color.GRAY
        canvas?.drawRect(rectBase, paint)

        paint.color = Color.BLUE
        canvas?.drawRect(rectAnimated, paint)

        paint.color = Color.YELLOW
        canvas?.drawArc(circleLeft, circleTop, circleRight, circleBottom, 0f, angle, true, paint)

        paint.color = Color.WHITE
        canvas?.drawText(buttonText, rectX, rectY, paint)
    }

    private fun animateAll() {
        animateLoadButton()
        animateCircle()
        animationRunning = true
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
    }

    private fun animateCircle() {
        animatorCircle = ValueAnimator.ofFloat(0f, 360f)
        animatorCircle.duration = 2000
        animatorCircle.repeatMode = ValueAnimator.RESTART
        animatorCircle.repeatCount = ValueAnimator.INFINITE
        animatorCircle.addUpdateListener { value ->
            angle = value.animatedValue as Float
            invalidate()
        }
        animatorCircle.start()
    }
}
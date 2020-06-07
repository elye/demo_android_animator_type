package com.elyeproj.animatortype

import android.animation.*
import android.animation.ValueAnimator.INFINITE
import android.animation.ValueAnimator.REVERSE
import android.graphics.Color
import android.graphics.LinearGradient
import android.graphics.Shader
import android.os.Bundle
import android.text.TextPaint
import android.view.View
import android.view.animation.AccelerateDecelerateInterpolator
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }

    private var currentAnimator: Animator? = null

    fun simpleAnimate(view: View) {
        currentAnimator?.cancel()

        txt_animate.rotationX = 0f
        txt_animate.animate().apply {
            rotationX(3600f)
            duration = 5000
            interpolator = AccelerateDecelerateInterpolator()
        }.start()
    }

    fun objectProperty(view: View) {
        currentAnimator?.cancel()

        currentAnimator = ObjectAnimator.ofFloat(txt_animate, View.ROTATION_X, 0f, 3600f).apply {
            duration = 5000
            interpolator = AccelerateDecelerateInterpolator()
            repeatCount = INFINITE
            repeatMode = REVERSE
            start()
        }
    }

    fun objectReflection(view: View) {
        currentAnimator?.cancel()
        txt_animate.paint.shader = null

        currentAnimator = ObjectAnimator.ofArgb(txt_animate, "textColor",
            Color.parseColor("#FFFF0000"), Color.parseColor("#FF0000FF")).apply {
            duration = 5000
            interpolator = AccelerateDecelerateInterpolator()
            repeatCount = INFINITE
            repeatMode = REVERSE
            start()
        }
    }

    fun propertyValueHolders(view: View) {
        currentAnimator?.cancel()
        txt_animate.paint.shader = null

        val rotationX = PropertyValuesHolder.ofFloat(View.ROTATION_X, 0f, 3600f)
        val textColor = PropertyValuesHolder.ofInt("textColor", Color.parseColor("#FFFF0000"), Color.parseColor("#FF0000FF"))
        textColor.setEvaluator(ArgbEvaluator())
        currentAnimator = ObjectAnimator.ofPropertyValuesHolder(txt_animate, rotationX, textColor).apply {
            duration = 10000
            interpolator = AccelerateDecelerateInterpolator()
            repeatCount = INFINITE
            repeatMode = REVERSE
            start()
        }

    }

    fun animationSet(view: View) {
        currentAnimator?.cancel()
        txt_animate.paint.shader = null

        val rotationX = PropertyValuesHolder.ofFloat(View.ROTATION_X, 0f, 3600f)
        val textColorX = PropertyValuesHolder.ofInt("textColor", Color.parseColor("#FFFF0000"), Color.parseColor("#FF0000FF"))
        textColorX.setEvaluator(ArgbEvaluator())
        val rotateXColor = ObjectAnimator.ofPropertyValuesHolder(txt_animate, rotationX, textColorX).apply {
            duration = 10000
            interpolator = AccelerateDecelerateInterpolator()
        }

        val rotationY = PropertyValuesHolder.ofFloat(View.ROTATION_Y, 0f, 3600f)
        val textColorY = PropertyValuesHolder.ofInt("textColor", Color.parseColor("#FF0000FF"), Color.parseColor("#FFFF0000"))
        textColorY.setEvaluator(ArgbEvaluator())
        val rotateYColor = ObjectAnimator.ofPropertyValuesHolder(txt_animate, rotationY, textColorY).apply {
            duration = 10000
            interpolator = AccelerateDecelerateInterpolator()
        }

        currentAnimator = AnimatorSet().apply{
            play(rotateYColor).after(rotateXColor)
            start()
        }
    }

    fun valueAnimator(view: View) {
        val paint: TextPaint = txt_animate.paint
        val width = paint.measureText(txt_animate.text.toString())

        currentAnimator?.cancel()

        currentAnimator = ValueAnimator.ofObject(
            GradientArgEvaluator(),
            intArrayOf(Color.RED, Color.RED, Color.RED),
            intArrayOf(Color.RED, Color.RED, Color.GREEN),
            intArrayOf(Color.RED, Color.GREEN, Color.BLACK),
            intArrayOf(Color.GREEN, Color.BLACK, Color.MAGENTA),
            intArrayOf(Color.BLACK, Color.MAGENTA, Color.BLUE),
            intArrayOf(Color.MAGENTA, Color.BLUE, Color.BLUE),
            intArrayOf(Color.BLUE, Color.BLUE, Color.BLUE)
        ).apply {
            repeatMode = REVERSE
            repeatCount = INFINITE
            duration = 10000
            interpolator = AccelerateDecelerateInterpolator()
            addUpdateListener {

                val textShader: Shader = LinearGradient(
                    0f, 0f, width, txt_animate.textSize,
                    it.animatedValue as IntArray, null, Shader.TileMode.CLAMP
                )
                txt_animate.paint.shader = textShader
                txt_animate.invalidate()
            }
            start()
        }
    }
}

class GradientArgEvaluator: TypeEvaluator<IntArray> {
    private val argbEvaluator = ArgbEvaluator()

    override fun evaluate(fraction: Float, startValue: IntArray, endValue: IntArray): IntArray {
        require(startValue.size == endValue.size)
        return startValue.mapIndexed { index, item ->
            argbEvaluator.evaluate(fraction, item, endValue[index]) as Int
        }.toIntArray()
    }
}

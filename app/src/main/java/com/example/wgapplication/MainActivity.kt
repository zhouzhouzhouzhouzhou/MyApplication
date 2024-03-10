package com.example.wgapplication

import android.animation.Animator
import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.app.Activity
import android.content.Context
import android.content.res.Resources
import android.graphics.Rect
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.util.DisplayMetrics
import android.util.Log
import android.view.View
import android.view.animation.AccelerateInterpolator
import android.view.animation.DecelerateInterpolator
import android.widget.Button
import android.widget.ImageView
import java.lang.Exception
import java.lang.reflect.Field

class MainActivity : AppCompatActivity() {
    var img: ImageView ?= null;
    private  val TAG = "快捷发布"
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        img = findViewById(R.id.iv_quick_goods)
        var rec = Rect()

        img?.getLocalVisibleRect(rec)

        findViewById<Button>(R.id.start).setOnClickListener {
            img?.let {
                showPushCardAnim(it)
            }
        }


         Handler().postDelayed(Runnable {
            img?.let {
                showPushCardAnim(it)
            }
        }, 1000)


    }

    fun showPushCardAnim(view: View) {
        // 获取依赖视图坐标
        val anchorLoc = IntArray(2)
        img?.getLocationOnScreen(anchorLoc)

        Log.i(TAG, "onCreate1: " + anchorLoc[0] + " " + anchorLoc[1])
        // 获取依赖视图坐标
        val anchor  = IntArray(2)

        img?.getLocationInWindow(anchor)

        Log.i(TAG, "onCreate2: " + anchor[0] + " " + anchor[1])
        Log.i(
            "快捷发布",
            view.visibility.toString() + "showPushCardAnim: " + getScreenWidth(this) + " " + getScreenHeight(
                this
            ) + " " + view.measuredWidth + " " + view.measuredHeight
        )
        Log.i(TAG, "showPushCardAnim: height " + view.height + " " + view.width)
        // 移动动画，从当前位置到右下角
        val moveX: ObjectAnimator =
            ObjectAnimator.ofFloat(view, "translationX", 0f,getScreenWidth(this) - view.width)
        val moveY: ObjectAnimator =
            ObjectAnimator.ofFloat(view, "translationY", 0f,getScreenHeight(this) )

        // 透明度动画，从100到0
        val alpha = ObjectAnimator.ofFloat(view, "alpha", 1f, 0f)

        // 大小变化动画，从当前大小变为40
        val scaleX = ObjectAnimator.ofFloat(view, "scaleX", 1f, 0f)
        val scaleY = ObjectAnimator.ofFloat(view, "scaleY",  1f, 0f)

        // 创建动画集合
        val animatorSet = AnimatorSet()
        animatorSet.interpolator = DecelerateInterpolator()
        animatorSet.duration = 1000
        animatorSet.playTogether(moveX, moveY, alpha, scaleX, scaleY)


        // 启动动画
        animatorSet.start()
    }

    fun getScreenWidth(context: Context): Float {
        val localDisplayMetrics = DisplayMetrics()
        (context as Activity).windowManager.defaultDisplay.getMetrics(localDisplayMetrics)
        return localDisplayMetrics.widthPixels.toFloat()
    }

    fun getScreenHeight(context: Context): Float {
        val localDisplayMetrics = DisplayMetrics()
        (context as Activity).windowManager.defaultDisplay.getMetrics(localDisplayMetrics)
        return localDisplayMetrics.heightPixels.toFloat()

    }

    fun getStatusBarHeight(context: Context): Float {
        var c: Class<*>? = null
        var obj: Any? = null
        var field: Field? = null
        var x = 0
        var statusBarHeight = 0f
        try {
            c = Class.forName("com.android.internal.R\$dimen")
            obj = c.newInstance()
            field = c.getField("status_bar_height")
            x = field[obj].toString().toInt()
            statusBarHeight = context.resources.getDimensionPixelSize(x).toFloat()
        } catch (e1: Exception) {
            e1.printStackTrace()
        }
        return statusBarHeight
    }

    fun dp2px(dpValue: Float): Float {
        return (0.5f + dpValue * Resources.getSystem().displayMetrics.density)
    }

}
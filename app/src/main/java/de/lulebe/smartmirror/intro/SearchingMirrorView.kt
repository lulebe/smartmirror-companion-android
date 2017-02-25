package de.lulebe.smartmirror.intro

import android.content.Context
import android.graphics.*
import android.util.TypedValue
import android.view.View


/*
Draw static mirror icon on left third
Draw 3 animating dots on middle third
Draw static phone icon on right third
 */

class SearchingMirrorView(context: Context): View(context) {

    private var pic: Picture? = null
    private var needsRedraw = true
    private var animationPosition = 0F
    private var animatingForward = true

    //Paints
    private val mirrorPaint = Paint(Paint.ANTI_ALIAS_FLAG)
    private val phonePaint = Paint(Paint.ANTI_ALIAS_FLAG)
    private val fillPaint = Paint(Paint.ANTI_ALIAS_FLAG)

    private val dp8 = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 8F, context.resources.displayMetrics)

    init {
        mirrorPaint.strokeWidth = dp8
        mirrorPaint.style = Paint.Style.STROKE
        mirrorPaint.color = Color.parseColor("#333333")

        phonePaint.strokeWidth = dp8
        phonePaint.style = Paint.Style.STROKE
        phonePaint.color = Color.parseColor("#333333")

        fillPaint.style = Paint.Style.FILL
        fillPaint.color = Color.WHITE
    }

    override fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int) {
        super.onLayout(changed, left, top, right, bottom)
        if (!changed) return
        needsRedraw = true
        invalidate()
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        if (pic != null && !needsRedraw)
            pic?.draw(canvas)
        else
            drawStatic(canvas)
        drawAnimation(canvas)
        invalidate()
    }

    private fun drawAnimation (canvas: Canvas) {
        //draw animating dots
        val dim = Math.min(width, height)
        val radius = dim.toFloat() / 18F - dp8
        val centerY = dim.toFloat() / 2F
        val centerX1 = (dim.toFloat() / 3F) + (dim.toFloat() / 18F)
        val centerX2 = (dim.toFloat() / 3F) + (dim.toFloat() / 18F * 3)
        val centerX3 = (dim.toFloat() / 3F) + (dim.toFloat() / 18F * 5)
        fillPaint.alpha = (animationPosition * 255).toInt()
        canvas.drawCircle(centerX1, centerY, radius, fillPaint)
        if (animationPosition <= 0.5F)
            fillPaint.alpha = ((0.5F + animationPosition) * 255).toInt()
        else
            fillPaint.alpha = ((0.5F + (1F - animationPosition)) * 255).toInt()
        canvas.drawCircle(centerX2, centerY, radius, fillPaint)
        fillPaint.alpha = ((1F - animationPosition) * 255).toInt()
        canvas.drawCircle(centerX3, centerY, radius, fillPaint)
        fillPaint.alpha = 255
        //update animationPosition
        if (animatingForward)
            if (animationPosition < 0.98F)
                animationPosition += 0.02F
            else
                animatingForward = false
        else
            if (animationPosition > 0.02F)
                animationPosition -= 0.02F
            else
                animatingForward = true
    }

    private fun drawStatic (canvas: Canvas) {
        val newPic = Picture()
        val dim = Math.min(width, height)
        val picCanvas = newPic.beginRecording(dim, dim)
        //1. Draw Mirror
        val mirrorCenterX = dim.toFloat() / 6F
        val mirrorCenterY = dim.toFloat() / 2F
        val mirrorRadius = dim.toFloat() / 6F - dp8
        picCanvas.drawCircle(mirrorCenterX, mirrorCenterY, mirrorRadius, fillPaint)
        picCanvas.drawCircle(mirrorCenterX, mirrorCenterY, mirrorRadius, mirrorPaint)
        //2. Draw Phone
        val phoneWidth = (dim.toFloat() / 3) - (dp8 * 2)
        val phoneHeight = phoneWidth * 1.5F
        val phoneRect = RectF(
                dim.toFloat() / 3 * 2 + dp8,
                (dim.toFloat() / 2) - (phoneHeight / 2),
                dim.toFloat() - dp8,
                (dim.toFloat() / 2) + (phoneHeight / 2)
        )
        picCanvas.drawRoundRect(phoneRect, dp8, dp8, fillPaint)
        picCanvas.drawRoundRect(phoneRect, dp8, dp8, phonePaint)
        val bmpSize = (phoneRect.width() / 2F).toInt()
        val androidBmp = Bitmap.createScaledBitmap(BitmapFactory.decodeStream(context.assets.open("android.png")), bmpSize, bmpSize, false)
        picCanvas.drawBitmap(
                androidBmp,
                phoneRect.centerX() - (bmpSize / 2F),
                phoneRect.centerY() - (bmpSize / 2F),
                Paint(Paint.ANTI_ALIAS_FLAG)
        )
        newPic.endRecording()
        newPic.draw(canvas)
        pic = newPic
        needsRedraw = false
    }

}
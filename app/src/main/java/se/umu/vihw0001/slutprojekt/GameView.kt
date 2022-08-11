package se.umu.vihw0001.slutprojekt

import android.content.Context
import android.content.res.Resources
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.drawable.ShapeDrawable
import android.graphics.drawable.shapes.RectShape
import android.util.AttributeSet
import android.view.View
import androidx.core.content.res.ResourcesCompat

class GameView(context: Context, attributeSet: AttributeSet) : View(context, attributeSet) {
    private lateinit var extraCanvas: Canvas
    private lateinit var extraBitmap: Bitmap
    private lateinit var viewModel: GameViewModel

    private val backgroundColor = ResourcesCompat.getColor(resources, R.color.teal_200, null)

    private var screenWidth = 0
    private var screenHeight = 0

    private val baseWidth = 1240f
    private val baseHeight = 720f

    fun setUp(viewModel: GameViewModel) {
        this.viewModel = viewModel
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)

        if (::extraBitmap.isInitialized) extraBitmap.recycle()

        extraBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        extraCanvas = Canvas(extraBitmap)
        extraCanvas.drawColor(backgroundColor)

        screenWidth = Resources.getSystem().displayMetrics.widthPixels
        screenHeight = Resources.getSystem().displayMetrics.heightPixels
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)



        canvas.drawBitmap(extraBitmap, 0f, 0f, null)

        drawObstacles(canvas)

    }

    private fun drawObstacles(canvas: Canvas) {
        val obstacles = viewModel.getObstacles()

        for (obs in obstacles) {
            val shapeDrawable: ShapeDrawable

            val left = (obs.xLeft * (screenWidth.toFloat() / baseWidth)).toInt()
            val top = (obs.yTop * (screenHeight.toFloat() / baseHeight)).toInt()
            val right = (obs.xRight * (screenWidth.toFloat() / baseWidth)).toInt()
            val bottom = (obs.yBottom * (screenHeight.toFloat() / baseHeight)).toInt()

            shapeDrawable = ShapeDrawable(RectShape())
            shapeDrawable.setBounds( left, top, right, bottom)
            shapeDrawable.getPaint().setColor(Color.parseColor("#009944"))
            shapeDrawable.draw(canvas)
        }
    }
}
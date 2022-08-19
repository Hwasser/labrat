package se.umu.vihw0001.slutprojekt

import android.R
import android.R.attr
import android.content.Context
import android.content.res.Resources
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Matrix
import android.graphics.drawable.Drawable
import android.graphics.drawable.ShapeDrawable
import android.graphics.drawable.shapes.RectShape
import android.util.AttributeSet
import android.util.Log
import android.view.View
import androidx.core.content.res.ResourcesCompat


class GameView(context: Context, attributeSet: AttributeSet) : View(context, attributeSet) {
    private lateinit var backgroundCanvas: Canvas
    private lateinit var backgroundBitmap: Bitmap
    private lateinit var playerCanvas: Canvas
    private lateinit var playerBitmap: Bitmap
    private lateinit var viewModel: GameViewModel
    private lateinit var player: Drawable
    private lateinit var cheese: Drawable

    private var playerPositionX = 0f
    private var playerPositionY = 0f
    private var screenWidth = 0
    private var screenHeight = 0
    private var actionBarHeight = 0
    private var hasInit = false
    var showActionBar = true

    private val baseWidth = 1920f
    private val baseHeight = 1080f

    var rotationMatrix = Matrix() // For being able to rotate the player bitmap

    fun setUp(viewModel: GameViewModel) {
        this.viewModel = viewModel

        // Get drawable of player
        val playerDrawable = context.resources.getIdentifier("player", "drawable", context.packageName)
        player = resources.getDrawable(playerDrawable, null)
        val cheeseDrawable = context.resources.getIdentifier("cheese", "drawable", context.packageName)
        cheese = resources.getDrawable(cheeseDrawable, null)
        movePlayer(viewModel.getPlayerPosition(), viewModel.getPlayerRotation())
    }

    fun movePlayer(position: Coordinates, rotation: Float) {
        if (!hasInit)
            return

        val playerSize = 80f

        if (::playerBitmap.isInitialized) playerBitmap.recycle()
        playerBitmap = Bitmap.createBitmap(80, 80, Bitmap.Config.ARGB_8888)
        playerCanvas = Canvas(playerBitmap)

        // Set rotation matrix for rotating bitmap
        rotationMatrix.setRotate(rotation, playerSize * 0.5f, playerSize * 0.5f)
        // Adjust position after rotation is done
        rotationMatrix.postTranslate(
            playerCanvas.width.toFloat()  - playerSize + playerPositionX,
            playerCanvas.height.toFloat() - playerSize + playerPositionY)
        // Get size and position of player bitmap
        player.setBounds(0,0,80,80)
        playerPositionX = position.x
        playerPositionY = position.y
        // Redraw screen
        invalidate()

    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)

        // Remove old background bitmap and player bitmap from memory
        if (::backgroundBitmap.isInitialized) backgroundBitmap.recycle()
        if (::playerBitmap.isInitialized) playerBitmap.recycle()

        // Draw background on background canvas, this way we can ensure the whole
        // screen gets the background, no matter the scaling
        val backgroundColor = Color.parseColor("#31bd56")
        backgroundBitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888)
        backgroundCanvas = Canvas(backgroundBitmap)
        backgroundCanvas.drawColor(backgroundColor)

        // Draw player of its own canvas to allow rotation
        playerBitmap = Bitmap.createBitmap(80, 80, Bitmap.Config.ARGB_8888)
        playerCanvas = Canvas(playerBitmap)

        // Get the resolution of the screen
        screenWidth = Resources.getSystem().displayMetrics.widthPixels
        screenHeight = Resources.getSystem().displayMetrics.heightPixels

        // Get the size of the action bar
        val actionBarOffset = 16
        actionBarHeight =
            (getResources().getDimensionPixelSize(androidx.appcompat.R.dimen.abc_action_bar_default_height_material)
            + actionBarOffset)

        hasInit = true
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        // Draw background
        canvas.drawBitmap(backgroundBitmap, 0f, 0f, null)
        // For scaling the canvas according to phone screen
        val actionBarValue = if (showActionBar) actionBarHeight else 0
        val widthModifier = screenWidth.toFloat() / baseWidth
        val heightModifier = (screenHeight.toFloat() - actionBarValue) / baseHeight
        canvas.scale(widthModifier, heightModifier)
        // Draw level
        drawObstacles(canvas)
        drawTraps(canvas)
        drawCheese(canvas)
        // Draw player
        player.draw(playerCanvas)
        canvas.drawBitmap(playerBitmap, rotationMatrix, null)
    }

    // TODO: Rename xLeft into left

    private fun drawObstacles(canvas: Canvas) {
        val obstacles = viewModel.level.obstacles

        for (obj in obstacles) {
            val shapeDrawable: ShapeDrawable
            shapeDrawable = ShapeDrawable(RectShape())
            shapeDrawable.setBounds(obj.xLeft, obj.yTop, obj.xRight, obj.yBottom)
            shapeDrawable.getPaint().setColor(Color.parseColor("#384230"))
            shapeDrawable.draw(canvas)
        }
    }

    private fun drawTraps(canvas: Canvas) {
        val trapDrawable = context.resources.getIdentifier("trap", "drawable", context.packageName)

        val traps = viewModel.level.traps

        for (obj in traps) {
            val trap = resources.getDrawable(trapDrawable, null)
            trap.setBounds( obj.xLeft, obj.yTop, obj.xRight, obj.yBottom)
            trap.draw(canvas)
        }
    }

    private fun drawCheese(canvas: Canvas) {
        val cheeseObject = viewModel.level.cheese
        cheese.setBounds(cheeseObject.xLeft, cheeseObject.yTop, cheeseObject.xRight, cheeseObject.yBottom)
        cheese.draw(canvas)
    }
}
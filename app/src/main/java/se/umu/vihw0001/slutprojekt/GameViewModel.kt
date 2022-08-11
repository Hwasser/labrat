package se.umu.vihw0001.slutprojekt

import android.util.Log
import android.view.View
import androidx.lifecycle.ViewModel

private const val TAG = "ViewModel"

class GameViewModel: ViewModel() {
    private val obstacles: MutableList<Obstacle> = mutableListOf()
    private val traps: MutableList<Trap> = mutableListOf()

    private lateinit var player: Player

    fun startGame(playerData: Player) {
        player = playerData
    }

    fun updateEvent(horizontalTilt: Float, verticalTilt: Float, screenWidth: Int, screenHeight: Int) {
        player.movePlayer(
            horizontalTilt,
            verticalTilt,
            screenWidth,
            screenHeight,
            getObstacles()
        )

        if (player.collisionTrap(getTraps()))
            playerDies()
    }

    fun getPlayerPosition() = player.position
    fun getPlayerRotation() = player.rotation

    fun setUpObstacles(objectsFromView: Sequence<View>) {
        obstacles.clear()
        for (obj in objectsFromView) {
            val stringId = obj.resources.getResourceName(obj.id)
            if (stringId.contains("wall")) run {
                // Measure the bounds of each obstacle
                val obstacleObject = Obstacle(
                    obj.width,
                    obj.height,
                    obj.translationX.toInt(),
                    obj.translationY.toInt(),
                    stringId
                )
                obstacles.add(obstacleObject)
            }
        }
        Log.d(TAG, "obstacles: ${obstacles}")
    }

    fun setUpTraps(objectsFromView: Sequence<View>) {
        traps.clear()
        for (obj in objectsFromView) {
            val stringId = obj.resources.getResourceName(obj.id)
            Log.d(TAG, "obstacle id: ${stringId}")
            if (stringId.contains("trap")) run {
                // Measure the bounds of each obstacle
                val trapObject = Trap(
                    obj.width,
                    obj.height,
                    obj.translationX.toInt(),
                    obj.translationY.toInt(),
                    stringId
                )
                traps.add(trapObject)
            }
        }
        Log.d(TAG, "traps: ${traps}")
    }

    fun getObstacles() = obstacles
    fun getTraps() = traps

    fun playerDies() {
        player.resetPosition()
        // TODO: And more
    }

    fun setSizeModifiers(widthModifier: Float, heightModifier: Float) {
        player.widthModifier  = widthModifier
        player.heightModifier = heightModifier
    }
}
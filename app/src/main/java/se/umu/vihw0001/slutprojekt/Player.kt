package se.umu.vihw0001.slutprojekt

import android.os.Debug
import android.util.Log
import java.lang.Math.atan2
import java.lang.Math.round
import kotlin.math.PI
import kotlin.math.abs

// TODO: Add modifier for all kinds of collisions

class Player(xPosition: Int, yPosition: Int) {
    val position = Coordinates(xPosition.toFloat(), yPosition.toFloat())
    val startPosition = Coordinates(xPosition.toFloat(), yPosition.toFloat())
    var rotation = 0.0f
    var widthModifier = 1.0f
    var heightModifier = 1.0f

    fun movePlayer(xMove: Float, yMove: Float, screenWidth: Int, screenHeight: Int, obstacles: List<Obstacle>) {
        // TODO: Change magic number to what user choose in settings
        val minValue = 0.1f

        val xMoveAdjusted: Float = if (abs(xMove) < minValue) 0f else xMove
        val yMoveAdjusted: Float = if (abs(yMove) < minValue) 0f else yMove

        rotatePlayer(xMoveAdjusted, yMoveAdjusted)

        val newPosX = position.x + yMoveAdjusted * 0.5f
        val newPosY = position.y + xMoveAdjusted * 0.5f

        //if (!horizontalScreenCollision(newPosX, screenWidth) && !horizontalObstacleCollision(newPosX, newPosY, obstacles))
            position.x = newPosX

        //if (!verticalScreenCollision(newPosY, screenHeight) && !verticalObstacleCollision(newPosX, newPosY, obstacles))
            position.y = newPosY
    }

    private fun rotatePlayer(xMove: Float, yMove: Float) {
        val minValue = 0.2f // Min value of a rotation
        var sensitivity = 10.0f // How sensitive rotation should be to change

        // If the player is barely moving, don't rotate it
        if (abs(xMove) < minValue && abs(yMove) < minValue)
            return

        // Make sesitivity value higher if acceleration is really low
        val threshold = 0.75f
        if (abs(xMove) < threshold && abs(yMove) < threshold)
            sensitivity /= ((abs(xMove) + abs(yMove)) / 2)

        // Calculate rotation in radians from acceleration
        val rotationInRadians = atan2((-yMove).toDouble(), (xMove).toDouble())
        // Convert radians to degrees
        val rotationDegrees = (rotationInRadians * (180 / PI)).toFloat()
        // Don't rotate player if the change in direction is minimal
        if (abs(rotation - rotationDegrees) > sensitivity)
            rotation = rotationDegrees
    }

    private fun horizontalScreenCollision(position: Float, screenWidth: Int): Boolean {
        val offset = 52 / widthModifier
        val revOffset = 4 / widthModifier
        // Check if the bounds of the screen (play field) is hit
        if (position < -revOffset || position > screenWidth - offset)
            return true

        return false
    }

    private fun verticalScreenCollision(position: Float, screenHeight: Int): Boolean {
        val offset = 52 * heightModifier
        val revOffset = 4 / widthModifier
        // Check if the bounds of the screen (play field) is hit
        if (position < -revOffset || position > screenHeight - offset)
            return true

        return false
    }

    private fun horizontalObstacleCollision(posX: Float, posY: Float, obstacles: List<Obstacle>): Boolean {
        for (obstacle in obstacles) {
            if (obstacle.horizontalCollision(posX, posY, position, widthModifier, heightModifier))
                return true
        }
        return false
    }
    private fun verticalObstacleCollision(posX: Float, posY: Float, obstacles: List<Obstacle>): Boolean {
        for (obstacle in obstacles) {
            if (obstacle.verticalCollision(posX, posY, position, widthModifier, heightModifier))
                return true
        }
        return false
    }

    fun collisionTrap(traps: List<Trap>): Boolean {
        for (trap in traps) {
            if (trap.collision(position, widthModifier, heightModifier))
                return true
        }
        return false
    }

    fun resetPosition() {
        position.x = startPosition.x
        position.y = startPosition.y
    }

    fun setSizeModifiers(widthMod: Float, heightMod: Float) {
        widthModifier  = widthMod
        heightModifier = heightMod
    }

}
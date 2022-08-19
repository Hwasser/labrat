package se.umu.vihw0001.slutprojekt

import android.os.Debug
import android.util.Log
import java.lang.Math.atan2
import java.lang.Math.round
import kotlin.math.PI
import kotlin.math.abs
import kotlin.math.sign

const val MAX_SPEED = 6.66f

class Player(var position: Coordinates, val settings: Settings) {
    val startPosition = Coordinates(position.x, position.y)
    var rotation = 0.0f

    fun movePlayer(xMove: Float, yMove: Float, obstacles: List<Obstacle>) {
        val minValue = 0.1f

        // Stops the movement of an axis if speed is below minValue
        val xSpeedMin = if (abs(xMove) < minValue) 0f else xMove * settings.xSpeedModifier
        val ySpeedMin = if (abs(yMove) < minValue) 0f else yMove * settings.ySpeedModifier
        // Puts a maximum restriction on speed
        val xSpeedMax = if (abs(xSpeedMin) > MAX_SPEED) MAX_SPEED * sign(xMove) else xSpeedMin
        val ySpeedMax = if (abs(ySpeedMin) > MAX_SPEED) MAX_SPEED * sign(yMove) else ySpeedMin

        rotatePlayer(xSpeedMax * settings.xDirection, ySpeedMax * settings.yDirection)

        // The next possible position of the player
        val newPosX = position.x + xSpeedMax * settings.xDirection
        val newPosY = position.y + ySpeedMax * settings.yDirection

        // checks if newPosX is colliding with the frame of the level or an obstacle
        if (!horizontalScreenCollision(newPosX) && !horizontalObstacleCollision(newPosX, newPosY, obstacles))
            position.x = newPosX
        // checks if newPosY is colliding with the frame of the level or an obstacle
        if (!verticalScreenCollision(newPosY) && !verticalObstacleCollision(newPosX, newPosY, obstacles))
            position.y = newPosY
    }

    /**
     * Sets the rotation of the player according to its movements.
     *
     * @param xMove Movement of the x-axis.
     * @param yMove Movement of the y-axis.
     */
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
        val rotationInRadians = atan2((-xMove).toDouble(), (yMove).toDouble())
        // Convert radians to degrees
        val rotationDegrees = (rotationInRadians * (180 / PI)).toFloat()
        // Don't rotate player if the change in direction is minimal
        if (abs(rotation - rotationDegrees) > sensitivity)
            rotation = rotationDegrees
    }

    /**
     * Checks if a position is colliding with the y-axis of the frame of the level.
     *
     * @param position A position of the y-axis to check.
     */
    private fun horizontalScreenCollision(position: Float): Boolean {
        val screenWidth = 1920

        val offset = 70
        val revOffset = 10
        // Check if the bounds of the screen (play field) is hit
        if (position < -revOffset || position > screenWidth - offset)
            return true

        return false
    }

    /**
     * Checks if a position is colliding with the x-axis of the frame of the level.
     *
     * @param position A position of the x-axis to check.
     */
    private fun verticalScreenCollision(position: Float): Boolean {
        val screenHeight = 1080

        val offset = 70
        val revOffset = 10
        // Check if the bounds of the screen (play field) is hit
        if (position < -revOffset || position > screenHeight - offset)
            return true

        return false
    }

    /**
     * Checks if a position of the x-axis is colliding with an obstacle (wall) in the level
     *
     * @param posX The next position of the x-axis of the player.
     * @param posY The next position of the y-axis of the player.
     * @param obstacles A list containing all obstacles of the current level.
     */
    private fun horizontalObstacleCollision(posX: Float, posY: Float, obstacles: List<Obstacle>): Boolean {
        // Check if any of the obstacles is currently colliding with the player position
        for (obstacle in obstacles) {
            if (obstacle.horizontalCollision(posX, posY, position))
                return true
        }
        return false
    }

    /**
     * Checks if a position of the y-axis is colliding with an obstacle (wall) in the level
     *
     * @param posX The next position of the x-axis of the player.
     * @param posY The next position of the y-axis of the player.
     * @param obstacles A list containing all obstacles of the current level.
     */
    private fun verticalObstacleCollision(posX: Float, posY: Float, obstacles: List<Obstacle>): Boolean {
        for (obstacle in obstacles) {
            if (obstacle.verticalCollision(posX, posY, position))
                return true
        }
        return false
    }

    /**
     * Checks if the current position of the player is colliding with a mouse trap.
     *
     * @param traps A list containing all traps of the current level.
     */
    fun collisionTrap(traps: List<Trap>): Boolean {
        for (trap in traps) {
            if (trap.collision(position))
                return true
        }
        return false
    }

    /**
     * Checks if the current position of the player is colliding with a cheese.
     *
     * @param cheese Contains a cheese-object.
     */
    fun collisionCheese(cheese: Cheese) = cheese.collision(position)

    /**
     * Resets the player position to the start position.
     */
    fun resetPosition() {
        position.x = startPosition.x
        position.y = startPosition.y
    }

}
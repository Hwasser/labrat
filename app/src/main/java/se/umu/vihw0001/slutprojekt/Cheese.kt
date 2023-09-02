package se.umu.vihw0001.slutprojekt

/**
 * Cheese class - the cheese is an object which takes the player to the next level
 */
class Cheese(xPos: Int, yPos: Int) {
    private val objectSize = 80

    // The position of all sides of the object inside the level
    val xLeft  = xPos * GRID_SIZE
    val xRight = xPos * GRID_SIZE + objectSize
    val yTop   = yPos * GRID_SIZE
    val yBottom= yPos * GRID_SIZE + objectSize

    /**
     * If a position point is colliding with the cheese.
     *
     * @param position A coordinate point
     * @return Whether a collision has occured
     */
    fun collision(position: Coordinates): Boolean {
        val offset = 70
        val revOffset = 10 // reversed offset from the other side of the collision
        if (position.x > xLeft - offset
            && position.x < xRight - revOffset
            && position.y > yTop - offset
            && position.y < yBottom - revOffset)
            return true

        return false
    }
}
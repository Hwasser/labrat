package se.umu.vihw0001.slutprojekt

/**
 * An obstacle (wall) which stops player movement inside the room.
 */
class Obstacle(xSize: Int, ySize: Int, xPos: Int, yPos: Int) {

    // The position of all sides of the object inside the level
    val xLeft  = xPos * GRID_SIZE
    val xRight = (xPos + xSize) * GRID_SIZE
    val yTop   = yPos * GRID_SIZE
    val yBottom= (yPos + ySize) * GRID_SIZE

    /**
     * If a horizontal collision occurs with the obstacle at a position point.
     *
     * @param posX A position point in the x-axis
     * @param posY A position point in the y-axis
     * @return Whether a collision has occured
     */
    fun horizontalCollision(posX: Float, posY: Float, position: Coordinates): Boolean {
        val offset = 70
        val revOffset = 10
        if (((posX > (xLeft - offset) && position.x <= (xLeft - offset))
            || (posX < (xRight - revOffset)) && position.x >= xRight - revOffset)
            && (posY > (yTop - offset) && posY < (yBottom - revOffset)))
            return true

        return false
    }

    /**
     * If a vertical collision occurs with the obstacle at a position point.
     *
     * @param posX A position point in the x-axis
     * @param posY A position point in the y-axis
     * @return Whether a collision has occured
     */
    fun verticalCollision(posX: Float, posY: Float, position: Coordinates): Boolean {
        val offset  = 70
        val revOffset = 10
        if ((posX > (xLeft - offset) && posX < (xRight - revOffset))
            && ((posY > (yTop - offset) && position.y <= (yTop - offset))
            || (posY < (yBottom - revOffset) && position.y >= (yBottom - revOffset))))
            return true

        return false
    }
}
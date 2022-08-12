package se.umu.vihw0001.slutprojekt

// TODO: Document class and functions
class Obstacle(xSize: Int, ySize: Int, xPos: Int, yPos: Int) {
    val gridSize = 40

    val xLeft  = xPos * gridSize
    val xRight = (xPos + xSize) * gridSize
    val yTop   = yPos * gridSize
    val yBottom= (yPos + ySize) * gridSize

    fun horizontalCollision(posX: Float, posY: Float, position: Coordinates): Boolean {
        val offset = 70
        val revOffset = 10
        if (((posX > (xLeft - offset) && position.x <= (xLeft - offset))
            || (posX < (xRight - revOffset)) && position.x >= xRight - revOffset)
            && (posY > (yTop - offset) && posY < (yBottom - revOffset)))
            return true

        return false
    }

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
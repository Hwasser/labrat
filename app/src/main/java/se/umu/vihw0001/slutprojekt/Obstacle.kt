package se.umu.vihw0001.slutprojekt

// TODO: Document class and functions
class Obstacle(xSize: Int, ySize: Int, xPos: Int, yPos: Int, id: String) {
    val xLeft  = xPos
    val xRight = xPos + xSize
    val yTop   = yPos
    val yBottom= yPos + ySize
    val objId = id

    fun horizontalCollision(posX: Float, posY: Float, position: Coordinates, widthModifier: Float, heightModifier: Float): Boolean {
        val offset = 52
        val revOffset = 4
        val topOffset = 52 - 52 * (1 - heightModifier)
        if (((posX > (xLeft - offset) && position.x <= (xLeft - offset))
            || (posX < (xRight - revOffset)) && position.x >= xRight - revOffset)
            && (posY > (yTop - topOffset) && posY < (yBottom - revOffset))
        )
            return true

        return false
    }

    fun verticalCollision(posX: Float, posY: Float, position: Coordinates, widthModifier: Float, heightModifier: Float): Boolean {
        val leftOffset  = 52
        val rightOffset = 4
        val topOffset   = 52 - 52 * (1 - heightModifier)
        val bottomOffset= 4
        if ((posX > (xLeft - leftOffset) && posX < (xRight - rightOffset))
            && ((posY > (yTop - topOffset) && position.y <= (yTop - topOffset))
            || (posY < (yBottom - bottomOffset) && position.y >= (yBottom - bottomOffset)))
        )
            return true

        return false
    }
}
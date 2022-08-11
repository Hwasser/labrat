package se.umu.vihw0001.slutprojekt

class Trap(xSize: Int, ySize: Int, xPos: Int, yPos: Int, id: String) {
    val xLeft  = xPos
    val xRight = xPos + xSize
    val yTop   = yPos
    val yBottom= yPos + ySize
    val objId = id

    fun collision(position: Coordinates, widthModifier: Float, heightModifier: Float): Boolean {
        val offset = 48
        val revOffset = 8 // reversed offset from the other side of the collision
        val topOffset   = 48 - 48 * (1 - heightModifier)
        if (position.x > xLeft - offset / widthModifier
            && position.x < xRight - revOffset / widthModifier
            && position.y > yTop - topOffset / heightModifier
            && position.y < yBottom - revOffset / heightModifier)
            return true

        return false
    }
}
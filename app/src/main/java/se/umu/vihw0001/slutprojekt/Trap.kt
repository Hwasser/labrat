package se.umu.vihw0001.slutprojekt

class Trap(xPos: Int, yPos: Int) {
    private val gridSize = 40
    private val objectSize = 80

    val xLeft  = xPos * gridSize
    val xRight = xPos * gridSize + objectSize
    val yTop   = yPos * gridSize
    val yBottom= yPos * gridSize + objectSize

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
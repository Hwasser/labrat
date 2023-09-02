package se.umu.vihw0001.slutprojekt

/**
 * Data class containing game settings
 * @param xSpeedModifier Fine adjustment of the x-axis movement speed of the player
 * @param ySpeedModifier Fine adjustment of the y-axis movement speed of the player
 * @param xDirection Direction of the x-axis, can be reversed
 * @param yDirection Direction of the y-axis, can be reversed
 */
data class Settings(val xSpeedModifier: Float, val ySpeedModifier: Float, val xDirection: Int, val yDirection: Int)
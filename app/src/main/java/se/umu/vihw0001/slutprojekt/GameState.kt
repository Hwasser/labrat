package se.umu.vihw0001.slutprojekt

data class GameState(
    val playerPosition: Coordinates,
    val timeLeft: Long,
    val level: Int,
    val timeLeftLast: Long,
    val onGoingGame: Boolean)
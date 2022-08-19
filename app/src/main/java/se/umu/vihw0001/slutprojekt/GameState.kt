package se.umu.vihw0001.slutprojekt

/**
 * Data of the current game state, for storing and retrieving game states.
 * @param playerPosition The current player position.
 * @param timeLeft Time left of the level.
 * @param level Which level the player is currently playing.
 * @param timeLeftLast How much time was left in the last level.
 * @param onGoingGame If a game is currently being played.
 */
data class GameState(
    val playerPosition: Coordinates,
    val timeLeft: Long,
    val level: Int,
    val timeLeftLast: Long,
    val onGoingGame: Boolean)
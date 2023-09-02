package se.umu.vihw0001.slutprojekt

import androidx.lifecycle.ViewModel

class GameViewModel: ViewModel() {
    lateinit var level: Level // Which the level we are currently plaing
    private lateinit var player: Player // THe player object

    /**
     * To call other classes if the player wins or dies.
     */
    interface Callbacks {
        fun playerDies()
        fun playerWins()
    }

    private var callbacks: Callbacks? = null

    /**
     * Attach callbacks to other fragments.
     */
    fun attachCallbacks(fragment: GameFragment) {
        callbacks = fragment
    }

    /**
     * This function contains basic game information that has to be set up before a game can start.
     *
     * @param startLevel Which level to start.
     * @param playerPosition The starting position of the player.
     * @param newGame Whether this is a new game or an ongoing game.
     * @param settings The game settings.
     */
    fun startGame(startLevel: Int, playerPosition: Coordinates, newGame: Boolean, settings: Settings) {
        level = Level(startLevel) // Always start at first level
        player = Player(level.startPosition, settings)
        // If we are return to a game, move player to recent position
        if (!newGame)
            player.position = playerPosition
    }

    /**
     * Check all game events at each change of the accelerometer.
     *
     * @param horizontalTilt The horizontal tilt of the phone.
     * @param verticalTilt The vertical tilt of the phone.
     */
    fun updateEvent(horizontalTilt: Float, verticalTilt: Float) {
        player.movePlayer(
            horizontalTilt,
            verticalTilt,
            level.obstacles
        )

        if (player.collisionTrap(level.traps))
            playerDies()

        if (player.collisionCheese(level.cheese))
            playerWins()
    }

    /**
     * Fetch the current player position
     */
    fun getPlayerPosition() = player.position

    /**
     * Fetch the current player rotation
     */
    fun getPlayerRotation() = player.rotation

    /**
     * When the player dies, run callback function and reset the player position.
     */
    private fun playerDies() {
        callbacks?.playerDies()
        player.resetPosition()
    }

    /**
     * When the player wins, run callback function and reset the player position.
     */
    private fun playerWins() {
        callbacks?.playerWins()
        player.resetPosition()
    }
}
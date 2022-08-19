package se.umu.vihw0001.slutprojekt

import android.content.Context
import android.util.Log
import android.view.View
import androidx.lifecycle.ViewModel

private const val TAG = "ViewModel"

class GameViewModel: ViewModel() {
    lateinit var level: Level // Which the level we are currently plaing
    private lateinit var player: Player // THe player object
    var highscore = 0 // The top highscore, this is used to check whether the player has beaten it

    interface Callbacks {
        fun playerDies()
        fun playerWins()
    }

    private var callbacks: Callbacks? = null

    fun attachCallbacks(fragment: GameFragment) {
        callbacks = fragment
    }

    /**
     * This function contains basic game information that has to be set up before a game can start
     */
    fun startGame(startLevel: Int, playerPosition: Coordinates, newGame: Boolean, settings: Settings) {
        level = Level(startLevel) // Always start at first level
        if (newGame)
            player = Player(level.startPosition, settings)
        else
            player = Player(playerPosition, settings)
    }

    /**
     * Update the game state at each change of the accelerometer
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
     * Resets the player position. When the player dies, the game waits for the game view
     * to response to reset parts of the view, then it runs this function.
     */
    private fun playerDies() {
        callbacks?.playerDies()
        player.resetPosition()
    }

    private fun playerWins() {
        callbacks?.playerWins()
        player.resetPosition()
    }
}
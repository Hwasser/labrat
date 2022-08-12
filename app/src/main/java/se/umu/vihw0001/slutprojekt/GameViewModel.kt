package se.umu.vihw0001.slutprojekt

import android.util.Log
import android.view.View
import androidx.lifecycle.ViewModel

private const val TAG = "ViewModel"

class GameViewModel: ViewModel() {
    lateinit var level: Level
    private lateinit var player: Player

    fun startGame() {
        level = Level(1)
        player = Player(level.startPosition)
    }

    fun updateEvent(horizontalTilt: Float, verticalTilt: Float) {
        player.movePlayer(
            horizontalTilt,
            verticalTilt,
            level.obstacles
        )

        if (player.collisionTrap(level.traps))
            playerDies()
    }

    fun getPlayerPosition() = player.position
    fun getPlayerRotation() = player.rotation

    fun playerDies() {
        player.resetPosition()
        // TODO: And more
    }
}
package se.umu.vihw0001.slutprojekt

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.WindowManager
import androidx.fragment.app.Fragment

const val DEFAULT_LEVEL_TIME: Long = 2 * 60 * 1000 // Two minutes
const val DEFAULT_FIRST_LEVEL: Int = 1

class MainActivity : AppCompatActivity(){
    lateinit var fragment: Fragment
    lateinit var highscore: Highscore
    var gameState: GameState? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Force screen to be active through the whole game
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        // Makes the game full screen
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN)

        // If no game state exist, create a clean one
        if (savedInstanceState == null) {
            Log.d("MainActivity", "TESTING onCreate -> bundle doesnt exist ")
            cleanGameState()
        }

        // Set up highscore in main activity since it will follow through all fragments
        highscore = Highscore(this)
        // Import highscore from local storage
        highscore.importHighscore()

        // Set up fragment to go to
        setUpFragment()
    }

    private fun setUpFragment() {
        // If there are no fragments in stack, go to menu
        if (supportFragmentManager.fragments.isEmpty()) {
            val fragment = MenuFragment()
            supportFragmentManager
                .beginTransaction()
                .add(R.id.fragment_container, fragment)
                .commit()
        } else {
            // If there are a fragment, go to that fragment
            val fragment: Fragment = supportFragmentManager.fragments.last()

            supportFragmentManager
                .beginTransaction()
                .replace(R.id.fragment_container, fragment)
                .commit()
        }
    }

    override fun onStop() {
        onSaveInstanceState(Bundle())
        highscore.exportHighscore()

        super.onStop()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        outState.putInt("current_level", gameState?.level ?: DEFAULT_FIRST_LEVEL)
        outState.putFloat("player_position_x", gameState?.playerPosition?.x ?: 0f)
        outState.putFloat("player_position_y", gameState?.playerPosition?.y ?: 0f)
        outState.putLong("time_left", gameState?.timeLeft ?: DEFAULT_LEVEL_TIME)
        outState.putBoolean("on_going_game", gameState?.onGoingGame ?: false)

        Log.d("MainActivity", "TESTING onSaveInstanceState: ")

        super.onSaveInstanceState(outState)
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        val currentLevel = savedInstanceState.getInt("current_level")
        val playerPositionX = savedInstanceState.getFloat("player_position_x")
        val playerPositionY = savedInstanceState.getFloat("player_position_y")
        val timeLeft = savedInstanceState.getLong("time_left")
        val onGoingGame = savedInstanceState.getBoolean("on_going_game")

        gameState = GameState(
            Coordinates(playerPositionX, playerPositionY),
            timeLeft,
            currentLevel,
            onGoingGame
        )

        Log.d("MainActivity", "TESTING onRestoreInstanceState: ")

        super.onRestoreInstanceState(savedInstanceState)
    }

    private fun cleanGameState() {
        gameState = GameState(
            Coordinates(0f, 0f),
            DEFAULT_LEVEL_TIME,
            DEFAULT_FIRST_LEVEL,
            false
        )
    }
}
package se.umu.vihw0001.slutprojekt

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.WindowManager
import androidx.fragment.app.Fragment

const val DEFAULT_LEVEL_TIME: Long = 2 * 60 * 1000 // Two minutes
const val DEFAULT_FIRST_LEVEL: Int = 1
const val NUMBER_OF_LEVELS: Int = 2
const val DEFAULT_SPEED = 1.5f

class MainActivity : AppCompatActivity(){
    lateinit var highscore: Highscore
    var gameState: GameState? = null
    var settings: Settings? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Force screen to be active through the whole game
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        // Makes the game full screen
        this.window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN)

        // If no saved state exist, create a clean game state and default game settings
        if (savedInstanceState == null) {
            cleanGameState()
            defaultSettings()
        }

        // Set up highscore in main activity since it will follow through all fragments
        highscore = Highscore(this)
        // Import highscore from local storage
        highscore.importHighscore()

        // Set up fragment to go to
        setUpFragment()
    }

    /**
     * Boot up the current fragment when the app state is restored
     */
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

    /**
     * Stores the game state and settings when the application or a current game is paused
     */
    override fun onSaveInstanceState(outState: Bundle) {
        // Store the game State
        outState.putInt("current_level", gameState?.level ?: DEFAULT_FIRST_LEVEL)
        outState.putFloat("player_position_x", gameState?.playerPosition?.x ?: 0f)
        outState.putFloat("player_position_y", gameState?.playerPosition?.y ?: 0f)
        outState.putLong("time_left", gameState?.timeLeft ?: DEFAULT_LEVEL_TIME)
        outState.putLong("time_left_last", gameState?.timeLeftLast ?: 0)
        outState.putBoolean("on_going_game", gameState?.onGoingGame ?: false)
        // Store settings
        outState.putFloat("x_speed_mod", settings?.xSpeedModifier ?: DEFAULT_SPEED)
        outState.putFloat("y_speed_mod", settings?.ySpeedModifier ?: DEFAULT_SPEED)
        outState.putInt("x_dir", settings?.xDirection ?: 1)
        outState.putInt("y_dir", settings?.yDirection ?: 1)

        super.onSaveInstanceState(outState)
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        restoreGameState(savedInstanceState)
        restoreGameSettings(savedInstanceState)

        super.onRestoreInstanceState(savedInstanceState)
    }

    /**
     * Restores the game state from a bundle.
     *
     * @param savedInstanceState A stored state
     */
    private fun restoreGameState(savedInstanceState: Bundle) {
        val currentLevel = savedInstanceState.getInt("current_level")
        val playerPositionX = savedInstanceState.getFloat("player_position_x")
        val playerPositionY = savedInstanceState.getFloat("player_position_y")
        val timeLeft = savedInstanceState.getLong("time_left")
        val timeLeftLast = savedInstanceState.getLong("time_left_last")
        val onGoingGame = savedInstanceState.getBoolean("on_going_game")

        gameState = GameState(
            Coordinates(playerPositionX, playerPositionY),
            timeLeft,
            currentLevel,
            timeLeftLast,
            onGoingGame
        )
    }

    /**
     * Restore the game settings from a bundle.
     *
     * @param savedInstanceState A stored state
     */
    private fun restoreGameSettings(savedInstanceState: Bundle) {
        val xSpeedModifier = savedInstanceState.getFloat("x_speed_mod")
        val ySpeedModifier = savedInstanceState.getFloat("y_speed_mod")
        val xDirection = savedInstanceState.getInt("x_dir")
        val yDirection = savedInstanceState.getInt("y_dir")

        settings = Settings(
            xSpeedModifier,
            ySpeedModifier,
            xDirection,
            yDirection
        )
    }

    /**
     * Set up a clean game state
     */
    fun cleanGameState() {
        gameState = GameState(
            Coordinates(0f, 0f),
            DEFAULT_LEVEL_TIME,
            DEFAULT_FIRST_LEVEL,
            0,
            false
        )
    }

    /**
     * Set up default game settings
     */
    private fun defaultSettings() {
        settings = Settings(
            DEFAULT_SPEED,
            DEFAULT_SPEED,
            1,
            1
        )
    }

    /**
     * Makes the back button return to the main menu.
     */
    override fun onBackPressed() {
        val fragment = MenuFragment()
        supportFragmentManager
            .beginTransaction()
            .replace(R.id.fragment_container, fragment)
            .commit()
    }
}
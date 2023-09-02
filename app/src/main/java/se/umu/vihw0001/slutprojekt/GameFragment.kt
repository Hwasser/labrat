package se.umu.vihw0001.slutprojekt

import android.content.Context
import android.content.pm.ActivityInfo
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Bundle
import android.os.CountDownTimer
import android.view.*
import androidx.fragment.app.Fragment
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.floatingactionbutton.FloatingActionButton

private const val TAG = "GameFragment"

class GameFragment : Fragment(), SensorEventListener, GameViewModel.Callbacks {
    private lateinit var viewModel: GameViewModel
    private lateinit var sensorManager: SensorManager
    private lateinit var actionbarButton: FloatingActionButton
    private lateinit var gameView: GameView
    private lateinit var mContext: Context

    private lateinit var menuTimeText: MenuItem // Shows the time left in the action bar
    private lateinit var timeText: TextView // Show the time left at the top of the screen
    private lateinit var menuLevelText: MenuItem // Shows the current level in the action bar
    private lateinit var levelText: TextView  // Shows the current level at the top of the screen

    var timeLeft: Long = 0 // How much time is left of the timer
    var timeLeftLast: Long = 0 // How much time was left when completing last level
    var actionBarHeight: Int = 0 // For calculating the size of the action bar
    var hideActionbar = false // Whether the player is hiding the action bar
    var countDownTimerObject: CountDownTimer? = null // The game timer
    var gameState: GameState? = null // For storing the game state
    private var waitForInput = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setHasOptionsMenu(true)
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)

        // Sets the orientation of the screen to landscape mode, since we don't want the player
        // to accidently rotate the screen while playing
        (activity as AppCompatActivity).setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE)
        // Show the action bar, since it is hidden in the menu
        (activity as AppCompatActivity).supportActionBar?.show()
        mContext = context
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val root = inflater.inflate(R.layout.fragment_game, container, false)
        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        gameView = view.findViewById(R.id.game_view)

        // Set up ViewModel
        viewModel = ViewModelProvider(requireActivity()).get(GameViewModel::class.java)

        setUpActionbar(view)
    }

    override fun onResume() {
        super.onResume()
        // Restore the state of the game
        gameState = (context as MainActivity).gameState
        timeLeft = gameState?.timeLeft ?: DEFAULT_LEVEL_TIME
        timeLeftLast = gameState?.timeLeftLast ?: 0
        // Boot up the current level
        startGame(
            gameState?.level ?: DEFAULT_FIRST_LEVEL,
            gameState?.playerPosition ?: Coordinates(0f,0f),
            gameState?.onGoingGame ?: false)

        // Attack fragment to view model callbacks
        viewModel.attachCallbacks(this@GameFragment)
        // Set up the acceleratometer - or ability to tilt the phone
        setUpTilt()
    }

    override fun onPause() {
        super.onPause()

        // Unregister the accelerometer to save resources and battery life
        sensorManager.unregisterListener(this)
        // Save the state of the game
        if (!waitForInput) {
            (mContext as MainActivity).gameState = GameState(
                viewModel.getPlayerPosition(),
                timeLeft,
                viewModel.level.lvl,
                timeLeftLast,
                true
            )
        }
    }

    /**
     * Sets up the action bar and allow for hiding the view
     */
    private fun setUpActionbar(view: View) {
        // Bind all necessary view objects for action bar
        actionbarButton = view.findViewById(R.id.show_actionbar)
        timeText = view.findViewById(R.id.text_time)
        levelText = view.findViewById(R.id.text_level)
        // Since we are starting with the actionbar visible, these don't need to show from start
        levelText.visibility = View.INVISIBLE
        timeText.visibility = View.INVISIBLE
        gameView.showActionBar = true

        // Set up text of level
        val levelTextDefault = getText(R.string.text_level).toString()
        val currentLevel = gameState?.level ?: 1
        levelText.text = "$levelTextDefault $currentLevel"

        // Add click listener which works both for showing and hiding action bar
        actionbarButton.setOnClickListener{
            if (hideActionbar) {
                // Show action bar
                (activity as AppCompatActivity).supportActionBar?.show()
                // Change show/hide actionbar button
                actionbarButton.setImageResource(R.drawable.hide_actionbar_foreground)
                // Make text on top of screen dissapear
                levelText.visibility   = View.INVISIBLE
                timeText.visibility    = View.INVISIBLE
                // Tell game view action bar is showing
                gameView.showActionBar = true
                hideActionbar = false
            } else {
                // Hide action bar
                (activity as AppCompatActivity).supportActionBar?.hide()
                // Change show/hide actionbar button
                actionbarButton.setImageResource(R.drawable.show_actionbar_foreground)
                // Make text on top of screen appear
                levelText.visibility   = View.VISIBLE
                timeText.visibility    = View.VISIBLE
                // Tell game view action bar is not showing
                gameView.showActionBar = false
                hideActionbar = true
            }
        }
    }


    /**
     * When the accelerometer gets an update, update the view model and the game view.
     */
    override fun onSensorChanged(event: SensorEvent?) {
        if (event?.sensor?.type == Sensor.TYPE_ACCELEROMETER && !waitForInput) {
            val horizontalTilt = event.values[1]
            val verticalTilt   = event.values[0]

            // An update loop for the game
            viewModel.updateEvent(
                horizontalTilt,
                verticalTilt)

            gameView.movePlayer(viewModel.getPlayerPosition(), viewModel.getPlayerRotation())
        }
    }

    /**
     * Sets up the accelerometer, allowing the application to respond to tilt of the phone.
     */
    private fun setUpTilt() {
        val sensorDelay = 7500 // This would put it between SENSOR_DELAY_GAME and SENSOR_DELAY_FASTEST
        sensorManager = requireActivity().getSystemService(AppCompatActivity.SENSOR_SERVICE) as SensorManager
        sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)?.also {
            sensorManager.registerListener(
                this,
                it,
                sensorDelay,
                sensorDelay)
        }
    }

    /**
     * Required for the SensorEventListener-interface.
     */
    override fun onAccuracyChanged(p0: Sensor?, p1: Int) {
        return
    }

    override fun playerDies() {
        countDownTimerObject?.cancel() // Cancel old timer
        startCountdown(DEFAULT_LEVEL_TIME) // Set new timer
    }

    override fun playerWins() {
        waitForInput = true
        countDownTimerObject?.cancel() // Cancel old timer

        if (viewModel.level.isLastLevel()) {
            val dialog = WinPopupFragment(this)
            dialog.show(childFragmentManager, null)
        } else {
            nextLevel()
        }

    }

    /**
     * Prepares the view for the next level and updates the game state.
     */
    private fun nextLevel() {
        // Update game state for new level
        val nextLevel = (gameState?.level ?: 1) + 1
        timeLeftLast = timeLeft
        timeLeft = DEFAULT_LEVEL_TIME
        // Update text of level
        val levelTextString = "${getText(R.string.text_level)} $nextLevel"
        levelText.text = levelTextString
        menuLevelText.title = levelTextString
        // Switch to new level
        startGame(nextLevel,Coordinates(0f,0f), false)
        // Let the player move again
        waitForInput = false
    }

    /**
     * Start the timer, boot up level data in the view model and boot up the game view
     */
    private fun startGame(currentLevel: Int, playerPosition: Coordinates, onGoingGame: Boolean) {
        // Set up the countdown timer
        countDownTimerObject?.cancel() // Cancel a previously set countdown timer if necessary
        startCountdown(timeLeft)
        // Fetch game settings
        val settings = (requireActivity() as MainActivity).settings ?: Settings(
            DEFAULT_SPEED, DEFAULT_SPEED, 1, 1
        )
        // Boot up the view model
        viewModel.startGame(currentLevel,playerPosition, !onGoingGame, settings)

        // Set upp the drawing surface of the game view
        gameView.setUp(viewModel)
    }

    /**
     * Bind and set view of the action bar.
     */
    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.game_actionbar, menu)

        actionBarHeight = menu.size()
        menuTimeText = menu.findItem(R.id.text_time_bar)
        menuLevelText = menu.findItem(R.id.text_level_bar)
        // Set up text of level
        val levelTextDefault = getText(R.string.text_level).toString()
        val currentLevel = gameState?.level ?: 1
        menuLevelText.title = "$levelTextDefault $currentLevel"

        return
    }

    /**
     * Set up the back to menu button.
     */
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.menu_button -> goToMenu()
        }

        return super.onOptionsItemSelected(item)
    }

    /**
     * Go to the menu fragment, replacing the current one.
     */
    fun goToMenu() {
        val fragment = MenuFragment()
        requireActivity().supportFragmentManager
            .beginTransaction()
            .replace(R.id.fragment_container, fragment)
            .commit()
    }

    /**
     * Initializes a new CountDownTimer object.
     *
     * @param countdownTime Time to start the timer at
     */
    private fun startCountdown(countdownTime: Long) {
        countDownTimerObject = object : CountDownTimer(countdownTime, 10) {
            /**
             * Updates text in the view at each centosecond.
             *
             * @param timeLeftTimer How much time that is left in the timer.
             */
            override fun onTick(timeLeftTimer: Long) {
                // Formating of the time
                val centSecond = (timeLeftTimer / 10).toString().takeLast(2)
                val second = (timeLeftTimer / 1000) % 60
                val minute = timeLeftTimer / (1000 * 60) % 60

                timeLeft = timeLeftTimer

                Update the TextView at the top at the screen and in the ActionBar,
                // if they are initialized.
                if (this@GameFragment::mContext.isInitialized) {
                    // Get text two be written before the count down time
                    val text = mContext.getText(R.string.text_time).toString()

                    // If views are initialized, update their text content
                    if (this@GameFragment::menuTimeText.isInitialized)
                        menuTimeText.title = "$text $minute:$second:$centSecond"
                    if (this@GameFragment::timeText.isInitialized)
                        timeText.text = "$text $minute:$second:$centSecond"
                }
            }

            /**
             * Kill player and restart game when time has run out
             */
            override fun onFinish() {
                playerDies()
            }
        }.start()
    }
}
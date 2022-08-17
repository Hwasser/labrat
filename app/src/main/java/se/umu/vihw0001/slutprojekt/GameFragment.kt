package se.umu.vihw0001.slutprojekt

import android.content.Context
import android.content.pm.ActivityInfo
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Bundle
import android.os.CountDownTimer
import android.util.Log
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
    lateinit var gameView: GameView
    lateinit var mContext: Context

    lateinit var menuTimeText: MenuItem // The text of the timer in the action bar
    lateinit var timeText: TextView // The text of the timer in the top of screen

    var timeLeft: Long = 0 // How much time is left of the timer
    var actionBarHeight: Int = 0 // For calculating the size of the action bar
    var hideActionbar = false // Whether the player is hiding the action bar
    var countDownTimerObject: CountDownTimer? = null // The game timer
    var gameState: GameState? = null // For storing the game state

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

    private fun setUpTilt() {
        sensorManager = requireActivity().getSystemService(AppCompatActivity.SENSOR_SERVICE) as SensorManager
        sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)?.also {
            sensorManager.registerListener(
                this,
                it,
                SensorManager.SENSOR_DELAY_FASTEST,
                SensorManager.SENSOR_DELAY_FASTEST)
        }
    }

    override fun onResume() {
        super.onResume()

        gameState = (context as MainActivity).gameState
        timeLeft = gameState?.timeLeft ?: DEFAULT_LEVEL_TIME
        startGame(
            gameState?.level ?: DEFAULT_FIRST_LEVEL,
            gameState?.playerPosition ?: Coordinates(0f,0f),
            gameState?.onGoingGame ?: false)

        // Set upp the drawing surface of the game view
        gameView.setUp(viewModel)
        // Attack fragment to view model callbacks
        viewModel.attachCallbacks(this@GameFragment)
        // Set up the acceleratometer - or ability to tilt the phone
        setUpTilt()
    }

    override fun onPause() {
        super.onPause()

        // Unregister the accelerometer to save resources and battery life
        sensorManager.unregisterListener(this)

        (mContext as MainActivity).gameState = GameState(
            viewModel.getPlayerPosition(),
            timeLeft,
            viewModel.level.lvl,
            true
        )
    }

    private fun setUpActionbar(view: View) {
        // Bind all necessary view objects for action bar
        actionbarButton = view.findViewById(R.id.show_actionbar)
        val levelText = view.findViewById<TextView>(R.id.text_level)
        timeText = view.findViewById(R.id.text_time)
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

    override fun onSensorChanged(event: SensorEvent?) {
        if (event?.sensor?.type == Sensor.TYPE_ACCELEROMETER) {
            // Renaname to xMove, yMove
            val horizontalTilt = event.values[0]
            val verticalTilt   = event.values[1]

            // An update loop for the game
            viewModel.updateEvent(
                horizontalTilt,
                verticalTilt)

            gameView.movePlayer(viewModel.getPlayerPosition(), viewModel.getPlayerRotation())
        }
    }

    override fun playerDies() {
        val startTime: Long = DEFAULT_LEVEL_TIME // Two minutes of countdown time
        countDownTimerObject?.cancel() // Cancel old timer
        startCountdown(startTime) // Set new timer
    }

    override fun playerWins(lastLevel: Boolean) {
        if (lastLevel) {
            (mContext as MainActivity).gameState = GameState(
                Coordinates(0f,0f),
                DEFAULT_LEVEL_TIME,
                DEFAULT_FIRST_LEVEL,
                false)

            val fragment = MenuFragment()
            requireActivity().supportFragmentManager
                .beginTransaction()
                .replace(R.id.fragment_container, fragment)
                .commit()
        }
    }

    private fun startGame(currentLevel: Int, playerPosition: Coordinates, onGoingGame: Boolean) {
        countDownTimerObject?.cancel()
        startCountdown(timeLeft)
        viewModel.startGame(currentLevel,playerPosition, !onGoingGame)
    }

    override fun onAccuracyChanged(p0: Sensor?, p1: Int) {
        return
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.game_actionbar, menu)

        actionBarHeight = menu.size()
        menuTimeText = menu.findItem(R.id.text_time_bar)
        val menuLevelText = menu.findItem(R.id.text_level_bar)
        // Set up text of level
        val levelTextDefault = getText(R.string.text_level).toString()
        val currentLevel = gameState?.level ?: 1
        menuLevelText.title = "$levelTextDefault $currentLevel"

        return
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.menu_button ->  {
                val fragment = MenuFragment()
                requireActivity().supportFragmentManager
                    .beginTransaction()
                    .replace(R.id.fragment_container, fragment)
                    .commit()
            }
        }

        return super.onOptionsItemSelected(item)
    }

    private fun startCountdown(countdownTime: Long) {
        countDownTimerObject = object : CountDownTimer(countdownTime, 10) {
            override fun onTick(timeLeftTimer: Long) {
                val centSecond = (timeLeftTimer / 10).toString().takeLast(2)
                val second = (timeLeftTimer / 1000) % 60
                val minute = timeLeftTimer / (1000 * 60) % 60

                timeLeft = timeLeftTimer

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
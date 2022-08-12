package se.umu.vihw0001.slutprojekt

import android.app.GameManager
import android.content.Context
import android.content.pm.ActivityInfo
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.Chronometer
import androidx.fragment.app.Fragment
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.appcompat.app.ActionBar
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.children
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.floatingactionbutton.FloatingActionButton
import kotlin.math.abs
import kotlin.math.floor
import kotlin.math.round

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "paramNewGame"
private const val TAG = "Level1Fragment"

/**
 * A simple [Fragment] subclass.
 * Use the [Level1Fragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class Level1Fragment : Fragment(), SensorEventListener {
    // TODO: Rename and change types of parameters
    private var paramNewGame: Boolean = false

    private lateinit var viewModel: GameViewModel
    lateinit var sensorManager: SensorManager
    lateinit var actionbarButton: FloatingActionButton
    lateinit var chronometer: Chronometer
    lateinit var gameView: GameView

    var actionBarHeight: Int = 0
    var hideActionbar = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            paramNewGame = it.getBoolean(ARG_PARAM1)
        }

        setHasOptionsMenu(true)
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)

        (activity as AppCompatActivity).setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE)
        (activity as AppCompatActivity).supportActionBar?.show()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val root = inflater.inflate(R.layout.fragment_level1, container, false)
        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        gameView = view.findViewById(R.id.game_view)

        // Set up ViewModel
        viewModel = ViewModelProvider(requireActivity()).get(GameViewModel::class.java)

        setUpActionbarButton(view)

        viewModel.startGame()
        gameView.setUp(viewModel)
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

        setUpTilt()
    }

    override fun onPause() {
        super.onPause()
        // Unregister the accelerometer to save resources and battery life
        sensorManager.unregisterListener(this)
    }

    private fun setUpActionbarButton(view: View) {
        actionbarButton = view.findViewById(R.id.show_actionbar)
        val levelText = view.findViewById<TextView>(R.id.text_level)
        val timeText = view.findViewById<TextView>(R.id.text_time)
        chronometer = view.findViewById(R.id.chronometer)

        chronometer.start()

        // Since we are starting with the actionbar visible, these don't need to show from start
        levelText.visibility = View.INVISIBLE
        timeText.visibility = View.INVISIBLE
        chronometer.visibility = View.INVISIBLE
        gameView.showActionBar = true

        actionbarButton.setOnClickListener{
            if (hideActionbar) {
                (activity as AppCompatActivity).supportActionBar?.show()
                actionbarButton.setImageResource(R.drawable.hide_actionbar_foreground)
                levelText.visibility   = View.INVISIBLE
                timeText.visibility    = View.INVISIBLE
                chronometer.visibility = View.INVISIBLE
                gameView.showActionBar = true
                hideActionbar = false
            } else {
                (activity as AppCompatActivity).supportActionBar?.hide()
                actionbarButton.setImageResource(R.drawable.show_actionbar_foreground)
                levelText.visibility   = View.VISIBLE
                timeText.visibility    = View.VISIBLE
                chronometer.visibility = View.VISIBLE
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

    override fun onAccuracyChanged(p0: Sensor?, p1: Int) {
        return
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.game_actionbar, menu)

        actionBarHeight = menu.size()

        val textTime: MenuItem = menu.findItem(R.id.text_time_bar)
        // TODO: Only updates when creating options menu
        chronometer.setOnChronometerTickListener {chrono -> textTime.title = "Time: ${chrono.text}"}

        //super.onCreateOptionsMenu(menu, inflater)
        return
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return super.onOptionsItemSelected(item)
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment Level1Fragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: Boolean) =
            Level1Fragment().apply {
                arguments = Bundle().apply {
                    putBoolean(ARG_PARAM1, param1)
                }
            }
    }
}
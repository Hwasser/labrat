package se.umu.vihw0001.slutprojekt

import android.content.Context
import android.content.pm.ActivityInfo
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.switchmaterial.SwitchMaterial
import kotlin.math.abs

class SettingsFragment : Fragment(), SensorEventListener {
    private lateinit var sensorManager: SensorManager
    private lateinit var point: TextView
    private lateinit var backButton: Button
    private lateinit var horizontalSlider: SeekBar
    private lateinit var verticalSlider: SeekBar
    private lateinit var horizontalReverse: SwitchMaterial
    private lateinit var verticalReverse: SwitchMaterial

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onResume() {
        super.onResume()

        val xDirection = (requireActivity() as MainActivity).settings?.xDirection ?: 1
        val yDirection = (requireActivity() as MainActivity).settings?.yDirection ?: 1
        horizontalReverse.isChecked = if (xDirection == 1) false else true
        verticalReverse.isChecked = if (yDirection == 1) false else true

        val xSpeedModifier = (requireActivity() as MainActivity).settings?.xSpeedModifier ?: DEFAULT_SPEED
        val ySpeedModifier = (requireActivity() as MainActivity).settings?.ySpeedModifier ?: DEFAULT_SPEED

        horizontalSlider.progress = (10f * xSpeedModifier / 0.15f - 50f).toInt()
        verticalSlider.progress   = (10f * ySpeedModifier / 0.15f - 50f).toInt()

        setUpTilt()
    }

    override fun onPause() {
        super.onPause()

        // Unregister the accelerometer to save resources and battery life
        sensorManager.unregisterListener(this)
    }

    /**
     * When attaching the fragment, force landscape orientation and hide action bar
     */
    override fun onAttach(context: Context) {
        super.onAttach(context)

        // Sets the orientation of the screen to landscape mode, since we don't want the player
        // to accidently rotate the screen while configuring
        (activity as AppCompatActivity).setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE)
        // Hide the action bar
        (activity as AppCompatActivity).supportActionBar?.hide()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_settings, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Bind views
        point = view.findViewById(R.id.meter_point)
        horizontalSlider  = view.findViewById(R.id.width_slider)
        verticalSlider    = view.findViewById(R.id.height_slider)
        horizontalReverse = view.findViewById(R.id.switch1)
        verticalReverse   = view.findViewById(R.id.switch2)
        // Set up back to menu button
        backButton = view.findViewById(R.id.return_button)
        backButton.setOnClickListener{
            saveSettings()
            val fragment = MenuFragment()
            requireActivity().supportFragmentManager
                .beginTransaction()
                .replace(R.id.fragment_container, fragment)
                .commit()
        }
    }

    /**
     * Save changes to game settings.
     */
    private fun saveSettings() {
        val xSpeedModifier = (horizontalSlider.progress + 50f) * 0.015f
        val ySpeedModifier = (verticalSlider.progress + 50f) * 0.015f
        val xDirection = if (horizontalReverse.isChecked) -1 else 1
        val yDirection = if (verticalReverse.isChecked) -1 else 1

        (requireActivity() as MainActivity).settings = Settings(
            xSpeedModifier,
            ySpeedModifier,
            xDirection,
            yDirection
        )
    }

    /**
     * Sets up the accelerometer, allowing the application to respond to tilt of the phone
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

    override fun onSensorChanged(event: SensorEvent?) {
        if (event?.sensor?.type == Sensor.TYPE_ACCELEROMETER) {
            val horizontalTilt = event.values[1]
            val verticalTilt = event.values[0]
            calibrationView(horizontalTilt, verticalTilt)
        }
    }

    override fun onAccuracyChanged(p0: Sensor?, p1: Int) {
        return
    }

    /**
     * Move the point within the calibration view by tilting the phone
     *
     * @param horizontalTilt Horizontal tilt of the phone.
     * @param verticalTilt Vertical tilt of the phone.
     */
    private fun calibrationView(horizontalTilt: Float, verticalTilt: Float) {
        // Modify speed to the slider
        val horizontalModifier: Float = (horizontalSlider.progress + 50f) * 0.015f
        val verticalModifier: Float   = (verticalSlider.progress   + 50f) * 0.015f
        // Max movement within the square
        val maxMovement = 170f
        // Axis direction
        val xDirection = if (horizontalReverse.isChecked) -1 else 1
        val yDirection = if (verticalReverse.isChecked) -1 else 1
        // Adapt to size of the square and take MAX_SPEED in consideration
        val newX = horizontalTilt * horizontalModifier * 100f / MAX_SPEED * xDirection
        val newY = verticalTilt * verticalModifier * 100f / MAX_SPEED * yDirection
        // Move the point within the calibration view
        point.apply {
            // Horizontal movement
            if (abs(newX) < maxMovement)
                translationX = newX
            else { // Force the x-position to be within the square
                if (newX > maxMovement)
                    translationX = maxMovement
                if (newX < -maxMovement)
                    translationX = -maxMovement
            }
            // Vertical movement
            if (abs(newY) < maxMovement)
                translationY = newY
            else { // Force the y-position to be within the square
                if (newY > maxMovement)
                    translationY = maxMovement
                if (newY < -maxMovement)
                    translationY = -maxMovement
            }

        }
    }

    override fun onDestroy() {
        sensorManager.unregisterListener(this)

        super.onDestroy()
    }
}
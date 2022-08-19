package se.umu.vihw0001.slutprojekt

import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class SettingsFragment : Fragment(), SensorEventListener {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

    lateinit var sensorManager: SensorManager
    lateinit var square: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
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

        square = view.findViewById(R.id.square)
        setUpSensorStuff()
    }

    // TODO: Make sensor delay better when the phone is charging
    private fun setUpSensorStuff() {
        sensorManager = requireActivity().getSystemService(AppCompatActivity.SENSOR_SERVICE) as SensorManager
        sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)?.also {
            sensorManager.registerListener(
                this,
                it,
                SensorManager.SENSOR_DELAY_FASTEST,
                SensorManager.SENSOR_DELAY_FASTEST)
        }
    }

    override fun onSensorChanged(event: SensorEvent?) {
        if (event?.sensor?.type == Sensor.TYPE_ACCELEROMETER) {
            // Renaname to xMove, yMove
            val sides = event.values[0]
            val upDown = event.values[1]

            // Ha med detta sedan i inställningar! Ha kallebrerings-inställningar!
            square.apply {
                rotationX = upDown * 3f
                rotationY = sides * 3f
                rotation = -sides
                translationX = sides * -10
                translationY = sides * 10
            }

            square.text = "vertial: ${upDown.toInt()}\nhorizontal: ${sides.toInt()}"
        }
    }

    override fun onAccuracyChanged(p0: Sensor?, p1: Int) {
        return
    }

    override fun onDestroy() {
        sensorManager.unregisterListener(this)

        super.onDestroy()
    }
}
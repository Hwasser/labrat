package se.umu.vihw0001.slutprojekt

import android.view.MenuItem
import android.widget.TextView
import java.util.*

class GameTimer {
    val timer = Timer()
    var milliseconds = 0

    fun startTimer() {
        timer.scheduleAtFixedRate(object : TimerTask() {
            override fun run() {
                    milliseconds += 10
                }
        }, 0, 10)
    }
    fun updateTimerText(menuTimeText: MenuItem) {
        timer.scheduleAtFixedRate(object : TimerTask() {
            override fun run() {

                if (milliseconds >= 1000){
                    val text = (milliseconds / 1000).toString()
                    menuTimeText.title = text
                }
            }
        }, 0, 1000)
    }
}
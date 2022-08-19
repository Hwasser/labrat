package se.umu.vihw0001.slutprojekt

import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import android.view.View
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AppCompatDialogFragment

class WinPopupFragment(private val fragment: GameFragment) : AppCompatDialogFragment() {
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val builder = AlertDialog.Builder(activity)
        val view = layoutInflater.inflate(R.layout.fragment_win_popup, null)
        val inputText = view.findViewById<EditText>(R.id.input_name)

        // Get elapsed time and divide by 10 since default level time is in ms
        val lastLevelScore = (DEFAULT_LEVEL_TIME - fragment.timeLeftLast) / 10
        val currentLevelScore = (DEFAULT_LEVEL_TIME - fragment.timeLeft) / 10
        val score = lastLevelScore + currentLevelScore
        // Set text in window
        setText(view, score)

        // Build the view
        builder.setView(view)
            .setPositiveButton("OK") { _, _ ->
                // Get the input name - or a default name
                val playerName =
                    if (inputText.text.toString() == "")
                        "Unknown Player"
                    else inputText.text.toString()
                // Insert new highscore
                (requireActivity() as MainActivity).highscore.insertHighscore(
                    playerName,
                    score
                )
                // Go back to the menu
                (requireActivity() as MainActivity).cleanGameState()
                fragment.goToMenu()
            }
        return builder.create()
    }

    private fun setText(view: View, score: Long) {
        val rankText = view.findViewById<TextView>(R.id.popup_rank)
        val inputText = view.findViewById<EditText>(R.id.input_name)
        // Get score to compare to
        val bestScore = (requireActivity() as MainActivity).highscore.getBest()
        val worstScore = (requireActivity() as MainActivity).highscore.getWorst()
        // Customize view after how well the player did
        if (score >= worstScore) {
            rankText.setText(R.string.name_rank_below)
            inputText.visibility = View.INVISIBLE
        } else {
            if (score < bestScore) {
                rankText.setText(R.string.name_rank_highest)
            } else {
                rankText.setText(R.string.name_rank_above)
            }
        }
    }
}
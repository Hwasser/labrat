package se.umu.vihw0001.slutprojekt

import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import android.view.View
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AppCompatDialogFragment

/**
 * A pop-up window when the player wins the game. If a top 10 score is reached the user is
 * asked to enter its name.
 *
 * @param fragment The game fragment the user game from.
 */
class WinPopupFragment(private val fragment: GameFragment) : AppCompatDialogFragment() {
    /**
     * Creates a dialog with text depending on the player score.
     */
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val builder = AlertDialog.Builder(activity)
        val view = layoutInflater.inflate(R.layout.fragment_win_popup, null)

        // Get elapsed time and divide by 10 since default level time is in ms
        val lastLevelScore = (DEFAULT_LEVEL_TIME - fragment.timeLeftLast) / 10
        val currentLevelScore = (DEFAULT_LEVEL_TIME - fragment.timeLeft) / 10
        val score = lastLevelScore + currentLevelScore
        // Set text in this pop up window
        setText(view, score)

        // Build the view
        builder.setView(view)
            .setPositiveButton("OK") { _, _ ->
                saveHighscore(view, score)
                // Reset the game state
                cleanGamestate()
                // Go back to the menu
                fragment.goToMenu()
            }
        return builder.create()
    }

    /**
     * Customize the text and view depending on how well the player did.
     *
     * @param view The pop-up view.
     * @param score The totalt score of the game.
     */
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

    /**
     * Asks the user to enter its name and
     * saves the new highscore entry to the highscore model. If no name is entered
     * a default name is chosen.
     *
     * @param view The pop-up view.
     * @param score The score of the game.
     */
    private fun saveHighscore(view: View, score: Long) {
        val defaultName = "Unknown Player"
        val inputText = view.findViewById<EditText>(R.id.input_name)
        // Get the input name - or a default name
        val playerName =
            if (inputText.text.toString() == "")
                defaultName
            else inputText.text.toString()
        // Insert new highscore
        (requireActivity() as MainActivity).highscore.insertHighscore(
            playerName,
            score
        )
    }

    /**
     * Calls the main acitivty to clean up the game state when the "OK"-button is clicked.
     */
    private fun cleanGamestate() {
        (requireActivity() as MainActivity).cleanGameState()
    }
}
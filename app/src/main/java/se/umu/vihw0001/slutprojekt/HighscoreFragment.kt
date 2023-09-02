package se.umu.vihw0001.slutprojekt

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.children

class HighscoreFragment : Fragment() {
    private lateinit var backButton: Button
    private var highscore: Highscore? = null

    /**
     * When attaching the view, makes sure the action bar is hidden and fetch
     * a pointer to highscores.
     */
    override fun onAttach(context: Context) {
        // Hide the action bar
        (activity as AppCompatActivity).supportActionBar?.hide()
        highscore = (activity as MainActivity).highscore

        super.onAttach(context)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_highscore, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setUpHighscores(view)
        setUpBackButton(view)
    }

    /**
     * Fetch formated strings of all highscore entires from the highscore model and place them
     * in the view.
     *
     * @param view The fragment view.
     */
    private fun setUpHighscores(view: View) {
        val leftScoreboard = view.findViewById<LinearLayout?>(R.id.scoreboard_left)
        val rightScoreboard = view.findViewById<LinearLayout?>(R.id.scoreboard_right)
        val allRanks = view.findViewById<LinearLayout>(R.id.scoreboard).children

        var index = 1
        if (leftScoreboard != null) {

            for (rank in leftScoreboard.children) {
                (rank as TextView).text = highscore?.getFormatedHighscore(index)
                index += 1
            }
            for (rank in rightScoreboard.children) {
                (rank as TextView).text = highscore?.getFormatedHighscore(index)
                index += 1
            }
        } else {

            for (rank in allRanks) {
                (rank as TextView).text = highscore?.getFormatedHighscore(index)
                index += 1
            }
        }
    }

    /**
     * Set up the back to menu button.
     *
     * @param view The fragment view.
     */
    private fun setUpBackButton(view: View) {
        backButton = view.findViewById(R.id.return_button)
        backButton.setOnClickListener{
            val fragment = MenuFragment()
            requireActivity().supportFragmentManager
                .beginTransaction()
                .replace(R.id.fragment_container, fragment)
                .commit()
        }
    }

}
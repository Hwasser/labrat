package se.umu.vihw0001.slutprojekt

import android.content.Context
import android.content.pm.ActivityInfo
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity

class MenuFragment : Fragment() {
    private lateinit var continueButton: Button
    private lateinit var newGameButton: Button
    private lateinit var highScoreButton: Button
    private lateinit var settingsButton: Button
    lateinit var mContext: Context
    var hasStarted = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)

        mContext = context

        // Hide the action bar
        (activity as AppCompatActivity).supportActionBar?.hide()
        // Activate screen rotation (since this is off in the GameFragment)
        requireActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_menu, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setUpMenuButtons(view)
    }

    override fun onResume() {
        hasStarted = (mContext as MainActivity).gameState?.onGoingGame ?: false

        // Whether the continue button is to be grayed out or not
        if (hasStarted)
            continueButton.alpha = 1f
        else
            continueButton.alpha = 0.33f

        super.onResume()
    }

    private fun setUpMenuButtons(view: View) {
        continueButton  = view.findViewById(R.id.continue_button)
        newGameButton   = view.findViewById(R.id.new_game_button)
        highScoreButton = view.findViewById(R.id.highscore_button)
        settingsButton  = view.findViewById(R.id.settings_button)

        continueButton.setOnClickListener{
            resumeGame()
        }
        newGameButton.setOnClickListener{
            startNewGame()
        }
        highScoreButton.setOnClickListener{
            val fragment = HighscoreFragment()
            requireActivity().supportFragmentManager
                .beginTransaction()
                .replace(R.id.fragment_container, fragment)
                .commit()
        }
        settingsButton.setOnClickListener{

        }
    }

    private fun startNewGame() {
        (mContext as MainActivity).gameState = GameState(
            Coordinates(0f,0f),
            DEFAULT_LEVEL_TIME,
            DEFAULT_FIRST_LEVEL,
            false)

        val fragment = GameFragment()
        requireActivity().supportFragmentManager
            .beginTransaction()
            .replace(R.id.fragment_container, fragment)
            .commit()
    }

    private fun resumeGame() {
        if (hasStarted) {
            val fragment = GameFragment()
            requireActivity().supportFragmentManager
                .beginTransaction()
                .replace(R.id.fragment_container, fragment)
                .commit()
        }
    }
}
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
    private lateinit var mContext: Context
    private var hasStarted = false

    /**
     * Hide the action bar and allow different screen orientations when attaching the menu fragment.
     */
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

    /**
     * When resuming the view, check whether an ongoing game is on to check whether
     * the "Continue button" should appear activated or not.
     */
    override fun onResume() {
        hasStarted = (mContext as MainActivity).gameState?.onGoingGame ?: false

        // Whether the continue button is to be grayed out or not
        if (hasStarted)
            continueButton.alpha = 1f
        else
            continueButton.alpha = 0.33f

        super.onResume()
    }

    /**
     * Sets up all the buttons of the menu.
     *
     * @param view The fragment view.
     */
    private fun setUpMenuButtons(view: View) {
        continueButton  = view.findViewById(R.id.continue_button)
        newGameButton   = view.findViewById(R.id.new_game_button)
        highScoreButton = view.findViewById(R.id.highscore_button)
        settingsButton  = view.findViewById(R.id.settings_button)
        // Bind fragments
        val gameFragment = GameFragment()
        val highscoreFragment = HighscoreFragment()
        val settingsFragment = SettingsFragment()
        // Set events for buttons
        continueButton.setOnClickListener{
            if (hasStarted)
                moveToFragment(gameFragment)
        }
        newGameButton.setOnClickListener{
            startNewGame(gameFragment)
        }
        highScoreButton.setOnClickListener{
            moveToFragment(highscoreFragment)
        }
        settingsButton.setOnClickListener {
            moveToFragment(settingsFragment)
        }
    }

    /**
     * Starts a new game and sets a clean game state.
     *
     * @param fragment The fragment of the game fragment.
     */
    private fun startNewGame(fragment: Fragment) {
        (mContext as MainActivity).gameState = GameState(
            Coordinates(0f,0f),
            DEFAULT_LEVEL_TIME,
            DEFAULT_FIRST_LEVEL,
            0,
            false)

        moveToFragment(fragment)
    }

    /**
     * Replaces the current framgent with another one.
     *
     * @param fragment Fragment to replace the current one with.
     */
    private fun moveToFragment(fragment: Fragment) {
        requireActivity().supportFragmentManager
            .beginTransaction()
            .replace(R.id.fragment_container, fragment)
            .commit()
    }
}
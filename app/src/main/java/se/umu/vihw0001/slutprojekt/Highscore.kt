package se.umu.vihw0001.slutprojekt

import android.content.Context
import java.io.BufferedWriter
import java.io.File
import java.io.InputStream

const val FILENAME = "highscore.text"
const val NAME_PLACEHOLDER = "Not Taken"

/**
 * This class contains the model of the highscore table.
 *
 * The data is stored in a text file and is represented in the following way:
 * name
 * score
 * name
 * score
 * ..
 */
class Highscore(context: Context) {
    // A list containing entries in the representation of tuples: (name, score)
    private var highscore: MutableList<Pair<String, Long>> = mutableListOf(Pair(
        NAME_PLACEHOLDER,
        DEFAULT_LEVEL_TIME * NUMBER_OF_LEVELS))
    val path = context.filesDir // Path of the file

    /**
     * Imports all highscore entries from a local file and sorts them by score in ascending order.
     */
    fun importHighscore() {
        // Always make sure a file exists with at least one entry, create a file if it doesn't exist
        if (!File(path, FILENAME).exists()) {
            exportHighscore()
        }

        val inputStream: InputStream = File(path, FILENAME).inputStream()
        val nameList = mutableListOf<String>()
        val scoreList = mutableListOf<Long>()

        // Import all name and scores from a text file
        var index = 0
        inputStream.bufferedReader().forEachLine {
            if (it != "") {
                if (index % 2 == 0)
                    nameList.add(it)
                else
                    scoreList.add(it.toLong())
            }
            index += 1
        }
        // When all the entries is imported, sort them by score id ascending order
        val mergedFromFile  = nameList.zip(scoreList)
        highscore = mergedFromFile.sortedWith(compareBy { it.second }).toMutableList()
        inputStream.close()
    }

    /**
     * Exports all highscore entries to a local file.
     */
    fun exportHighscore() {
        val writer: BufferedWriter = File(path, FILENAME).bufferedWriter()
        // Write all highscore entries to a text file
        writer.use { out ->
            out.write("") // Clear file
            var index = 0
            for (high in highscore) {
                out.appendLine(high.first)
                out.appendLine(high.second.toString())
                index += 1
            }
        }
        writer.close()
    }

    /**
     * Inserts a highscore entry inside the highscore-list and sorts the list in ascending order
     * and makes sure only ten entries makes it to the list.
     *
     * @param name Name of the highscore entry.
     * @param score Score of the highscore entry.
     */
    fun insertHighscore(name: String, score: Long) {
        // Add a new highscore entry
        highscore.add(Pair(name, score))
        // Sort list - putting the best score at the top
        highscore = highscore.sortedWith(compareBy { it.second }).toMutableList()
        // If more than 10 scores exist, remove the last one
        if (highscore.count() > 10) {
            highscore.removeLast()
        }
    }

    /**
     * Get the best highscore entry in the list.
     *
     * @return The best highscore entry
     */
    fun getBest(): Long {
        if (highscore.isEmpty())
            return DEFAULT_LEVEL_TIME * NUMBER_OF_LEVELS
        return highscore.first().second
    }

    /**
     * Get the worst highscore entry in the list.
     *
     * @return The worst highscore entry
     */
    fun getWorst(): Long {
        if (highscore.isEmpty())
            return DEFAULT_LEVEL_TIME * NUMBER_OF_LEVELS
        return highscore.last().second
    }

    /**
     * Makes a formated string of a highscore entry of a specified rank.
     *
     * @param rank Rank (1-10) of the highscore entry to receive.
     * @return A formated string containing name and time of a highscore entry.
     */
    fun getFormatedHighscore(rank: Int): String {
        // This will align text of "10. " and "1   "
        val spaceLength = if (rank == 10) ". " else ".   "

        // Converts the score two correct time formatting: %mm%ss%cc
        if (rank <= highscore.count()) {
            val name  = highscore[rank - 1].first
            val scoreRaw: Long = highscore[rank - 1].second

            val minutes = (scoreRaw / 100) / 60
            val seconds = (scoreRaw / 100) % 60
            val centiSecond      = scoreRaw % 100

            return "$rank$spaceLength $name - $minutes:$seconds:$centiSecond"
        }
        return "$rank$spaceLength $NAME_PLACEHOLDER - 4:0:0"
    }


}
package se.umu.vihw0001.slutprojekt

import android.content.Context
import android.widget.Chronometer
import java.io.BufferedWriter
import java.io.File
import java.io.InputStream
import java.io.OutputStream
import java.sql.Time
import java.util.*

const val FILENAME = "highscore.text"
const val NAME_PLACEHOLDER = "Not Taken"

class Highscore(context: Context) {
    private var highscore: MutableList<Pair<String, Long>> = mutableListOf(Pair(
        NAME_PLACEHOLDER,
        DEFAULT_LEVEL_TIME * NUMBER_OF_LEVELS))
    val path = context.filesDir

    fun importHighscore() {
        // Always make sure a file exists with at least one entry
        if (!File(path, FILENAME).exists()) {
            exportHighscore()
        }

        val inputStream: InputStream = File(path, FILENAME).inputStream()
        val nameList = mutableListOf<String>()
        val scoreList = mutableListOf<Long>()

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
        val mergedFromFile  = nameList.zip(scoreList)
        highscore = mergedFromFile.sortedWith(compareBy { it.second }).toMutableList()
    }

    fun exportHighscore() {
        val writer: BufferedWriter = File(path, FILENAME).bufferedWriter()

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

    fun insertHighscore(name: String, score: Long) {
        // Add new highscore
        highscore.add(Pair(name, score))
        // Sort list - putting the best score at the top
        highscore = highscore.sortedWith(compareBy { it.second }).toMutableList()
        // Remove a score at place 11
        if (highscore.count() > 10) {
            highscore.removeLast()
        }
    }

    fun getBest(): Long {
        if (highscore.isEmpty())
            return DEFAULT_LEVEL_TIME * NUMBER_OF_LEVELS
        return highscore.first().second
    }

    fun getWorst(): Long {
        if (highscore.isEmpty())
            return DEFAULT_LEVEL_TIME * NUMBER_OF_LEVELS
        return highscore.last().second
    }

    fun getFormatedHighscore(rank: Int): String {
        // This will align text of "10. " and "1   "
        val spaceLength = if (rank == 10) ". " else ".   "

        if (rank <= highscore.count()) {
            val name  = highscore[rank - 1].first
            val scoreRaw: Long = highscore[rank - 1].second

            val minutes = (scoreRaw / 100) / 60
            val seconds = (scoreRaw / 100) % 60
            val ms      = scoreRaw % 100

            return "$rank$spaceLength $name - $minutes:$seconds:$ms"
        }
        return "$rank$spaceLength $NAME_PLACEHOLDER - 4:0:0"
    }


}
import android.util.Log

data class Song(
    val id: Int,
    val name: String,
    val lyrics: String,
    val chords: String,
    var finalChords: String,
    var chordList: List<String>,
    val category_id: Int,
    val indexes: List<Int>,
    ) {

    // An ordered list of the chromatic scale in Portuguese naming convention
    private val chromaticScale = listOf("dó", "dó#", "ré", "ré#", "mi", "fá", "fá#", "sol", "sol#", "lá", "lá#", "si",
        "réb", "mib", "solb", "láb", "sib") // Including flats for completeness

    fun transpose(steps: Int) {
        // Transpose each chord in the chord list by the given number of steps
        Log.i("chordList", chordList.toString())
        chordList = chordList.map { chord ->
            transposeChord(chord, steps)
        }

        // Assuming `setChords()` appropriately sets `finalChords` from `chordList`
        setChordString()
    }

    private fun transposeChord(chord: String, steps: Int): String {
        // This function now uses Portuguese chord names
        Log.d("Song", "Transposing chord: $chord")

        // An ordered list of the chromatic scale in Portuguese naming convention
        val chromaticScaleSharp = listOf("dó", "dó#", "ré", "ré#", "mi", "fá", "fá#", "sol", "sol#", "lá", "lá#", "si")
        val chromaticScaleFlat = listOf("dó", "réb", "ré", "mib", "mi", "fá", "solb", "sol", "láb", "lá", "sib", "si")

        // Detect whether to use sharp or flat based on the existing chord naming
        val useFlat = chord.contains("b") || chord.contains("solb") || chord.contains("láb") || chord.contains("sib")

        // Use the appropriate scale: sharp or flat
        var scale = if (useFlat) chromaticScaleFlat else chromaticScaleSharp

        // Find the index of the root note in the scale
        val rootIndex = scale.indexOfFirst { it == chord }
        if (rootIndex == -1) {
            // Chord was not found in the scale, which means it could be a complex chord or an error in input
            return chord // Return the chord unchanged if it's not recognized
        }

        // Calculate the new index for transposing up or down
        val newIndex = (rootIndex + steps + scale.size) % scale.size
        return scale[newIndex] // Return the transposed chord
    }

    fun setChords() {
        generateChordList()
        setChordString()
    }

    fun generateChordList() {
        // Remove the curly braces from the string
        val sanitizedInput = chords.removeSurrounding("{", "}")

        // Split the string into a list using the comma as a separator
        chordList = sanitizedInput.split(",")
    }

    private fun removeBracketsFromLyrics(lyrics: String): String {
        // Remove only the square brackets but keep the content between them.
        return lyrics.replace("[", "").replace("]", "")
    }

    private fun setChordString() {
        //finalChords = alignChordsWithLyrics(lyrics, chordList)
    }

    private fun alignChordsWithLyrics(lyrics: String, chords: List<String>): String {
        val lyricsLines = lyrics.lines()

        // Initialize an index to keep track of which chord is next
        var currentChordIndex = 0

        // A mutable list to hold the aligned chords for each line of lyrics
        val alignedChordsLines = mutableListOf<String>()

        // Iterate over each line of lyrics
        lyricsLines.forEach { line ->
            // Find all placeholders in the current line of lyrics
            val placeholders = """\[(.*?)\]""".toRegex().findAll(line).toList()

            // Build the chord line with spaces and chords according to placeholders
            val chordLineBuilder = StringBuilder()
            var placeholderCounter = 0 // Counter for the number of placeholders processed

            placeholders.forEach { match ->
                // Calculate the number of characters since the last chord or start of the line
                val charsSinceLastChord = match.range.first - placeholderCounter * 2  // Subtract 2 for brackets per placeholder
                chordLineBuilder.append(" ".repeat(charsSinceLastChord - chordLineBuilder.length))

                // Append the chord (if available)
                if (currentChordIndex < chords.size) {
                    chordLineBuilder.append(chords[currentChordIndex])
                    currentChordIndex++
                }

                // Increase placeholder counter
                placeholderCounter++
            }

            // Add the chord line to the list of aligned chords
            alignedChordsLines.add(chordLineBuilder.toString())
        }

        // Adjust the chord lines to have the same number of characters as the corresponding lyrics lines
        return alignedChordsLines.mapIndexed { index, chordLine ->
            chordLine.padEnd(lyricsLines[index].replace("[", "").replace("]", "").length)
        }.joinToString("\n")
    }

    fun createFinalChords() {
        // Initialize the finalChords with a StringBuilder filled with spaces to match lyrics length
        val chordString = StringBuilder(" ".repeat(lyrics.length))

        // Place chords at the given indexes
        for ((chordIndex, index) in indexes.withIndex()) {
            // Check if chordIndex is within the range of chordList before attempting to access it
            if (chordIndex < chordList.size) {
                val chord = chordList[chordIndex]
                // Ensure we do not exceed the lyrics length
                if (index + chord.length <= lyrics.length) {
                    for (j in chord.indices) {
                        chordString.setCharAt(index + j, chord[j])
                    }
                }
            }
        }

        // Replace spaces with newlines in the finalChords where the lyrics have them
        lyrics.indices.forEach { i ->
            if (lyrics[i] == '\n') {
                // If the character at the current index is not a space, find the next space to replace
                if (chordString[i] != ' ') {
                    var j = i
                    while (j < chordString.length && chordString[j] != ' ') {
                        j++
                    }
                    // If we found a space, replace it with a newline
                    if (j < chordString.length) {
                        chordString[j] = '\n'
                    }
                } else {
                    // If the character at the current index is a space, we can safely replace it with a newline
                    chordString[i] = '\n'
                }
            }
        }

        // Update the finalChords field with the new string
        finalChords = chordString.toString()
    }

}
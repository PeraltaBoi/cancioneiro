package com.peraltaboi.cancioneiro

import Song
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun SongText(song: Song) {
    // Split lyrics and chords into lines assuming they are pre-aligned
    val lyricsLines = song.lyrics.split("\n")
    val chordsLines = song.finalChords.split("\n")


    /*val textStyleBody1 = appTypography.bodyMedium
    var textStyle by remember { mutableStateOf(textStyleBody1) }
    var readyToDraw by remember { mutableStateOf(false) }*/

    // Use a column to display the lyrics and chords
    Column (
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .horizontalScroll(rememberScrollState())
            .verticalScroll(rememberScrollState()),
    ){
        // Chords and Lyrics
        for ((index, lyricLine) in lyricsLines.withIndex()) {
            // Only show chord line if available and not empty
            if (index < chordsLines.size && chordsLines[index].isNotBlank()) {
                Text(
                    text = chordsLines[index],
                    style = appTypography.bodyMedium,
                    modifier = Modifier.
                        fillMaxWidth()
                )
            }

            /*Text(
                text = "long text goes here",
                style = textStyle,
                maxLines = 1,
                softWrap = false,
                modifier = modifier.drawWithContent {
                    if (readyToDraw) drawContent()
                },
                onTextLayout = { textLayoutResult ->
                    if (textLayoutResult.didOverflowWidth) {
                        textStyle = textStyle.copy(fontSize = textStyle.fontSize * 0.9)
                    } else {
                        readyToDraw = true
                    }
                }
            )*/

            // Lyrics line
            Text(
                text = lyricLine,
                style = appTypography.bodyMedium,
                modifier = Modifier.
                    fillMaxWidth()
            )
            // Spacer between each set of chords and lyrics
            Spacer(modifier = Modifier.height(10.dp))
        }
    }
}
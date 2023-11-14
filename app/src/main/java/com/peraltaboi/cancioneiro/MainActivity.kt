package com.peraltaboi.cancioneiro

import Song
import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FabPosition
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.Typography
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.peraltaboi.cancioneiro.ui.theme.CancioneiroTheme
import kotlinx.coroutines.launch
import androidx.navigation.compose.rememberNavController
import androidx.navigation.compose.composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.currentBackStackEntryAsState
import com.peraltaboi.cancioneiro.ui.theme.Typography
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController

val OpenSansMono = FontFamily(
    Font(R.font.opensans_variablefont_wdth_wght, FontWeight.W400),
    // Add other font weights if available and needed
)

val NotoSansMono = FontFamily(
    Font(R.font.notosansmono_variablefont_wdth_wght),
)

val ComicSans = FontFamily(
    Font(R.font.ldfcomicsans_jj7l),
)

val RobotoMono = FontFamily(
    Font(R.font.robotomonovariablefont_wght),
)

// Define a custom Typography
val appTypography = Typography(
    bodyLarge = TextStyle(
        fontWeight = FontWeight.Bold,
        fontFamily = RobotoMono,
        fontSize = 24.sp,
    ),
    bodyMedium = TextStyle(
        fontWeight = FontWeight.Bold,
        fontFamily = RobotoMono,
        fontSize = 16.sp
    ),
    bodySmall = TextStyle(
        fontWeight = FontWeight.Bold,
        fontFamily = RobotoMono,
        fontSize = 12.sp
    ),
    labelSmall = TextStyle(
        fontWeight = FontWeight.Bold,
        fontFamily = RobotoMono,
        fontSize = 12.sp
    ),
)

// Define the dark theme color scheme
private val DarkColorScheme = darkColorScheme(
    primary = Color(0xFFBB86FC),
    onPrimary = Color.Black,
    secondary = Color(0xFF03DAC5),
    onSecondary = Color.Black,
    background = Color(0xFF121212),  // Dark background color
    onBackground = Color.White,      // Light text color on dark background
    surface = Color(0xFF1E1E1E),
    onSurface = Color.White,
    // You can define other colors like error, onSurface, etc.
)

// Apply this Typography in your theme
@Composable
fun AppTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = DarkColorScheme,
        typography = appTypography
    ) {
        Surface(
            color = MaterialTheme.colorScheme.background,
            contentColor = MaterialTheme.colorScheme.onBackground
        ) {
            content()
        }
    }
}

class MainViewModelFactory(private val repository: AppRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(MainViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return MainViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}

class MainViewModel(private val repository: AppRepository): ViewModel() {
    var songNames: MutableState<List<String>> = mutableStateOf(listOf())
        private set

    init {
        viewModelScope.launch {
            try {
                songNames.value = repository.getAllSongs()
            } catch (e: Exception) {
                Log.e("MainViewModel", "Error fetching songs", e)
            }
        }
    }

    private val _selectedSong = MutableLiveData<Song?>()
    val selectedSong: LiveData<Song?> get() = _selectedSong

    // Function to fetch a song by name
    fun fetchSongByName(songName: String) {
        viewModelScope.launch {
            try {
                val song = repository.getSong(songName)
                song.setChords()
                song.createFinalChords()
                println(song.finalChords)
                println()
                _selectedSong.postValue(song)
            } catch (e: Exception) {
                _selectedSong.postValue(null)
            }
        }
    }
    fun transposeSelectedSong(steps: Int) {
        // You must change the state that Compose is observing.
        // Since _selectedSong is a LiveData, we should post a new, updated Song object to it
        val updatedSong = _selectedSong.value?.copy() // Copy the current Song
        if (updatedSong != null) {
            // Transpose the chords
            updatedSong.transpose(steps)
            // Post the updated song
            _selectedSong.postValue(updatedSong)
        }
    }
}

class MainActivity : ComponentActivity() {
    @SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Provide a repository instance (ensure this is done correctly depending on your repository implementation)
        val repository = AppRepository() // Make sure this is initialized correctly with necessary context or parameters

        // Create an instance of MainViewModelFactory with the repository
        val factory = MainViewModelFactory(repository)

        // Obtain a MainViewModel instance using the factory
        val viewModel: MainViewModel by viewModels { factory }

        setContent {
            AppTheme {  // Apply your custom theme
                val navController = rememberNavController()
                MainScreen(navController, viewModel)
            }
        }
    }
}


@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(navController: NavHostController, viewModel: MainViewModel) {
    val backstackEntry = navController.currentBackStackEntryAsState()
    val currentRoute = backstackEntry.value?.destination?.route

    Scaffold(
        topBar = {
            if (currentRoute != "songList") {
                TopAppBar(
                    title = {
                        val song by viewModel.selectedSong.observeAsState()
                        Text(text = song?.name ?: "Loading...")
                    },
                    navigationIcon = {
                        if (currentRoute != "songList") {
                            IconButton(onClick = { navController.popBackStack() }) {
                                Icon(
                                    imageVector = Icons.Filled.ArrowBack,
                                    contentDescription = "Back"
                                )
                            }
                        }
                    }
                )
            }
        }
    ) {
        NavHost(navController = navController, startDestination = "songList") {
            composable("songList") {
                SongListScreen(viewModel = viewModel) { songName ->
                    navController.navigate("songDetail/$songName")
                }
            }
            composable("songDetail/{songName}") { backStackEntry ->
                val songName = backStackEntry.arguments?.getString("songName") ?: return@composable
                // Use LaunchedEffect to fetch the song once, not on every recomposition
                LaunchedEffect(songName) {
                    viewModel.fetchSongByName(songName)
                }
                // Observe the selectedSong LiveData as State and use it in SongDetailScreen
                val song by viewModel.selectedSong.observeAsState()
                song?.let {
                    SongDetailScreen(viewModel, song = it, onBack = { navController.popBackStack() })
                }
            }
        }
    }
}

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SongDetailScreen(viewModel: MainViewModel, song: Song, onBack: () -> Unit) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = song.name) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        },
    ) { innerPadding ->
        Surface(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize(),
            color = MaterialTheme.colorScheme.background
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                SongText(song = song) // Your composable function to show the song text

                // Space for the buttons, ensuring they appear at the bottom
                TransposeButtons(viewModel)
            }
        }
    }
}

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(
        text = "Hello $name!"
    )
}

@Composable
fun SongListScreen(viewModel: MainViewModel, onSongClick: (String) -> Unit) {
    val songNames by viewModel.songNames  // Use 'by' for state delegation

    SongList(songNames, onSongClick) // Now passing the lambda correctly
}

@Composable
fun SongList(songs: List<String>, onSongClick: (String) -> Unit) {
    LazyColumn {
        items(songs) { song ->
            Text(
                text = song,
                modifier = Modifier
                    .padding(16.dp)
                    .clickable { onSongClick(song) }, // This lambda is correctly used here
                style = TextStyle(fontSize = 20.sp, fontWeight = FontWeight.Bold)
            )
        }
    }
}

@Composable
fun TransposeButtons(viewModel: MainViewModel) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.Center
    ) {
        // Transpose down button with arrow and text
        Button(onClick = { viewModel.transposeSelectedSong(-1) }) {
            Icon(Icons.Default.KeyboardArrowDown, contentDescription = "Transpose Down")
            Text("Transpose")
        }
        Spacer(modifier = Modifier.width(16.dp))  // Space between buttons
        // Transpose up button with arrow and text
        Button(onClick = { viewModel.transposeSelectedSong(1) }) {
            Icon(Icons.Default.KeyboardArrowUp, contentDescription = "Transpose Up")
            Text("Transpose")
        }
    }
}
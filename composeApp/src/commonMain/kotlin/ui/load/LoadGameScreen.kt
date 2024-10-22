package ui.load

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import board.Board.Companion.BoardKeyPrefix
import board.deleteSavedGame
import board.loadSavedGames
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import kotlinx.coroutines.launch
import kotlinx.datetime.*
import settings.boardSettings
import ui.game.GameScreenNav

@Composable
fun LoadGameScreen() {
    val navigator = LocalNavigator.currentOrThrow
    val coroutineScope = rememberCoroutineScope()

    var savedGames by remember { mutableStateOf(emptyList<Pair<LocalDateTime, String>>()) }

    LaunchedEffect(Unit) {
        savedGames = loadSavedGames()
    }

    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier.fillMaxSize()
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.fillMaxSize().align(Alignment.Center)
        ) {
            if (savedGames.isEmpty()) {
                Text(text = "No saved games")
            } else {
                savedGames.forEach { (date, encodedPieces) ->
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween,
                        modifier = Modifier.fillMaxWidth().padding(8.dp)
                    ){
                        Button(
                            onClick = {
                                navigator.push(GameScreenNav(encodedPieces))
                            },
                            modifier = Modifier.weight(1f)
                        ) {
                            Text(text = "Game $date")
                        }
                        IconButton(
                            onClick = {

                                coroutineScope.launch {
                                    deleteSavedGame(date)
                                    savedGames = loadSavedGames() // Обновляем данные
                                }
                            },
                            modifier = Modifier.size(30.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Filled.Delete,
                                contentDescription = "Delete game"
                            )
                        }
                    }
                }
            }
        }

        IconButton(
            onClick = {
                navigator.pop()
            },
            modifier = Modifier
                .align(Alignment.TopStart)
                .padding(16.dp)
        ) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                contentDescription = null
            )
        }
    }
}

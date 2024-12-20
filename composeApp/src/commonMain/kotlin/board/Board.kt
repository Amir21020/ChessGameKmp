package board
import androidx.compose.runtime.*
import androidx.compose.ui.unit.IntOffset
import board.Board.Companion.BoardKeyPrefix
import com.russhwolf.settings.set
import kotlinx.coroutines.*
import kotlinx.datetime.*
import pieces.Piece
import settings.boardSettings

@Composable
fun Board.rememberPieceAt(x: Int, y: Int): Piece? =
    remember(x, y, moveIncrement) {
        getPiece(
            x = x,
            y = y,
        )
    }

@Composable
fun rememberBoard(
    encodedPieces: String = InitialEncodedPiecesPosition,
): Board =
    remember {
        Board(encodedPieces = encodedPieces)
    }


suspend fun loadSavedGames(): List<Pair<LocalDateTime, String>> {
    return withContext(Dispatchers.IO) {
        boardSettings
            .keys
            .filter { it.startsWith(BoardKeyPrefix) }
            .mapNotNull { key ->
                val encodedPieces = boardSettings.getStringOrNull(key) ?: return@mapNotNull null
                val millis = key.removePrefix(BoardKeyPrefix).toLongOrNull() ?: return@mapNotNull null
                val date = Instant.fromEpochMilliseconds(millis).toLocalDateTime(TimeZone.currentSystemDefault())
                date to encodedPieces
            }
            .sortedBy { (date, _) -> date }
    }
}


suspend fun deleteSavedGame(date: LocalDateTime) {
    withContext(Dispatchers.IO) {
        val instant = date.toInstant(TimeZone.currentSystemDefault())
        val millis = instant.toEpochMilliseconds()
        val keyToDelete = "$BoardKeyPrefix$millis"
        boardSettings.remove(keyToDelete)
    }
}


@Immutable
class Board(
    encodedPieces: String = InitialEncodedPiecesPosition,
) {
    private val _pieces = mutableStateListOf<Piece>()
    val pieces get() = _pieces.toList()

    init {
        _pieces.addAll(
            decodePieces(encodedPieces = encodedPieces)
        )
    }

    var selectedPiece by mutableStateOf<Piece?>(null)
        private set

    var selectedPieceMoves by mutableStateOf(emptySet<IntOffset>())
        private set

    var moveIncrement by mutableIntStateOf(0)
        private set

    var playerTurn by mutableStateOf(
        if(moveIncrement % 2 == 0)
            Piece.Color.White
        else
            Piece.Color.Black)

    /**
     * User events
     */

    suspend fun selectPiece(piece: Piece) {
        if (piece.color != playerTurn)
            return

        if (piece == selectedPiece) {
            clearSelection()
        } else {
            selectedPiece = piece
            selectedPieceMoves = piece.getAvailableMoves(pieces = pieces)
        }
    }

    fun moveSelectedPiece(x: Int, y: Int) {
        selectedPiece?.let { piece ->
            if (!isAvailableMove(x = x, y = y))
                return

            if (piece.color != playerTurn)
                return

            movePiece(
                piece = piece,
                position = IntOffset(x, y)
            )

            clearSelection()

            switchPlayerTurn()

            moveIncrement++

            piece.isCovered = false

            piece.moveCount++

        }
    }

    /**
     * Public Methods
     */

    fun getPiece(x: Int, y: Int): Piece? =
        _pieces.find { it.position.x == x && it.position.y == y }

    fun isAvailableMove(x: Int, y: Int): Boolean =
        selectedPieceMoves.any { it.x == x && it.y == y }

    fun save() {
        val encodedBoard = encode()
        val millis = Clock.System.now().toEpochMilliseconds()

        boardSettings[BoardKeyPrefix + millis] = encodedBoard
    }

    /**
     * Private Methods
     */

    private fun movePiece(
        piece: Piece,
        position: IntOffset
    ) {
        val targetPiece = pieces.find { it.position == position }

        if (targetPiece != null)
            removePiece(targetPiece)

        piece.position = position
    }

    private fun removePiece(piece: Piece) {
        _pieces.remove(piece)
    }


    private fun clearSelection() {
        selectedPiece = null
        selectedPieceMoves = emptySet()
    }

    private fun switchPlayerTurn() {
        playerTurn =
            if (playerTurn.isWhite)
                Piece.Color.Black
            else
                Piece.Color.White
    }

    private fun encode(): String {
        return pieces.joinToString(separator = "") { it.encode() }
    }

    companion object {
        const val BoardKeyPrefix = "board_"
    }
}

@Composable
fun Board.rememberIsAvailableMove(x: Int, y: Int): Boolean =
    remember(x, y, selectedPieceMoves) {
        isAvailableMove(
            x = x,
            y = y,
        )
    }
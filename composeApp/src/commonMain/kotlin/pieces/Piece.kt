package pieces

import androidx.compose.ui.unit.IntOffset
import board.BoardXCoordinates
import board.BoardYCoordinates
import org.jetbrains.compose.resources.DrawableResource

interface Piece {

    val color: Color

    enum class Color {
        White,
        Black;

        val isWhite: Boolean
            get() = this == White

        val isBlack: Boolean
            get() = this == Black
    }

    val type: Char

    var isCovered : Boolean

    var moveCount : Int

    val drawable: DrawableResource

    var position: IntOffset

    suspend fun getAvailableMoves(pieces: List<Piece>): Set<IntOffset>

    suspend fun getCheckThreateningMoves(pieces: List<Piece>) : Set<IntOffset>

    fun encode(): String {
        // W, B
        val colorCode = color.name.first()

        return StringBuilder()
            .append(type)
            .append(colorCode)
            .append(position.x - BoardXCoordinates.min())
            .append(position.y - BoardYCoordinates.min())
            .toString()
    }

    companion object {
        fun decode(encodedPiece: String): Piece {
            val (type, color, x, y) = encodedPiece.toCharArray()

            val pieceColor =
                Color.entries
                    .find { it.name.first() == color }
                    ?: throw IllegalArgumentException("Invalid piece color!")

            val position =
                IntOffset(
                    x = x.digitToInt() + BoardXCoordinates.min(),
                    y = y.digitToInt() + BoardYCoordinates.min()
                )

            val isCovered = false

            val moveCount = 0

            return when (type) {
                Pawn.Type ->
                    Pawn(pieceColor, position,moveCount, isCovered)
                King.Type ->
                    King(pieceColor, position, moveCount, isCovered  )


                Queen.Type ->
                    Queen(pieceColor, position, moveCount, isCovered)

                Knight.Type ->
                    Knight(pieceColor, position, moveCount, isCovered)

                Rook.Type ->
                    Rook(pieceColor, position, moveCount, isCovered)

                Bishop.Type ->
                    Bishop(pieceColor, position, moveCount, isCovered)

                else ->
                    throw IllegalArgumentException("Invalid piece type!")
            }
        }

        const val EncodedPieceLength = 4
    }

}

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

    var moveCount : Int

    val drawable: DrawableResource

    var position: IntOffset

    fun getAvailableMoves(pieces: List<Piece>): Set<IntOffset>

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

            val moveCount = 0

            return when (type) {
                Pawn.Type ->
                    Pawn(pieceColor, position,moveCount)
                King.Type ->
                    King(pieceColor, position, moveCount  )


                Queen.Type ->
                    Queen(pieceColor, position, moveCount)

                Knight.Type ->
                    Knight(pieceColor, position, moveCount)

                Rook.Type ->
                    Rook(pieceColor, position, moveCount)

                Bishop.Type ->
                    Bishop(pieceColor, position, moveCount)

                else ->
                    throw IllegalArgumentException("Invalid piece type!")
            }
        }

        const val EncodedPieceLength = 4
    }

}

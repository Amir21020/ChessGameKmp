package board

import androidx.compose.ui.unit.IntOffset
import pieces.King
import pieces.Piece

fun isTheKingInThreat(
    pieces: List<Piece>,
    piece: Piece,
    x: Int,
    y: Int,
): Boolean {
    val piecePosition = piece.position

    // Move the piece to the new position temporarily to check if the king is in threat
    piece.position = IntOffset(x = x, y = y)


    // Remove the piece from the list of pieces to get the correct available moves
    val newPieces = pieces.filter { it.position != piece.position }

    val isTheKingInThreat =  isKingInThreat(newPieces, pieces, piece.color)

    return isTheKingInThreat
}

private fun isKingInThreat(newPieces: List<Piece>,
                           pieces: List<Piece>,
                           color: Piece.Color): Boolean {
    val king = pieces.find { it is King && it.color == color } ?: return false
    val enemyMoves = getEnemyMoves(newPieces, pieces, color)
    return enemyMoves.contains(king.position)
}


private fun getEnemyMoves(
    newPieces: List<Piece>,
    pieces: List<Piece>,
    color: Piece.Color
) : List<IntOffset> = pieces.filter { it.color != color && it.type == 'Q' }
    .flatMap { it.getAvailableMoves(pieces = newPieces) }



fun isCheckmate(
    pieces: List<Piece>,
    playerTurn: Piece.Color,
): Boolean {
    val king = pieces.firstOrNull { it is King && it.color == playerTurn } ?: return false
    // Add the king's position to the list of available moves because the current king position can be a safe position
    val kingMoves = king.getAvailableMoves(pieces = pieces) + king.position

    val isCheckmate = kingMoves.all { kingMove ->
        isTheKingInThreat(
            pieces = pieces,
            piece = king,
            x = kingMove.x,
            y = kingMove.y
        )
    }

    return isCheckmate
}
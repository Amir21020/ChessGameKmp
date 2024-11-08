package board

import androidx.compose.ui.unit.IntOffset
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import pieces.King
import pieces.Piece

suspend fun isTheKingInThreat(
    pieces: List<Piece>,
    piece: Piece,
    x: Int,
    y: Int,
): Boolean {
    val piecePosition = piece.position

    // Move the piece to the new position temporarily to check if the king is in threat
    piece.position = IntOffset(x = x, y = y)

    val king = pieces.firstOrNull { it is King && it.color == piece.color } ?: return false

    // Remove the piece from the list of pieces to get the correct available moves
    val newPieces = pieces.filter { it.position != piece.position }

    val enemyPieces = newPieces.filter { it.color != piece.color }
    val enemyMoves = enemyPieces.flatMap { it.getCheckThreateningMoves(pieces = newPieces) }

    val isTheKingInThreat = enemyMoves.any { it == king.position }

    // Move the piece back to its original position
    piece.position = piecePosition

    return isTheKingInThreat
}



suspend fun isCheckmate(
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
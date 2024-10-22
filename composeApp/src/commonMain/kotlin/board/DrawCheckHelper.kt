package board

import pieces.King
import pieces.Piece


fun isCheckDraw(pieces: List<Piece>,
                playerTurn: Piece.Color) : Boolean {
    val king = pieces.firstOrNull { it is King && it.color == playerTurn } ?: return false
    val kingMoves = king.getAvailableMoves(pieces = pieces) + king.position
    return false
}
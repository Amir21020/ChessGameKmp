package pieces.dsl

import androidx.compose.ui.unit.IntOffset
import board.isTheKingInThreat
import pieces.Piece

enum class CastlingMovement {
    Left,
    Right
}

suspend fun Piece.getCastlingMoves(
    pieces: List<Piece>,
    movement: CastlingMovement,
    maxMovements: Int,
): Set<IntOffset> {
    return getCMoves(
        pieces = pieces,
        getPosition = {
            when (movement) {
                CastlingMovement.Left ->
                    IntOffset(x = position.x - it, y = position.y)
                CastlingMovement.Right ->
                    IntOffset(x = position.x + it, y = position.y)
            }
        },
        maxMovements = maxMovements,
    )
}
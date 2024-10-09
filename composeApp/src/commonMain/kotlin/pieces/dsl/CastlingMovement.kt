package pieces.dsl

import androidx.compose.ui.unit.IntOffset
import board.isTheKingInThreat
import pieces.Piece

//enum class CastlingMovement {
//    Left,
//    Right
//}
//
//fun Piece.getCastlingMoves(
//    pieces: List<Piece>,
//    movement: CastlingMovement,
//    maxMovements: Int,
//): Set<IntOffset> {
//    return getCMoves(
//        pieces = pieces,
//        getPosition = {
//            when (movement) {
//                CastlingMovement.Left ->
//                    IntOffset(x = position.x - 2, y = position.y)
//                CastlingMovement.Right ->
//                    IntOffset(x = position.x + 2, y = position.y)
//            }
//        },
//        maxMovements = maxMovements,
//    )
//}
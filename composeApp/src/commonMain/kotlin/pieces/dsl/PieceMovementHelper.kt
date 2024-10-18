package pieces.dsl

import androidx.compose.ui.unit.IntOffset
import board.BoardXCoordinates
import board.BoardYCoordinates
import board.isTheKingInThreat
import pieces.Piece

fun Piece.getMoves(
    pieces: List<Piece>,
    getPosition: (Int) -> IntOffset,
    maxMovements: Int,
    canCapture: Boolean,
    captureOnly: Boolean,
): Set<IntOffset> {
    val moves = mutableSetOf<IntOffset>()

    for (i in 1..maxMovements) {
        val targetPosition = getPosition(i)

        if (targetPosition.x !in BoardXCoordinates || targetPosition.y !in BoardYCoordinates)
            break

        val targetPiece = pieces.find { it.position == targetPosition }

        val kingPosition = pieces.find { it.type == 'K' && it.color != this.color}?.position



        if (targetPiece != null) {
            if (targetPiece.color != this.color && canCapture && kingPosition != targetPosition)
                moves.add(targetPosition)

            break
        } else if (captureOnly) {
            break
        } else {
            moves.add(targetPosition)
        }
    }

    return moves
}

fun Piece.getLMoves(
    pieces: List<Piece>,
): MutableSet<IntOffset> {
    val moves = mutableSetOf<IntOffset>()

    val offsets = listOf(
        IntOffset(-1, -2),
        IntOffset(1, -2),
        IntOffset(-2, -1),
        IntOffset(2, -1),
        IntOffset(-2, 1),
        IntOffset(2, 1),
        IntOffset(-1, 2),
        IntOffset(1, 2),
    )

    for (offset in offsets) {
        val targetPosition = position + offset

        if (targetPosition.x !in BoardXCoordinates || targetPosition.y !in BoardYCoordinates)
            continue

        val kingPosition = pieces.find { it.type == 'K' && it.color != this.color}?.position

        val targetPiece = pieces.find { it.position == targetPosition }

        if (kingPosition != targetPosition && (targetPiece == null || targetPiece.color != this.color )) {
            moves.add(targetPosition)
        }
    }

    return moves
}

fun Piece.getCMoves(
    pieces : List<Piece>,
    getPosition: (Int) -> IntOffset,
    maxMovements: Int,
) : MutableSet<IntOffset> {
    val moves = mutableSetOf<IntOffset>()

    for (i in 1..maxMovements) {
        var targetPosition = getPosition(i)

        if (targetPosition.x !in BoardXCoordinates || targetPosition.y !in BoardYCoordinates)
            break

        val targetPiece = pieces.find { it.position == targetPosition }

        if (targetPiece != null)
            break


        if (i == maxMovements) {
            val rooks = pieces.filter { it.type == 'R' && it.color == this.color && it.moveCount == 0 }

            if(rooks.isEmpty() || this.moveCount != 0 || isTheKingInThreat(pieces, this, this.position.x, this.position.y))
                break;

            if (maxMovements == 3){
                targetPosition = getPosition(i - 1)

                rooks.first().position = IntOffset(targetPosition.x + 1, targetPosition.y)
            }
            else{
                rooks.last().position = IntOffset(targetPosition.x - 1, targetPosition.y)
            }

            moves.add(targetPosition)

        }

        }

        return moves
    }

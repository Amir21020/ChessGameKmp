package pieces.dsl

import androidx.compose.ui.unit.IntOffset
import board.BoardXCoordinates
import board.BoardYCoordinates
import board.isCheckmate
import board.isTheKingInThreat
import kotlinx.coroutines.*
import pieces.Piece
suspend fun Piece.getMoves(
    pieces: List<Piece>,
    getPosition: (Int) -> IntOffset,
    maxMovements: Int,
    canCapture: Boolean,
    captureOnly: Boolean
): Set<IntOffset> {
    val moves = mutableSetOf<IntOffset>()

    for (i in 1..maxMovements) {
        val targetPosition = getPosition(i)


        // Check if the target position is within board limits
        if (targetPosition.x !in BoardXCoordinates || targetPosition.y !in BoardYCoordinates) {
            break
        }

        val targetPiece = pieces.find { it.position == targetPosition }
        val isKing = this.type == 'K'


        val isCheck = isTheKingInThreat(pieces,this,targetPosition.x,targetPosition.y)


        if(!isCheck){
            if (targetPiece != null) {
                if (targetPiece.color != this.color && canCapture)
                    moves.add(targetPosition)
                break
            } else if (captureOnly) {
                break
            } else {
                moves.add(targetPosition)
            }
        }


    }
    return moves
}

suspend fun Piece.getMovesThatCauseCheck(
    pieces: List<Piece>,
    getPosition: (Int) -> IntOffset,
    maxMovements: Int,
    canCapture: Boolean,
    captureOnly: Boolean,
): Set<IntOffset>
{
    var lastPosition: IntOffset? = null

    val moves = mutableSetOf<IntOffset>()

    for (i in 1..maxMovements) {
        val targetPosition = getPosition(i)

        // Проверка выхода за пределы доски
        if (targetPosition.x !in BoardXCoordinates || targetPosition.y !in BoardYCoordinates) {
            break
        }

        // Поиск цели в позиции
        val targetPiece = pieces.find { it.position == targetPosition }

        // Временно перемещаем фигуру
        val isKingInThreatNow = false
        // Если король не под угрозой, добавляем позицию в возможные ходы
        if (!isKingInThreatNow) {
            if (targetPiece != null) {

                // Если есть цель и она противника, добавляем в возможные ходы (если захват разрешен)
                if (targetPiece.color != this.color && canCapture) {
                    moves.add(targetPosition)
                }
                if(targetPiece.color == this.color){
                    targetPiece.isCovered = true
                }
                break
            } else if (captureOnly) {
                // Прерываем, если ищем только захваты
                break
            } else {
                // Добавляем позицию как свободную
                lastPosition = targetPosition
            }

        }
    }

    if(lastPosition == null) return moves

    moves.add(lastPosition)


    return moves;
}






private suspend fun Piece.isKingInThreatAfterMove(
    targetPosition: IntOffset,
    pieces: List<Piece>
): Boolean {
    val originalPosition = this.position  // Сохраняем оригинальную позицию
    this.position = targetPosition         // Перемещаем фигуру в новую позицию

    // Проверяем, в угрозе ли король после потенциального хода
    val isKingInThreatNow =
        isTheKingInThreat(pieces, this, targetPosition.x, targetPosition.y)

    // Возвращаем фигуру обратно на оригинальную позицию
    this.position = originalPosition
    return isKingInThreatNow
}


suspend fun Piece.getLMovesThatCauseCheck(
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

        val targetPiece = pieces.find { it.position == targetPosition }

        val isKingInThreatAfterMove =  false

        if (!isKingInThreatAfterMove ){
            if (targetPiece != null) {
                if(targetPiece.color != this.color){
                    moves.add(targetPosition)
                }
                else{
                    targetPiece.isCovered = true
                }
            }
            else{
                moves.add(targetPosition)
            }
        }
    }

    return moves
}

suspend fun Piece.getLMoves(
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

        val targetPiece = pieces.find { it.position == targetPosition }

        val isCheck = isTheKingInThreat(pieces,this,targetPosition.x,targetPosition.y)

        if (!isCheck ){
            if (targetPiece == null || targetPiece.color != this.color)
                moves.add(targetPosition)
        }
    }

    return moves
}
suspend fun Piece.getCMoves(pieces: List<Piece>, getPosition: (Int) -> IntOffset, maxMovements: Int): MutableSet<IntOffset>
{
    val moves = mutableSetOf<IntOffset>()

    for (i in 1..maxMovements) {
        var targetPosition = getPosition(i)

        // Проверка выхода за пределы доски
        if (targetPosition.x !in BoardXCoordinates && targetPosition.y !in BoardYCoordinates) break

        // Поиск фигуры на целевой позиции
        val targetPiece = pieces.find { it.position == targetPosition }


        val isKingInThreatNow =
            isTheKingInThreat(pieces, this,
                targetPosition.x, targetPosition.y)


        if (targetPiece != null || isKingInThreatNow) {
            break
        }

        // Обрабатываем ситуацию с рокировкой
        if (i == maxMovements) {
            val rooks = pieces.filter { it.type == 'R' && it.color == this.color && it.moveCount == 0 }

            if (rooks.isEmpty() ||  this.moveCount != 0) {
                break
            }

            // Обработка рокировки
            if (maxMovements == 3) {
                targetPosition = getPosition(i - 1)
                rooks.first().position = IntOffset(targetPosition.x + 1, targetPosition.y)
                moves.add(targetPosition)
            } else {
                rooks.last().position = IntOffset(targetPosition.x - 1, targetPosition.y)
                moves.add(targetPosition)
            }
        }

    }

    return moves
}

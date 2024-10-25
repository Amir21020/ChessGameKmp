package pieces.dsl

import androidx.compose.ui.unit.IntOffset
import board.BoardXCoordinates
import board.BoardYCoordinates
import board.isCheckmate
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

        // Проверка выхода за пределы доски
        if (targetPosition.x !in BoardXCoordinates || targetPosition.y !in BoardYCoordinates) {
            break
        }

        // Поиск цели в позиции
        val targetPiece = pieces.find { it.position == targetPosition }

        // Временно перемещаем фигуру
        val originalPosition = this.position  // Сохраняем оригинальную позицию
        this.position = targetPosition         // Перемещаем фигуру в новую позицию

        // Проверяем, в угрозе ли король после потенциального хода
        val isKingInThreatNow = isTheKingInThreat(pieces, this, targetPosition.x, targetPosition.y)

        // Возвращаем фигуру обратно на оригинальную позицию
        this.position = originalPosition

        // Если король не под угрозой, добавляем позицию в возможные ходы
        if (!isKingInThreatNow) {
            if (targetPiece != null) {
                // Если есть цель и она противника, добавляем в возможные ходы (если захват разрешен)
                if (targetPiece.color != this.color && canCapture) {
                    moves.add(targetPosition)
                }
                // Прерываем цикл, так как не можем двигаться дальше
                break
            } else if (captureOnly) {
                // Прерываем, если ищем только захваты
                break
            } else {
                // Добавляем позицию как свободную
                moves.add(targetPosition)
            }
        }
    }

    return moves
}



fun Piece.getLMoves(pieces: List<Piece>): MutableSet<IntOffset> {
    val moves = mutableSetOf<IntOffset>()

    // Получаем позицию короля противника
    val king = pieces.find { it.type == 'K' && it.color != this.color }?.position

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

        // Проверка выхода за пределы доски
        if (targetPosition.x !in BoardXCoordinates || targetPosition.y !in BoardYCoordinates) continue

        // Поиск фигуры на целевой позиции
        val targetPiece = pieces.find { it.position == targetPosition }

        // Проверяем, не находится ли король под угрозой при этом движении
        val originalPosition = this.position
        this.position = targetPosition // Временное перемещение фигуры

        // Проверка угрозы для короля
        if (king != targetPosition && (targetPiece == null || targetPiece.color != this.color) && !isTheKingInThreat(pieces, this, targetPosition.x, targetPosition.y)) {
            moves.add(targetPosition)
        }

        // Возврат фигуры в оригинальное состояние
        this.position = originalPosition
    }

    return moves
}
fun Piece.getCMoves(pieces: List<Piece>, getPosition: (Int) -> IntOffset, maxMovements: Int): MutableSet<IntOffset> {
    val moves = mutableSetOf<IntOffset>()

    for (i in 1..maxMovements) {
        val targetPosition = getPosition(i)

        // Проверка выхода за пределы доски
        if (targetPosition.x !in BoardXCoordinates || targetPosition.y !in BoardYCoordinates) break

        // Поиск фигуры на целевой позиции
        val targetPiece = pieces.find { it.position == targetPosition }

        // Проверяем угрозу для короля
        val originalPosition = this.position
        this.position = targetPosition // Временное перемещение фигуры

        // Проверка угрозы для короля
        if (targetPiece == null && !isTheKingInThreat(pieces, this, targetPosition.x, targetPosition.y)) {
            moves.add(targetPosition)
        }

        // Обрабатываем ситуацию с рокировкой
        if (i == maxMovements) {
            val rooks = pieces.filter { it.type == 'R' && it.color == this.color && it.moveCount == 0 }

            if (rooks.isEmpty() || (this.moveCount != 0 && isTheKingInThreat(pieces, this, this.position.x, this.position.y))) {
                break
            }

            // Обработка рокировки
            if (maxMovements == 3) {
                val rookTargetPosition = getPosition(i - 1)
                rooks.first().position = IntOffset(rookTargetPosition.x + 1, rookTargetPosition.y)
            } else {
                rooks.last().position = IntOffset(targetPosition.x - 1, targetPosition.y)
            }

            moves.add(targetPosition)
        }

        // Возврат фигуры в оригинальное состояние
        this.position = originalPosition

        // Прерываем цикл, если на позиции есть другая фигура
        if (targetPiece != null) break
    }

    return moves
}

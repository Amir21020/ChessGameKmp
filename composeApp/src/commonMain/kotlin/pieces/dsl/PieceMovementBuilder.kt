package pieces.dsl

import androidx.compose.ui.unit.IntOffset
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import pieces.Piece

fun Piece.getPieceMoves(
    pieces: List<Piece>,
    block: PieceMovementBuilder.() -> Unit
): Set<IntOffset> {
    val builder = PieceMovementBuilder(
        piece = this,
        pieces = pieces
    )
    builder.block()
    return builder.build()
}

class PieceMovementBuilder(
    private val piece: Piece,
    private val pieces: List<Piece>,
) {
    private val moves = mutableSetOf<IntOffset>()

    suspend fun straightMoves(
        maxMovements: Int = 7,
        canCapture: Boolean = true,
        captureOnly: Boolean = false,
    ) {
        StraightMovement.entries.forEach { movement ->
            straightMoves(
                movement = movement,
                maxMovements = maxMovements,
                canCapture = canCapture,
                captureOnly = captureOnly,
            )
        }
    }

    suspend fun straightMoves(
        movement: StraightMovement,
        maxMovements: Int = 7,
        canCapture: Boolean = true,
        captureOnly: Boolean = false,
    ) {
        moves.addAll(
            piece.getStraightMoves(
                pieces = pieces,
                movement = movement,
                maxMovements = maxMovements,
                canCapture = canCapture,
                captureOnly = captureOnly,
            )
        )
    }

    suspend fun openStraightMoves(
        maxMovements: Int = 7,
        canCapture: Boolean = true,
        captureOnly: Boolean = false,
        check: Boolean = true
    ){
        StraightMovement.entries.forEach { movement ->
            openStraightMoves(
                movement = movement,
                maxMovements = maxMovements,
                canCapture = canCapture,
                captureOnly = captureOnly,
                check = check
            )
        }
    }

    suspend fun openStraightMoves(
        movement: StraightMovement,
        maxMovements: Int = 7,
        canCapture: Boolean = true,
        captureOnly: Boolean = false,
        check: Boolean = true
    ){
        moves.addAll(
            piece.getStraightMoves(
                pieces = pieces,
                movement = movement,
                maxMovements = maxMovements,
                canCapture = canCapture,
                captureOnly = captureOnly,
                check = check
            )
        )
    }



   suspend fun castlingMoves(
    ) {
        CastlingMovement.entries.forEach { movement ->
            castlingMoves(
                movement = movement,
            )
        }
    }

    private suspend fun castlingMoves(
        movement: CastlingMovement,
    ){
        val maxMovements = when (movement) {
            CastlingMovement.Left -> 3
            CastlingMovement.Right -> 2
        }
        moves.addAll(
            piece.getCastlingMoves(
                pieces = pieces,
                movement = movement,
                maxMovements = maxMovements
            )
        )
    }

    suspend fun diagonalMoves(
        maxMovements: Int = 7,
        canCapture: Boolean = true,
        captureOnly: Boolean = false,
    ) {
        DiagonalMovement.entries.forEach { movement ->
            diagonalMoves(
                movement = movement,
                maxMovements = maxMovements,
                canCapture = canCapture,
                captureOnly = captureOnly,
            )
        }
    }


    suspend fun diagonalMoves(
        movement: DiagonalMovement,
        maxMovements: Int = 7,
        canCapture: Boolean = true,
        captureOnly: Boolean = false,
    ) {
        moves.addAll(
            piece.getDiagonalMoves(
                pieces = pieces,
                movement = movement,
                maxMovements = maxMovements,
                canCapture = canCapture,
                captureOnly = captureOnly,
            )
        )
    }

    suspend fun openDiagonalMoves(
        maxMovements: Int = 7,
        canCapture: Boolean = true,
        captureOnly: Boolean = false,
        check: Boolean = true
    ) {
        DiagonalMovement.entries.forEach { movement ->
            openDiagonalMoves(
                movement = movement,
                maxMovements = maxMovements,
                canCapture = canCapture,
                captureOnly = captureOnly,
                check = check
            )
        }
    }

    suspend fun openDiagonalMoves(
        movement: DiagonalMovement,
        maxMovements: Int = 7,
        canCapture: Boolean = true,
        captureOnly: Boolean = false,
        check: Boolean = true
    ){
        moves.addAll(
            piece.getDiagonalMoves(
                pieces = pieces,
                movement = movement,
                maxMovements = maxMovements,
                canCapture = canCapture,
                captureOnly = captureOnly,
                check = check
            )
        )
    }

    suspend fun getLMoves() {
        moves.addAll(
            piece.getLMoves(
                pieces = pieces,
            )
        )
    }

    suspend fun openGetLMoves(){
        moves.addAll(
            piece.getLMovesThatCauseCheck(
                pieces = pieces,
            )
        )
    }

    fun build(): Set<IntOffset> = moves
}
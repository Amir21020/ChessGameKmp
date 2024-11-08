package pieces

import androidx.compose.ui.unit.IntOffset
import chessgamekmp.composeapp.generated.resources.Res
import chessgamekmp.composeapp.generated.resources.pawn_black
import chessgamekmp.composeapp.generated.resources.pawn_white
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.jetbrains.compose.resources.DrawableResource
import pieces.dsl.DiagonalMovement
import pieces.dsl.StraightMovement
import pieces.dsl.getPieceMoves

class Pawn(
    override val color: Piece.Color,
    override var position: IntOffset, override var moveCount: Int, override var isCovered: Boolean,
): Piece {

    override val type: Char = Type

    override suspend  fun getAvailableMoves(pieces: List<Piece>): Set<IntOffset> {
        val isFirstMove =
            position.y == 2 && color.isWhite ||
                    position.y == 7 && color.isBlack

        return coroutineScope {
            getPieceMoves(pieces) {
                launch {
                    straightMoves(
                        movement = if (color.isWhite) StraightMovement.Up else StraightMovement.Down,
                        maxMovements = if (isFirstMove) 2 else 1,
                        canCapture = false,
                    )

                    diagonalMoves(
                        movement = if (color.isWhite) DiagonalMovement.UpRight else DiagonalMovement.DownRight,
                        maxMovements = 1,
                        captureOnly = true,
                    )

                    diagonalMoves(
                        movement = if (color.isWhite) DiagonalMovement.UpLeft else DiagonalMovement.DownLeft,
                        maxMovements = 1,
                        captureOnly = true,
                    )

                }
            }

        }
    }

    override suspend  fun getCheckThreateningMoves(pieces: List<Piece>): Set<IntOffset> {
        val isFirstMove =
            position.y == 2 && color.isWhite ||
                    position.y == 7 && color.isBlack
        return coroutineScope {
            getPieceMoves(pieces) {
                launch {
                    openStraightMoves(
                        movement = if (color.isWhite) StraightMovement.Up else StraightMovement.Down,
                        maxMovements = if (isFirstMove) 2 else 1,
                        canCapture = false,
                    )

                    openDiagonalMoves(
                        movement = if (color.isWhite) DiagonalMovement.UpRight else DiagonalMovement.DownRight,
                        maxMovements = 1,
                        captureOnly = true,
                    )

                    openDiagonalMoves(
                        movement = if (color.isWhite) DiagonalMovement.UpLeft else DiagonalMovement.DownLeft,
                        maxMovements = 1,
                        captureOnly = true,
                    )
                }
            }
        }
    }






    companion object {
        const val Type = 'P'
    }
    override val drawable: DrawableResource =
        if (color.isWhite)
            Res.drawable.pawn_white
        else
            Res.drawable.pawn_black


}
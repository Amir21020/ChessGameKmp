package pieces

import androidx.compose.ui.unit.IntOffset
import chessgamekmp.composeapp.generated.resources.Res
import chessgamekmp.composeapp.generated.resources.rook_black
import chessgamekmp.composeapp.generated.resources.rook_white
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.DrawableResource
import pieces.dsl.getPieceMoves

class Rook(
    override val color: Piece.Color,
    override var position: IntOffset, override var moveCount: Int, override var isCovered: Boolean,
): Piece {

    override val type: Char = Type

    override val drawable: DrawableResource =
        if (color.isWhite)
            Res.drawable.rook_white
        else
            Res.drawable.rook_black

    override suspend  fun getAvailableMoves(pieces: List<Piece>): Set<IntOffset> =
        coroutineScope {
            getPieceMoves(pieces) {
                launch {
                    straightMoves()
                }
            }
        }




    override suspend  fun getCheckThreateningMoves(pieces: List<Piece>): Set<IntOffset> =
        coroutineScope {
            getPieceMoves(pieces) {
                launch {
                    openStraightMoves()
                }
            }
        }


    companion object {
        const val Type = 'R'
    }

}
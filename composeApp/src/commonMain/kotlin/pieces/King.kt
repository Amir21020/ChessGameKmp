package pieces

import androidx.compose.ui.unit.IntOffset
import chessgamekmp.composeapp.generated.resources.Res
import chessgamekmp.composeapp.generated.resources.bishop_black
import chessgamekmp.composeapp.generated.resources.bishop_white
import chessgamekmp.composeapp.generated.resources.king_black
import chessgamekmp.composeapp.generated.resources.king_white
import chessgamekmp.composeapp.generated.resources.queen_black
import chessgamekmp.composeapp.generated.resources.queen_white
import chessgamekmp.composeapp.generated.resources.rook_black
import chessgamekmp.composeapp.generated.resources.rook_white
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.DrawableResource
//import pieces.dsl.getCastlingMoves
import pieces.dsl.getPieceMoves

class King(
    override val color: Piece.Color,
    override var position: IntOffset, override var moveCount: Int, override var isCovered: Boolean,
): Piece {
    var isCheck : Boolean = false
    override val type: Char = Type

    override val drawable: DrawableResource =
        if (color.isWhite)
            Res.drawable.king_white
        else
            Res.drawable.king_black

    override suspend fun getAvailableMoves(pieces: List<Piece>): Set<IntOffset> =
        coroutineScope {
            getPieceMoves(pieces){
                launch {
                    straightMoves(
                        maxMovements = 1,
                    )
                    diagonalMoves(
                        maxMovements = 1,
                    )
                    castlingMoves()
                }
            }
        }

    override suspend  fun getCheckThreateningMoves(pieces: List<Piece>): Set<IntOffset>
    = coroutineScope {
        getPieceMoves(pieces){

        }
    }

    companion object {
        const val Type = 'K'
    }

}
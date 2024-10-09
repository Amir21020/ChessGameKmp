package data.room.models

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert

@Dao
interface ChessBoardSnapshotsDao{

    @Upsert
    fun Upsert(chess_board_snapshots: chess_board_snapshots)


    @Query("Select * FROM chess_board_snapshots")
    fun getAllChessBoard() : List<chess_board_snapshots>
}
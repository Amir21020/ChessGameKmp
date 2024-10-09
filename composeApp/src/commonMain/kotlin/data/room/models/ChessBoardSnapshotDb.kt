package data.room.models

import androidx.room.Database
import androidx.room.RoomDatabase


@Database(
    entities = [chess_board_snapshots::class],
    version = 1,
)
abstract class ChessBoardSnapshotDb : RoomDatabase()
{
    abstract fun chessBoardSnapshotsDao() : ChessBoardSnapshotsDao
}
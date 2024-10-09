package data.room.models

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class chess_board_snapshots(
    @PrimaryKey(autoGenerate = true) val Id: Int = 0,
    var SerializedObject : String,
)
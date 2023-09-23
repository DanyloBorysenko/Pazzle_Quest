import androidx.annotation.DrawableRes
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.unit.IntOffset

data class PuzzleCell(
    val number: Int,
    val row: Int,
    val column: Int,
    var size: Int,
    @DrawableRes val imageRes : Int?
) {
    var offsetState by mutableStateOf(IntOffset.Zero)

    val actualColumn : Int
        get() = offsetState.x / size + column

    val actualRow : Int
        get() = offsetState.y / size + row
}
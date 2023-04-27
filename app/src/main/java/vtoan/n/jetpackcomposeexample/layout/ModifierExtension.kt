package vtoan.n.jetpackcomposeexample.layout

import android.text.Layout.Alignment
import android.util.Log
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.AlignmentLine
import androidx.compose.ui.layout.FirstBaseline
import androidx.compose.ui.layout.LastBaseline
import androidx.compose.ui.layout.layout
import androidx.compose.ui.unit.Dp

fun Modifier.firstBaselineToTop(firstBaselineToTop: Dp) = this.then(
    layout { measurable, constraints ->
        // Measure the composable
        val placeable = measurable.measure(constraints)

        // Check the composable has a first baseline
        check(placeable[FirstBaseline] != AlignmentLine.Unspecified)
        val firstBaseline = placeable[FirstBaseline]

        // Check the composable has a last baseline
        check(placeable[LastBaseline] != AlignmentLine.Unspecified)
        val lastBaseline = placeable[LastBaseline]
        Log.d("firstBaselineToTop", "firstBaseline: $firstBaseline, lastBaseline: $lastBaseline")

        // Height of the composable with padding  - first baseline
        val placeableY = firstBaselineToTop.roundToPx() - firstBaseline
        val height = placeable.height + placeableY

        layout(placeable.width, height) {
            placeable.placeRelative(0, placeableY)
        }
    }
)
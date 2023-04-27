package vtoan.n.jetpackcomposeexample.layout

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

@Composable
fun CustomLayoutApp() {
    CustomLayoutView {
        CustomTextWithFirstBaseline()
        Card(modifier = Modifier
            .background(Color.Cyan)
            .padding(16.dp).height(120.dp), shape = RectangleShape) {
            MyBasicColumn {
                Text(text = "Text 1")
                Text(text = "Text 2")
                Text(text = "Text 3")
                Text(text = "Text 4")
            }
        }
    }
}

@Composable
private fun CustomLayoutView(content: @Composable () -> Unit) {
    Column(
        Modifier
            .background(Color.Cyan)
            .fillMaxSize()
            .verticalScroll(rememberScrollState())) {
        content()
    }
}

@Composable
private fun CustomTextWithFirstBaseline() {
    Text(
        text = "first baseline 1",
        Modifier
            .background(Color(0xFF8BC34A))
            .padding(top = 32.dp)
    )

    Text(
        text = "first baseline",
        Modifier
            .background(Color(0xFF8BC34A))
            .firstBaselineToTop(32.dp)
    )
}

@Composable
private fun MyBasicColumn(modifier: Modifier = Modifier, content: @Composable () -> Unit) {
    Layout(modifier = modifier, content = content) {measureables, constraints ->
        // Don't constrain child views further, measure them with given constraints
        // List of measured children
        val placeables = measureables.map { measurable ->
            measurable.measure(constraints)
        }

        // Set the size of the layout as big as it can
        layout(constraints.maxWidth, constraints.maxHeight) {
            // Track the y co-ord we have placed children up to
            var yPosition = 0

            // Place children in the parent layout
            placeables.forEach { placeable ->
                // Position item on the screen
                placeable.placeRelative(x = 0, y = yPosition)

                // Record the y co-ord placed up to
                yPosition += placeable.height
            }
        }
    }
}

@Preview
@Composable
private fun CustomLayoutPreview() {
    CustomLayoutApp()
}
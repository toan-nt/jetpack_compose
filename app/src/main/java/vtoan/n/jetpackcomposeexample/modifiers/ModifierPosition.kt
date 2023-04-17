package vtoan.n.jetpackcomposeexample.modifiers

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun ModifierPositionApp(content: @Composable () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(color = Color.Gray)
    ) {
        content()
    }
}

@Composable
fun ModifierPositionView() {
    Column {
        Text(text = "Hello world")
        Text(
            text = "Nguyen Van Toan", modifier = Modifier.offset(x = 20.dp),
            color = Color.White,
            textDecoration = TextDecoration.Underline,
            textAlign = TextAlign.Center,
            fontSize = 32.sp
        )

    }
}

@Composable
fun ModifierPositionTabIndicatorOffsetView() {

}

@Preview
@Composable
fun ModifierPositionPreview() {
    ModifierPositionApp {
        ModifierPositionView()
    }
}
package vtoan.n.jetpackcomposeexample.modifiers

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp


@Composable
private fun PaddingViewApp(content: @Composable () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(color = Color.Green)
    ) {
        content()
    }
}

@Composable
private fun PaddingView1() {
    Column(
        modifier = Modifier
            .padding(24.dp)
            .background(color = Color.Cyan)
    ) {
        Text(text = "Hello")
        Text(text = "Toan")
    }
}

@Composable
private fun PaddingView2() {
    Column(
        modifier = Modifier
            .background(color = Color.Cyan)
            .padding(24.dp)
            .fillMaxWidth()
    ) {
        Text(text = "Hello")
        Text(text = "Toan")
    }
}

@Composable
private fun PaddingView3() {
    Column(
        modifier = Modifier
            .background(color = Color.Cyan)
            .fillMaxWidth()
            .padding(24.dp)
    ) {
        Text(text = "Hello")
        Text(text = "Toan")
    }
}

@Composable
private fun PaddingFromBaselineView() {
    Column(
        modifier = Modifier
            .background(color = Color.Cyan)
    ) {
        Text(text = "Hello", modifier = Modifier.paddingFromBaseline(top = 150.dp))
        Text(text = "Toan")
    }
}

@Preview
@Composable
private fun PaddingPreview() {
    PaddingViewApp {
        PaddingView1()
    }
}

@Preview
@Composable
private fun PaddingPreview2() {
    PaddingViewApp {
        PaddingView2()
    }
}

@Preview
@Composable
private fun PaddingPreview3() {
    PaddingViewApp {
        PaddingView3()
    }
}

@Preview
@Composable
private fun PaddingFromBaselinePreview() {
    PaddingViewApp {
        PaddingFromBaselineView()
    }
}
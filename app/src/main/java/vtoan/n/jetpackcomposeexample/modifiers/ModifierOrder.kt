package vtoan.n.jetpackcomposeexample.modifiers

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

@Composable
private fun PaddingViewApp() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(color = Color.Green)
    ) {
        PaddingView1()
    }
}

@Composable
private fun PaddingView1() {
    Column(
        modifier = Modifier
            .padding(24.dp)
            .background(color = Color.Blue)
    ) {
        Text(text = "Hello")
        Text(text = "Toan")
    }
}

@Composable
private fun PaddingViewApp2() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(color = Color.Green)
    ) {
        PaddingView2()
    }
}

@Composable
private fun PaddingView2() {
    Column(
        modifier = Modifier
            .clickable {  }
            .background(color = Color.Blue)
            .padding(24.dp)
            .fillMaxWidth()
    ) {
        Text(text = "Hello")
        Text(text = "Toan")
    }
}

@Composable
private fun PaddingViewApp3() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(color = Color.Green)
    ) {
        PaddingView3()
    }
}

@Composable
private fun PaddingView3() {
    Column(
        modifier = Modifier
            .background(color = Color.Blue)
            .fillMaxWidth()
            .padding(24.dp)
            .clickable {  }
    ) {
        Text(text = "Hello")
        Text(text = "Toan")
    }
}

@Preview
@Composable
private fun PaddingPreview() {
    PaddingViewApp()
}

@Preview
@Composable
private fun PaddingPreview2() {
    PaddingViewApp2()
}

@Preview
@Composable
private fun PaddingPreview3() {
    PaddingViewApp3()
}
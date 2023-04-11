package vtoan.n.jetpackcomposeexample.modifiers

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import vtoan.n.jetpackcomposeexample.R

// START android_compose_modifiers_requiredSize
@Composable
fun RequiredSizeArtistCard() {
    Column(
        modifier = Modifier.background(
            color = Color.Red
        ).fillMaxSize()
    ) {
        Row(
            modifier = Modifier
                .size(width = 400.dp, height = 100.dp)
                .background(
                    color = Color.Black
                )
        ) {
            Image(
                painter = painterResource(id = R.drawable.ic_launcher_background),
                contentDescription = null,
                modifier = Modifier.requiredSize(150.dp)
            )
        }
    }
}

// END android_compose_modifiers_requiredSize

@Preview
@Composable
fun PreviewRequiredSizeArtistCard() {
    RequiredSizeArtistCard()
}

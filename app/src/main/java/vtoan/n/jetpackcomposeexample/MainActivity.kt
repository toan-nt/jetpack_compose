package vtoan.n.jetpackcomposeexample

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import vtoan.n.jetpackcomposeexample.modifiers.*
import vtoan.n.jetpackcomposeexample.modifiers.actions.*
import vtoan.n.jetpackcomposeexample.ui.theme.JetpackComposeExampleTheme

class MainActivity : ComponentActivity() {
    @ExperimentalFoundationApi
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            JetpackComposeExampleTheme {
                // A surface container using the 'background' color from the theme

                //ModifierClickableApp()
                //ModifierIndicationApp()
//                ModifierPointerApp()
                GestureActionsApp()
            }

        }
    }
}

@Composable
fun Greeting(name: String) {
    Text(
        text = "Hello $name!", modifier =
        Modifier.fillMaxSize()
    )
    Text(
        text = "Hello Toan", modifier =
        Modifier.fillMaxSize()
    )
}

@Composable
fun ArtistCard() {
    Column(
        modifier = Modifier.background(
            color = Color.Red
        )
    ) {
        Row(
            modifier = Modifier
                .size(width = 400.dp, height = 100.dp)
                .background(
                    color = Color.Green
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

@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    JetpackComposeExampleTheme {
        ModifierClickableApp()
    }
}
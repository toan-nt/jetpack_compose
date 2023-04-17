package vtoan.n.jetpackcomposeexample.modifiers.actions

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTransformGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.TransformOrigin
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import vtoan.n.jetpackcomposeexample.R
import java.lang.Math.PI
import java.text.DecimalFormat
import kotlin.math.cos
import kotlin.math.sin


@Composable
fun TransformGestureApp() {
    TransformGestureView {
        ZoomView()
        ZoomPanView()
        ZoomPanRotate()
        LimitedPanImage()
    }
}

@Composable
private fun TransformGestureView(content: @Composable () -> Unit) {
    Column(modifier = Modifier
        .fillMaxSize()
        .verticalScroll(rememberScrollState())) {
        content()
    }
}

val boxModifier = Modifier
    .fillMaxWidth()
    .height(250.dp)
    .clipToBounds()
    .background(Color.LightGray)

@Composable
private fun ZoomView() {
    var centroid by remember {
        mutableStateOf(Offset.Zero)
    }
    val decimalFormat = remember { DecimalFormat("0.0") }
    var zoom by remember {
        mutableStateOf(1f)
    }

    var transformDetailText by remember {
        mutableStateOf(
            "Use pinch gesture to zoom in or out.\n" +
                    "Centroid is position of center of touch pointers"
        )
    }

    val imageModifier = Modifier
        .fillMaxSize()
        .pointerInput(Unit) {
            detectTransformGestures { gestureCentroid, pan, gestureZoom, rotation ->
                centroid = gestureCentroid
                val newZoom = zoom * gestureZoom
                zoom = newZoom.coerceIn(0.5f..5f)
                transformDetailText = "Zoom: ${decimalFormat.format(zoom)}, centroid: $centroid"
            }
        }
        .drawWithContent {
            drawContent()
            drawCircle(color = Color.Red, center = centroid, radius = 20f)
        }
        .graphicsLayer {
            scaleX = zoom
            scaleY = zoom
        }

    ImageBox(modifier = boxModifier, imageModifier, imageRes = R.drawable.pexels_photo_16216144, text = transformDetailText)
}

@Composable
fun ZoomPanView() {
    val decimalFormat = remember { DecimalFormat("0.0") }
    var zoom by remember {
        mutableStateOf(1f)
    }
    var offset by remember {
        mutableStateOf(Offset.Zero)
    }
    var centroid by remember {
        mutableStateOf(Offset.Zero)
    }
    var transformDetailText by remember {
        mutableStateOf("Use pinch gesture to zoom, and move image with single finger.")
    }

    val imageModifier = Modifier
        .fillMaxSize()
        .pointerInput(Unit) {
            detectTransformGestures { gestureCentroid, gesturePan, gestureZoom, gestureRotation ->
                centroid = gestureCentroid
                val oldScale = zoom
                val newScale = zoom * gestureZoom
                offset = (offset + centroid / oldScale).rotateBy(gestureRotation) - (centroid / newScale + gesturePan / oldScale)
                zoom = newScale.coerceIn(0.5f..5f)
                transformDetailText = "Zoom: ${decimalFormat.format(zoom)}, centroid: $centroid"
            }
        }
        .drawWithContent {
            drawContent()
            drawCircle(color = Color.Red, center = centroid, radius = 20f)
        }
        .graphicsLayer {
            translationX = -offset.x * zoom
            translationY = -offset.y * zoom
            scaleX = zoom
            scaleY = zoom
        }

    ImageBox(modifier = boxModifier, imageModifier, imageRes = R.drawable.pexels_photo_16216144, text = transformDetailText)

}

@Composable
fun ZoomPanRotate() {
    val decimalFormat = remember { DecimalFormat("0.0") }

    var zoom by remember { mutableStateOf(1f) }
    var offset by remember { mutableStateOf(Offset.Zero) }
    var centroid by remember { mutableStateOf(Offset.Zero) }
    var angle by remember { mutableStateOf(0f) }

    var transformDetailText by remember {
        mutableStateOf(
            "Use pinch gesture to zoom, move image with single finger in either x or y coordinates.\n" +
                    "Rotate image using two fingers with twisting gesture."
        )
    }

    val imageModifier = Modifier
        .fillMaxSize()
        .pointerInput(Unit) {
            detectTransformGestures(
                onGesture = { gestureCentroid, gesturePan, gestureZoom, gestureRotate ->
                    val oldScale = zoom
                    val newScale = zoom * gestureZoom

                    // For natural zooming and rotating, the centroid of the gesture should
                    // be the fixed point where zooming and rotating occurs.
                    // We compute where the centroid was (in the pre-transformed coordinate
                    // space), and then compute where it will be after this delta.
                    // We then compute what the new offset should be to keep the centroid
                    // visually stationary for rotating and zooming, and also apply the pan.
                    offset = (offset + gestureCentroid / oldScale).rotateBy(gestureRotate) -
                            (gestureCentroid / newScale + gesturePan / oldScale)
                    zoom = newScale.coerceIn(0.5f..5f)
                    angle += gestureRotate

                    centroid = gestureCentroid
                    transformDetailText =
                        "Zoom: ${decimalFormat.format(zoom)}, centroid: $gestureCentroid\n" +
                                "angle: ${decimalFormat.format(angle)}, " +
                                "Rotate: ${decimalFormat.format(gestureRotate)}, pan: $gesturePan"
                }
            )
        }
        .drawWithContent {
            drawContent()
            drawCircle(color = Color.Red, center = centroid, radius = 20f)
        }
        .graphicsLayer {
            translationX = -offset.x * zoom
            translationY = -offset.y * zoom
            scaleX = zoom
            scaleY = zoom
            rotationZ = angle
            TransformOrigin(0f, 0f).also { transformOrigin = it }
        }

    ImageBox(boxModifier, imageModifier, R.drawable.pexels_photo_16216144, transformDetailText)
}

@Composable
private fun LimitedPanImage() {
    var zoom by remember { mutableStateOf(1f) }
    var pan by remember { mutableStateOf(Offset.Zero) }

    val imageModifier = boxModifier
        .pointerInput(Unit) {
            detectTransformGestures { _, panChange, zoomChange, _ ->

                zoom = (zoom * zoomChange).coerceIn(1f, 5f)


                val maxX = (size.width * (zoom - 1) / 2f)
                    .coerceAtLeast(0f)
                val maxY = (size.height * (zoom - 1) / 2f)
                    .coerceAtLeast(0f)

                val newOffset = pan + panChange.times(zoom)

                // This for TransformOrigin(0.5, 0.5f)
                // For TransformOrigin(0f, 0f) it's  coerceIn(0f, 2*Max)
                pan = Offset(
                    newOffset.x.coerceIn(-maxX, maxX),
                    newOffset.y.coerceIn(-maxY, maxY)
                )
            }
        }
        .graphicsLayer {
            this.scaleX = zoom
            this.scaleY = zoom
            this.translationX = pan.x
            this.translationY = pan.y
        }

    Image(
        modifier = imageModifier,
        painter = painterResource(id = R.drawable.pexels_photo_16216144),
        contentScale = ContentScale.FillBounds,
        contentDescription = null
    )
}

@Composable
private fun ImageBox(
    modifier: Modifier,
    imageModifier: Modifier,
    imageRes: Int,
    text: String,
    color: Color = Color.Green
) {
    Box(modifier = modifier, contentAlignment = Alignment.Center) {
        Image(
            painter = painterResource(id = imageRes),
            contentDescription = null,
            modifier = imageModifier,
            contentScale = ContentScale.Crop
        )
        Text(
            text = text,
            color = color,
            modifier = Modifier
                .fillMaxWidth()
                .background(Color(0x66000000))
                .padding(vertical = 2.dp)
                .align(Alignment.BottomStart)
        )
    }
}

fun Offset.rotateBy(angle: Float): Offset {
    val angleInRadians = angle * PI / 180
    return Offset(
        (x * cos(angleInRadians) - y * sin(angleInRadians)).toFloat(),
        (x * sin(angleInRadians) + y * cos(angleInRadians)).toFloat()
    )
}
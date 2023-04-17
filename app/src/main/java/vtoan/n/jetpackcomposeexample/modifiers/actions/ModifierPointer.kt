@file:OptIn(ExperimentalComposeUiApi::class)

package vtoan.n.jetpackcomposeexample.modifiers.actions

import android.util.Log
import android.view.MotionEvent
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.*
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.toSize
import kotlin.coroutines.cancellation.CancellationException
import kotlin.math.roundToInt

@Composable
fun ModifierPointerApp() {
    var currentParameter by remember { mutableStateOf("Toan") }
    ModifierPointerView {
        Text(text = "Detect ")
        DetectTapGesture(currentParameter) {
            currentParameter += 1
        }

        DetectPressAwait()
        PointerInteropFilter()
        DetectDragGesture()
        DetectDragGesture2()
        DetectDragGestureAfterLongPress()
        DetectVerticalDragGestures()
        DetectHorizontalDragGestures()
    }
}

@Composable
private fun ModifierPointerView(content: @Composable () -> Unit) {
    Column(
        Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())) {
        content()
    }
}

private val modifier = Modifier
    .fillMaxWidth()
    .height(64.dp)
    .padding(8.dp)
    .shadow(elevation = 2.dp, shape = RoundedCornerShape(8.dp), clip = true)

@Composable
private fun DetectTapGesture(parameter: String, onPress: () -> Unit) {
    val currentParameter by rememberUpdatedState(parameter)
    val gestureColor by remember { mutableStateOf(Color.White) }
    val pointerModifier =
        Modifier
            .then(modifier)
            .background(gestureColor, shape = RoundedCornerShape(8.dp))
            .pointerInput(Unit) {
                Log.d("pointerModifier", "pointerInput")
                // This pointerInput block will never restart since
                // it specifies a key of `Unit`, which never changes
                this.detectTapGestures(onTap = { offset ->

                }, onDoubleTap = {

                }, onLongPress = {

                }, onPress = {
                    performAction(currentParameter)
                    onPress()
                })

                // ...however, currentParameter is updated out from under this running
                // pointerInput suspend block by rememberUpdatedState, and will always
                // contain the latest value updated by the composition when a tap
                // is detected here.

            }

    Box(modifier = pointerModifier) {
        Text(text = "Click: $currentParameter")
    }
}

private fun performAction(parameter: String) {
    Log.d("performAction", "parameter: $parameter")
}

@Composable
private fun DetectPressAwait() {
    var currentValue by remember { mutableStateOf("Release press in our out of bounds") }
    val gestureColor by remember { mutableStateOf(Color.White) }
    val pointerModifier =
        Modifier
            .then(modifier)
            .background(gestureColor, shape = RoundedCornerShape(8.dp))
            .pointerInput(Unit) {
                Log.d("pointerModifier", "pointerInput")

                // This pointerInput block will never restart since
                // it specifies a key of `Unit`, which never changes
                this.detectTapGestures(onTap = { offset ->

                }, onDoubleTap = {

                }, onLongPress = {

                }, onPress = {
                    currentValue = "onPress"
                    // Waits for the press to be released before returning.
                    // If the press was released, true is returned, or if the gesture was
                    // canceled by motion being consumed by another gesture, false is returned .
                    val released = try {
                        tryAwaitRelease()
                    } catch (c: CancellationException) {
                        false
                    }

                    currentValue = if (released) {
                        "onPress Released"

                    } else {
                        "onPress canceled"
                    }
                })
            }

    Box(modifier = pointerModifier) {
        Text(text = "Click: $currentValue")
    }
}

@Composable
private fun PointerInteropFilter() {
    var currentValue by remember { mutableStateOf("PointerInteropFilter") }
    val gestureColor by remember { mutableStateOf(Color.White) }
    val pointerModifier =
        Modifier
            .then(modifier)
            .background(gestureColor, shape = RoundedCornerShape(8.dp))
            .pointerInteropFilter { event ->
                Log.d("pointerModifier", "pointerInput")
                when (event.action) {
                    MotionEvent.ACTION_DOWN -> {
                        currentValue =
                            "ACTION_DOWN " + "rawX: ${event.rawX}, rawY: ${event.rawY}, " + "x: ${event.x}, y: ${event.y}"
                    }
                    MotionEvent.ACTION_MOVE -> {
                        currentValue =
                            "ACTION_MOVE " +
                                    "rawX: ${event.rawX.toInt()}, rawY: ${event.rawY.toInt()}, " +
                                    "x: ${event.x.toInt()}, y: ${event.y.toInt()}"
                    }
                    MotionEvent.ACTION_UP -> {
                        currentValue =
                            "ACTION_UP " +
                                    "rawX: ${event.rawX.toInt()}, rawY: ${event.rawY.toInt()}, " +
                                    "x: ${event.x.toInt()}, y: ${event.y.toInt()}"
                    }
                    else -> false
                }
                true
            }

    Box(modifier = pointerModifier) {
        Text(text = "Click: $currentValue")
    }
}

@Composable
private fun DetectDragGesture() {
    var currentValue by remember { mutableStateOf("Current Value") }
    var currentDetailValue by remember {
        mutableStateOf("Current Detail Value")
    }
    val gestureColor by remember { mutableStateOf(Color.White) }
    val pointerModifier = Modifier
        .then(modifier)
        .background(gestureColor, shape = RoundedCornerShape(8.dp))
        .pointerInput(Unit) {
            detectDragGestures(
                onDragStart = { offset ->
                    currentValue = "onDragStart: $offset"
                },
                onDrag = { change: PointerInputChange, dragAmount: Offset ->
                    currentValue = "onDrag: $dragAmount"
                    currentDetailValue = """
                        onDrag
                        id: ${change.id}, type: ${change.type}, position: ${change.position}, previousPosition: ${change.previousPosition}
                        consumed downChange: ${change.consume()}, previousPressed: ${change.previousPressed}
                        previousUptimeMillis: ${change.previousUptimeMillis}, ${change.position}
                        changedToDOwn: ${change.changedToDown()}, changedToUp: ${change.changedToUp()}
                    """.trimIndent()
                },
                onDragEnd = {
                    currentValue = "onDragEnd"
                },
                onDragCancel = {
                    currentValue = "onDragCancel"
                }
            )
        }

    Column(modifier = Modifier.fillMaxWidth()) {
        Box(modifier = pointerModifier) {
            Text(text = "Click: $currentValue")
        }

        Text(
            modifier = Modifier
                .padding(horizontal = 8.dp)
                .fillMaxWidth()
                .height(400.dp)
                .background(Color.LightGray)
                .padding(8.dp),
            color = Color.White,
            text = currentDetailValue,
            textAlign = TextAlign.Center
        )
    }
}


@Composable
private fun DetectDragGesture2() {
    val offsetX = remember {
        mutableStateOf(0f)
    }
    val offsetY = remember {
        mutableStateOf(0f)
    }
    var size by remember {
        mutableStateOf(Size.Zero)
    }
    var boxColor by remember {
        mutableStateOf(Color.Blue)
    }

    var dragDetailText by remember {
        mutableStateOf("Drag blue box to change its Modifier.offset{IntSize}")
    }

    Box(
        Modifier
            .padding(vertical = 8.dp, horizontal = 12.dp)
            .fillMaxWidth()
            .background(Color.LightGray)
            .height(200.dp)
            .onSizeChanged {
                Log.d("DetectDragGesture2", "change Size: $it")
                size = it.toSize()
            }) {

        Text(
            text = dragDetailText,
            color = Color.White,
            modifier = Modifier.align(Alignment.Center),
            textAlign = TextAlign.Center
        )

        Box(
            Modifier
                .offset {
                    IntOffset(offsetX.value.roundToInt(), offsetY.value.roundToInt())
                }
                .size(50.dp)
                .background(color = boxColor)
                .pointerInput(Unit) {
                    detectDragGestures(
                        onDragStart = {
                            boxColor = Color.Green
                        },
                        onDragEnd = {
                            boxColor = Color.Blue
                        },
                        onDrag = { _, dragAmount ->
                            val original = Offset(offsetX.value, offsetY.value)
                            val summed = original + dragAmount

                            val newValue = Offset(
                                x = summed.x.coerceIn(0f, size.width - 50.dp.toPx()),
                                y = summed.y.coerceIn(0f, size.height - 50.dp.toPx())
                            )
                            offsetX.value = newValue.x
                            offsetY.value = newValue.y
                            dragDetailText =
                                "dragAmount: $dragAmount\noriginal: $original\nsummed: $summed"
                        }
                    )
                })
    }
}

@Composable
private fun DetectDragGestureAfterLongPress() {
    val offsetX = remember {
        mutableStateOf(0f)
    }
    val offsetY = remember {
        mutableStateOf(0f)
    }
    var size by remember {
        mutableStateOf(Size.Zero)
    }
    var boxColor by remember {
        mutableStateOf(Color.Blue)
    }

    var dragDetailText by remember {
        mutableStateOf("Detect drag gesture after long press")
    }

    Box(
        Modifier
            .padding(vertical = 8.dp, horizontal = 12.dp)
            .fillMaxWidth()
            .background(Color.LightGray)
            .height(200.dp)
            .onSizeChanged {
                Log.d("DetectDragGestureAfterLongPress", "change Size: $it")
                size = it.toSize()
            }) {

        Text(text = "DetectDragGestureAfterLongPress", color = Color.White,
            modifier = Modifier.align(Alignment.TopCenter))

        Text(
            text = dragDetailText,
            color = Color.White,
            modifier = Modifier.align(Alignment.Center),
            textAlign = TextAlign.Center
        )

        Box(
            Modifier
                .offset {
                    IntOffset(offsetX.value.roundToInt(), offsetY.value.roundToInt())
                }
                .size(50.dp)
                .background(color = boxColor)
                .pointerInput(Unit) {
                    detectDragGesturesAfterLongPress(
                        onDragStart = {
                            boxColor = Color.Green
                        },
                        onDragEnd = {
                            boxColor = Color.Blue
                        },
                        onDrag = { _, dragAmount ->
                            val original = Offset(offsetX.value, offsetY.value)
                            val summed = original + dragAmount

                            val newValue = Offset(
                                x = summed.x.coerceIn(0f, size.width - 50.dp.toPx()),
                                y = summed.y.coerceIn(0f, size.height - 50.dp.toPx())
                            )
                            offsetX.value = newValue.x
                            offsetY.value = newValue.y
                            dragDetailText =
                                "dragAmount: $dragAmount\noriginal: $original\nsummed: $summed"
                        }
                    )
                })
    }
}

@Composable
private fun DetectVerticalDragGestures() {
    val offsetX = remember { mutableStateOf(0f) }
    val offsetY = remember { mutableStateOf(0f) }
    var height by remember { mutableStateOf(0f) }
    var boxColor by remember {
        mutableStateOf(Color.Blue)
    }

    Box(
        Modifier
            .padding(vertical = 8.dp, horizontal = 12.dp)
            .width(60.dp)
            .height(200.dp)
            .background(Color.LightGray)
            .onSizeChanged { height = it.height.toFloat() }
    ) {
        Text(text = "DetectVerticalDragGestures", color = Color.White,
            modifier = Modifier.align(Alignment.TopCenter))
        Box(
            Modifier
                .offset { IntOffset(offsetX.value.roundToInt(), offsetY.value.roundToInt()) }
                .fillMaxWidth()
                .height(50.dp)
                .background(boxColor)
                .pointerInput(Unit) {

                    detectVerticalDragGestures(
                        onDragStart = {
                            boxColor = Color.Green
                        },

                        onDragEnd = {
                            boxColor = Color.Blue
                        },
                        onVerticalDrag = { _, dragAmount ->
                            val originalY = offsetY.value
                            val newValue =
                                (originalY + dragAmount).coerceIn(0f, height - 50.dp.toPx())
                            offsetY.value = newValue
                        }
                    )
                }
        )
    }
}

@Composable
private fun DetectHorizontalDragGestures() {
    val offsetX = remember { mutableStateOf(0f) }
    val offsetY = remember { mutableStateOf(0f) }
    var width by remember { mutableStateOf(0f) }
    var boxColor by remember {
        mutableStateOf(Color.Blue)
    }

    Box(
        Modifier
            .padding(vertical = 8.dp, horizontal = 12.dp)
            .fillMaxWidth()
            .background(Color.LightGray)
            .onSizeChanged { width = it.width.toFloat() }
    ) {
        Text(text = "DetectHorizontalDragGestures", color = Color.White,
            modifier = Modifier.align(Alignment.TopCenter))
        Box(
            Modifier
                .offset { IntOffset(offsetX.value.roundToInt(), offsetY.value.roundToInt()) }
                .size(50.dp)
                .background(boxColor)
                .pointerInput(Unit) {

                    detectHorizontalDragGestures(
                        onDragStart = {
                            boxColor = Color.Green
                        },

                        onDragEnd = {
                            boxColor = Color.Blue
                        },
                        onHorizontalDrag = { _, dragAmount ->
                            val originalX = offsetX.value
                            val newValue =
                                (originalX + dragAmount).coerceIn(0f, width - 50.dp.toPx())
                            offsetX.value = newValue
                        }
                    )
                }
        )
    }
}

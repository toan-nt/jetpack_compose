package vtoan.n.jetpackcomposeexample.modifiers.actions

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.PointerEvent
import androidx.compose.ui.input.pointer.PointerInputChange
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.input.pointer.positionChange
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.toSize
import kotlin.math.roundToInt

@Composable
fun GestureActionsApp() {
    GestureActionsView {
        AwaitFirstDown()
        AwaitPointerEvent()
        AwaitTouchSlopOrCancellationExample()
        DragExample()
        HorizontalDragExample()
        VerticalDragExample()
    }
}

@Composable
private fun GestureActionsView(content: @Composable () -> Unit) {
    Column(
        Modifier
            .fillMaxSize()
            .background(Color.Gray)
            .verticalScroll(rememberScrollState())
    ) {
        content()
    }
}

@Composable
private fun AwaitFirstDown() {
    var touchText by remember {
        mutableStateOf("Touch to get awaitFirstDown(), reads events until the first down is received")
    }

    var gestureColor by remember { mutableStateOf(Color.LightGray) }
    val pointerModifier = Modifier
        .padding(8.dp)
        .fillMaxWidth()
        .background(color = gestureColor)
        .height(90.dp)
        .pointerInput(Unit) {
            awaitEachGesture {
                val down: PointerInputChange = awaitFirstDown(requireUnconsumed = true)
                touchText = "DOWN Pointer down position: ${down.position}"
                gestureColor = Color.Yellow

                val upOrCancel = waitForUpOrCancellation()
                if (upOrCancel?.position != null) {
                    touchText = "UP Pointer up.position: ${(upOrCancel.position)}"
                    gestureColor = Color.Green
                } else {
                    touchText = "UP CANCEL"
                    gestureColor = Color.Red
                }
            }
        }
    GestureDisplayBox(pointerModifier, touchText)
}

@Composable
private fun AwaitPointerEvent() {
    var touchText by remember { mutableStateOf("Use single or multiple pointers.") }
    var gestureColor by remember { mutableStateOf(Color.LightGray) }
    val pointerModifier = Modifier
        .pointerInput(Unit) {
            awaitPointerEventScope { ->

                awaitFirstDown()
                gestureColor = Color.Yellow

                do {
                    // 🔥🔥 This PointerEvent contains details including events,
                    // id, position and more
                    // Other events such as drag are structured with consume events
                    // using awaitPointerEvent in a while loop
                    val event: PointerEvent = awaitPointerEvent()

                    var eventChanges = ""

                    event.changes
                        .forEachIndexed { index: Int, pointerInputChange: PointerInputChange ->

                            // 🔥🔥 If consume() is not called
                            // vertical scroll or other events interfere with current event
                            pointerInputChange.consume()

                            eventChanges += "Index: $index, id: ${pointerInputChange.id}, " +
                                    "pos: ${pointerInputChange.position}\n"
                        }

                    touchText = "EVENT changes size ${event.changes.size}\n" + eventChanges
                    gestureColor = Color.Blue
                } while (event.changes.any { it.pressed })

                gestureColor = Color.Green
            }
        }

    Box(
        modifier = Modifier
            .padding(vertical = 8.dp, horizontal = 12.dp)
            .fillMaxWidth()
            .height(120.dp)
            .background(gestureColor),
        contentAlignment = Alignment.Center
    ) {
        GestureDisplayBox(pointerModifier.matchParentSize(), touchText)
    }
}

@Composable
private fun AwaitTouchSlopOrCancellationExample() {

    val context = LocalContext.current

    val offsetX = remember { mutableStateOf(0f) }
    val offsetY = remember { mutableStateOf(0f) }
    var size by remember { mutableStateOf(Size.Zero) }

    var gestureColor by remember { mutableStateOf(Color.LightGray) }

    var text by remember {
        mutableStateOf(
            "awaitTouchSlopOrCancellation waits for drag motion to pass touch slop to start drag"
        )
    }

    val modifier = Modifier
        .offset { IntOffset(offsetX.value.roundToInt(), offsetY.value.roundToInt()) }
        .size(80.dp)
        .shadow(2.dp, RoundedCornerShape(8.dp))
        .background(Color.Yellow)
        .pointerInput(Unit) {
            awaitEachGesture {
                val down: PointerInputChange = awaitFirstDown()
                gestureColor = Color.DarkGray
                text = "awaitFirstDown() id: ${down.id}"
                println("🍏 DOWN: ${down.position}")

                // 🔥🔥 Waits for drag threshold to be passed by pointer
                // or it returns null if up event is triggered
                var change: PointerInputChange? =
                    awaitTouchSlopOrCancellation(down.id) { change: PointerInputChange, over: Offset ->

                        Toast
                            .makeText(
                                context,
                                "awaitTouchSlopOrCancellation(down.id) passed for " +
                                        "id: ${down.id}, ${change.position}, over: $over",
                                Toast.LENGTH_SHORT
                            )
                            .show()

                        println("⛺️ awaitTouchSlopOrCancellation ${change.position}, over: $over")
                        val original = Offset(offsetX.value, offsetY.value)
                        val summed = original + over

                        val newValue = Offset(
                            x = summed.x.coerceIn(0f, size.width - 80.dp.toPx()),
                            y = summed.y.coerceIn(0f, size.height - 80.dp.toPx())
                        )

                        // 🔥🔥 If consume() is not called drag does not
                        // function properly.
                        // Consuming position change causes
                        // change.positionChanged() to return false.
                        change.consume()
                        offsetX.value = newValue.x
                        offsetY.value = newValue.y

                        gestureColor = Color.Magenta
                        text =
                            "awaitTouchSlopOrCancellation()  down.id: ${down.id} change.id: ${change.id}" +
                                    "\nnewValue: $newValue"
                    }

                if (change == null) {
                    gestureColor = Color.Red
                    text = "awaitTouchSlopOrCancellation() is NULL"
                }

                while (change != null && change.pressed) {

                    gestureColor = Color.Blue

                    // 🔥 Calls awaitPointerEvent() in a while loop and checks drag change
                    change = awaitDragOrCancellation(change.id)

                    if (change != null && change.pressed) {
                        val original = Offset(offsetX.value, offsetY.value)
                        val summed = original + change.positionChange()
                        val newValue = Offset(
                            x = summed.x.coerceIn(0f, size.width - 80.dp.toPx()),
                            y = summed.y.coerceIn(0f, size.height - 80.dp.toPx())
                        )
                        change.consume()
                        offsetX.value = newValue.x
                        offsetY.value = newValue.y

                        text =
                            "awaitDragOrCancellation() down.id: ${down.id} change.id: ${change.id}" +
                                    "\nnewValue: $newValue"
                    }
                }

                if (gestureColor != Color.Red) {
                    gestureColor = Color.LightGray
                }
            }
        }

    Box(
        Modifier
            .padding(8.dp)
            .fillMaxWidth()
            .height(120.dp)
            .background(gestureColor)
            .onSizeChanged { size = it.toSize() }
    ) {
        Text(
            modifier = Modifier.align(Alignment.Center),
            text = text,
            color = Color.White
        )

        Box(modifier = modifier)
    }
}

@Composable
private fun GestureDisplayBox(boxModifier: Modifier, text: String) {
    Box(modifier = boxModifier) {
        Text(text = text, color = Color.White, modifier = Modifier.align(Alignment.Center))
    }
}


private val WIDTH = 80.dp

@Composable
private fun DragExample() {
    val offsetX = remember { mutableStateOf(0f) }
    val offsetY = remember { mutableStateOf(0f) }
    var size by remember { mutableStateOf(Size.Zero) }

    var gestureColor by remember { mutableStateOf(Color.LightGray) }

    var text by remember {
        mutableStateOf(
            "Drag."
        )
    }

    val dragModifier = Modifier
        .offset { IntOffset(offsetX.value.roundToInt(), offsetY.value.roundToInt()) }
        .size(WIDTH)
        .shadow(2.dp, RoundedCornerShape(8.dp))
        .background(Color.Yellow)
        .pointerInput(Unit) {
            awaitEachGesture {

                val down = awaitFirstDown()
                gestureColor = Color.Magenta
                text = "awaitFirstDown() id: ${down.id}"

                // 🔥 Function to detect if our down pointer passed
                // viewConfiguration.pointerSlop(pointerType)
                val change = awaitTouchSlopOrCancellation(down.id) { change, over ->

                    val original = Offset(offsetX.value, offsetY.value)
                    val summed = original + over
                    val newValue = Offset(
                        x = summed.x.coerceIn(0f, size.width - WIDTH.toPx()),
                        y = summed.y.coerceIn(0f, size.height - WIDTH.toPx())
                    )
                    change.consume()
                    offsetX.value = newValue.x
                    offsetY.value = newValue.y

                    gestureColor = Color.Gray
                    text =
                        "awaitTouchSlopOrCancellation()  down.id: ${down.id} change.id: ${change.id}" +
                                "\nnewValue: $newValue"
                }

                if (change == null) {
                    gestureColor = Color.Red
                    text = "awaitTouchSlopOrCancellation() is NULL"

                }

                if (change != null) {

                    // 🔥 Calls  awaitDragOrCancellation(pointer) in a while loop
                    drag(change.id) {
                        val original = Offset(offsetX.value, offsetY.value)
                        val summed = original + it.positionChange()
                        val newValue = Offset(
                            x = summed.x.coerceIn(0f, size.width - WIDTH.toPx()),
                            y = summed.y.coerceIn(0f, size.height - WIDTH.toPx())
                        )

                        it.consume()
                        offsetX.value = newValue.x
                        offsetY.value = newValue.y

                        gestureColor = Color.Blue
                        text = "drag()  down.id: ${down.id} change.id: ${change.id}" +
                                "\nnewValue: $newValue"
                    }
                }

                if (gestureColor != Color.Red) {
                    gestureColor = Color.LightGray
                }
            }
        }

    Box(
        Modifier
            .fillMaxWidth()
            .background(gestureColor)
            .height(200.dp)
            .onSizeChanged { size = it.toSize() }
    ) {

        Text(
            modifier = Modifier.align(Alignment.Center),
            text = text,
            color = Color.White
        )

        Box(dragModifier)
    }
}

@Composable
private fun HorizontalDragExample() {
    val offsetX = remember { mutableStateOf(0f) }
    val offsetY = remember { mutableStateOf(0f) }
    var width by remember { mutableStateOf(0f) }

    var gestureColor by remember { mutableStateOf(Color.LightGray) }

    var text by remember {
        mutableStateOf(
            "Without awaitTouchSlopOrCancellation drag starts when awaitFirstDown is invoked."
        )
    }

    val dragModifier = Modifier
        .offset { IntOffset(offsetX.value.roundToInt(), offsetY.value.roundToInt()) }
        .size(WIDTH)
        .shadow(2.dp, RoundedCornerShape(8.dp))
        .background(Color.Yellow)
        .pointerInput(Unit) {
            awaitEachGesture {
                val down = awaitFirstDown()
                gestureColor = Color.Magenta
                text = "awaitFirstDown() id: ${down.id}"

                // 🔥 Function to detect if our down pointer passed
                // viewConfiguration.pointerSlop(pointerType)
                val change =
                    awaitHorizontalTouchSlopOrCancellation(down.id) { change, over ->
                        val originalX = offsetX.value
                        val newValue =
                            (originalX + over).coerceIn(0f, width - WIDTH.toPx())
                        change.consume()
                        offsetX.value = newValue

                        gestureColor = Color.Magenta

                        text = "awaitHorizontalTouchSlopOrCancellation()" +
                                "\nnewValue: $newValue"
                    }

                if (change == null) {
                    gestureColor = Color.Red
                    text = "awaitHorizontalTouchSlopOrCancellation() is NULL"

                }

                if (change != null) {

                    // 🔥 Calls  awaitDragOrCancellation(pointer) in a while loop
                    horizontalDrag(change.id) {
                        val originalX = offsetX.value
                        val newValue = (originalX + it.positionChange().x)
                            .coerceIn(0f, width - WIDTH.toPx())
                        it.consume()
                        offsetX.value = newValue

                        gestureColor = Color.Blue
                        text = "horizontalDrag()" +
                                "\nnewValue: $newValue"
                    }
                }

                if (gestureColor != Color.Red) {
                    gestureColor = Color.LightGray
                }
            }
        }

    Box(
        Modifier
            .background(gestureColor)
            .fillMaxWidth()
            .height(100.dp)
            .onSizeChanged { width = it.width.toFloat() },
        contentAlignment = Alignment.CenterStart
    ) {

        Text(
            modifier = Modifier.align(Alignment.Center),
            text = text,
            color = Color.White
        )

        Box(dragModifier)
    }
}

@Composable
private fun VerticalDragExample() {
    val offsetX = remember { mutableStateOf(0f) }
    val offsetY = remember { mutableStateOf(0f) }
    var height by remember { mutableStateOf(0f) }

    var gestureColor by remember { mutableStateOf(Color.LightGray) }


    val dragModifier = Modifier
        .offset { IntOffset(offsetX.value.roundToInt(), offsetY.value.roundToInt()) }
        .size(WIDTH)
        .shadow(2.dp, RoundedCornerShape(8.dp))
        .background(Color.Yellow)
        .pointerInput(Unit) {
            awaitEachGesture {
                val down = awaitFirstDown()
                gestureColor = Color.Magenta

                // 🔥 Function to detect if our down pointer passed
                // viewConfiguration.pointerSlop(pointerType)
                val change =
                    awaitVerticalTouchSlopOrCancellation(down.id) { change, over ->
                        val originalY = offsetY.value
                        val newValue = (originalY + over)
                            .coerceIn(0f, height - WIDTH.toPx())
                        change.consume()
                        offsetY.value = newValue

                        gestureColor = Color.LightGray
                    }

                if (change == null) {
                    gestureColor = Color.Red

                }

                if (change != null) {

                    // 🔥 Calls  awaitDragOrCancellation(pointer) in a while loop
                    verticalDrag(change.id) {
                        val originalY = offsetY.value
                        val newValue = (originalY + it.positionChange().y)
                            .coerceIn(0f, height - WIDTH.toPx())
                        it.consume()
                        offsetY.value = newValue

                        gestureColor = Color.Blue
                    }

                }

                if (gestureColor != Color.Red) {
                    gestureColor = Color.LightGray
                }
            }
        }

    Box(
        Modifier
            .background(gestureColor)
            .width(100.dp)
            .height(240.dp)
            .onSizeChanged { height = it.height.toFloat() },
        contentAlignment = Alignment.TopCenter

    ) {
        Box(dragModifier)
    }
}

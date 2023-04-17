package vtoan.n.jetpackcomposeexample.modifiers.actions

import android.util.Log
import androidx.compose.foundation.*
import androidx.compose.foundation.interaction.*
import androidx.compose.foundation.layout.*
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.ContentDrawScope
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun ModifierClickableApp() {
    ModifierClickableView {
        ModifierActionsCombineClickable()
        ModifierActionsCombineClickableWithCustomIndication()
    }
}

@Composable
private fun ModifierClickableView(content: @Composable () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(color = Color.Gray)

    ) {
        content()
    }
}

@Composable
fun ModifierActionsClickableView() {
    Column(
        Modifier
            .background(color = Color.Green)
            .requiredSize(150.dp)
            .clickable(
                enabled = true,
                onClickLabel = "ModifierActionsClickableView",
                role = Role.Tab,
                onClick = {
                    Log.d("ModifierActions", "ModifierActionsClickableView")
                }),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        Text(text = "Click")
    }
}

@Composable
fun ModifierActionsInteractionView() {

    val interactionSource = remember { MutableInteractionSource() }
    Column {
        Text(
            text = "Click me and my neighbour will indicate as well!",
            modifier = Modifier
                // clickable will dispatch events using MutableInteractionSource and show ripple
                .clickable(
                    interactionSource = interactionSource,
                    indication = rememberRipple()
                ) {

                }
                .padding(10.dp)
        )
        Spacer(Modifier.requiredHeight(10.dp))
        Text(
            text = "I'm neighbour and I indicate when you click the other one",
            modifier = Modifier
                // this element doesn't have a click, but will show default indication from the
                // CompositionLocal as it accepts the same MutableInteractionSource
                .indication(interactionSource, LocalIndication.current)
                .padding(10.dp)
        )
    }
}

@ExperimentalFoundationApi
@Composable
fun ModifierActionsCombineClickable() {
    val interactionSource = remember { MutableInteractionSource() }
    Column (Modifier.background(color = Color.Magenta)) {
        Text(
            text = "ModifierActionsCombineClickable - Click me and my neighbour will indicate as well!",
            modifier = Modifier
                // clickable will dispatch events using MutableInteractionSource and show ripple
                .combinedClickable(
                    interactionSource = interactionSource,
                    indication = rememberRipple(radius = 24.dp, color = Color.White),
                    onLongClick = {
                        Log.d("ModifierActionsCombineClickable", "onLongClick")
                    },
                    onDoubleClick = {
                        Log.d("ModifierActionsCombineClickable", "onDoubleClick")
                    },
                    onClick = {
                        Log.d("ModifierActionsCombineClickable", "onClick")
                    }
                )
                .padding(10.dp)
        )
        Spacer(Modifier.requiredHeight(10.dp))
        Text(
            text = "I'm neighbour and I indicate when you click the other one",
            modifier = Modifier
                // this element doesn't have a click, but will show default indication from the
                // CompositionLocal as it accepts the same MutableInteractionSource
                .indication(interactionSource, LocalIndication.current)
                .padding(10.dp)
        )
    }
}

@ExperimentalFoundationApi
@Composable
fun ModifierActionsCombineClickableWithCustomIndication() {
    val interactionSource = remember { MutableInteractionSource() }
    Column(Modifier.background(color = Color.Yellow)) {
        Text(
            text = "ModifierActionsCombineClickableWithCustomIndication - Click me and my neighbour will indicate as well!",
            modifier = Modifier
                // clickable will dispatch events using MutableInteractionSource and show ripple
                .combinedClickable(
                    interactionSource = interactionSource,
                    indication = CustomIndication(),
                    onLongClick = {
                        Log.d("ModifierActionsCombineClickable", "onLongClick")
                    },
                    onDoubleClick = {
                        Log.d("ModifierActionsCombineClickable", "onDoubleClick")
                    },
                    onClick = {
                        Log.d("ModifierActionsCombineClickable", "onClick")
                    }
                )
                .padding(10.dp)
        )
        Spacer(Modifier.requiredHeight(10.dp))
        Text(
            text = "I'm neighbour and I indicate when you click the other one",
            modifier = Modifier
                // this element doesn't have a click, but will show default indication from the
                // CompositionLocal as it accepts the same MutableInteractionSource
                .indication(interactionSource, CustomIndication())
                .padding(10.dp)
        )
    }
}

@Preview
@Composable
fun ModifierActionsPreview() {
    ModifierClickableView {
        ModifierActionsInteractionView()
    }
}

private class CustomIndication(
    val pressColor: Color = Color.Red,
    val cornerRadius: CornerRadius = CornerRadius(16f, 16f),
    val alpha: Float = 0.5f,
    val drawRoundedShape: Boolean = true
) : Indication {

    private inner class DefaultIndicationInstance(private val isPressed: State<Boolean>) :
        IndicationInstance {

        override fun ContentDrawScope.drawIndication() {
            drawContent()
            when {
                isPressed.value -> {
                    if (drawRoundedShape) {
                        drawRoundRect(
                            cornerRadius = cornerRadius,
                            color = pressColor.copy(
                                alpha = alpha
                            ), size = size
                        )
                    } else {

                        drawCircle(
                            radius = size.width,
                            color = pressColor.copy(
                                alpha = alpha
                            )
                        )
                    }
                }
            }
        }

    }

    @Composable
    override fun rememberUpdatedInstance(interactionSource: InteractionSource): IndicationInstance {
        val isPressed = interactionSource.collectIsPressedAsState()
        val isHovered = interactionSource.collectIsHoveredAsState()
        val isFocused = interactionSource.collectIsFocusedAsState()
        return remember(interactionSource) {
            DefaultIndicationInstance(isPressed)
        }
    }

}
package vtoan.n.jetpackcomposeexample.modifiers.actions

import android.widget.Toast
import androidx.compose.foundation.LocalIndication
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.*
import androidx.compose.foundation.layout.*
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

@Composable
fun ModifierIndicationApp() {
    ModifierIndicationView {
        CollectPressedStateExample()
        Text(text = "Interaction Flow Example")
        InteractionFlow()
        CollectInteraction()
        SharedInteractionSource()
        DiscreteInteractionSource()
    }
}

@Composable
private fun ModifierIndicationView(content: @Composable () -> Unit) {
    Column(Modifier.fillMaxSize(), verticalArrangement = Arrangement.spacedBy(16.dp)) {
        content()
    }
}

@Composable
private fun CollectPressedStateExample() {
    val interactionSource = remember {
        MutableInteractionSource()
    }

    val isPressed by interactionSource.collectIsPressedAsState()

    Button(
        modifier = Modifier.fillMaxWidth(),
        onClick = { },
        interactionSource = interactionSource
    ) {
        Text(text = if (isPressed) "Pressed" else "Not pressed")
    }
}

@Composable
private fun InteractionFlow() {
    val interactionSource = remember {
        MutableInteractionSource()
    }
    val context = LocalContext.current

    LaunchedEffect(interactionSource) {
        interactionSource.interactions.onEach { interaction ->
            Toast.makeText(
                context,
                "Interaction: $interaction",
                Toast.LENGTH_SHORT
            ).show()
        }.launchIn(this)
    }

    Box(modifier = Modifier
        .fillMaxWidth()
        .height(64.dp)
        .background(color = Color.Gray)
        .clickable(
            interactionSource = interactionSource,
            indication = rememberRipple(),
            onClick = {}), contentAlignment = Alignment.Center
    ) {
        Text(text = "Interaction Flow Example", color = Color.Magenta)

    }
}

@Composable
private fun CollectInteraction() {
    val interactionSource = remember {
        MutableInteractionSource()
    }

    val interactions = remember {
        mutableStateListOf<Interaction>()
    }

    LaunchedEffect(interactionSource) {
        interactionSource.interactions.collect { interaction ->
            when (interaction) {
                is PressInteraction.Press -> {
                    interactions.add(interaction)
                }
                is PressInteraction.Release -> {
                    interactions.remove(interaction.press)
                }
                is PressInteraction.Cancel -> {
                    interactions.remove(interaction.press)
                }
                is DragInteraction.Start -> {
                    interactions.add(interaction)
                }
                is DragInteraction.Stop -> {
                    interactions.remove(interaction.start)
                }
                is DragInteraction.Cancel -> {
                    interactions.remove(interaction.start)
                }

            }
        }
    }


    val lastInteraction = when (interactions.lastOrNull()) {
        is DragInteraction.Start -> "Drag Start"
        is DragInteraction.Stop -> "Drag Stop"
        is DragInteraction.Cancel -> "Drag Cancel"
        is PressInteraction.Press -> "Pressed"
        is PressInteraction.Release -> "Press Release"
        is PressInteraction.Cancel -> "Press Cancel"
        else -> "No state"
    }

    Box(
        Modifier
            .fillMaxWidth()
            .border(2.dp, color = Color.Red)
            .clickable(
                interactionSource, rememberRipple()
            ) {}

            .padding(8.dp)
    ) {
        Text(lastInteraction, modifier = Modifier.padding(8.dp))
    }
}

@Composable
private fun SharedInteractionSource() {

    val interactionSource  = remember {
        MutableInteractionSource()
    }
    val context = LocalContext.current

    Column(
        Modifier
            .fillMaxWidth()
            .wrapContentHeight(Alignment.CenterVertically)
            .padding(16.dp)
            .border(2.dp, color = Color.Green)) {
        Text(text = "Click Share", Modifier.clickable(interactionSource = interactionSource, indication = LocalIndication.current, onClick = {}).height(64.dp).background(color = Color.Gray))
        Text(text = "Shared Source", Modifier.clickable(interactionSource = interactionSource, indication = LocalIndication.current, onClick = {}).height(64.dp))
    }
}

@Composable
private fun DiscreteInteractionSource () {
    val interactionSourceOne = remember {
        MutableInteractionSource()
    }

    val interactionSourceTwo = remember {
        MutableInteractionSource()
    }

    LaunchedEffect(interactionSourceOne) {
        interactionSourceOne.interactions.onEach {
            interactionSourceTwo.emit(it)
        }.launchIn(this)
    }

    Column(
        Modifier
            .fillMaxWidth()
            .wrapContentHeight(Alignment.CenterVertically)
            .padding(16.dp)
            .border(2.dp, color = Color.Green)) {
        Text(text = "Click Share", Modifier.clickable(interactionSource = interactionSourceOne, indication = LocalIndication.current, onClick = {}).height(64.dp).background(color = Color.Gray))
        Text(text = "Shared Source", Modifier.clickable(interactionSource = interactionSourceTwo, indication = LocalIndication.current, onClick = {}).height(64.dp))
    }
}
package com.eypancakir.balon

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.gestures.*
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.*
import androidx.compose.ui.input.pointer.*
import androidx.compose.ui.tooling.preview.*
import androidx.compose.ui.unit.*
import com.eypancakir.balon.ui.theme.BalonTheme
import kotlinx.coroutines.delay
import kotlin.random.Random


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            BalonTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colors.background
                ) {

                }
            }
        }
    }
}

@Composable
fun BalloonPopGame() {
    var balloons by remember { mutableStateOf(emptyList<Balloon>()) }
    var score by remember { mutableStateOf(0) }
    var lives by remember { mutableStateOf(3) }

    LaunchedEffect(Unit) {
        while (lives > 0) {
            delay(20)
            val newBalloons = mutableListOf<Balloon>()
            for (balloon in balloons) {
                val newBalloon = balloon.move()
                if (newBalloon != null) {
                    newBalloons.add(newBalloon)
                }
            }
            balloons = newBalloons
            if (Random.nextFloat() < 0.1f) {
                balloons = balloons + Balloon(Random.nextInt(500), 700)
            }
            score += balloons.count { it.popped }
            lives -= balloons.count { it.popped && it.decreaseLife }
        }
    }

    Box(Modifier.fillMaxSize()) {
        for (balloon in balloons) {
            Balloon(balloon, Modifier.offset { IntOffset(balloon.x, balloon.y) })
        }
        Text("Score: $score", Modifier.padding(16.dp).align(Alignment.TopStart))
        Text("Lives: $lives", Modifier.padding(16.dp).align(Alignment.TopEnd))
    }
}

data class Balloon(val x: Balloon, val y: Int, val speed: Int = Random.nextInt(10) + 10, val popped: Boolean = false) {
    val decreaseLife = y < 0 && !popped
    fun move(): Balloon? {
        if (popped) {
            return null
        }
        val newY = y - speed
        if (newY < -100) {
            return copy(y = -100)
        }
        return copy(y = newY)
    }
}

@Composable
fun Balloon(balloon: Balloon, modifier: Modifier = Modifier, onClick: (Offset) -> Unit) {
    var scale by remember { mutableStateOf(1f) }
    val color = remember(balloon) {
        val hue = balloon.x * 360f / 500f
        Color.hsv(hue, 0.8f, 0.8f)
    }

    Canvas(modifier.size(100.dp).pointerInput(Unit) {
        detectTapGestures(onTap = onClick)
    }) {
        drawCircle(color, radius = 50f * scale)
        if (balloon.y < 0) {
            drawCircle(Color.Red, radius = 20f, style = Stroke(4f))
        }
    }
}

@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    BalonTheme {

    }
}
package com.graphsui.graphsui.graphs

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.AnimationVector1D
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.drawText
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch
import kotlin.math.min


private fun getAnglesFromItems(items: List<Float>, arcMaxAngle: Int): List<Float>{
    val normalizer = items.sum()
    return MutableList(items.size){ idx -> ((items[idx]/normalizer)*arcMaxAngle) }
}
private fun getSweepsWithAnimatable(items: List<Float>, arcMaxAngle: Int): List<Pair<Float, Animatable<Float, AnimationVector1D>>>{
    val angles: MutableList<Float> = getAnglesFromItems(items, arcMaxAngle).toMutableList()
    val pairs: List<Pair<Float, Animatable<Float, AnimationVector1D>>> = List(angles.size){ idx -> angles[idx] to Animatable(0f)}
    return pairs
}

private fun getStartingPoints(items: List<Float>, arcMaxAngle: Int): List<Float>{
    var cumulative=180f
    val res=mutableListOf<Float>()
    val angles: List<Float> = getAnglesFromItems(items, arcMaxAngle)
    for (angle in angles){
        res.add(cumulative)
        cumulative+=angle
    }
    return res
}

@Composable
fun AnimatedArcChart(modifier: Modifier,
                     itemNames: List<String>,
                     values: List<Float>,
                     colors: List<Color>,
                     strokeThickness: Float?= null,
                     arcMaxAngle: Int=360,
                     animationDurationMilliseconds:Int=1000,
                     parallelAnimation: Boolean=false,
                     textStyle: TextStyle = TextStyle(),
                     dotRadius: Float=5f,
                     referenceSpacing: Float=20f,
                     graphSize: Size?=null){
    if (colors.size < values.size || itemNames.size < values.size){
        throw RuntimeException("Colors or Names are less than Values")
    }
    val angles = remember { getSweepsWithAnimatable(values, arcMaxAngle) }
    val starts= remember { getStartingPoints(values, arcMaxAngle) }
    val textMeasurer= rememberTextMeasurer()
    LaunchedEffect(angles) {
        if (parallelAnimation){
            for (angle in angles) {
                launch {
                    angle.second.animateTo(
                        angle.first,
                        animationSpec = tween(animationDurationMilliseconds)
                    )
                }
            }
        }
        else {
            launch {
                for (angle in angles) {
                    angle.second.animateTo(
                        angle.first,
                        animationSpec = tween(animationDurationMilliseconds)
                    )
                }
            }
        }
    }
    Canvas(modifier.fillMaxSize()) {
        val width=size.width
        val height=size.height
        val heightOffsetFromEdge=height-((referenceSpacing+dotRadius)*angles.size)
        val arcSize=graphSize ?: Size(min(width, height) /2f, min(width, height)/2f)
        val maxTextSize=textMeasurer.measure(text=itemNames.maxBy { it.length }, style=textStyle).size
        val infoOffsetX:Float=width-(maxTextSize.width*1.5f)
        for (idx: Int in 0..<angles.size) {
            drawArc(
                color = colors[idx],
                topLeft = Offset((width/2f)-(arcSize.width/2f), (height / 2f)-(arcSize.height/2)),
                startAngle = starts[idx],
                sweepAngle = angles[idx].second.value,
                useCenter = false,
                style = Stroke(strokeThickness ?: (arcSize.width / 8f), cap = StrokeCap.Butt),
                size = arcSize
            )
            drawCircle(
                color=colors[idx],
                radius=dotRadius,
                center=Offset(infoOffsetX, heightOffsetFromEdge+idx*referenceSpacing)
            )
            drawText(
                textMeasurer=textMeasurer,
                itemNames[idx],
                style = textStyle,
                topLeft =Offset(infoOffsetX+10, (heightOffsetFromEdge+idx*referenceSpacing)-10))
        }
    }
}

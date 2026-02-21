package com.graphsui.graphsui.graphs

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.drawText
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.TextUnitType
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch
import kotlin.math.min

@Composable
fun AnimatedBarPlot(
    modifier: Modifier,
    data: List<Pair<String, List<Float>>>,
    dataColors: List<Color>,
    showNumbers: Boolean=true,
    step: Int=20,
    maxRecordedValueSlider: Float=1.5f,
    barWidth: Float?=null,
    barMaxHeightPercentage: Float=0.25f,
    barCornerRadius: Float=5f,
    textStyle: TextStyle= TextStyle(Color.White),
    lineColor: Color=Color.White,
                    ){
    val animatableValues = remember { List(data.size){ List(dataColors.size){Animatable(0f) } } }
    val textMeasurer= rememberTextMeasurer()
    LaunchedEffect(animatableValues){
        for (avListIdx in 0..<animatableValues.size){
            for(avIdx in 0..<dataColors.size){
                launch {
                    animatableValues[avListIdx][avIdx].animateTo(
                        data[avListIdx].second[avIdx],
                        animationSpec= spring(
                            dampingRatio = Spring.DampingRatioMediumBouncy,
                            stiffness = Spring.StiffnessLow
                        ))
                }
            }
        }
    }
    Canvas(modifier.fillMaxSize()){
        val width=size.width
        val height=size.height
        val rectWidth= barWidth ?: (width / (10f * animatableValues.size))
        var offset=-width/4
        val maxVal=data.maxOf { it.second.max()  }
        val maxHeight=((height/1.2f)-(height*barMaxHeightPercentage))/maxRecordedValueSlider
        for ((listIdx, aList) in animatableValues.withIndex()) {
            for ((idx, av) in aList.withIndex()) {
                val rectHeight = (height*barMaxHeightPercentage)*(av.value/maxVal)
                drawRoundRect(
                    dataColors[idx],
                    topLeft = Offset((width/2)+offset, (height / 1.2f) - rectHeight),
                    size = Size(width = rectWidth, height = rectHeight),
                    cornerRadius = CornerRadius(barCornerRadius)
                )
                offset+=rectWidth
            }
            val nameSize=textMeasurer.measure(data[listIdx].first, style = textStyle).size
            val barGroupWidth=(rectWidth*aList.size)
            val nameXOffset=if(nameSize.width<barGroupWidth) (barGroupWidth+nameSize.width)/2 else barGroupWidth+(nameSize.width - barGroupWidth)/2
            drawText(textMeasurer,
                data[listIdx].first, style = textStyle,
                topLeft = Offset(x = (width/2)+offset-nameXOffset, y=height/1.2f+(nameSize.height/2)))
            offset+=width/(aList.size*animatableValues.size)
        }
        drawLine(
            lineColor,
            start = Offset(x = (width/6), y = height/1.2f),
            end = Offset(x=(width/2.2f)+offset, y=height/1.2f),
            strokeWidth = 2f
        )
        drawLine(
            lineColor,
            start = Offset(x = (width/6), y = height/1.2f),
            end = Offset(x=(width/6), y=maxHeight),
            strokeWidth = 2f
        )
        if (showNumbers) {
            offset = (height / 1.2f) - ((height * barMaxHeightPercentage) * (step / maxVal))
            var counter = step
            while (offset >= maxHeight) {
                val textSize = textMeasurer.measure("$counter", style = textStyle).size
                drawLine(
                    Color.White,
                    start = Offset(x = (width / 6f) + 7f, y = offset),
                    end = Offset(x = (width / 6f) - 7f, y = offset),
                    strokeWidth = 2f
                )
                drawText(
                    textMeasurer,
                    "$counter",
                    style = textStyle,
                    topLeft = Offset(
                        x = (width / 6f) - (textSize.width * 1.5f),
                        y = offset - (textSize.height * 0.5f)
                    )
                )
                offset -= (height * barMaxHeightPercentage) * (step / maxVal)
                counter += step
            }
        }
    }
}

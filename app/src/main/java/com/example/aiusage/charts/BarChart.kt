package com.example.aiusage.charts

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import android.graphics.Paint
import java.util.Locale

@Composable
fun BarChart(
    data: List<Pair<String, Float>>,
    modifier: Modifier = Modifier
        .fillMaxWidth()
        .height(200.dp),
    barColor: Color = MaterialTheme.colorScheme.primary,
    gridColor: Color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.1f),
    textColor: Color = MaterialTheme.colorScheme.onSurfaceVariant
) {
    val maxVal = (data.maxOfOrNull { it.second } ?: 1f).coerceAtLeast(1f)

    Canvas(modifier = modifier) {
        val width = size.width
        val height = size.height
        
        val paddingLeft = 100.dp.value // converting directly or using .toPx() inside DrawScope
        val paddingLeftPx = 40.dp.toPx()
        val paddingBottomPx = 30.dp.toPx()
        val paddingTopPx = 10.dp.toPx()
        val paddingRightPx = 10.dp.toPx()

        val chartWidth = width - paddingLeftPx - paddingRightPx
        val chartHeight = height - paddingTopPx - paddingBottomPx

        // Draw horizontal grid lines (Y-axis grid)
        val paint = Paint().apply {
            color = textColor.hashCode()
            textSize = 10.sp.toPx()
            textAlign = Paint.Align.RIGHT
            isAntiAlias = true
        }

        val gridLines = 4
        for (i in 0..gridLines) {
            val ratio = i.toFloat() / gridLines
            val y = paddingTopPx + chartHeight * (1f - ratio)
            
            // Grid line
            drawLine(
                color = gridColor,
                start = Offset(paddingLeftPx, y),
                end = Offset(width - paddingRightPx, y),
                strokeWidth = 1.dp.toPx()
            )

            // Y-axis label (Hours format: e.g. "5.0h")
            val yValue = maxVal * ratio
            val label = String.format(Locale.US, "%.1fh", yValue)
            
            drawIntoCanvas { canvas ->
                canvas.nativeCanvas.drawText(
                    label,
                    paddingLeftPx - 8.dp.toPx(),
                    y + 4.dp.toPx(),
                    paint
                )
            }
        }

        // Draw Bars
        if (data.isNotEmpty()) {
            val barSpacing = chartWidth / data.size
            val barWidth = barSpacing * 0.5f

            data.forEachIndexed { index, pair ->
                val ratio = pair.second / maxVal
                val barHeight = chartHeight * ratio
                
                val x = paddingLeftPx + (index * barSpacing) + (barSpacing - barWidth) / 2f
                val y = paddingTopPx + chartHeight - barHeight

                // Draw rounded bar
                drawRoundRect(
                    color = barColor,
                    topLeft = Offset(x, y),
                    size = Size(barWidth, barHeight),
                    cornerRadius = CornerRadius(4.dp.toPx(), 4.dp.toPx())
                )

                // Draw X-axis text labels (e.g. "Mon")
                paint.textAlign = Paint.Align.CENTER
                drawIntoCanvas { canvas ->
                    canvas.nativeCanvas.drawText(
                        pair.first,
                        x + barWidth / 2f,
                        height - 8.dp.toPx(),
                        paint
                    )
                }
            }
        }
    }
}

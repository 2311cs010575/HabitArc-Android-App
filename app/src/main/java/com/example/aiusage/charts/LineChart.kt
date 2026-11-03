package com.example.aiusage.charts

import android.graphics.Paint
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import java.util.Locale

@Composable
fun LineChart(
    data: List<Pair<String, Float>>,
    modifier: Modifier = Modifier
        .fillMaxWidth()
        .height(200.dp),
    lineColor: Color = MaterialTheme.colorScheme.tertiary,
    gridColor: Color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.1f),
    textColor: Color = MaterialTheme.colorScheme.onSurfaceVariant
) {
    val maxVal = (data.maxOfOrNull { it.second } ?: 100f).coerceAtLeast(10f)
    val backgroundColor = MaterialTheme.colorScheme.background

    Canvas(modifier = modifier) {
        val width = size.width
        val height = size.height
        
        val paddingLeftPx = 40.dp.toPx()
        val paddingBottomPx = 30.dp.toPx()
        val paddingTopPx = 10.dp.toPx()
        val paddingRightPx = 15.dp.toPx()

        val chartWidth = width - paddingLeftPx - paddingRightPx
        val chartHeight = height - paddingTopPx - paddingBottomPx

        val paint = Paint().apply {
            color = textColor.hashCode()
            textSize = 10.sp.toPx()
            textAlign = Paint.Align.RIGHT
            isAntiAlias = true
        }

        // Draw horizontal grid lines (Y-axis grid)
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

            // Y-axis label (Currency format: e.g. "₹120")
            val yValue = maxVal * ratio
            val label = String.format(Locale.US, "₹%.0f", yValue)
            
            drawIntoCanvas { canvas ->
                canvas.nativeCanvas.drawText(
                    label,
                    paddingLeftPx - 8.dp.toPx(),
                    y + 4.dp.toPx(),
                    paint
                )
            }
        }

        // Draw Line and Filled Path
        if (data.isNotEmpty()) {
            val spacing = chartWidth / (data.size - 1).coerceAtLeast(1)
            
            val linePath = Path()
            val fillPath = Path()

            val points = data.mapIndexed { index, pair ->
                val ratio = pair.second / maxVal
                val x = paddingLeftPx + (index * spacing)
                val y = paddingTopPx + chartHeight - (chartHeight * ratio)
                Offset(x, y)
            }

            // Draw line connecting points using Bezier curves for smooth spline effect
            linePath.moveTo(points.first().x, points.first().y)
            fillPath.moveTo(points.first().x, points.first().y)

            for (i in 1 until points.size) {
                val pPrev = points[i - 1]
                val pCurr = points[i]
                
                // Control points for cubic bezier
                val cp1X = pPrev.x + (pCurr.x - pPrev.x) / 2f
                val cp1Y = pPrev.y
                val cp2X = pPrev.x + (pCurr.x - pPrev.x) / 2f
                val cp2Y = pCurr.y

                linePath.cubicTo(cp1X, cp1Y, cp2X, cp2Y, pCurr.x, pCurr.y)
                fillPath.cubicTo(cp1X, cp1Y, cp2X, cp2Y, pCurr.x, pCurr.y)
            }

            // Close the fill path to draw gradient beneath
            fillPath.lineTo(points.last().x, paddingTopPx + chartHeight)
            fillPath.lineTo(points.first().x, paddingTopPx + chartHeight)
            fillPath.close()

            // Draw the gradient fill
            drawPath(
                path = fillPath,
                brush = Brush.verticalGradient(
                    colors = listOf(
                        lineColor.copy(alpha = 0.3f),
                        lineColor.copy(alpha = 0.0f)
                    ),
                    startY = paddingTopPx,
                    endY = paddingTopPx + chartHeight
                )
            )

            // Draw the line stroke
            drawPath(
                path = linePath,
                color = lineColor,
                style = Stroke(
                    width = 3.dp.toPx(),
                    cap = StrokeCap.Round
                )
            )

            // Draw points and X-axis Labels
            data.forEachIndexed { index, pair ->
                val pt = points[index]

                // Draw circles on points
                drawCircle(
                    color = lineColor,
                    radius = 4.dp.toPx(),
                    center = pt
                )
                drawCircle(
                    color = backgroundColor,
                    radius = 2.dp.toPx(),
                    center = pt
                )

                // Draw X-axis label (e.g. "Mon")
                paint.textAlign = Paint.Align.CENTER
                drawIntoCanvas { canvas ->
                    canvas.nativeCanvas.drawText(
                        pair.first,
                        pt.x,
                        height - 8.dp.toPx(),
                        paint
                    )
                }
            }
        }
    }
}

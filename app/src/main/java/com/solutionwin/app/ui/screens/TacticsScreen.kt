package com.solutionwin.app.ui.screens

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.FilterChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.drawText
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

private data class Formation(val name: String, val players: List<Pair<Float, Float>>)

private val formations = listOf(
    Formation("4-4-2", rows(2, 4, 4)),
    Formation("4-3-3", rows(3, 3, 4)),
    Formation("4-2-3-1", rows(1, 3, 2, 4)),
    Formation("4-3-2-1", rows(1, 2, 3, 4)),
    Formation("3-5-2", rows(2, 5, 3)),
    Formation("3-2-4-1", rows(1, 4, 2, 3)),
)

private fun rows(vararg counts: Int): List<Pair<Float, Float>> {
    val positions = mutableListOf(0.5f to 0.92f)
    counts.forEachIndexed { rowIndex, count ->
        val y = 0.14f + rowIndex * (0.68f / counts.size.coerceAtLeast(1))
        repeat(count) { playerIndex ->
            positions += ((playerIndex + 1f) / (count + 1f)) to y
        }
    }
    return positions
}

@Composable
fun TacticsScreen() {
    var selected by remember { mutableStateOf(formations.first()) }
    val fieldColor = MaterialTheme.colorScheme.primaryContainer
    val lineColor = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.72f)
    val playerColor = MaterialTheme.colorScheme.primary
    val playerTextColor = MaterialTheme.colorScheme.onPrimary
    val textMeasurer = rememberTextMeasurer()

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp),
    ) {
        item {
            Text("Выберите расстановку", style = MaterialTheme.typography.titleMedium)
            LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                items(formations, key = Formation::name) { formation ->
                    FilterChip(
                        selected = selected == formation,
                        onClick = { selected = formation },
                        label = { Text(formation.name) },
                    )
                }
            }
        }
        item {
            Card(Modifier.fillMaxWidth()) {
                Column(Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text("Схема ${selected.name}", style = MaterialTheme.typography.titleLarge)
                    Canvas(Modifier.fillMaxWidth().height(520.dp)) {
                        drawRoundRect(fieldColor, cornerRadius = androidx.compose.ui.geometry.CornerRadius(28f, 28f))
                        val inset = 20f
                        val fieldSize = Size(size.width - inset * 2, size.height - inset * 2)
                        drawRect(lineColor, Offset(inset, inset), fieldSize, style = Stroke(width = 4f))
                        drawLine(lineColor, Offset(inset, size.height / 2), Offset(size.width - inset, size.height / 2), 4f)
                        drawCircle(lineColor, radius = size.width * 0.14f, center = Offset(size.width / 2, size.height / 2), style = Stroke(4f))
                        drawRect(
                            lineColor,
                            topLeft = Offset(size.width * 0.22f, inset),
                            size = Size(size.width * 0.56f, size.height * 0.16f),
                            style = Stroke(4f),
                        )
                        drawRect(
                            lineColor,
                            topLeft = Offset(size.width * 0.22f, size.height - inset - size.height * 0.16f),
                            size = Size(size.width * 0.56f, size.height * 0.16f),
                            style = Stroke(4f),
                        )
                        selected.players.forEachIndexed { index, position ->
                            val center = Offset(position.first * size.width, position.second * size.height)
                            drawCircle(playerColor, radius = 23f, center = center)
                            val label = textMeasurer.measure(
                                text = (index + 1).toString(),
                                style = androidx.compose.ui.text.TextStyle(
                                    color = playerTextColor,
                                    fontSize = 12.sp,
                                    textAlign = TextAlign.Center,
                                ),
                            )
                            drawText(label, topLeft = Offset(center.x - label.size.width / 2, center.y - label.size.height / 2))
                        }
                    }
                }
            }
        }
        item {
            Text(
                "Номера показывают стартовые позиции. Редактор перемещений игроков и экспорт изображения можно добавить следующим обновлением.",
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
    }
}

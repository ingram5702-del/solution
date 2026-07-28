package com.sporthub.app.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.AssistChip
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.unit.dp

private data class NewsArticle(
    val category: String,
    val title: String,
    val summary: String,
    val sourceUrl: String,
)

private val demoNews = listOf(
    NewsArticle(
        "Футбол",
        "Клубы готовятся к новому игровому туру",
        "Расписание, последние тренировки и ключевые игроки предстоящих встреч.",
        "https://www.uefa.com/",
    ),
    NewsArticle(
        "Баскетбол",
        "Команды усиливают защиту перед решающими матчами",
        "Тренеры рассказали о подготовке и обновлённых игровых сочетаниях.",
        "https://www.fiba.basketball/",
    ),
    NewsArticle(
        "Теннис",
        "Спортсмены вышли на финальный этап подготовки",
        "В центре внимания — физическая форма, подача и восстановление.",
        "https://www.itftennis.com/",
    ),
)

@Composable
fun HomeScreen() {
    val uriHandler = LocalUriHandler.current
    LazyColumn(
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp),
    ) {
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer),
            ) {
                Column(Modifier.padding(20.dp)) {
                    Text("Всё для спорта — в одном месте", style = MaterialTheme.typography.headlineSmall)
                    Spacer(Modifier.height(8.dp))
                    Text(
                        "Новости, матчи, тренировки, таймеры и тактическая доска.",
                        color = MaterialTheme.colorScheme.onPrimaryContainer,
                    )
                }
            }
        }
        item {
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                AssistChip(onClick = {}, label = { Text("Футбол") })
                AssistChip(onClick = {}, label = { Text("Баскетбол") })
                AssistChip(onClick = {}, label = { Text("Теннис") })
            }
        }
        item {
            Text("Последние новости", style = MaterialTheme.typography.titleLarge)
            Text(
                "Демонстрационная лента — источник API подключается отдельно.",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
        items(demoNews, key = { it.title }) { article ->
            Card(
                onClick = { uriHandler.openUri(article.sourceUrl) },
                modifier = Modifier.fillMaxWidth(),
            ) {
                Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    Text(article.category, style = MaterialTheme.typography.labelLarge, color = MaterialTheme.colorScheme.primary)
                    Text(article.title, style = MaterialTheme.typography.titleMedium)
                    Text(article.summary, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Text("Открыть официальный источник", style = MaterialTheme.typography.labelMedium)
                }
            }
        }
    }
}

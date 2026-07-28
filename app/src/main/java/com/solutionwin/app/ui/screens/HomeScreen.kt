package com.solutionwin.app.ui.screens

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
        "Football",
        "Clubs prepare for the next round of matches",
        "Schedules, recent training sessions, and key players to watch in the upcoming fixtures.",
        "https://www.uefa.com/",
    ),
    NewsArticle(
        "Basketball",
        "Teams strengthen their defense ahead of decisive games",
        "Coaches shared details about their preparation and updated lineups.",
        "https://www.fiba.basketball/",
    ),
    NewsArticle(
        "Tennis",
        "Players enter the final stage of preparation",
        "Fitness, serving, and recovery are the main areas of focus.",
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
                    Text("Everything for sports in one place", style = MaterialTheme.typography.headlineSmall)
                    Spacer(Modifier.height(8.dp))
                    Text(
                        "News, matches, training sessions, timers, and a tactics board.",
                        color = MaterialTheme.colorScheme.onPrimaryContainer,
                    )
                }
            }
        }
        item {
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                AssistChip(onClick = {}, label = { Text("Football") })
                AssistChip(onClick = {}, label = { Text("Basketball") })
                AssistChip(onClick = {}, label = { Text("Tennis") })
            }
        }
        item {
            Text("Latest news", style = MaterialTheme.typography.titleLarge)
            Text(
                "Demo news feed — a live API source can be connected separately.",
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
                    Text("Open official source", style = MaterialTheme.typography.labelMedium)
                }
            }
        }
    }
}

package com.solutionwin.app.ui.screens

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.SystemClock
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.selection.selectable
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.FilterChip
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.dp
import com.google.mlkit.vision.barcode.common.Barcode
import com.google.mlkit.vision.codescanner.GmsBarcodeScannerOptions
import com.google.mlkit.vision.codescanner.GmsBarcodeScanning
import com.solutionwin.app.domain.AppSettings
import com.solutionwin.app.domain.ThemeMode
import java.util.Locale
import kotlinx.coroutines.delay

private data class MoreItem(val route: String, val title: String, val description: String, val symbol: String)

private val moreItems = listOf(
    MoreItem("Секундомер", "Секундомер", "Круги, пауза и точный замер времени", "◷"),
    MoreItem("QR-сканер", "QR-сканер", "Найти официальную ссылку на матч", "▣"),
    MoreItem("Настройки", "Настройки", "Тема, уведомления и обратная связь", "⚙"),
)

@Composable
fun MoreScreen(
    destination: String?,
    settings: AppSettings,
    onOpen: (String) -> Unit = {},
    onThemeChanged: (ThemeMode) -> Unit,
    onDynamicColorChanged: (Boolean) -> Unit,
) {
    when (destination) {
        "Секундомер" -> StopwatchScreen()
        "QR-сканер" -> QrScannerScreen()
        "Настройки" -> SettingsScreen(settings, onThemeChanged, onDynamicColorChanged)
        else -> LazyColumn(
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            item { Text("Спортивные инструменты", style = MaterialTheme.typography.titleLarge) }
            items(moreItems.size) { index ->
                val item = moreItems[index]
                Card(
                    modifier = Modifier.fillMaxWidth().clickable { onOpen(item.route) },
                ) {
                    Row(
                        Modifier.padding(18.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(16.dp),
                    ) {
                        Text(item.symbol, style = MaterialTheme.typography.headlineMedium)
                        Column {
                            Text(item.title, style = MaterialTheme.typography.titleMedium)
                            Text(item.description, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun StopwatchScreen() {
    var mode by rememberSaveable { mutableStateOf("Бег") }
    var running by rememberSaveable { mutableStateOf(false) }
    var baseElapsed by rememberSaveable { mutableLongStateOf(0L) }
    var startMarker by rememberSaveable { mutableLongStateOf(0L) }
    var displayed by rememberSaveable { mutableLongStateOf(0L) }
    val laps = remember { mutableStateListOf<Long>() }

    LaunchedEffect(running, startMarker, baseElapsed) {
        while (running) {
            displayed = baseElapsed + SystemClock.elapsedRealtime() - startMarker
            delay(32)
        }
    }

    Column(
        Modifier.fillMaxSize().padding(20.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(18.dp),
    ) {
        Spacer(Modifier.height(24.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            listOf("Бег", "Матч", "Свободный").forEach { option ->
                FilterChip(
                    selected = mode == option,
                    onClick = { mode = option },
                    label = { Text(option) },
                )
            }
        }
        Text("Режим: $mode", color = MaterialTheme.colorScheme.primary)
        Text("Точное время", color = MaterialTheme.colorScheme.onSurfaceVariant)
        Text(formatStopwatch(displayed), style = MaterialTheme.typography.displayMedium)
        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            Button(onClick = {
                if (running) {
                    displayed = baseElapsed + SystemClock.elapsedRealtime() - startMarker
                    baseElapsed = displayed
                    running = false
                } else {
                    startMarker = SystemClock.elapsedRealtime()
                    running = true
                }
            }) { Text(if (running) "Пауза" else "Старт") }
            OutlinedButton(onClick = { if (displayed > 0) laps.add(0, displayed) }) { Text("Круг") }
            OutlinedButton(onClick = {
                running = false; baseElapsed = 0; displayed = 0; startMarker = 0; laps.clear()
            }) { Text("Сброс") }
        }
        if (laps.isNotEmpty()) {
            HorizontalDivider()
            LazyColumn(Modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                itemsIndexed(laps) { index, lap ->
                    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text("Круг ${laps.size - index}")
                        Text(formatStopwatch(lap))
                    }
                }
            }
        }
    }
}

internal fun formatStopwatch(milliseconds: Long): String {
    val totalSeconds = milliseconds / 1_000
    val minutes = totalSeconds / 60
    val seconds = totalSeconds % 60
    val hundredths = milliseconds % 1_000 / 10
    return String.format(Locale.getDefault(), "%02d:%02d.%02d", minutes, seconds, hundredths)
}

@Composable
private fun QrScannerScreen() {
    val context = LocalContext.current
    var result by rememberSaveable { mutableStateOf<String?>(null) }
    var error by rememberSaveable { mutableStateOf<String?>(null) }
    val scanner = remember {
        val options = GmsBarcodeScannerOptions.Builder()
            .setBarcodeFormats(Barcode.FORMAT_QR_CODE)
            .enableAutoZoom()
            .build()
        GmsBarcodeScanning.getClient(context, options)
    }
    val isWebLink = result?.let { value ->
        val scheme = Uri.parse(value).scheme?.lowercase()
        scheme == "https" || scheme == "http"
    } == true

    Column(
        Modifier.fillMaxSize().padding(20.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        Spacer(Modifier.height(24.dp))
        Text("▣", style = MaterialTheme.typography.displayLarge, color = MaterialTheme.colorScheme.primary)
        Text("Сканер QR-кода", style = MaterialTheme.typography.headlineSmall)
        Text(
            "Наведите камеру на QR-код. Ссылка откроется только после вашего подтверждения.",
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
        Button(onClick = {
            error = null
            scanner.startScan()
                .addOnSuccessListener { barcode -> result = barcode.rawValue ?: "Пустой QR-код" }
                .addOnFailureListener { error = "Не удалось отсканировать код" }
        }) { Text("Сканировать") }
        result?.let { value ->
            Card(Modifier.fillMaxWidth()) {
                Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text("Результат", style = MaterialTheme.typography.labelLarge)
                    Text(value)
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        if (isWebLink) {
                            Button(onClick = {
                                context.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(value)))
                            }) { Text("Открыть ссылку") }
                        }
                        OutlinedButton(onClick = {
                            val clipboard = context.getSystemService(ClipboardManager::class.java)
                            clipboard.setPrimaryClip(ClipData.newPlainText("QR", value))
                            Toast.makeText(context, "Скопировано", Toast.LENGTH_SHORT).show()
                        }) { Text("Копировать") }
                    }
                }
            }
        }
        error?.let { Text(it, color = MaterialTheme.colorScheme.error) }
        Text(
            "Проверяйте адрес сайта и используйте только официальные трансляции.",
            style = MaterialTheme.typography.bodySmall,
        )
    }
}

@Composable
private fun SettingsScreen(
    settings: AppSettings,
    onThemeChanged: (ThemeMode) -> Unit,
    onDynamicColorChanged: (Boolean) -> Unit,
) {
    val context = LocalContext.current
    val uriHandler = LocalUriHandler.current
    var feedback by rememberSaveable { mutableStateOf("") }
    var contact by rememberSaveable { mutableStateOf("") }
    var attachment by remember { mutableStateOf<Uri?>(null) }
    val attachmentPicker = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri ->
        attachment = uri
    }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp),
    ) {
        item { Text("Оформление", style = MaterialTheme.typography.titleLarge) }
        item {
            Card(Modifier.fillMaxWidth()) {
                Column(Modifier.padding(8.dp)) {
                    ThemeMode.entries.forEach { mode ->
                        val label = when (mode) {
                            ThemeMode.SYSTEM -> "Как в системе"
                            ThemeMode.LIGHT -> "Светлая"
                            ThemeMode.DARK -> "Тёмная"
                        }
                        Row(
                            Modifier.fillMaxWidth().selectable(
                                selected = settings.themeMode == mode,
                                onClick = { onThemeChanged(mode) },
                                role = Role.RadioButton,
                            ).padding(8.dp),
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            RadioButton(settings.themeMode == mode, onClick = null)
                            Text(label, Modifier.padding(start = 10.dp))
                        }
                    }
                    Row(
                        Modifier.fillMaxWidth().padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween,
                    ) {
                        Column(Modifier.weight(1f)) {
                            Text("Динамические цвета")
                            Text("Цвета обоев на Android 12+", style = MaterialTheme.typography.bodySmall)
                        }
                        Switch(settings.dynamicColor, onCheckedChange = onDynamicColorChanged)
                    }
                }
            }
        }
        item { Text("Обратная связь", style = MaterialTheme.typography.titleLarge) }
        item {
            Card(Modifier.fillMaxWidth()) {
                Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    OutlinedTextField(
                        value = contact,
                        onValueChange = { contact = it },
                        label = { Text("Ваш email (необязательно)") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                    )
                    OutlinedTextField(
                        value = feedback,
                        onValueChange = { feedback = it },
                        label = { Text("Сообщение") },
                        modifier = Modifier.fillMaxWidth(),
                        minLines = 4,
                    )
                    OutlinedButton(onClick = { attachmentPicker.launch("image/*") }) {
                        Text(if (attachment == null) "Добавить скриншот" else "Скриншот прикреплён")
                    }
                    Button(
                        enabled = feedback.isNotBlank(),
                        onClick = { sendFeedback(context, contact, feedback, attachment) },
                        modifier = Modifier.fillMaxWidth(),
                    ) { Text("Отправить") }
                }
            }
        }
        item {
            Text("SportHub 1.0 · Данные заметок и календаря хранятся на устройстве.", style = MaterialTheme.typography.bodySmall)
        }
        item {
            OutlinedButton(
                onClick = { uriHandler.openUri("https://ingram5702-del.github.io/solution/") },
                modifier = Modifier.fillMaxWidth(),
            ) {
                Text("Privacy Policy")
            }
        }
    }
}

private fun sendFeedback(context: Context, contact: String, feedback: String, attachment: Uri?) {
    val body = buildString {
        append(feedback.trim())
        if (contact.isNotBlank()) append("\n\nКонтакт: ${contact.trim()}")
    }
    val intent = Intent(Intent.ACTION_SEND).apply {
        type = if (attachment == null) "text/plain" else "image/*"
        putExtra(Intent.EXTRA_EMAIL, arrayOf("support@sporthub.app"))
        putExtra(Intent.EXTRA_SUBJECT, "Обратная связь SportHub")
        putExtra(Intent.EXTRA_TEXT, body)
        attachment?.let {
            putExtra(Intent.EXTRA_STREAM, it)
            clipData = ClipData.newRawUri("Скриншот", it)
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }
    }
    runCatching { context.startActivity(Intent.createChooser(intent, "Отправить обратную связь")) }
        .onFailure { Toast.makeText(context, "Не найдено приложение для отправки", Toast.LENGTH_SHORT).show() }
}

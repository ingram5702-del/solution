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
    MoreItem("Stopwatch", "Stopwatch", "Laps, pause, and precise time tracking", "◷"),
    MoreItem("QR Scanner", "QR Scanner", "Find an official match link", "▣"),
    MoreItem("Settings", "Settings", "Theme, notifications, and feedback", "⚙"),
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
        "Stopwatch" -> StopwatchScreen()
        "QR Scanner" -> QrScannerScreen()
        "Settings" -> SettingsScreen(settings, onThemeChanged, onDynamicColorChanged)
        else -> LazyColumn(
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            item { Text("Sports tools", style = MaterialTheme.typography.titleLarge) }
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
    var mode by rememberSaveable { mutableStateOf("Running") }
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
            listOf("Running", "Match", "Free").forEach { option ->
                FilterChip(
                    selected = mode == option,
                    onClick = { mode = option },
                    label = { Text(option) },
                )
            }
        }
        Text("Mode: $mode", color = MaterialTheme.colorScheme.primary)
        Text("Precise time", color = MaterialTheme.colorScheme.onSurfaceVariant)
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
            }) { Text(if (running) "Pause" else "Start") }
            OutlinedButton(onClick = { if (displayed > 0) laps.add(0, displayed) }) { Text("Lap") }
            OutlinedButton(onClick = {
                running = false; baseElapsed = 0; displayed = 0; startMarker = 0; laps.clear()
            }) { Text("Reset") }
        }
        if (laps.isNotEmpty()) {
            HorizontalDivider()
            LazyColumn(Modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                itemsIndexed(laps) { index, lap ->
                    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text("Lap ${laps.size - index}")
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
    return String.format(Locale.ENGLISH, "%02d:%02d.%02d", minutes, seconds, hundredths)
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
        Text("QR Code Scanner", style = MaterialTheme.typography.headlineSmall)
        Text(
            "Point the camera at a QR code. A link will open only after you confirm it.",
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
        Button(onClick = {
            error = null
            scanner.startScan()
                .addOnSuccessListener { barcode -> result = barcode.rawValue ?: "Empty QR code" }
                .addOnFailureListener { error = "Could not scan the code" }
        }) { Text("Scan") }
        result?.let { value ->
            Card(Modifier.fillMaxWidth()) {
                Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text("Result", style = MaterialTheme.typography.labelLarge)
                    Text(value)
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        if (isWebLink) {
                            Button(onClick = {
                                context.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(value)))
                            }) { Text("Open link") }
                        }
                        OutlinedButton(onClick = {
                            val clipboard = context.getSystemService(ClipboardManager::class.java)
                            clipboard.setPrimaryClip(ClipData.newPlainText("QR", value))
                            Toast.makeText(context, "Copied", Toast.LENGTH_SHORT).show()
                        }) { Text("Copy") }
                    }
                }
            }
        }
        error?.let { Text(it, color = MaterialTheme.colorScheme.error) }
        Text(
            "Check the website address and use official broadcasts only.",
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
        item { Text("Appearance", style = MaterialTheme.typography.titleLarge) }
        item {
            Card(Modifier.fillMaxWidth()) {
                Column(Modifier.padding(8.dp)) {
                    ThemeMode.entries.forEach { mode ->
                        val label = when (mode) {
                            ThemeMode.SYSTEM -> "Use system setting"
                            ThemeMode.LIGHT -> "Light"
                            ThemeMode.DARK -> "Dark"
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
                            Text("Dynamic colors")
                            Text("Wallpaper colors on Android 12+", style = MaterialTheme.typography.bodySmall)
                        }
                        Switch(settings.dynamicColor, onCheckedChange = onDynamicColorChanged)
                    }
                }
            }
        }
        item { Text("Feedback", style = MaterialTheme.typography.titleLarge) }
        item {
            Card(Modifier.fillMaxWidth()) {
                Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    OutlinedTextField(
                        value = contact,
                        onValueChange = { contact = it },
                        label = { Text("Your email (optional)") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                    )
                    OutlinedTextField(
                        value = feedback,
                        onValueChange = { feedback = it },
                        label = { Text("Message") },
                        modifier = Modifier.fillMaxWidth(),
                        minLines = 4,
                    )
                    OutlinedButton(onClick = { attachmentPicker.launch("image/*") }) {
                        Text(if (attachment == null) "Add screenshot" else "Screenshot attached")
                    }
                    Button(
                        enabled = feedback.isNotBlank(),
                        onClick = { sendFeedback(context, contact, feedback, attachment) },
                        modifier = Modifier.fillMaxWidth(),
                    ) { Text("Send") }
                }
            }
        }
        item {
            Text("Winner SportHub 1.0 · Notes and calendar data are stored on your device.", style = MaterialTheme.typography.bodySmall)
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
        if (contact.isNotBlank()) append("\n\nContact: ${contact.trim()}")
    }
    val intent = Intent(Intent.ACTION_SEND).apply {
        type = if (attachment == null) "text/plain" else "image/*"
        putExtra(Intent.EXTRA_EMAIL, arrayOf("support@sporthub.app"))
        putExtra(Intent.EXTRA_SUBJECT, "Winner SportHub feedback")
        putExtra(Intent.EXTRA_TEXT, body)
        attachment?.let {
            putExtra(Intent.EXTRA_STREAM, it)
            clipData = ClipData.newRawUri("Screenshot", it)
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }
    }
    runCatching { context.startActivity(Intent.createChooser(intent, "Send feedback")) }
        .onFailure { Toast.makeText(context, "No compatible app found", Toast.LENGTH_SHORT).show() }
}

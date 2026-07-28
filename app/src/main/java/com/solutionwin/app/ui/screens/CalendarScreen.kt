package com.solutionwin.app.ui.screens

import android.Manifest
import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.pm.PackageManager
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import com.solutionwin.app.data.SportEventRepository
import com.solutionwin.app.domain.SportEvent
import com.solutionwin.app.domain.SportEventType
import com.solutionwin.app.notifications.ReminderScheduler
import dagger.hilt.android.lifecycle.HiltViewModel
import java.text.DateFormat
import java.util.Calendar
import java.util.Date
import javax.inject.Inject
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

@HiltViewModel
class CalendarViewModel @Inject constructor(
    private val repository: SportEventRepository,
    private val scheduler: ReminderScheduler,
) : ViewModel() {
    val events = repository.observeAll().stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    fun add(title: String, type: SportEventType, startAt: Long, reminderMinutes: Int) = viewModelScope.launch {
        val saved = repository.add(SportEvent(0, title.trim(), type, startAt, reminderMinutes))
        scheduler.schedule(saved)
    }

    fun delete(event: SportEvent) = viewModelScope.launch {
        scheduler.cancel(event)
        repository.delete(event)
    }
}

@Composable
fun CalendarScreen(viewModel: CalendarViewModel = viewModel()) {
    val events by viewModel.events.collectAsStateWithLifecycle()
    val context = LocalContext.current
    var showEditor by remember { mutableStateOf(false) }
    val notificationPermissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission(),
    ) { }

    Box(Modifier.fillMaxSize()) {
        if (events.isEmpty()) {
            Column(
                Modifier.align(Alignment.Center).padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(10.dp),
            ) {
                Text("Календарь свободен", style = MaterialTheme.typography.titleLarge)
                Text("Добавьте матч или тренировку и выберите время напоминания.")
                Button(onClick = { showEditor = true }) { Text("Добавить событие") }
            }
        } else {
            LazyColumn(
                contentPadding = PaddingValues(16.dp, 16.dp, 16.dp, 88.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                items(events, key = SportEvent::id) { event ->
                    Card(Modifier.fillMaxWidth()) {
                        Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(5.dp)) {
                            Text(
                                if (event.type == SportEventType.MATCH) "МАТЧ" else "ТРЕНИРОВКА",
                                style = MaterialTheme.typography.labelLarge,
                                color = MaterialTheme.colorScheme.primary,
                            )
                            Text(event.title, style = MaterialTheme.typography.titleMedium)
                            Text(DateFormat.getDateTimeInstance(DateFormat.MEDIUM, DateFormat.SHORT).format(Date(event.startAt)))
                            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                Text("Напомнить за ${event.reminderMinutes} мин", style = MaterialTheme.typography.bodySmall)
                                TextButton(onClick = { viewModel.delete(event) }) { Text("Удалить") }
                            }
                        }
                    }
                }
            }
        }
        FloatingActionButton(
            onClick = { showEditor = true },
            modifier = Modifier.align(Alignment.BottomEnd).padding(16.dp),
        ) { Text("＋") }
    }

    if (showEditor) {
        EventEditorDialog(
            onDismiss = { showEditor = false },
            onSave = { title, type, startAt, reminder ->
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU &&
                    ContextCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED
                ) {
                    notificationPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
                }
                viewModel.add(title.ifBlank { if (type == SportEventType.MATCH) "Матч" else "Тренировка" }, type, startAt, reminder)
                showEditor = false
            },
        )
    }
}

@Composable
private fun EventEditorDialog(
    onDismiss: () -> Unit,
    onSave: (String, SportEventType, Long, Int) -> Unit,
) {
    val context = LocalContext.current
    val initial = remember { Calendar.getInstance().apply { add(Calendar.HOUR_OF_DAY, 1) } }
    var title by remember { mutableStateOf("") }
    var type by remember { mutableStateOf(SportEventType.MATCH) }
    var startAt by remember { mutableLongStateOf(initial.timeInMillis) }
    var reminder by remember { mutableIntStateOf(30) }
    val formatted = DateFormat.getDateTimeInstance(DateFormat.MEDIUM, DateFormat.SHORT).format(Date(startAt))

    fun pickDateTime() {
        val current = Calendar.getInstance().apply { timeInMillis = startAt }
        DatePickerDialog(
            context,
            { _, year, month, day ->
                current.set(year, month, day)
                TimePickerDialog(
                    context,
                    { _, hour, minute -> current.set(Calendar.HOUR_OF_DAY, hour); current.set(Calendar.MINUTE, minute); startAt = current.timeInMillis },
                    current.get(Calendar.HOUR_OF_DAY),
                    current.get(Calendar.MINUTE),
                    true,
                ).show()
            },
            current.get(Calendar.YEAR),
            current.get(Calendar.MONTH),
            current.get(Calendar.DAY_OF_MONTH),
        ).show()
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Новое событие") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                OutlinedTextField(title, { title = it }, label = { Text("Название") }, singleLine = true)
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    FilterChip(type == SportEventType.MATCH, { type = SportEventType.MATCH }, label = { Text("Матч") })
                    FilterChip(type == SportEventType.TRAINING, { type = SportEventType.TRAINING }, label = { Text("Тренировка") })
                }
                Button(onClick = ::pickDateTime, modifier = Modifier.fillMaxWidth()) { Text(formatted) }
                Text("Напомнить заранее", style = MaterialTheme.typography.labelLarge)
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    listOf(15, 30, 60).forEach { minutes ->
                        FilterChip(reminder == minutes, { reminder = minutes }, label = { Text("$minutes мин") })
                    }
                }
            }
        },
        confirmButton = { TextButton(onClick = { onSave(title, type, startAt, reminder) }) { Text("Сохранить") } },
        dismissButton = { TextButton(onClick = onDismiss) { Text("Отмена") } },
    )
}

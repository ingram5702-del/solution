package com.solutionwin.app.notifications

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import com.solutionwin.app.domain.SportEvent
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ReminderScheduler @Inject constructor(
    @ApplicationContext private val context: Context,
) {
    private val alarmManager = context.getSystemService(AlarmManager::class.java)

    fun schedule(event: SportEvent) {
        val triggerAt = event.startAt - event.reminderMinutes * 60_000L
        if (triggerAt <= System.currentTimeMillis()) return
        alarmManager.setAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, triggerAt, pendingIntent(event))
    }

    fun cancel(event: SportEvent) {
        alarmManager.cancel(pendingIntent(event))
    }

    private fun pendingIntent(event: SportEvent): PendingIntent {
        val intent = Intent(context, ReminderReceiver::class.java).apply {
            putExtra(ReminderReceiver.EXTRA_TITLE, event.title)
            putExtra(ReminderReceiver.EXTRA_TYPE, event.type.name)
        }
        return PendingIntent.getBroadcast(
            context,
            event.id.hashCode(),
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE,
        )
    }
}

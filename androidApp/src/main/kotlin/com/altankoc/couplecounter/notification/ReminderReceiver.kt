package com.altankoc.couplecounter.notification

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.altankoc.couplecounter.domain.usecase.RescheduleRemindersUseCase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import org.koin.core.component.KoinComponent
import org.koin.core.component.get

class ReminderReceiver : BroadcastReceiver(), KoinComponent {

    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action != ACTION_SHOW_REMINDER) return

        val id = intent.getIntExtra(EXTRA_ID, INVALID_ID)
        val title = intent.getStringExtra(EXTRA_TITLE)
        val body = intent.getStringExtra(EXTRA_BODY)
        if (id != INVALID_ID && title != null && body != null) {
            ReminderNotifications.show(context, id, title, body)
        }

        val pendingResult = goAsync()
        CoroutineScope(SupervisorJob() + Dispatchers.Default).launch {
            try {
                get<RescheduleRemindersUseCase>().invoke()
            } finally {
                pendingResult.finish()
            }
        }
    }

    companion object {
        const val ACTION_SHOW_REMINDER = "com.altankoc.couplecounter.action.SHOW_REMINDER"
        const val EXTRA_ID = "extra_reminder_id"
        const val EXTRA_TITLE = "extra_reminder_title"
        const val EXTRA_BODY = "extra_reminder_body"
        private const val INVALID_ID = -1
    }
}
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

class RescheduleReceiver : BroadcastReceiver(), KoinComponent {

    override fun onReceive(context: Context, intent: Intent) {
        val pendingResult = goAsync()
        CoroutineScope(SupervisorJob() + Dispatchers.Default).launch {
            try {
                get<RescheduleRemindersUseCase>().invoke()
            } finally {
                pendingResult.finish()
            }
        }
    }
}
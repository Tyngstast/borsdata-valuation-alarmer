package com.github.tyngstast.borsdatavaluationalarmer.android.worker

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import co.touchlab.kermit.Logger
import com.github.tyngstast.borsdatavaluationalarmer.model.ValuationAlarmWorkerModel
import com.github.tyngstast.borsdatavaluationalarmer.android.messaging.TriggerWorkerMessagingService
import com.github.tyngstast.borsdatavaluationalarmer.android.util.NotificationFactory
import com.github.tyngstast.borsdatavaluationalarmer.injectLogger
import com.google.firebase.ktx.Firebase
import com.google.firebase.messaging.ktx.messaging
import kotlinx.coroutines.delay
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class ValuationAlarmWorker(
    context: Context,
    params: WorkerParameters
) : CoroutineWorker(context, params), KoinComponent {

    private val log: Logger by injectLogger("ValuationAlarmWorker")

    private val valuationAlarmWorkerModel: ValuationAlarmWorkerModel by inject()

    override suspend fun doWork(): Result {
        log.d { "doWork" }
        val context = applicationContext

        val onFailure: () -> Unit = {
            Firebase.messaging.unsubscribeFromTopic(TriggerWorkerMessagingService.TRIGGER_TOPIC)
            NotificationFactory(context).makeErrorNotification()
        }

        // sv or other
        val lang = context.resources.configuration.locales[0].language
        val result = valuationAlarmWorkerModel.run(lang, onFailure)
        return result?.forEach {
            NotificationFactory(context).makeAlarmTriggerNotification(it)
            delay(1000)
        }?.run { Result.success() } ?: Result.failure()
    }
}
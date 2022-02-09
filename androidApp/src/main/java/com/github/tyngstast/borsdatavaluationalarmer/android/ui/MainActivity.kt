package com.github.tyngstast.borsdatavaluationalarmer.android.ui

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import co.touchlab.kermit.Logger
import com.github.tyngstast.borsdatavaluationalarmer.android.ui.theme.AppTheme
import com.github.tyngstast.borsdatavaluationalarmer.android.worker.TriggerWorkerMessagingService.Companion.TRIGGER_TOPIC
import com.github.tyngstast.borsdatavaluationalarmer.injectLogger
import com.google.firebase.ktx.Firebase
import com.google.firebase.messaging.ktx.messaging
import org.koin.core.component.KoinComponent

class MainActivity : ComponentActivity(), KoinComponent {

    private val log: Logger by injectLogger("MainActivity")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        Firebase.messaging.subscribeToTopic(TRIGGER_TOPIC)
            .addOnCompleteListener { task ->
                log.d { "Subscribed to topic $TRIGGER_TOPIC: ${task.isSuccessful}" }
            }

        setContent {
            AppTheme {
                MainLayout()
            }
        }
    }
}
package com.github.tyngstast.borsdatavaluationalarmer.android.ui.list

import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material.DropdownMenu
import androidx.compose.material.DropdownMenuItem
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext

@Composable
fun Menu(onResetKey: () -> Unit) {
    var expanded by remember { mutableStateOf(false) }

    val context = LocalContext.current
    val feedbackEmailIntent: () -> Unit = {
        val email = "bd.varderingslarm@gmail.com"
        val intent = Intent(Intent.ACTION_SENDTO).apply {
            data = Uri.parse("mailto:$email?subject=Feedback")
            putExtra(Intent.EXTRA_SUBJECT, "Feedback")
        }
        if (intent.resolveActivity(context.packageManager) != null) {
            context.startActivity(intent)
        } else {
            Toast.makeText(
                context,
                "Hittade ingen app för e-post\r\n\nMaila oss på:\r\n$email",
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    Box(
        modifier = Modifier
            .wrapContentSize(Alignment.TopEnd)
    ) {
        IconButton(onClick = { expanded = true }) {
            Icon(Icons.Default.MoreVert, contentDescription = "Meny")
        }
        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
        ) {
            DropdownMenuItem(onClick = feedbackEmailIntent) {
                Text("Lämna Feedback")
            }
            DropdownMenuItem(onClick = onResetKey) {
                Text("Nollställ API-nyckel")
            }
        }
    }
}
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
import androidx.compose.ui.res.stringResource
import com.github.tyngstast.borsdatavaluationalarmer.android.R

@Composable
fun Menu(onResetKey: () -> Unit) {
    var expanded by remember { mutableStateOf(false) }
    val context = LocalContext.current

    val resetKey: () -> Unit = {
        expanded = false
        onResetKey()
    }

    val feedbackEmailIntent: () -> Unit = {
        expanded = false
        val email = context.getString(R.string.contact_email)
        val intent = Intent(Intent.ACTION_SENDTO).apply {
            data = Uri.parse("mailto:$email?subject=Feedback")
            putExtra(Intent.EXTRA_SUBJECT, "Feedback")
        }
        try {
            context.startActivity(intent)
        } catch (e: Throwable) {
            val errorMsg = context.getString(R.string.menu_toast_email_error)
            Toast.makeText(
                context,
                "$errorMsg$email",
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    Box(
        modifier = Modifier
            .wrapContentSize(Alignment.TopEnd)
    ) {
        IconButton(onClick = { expanded = true }) {
            Icon(Icons.Default.MoreVert, contentDescription = stringResource(R.string.menu_icon_cd))
        }
        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
        ) {
            DropdownMenuItem(onClick = feedbackEmailIntent) {
                Text(stringResource(R.string.menu_feedback))
            }
            DropdownMenuItem(onClick = resetKey) {
                Text(stringResource(R.string.menu_reset))
            }
        }
    }
}
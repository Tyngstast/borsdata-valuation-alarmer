package com.github.tyngstast.borsdatavaluationalarmer.android.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.material.LocalTextStyle
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.github.tyngstast.db.Alarm

@Composable
fun AlarmView(alarm: Alarm) {
    Card(
        modifier = Modifier
            .shadow(
                elevation = 4.dp,
                shape = RoundedCornerShape(8.dp),
                clip = true
            )
            .fillMaxWidth()
            .padding(top = 2.dp, start = 4.dp, end = 4.dp, bottom = 2.dp),
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            Column(Modifier.padding(12.dp)) {
                Column {
                    Text(
                        text = "Bolag",
                        style = LocalTextStyle.current.copy(fontSize = 12.sp, color = Color.DarkGray)
                    )
                }
                Column {
                    Text(text = alarm.insName)
                }
            }
            Row(horizontalArrangement = Arrangement.End) {
                Column(Modifier.padding(12.dp)) {
                    Column {
                        Text(
                            text = "Nyckeltal",
                            style = LocalTextStyle.current.copy(fontSize = 12.sp, color = Color.DarkGray)
                        )
                    }
                    Column {
                        Text(text = alarm.kpiName)
                    }
                }
                Column(Modifier.padding(12.dp)) {
                    Column {
                        Text(
                            text = "Värde",
                            style = LocalTextStyle.current.copy(fontSize = 12.sp, color = Color.DarkGray)
                        )
                    }
                    Column {
                        Text(text = alarm.kpiValue.toString())
                    }
                }
            }
        }
    }
}

@Preview
@Composable
fun AlarmViewPreview() {
    MaterialTheme {
        Column {
            AlarmView(alarm = Alarm(1, 2, "Evolution", 2, "P/E", 40.0, "lte"))
            AlarmView(alarm = Alarm(1, 2, "Kambi", 2, "P/E", 20.0, "lte"))
        }
    }
}

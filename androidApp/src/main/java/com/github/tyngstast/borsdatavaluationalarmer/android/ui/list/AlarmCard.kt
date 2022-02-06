package com.github.tyngstast.borsdatavaluationalarmer.android.ui.list

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.Card
import androidx.compose.material.LocalTextStyle
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.github.tyngstast.db.Alarm

@Composable
fun AlarmView(alarm: Alarm) {
    val screenWidth = LocalConfiguration.current.screenWidthDp
    val col = screenWidth.div(12)

    Card(Modifier.fillMaxWidth()) {
        Row(
            verticalAlignment = Alignment.Top,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(
                Modifier
                    .padding(12.dp)
                    .width(col.times(6).dp)
            ) {
                Column {
                    Text(
                        text = "Bolag",
                        style = LocalTextStyle.current.copy(
                            fontSize = 12.sp,
                            color = Color.DarkGray
                        )
                    )
                }
                Column {
                    Text(text = alarm.insName)
                }
            }
            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top,
                modifier = Modifier
                    .padding(vertical = 12.dp)
                    .width(col.times(6).dp)
            ) {
                Column(
                    Modifier.width(col.times(3).dp)
                ) {
                    Column {
                        Text(
                            text = "Nyckeltal",
                            style = LocalTextStyle.current.copy(
                                fontSize = 12.sp,
                                color = Color.DarkGray
                            )
                        )
                    }
                    Column {
                        Text(
                            text = alarm.kpiName,
                            maxLines = 2,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }
                Column(
                    Modifier
                        .padding(horizontal = 8.dp)
                        .width(col.times(3).dp),
                    horizontalAlignment = Alignment.End
                ) {
                    Column {
                        Text(
                            text = "Värde",
                            style = LocalTextStyle.current.copy(
                                fontSize = 12.sp,
                                color = Color.DarkGray
                            )
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
            AlarmView(alarm = Alarm(1, 2, "Evolution Gaming", "", 2, "P/E", 40.0, "lte"))
            AlarmView(alarm = Alarm(1, 2, "Kambi", "", 2, "EV/EBITDA", 100.0, "lte"))
            AlarmView(alarm = Alarm(1, 2, "Brdr. A&O Johansen", "", 2, "Direktavkastning", 20.0, "lte"))
        }
    }
}

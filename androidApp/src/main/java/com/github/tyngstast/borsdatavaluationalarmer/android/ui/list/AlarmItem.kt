package com.github.tyngstast.borsdatavaluationalarmer.android.ui.list

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.ContentAlpha
import androidx.compose.material.LocalTextStyle
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.github.tyngstast.borsdatavaluationalarmer.android.ui.theme.textLabel
import com.github.tyngstast.db.Alarm

@Composable
fun AlarmItem(alarm: Alarm, backgroundColor: Color = MaterialTheme.colors.background) {
    val screenWidth = LocalConfiguration.current.screenWidthDp
    val col = screenWidth.div(12)

    Box(
        Modifier
            .fillMaxWidth()
            .background(backgroundColor)
            .alpha(if (alarm.disabled == true) ContentAlpha.disabled else 1.0f)
    ) {
        Row(
            modifier = Modifier.padding(vertical = 12.dp),
            verticalAlignment = Alignment.Top,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(
                Modifier
                    .padding(horizontal = 8.dp)
                    .width(col.times(6).dp)
            ) {
                Column {
                    Text(
                        text = "Bolag",
                        style = LocalTextStyle.current.copy(
                            fontSize = 12.sp,
                            color = MaterialTheme.colors.textLabel
                        )
                    )
                }
                Column {
                    Text(
                        text = alarm.insName,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }
            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top,
                modifier = Modifier
                    .width(col.times(6).dp)
            ) {
                Column(
                    Modifier.width(col.times(3.1).dp)
                ) {
                    Column {
                        Text(
                            text = "Nyckeltal",
                            style = LocalTextStyle.current.copy(
                                fontSize = 12.sp,
                                color = MaterialTheme.colors.textLabel
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
                        .width(col.times(2.9).dp),
                    horizontalAlignment = Alignment.End
                ) {
                    Column {
                        Text(
                            text = "Värde",
                            style = LocalTextStyle.current.copy(
                                fontSize = 12.sp,
                                color = MaterialTheme.colors.textLabel
                            )
                        )
                    }
                    Column {
                        Text(
                            text = alarm.kpiValue.toString(),
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
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
            AlarmItem(alarm = Alarm(1, 2, "Evolution Gaming", "", 2, "P/E", 40.0, "lte", false))
            AlarmItem(alarm = Alarm(1, 2, "Kambi", "", 2, "EV/EBITDA", 100.0, "lte", false))
            AlarmItem(
                alarm = Alarm(
                    1,
                    2,
                    "Brdr. A&O Johansen",
                    "",
                    2,
                    "Direktavkastning",
                    20.0,
                    "lte",
                    false
                )
            )
        }
    }
}

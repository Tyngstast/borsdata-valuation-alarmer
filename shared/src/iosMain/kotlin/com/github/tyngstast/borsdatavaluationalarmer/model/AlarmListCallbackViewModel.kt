package com.github.tyngstast.borsdatavaluationalarmer.model

@Suppress("unused")
class AlarmListCallbackViewModel(
   alarmListModel: AlarmListModel
) : CallbackViewModel() {

   override val viewModel = AlarmListViewModel(alarmListModel)

   val alarmListState = viewModel.alarmListStateFlow.asCallbacks()

   fun updateDisableAlarm(id: Long, disable: Boolean) {
      viewModel.updateDisableAlarm(id, disable)
   }

   fun deleteAlarm(id: Long) {
      viewModel.deleteAlarm(id)
   }
}
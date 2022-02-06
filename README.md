# Börsdata Valuation Alarm

## About
  * Uses Börsdata API.
  * Requires personal API key from Pro membership.

### Data Updates and Alarm Triggers
  * BD only provides API data for nordic companies unfortunately.
  * Data is also only updated once per day at 21:00 CET.
  * Alarm will therefore schedule update and provide notification for alarms before market open every weekday, and saturday morning.

## Tech
  * KMM project.
    * Ktor
    * Sql Delight
    * KVault
    * WorkManager
    
## TODO 
  * iOS...
  * Show some info graphic and indication when kpi is calculated on the fly (manually)


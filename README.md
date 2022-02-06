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
  * Implement Yahoo Finance for prices
      * Probably won't do for all KPIs
      * Maybe show an info graphic when value is calculated daily (manually) or not


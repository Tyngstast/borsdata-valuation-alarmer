# Börsdata Valuation Alarmer

## About
  * Uses Börsdata API.
  * Requires personal API key from Pro membership.

### Data Updates and Alarm Triggers
  * BD only provides API data for nordic companies unfortunately.
  * BD data is only updated once per day at 21:00 CET.
  * Schedule for alarm triggers will run on the hour, every hour, during market open (swedish time) and once on saturday morning.
    * Some KPIs are calculated on the fly, others will only change once per day. Current "flying":
      * P/E 
      * EV/EBIT
      * EV/EBITDA 
      * EV/FCF 
      * EV/S

## Tech
  * KMM project.
    * Ktor
    * Sql Delight
    * KVault
    * WorkManager
    
## TODO 
  * iOS...
    * Need to look into if it's even possible to reliably run background work on iOS.
  * Show some info graphic and indication when kpi is calculated on the fly (manually)


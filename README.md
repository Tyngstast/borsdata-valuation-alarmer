# Börsdata Valuation Alarmer

**Only working Android implementation** for now. See [TODO](#todo).

## About
  * Uses Börsdata API.
  * Requires personal API key from Pro membership.
  * No separate backend, and key is only stored, encrypted, on local device.

### Data Updates and Alarm Triggers
  * BD only provides API data for nordic companies unfortunately.
  * BD data is only updated once per day at 21:00 CET.
  * Schedule for alarm triggers will run on the hour, every hour, during market open (swedish time) and once on saturday morning.
    * Some KPIs are calculated on the fly, others will only change once per day. Current "flying":
      * `P/E` 
      * `EV/EBIT`
      * `EV/EBITDA` 
      * `EV/FCF` 
      * `EV/S`

## Tech
  * KMM project.
    * Ktor
    * Sql Delight
    * KVault
    * WorkManager
    
## TODO 

#### Possible hack idea to solve background execution without an actual backend service
Set up serverless functions for registering user ids and ping their devices. 
  * Register/unregister function
  * NoSQL DB with only device ids
  * Sheduled function that "pings" devices. Execution and all data will still be limited to user's device.

**Android**
  * Trouble with scheduled work not running after a night of sleep. It is likely the app is "completely killed" after such a time of inactivty.
    * Look into combining AlarmManager with WorkManager to wake and "start cycle" each day.
  * Show some info graphic and indication when kpi is calculated on the fly (manually)
  * Click notification to open app.
  * Alarm snooze / disable.
 
**iOS**
  * Complete GUI.
  * Need to look into if it's even possible to reliably run background work on iOS.
    * A physical device is needed to PoC, which the author does not possess.


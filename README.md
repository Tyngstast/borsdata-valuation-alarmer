# Börsdata Valuation Alarmer

**Only working Android implementation** for now. See [TODO](#todo).

## About
  * Uses Börsdata API.
  * Requires personal API key from Pro membership.
  * No separate backend, and key is only stored, encrypted, on local device.

### Data Updates and Alarm Triggers
  * BD only provides API data for nordic companies unfortunately.
  * BD data is only updated once per day at 21:00 CET.
  * Schedule for alarm triggers will run on the hour, every hour, during market open (swedish time).
    * Some KPIs are calculated on the fly, others will only change once per day. Current "fluent" (marked by lightning bolt when selecting):
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
    * Firebase Messaging
    
## TODO 
**Android**
  * Alarm snooze / disable.
 
**iOS**
  * Complete GUI.
  * Need to look into if it's even possible to reliably run background work on iOS.
    * A physical device is needed to PoC, which the author does not possess.


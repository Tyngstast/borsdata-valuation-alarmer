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
    * Koin
    * KVault
    * Multiplatform Settings
  * Android
    * Jetpack Compose
    * Coroutines with StateFlow
    * WorkManager
    * Firebase Messaging
  * iOS
    * TODO
  * Firebase Cloud Messaging (FCM)
    * Cloud Function serves as "ping" to execute workers on mobile devices. No data is stored in any backend. 
  
## TODO 
**Android**
  * Show indication of items being swipable when clicking.
  * Font family seems to be ignored on OnePlus (and maybe other brand).

**iOS**
  * Complete GUI implementation.
  * A physical device is needed to PoC BackgroundTasks, which the author does not possess.


# 📍 GeofenceTrigger

An Android app that triggers webhook events when you enter or leave geographic zones. Built for integration with [OpenClaw](https://github.com/openclaw/openclaw) but works with any webhook endpoint.

![Build](https://github.com/beldjilali/geofence-trigger/actions/workflows/build.yml/badge.svg)

## Features

- **Geofencing** — Uses Google Play Services `GeofencingClient` for reliable enter/exit detection
- **Webhook Integration** — Sends JSON POST with Bearer auth on every geofence event
- **Google Maps UI** — Visual map with geofence zones displayed as circles
- **Background Service** — Foreground service ensures reliable detection even when app is closed
- **Event History** — Full log of all geofence events with webhook delivery status
- **Configurable** — Set webhook URL, auth token, device name, and zone radius

## Webhook Payload

```json
{
  "event": "enter | exit",
  "zone_name": "Home",
  "zone_id": "uuid",
  "latitude": 48.8566,
  "longitude": 2.3522,
  "radius": 100,
  "timestamp": "2026-03-04T22:00:00Z",
  "device_id": "my-phone"
}
```

## Tech Stack

- **Kotlin** + **Jetpack Compose** + **Material 3**
- **Google Maps Compose** for map UI
- **Room** database for zones & event log
- **Hilt** for dependency injection
- **WorkManager** for reliable webhook delivery with retry
- **DataStore** for preferences
- **OkHttp/Retrofit** for HTTP

## Setup

1. Clone the repo
2. Add your Google Maps API key in `gradle.properties`:
   ```
   MAPS_API_KEY=your_key_here
   ```
3. Open in Android Studio and build

### CI/CD

The GitHub Actions pipeline builds a debug APK on every push. Download it from the Actions artifacts.

For releases, create a GitHub release and a signed APK will be attached automatically (requires signing secrets).

## OpenClaw Integration

Configure your OpenClaw webhook:

```json5
{
  hooks: {
    mappings: [
      {
        match: { path: "geofence" },
        action: "agent",
        name: "Geofence",
        wakeMode: "now",
        deliver: true,
        messageTemplate: "📍 Geofence {{event}} — {{zone_name}} at {{latitude}}, {{longitude}}"
      }
    ]
  }
}
```

Then set the webhook URL in the app to `https://your-gateway/hooks/geofence`.

## License

MIT

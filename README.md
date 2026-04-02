# FocusGuard

A digital wellbeing Android app that blocks food delivery apps, adult content, and food-related YouTube videos to help you stay focused and build healthier habits.

## Features

### App Blocking
- Blocks 25+ food delivery apps (Uber Eats, DoorDash, Zomato, Swiggy, Deliveroo, etc.)
- Blocks known adult content apps
- Detects foreground app every 500ms and redirects to a "Blocked" screen

### Web Content Filter
- Local VPN-based DNS filter — no data leaves your device
- Blocks 20+ adult content domains
- Blocks 12+ food delivery websites in any browser
- Zero-log policy: your browsing data is never stored or transmitted

### YouTube Food Filter
- Accessibility service monitors YouTube for food-related content
- Detects 35+ food keywords (mukbang, recipe, cooking, food review, etc.)
- Auto-scrolls past food videos or clicks "Not interested"
- Only active inside the YouTube app — does not affect other apps

### PIN Lock
- Set a 4-digit PIN to prevent disabling the blocker
- SHA-256 hashed — your PIN is never stored in plain text
- Required to turn off blocking or temporarily unlock
- Temporary 5-minute unlock option for emergencies

### Scheduling
- Set active blocking hours (e.g., 9 AM to 9 PM)
- Supports overnight schedules (e.g., 10 PM to 6 AM)
- Blocking auto-starts and stops on schedule
- Uses AlarmManager for reliable daily scheduling

### Persistence
- Auto-restarts after device reboot via Boot Receiver
- Runs as a foreground service with persistent notification
- Optional Device Admin to prevent easy uninstall

## Screenshots

| Dashboard | Blocked Screen | Schedule |
|-----------|---------------|----------|
| Main control panel with toggles | Shown when a blocked app is opened | Time picker for blocking hours |

## Requirements

- Android 8.0 (API 26) or higher
- Permissions needed:
  - **Usage Access** — detect foreground app
  - **Accessibility Service** — filter YouTube content
  - **VPN** — block websites via DNS
  - **Notification** — show service status

## Installation

### Download APK
1. Go to [Actions](https://github.com/marketingsales-debug/FocusGuard/actions)
2. Click the latest successful **Build APK** run
3. Download the **FocusGuard-debug** artifact
4. Transfer the APK to your Android device
5. Enable "Install from unknown sources" and install

### Build from Source
```bash
git clone https://github.com/marketingsales-debug/FocusGuard.git
cd FocusGuard
./gradlew assembleDebug
```
The APK will be at `app/build/outputs/apk/debug/app-debug.apk`

## Setup Guide

1. **Open FocusGuard** after installing
2. **Grant Usage Access** — tap the button, find FocusGuard, enable it
3. **Enable Accessibility** — tap the button, find FocusGuard, enable it
4. **Set a PIN** — tap "Setup PIN Lock" and enter a 4-digit PIN
5. **Configure Schedule** (optional) — set your active blocking hours
6. **Toggle on** the main blocking switch

## Architecture

```
com.focusguard.app/
├── FocusGuardApp.kt                 # Application class, notification channel
├── data/
│   ├── PrefsManager.kt              # SharedPreferences wrapper
│   └── BlockedApps.kt               # Blocklists (apps, domains, keywords)
├── service/
│   ├── AppBlockerService.kt         # Foreground service, app detection
│   ├── ContentFilterVpnService.kt   # Local VPN, DNS filtering
│   └── YouTubeFilterService.kt      # Accessibility service, content filter
├── receiver/
│   ├── BootReceiver.kt              # Auto-start on reboot
│   ├── ScheduleReceiver.kt          # Timed start/stop via AlarmManager
│   └── AdminReceiver.kt             # Device admin (prevent uninstall)
├── ui/
│   ├── MainActivity.kt              # Dashboard with toggles and permissions
│   ├── BlockedActivity.kt           # "Content Blocked" overlay
│   ├── PinActivity.kt               # PIN setup and verification
│   └── ScheduleActivity.kt          # Time picker for schedule
└── util/
    ├── PinUtil.kt                   # SHA-256 PIN hashing
    └── ScheduleUtil.kt              # Time range checking
```

## Blocked Apps List

<details>
<summary>Food Delivery Apps (25+)</summary>

- Uber Eats
- DoorDash
- Grubhub
- Postmates
- Instacart
- Seamless
- Swiggy
- Zomato
- Faasos
- Deliveroo
- Just Eat
- Foodpanda
- GrabFood
- GoFood
- McDonald's
- Burger King
- Subway
- Pizza Hut
- Domino's
- Waitr
- SliceLife
- Caviar
- EatStreet
- GoPuff

</details>

<details>
<summary>YouTube Food Keywords (35+)</summary>

recipe, cooking, food, mukbang, eating, restaurant, chef, baking, kitchen, meal, dinner, lunch, breakfast, snack, delicious, tasty, yummy, cuisine, asmr eating, food review, what i eat, grocery, food haul, pizza, burger, sushi, ramen, noodle, cake, dessert, bbq, grill, fry, cook with me, food challenge, eating show, street food, food tour, biryani, paneer, chicken, mutton, fish fry

</details>

## Privacy

- **No data collection** — everything runs locally on your device
- **No internet required** — the VPN is local-only for DNS filtering
- **No analytics or tracking**
- **PIN is hashed** with SHA-256 before storage
- **Open source** — audit the code yourself

## Tech Stack

- **Language:** Kotlin
- **Min SDK:** 26 (Android 8.0)
- **Target SDK:** 34 (Android 14)
- **UI:** Material Design 3
- **Build:** Gradle 8.5 with Kotlin DSL
- **CI/CD:** GitHub Actions

## Contributing

1. Fork the repository
2. Create a feature branch (`git checkout -b feature/my-feature`)
3. Commit your changes
4. Push to the branch
5. Open a Pull Request

## License

MIT License — see [LICENSE](LICENSE) for details.

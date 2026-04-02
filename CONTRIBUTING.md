# Contributing to FocusGuard

Thanks for your interest in contributing!

## How to Contribute

### Reporting Bugs
- Open an issue with a clear title and description
- Include your Android version and device model
- Add steps to reproduce the bug

### Suggesting Features
- Open an issue with the "enhancement" label
- Describe the feature and why it would be useful

### Submitting Code

1. Fork the repo and create a branch from `main`
2. Make your changes
3. Test on a real device (emulators don't support VPN/Accessibility well)
4. Ensure the build passes: `./gradlew assembleDebug`
5. Open a pull request

### Code Style
- Follow Kotlin coding conventions
- Use 4 spaces for indentation
- Keep files under 500 lines
- Add KDoc comments for public functions

### Adding Blocked Apps/Domains
To add new entries to the blocklists, edit `BlockedApps.kt`:
- `foodDeliveryApps` — package names of food apps
- `adultApps` — package names of adult apps
- `foodKeywords` — keywords to match in YouTube
- `blockedDomains` — domains to block via DNS
- `foodDomains` — food delivery website domains

### Testing
- Test app blocking with actual food delivery apps installed
- Test VPN by visiting a blocked domain in Chrome
- Test YouTube filter by searching for food content
- Test PIN by setting one and trying to disable blocking
- Test schedule by setting a short time window

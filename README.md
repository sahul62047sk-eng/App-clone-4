# Multi Clone Manager (Android starter)

A small Android app for maintaining a large list of account/profile labels and launching the corresponding **official installed apps**.

## Supported app launch targets
- WhatsApp
- Telegram
- Instagram
- Facebook
- MyJio
- Airtel Thanks
- Swiggy

## Current scope
- Add unlimited profile labels (subject to device storage).
- Delete saved labels.
- Open the official app associated with a profile.
- Store labels locally on the device.

## Important limitations
This starter does **not** clone third-party app binaries, create isolated Android user spaces, automate OTPs, or bypass any provider's account/device restrictions. A profile is a label and launcher shortcut, not a separate authenticated session. For genuinely separate app data, use Android's built-in Dual Apps / app cloning feature (if your phone provides it) or a supported Android Work Profile.

The app intentionally does not collect or store phone numbers, passwords, or OTPs.

## Build APK with GitHub Actions
1. Create a new GitHub repository.
2. Upload/push the contents of this project to the repository's `main` branch.
3. Open **Actions** and select **Build Android APK**.
4. Run the workflow (or let it run after a push).
5. Open the successful workflow run and download the `MultiCloneManager-debug` artifact.
6. Extract the artifact to get `app-debug.apk`, then install it on your Android phone. You may need to allow installation from your chosen file manager/browser.

## Build locally
Requires JDK 17 and Android SDK platform 35:
```bash
gradle assembleDebug
```

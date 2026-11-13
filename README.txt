PINAM starter
1. Unzip, then Android Studio > File > Open > select the "Pinam" folder (contains settings.gradle.kts).
2. Sync. Accept the SDK 35 install prompt if shown.
3. Run on an emulator/phone. You should see Login > Circles > Map list > Pin detail, all on fake data.
4. Firebase: see the steps in the chat. Put google-services.json in the app/ folder, sync again,
   and the Login screen will say "Firebase: connected".
5. Maps key: create local.properties entry   MAPS_API_KEY=your_key   (file is git-ignored).

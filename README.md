# AI Pirate

A brand-new Android AI companion app: an original AI pirate captain who chats,
tells stories, riddles, jokes and shers, keeps a daily Captain's Log, and
translates plain sentences into pirate speak.

- Kotlin + Jetpack Compose + Material3, minSdk 26, targetSdk 34
- Backend: Google Gemini only (`gemini-2.0-flash` chat,
  `gemini-2.5-flash-preview-tts` with voice Aoede for natural spoken replies).
  No Android robotic TTS is used for replies.
- The user's Gemini API key is typed in the app's Settings screen on the
  phone. It is never in the code, the repo, or any log.
- Memory: past chats are stored locally on the phone (DataStore + local
  history file) and the captain refers back to them.
- Daily riddle and Captain's Log change every day (picked by day of year).
- Build: GitHub Actions (`./gradlew assembleDebug`), debug APK artifact.

The pirate is an ORIGINAL AI character. He never claims to be Johnny Depp or
any movie character.

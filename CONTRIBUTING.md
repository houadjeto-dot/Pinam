# Pinam team rules

## Ownership (each pair edits only its own files)
| Pair | Files |
|---|---|
| 1 Auth + Circles | ui/screens/LoginScreen.kt, CirclesScreen.kt, Firebase auth/circle repositories, Firestore security rules |
| 2 Map + integration | ui/screens/CircleMapScreen.kt, Navigation.kt (tech lead approves changes) |
| 3 Add Pin | ui/screens/AddPinScreen.kt, location + photo upload code |
| 4 Pin detail | ui/screens/PinDetailScreen.kt, ui/components/ (shared UI) |
| 5 Feed + Recs | ui/screens/RecommendationsScreen.kt, domain/Recommendations.kt, testing |

Shared files (model/, data/Repositories.kt, theme, build.gradle, libs.versions.toml): change only in a SMALL pull request, announce it in the group chat first.

## Git
- `main` always builds. Never commit directly to it.
- Branch per feature: `feature/add-pin`, `feature/login`, ...
- Open a pull request; someone from ANOTHER pair reviews it before merge.
- Commit small and often, with clear messages ("Add vibe chip row to AddPinScreen").
- Pull from main before you start work each day: `git pull origin main`.
- Never commit local.properties or API keys. google-services.json IS committed (private repo).

## How to swap fake data for Firebase
Write e.g. `FirebasePinRepository : PinRepository`, then change one line in data/ServiceLocator.kt.

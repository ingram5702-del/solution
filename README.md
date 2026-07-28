# Winner SportHub

Android-приложение для спортивных новостей, матчей, тренировок и личных инструментов.

Package name: `com.solutionwin.app`.

## Что уже работает

- демонстрационная спортивная лента с переходом на официальные источники;
- секундомер для бега, матча и свободного замера, включая круги;
- локальные заметки;
- тактическая доска со схемами 4-4-2, 4-3-3, 4-2-3-1, 4-3-2-1, 3-5-2 и 3-2-4-1;
- QR-сканер с подтверждением перед открытием ссылки;
- календарь матчей и тренировок;
- уведомления за 15, 30 или 60 минут до события;
- светлая, тёмная, системная и динамическая темы;
- обратная связь с прикреплением скриншота.

Заметки, календарь и настройки хранятся локально. Новостная лента пока использует демонстрационные материалы: для публикации приложения следует подключить лицензированный спортивный API.

## Сборка

Требуются Android SDK 35 и Java 17.

```bash
./gradlew assembleDebug
```

APK появится в `app/build/outputs/apk/debug/app-debug.apk`.

## Privacy Policy

The published Privacy Policy is available at:

https://ingram5702-del.github.io/solution/

## Firebase URL switch

At launch, the app checks the Firebase Realtime Database node `url`. A valid `http://` or `https://` value opens the online experience and is cached locally. An empty, invalid, or unavailable value keeps the native Winner SportHub interface active.

The Firebase rules must allow read access to the `url` node while keeping client writes disabled.

## Google Play workflow

The manual `Publish to Google Play` GitHub Actions workflow builds a signed release AAB and can upload it to the internal, alpha, beta, or production track. It defaults to the internal track with draft status.

Required repository secrets:

- `PLAY_SERVICE_ACCOUNT_JSON`
- `ANDROID_SIGNING_KEY_BASE64`
- `ANDROID_KEYSTORE_PASSWORD`
- `ANDROID_KEY_ALIAS`
- `ANDROID_KEY_PASSWORD`

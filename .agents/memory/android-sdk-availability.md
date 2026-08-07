---
name: Android SDK availability
description: Environment constraint relevant when building Android Studio projects in this workspace.
---

Android Studio project generation is possible without an installed Android SDK, but Gradle cannot execute an Android compile until `ANDROID_HOME` or a valid `sdk.dir` is available.

**Why:** The workspace can provide Java and Gradle while omitting Android platform packages and `sdkmanager`.

**How to apply:** Always attempt the actual Gradle build, then report an SDK-location failure as an environment limitation rather than a source compile result.
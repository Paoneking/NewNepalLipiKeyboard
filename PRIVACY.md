# Privacy Policy — NepalLipi Keyboard

**Effective date:** 29 September 2026
**Applies to:** NepalLipi Keyboard (`com.paoneking.nepallipikeyboard`)
**Published by:** Callijatra Foundation

## Summary

NepalLipi Keyboard does not collect, transmit, or share any personal data.

The app has **no internet permission**. It is not able to send anything anywhere,
by design rather than by promise. There is no analytics, no crash reporting, no
advertising, and no third-party SDK of any kind in the app.

Everything the keyboard learns stays on your device.

## What the app stores on your device

A keyboard necessarily handles what you type. All of it stays local:

| What | Why | Where |
|---|---|---|
| Words you type and corrections you make | To improve suggestions for you | Your device's app storage |
| Your personal dictionary | Words you add yourself | Android's user dictionary |
| Clipboard history, if you enable it | To let you paste earlier items | Your device's app storage |
| Settings, themes and layouts | To remember how you set the keyboard up | Your device's app storage |

None of this is transmitted. None of it is readable by us.

You can erase it at any time: **Settings → Advanced → Backup and restore**, or by
clearing the app's data in Android settings, or by uninstalling the app. Removing
the app removes everything it stored.

## If the app crashes

When the keyboard crashes, it writes a crash report to its own storage on your
device so the fault can be diagnosed. A report contains the error and a stack
trace from the app, not what you were typing.

Nothing is sent. There is no crash reporting service in the app. The report stays
on your device unless you choose to export it yourself, which the app offers as a
file you save or send using your own apps. Clearing the app's data deletes any
stored reports.

## Permissions, and why each exists

- **Contacts (`READ_CONTACTS`)** — optional. If you grant it, names from your
  contacts are used to suggest and correctly spell those names as you type. The
  names are read on your device only, are never sent anywhere, and are not added
  to any dictionary that leaves the app. If you deny it, the keyboard works
  normally without name suggestions.
- **User dictionary (`READ_USER_DICTIONARY`, `WRITE_USER_DICTIONARY`)** — to read
  and add words in Android's shared personal dictionary, the same one other
  keyboards use.
- **Vibration (`VIBRATE`)** — key press feedback, if you turn it on.
- **Run after restart (`RECEIVE_BOOT_COMPLETED`)** — so the keyboard is ready
  when your device restarts.

The app does **not** request internet, location, camera, microphone, storage, or
phone permissions.

## Passwords and sensitive fields

The keyboard does not learn from password fields, or from fields an app marks as
not suggesting. Incognito mode, under **Settings → Advanced**, disables learning
entirely for whenever you want it off.

## Gesture typing library

Gesture typing is not included with the app. If you choose to add a library
yourself, you obtain that file from a third party and load it manually. We do not
supply, host, or control it, and this policy does not cover code you add. Only
load a library from a source you trust.

## Gesture data, if you take part

The app includes an optional feature for contributing swipe gesture recordings to
help build an open gesture typing library. It is off unless you turn it on, it
records only the gestures you agree to, you can review and delete them before
anything happens, and sending them is an explicit action you take through your own
email or file app. Nothing is uploaded automatically, and the app cannot upload
anything on its own.

## Children

The app is a keyboard with no accounts, no content feed, and no data collection.
It is suitable for all ages and collects nothing from anyone, children included.

## Changes to this policy

If this policy changes, the revised version will be published at the same address
with a new effective date. Because the app collects nothing, material changes are
unlikely.

## Contact

Questions about this policy or the app:

**paoneking@gmail.com**
Callijatra Foundation — https://callijatra.github.io/

## Verifying any of this

The app is free software under GPL-3.0-only, and its complete source is public:

https://github.com/Paoneking/NewNepalLipiKeyboard

You do not have to take the above on trust. The absence of an internet permission
is visible in `AndroidManifest.xml`, and the absence of analytics or networking
code is visible in the source.

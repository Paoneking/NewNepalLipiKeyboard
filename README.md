# Nepal Lipi Keyboard

An Android keyboard for **Nepal Bhasa (Newar)** and **Nepali**, with Romanised
transliteration into Nepal Lipi (Newa script) and Devanagari.

Type `newa` and get `𑐣𑐾𑐰𑐵`. Type `nepal` and get `नेपाल`. The keyboard stays a familiar
QWERTY layout — the script conversion happens as you type, with a live preview above the
suggestion strip.

## Features

- **Nepal Bhasa transliteration** — Roman → Nepal Lipi (Newa, Unicode U+11400–U+1147F)
- **Nepali transliteration** — Roman → Devanagari
- Traditional and romanised direct-input layouts for both languages
- Devanagari hints beneath Newa suggestions, for readers more used to Devanagari
- A transliteration bar showing `roman → script` while composing
- Per-language dictionaries with frequency-ranked suggestions, and words learned from
  your corrections
- Everything else inherited from HeliBoard: themes, custom layouts, clipboard history,
  one-handed and split modes, multilingual typing

No internet permission. Nothing leaves the device. See the
[privacy policy](https://paoneking.github.io/NewNepalLipiKeyboard/privacy).

## Default languages

A fresh install enables, in this order:

1. Nepalbhasa (Traditional) — the default keyboard
2. Nepalbhasa
3. Nepalbhasa (Transliteration)
4. Nepali
5. Nepali (Traditional)
6. Nepali (Transliteration)
7. English (US)

Switch between them with the globe key.

## Gesture typing

Swipe typing is **not** included. The gesture decoder is closed source: Google
published the hook for it but never the implementation, so the AOSP sources this
keyboard is built from contain `GestureSuggestPolicyFactory` with a null factory
method and no decoder behind it. It cannot be rebuilt, and the compiled library
cannot be redistributed, so no build of this app can ship with it.

Everything else — suggestions, autocorrect, transliteration — works without it,
from the engine built from `app/src/main/jni`.

To enable swiping, obtain the library yourself and load it once:

1. Get the gesture typing library for your phone's architecture (most modern
   phones: `arm64-v8a`). It is commonly distributed as **swypelibs**, extracted
   from GApps packages.
2. In the app: **Advanced → Load gesture typing library**, then pick the file.
   The app checks its hash before loading it.

This project does not host or distribute that library.
Loading external native code is a real security decision — only use a file from a
source you trust.

## Building

```bash
./gradlew assembleDebug
```

The APK lands in `app/build/outputs/apk/debug/`.

Gesture typing needs a closed-source library that is not bundled; it is disabled without
it. Gesture *data gathering*, which upstream uses to build an open replacement, is
switched off in this fork.

## Credits and licence

Made by [Callijatra Foundation](https://callijatra.github.io/), a youth-led initiative
working to revive the Ranjana script, Nepal Lipi and the Nepalbhasa language. Supported
by the Global Greengrants Fund. The same credit appears in the app under
Settings → About → About Callijatra.

This is a fork of [HeliBoard](https://github.com/HeliBorg/HeliBoard) by Helium314, which
is itself based on [OpenBoard](https://github.com/openboard-team/openboard) and the AOSP
keyboard. The Nepal Bhasa and Nepali transliteration work is what this fork adds.

Licensed under **GPL-3.0-only**, with Apache-2.0 and CC-BY-SA-4.0 components — see
[LICENSE](LICENSE), [LICENSE-Apache-2.0](LICENSE-Apache-2.0) and
[LICENSE-CC-BY-SA-4.0](LICENSE-CC-BY-SA-4.0).

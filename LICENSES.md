# Licences

NepalLipi Keyboard is licensed under the **GNU General Public License, version 3
only** (`GPL-3.0-only`). The full text is in [LICENSE](LICENSE).

You may use, study, share and modify it. If you distribute it, modified or not,
you must pass on the same freedoms and make the corresponding source available
to whoever receives the binary.

## Source code

The complete source for each released build, including the scripts needed to
produce it, is published at:

<https://github.com/Paoneking/NewNepalLipiKeyboard>

Development happens in a private repository; each release is published there as
one snapshot. That satisfies GPL-3 section 6(d), which requires source to be
available to anyone holding a binary — not that development happen in public.

## What this is built from

| Project | Licence | |
|---|---|---|
| [HeliBoard](https://github.com/HeliBorg/HeliBoard) | GPL-3.0-only | The keyboard this is forked from |
| [OpenBoard](https://github.com/openboard-team/openboard) | GPL-3.0-only | Which HeliBoard is itself based on |
| Android Open Source Project (LatinIME) | Apache-2.0 | The keyboard and native suggestion engine both build on |

The Nepal Bhasa and Nepali transliteration, layouts, dictionaries and the
Callijatra branding are this fork's own work.

## Bundled components

| Component | Licence |
|---|---|
| AndroidX, Jetpack Compose | Apache-2.0 |
| Material Design icons | Apache-2.0 |
| kotlinx.serialization | Apache-2.0 |
| Timber | Apache-2.0 |
| reorderable | Apache-2.0 |
| colorpicker-compose | Apache-2.0 |
| Noto Sans Newa | SIL Open Font License 1.1 |

Noto Sans Newa is not shipped in the app. The launcher icon's wordmark is drawn
from its glyph outlines.

Full texts: [LICENSE-Apache-2.0](LICENSE-Apache-2.0) and
[LICENSE-CC-BY-SA-4.0](LICENSE-CC-BY-SA-4.0).

## Native libraries

`libjni_latinime.so` — the dictionary and suggestion engine — is built from the
AOSP C++ sources in `app/src/main/jni/` and is covered by Apache-2.0. It is
buildable from this repository:

```sh
ndk-build NDK_PROJECT_PATH=null \
  APP_BUILD_SCRIPT=app/src/main/jni/Android.mk \
  NDK_APPLICATION_MK=app/src/main/jni/Application.mk \
  APP_ABI=all APP_PLATFORM=android-23 APP_OPTIM=release NDK_DEBUG=0
```

`libjni_latinimegoogle.so` — the glide typing library — is **not** open source
and is **not** part of this repository. Google published the hook for it but
never the implementation: the AOSP sources here contain only
`GestureSuggestPolicyFactory`, whose factory method is initialised to null, and
no gesture decoder. It cannot be rebuilt from source.

Because it cannot be redistributed, releases ship without it and glide typing is
not available. Upstream HeliBoard lets users supply the library themselves at
runtime; that path exists in `JniUtils` but is currently disabled in this fork.

## Dictionaries

The `.dict` files shipped in releases are **not** in this repository.

They are a separate and independent work, aggregated with the program under
GPL-3 section 5 rather than being part of it, and are not covered by the licence
above. A `.dict` is data the keyboard reads; it is not linked into the program
and any compatible keyboard could load it. Upstream is arranged the same way,
with its dictionaries in a separate repository.

The app builds and runs without them; it simply offers no suggestions until
dictionary files are present. See
[`app/src/main/assets/dicts/README.md`](app/src/main/assets/dicts/README.md).

The English and emoji dictionaries used in releases come from the Android Open
Source Project under Apache-2.0.

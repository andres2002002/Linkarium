# Third-Party Licenses

Linkarium is free and open-source software distributed under the GNU
General Public License, version 3 or later.

Linkarium uses third-party open-source software. Each third-party
component remains subject to its own license.

This document lists the third-party runtime dependencies declared
directly by Linkarium. Transitive dependencies may also be included
in the final application and remain subject to their respective
licenses.

## Apache License 2.0

The following projects used by Linkarium are distributed under the
Apache License, Version 2.0.

### AndroidX

Copyright © Android Open Source Project.

Used components include AndroidX Core, Lifecycle, Activity, Navigation,
DataStore, Room, Paging, Biometric, Foundation, and Core Splashscreen.

Project:
https://developer.android.com/jetpack/androidx

License:
https://www.apache.org/licenses/LICENSE-2.0

### Jetpack Compose

Copyright © Android Open Source Project.

Used components include Compose UI, Foundation, Material 3, Material
icons, and related Compose libraries.

Project:
https://developer.android.com/jetpack/compose

License:
https://www.apache.org/licenses/LICENSE-2.0

### Kotlin

Copyright © JetBrains s.r.o. and Kotlin Programming Language contributors.

Linkarium is built using Kotlin and uses the Kotlin standard library.

Project:
https://kotlinlang.org/

License:
https://www.apache.org/licenses/LICENSE-2.0

### Kotlin Coroutines

Kotlin Coroutines is used transitively by AndroidX and other
components.

Project:
https://github.com/Kotlin/kotlinx.coroutines

License:
https://www.apache.org/licenses/LICENSE-2.0

### Kotlinx Serialization

Copyright © JetBrains s.r.o. and contributors.

Used components:

- kotlinx-serialization-core
- kotlinx-serialization-json

Project:
https://github.com/Kotlin/kotlinx.serialization

License:
https://www.apache.org/licenses/LICENSE-2.0

### Dagger / Hilt

Copyright © The Dagger Authors.

Used components:

- Dagger Hilt
- Hilt Android
- Hilt ViewModel integration

Project:
https://github.com/google/dagger

License:
https://www.apache.org/licenses/LICENSE-2.0

### Gson

Copyright © Google Inc.

Gson is used for JSON serialization and deserialization.

Project:
https://github.com/google/gson

License:
https://www.apache.org/licenses/LICENSE-2.0

### Coil

Copyright © Coil Contributors.

Used components:

- Coil Compose
- Coil network integration for OkHttp

Project:
https://github.com/coil-kt/coil

License:
https://www.apache.org/licenses/LICENSE-2.0

### Timber

Copyright © Jake Wharton and contributors.

Timber is used for application logging.

Project:
https://github.com/JakeWharton/timber

License:
https://www.apache.org/licenses/LICENSE-2.0

### PDFBox-Android

Copyright © The Apache Software Foundation and contributors.
Android port maintained by Tom Roush and contributors.

PDFBox-Android is a port of Apache PDFBox for Android.

Project:
https://github.com/TomRoush/PdfBox-Android

License:
https://www.apache.org/licenses/LICENSE-2.0

Note that PDFBox itself contains additional third-party components
with their own notices and license terms. The applicable notices
distributed with PDFBox-Android remain applicable.

## MIT License

### jsoup

Copyright © Jonathan Hedley and contributors.

jsoup is used to parse HTML documents and extract webpage metadata,
including metadata used to identify thumbnail images.

Project:
https://jsoup.org/

License:
https://jsoup.org/license

jsoup is distributed under the MIT License.

The MIT License requires preservation of the applicable copyright
notice and permission notice when redistributing copies or
substantial portions of the software.

### AkariUi

Copyright © 2026 Akari / AkariUi contributors.

AkariUi is a UI component library developed by the Linkarium developer
and used by Linkarium for reusable Jetpack Compose UI components.

Project:
https://github.com/andres2002002/AkariUI

License:
MIT License

AkariUi is distributed independently under the MIT License. Its
inclusion in Linkarium does not change the license of AkariUi itself.

The applicable copyright and license notices for AkariUi are
preserved in the AkariUi project.

## Testing Dependencies

Dependencies used exclusively for testing and development are not
listed as runtime dependencies in this document.

These include, among others:

- JUnit
- JUnit Jupiter
- AndroidX Test
- Espresso
- Mockito
- Room Testing
- Compose UI Testing

They are not packaged as part of the normal Linkarium release
application runtime.

## License Compliance

The licenses of third-party components remain independent from the
license of Linkarium itself.

The inclusion of a permissively licensed dependency does not change
the license of that dependency.

Where a third-party license requires preservation of copyright
notices, license texts, attribution, or other notices, those
requirements apply to the corresponding component.

The full license texts for third-party components can be obtained
from the official project repositories or license URLs listed above.

## Linkarium License

The Linkarium source code is licensed under the:

GNU General Public License, version 3 or later.

See the `LICENSE` file in this repository for the complete license
text.

Copyright © 2026 andres2002002 / Linkarium contributors.
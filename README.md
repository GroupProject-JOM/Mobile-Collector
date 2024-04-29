# JOM Business and Manufacturing Process Management System

Presenting a mobile application to cope with the responsibilities of company collectors to manage their daily coconut collection process. This application empowers users with features such as navigation, amount editing, optional verification, date filtering, and real-time validation.

<a href="https://github.com/GroupProject-JOM/Mobile-Collector/releases/download/v1.1.0/JOM.apk" target="_blank"> <img src="https://github.com/GroupProject-JOM/Frontend-web-/blob/main/common/img/collector_qr.png" height="200px"></a>

## Features

- **Navigation (powered by Google Maps):** Obtain precise directions to estates directly from the map and optimize travel routes and reduce navigation uncertainties.
- **Amount Editing:** Edit or update collected coconut amounts with real-time supplier verification.
- **Optional Verification:** Offer OTP verification via email for added security.
- **Date Filtering:** Filter coconut collections by specific dates.
- **Real-Time Validation (powered by WebSockets):** Facilitate instant validation within the system.

## Technologies Used

- **Kotlin:** Connecting mobile frontend to Java backend and creating dynamic XML pages.
- **Google Maps API:** Integrating location services for efficient coordination.
- **WebSockets:** Enabling real-time bidirectional communication between clients and server.

## Folder Structure

- **app:** Main source code for the application.
- **build:** Generated by the Gradle build system, contains compiled code and resources.
- **java:** Java source code for the application.
- **manifests:** AndroidManifest.xml file defining metadata for the application.
- **res:** Resources for the application (images, layouts, strings).
  - **drawable:** Image resources.
  - **font:** Custom fonts.
  - **layout:** XML layouts defining screen structure.
  - **menu:** Navigation menu XML files.
  - **mipmap-anydpi-v26:** Launcher icons for Android Pie (API 28) and above.
  - **mipmap-hdpi, mipmap-mdpi, etc.:** Launcher icons optimized for different screen resolutions.
  - **values:** XML files defining app-wide settings and resources.
    - **styles.xml:** Styles for text, buttons, and layouts.
    - **colors.xml:** Color palettes used throughout the app.
    - **strings.xml:** Strings used throughout the app.

- **build.gradle:** Main build file for the application.
- **gradle.properties:** Project-specific properties for Gradle.
- **.gitignore:** Specifies files ignored by Git.
- **local.properties:** Local properties not to be committed to Git.

## License

This project is licensed under the [GNU General Public License v3.0](LICENSE).

---


<p align="center">
    <a href="https://github.com/GroupProject-JOM/Mobile-Collector/blob/main/LICENSE">
      <img alt="License: GNU" src="https://img.shields.io/badge/License-GPLv3-blue.svg">
   </a>
    <a href="https://github.com/GroupProject-JOM/Mobile-Collector">
      <img alt="Hits" src="https://hits.sh/github.com/GroupProject-JOM/Mobile-Collector.svg?label=Views"/>
    </a>
    <a href="https://github.com/GroupProject-JOM/Mobile-Collector/graphs/contributors">
      <img alt="GitHub Contributors" src="https://img.shields.io/github/contributors/GroupProject-JOM/Mobile-Collector" />
    </a>
    <a href="https://github.com/GroupProject-JOM/Mobile-Collector/issues">
      <img alt="Issues" src="https://img.shields.io/github/issues/GroupProject-JOM/Mobile-Collector?color=0088ff" />
    </a>
    <a href="https://github.com/GroupProject-JOM/Mobile-Collector/pulls">
      <img alt="GitHub pull requests" src="https://img.shields.io/github/issues-pr/GroupProject-JOM/Mobile-Collector?color=0088ff" />
    </a>
  </p>


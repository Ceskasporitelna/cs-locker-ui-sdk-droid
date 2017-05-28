# CSLockerUI

# Features
- [x] **User interface for [Locker](https://github.com/Ceskasporitelna/cs-core-sdk-droid/blob/master/docs/locker.md)** - Provide your users with secure & simple authentication interface
- [x] **Various LockTypes** - User access token can be sceured by Gesture, PIN, Fingerprint or nothing at all.
- [x] **Customizable UI flow** - Skippable status screens
- [x] **Customizable texts, colors and background** - Set various elements in the UI.

# [CHANGELOG](CHANGELOG.md)

# Requirements
- Android 4.1+
- CSCoreSDK 0.9+
- Gradle 2.8+
- Android Studio 1.5+

# LockerUI Installation

## Install
You can install LockerUISDK using the following gradle settings.

1. Check your project build.gradle file that it contains `JCenter` repository:
```gradle
    allprojects {
        repositories {
            ...
            jcenter()
            ...
        }
    }
```

2. Insert these lines into your module build.gradle file to compile LockerUISDK and CoreSDK (change x.y.z to the version you want to use):
```gradle
    dependencies {
        ...
        compile 'cz.csas:cs-core-sdk:x.y.z@aar'
        compile 'cz.csas:cs-locker-ui-sdk:x.y.z@aar'
        ...
    }
```

# Usage
After you've installed the SDK you will be able to use the module in your project.
Also CSLockerUI has dependency to CSCoreSDK, you will be able to use it as well.

**See [CoreSDK](https://github.com/Ceskasporitelna/cs-core-sdk-droid)**

## Configuration
Before using CoreSDK in your application, you need to initialize it by providing it your WebApiKey.

```
    CoreSDK.getInstance().useWebApiKey( "YourApiKey" )
```
**See [configuration guide](docs/configuration.md)** for all the available configuration options.

## Usage
**See [Usage Guide](./docs/lockerui.md)** for usage instructions.

# Contributing
Contributions are more than welcome!

Please read our [contribution guide](CONTRIBUTING.md) to learn how to contribute to this project.

# Terms and License
Please read our [terms and conditions in license](LICENSE.md)
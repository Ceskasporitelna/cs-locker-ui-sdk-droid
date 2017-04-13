#CSLockerUI

#Features
- [x] **User interface for [Locker](https://github.com/Ceskasporitelna/cs-core-sdk-droid/blob/master/docs/locker.md)** - Provide your users with secure & simple authentication interface
- [x] **Various LockTypes** - User access token can be sceured by Gesture, PIN, Fingerprint or nothing at all.
- [x] **Customizable UI flow** - Skippable status screens
- [x] **Customizable texts, colors and background** - Set various elements in the UI.

#[CHANGELOG](CHANGELOG.md)

#Requirements
- Android 4.1+
- CSCoreSDK 0.9+
- Gradle 2.8+
- Android Studio 1.5+

#LockerUI Installation
**IMPORTANT!** You need to have your SSH keys registered with the GitHub since this repository is private.

##Install
You can install CSLockerUI using the following git and gradle settings.

1. Navigate to your git configured project repository and process this command to add CSLockerUI as a submodule:
```
    git submodule add https://github.com/Ceskasporitelna/cs-locker-ui-sdk-droid.git your_lib_folder/cs-locker-ui-sdk-droid
```

2. Insert these two lines into your project settings.gradle file to include your submodules:
```gradle
    include ':core'
    project (':core').projectDir = new File(settingsDir, 'your_lib_folder/cs-locker-ui-sdk-droid/lib/cs-core-sdk-droid/core')
    include ':lockerui'
    project (':lockerui').projectDir = new File(settingsDir, 'your_lib_folder/cs-locker-ui-sdk-droid/lockerui')
```

3. Insert this line into your module build.gradle file to compile your submodules:
```gradle
    dependencies {
        ...
        compile project(':core')
        compile project(':lockerui')
        ...
    }
```

#Usage

After you've installed the SDK using git submodules you will be able to use the module in your project.
Also CSLockerUI has dependency to CSCoreSDK, you will be able to use it as well.

**See [CoreSDK](https://github.com/Ceskasporitelna/cs-core-sdk-droid)**

##Configuration
Before using CoreSDK in your application, you need to initialize it by providing it your WebApiKey.

```
    CoreSDK.getInstance().useWebApiKey( "YourApiKey" )
```
**See [configuration guide](docs/configuration.md)** for all the available configuration options.

##Usage
**See [Usage Guide](./docs/lockerui.md)** for usage instructions.


#Contributing
Contributions are more than welcome!

Please read our [contribution guide](CONTRIBUTING.md) to learn how to contribute to this project.

#Terms and License
Please read our [terms and conditions in license](LICENSE.md)
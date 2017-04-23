# Configuration
In order to use the LockerUI, you have to configure the CoreSDK and Locker first
## 1. Configure CoreSDK

Before using CoreSDK in your application, you need to initialize it by providing it your WebApiKey.

```
    CoreSDK.getInstance().useWebApiKey( "YourApiKey" )
```

For more configuration options see **[CoreSDK configuration guide](https://github.com/Ceskasporitelna/cs-core-sdk-droid/blob/master/docs/configuration.md)**

## 2. Configure Locker
You have to configure locker before using LockerUI.

You can find example of Locker configuration below:
```
    LockerConfig lockerConfig = new LockerConfig.Builder()
        .setClientId("YourClientID")
        .setClientSecret("YourClientSecret")
        .setPublicKey("YourPublicKey")
        .setRedirectUrl("yourscheme://your-host")
        .setScope("/v1/netbanking")
        .setOfflineAuthEnabled()
        .create();

    CoreSDK.getInstance()
        .useContext(Context)
        .useWebApiKey("YourApiKey")
        .useEnvironment(Environment.Sandbox)
        .useLanguage("en-US")
        .useRequestSigning("YourPrivateSigningKey")
        .useLocker(lockerConfig)
```
For more configuration options see **[Locker guide](https://github.com/Ceskasporitelna/cs-core-sdk-droid/blob/master/docs/locker.md)**


## 3. Configure LockerUI
### OAuth redireciton handling
If you decided to use LockerUI, you dont need to do any special configuration.

You are all set to use the lockerUI!


## 4. Customize LockerUI

You can customize locker by using the `LockerUI.getInstance().initialize(context, lockerUIoptions)` or either `LockerUI.getInstance().initialize(context, locker, lockerUIoptions)` method by passing `LockerUIOptions` struct in it.

### Available customizations

* `appName` - Name of the application that should be displayed on the Locker screens
* `allowedLockTypes` - Array of LockTypes that are allowed to be used by the user
* `backgroundImage` - Background image that should be used instead of the default one in the layer list.
* `customColor` - Color which should be used instead of the default one in the layer list.

### Lock types
There are several lock types that could be used to secure user access token:

* `PinLock` - User is verified by pin. Developer can set exact length that is required. It has to be between 4 and 8 digits long.
* `GestureLock` - User is verified by gesture on a grid. The length of the gesture can be set by developer. The minimal length of the gesture must be at least 4 points long.
* `NoLock` - User is not verified when the access token is retrieved from the server


Now you are all set to use the LockerUI! See the [LockerUI usage guide](lockerui.md) to learn how to use LockerUI.



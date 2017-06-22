#Using LockerUI

*Make sure that you have [configured](./configuration.md) the locker correctly, before using it.* 

Please see the documented [public API of the Locker UI](../lockerui/src/main/java/cz/csas/lockerui/LockerUI.java) for available functionality.

Check out the [demo application](https://github.com/Ceskasporitelna/csas-sdk-demo-droid) for usage demonstration.

##Before You Begin

Before using CoreSDK in your application, you need to initialize it by providing it your WebApiKey.

```
    CoreSDK.getInstance()
        .useWebApiKey("YourApiKey")
        .useEnvironment(Environment);
        
    // Initialize LockerUI
	LockerUIOptions lockerUIOptions = new LockerUIOptions.Builder().setAllowedLockTypes(lockTypes).setAppName("appName").create();
	LockerUI.getInstance().initialize(context,lockerUIOptions);
	
    // Now you are ready to obtain the LockerUI
    Locker locker = LockerUI.getInstance().getLocker();
```

##Starting Auth Flow

To start the authentication flow, execute following command on main thread:

```
LockerUI.getInstance().startAuthenticationFlow(authFlowOptions, callback);
```

Where `AuthFlowOptions` is an object used for customization of registration and unlock flow and callback is called after either (un)successful auth or cancelation of the flow.

###Auth Flow Options

To customize behavior of LockerUI auth flow, you need to create a new instance of `AuthFlowOptions` with desired settings:

```
	AuthFlowOptions authFlowOptions = new AuthFlowOptions.Builder()
		// You can set registration screen text
		.setRegistrationScreenText("registrationScreenText")
		// You can set locked screen text
		.setLockedScreenText("LockedScreenText")
		// You can decide if you want to see status screens or not
		.setSkipStatusScreen(SkipStatusScreen.ALWAYS)
		/* 
		 * You can enable the offline auth. Default value of OfflineAuthEnabled is false.
		 * Locker has to be also properly set using LockerConfig
		 */
		.setOfflineAuthEnabled()
		.create();
```

You may pass this instance to the `startAuthenticationFlow(authFlowOptions, callback)` call.

##Locking the Locker

To lock the Locker, call

```
LockerUI.getInstance().lockUser(callback);
```

##Presenting Info Screen

To display controller with information about user registration, execute following command on main thread:

```
LockerUI.getInstance().displayInfo(displayInfoOptions, callback);
```

Where  `DisplayInfoOptions` is an object used for customization of info fragment and callback is called after deletion of registration or cancellation of the flow.

###Info Screen Options

To customize behavior of LockerUI auth flow, you need to create a new instance of `DisplayInfoOptions` with desired settings:

```
	DisplayInfoOptions displayInfoOptions = new DisplayInfoOptions("unregisterPromptText");
```

You may pass this instance to the `displayInfo(displayInfoOptions, callback)` call.
You can either use the Builder for `DisplayInfoOptions`.

##Tracking Locker Status

To track changes of Locker state, you can subscribe to OnLockerStatusChangeListener using:

```
	LockerUI.getInstance().getLocker().setOnLockerStatusChangeListener(new OnLockerStatusChangeListener() {
		@Override
		public void onLockerStatusChanged(State state) {
			
		}
	});

```

To get current locker status, you can extract it from shared locker object:

```
	LockerStatus lockerStatus = LockerUI.getInstance().getLocker().getStatus();
	
	// to get locker state, you can call this
	State state = lockerStatus.getState();
	
	// to get locker offline authentization status call this
	boolean isVerifiedOffline = lockerStatus.getStatus().isVerifiedOffline();
```

##Customizing the LockerUI

To customize the visuals of LockerUI, you need to create a new instance of `LockerUIOptions` and set desired properties of the object. Afterwards, you declare to use these options:

```
	LockerUIOptions lockerUIOptions = new LockerUIOptions.Builder()
	    .setAllowedLockTypes(lockTypes)
	    .setAppName("appName")
	    .setCustomColor(Color.parseColor("#135091"))
	    .setBackgroundImage(drawable)
	    .setNavBarColor(csNavBarColor) // use CsNavBarColor for two default colors, DEFAULT/WHITE
	    .setNavBarColor(Color.parseColor("#135091")) // use custom navigation bar color
	    .setShowLogo(showLogo)
	    .create();
	LockerUI.getInstance().initialize(context,lockerUIOptions);
```

`CsNavBarColor` allows you to set two colors, `DEFAULT` color of Česká spořitelna a.s. and `WHITE` color. Custom navigation bar color is available as well.
`ShowLogo` accepts three values, `ALWAYS`, `NEVER` and `EXCEPT_REGISTRATION`, which displays logo everywhere besides WebView registration screen

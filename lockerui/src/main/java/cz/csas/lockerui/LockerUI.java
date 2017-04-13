package cz.csas.lockerui;

import android.content.Context;

import cz.csas.cscore.client.rest.CallbackBasic;
import cz.csas.cscore.client.rest.CallbackUI;
import cz.csas.cscore.locker.Locker;
import cz.csas.cscore.locker.LockerStatus;
import cz.csas.lockerui.config.AuthFlowOptions;
import cz.csas.lockerui.config.DisplayInfoOptions;
import cz.csas.lockerui.config.LockerUIOptions;

/**
 * The interface Locker ui.
 *
 * @author Jan Hauser <jan.hauser@applifting.cz>
 * @since 24 /11/15.
 */
public abstract class LockerUI {

    static final String LOCKER_UI_MODULE = "LockerUI";
    private static LockerUI sharedInstance;
    /**
     * The M locker ui manager.
     */
    static LockerUIManager mLockerUIManager;

    /**
     * Get instance locker ui. This method returns instance of LockerUI Singleton.
     *
     * @return the locker ui
     */
    public static LockerUI getInstance() {
        if (sharedInstance == null)
            sharedInstance = new LockerUIImpl();
        if (mLockerUIManager == null)
            mLockerUIManager = new LockerUIManagerImpl();
        return sharedInstance;
    }

    /**
     * Gets locker. LockerUI is available only after Locker configuration, so it is connected and
     * you can then get Locker through LockerUI after its initialization. (see methods below)
     *
     * @return the locker
     */
    public abstract Locker getLocker();

    /**
     * Initialize. First type of initialization with Context,Locker and LockerUIOptions. See LockerUIOptions
     * class to more specific comments.
     *
     * @param context         the context
     * @param locker          the locker
     * @param lockerUIOptions the locker ui options
     */
    public abstract void initialize(Context context, Locker locker, LockerUIOptions lockerUIOptions);

    /**
     * Initialize. Second Type of initialization. Locker is here brought via CoreSDK and doesnt have
     * to be specified.
     *
     * @param context         the context
     * @param lockerUIOptions the locker ui options
     */
    public abstract void initialize(Context context, LockerUIOptions lockerUIOptions);

    /**
     * Start authentication flow. This method will handle registration for unregistered users and
     * unlock for registered ones. AuthFlowOptions allows you to customize LockerUI. See AuthFlowOptions
     * class to more specific doc.
     * <p/>
     * After calling this method, LockerUI will finish as follows according to Locker State:
     * <p/>
     * State.USER_UNLOCKED - just returns LockerStatus in callback
     * State.USER_LOCKED - opens unlock screen
     * State.USER_UNREGISTERED - opens registration screen
     *
     * @param authFlowOptions the auth flow options
     * @param callback        the callback
     */
    public abstract void startAuthenticationFlow(AuthFlowOptions authFlowOptions, CallbackUI<LockerStatus> callback);

    /**
     * Change password. allows you to open change password flow immediately. You will receive LockerStatus
     * after LockerUI terminates.
     *
     * @param callback the callback
     */
    public abstract void changePassword(CallbackUI<LockerStatus> callback);

    /**
     * Display info. This method display info about your LockerStatus and allows you to either change password
     * or unregister. You can again customize UI via DisplayInfoOptions. See its doc.
     *
     * @param displayInfoOptions the display info options
     * @param callback           the callback
     */
    public abstract void displayInfo(DisplayInfoOptions displayInfoOptions, CallbackUI<LockerStatus> callback);

    /**
     * Lock user. This will lock user and finish LockerUI.
     *
     * @param callback the callback with locker status
     */
    public abstract void lockUser(CallbackBasic<LockerStatus> callback);

    /**
     * Unregister user. This will unregister user and finish LockerUI.
     *
     * @param callback the callback with locker status
     */
    public abstract void unregisterUser(CallbackBasic<LockerStatus> callback);

    /**
     * Cancel. This will finish LockerUI.
     *
     * @param callback the callback with locker status
     */
    public abstract void cancel(CallbackBasic<LockerStatus> callback);

}

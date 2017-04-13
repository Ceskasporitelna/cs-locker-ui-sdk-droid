package cz.csas.lockerui;

import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.Drawable;

import cz.csas.cscore.client.rest.CallbackUI;
import cz.csas.cscore.locker.Locker;
import cz.csas.cscore.locker.LockerRegistrationProcess;
import cz.csas.cscore.locker.LockerStatus;
import cz.csas.lockerui.config.AuthFlowOptions;
import cz.csas.lockerui.config.DisplayInfoOptions;
import cz.csas.lockerui.config.LockerUIOptions;
import cz.csas.lockerui.config.ShowLogo;

/**
 * The interface Locker ui manager.
 *
 * @author Jan Hauser <jan.hauser@applifting.cz>
 * @since 28 /11/15.
 */
public interface LockerUIManager {

    /**
     * Sets activity.
     *
     * @param activity the activity
     */
    public void setCurrentLockerUIActivity(Activity activity);

    /**
     * Gets activity.
     *
     * @return the activity
     */
    public Activity getCurrentLockerUIActivity();

    /**
     * Clear current locker ui activity.
     */
    public void clearCurrentLockerUIActivity();

    /**
     * Sets locker ui options.
     *
     * @param lockerUIOptions the locker ui options
     */
    public void setLockerUIOptions(LockerUIOptions lockerUIOptions);

    /**
     * Gets locker ui options.
     *
     * @return the locker ui options
     */
    public LockerUIOptions getLockerUIOptions();

    /**
     * Sets auth flow options.
     *
     * @param authFlowOptions the auth flow options
     */
    public void setAuthFlowOptions(AuthFlowOptions authFlowOptions);

    /**
     * Gets auth flow options.
     *
     * @return the auth flow options
     */
    public AuthFlowOptions getAuthFlowOptions();

    /**
     * Sets display info options.
     *
     * @param displayInfoOptions the display info options
     */
    public void setDisplayInfoOptions(DisplayInfoOptions displayInfoOptions);

    /**
     * Gets display info options.
     *
     * @return the display info options
     */
    public DisplayInfoOptions getDisplayInfoOptions();

    /**
     * Sets locker.
     *
     * @param locker the locker
     */
    public void setLocker(Locker locker);

    /**
     * Gets locker.
     *
     * @return the locker
     */
    public Locker getLocker();

    /**
     * Sets locker registration process.
     *
     * @param lockerRegistrationProcess the locker registration process
     */
    public void setLockerRegistrationProcess(LockerRegistrationProcess lockerRegistrationProcess);

    /**
     * Gets locker registration process.
     *
     * @return the locker registration process
     */
    public LockerRegistrationProcess getLockerRegistrationProcess();

    /**
     * Sets register or unlock callback.
     *
     * @param lockerUICallback the locker ui callback
     */
    public void setLockerUICallback(CallbackUI<LockerStatus> lockerUICallback);

    /**
     * Gets register or unlock callback.
     *
     * @return the register or unlock callback
     */
    public CallbackUI<LockerStatus> getLockerUICallback();


    /**
     * Sets remaining attempts.
     *
     * @param attempts the attempts
     */
    public void setUnlockRemainingAttempts(Integer attempts);

    /**
     * Gets remaining attempts.
     *
     * @return the remaining attempts
     */
    public Integer getUnlockRemainingAttempts();

    /**
     * Sets changing password.
     *
     * @param changingPassword the changing password
     */
    public void setChangingPassword(boolean changingPassword);

    /**
     * Is changing password boolean.
     *
     * @return the boolean
     */
    public boolean isChangingPassword();

    /**
     * Sets password.
     *
     * @param password the password
     */
    void setPassword(String password);

    /**
     * Gets password.
     *
     * @return the password
     */
    String getPassword();

    /**
     * Has custom color boolean.
     *
     * @return the boolean
     */
    boolean hasCustomColor();

    /**
     * Has background drawable boolean.
     *
     * @return the boolean
     */
    boolean hasBackgroundDrawable();

    /**
     * Gets custom color.
     *
     * @return the custom color
     */
    Integer getMainColor();

    /**
     * Gets light custom color.
     *
     * @return the light custom color
     */
    Integer getLightColor();

    /**
     * Gets dark custom color.
     *
     * @return the dark custom color
     */
    Integer getDarkColor();

    /**
     * Gets background drawable.
     *
     * @return the background drawable
     */
    Drawable getBackgroundDrawable();

    /**
     * Gets show logo.
     *
     * @return the show logo
     */
    ShowLogo getShowLogo();

    /**
     * Set context to load shared preferences
     *
     * @param context of LockerUI
     */
    void setPreferences(Context context);

    /**
     * Set waiting for result
     *
     * @param waitingForResult value
     */
    void setWaitingForResult(boolean waitingForResult);

    /**
     * Is waiting for result
     *
     * @return boolean is waiting for result
     */
    boolean isWaitingForResult();

    /**
     * Save switched preferences value
     *
     * @param switchedPreference value
     */
    void saveSwitchedPreference(boolean switchedPreference);

    /**
     * Get the switched preferences
     *
     * @return boolean switched preferences
     */
    boolean getSwitchedPreference();
}

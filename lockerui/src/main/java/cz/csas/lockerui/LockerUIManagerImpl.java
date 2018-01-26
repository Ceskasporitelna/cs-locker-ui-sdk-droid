package cz.csas.lockerui;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;

import cz.csas.cscore.client.rest.CallbackBasic;
import cz.csas.cscore.locker.Locker;
import cz.csas.cscore.locker.LockerRegistrationProcess;
import cz.csas.cscore.locker.LockerStatus;
import cz.csas.lockerui.components.CallbackUI;
import cz.csas.lockerui.config.AuthFlowOptions;
import cz.csas.lockerui.config.DisplayInfoOptions;
import cz.csas.lockerui.config.LockerUIOptions;
import cz.csas.lockerui.config.ShowLogo;
import cz.csas.lockerui.config.SkipStatusScreen;
import cz.csas.lockerui.utils.ColorUtils;

/**
 * The type Locker ui manager.
 *
 * @author Jan Hauser <jan.hauser@applifting.cz>
 * @since 28 /11/15.
 */
class LockerUIManagerImpl implements LockerUIManager {

    // LockerUI Settings
    private static final String WAITING_FOR_RESULT_KEY = "waiting_for_result";
    private static final String SWITCHED_KEY = "switched";
    private final String LOCKER_UI_PREFERENCES = "locker_ui_preferences";
    private SharedPreferences mLockerUISharedPreferences;
    private SharedPreferences.Editor mLockerUIEditor;
    private final float[] DEFAULT_COLOR_HSL = new float[]{211, .76f, .32f};
    private Activity mActivity;
    private LockerUIOptions mLockerUIOptions;
    private AuthFlowOptions mAuthFlowOptions;
    private DisplayInfoOptions mDisplayInfoOptions;
    private Locker mLocker;
    private LockerRegistrationProcess mLockerRegistrationProcess;
    private CallbackUI<LockerStatus> mLockerUICallback;
    private CallbackBasic<LockerStatus> mLockerMigrationCallback;
    private Integer mUnlockRemainingAttempts;
    private boolean mChangingPassword;
    private String mPassword;

    public LockerUIManagerImpl() {
        // default auth flow options
        mAuthFlowOptions = new AuthFlowOptions.Builder()
                .setLockedScreenText("")
                .setRegistrationScreenText("")
                .setSkipStatusScreen(SkipStatusScreen.ALWAYS)
                .create();
    }

    @Override
    public void setCurrentLockerUIActivity(Activity activity) {
        mActivity = activity;
    }

    @Override
    public Activity getCurrentLockerUIActivity() {
        return mActivity;
    }

    @Override
    public void clearCurrentLockerUIActivity() {
        mActivity = null;
    }

    @Override
    public void setLockerUIOptions(LockerUIOptions lockerUIOptions) {
        mLockerUIOptions = lockerUIOptions;
    }

    @Override
    public LockerUIOptions getLockerUIOptions() {
        return mLockerUIOptions;
    }

    @Override
    public void setAuthFlowOptions(AuthFlowOptions authFlowOptions) {
        if (authFlowOptions != null)
            mAuthFlowOptions = authFlowOptions;
    }

    @Override
    public AuthFlowOptions getAuthFlowOptions() {
        return mAuthFlowOptions;
    }

    @Override
    public void setDisplayInfoOptions(DisplayInfoOptions displayInfoOptions) {
        mDisplayInfoOptions = displayInfoOptions;
    }

    @Override
    public DisplayInfoOptions getDisplayInfoOptions() {
        return mDisplayInfoOptions;
    }

    @Override
    public void setLocker(Locker locker) {
        mLocker = locker;
    }

    @Override
    public Locker getLocker() {
        return mLocker;
    }

    @Override
    public void setLockerRegistrationProcess(LockerRegistrationProcess lockerRegistrationProcess) {
        mLockerRegistrationProcess = lockerRegistrationProcess;
    }

    @Override
    public LockerRegistrationProcess getLockerRegistrationProcess() {
        return mLockerRegistrationProcess;
    }

    @Override
    public void setLockerUICallback(CallbackUI<LockerStatus> registerOrUnlockCallback) {
        mLockerUICallback = registerOrUnlockCallback;
    }

    @Override
    public CallbackUI<LockerStatus> getLockerUICallback() {
        return mLockerUICallback;
    }

    public CallbackBasic<LockerStatus> getLockerMigrationCallback() {
        return mLockerMigrationCallback;
    }

    public void setLockerMigrationCallback(CallbackBasic<LockerStatus> mLockerMigrationCallback) {
        this.mLockerMigrationCallback = mLockerMigrationCallback;
    }

    @Override
    public void setUnlockRemainingAttempts(Integer attempts) {
        mUnlockRemainingAttempts = attempts;
    }

    @Override
    public Integer getUnlockRemainingAttempts() {
        return mUnlockRemainingAttempts;
    }

    @Override
    public void setChangingPassword(boolean changingPassword) {
        mChangingPassword = changingPassword;
    }

    @Override
    public boolean isChangingPassword() {
        return mChangingPassword;
    }

    @Override
    public void setPassword(String password) {
        mPassword = password;
    }

    @Override
    public String getPassword() {
        return mPassword;
    }

    @Override
    public boolean hasCustomColor() {
        return mLockerUIOptions.getCustomColor() != null;
    }

    @Override
    public boolean hasBackgroundDrawable() {
        return mLockerUIOptions.getBackgrounImage() != null;
    }

    @Override
    public Integer getMainColor() {
        if (hasCustomColor())
            return mLockerUIOptions.getCustomColor();
        return android.support.v4.graphics.ColorUtils.HSLToColor(DEFAULT_COLOR_HSL);
    }

    @Override
    public Integer getLightColor() {
        return android.support.v4.graphics.ColorUtils.HSLToColor(ColorUtils.transformHslColor(getColorHsl(), 0, 0, .48f));
    }

    @Override
    public Integer getDarkColor() {
        return android.support.v4.graphics.ColorUtils.HSLToColor(ColorUtils.transformHslColor(getColorHsl(), 0, 0, -.12f));
    }

    @Override
    public Drawable getBackgroundDrawable() {
        return hasBackgroundDrawable() ? mLockerUIOptions.getBackgrounImage() : null;
    }

    @Override
    public ShowLogo getShowLogo() {
        return mLockerUIOptions.getShowLogo();
    }

    @Override
    public void setPreferences(Context context) {
        mLockerUISharedPreferences = context.getSharedPreferences(LOCKER_UI_PREFERENCES, Context.MODE_PRIVATE);
        mLockerUIEditor = mLockerUISharedPreferences.edit();
    }

    @Override
    public void setWaitingForResult(boolean waitingForResult) {
        mLockerUIEditor.putBoolean(WAITING_FOR_RESULT_KEY, waitingForResult);
        mLockerUIEditor.commit();
    }

    @Override
    public boolean isWaitingForResult() {
        return mLockerUISharedPreferences.getBoolean(WAITING_FOR_RESULT_KEY, false);
    }

    @Override
    public void saveSwitchedPreference(boolean switchedPreference) {
        mLockerUIEditor.putBoolean(SWITCHED_KEY, switchedPreference);
        mLockerUIEditor.commit();
    }

    @Override
    public boolean getSwitchedPreference() {
        return mLockerUISharedPreferences.getBoolean(SWITCHED_KEY, false);
    }

    private float[] getColorHsl() {
        if (hasCustomColor()) {
            float[] hsl = new float[3];
            android.support.v4.graphics.ColorUtils.colorToHSL(getMainColor(), hsl);
            return hsl;
        }
        return DEFAULT_COLOR_HSL;
    }

}

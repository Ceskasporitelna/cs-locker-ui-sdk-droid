package cz.csas.lockerui;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;

import cz.csas.cscore.CoreSDK;
import cz.csas.cscore.client.rest.CallbackBasic;
import cz.csas.cscore.client.rest.CallbackUI;
import cz.csas.cscore.locker.Locker;
import cz.csas.cscore.locker.LockerStatus;
import cz.csas.cscore.locker.State;
import cz.csas.cscore.logger.LogLevel;
import cz.csas.cscore.logger.LogManager;
import cz.csas.cscore.utils.StringUtils;
import cz.csas.cscore.utils.csjson.CsJson;
import cz.csas.lockerui.config.AuthFlowOptions;
import cz.csas.lockerui.config.DisplayInfoOptions;
import cz.csas.lockerui.config.LockerUIOptions;
import cz.csas.lockerui.error.CsLockerUIError;

/**
 * The type Locker ui.
 *
 * @author Jan Hauser <jan.hauser@applifting.cz>
 * @since 24 /11/15.
 */
class LockerUIImpl extends LockerUI {

    private Context mContext;
    private LogManager mLogManager;
    private CsJson mCsJson;

    @Override
    public Locker getLocker() {
        return mLockerUIManager.getLocker();
    }

    @Override
    public void initialize(Context context, Locker locker, LockerUIOptions lockerUIOptions) {
        if (context == null)
            throw new CsLockerUIError(CsLockerUIError.Kind.BAD_CONTEXT);
        if (locker == null)
            throw new CsLockerUIError(CsLockerUIError.Kind.BAD_LOCKER);
        if (lockerUIOptions == null)
            throw new CsLockerUIError(CsLockerUIError.Kind.BAD_LOCKER_UI);
        mLockerUIManager.setLocker(locker);
        mLockerUIManager.setLockerUIOptions(lockerUIOptions);
        mLockerUIManager.setPreferences(context);
        mContext = context;
        mLogManager = CoreSDK.getInstance().getLogger();
        mCsJson = new CsJson();
    }

    @Override
    public void initialize(Context context, LockerUIOptions lockerUIOptions) {
        Locker locker = CoreSDK.getInstance().getLocker();
        initialize(context, locker, lockerUIOptions);
    }

    @Override
    public void startAuthenticationFlow(AuthFlowOptions authFlowOptions, CallbackUI<LockerStatus> callback) {
        mLockerUIManager.setAuthFlowOptions(authFlowOptions);
        mLockerUIManager.setLockerUICallback(callback);
        mLockerUIManager.setWaitingForResult(false);
        Intent intent = new Intent(mContext, MainActivity.class);
        intent.putExtra(Constants.AUTH_FLOW_EXTRA, true);
        mContext.startActivity(intent);
        mLogManager.log(StringUtils.logLine(LOCKER_UI_MODULE, "AuthFlowStarted", "Start authentication flow with the following options:" + mCsJson.toJson(authFlowOptions)), LogLevel.DEBUG);
    }

    @Override
    public void changePassword(CallbackUI<LockerStatus> callback) {
        mLockerUIManager.setChangingPassword(true);
        mLockerUIManager.setLockerUICallback(callback);
        Intent intent = new Intent(mContext, MainActivity.class);
        intent.putExtra(Constants.CHANGE_PWD_EXTRA, true);
        mContext.startActivity(intent);
        mLogManager.log(StringUtils.logLine(LOCKER_UI_MODULE, "ChangeSecurityFlowStarted", "Start change password flow."), LogLevel.DEBUG);
    }

    @Override
    public void displayInfo(DisplayInfoOptions displayInfoOptions, CallbackUI<LockerStatus> callback) {
        mLockerUIManager.setDisplayInfoOptions(displayInfoOptions);
        mLockerUIManager.setLockerUICallback(callback);
        Intent intent = new Intent(mContext, MainActivity.class);
        intent.putExtra(Constants.DISPLAY_INFO_EXTRA, true);
        mContext.startActivity(intent);
        mLogManager.log(StringUtils.logLine(LOCKER_UI_MODULE, "DisplayInfoFlowStarted", "Start display info flow with the following options: " + mCsJson.toJson(displayInfoOptions)), LogLevel.DEBUG);
    }

    @Override
    public void lockUser(final CallbackBasic<LockerStatus> callback) {
        State state = getLocker().getStatus().getState();
        if (state == State.USER_UNLOCKED) {
            getLocker().lock(new CallbackBasic<LockerStatus>() {
                @Override
                public void success(LockerStatus lockerStatus) {
                    callback.success(lockerStatus);
                }

                @Override
                public void failure() {
                    callback.failure();
                }
            });
        } else
            callback.success(getLocker().getStatus());
        finishLockerUI(mLockerUIManager.getCurrentLockerUIActivity());
    }

    @Override
    public void unregisterUser(final CallbackBasic<LockerStatus> callback) {
        State state = getLocker().getStatus().getState();
        if (state != State.USER_UNREGISTERED) {
            getLocker().unregister(new CallbackBasic<LockerStatus>() {
                @Override
                public void success(LockerStatus lockerStatus) {
                    callback.success(lockerStatus);
                }

                @Override
                public void failure() {
                    callback.failure();
                }
            });
        } else
            callback.success(getLocker().getStatus());
        finishLockerUI(mLockerUIManager.getCurrentLockerUIActivity());
    }

    @Override
    public void cancel(CallbackBasic<LockerStatus> callback) {
        finishLockerUI(mLockerUIManager.getCurrentLockerUIActivity());
        callback.success(getLocker().getStatus());
        if (mLockerUIManager.isChangingPassword())
            mLogManager.log(StringUtils.logLine(LOCKER_UI_MODULE, "ChangeSecurityFlowFinished", "Change security flow was canceled."), LogLevel.DEBUG);
        else
            mLogManager.log(StringUtils.logLine(LOCKER_UI_MODULE, "AuthFlowFinished", "Authentication flow was canceled."), LogLevel.DEBUG);
    }

    /**
     * Get locker ui manager locker ui manager.
     *
     * @return the locker ui manager
     */
    LockerUIManager getLockerUIManager() {
        return mLockerUIManager;
    }

    private void finishLockerUI(Activity activity) {
        if (activity != null)
            activity.finish();
        getLocker().cancelOAuthLoginActivity();
    }

}

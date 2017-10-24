package cz.csas.lockerui;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;

import cz.csas.cscore.CoreSDK;
import cz.csas.cscore.client.rest.CallbackBasic;
import cz.csas.cscore.client.rest.CsCallback;
import cz.csas.cscore.client.rest.client.Response;
import cz.csas.cscore.error.CsSDKError;
import cz.csas.cscore.locker.LockType;
import cz.csas.cscore.locker.Locker;
import cz.csas.cscore.locker.LockerMigrationData;
import cz.csas.cscore.locker.LockerStatus;
import cz.csas.cscore.locker.Password;
import cz.csas.cscore.locker.PasswordMigrationProcess;
import cz.csas.cscore.locker.RegistrationOrUnlockResponse;
import cz.csas.cscore.locker.State;
import cz.csas.cscore.logger.LogLevel;
import cz.csas.cscore.logger.LogManager;
import cz.csas.cscore.utils.StringUtils;
import cz.csas.cscore.utils.csjson.CsJson;
import cz.csas.lockerui.components.CallbackUI;
import cz.csas.lockerui.config.AuthFlowOptions;
import cz.csas.lockerui.config.DisplayInfoOptions;
import cz.csas.lockerui.config.LockerUIOptions;
import cz.csas.lockerui.config.MigrationFlowOptions;
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
    public void startMigrationFlow(MigrationFlowOptions options, final CallbackUI<LockerStatus> callback) {
        if (validateMigrationFlowOptions(options)) {
            final LockerMigrationData data = new LockerMigrationData.Builder()
                    .setClientId(options.getClientId())
                    .setDeviceFingerprint(options.getDeviceFingerprint())
                    .setOneTimePasswordKey(options.getOneTimePasswordKey())
                    .setRefreshToken(options.getRefreshToken())
                    .setEncryptionKey(options.getEncryptionKey())
                    .create();
            final Password password = options.getPassword();
            final PasswordMigrationProcess passwordMigrationProcess = options.getPasswordMigrationProcess();

            if (password.getLockType() == LockType.FINGERPRINT) {
                // initiate UI
                mLockerUIManager.setWaitingForResult(false);
                mLockerUIManager.setLockerMigrationCallback(new CallbackBasic<LockerStatus>() {
                    @Override
                    public void success(LockerStatus lockerStatus) {
                        callMigrationUnlock(password, passwordMigrationProcess, data, callback);
                    }

                    @Override
                    public void failure() {
                        callback.failure(new CsLockerUIError(CsLockerUIError.Kind.MIGRATION_REJECTED));
                    }
                });
                Intent intent = new Intent(mContext, MainActivity.class);
                intent.putExtra(Constants.MIGRATION_EXTRA, true);
                intent.putExtra(Constants.MIGRATION_FINGERPRINT_EXTRA, password.getPassword());
                mContext.startActivity(intent);
            } else {
                callMigrationUnlock(password, passwordMigrationProcess, data, callback);
            }
        } else {
            callback.failure(new CsLockerUIError(CsLockerUIError.Kind.BAD_MIGRATION_DATA));
        }
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
        if (getLocker().getStatus().getState() == State.USER_UNLOCKED || getLocker().getStatus().isVerifiedOffline()) {
            getLocker().lock(new CsCallback<LockerStatus>() {
                @Override
                public void success(LockerStatus lockerStatus, Response response) {
                    callback.success(lockerStatus);
                }

                @Override
                public void failure(CsSDKError error) {
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
            getLocker().unregister(new CsCallback<LockerStatus>() {
                @Override
                public void success(LockerStatus lockerStatus, Response response) {
                    callback.success(lockerStatus);
                }

                @Override
                public void failure(CsSDKError error) {
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

    private void callMigrationUnlock(Password password, PasswordMigrationProcess passwordMigrationProcess, LockerMigrationData data, final CallbackUI<LockerStatus> callback) {
        getLocker().unlockAfterMigration(password, passwordMigrationProcess, data, new CsCallback<RegistrationOrUnlockResponse>() {
            @Override
            public void success(RegistrationOrUnlockResponse registrationOrUnlockResponse, Response response) {
                callback.success(LockerUI.getInstance().getLocker().getStatus());
            }

            @Override
            public void failure(CsSDKError error) {
                callback.failure(error);
            }
        });
    }

    private boolean validateMigrationFlowOptions(MigrationFlowOptions options) {
        // password hash process is optional, set tu dummy process if not provided
        if (options.getPasswordMigrationProcess() == null)
            options.setPasswordMigrationProcess(new PasswordMigrationProcess() {
                @Override
                public String hashPassword(String password) {
                    return password;
                }

                @Override
                public String transformPassword(String oldPassword) {
                    return oldPassword;
                }
            });
        Password password = options.getPassword();
        return password != null &&
                password.getLockType() != null &&
                password.getPassword() != null &&
                // for PIN and GESTURE check password space size value
                (!(password.getLockType() == LockType.PIN || password.getLockType() == LockType.GESTURE) || password.getPasswordSpaceSize() != null) &&
                options.getClientId() != null &&
                options.getDeviceFingerprint() != null &&
                options.getOneTimePasswordKey() != null &&
                options.getEncryptionKey() != null &&
                options.getRefreshToken() != null;
    }
}

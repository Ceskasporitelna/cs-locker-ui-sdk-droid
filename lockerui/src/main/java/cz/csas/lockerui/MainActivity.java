package cz.csas.lockerui;

import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.FrameLayout;

import csas.cz.lockerui.R;
import cz.csas.cscore.CoreSDK;
import cz.csas.cscore.client.rest.CallbackUI;
import cz.csas.cscore.client.rest.CsCallback;
import cz.csas.cscore.client.rest.CsRestError;
import cz.csas.cscore.client.rest.client.Response;
import cz.csas.cscore.error.CsSDKError;
import cz.csas.cscore.locker.CsNavBarColor;
import cz.csas.cscore.locker.LockerStatus;
import cz.csas.cscore.locker.OAuthLoginActivityOptions;
import cz.csas.cscore.locker.PasswordResponse;
import cz.csas.cscore.locker.RegistrationOrUnlockResponse;
import cz.csas.cscore.locker.State;
import cz.csas.cscore.logger.LogLevel;
import cz.csas.cscore.logger.LogManager;
import cz.csas.cscore.utils.StringUtils;
import cz.csas.cscore.utils.csjson.CsJson;
import cz.csas.lockerui.config.LockType;
import cz.csas.lockerui.config.LockerUIOptions;
import cz.csas.lockerui.config.ShowLogo;
import cz.csas.lockerui.config.SkipStatusScreen;
import cz.csas.lockerui.utils.ColorUtils;

import static cz.csas.lockerui.LockerUI.LOCKER_UI_MODULE;
import static cz.csas.lockerui.config.SkipStatusScreen.ALWAYS;
import static cz.csas.lockerui.utils.ColorUtils.setBackground;


/**
 * The type Main activity.
 *
 * @author Jan Hauser <jan.hauser@applifting.cz>
 * @since 01 /12/15.
 */
public class MainActivity extends AppCompatActivity implements FragmentCallback {

    private static LogManager mLogManager;
    private FrameLayout mFlContainer;
    private Toolbar mToolbar;
    private LockerUIOptions mLockerUIOptions;
    private static CsJson mCsJson = new CsJson();
    private static LockerUIManagerImpl mLockerUIManagerImpl;
    private cz.csas.cscore.locker.LockType mLockType;
    private static MainActivity sActivity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_activity);
        hideActionBar();
        init();

        /**
         * https://github.com/Ceskasporiteln/cs-core-sdk-droid/issues/70
         * I commented this because of do not keep activities settings.
         */
        //if (sActivity != null)
        //    sActivity.finish();
        sActivity = this;
        mLogManager = CoreSDK.getInstance().getLogger();

        final cz.csas.cscore.locker.LockType lockType = LockerUI.getInstance().getLocker().getStatus().getLockType();
        mLockerUIManagerImpl = (LockerUIManagerImpl) ((LockerUIImpl) LockerUI.getInstance()).getLockerUIManager();
        mLockerUIManagerImpl.setCurrentLockerUIActivity(this);
        mLockerUIOptions = mLockerUIManagerImpl.getLockerUIOptions();
        mLockerUIManagerImpl.setUnlockRemainingAttempts(null);

        boolean forceAuthFlow = lockType != null && !unregisterIfLockTypeNotValid(lockType);

        LockerStatus status = LockerUI.getInstance().getLocker().getStatus();
        mLockType = status.getLockType();
        setCustomizedBackground();
        setNavBar();

        /**
         * DON'T KEEP ACTIVITIES ISSUE
         * Problem with handling the "Don't keep activities" scenario. New flag that keeps info
         * about initiation of OAuth2.
         *
         * The problem was that in this situation onCreate() method was called before
         * onActivityResult() and it created the inifinite loop of opening OAuth2 webview.
         *
         */
        boolean isUnregistered = status.getState() == State.USER_UNREGISTERED;
        if (!isUnregistered || !mLockerUIManagerImpl.isWaitingForResult()) {
            if (isUnregistered) {
                mLockerUIManagerImpl.setWaitingForResult(true);
            }
            if (getIntent().hasExtra(Constants.AUTH_FLOW_EXTRA) || forceAuthFlow) {
                LockerUIFragment lockerUIFragment = new LockerUIFragment();
                changeFragment(lockerUIFragment, Constants.FRAGMENT_LOCKERUI);
            } else if (getIntent().hasExtra(Constants.DISPLAY_INFO_EXTRA)) {
                InfoFragment infoFragment = new InfoFragment();
                changeFragment(infoFragment, Constants.FRAGMENT_INFO);
            } else if (getIntent().hasExtra(Constants.CHANGE_PWD_EXTRA)) {
                if (lockType == cz.csas.cscore.locker.LockType.PIN) {
                    PinFragment pinFragment = new PinFragment();
                    changeFragment(pinFragment, Constants.FRAGMENT_PIN);
                } else if (lockType == cz.csas.cscore.locker.LockType.GESTURE) {
                    GestureFragment gestureFragment = new GestureFragment();
                    changeFragment(gestureFragment, Constants.FRAGMENT_GESTURE);
                } else if (lockType == cz.csas.cscore.locker.LockType.NONE) {
                    RegisterFragment registerFragment = new RegisterFragment();
                    changeFragment(registerFragment, Constants.FRAGMENT_REGISTER);
                } else if (lockType == cz.csas.cscore.locker.LockType.FINGERPRINT) {
                    FingerprintFragment fingerprintFragment = new FingerprintFragment();
                    changeFragment(fingerprintFragment, Constants.FRAGMENT_FINGERPRINT);
                }
            }
        }
        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private void init() {
        mFlContainer = (FrameLayout) findViewById(R.id.fragment_container_main_activity);
        mToolbar = (Toolbar) findViewById(R.id.toolbar_main_activity);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == Constants.OAUTH_REQUEST_CODE) {
            if (resultCode == RESULT_CANCELED && checkSkipStatusScreenCustomization())
                finish();
            else if (resultCode == RESULT_CANCELED || !CoreSDK.getInstance().getLocker().processUrl(data.getStringExtra(cz.csas.cscore.locker.Constants.CODE_EXTRA)))
                changeFragmentToLockerUI();
            else {
                SkipStatusScreen skipStatusScreen = mLockerUIManagerImpl.getAuthFlowOptions().getSkipStatusScreen();
                if (skipStatusScreen != SkipStatusScreen.WHEN_NOT_REGISTERED &&
                        skipStatusScreen != ALWAYS)
                    changeFragmentToLockerUI();
                changeFragmentToRegister();
            }
        }
        // Reset the OAuth2 initiation flag.
        mLockerUIManagerImpl.setWaitingForResult(false);
    }

    @Override
    protected void onResume() {
        super.onResume();
        sActivity = this;
    }

    @Override
    public void changeFragmentToLockerUI() {
        changeFragment(new LockerUIFragment(), Constants.FRAGMENT_LOCKERUI);
    }

    @Override
    public void changeFragmentToRegister() {
        changeFragment(new RegisterFragment(), Constants.FRAGMENT_REGISTER);
    }

    @Override
    public void changeFragmentToPin() {
        changeFragment(new PinFragment(), Constants.FRAGMENT_PIN);
    }

    @Override
    public void changeFragmentToFingerprint() {
        changeFragment(new FingerprintFragment(), Constants.FRAGMENT_FINGERPRINT);
    }

    @Override
    public void changeFragmentToGesture() {
        changeFragment(new GestureFragment(), Constants.FRAGMENT_GESTURE);
    }

    @Override
    public void changeFragmentToResult() {
        changeFragment(new ResultFragment(), Constants.FRAGMENT_RESULT);
    }

    @Override
    public void changeFragmentToResult(CsRestError.Kind kind) {
        ResultFragment resultFragment = (ResultFragment) getFragmentManager().findFragmentByTag(Constants.FRAGMENT_RESULT);
        if (mLockerUIManagerImpl.getCurrentLockerUIActivity() != null && checkFragmentVisibility(resultFragment)) {
            resultFragment = new ResultFragment();
            Bundle bundle = new Bundle();
            bundle.putString(Constants.ERROR_KIND_EXTRA, kind.toString());
            resultFragment.setArguments(bundle);
            changeFragment(resultFragment, Constants.FRAGMENT_RESULT);
        }
    }

    @Override
    public void changeFragmentToDisplayInfo() {
        changeFragment(new InfoFragment(), Constants.FRAGMENT_INFO);
        mLogManager.log(StringUtils.logLine(LOCKER_UI_MODULE, "DisplayInfoFlowStarted", "Start display info flow with the following options: " + mCsJson.toJson(mLockerUIManagerImpl.getDisplayInfoOptions())), LogLevel.DEBUG);
    }

    @Override
    public void clearFragment() {
        FragmentTransaction transaction = getFragmentManager().beginTransaction();
        LockerUIFragment lockerUIFragment = (LockerUIFragment) getFragmentManager().findFragmentByTag(Constants.FRAGMENT_LOCKERUI);
        ResultFragment resultFragment = (ResultFragment) getFragmentManager().findFragmentByTag(Constants.FRAGMENT_RESULT);
        FingerprintFragment fingerprintFragment = (FingerprintFragment) getFragmentManager().findFragmentByTag(Constants.FRAGMENT_FINGERPRINT);
        if (lockerUIFragment != null)
            transaction.remove(lockerUIFragment);
        if (resultFragment != null)
            transaction.remove(resultFragment);
        if (fingerprintFragment != null)
            transaction.remove(fingerprintFragment);
        // allow state loss due to several number of crashes, see http://www.androiddesignpatterns.com/2013/08/fragment-transaction-commit-state-loss.html
        transaction.commitAllowingStateLoss();
        transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_CLOSE);
    }


    private void changeFragment(Fragment fragment, String tag) {
        setNavigationIcon(fragment);
        FragmentTransaction transaction = getFragmentManager().beginTransaction();
        transaction.setCustomAnimations(R.animator.fade_in, R.animator.fade_out);
        transaction.replace(R.id.fragment_container_main_activity, fragment, tag);
        transaction.addToBackStack(tag);
        // allow state loss due to several number of crashes, see http://www.androiddesignpatterns.com/2013/08/fragment-transaction-commit-state-loss.html
        transaction.commitAllowingStateLoss();
    }

    /**
     * On unlock success.
     *
     * @param registrationOrUnlockResponse the registration or unlock response
     */
    static void onUnlockSuccess(RegistrationOrUnlockResponse registrationOrUnlockResponse) {
        if (registrationOrUnlockResponse.getRemainingAttempts() == null) {
            mLogManager.log(StringUtils.logLine(LOCKER_UI_MODULE, "AuthFlowFinished", "Unlock finished successfully."), LogLevel.DEBUG);
            sActivity.finish();
        } else {
            ((LockerUIImpl) LockerUI.getInstance()).getLockerUIManager().setUnlockRemainingAttempts(Integer.valueOf(registrationOrUnlockResponse.getRemainingAttempts()));
            if (sActivity.mLockType == cz.csas.cscore.locker.LockType.GESTURE)
                sActivity.changeFragmentToGesture();
            else if (sActivity.mLockType == cz.csas.cscore.locker.LockType.PIN)
                sActivity.changeFragmentToPin();
            else if (sActivity.mLockType == cz.csas.cscore.locker.LockType.FINGERPRINT)
                sActivity.changeFragmentToFingerprint();
        }
    }

    /**
     * On unlock failed.
     */
    static void onUnlockFailed(CsSDKError error) {
        ResultFragment resultFragment = (ResultFragment) sActivity.getFragmentManager().findFragmentByTag(Constants.FRAGMENT_RESULT);
        if (resultFragment != null && resultFragment.isVisible()) {
            resultFragment.onUnlockFailed(error);
            mLogManager.log(StringUtils.logLine(LOCKER_UI_MODULE, "AuthFlowFinished", "Unlock failed."), LogLevel.DEBUG);
        }
    }

    /**
     * On registration failed.
     */
    static void onRegistrationFailed() {
        ResultFragment resultFragment = (ResultFragment) sActivity.getFragmentManager().findFragmentByTag(Constants.FRAGMENT_RESULT);
        if (resultFragment != null && resultFragment.isVisible()) {
            resultFragment.onRegistrationFailed();
            mLogManager.log(StringUtils.logLine(LOCKER_UI_MODULE, "AuthFlowFinished", "Registration failed."), LogLevel.DEBUG);
        }
    }

    /**
     * On registration success.
     */
    static void onRegistrationSuccess() {
        mLogManager.log(StringUtils.logLine(LOCKER_UI_MODULE, "AuthFlowFinished", "Registration finished successfully."), LogLevel.DEBUG);
        sActivity.finish();
    }

    /**
     * On unregistration success.
     */
    static void onUnregistrationSuccess() {
        mLogManager.log(StringUtils.logLine(LOCKER_UI_MODULE, "DisplayInfoFlowFinished", "Display info flow finished with unregistration."), LogLevel.DEBUG);
        sActivity.finish();
    }

    /**
     * On password change success.
     *
     * @param passwordResponse the password response
     */
    static void onPasswordChangeSuccess(PasswordResponse passwordResponse, cz.csas.cscore.locker.LockType lockType) {
        if (passwordResponse == null || (passwordResponse != null && passwordResponse.getRemainingAttempts() == null)) {
            mLogManager.log(StringUtils.logLine(LOCKER_UI_MODULE, "ChangeSecurityFlowFinished", "Password change finished successfully."), LogLevel.DEBUG);
            sActivity.changeFragmentToDisplayInfo();
            if (lockType != cz.csas.cscore.locker.LockType.FINGERPRINT) {
                LockerUI.getInstance().getLocker().wipeEncryptedSecret();
                LockerUI.getInstance().getLocker().wipeIvSecret();
            }
        } else {
            sActivity.changeFragmentToRegister();
        }
    }

    /**
     * On password change failure.
     */
    static void onPasswordChangeFailure() {
        mLogManager.log(StringUtils.logLine(LOCKER_UI_MODULE, "ChangeSecurityFlowFinished", "Password change failed."), LogLevel.DEBUG);
        sActivity.changeFragmentToLockerUI();
    }

    /**
     * On password check success.
     *
     * @param registrationOrUnlockResponse the registration or unlock response
     * @param lockType                     the lock type
     */
    static void onPasswordCheckSuccess(RegistrationOrUnlockResponse registrationOrUnlockResponse, cz.csas.cscore.locker.LockType lockType) {
        if (registrationOrUnlockResponse.getRemainingAttempts() == null) {
            sActivity.mLockerUIManagerImpl.setUnlockRemainingAttempts(null);
            sActivity.changeFragmentToRegister();
        } else {
            sActivity.mLockerUIManagerImpl.setUnlockRemainingAttempts(Integer.valueOf(registrationOrUnlockResponse.getRemainingAttempts()));
            sActivity.mLockerUIManagerImpl.setChangingPassword(true);
            if (lockType == cz.csas.cscore.locker.LockType.GESTURE)
                sActivity.changeFragmentToGesture();
            else if (lockType == cz.csas.cscore.locker.LockType.PIN)
                sActivity.changeFragmentToPin();
            else if (lockType == cz.csas.cscore.locker.LockType.FINGERPRINT)
                sActivity.changeFragmentToFingerprint();
        }
    }

    /**
     * On password check failure.
     */
    static void onPasswordCheckFailure() {
        ResultFragment resultFragment = (ResultFragment) sActivity.getFragmentManager().findFragmentByTag(Constants.FRAGMENT_RESULT);
        sActivity.mLockerUIManagerImpl.setUnlockRemainingAttempts(null);
        sActivity.mLockerUIManagerImpl.setChangingPassword(false);
        if (resultFragment != null && resultFragment.isVisible()) {
            resultFragment.onUnlockFailed(null);
        }
    }

    /*
     * Handling the back press is well documented cause of method complexity.
     * How it works:
     *
     * 1) check if the result fragment is on, then decide
     *      a) LockerUIFragment in backstack => go there, unlock or register operation is on
     *      b) otherwise go to InfoFragment, change password operation is on. Log change password
     *      finish and display info start.
     * 2) otherwise need to solve the backstack fallback
     *      a) InfoFragment is visible => log display info finish and finish activity
     *      b) LockerUIFragment is visible => log auth flow finish and finish activity
     *      c) otherwise the flow state is not somewhere in the beginning
     *          - pop fragment from backstack until Info,LockerUI or Register fragment is on or
     *          until the backstack is empty
     *          - I) check the DisplayInfoFragment visibility to log
     *          - II) otherwise if non of DisplayInfo,LockerUI,Register fragments is on, finish
     *            activity
     *
     * FLOW IMPROVED (works even for skipping the status screen.
     */
    @Override
    public void onBackPressed() {
        LockerUIFragment lockerUIFragment = (LockerUIFragment) getFragmentManager().findFragmentByTag(Constants.FRAGMENT_LOCKERUI);
        RegisterFragment registerFragment = (RegisterFragment) getFragmentManager().findFragmentByTag(Constants.FRAGMENT_REGISTER);
        InfoFragment infoFragment = (InfoFragment) getFragmentManager().findFragmentByTag(Constants.FRAGMENT_INFO);
        ResultFragment resultFragment = (ResultFragment) getFragmentManager().findFragmentByTag(Constants.FRAGMENT_RESULT);

        if (checkFragmentVisibility(resultFragment)) {
            if (hasLockerUIFragmentInBackStack())
                changeFragmentToLockerUI();
            else {
                mLogManager.log(StringUtils.logLine(LOCKER_UI_MODULE, "ChangeSecurityFlowFinished", "Password change failed."), LogLevel.DEBUG);
                changeFragmentToDisplayInfo();
            }
        } else {
            if (checkFragmentVisibility(infoFragment)) {
                finish();
                mLogManager.log(StringUtils.logLine(LOCKER_UI_MODULE, "DisplayInfoFlowFinished", "Display info flow was canceled."), LogLevel.DEBUG);
            } else if (checkFragmentVisibility(lockerUIFragment)) {
                finish();
                mLogManager.log(StringUtils.logLine(LOCKER_UI_MODULE, "AuthFlowFinished", "Authentication flow was canceled."), LogLevel.DEBUG);
            } else {
                do
                    getFragmentManager().popBackStackImmediate();
                while (!(checkFragmentVisibility(infoFragment)
                        || checkFragmentVisibility(registerFragment)
                        || (checkFragmentVisibility(lockerUIFragment)
                        || (lockerUIFragment != null && checkSkipStatusScreenCustomization())))
                        && getFragmentManager().getBackStackEntryCount() != 0);

                if (checkFragmentVisibility(infoFragment)) {
                    mLogManager.log(StringUtils.logLine(LOCKER_UI_MODULE, "ChangeSecurityFlowFinished", "Password change failed."), LogLevel.DEBUG);
                    mLogManager.log(StringUtils.logLine(LOCKER_UI_MODULE, "DisplayInfoFlowStarted", "Start display info flow with the following options: "
                            + mCsJson.toJson(mLockerUIManagerImpl.getDisplayInfoOptions())), LogLevel.DEBUG);
                } else if (!checkFragmentVisibility(registerFragment)
                        && (!checkFragmentVisibility(lockerUIFragment)
                        || checkSkipStatusScreenCustomization()))
                    finish();
            }
        }
    }


    @Override
    public void finish() {
        dispatchCallback();
        mLockerUIManagerImpl.clearCurrentLockerUIActivity();
        super.finish();
    }

    private void setCustomizedBackground() {
        Drawable drawable = mLockerUIManagerImpl.getBackgroundDrawable();
        if (drawable == null)
            drawable = ContextCompat.getDrawable(this, R.drawable.default_background_screen);
        Drawable[] drawables = {ColorUtils.createBackground(mLockerUIManagerImpl.getMainColor()
                , null, ((BitmapDrawable) drawable).getBitmap(), this),
                new ColorDrawable(ColorUtils.setTransparency(30, mLockerUIManagerImpl.getMainColor()))};
        setBackground(mFlContainer, new LayerDrawable(drawables));
    }

    private void setNavBar() {
        CsNavBarColor csNavBarColor = mLockerUIOptions.getNavBarColor();
        Integer customNavBarColor = mLockerUIOptions.getCustomNavBarColor();

        if (customNavBarColor != null) {
            setBackground(mToolbar, new ColorDrawable(customNavBarColor));
        } else if (csNavBarColor == CsNavBarColor.WHITE) {
            setBackground(mToolbar, new ColorDrawable(ContextCompat.getColor(this, R.color.csasColorWhite)));
        } else {
            setBackground(mToolbar, new ColorDrawable(ContextCompat.getColor(this, R.color.csasColorNavBar)));
        }

        ShowLogo showLogo = mLockerUIManagerImpl.getShowLogo();
        switch (showLogo) {
            case ALWAYS:
            case EXCEPT_REGISTRATION:
                mToolbar.setLogo(ContextCompat.getDrawable(this, R.drawable.logo_csas));
                break;
            case NEVER:
                mToolbar.setLogo(null);
                break;
        }

        if (customNavBarColor != null) {
            mLockerUIManagerImpl.getLocker().setOAuthLoginActivityOptions(new OAuthLoginActivityOptions.Builder().setNavBarColor(customNavBarColor).setShowLogo(showLogo == ShowLogo.ALWAYS).create());
        } else {
            mLockerUIManagerImpl.getLocker().setOAuthLoginActivityOptions(new OAuthLoginActivityOptions.Builder().setNavBarColor(csNavBarColor).setShowLogo(showLogo == ShowLogo.ALWAYS).create());
        }
    }

    private void setNavigationIcon(Fragment fragment) {
        if (mToolbar != null) {
            mToolbar.setNavigationContentDescription(R.string.navigation_back_content_description);
            if (fragment.getClass() == InfoFragment.class)
                mToolbar.setNavigationIcon(ContextCompat.getDrawable(this, R.drawable.ic_arrow_left));
            else
                mToolbar.setNavigationIcon(null);
        }
    }

    private void hideActionBar() {
        if (getActionBar() != null)
            getActionBar().hide();
        if (getSupportActionBar() != null)
            getSupportActionBar().hide();
    }

    private void dispatchCallback() {
        CallbackUI<LockerStatus> lockerUICallback = mLockerUIManagerImpl.getLockerUICallback();
        if (lockerUICallback != null)
            lockerUICallback.success(LockerUI.getInstance().getLocker().getStatus());
    }

    private boolean hasLockerUIFragmentInBackStack() {
        for (int entry = 0; entry < getFragmentManager().getBackStackEntryCount(); entry++) {
            if (getFragmentManager().getBackStackEntryAt(entry).getName() == Constants.FRAGMENT_LOCKERUI)
                return true;
        }
        return false;
    }

    private boolean checkSkipStatusScreenCustomization() {
        SkipStatusScreen skipStatusScreen = ((LockerUIImpl) LockerUI.getInstance()).getLockerUIManager().getAuthFlowOptions().getSkipStatusScreen();
        LockerStatus lockerStatus = LockerUI.getInstance().getLocker().getStatus();
        State state = lockerStatus.getState();
        return skipStatusScreen != null && (skipStatusScreen == ALWAYS || (skipStatusScreen == SkipStatusScreen.WHEN_LOCKED && state == State.USER_LOCKED) || (skipStatusScreen == SkipStatusScreen.WHEN_NOT_REGISTERED && state == State.USER_UNREGISTERED));
    }

    private boolean checkFragmentVisibility(Fragment fragment) {
        return fragment != null && fragment.isVisible();
    }

    private boolean unregisterIfLockTypeNotValid(cz.csas.cscore.locker.LockType lockType) {
        for (LockType allowedLockType : mLockerUIOptions.getAllowedLockTypes()) {
            if (lockType == allowedLockType.getLockType())
                return true;
        }
        LockerUI.getInstance().getLocker().unregister(new CsCallback<LockerStatus>() {
            @Override
            public void success(LockerStatus lockerStatus, Response response) {
                // Do nothing
            }

            @Override
            public void failure(CsSDKError error) {
                // Do nothing
            }
        });
        return false;
    }
}

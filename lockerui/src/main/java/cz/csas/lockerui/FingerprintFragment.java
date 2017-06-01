package cz.csas.lockerui;


import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.graphics.Color;
import android.hardware.fingerprint.FingerprintManager;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.accessibility.AccessibilityEvent;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import csas.cz.lockerui.R;
import cz.csas.cscore.client.rest.CallbackBasic;
import cz.csas.cscore.client.rest.CsCallback;
import cz.csas.cscore.client.rest.CsRestError;
import cz.csas.cscore.client.rest.client.Response;
import cz.csas.cscore.error.CsSDKError;
import cz.csas.cscore.locker.LockType;
import cz.csas.cscore.locker.LockerRegistrationProcess;
import cz.csas.cscore.locker.LockerStatus;
import cz.csas.cscore.locker.OfflineUnlockResponse;
import cz.csas.cscore.locker.Password;
import cz.csas.cscore.locker.PasswordResponse;
import cz.csas.cscore.locker.RegistrationOrUnlockResponse;
import cz.csas.cscore.locker.State;
import cz.csas.lockerui.error.LockerUIErrorHandler;
import cz.csas.lockerui.utils.ColorUtils;
import cz.csas.lockerui.utils.TypefaceUtils;

/**
 * @author Jan Hauser <hauseja3@gmail.com>
 * @since 22/02/16.
 */
@SuppressLint({"ValidFragment", "NewApi"})
public class FingerprintFragment extends Fragment implements FingerprintHelper.Callback {

    private RelativeLayout mRlFingerprintParent;
    private ImageView mIvPicture;
    private TextView mTvTitle;
    private TextView mTvDescription;
    private Button mBtnFingerprint;
    private Button mBtnFingerprintBackground;
    private Button mBtnNewRegistration;
    private Button mBtnNewRegistrationBackground;
    private ImageView mIvBrokenOverlay;
    private FingerprintHelper mFingerprintHelper;
    private FingerprintManager mFingerprintManager;
    private FragmentCallback mFragmentCallback;
    private State mState;
    private String mOldFingerprintHash;
    private boolean mFingerprintFailedFlag = false;

    @SuppressWarnings("deprecation")
    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        mFragmentCallback = (FragmentCallback) activity;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        RelativeLayout group = new RelativeLayout(getActivity());
        populateViewForOrientation(inflater, group);

        mFingerprintManager = (FingerprintManager) getActivity().getSystemService(Context.FINGERPRINT_SERVICE);
        // this permission condition should be always true
        if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.USE_FINGERPRINT) == PackageManager.PERMISSION_GRANTED &&
                mFingerprintManager.hasEnrolledFingerprints()) {
            mFingerprintHelper = new FingerprintHelper(mFingerprintManager, this, getActivity());
            if (mFingerprintHelper.initCipher())
                setCheckingScreen();
            else
                setAgainScreen(null);
        } else {
            if (mState == State.USER_UNREGISTERED) {
                setUiComponents(R.string.title_fingerprint_fragment,
                        R.string.description_set_fingerprint_fragment,
                        R.string.btn_fingerprint_set,
                        View.VISIBLE,
                        View.VISIBLE,
                        View.INVISIBLE,
                        null);
            } else {
                ((LockerUIImpl) LockerUI.getInstance()).getLockerUIManager().getLocker().unregister(new CsCallback<LockerStatus>() {
                    @Override
                    public void success(LockerStatus lockerStatus, Response response) {

                    }

                    @Override
                    public void failure(CsSDKError error) {

                    }

                });
                setUiComponents(R.string.authorization_failed_result_activity,
                        R.string.description_deleted_fingerprint_fragment,
                        null,
                        View.INVISIBLE,
                        View.VISIBLE,
                        View.VISIBLE,
                        null);
            }
        }

        return group;
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        populateViewForOrientation(LayoutInflater.from(getActivity()), (ViewGroup) getView());
    }

    private void populateViewForOrientation(LayoutInflater inflater, ViewGroup viewGroup) {
        viewGroup.removeAllViewsInLayout();
        View rootView = inflater.inflate(R.layout.register_fingerprint_fragment, viewGroup);
        init(rootView);
    }

    private void init(View rootView) {
        // init all components
        mTvTitle = (TextView) rootView.findViewById(R.id.tv_fingerprint_fragment);
        mTvDescription = (TextView) rootView.findViewById(R.id.tv_description_fingerprint_fragment);
        mIvPicture = (ImageView) rootView.findViewById(R.id.iv_fingerprint_fragment);
        mIvBrokenOverlay = (ImageView) rootView.findViewById(R.id.iv_fingerprint_broken);
        mBtnFingerprint = (Button) rootView.findViewById(R.id.btn_fingerprint);
        mBtnFingerprintBackground = (Button) rootView.findViewById(R.id.btn_fingerprint_background);
        mBtnNewRegistration = (Button) rootView.findViewById(R.id.btn_new_registration_fingerprint_fragment);
        mBtnNewRegistrationBackground = (Button) rootView.findViewById(R.id.btn_new_registration_fingerprint_fragment_background);
        mRlFingerprintParent = (RelativeLayout) rootView.findViewById(R.id.rl_fingerprint_parent);

        mTvTitle.setTypeface(TypefaceUtils.getRobotoBlack(getActivity()));
        mTvDescription.setTypeface(TypefaceUtils.getRobotoRegular(getActivity()));
        mBtnFingerprint.setTypeface(TypefaceUtils.getRobotoBlack(getActivity()));
        mBtnNewRegistration.setTypeface(TypefaceUtils.getRobotoBlack(getActivity()));
        setButtonVisibility(View.INVISIBLE, View.INVISIBLE);

        // set ui
        int lightColor = LockerUI.mLockerUIManager.getLightColor();
        ColorUtils.colorizeButton(mBtnFingerprint, lightColor, Color.WHITE);
        ColorUtils.colorizeButtonBackground(mBtnFingerprintBackground, lightColor);
        mBtnFingerprint.setTextColor(LockerUI.mLockerUIManager.getDarkColor());
        mIvPicture.setImageDrawable(ColorUtils.createBackground(LockerUI.mLockerUIManager.getMainColor(), R.drawable.icons_finger_shape, R.drawable.icons_finger_screen, getActivity()));

        mState = LockerUI.getInstance().getLocker().getStatus().getState();
        if (mFingerprintFailedFlag) {
            setAgainScreen(null);
        }
        mBtnFingerprint.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String btnText = mBtnFingerprint.getText().toString();
                if (btnText.equals(getString(R.string.btn_fingerprint_set))) {
                    startActivity(new Intent(Settings.ACTION_SETTINGS));
                    ((MainActivity) getActivity()).onBackPressed();
                } else if (btnText.equals(getString(R.string.btn_fingerprint_repeat))) {
                    startAuthentiation();
                    mFingerprintFailedFlag = false;
                }

            }
        });

        mBtnNewRegistration.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mState = LockerUI.getInstance().getLocker().getStatus().getState();
                if (mState != State.USER_UNREGISTERED) {
                    ((LockerUIImpl) LockerUI.getInstance()).getLockerUIManager().getLocker().unregister(new CsCallback<LockerStatus>() {
                        @Override
                        public void success(LockerStatus lockerStatus, Response response) {
                            startNewRegistration();
                        }

                        @Override
                        public void failure(CsSDKError error) {
                            startNewRegistration();
                        }
                    });
                } else if (mTvDescription.getText().equals(getString(R.string.description_deleted_fingerprint_fragment)))
                    startNewRegistration();
                else
                    mFragmentCallback.changeFragmentToRegister();
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        readFragmentDescription();
        if (mFingerprintHelper != null)
            startAuthentiation();
    }

    @Override
    public void onPause() {
        super.onPause();
        if (mFingerprintHelper != null)
            mFingerprintHelper.stopListening();
    }


    @Override
    public void onAuthenticated(String fingerprintHash) {
        if (mState == State.USER_UNREGISTERED)
            checkRegisterFingerprintResult(fingerprintHash);
        else if (mState == State.USER_LOCKED)
            checkUnlockFingerprintResult(fingerprintHash);
        else if (mState == State.USER_UNLOCKED) {
            if (((LockerUIImpl) LockerUI.getInstance()).getLockerUIManager().isChangingPassword())
                handlePasswordCheckFingerprint(fingerprintHash);
            else
                checkPasswordChangeFingerprintResult(fingerprintHash);
        }
    }

    @Override
    public void onError(int msgId, CharSequence msg) {
        setAgainScreen(msg);
    }

    private void checkRegisterFingerprintResult(String fingerprintHash) {
        if (fingerprintHash != null) {
            ((LockerUIImpl) LockerUI.getInstance()).getLockerUIManager().getLockerRegistrationProcess().finishRegistration(new Password(LockType.FINGERPRINT, fingerprintHash));
            mFragmentCallback.changeFragmentToResult();
        }
    }

    private void checkUnlockFingerprintResult(String fingerprintHash) {
        ((LockerUIImpl) LockerUI.getInstance()).getLockerUIManager().getLocker().unlock(fingerprintHash, new CsCallback<RegistrationOrUnlockResponse>() {
            @Override
            public void success(RegistrationOrUnlockResponse registrationOrUnlockResponse, Response response) {
                if (registrationOrUnlockResponse instanceof OfflineUnlockResponse
                        && ((OfflineUnlockResponse) registrationOrUnlockResponse).getError() instanceof CsRestError
                        && !((LockerUIImpl) LockerUI.getInstance()).getLockerUIManager().getAuthFlowOptions().isOfflineAuthEnabled())
                    failure((CsRestError) ((OfflineUnlockResponse) registrationOrUnlockResponse).getError());
                else
                    MainActivity.onUnlockSuccess(registrationOrUnlockResponse);
            }

            @Override
            public void failure(CsSDKError error) {
                if (LockerUIErrorHandler.handleError(mFragmentCallback, error))
                    MainActivity.onUnlockFailed(error);
            }
        });
        mFragmentCallback.changeFragmentToResult();
    }

    private void checkPasswordChangeFingerprintResult(String fingerprintHash) {
        if (mOldFingerprintHash == null)
            mOldFingerprintHash = ((LockerUIImpl) LockerUI.getInstance()).getLockerUIManager().getPassword();
        ((LockerUIImpl) LockerUI.getInstance()).getLockerUIManager().setPassword(null);
        if (fingerprintHash != null) {
            ((LockerUIImpl) LockerUI.getInstance()).getLockerUIManager().getLocker().changePassword(mOldFingerprintHash, new Password(LockType.FINGERPRINT, fingerprintHash), new CsCallback<PasswordResponse>() {
                        @Override
                        public void success(PasswordResponse passwordResponse, Response response) {
                            MainActivity.onPasswordChangeSuccess(passwordResponse, LockType.FINGERPRINT);
                        }

                        @Override
                        public void failure(CsSDKError error) {
                            if (LockerUIErrorHandler.handleError(mFragmentCallback, error))
                                MainActivity.onPasswordChangeFailure();
                        }
                    }
            );
            mFragmentCallback.changeFragmentToResult();
        } else
            setAgainScreen(null);
    }

    private void handlePasswordCheckFingerprint(String fingerprintHash) {
        mOldFingerprintHash = fingerprintHash;
        mFragmentCallback.changeFragmentToResult();
        ((LockerUIImpl) LockerUI.getInstance()).getLockerUIManager().getLocker().unlock(mOldFingerprintHash, new CsCallback<RegistrationOrUnlockResponse>() {
            @Override
            public void success(RegistrationOrUnlockResponse registrationOrUnlockResponse, Response response) {
                ((LockerUIImpl) LockerUI.getInstance()).getLockerUIManager().setPassword(mOldFingerprintHash);
                mOldFingerprintHash = null;
                MainActivity.onPasswordCheckSuccess(registrationOrUnlockResponse, LockType.FINGERPRINT);
            }

            @Override
            public void failure(CsSDKError error) {
                if (LockerUIErrorHandler.handleError(mFragmentCallback, error))
                    MainActivity.onPasswordCheckFailure();
            }
        });
    }

    private void startNewRegistration() {
        mFragmentCallback.clearFragment();
        ((LockerUIImpl) LockerUI.getInstance()).getLockerUIManager().getLocker().register(getActivity(), new CallbackBasic<LockerRegistrationProcess>() {
            @Override
            public void success(LockerRegistrationProcess lockerRegistrationProcess) {
                ((LockerUIImpl) LockerUI.getInstance()).getLockerUIManager().setLockerRegistrationProcess(lockerRegistrationProcess);
            }

            @Override
            public void failure() {
                MainActivity.onRegistrationFailed();
            }
        }, new CsCallback<RegistrationOrUnlockResponse>() {
            @Override
            public void success(RegistrationOrUnlockResponse registrationOrUnlockResponse, Response response) {
                MainActivity.onRegistrationSuccess();
            }

            @Override
            public void failure(CsSDKError error) {
                if (LockerUIErrorHandler.handleError(mFragmentCallback, error)) {
                    if (((LockerUIImpl) LockerUI.getInstance()).getLockerUIManager().getLockerUICallback() != null)
                        ((LockerUIImpl) LockerUI.getInstance()).getLockerUIManager().getLockerUICallback().failure((CsRestError) error);
                    MainActivity.onRegistrationFailed();
                }
            }
        });
    }

    private void startAuthentiation() {
        setCheckingScreen();
        mFingerprintHelper.startListening();
    }

    private void setCheckingScreen() {
        setUiComponents(R.string.title_fingerprint_fragment,
                R.string.description_in_progress_fingerprint_fragment,
                null,
                View.INVISIBLE,
                View.INVISIBLE,
                View.INVISIBLE,
                null);
    }

    private void setAgainScreen(CharSequence msg) {
        mFingerprintFailedFlag = true;
        setUiComponents(R.string.authorization_failed_result_activity,
                R.string.description_failed_fingerprint_fragment,
                R.string.btn_fingerprint_repeat,
                View.VISIBLE,
                View.VISIBLE,
                View.VISIBLE,
                msg);
        mTvTitle.sendAccessibilityEvent(AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED);
    }

    private void setButtonVisibility(int visibilityButtonOther, int visibilityButtonNewReg) {
        mBtnFingerprint.setVisibility(visibilityButtonOther);
        mBtnFingerprintBackground.setVisibility(visibilityButtonOther);
        mBtnNewRegistration.setVisibility(visibilityButtonNewReg);
        mBtnNewRegistrationBackground.setVisibility(visibilityButtonNewReg);
    }

    private void setUiComponents(int titleResource, int descriptionResource, Integer btnText, int visibilityButtonOther, int visibilityButtonNewReg, int visibilityBrokenOverlay, CharSequence additionalDescText) {
        if (this.isAdded()) {
            mTvTitle.setText(getString(titleResource));
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append(getString(descriptionResource));
            //if (additionalDescText != null)
            //    stringBuilder.append(" " + additionalDescText.toString());
            mTvDescription.setText(stringBuilder);
            mIvBrokenOverlay.setVisibility(visibilityBrokenOverlay);
            if (btnText != null)
                mBtnFingerprint.setText(getString(btnText));
            setButtonVisibility(visibilityButtonOther, visibilityButtonNewReg);
        }
    }

    private void readFragmentDescription() {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                mRlFingerprintParent.sendAccessibilityEvent(AccessibilityEvent.TYPE_VIEW_FOCUSED);
            }
        }, 1000);
    }
}

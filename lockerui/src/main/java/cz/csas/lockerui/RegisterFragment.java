package cz.csas.lockerui;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.hardware.fingerprint.FingerprintManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.accessibility.AccessibilityEvent;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.List;

import csas.cz.lockerui.R;
import cz.csas.cscore.client.rest.CsCallback;
import cz.csas.cscore.client.rest.client.Response;
import cz.csas.cscore.error.CsSDKError;
import cz.csas.cscore.locker.Password;
import cz.csas.cscore.locker.PasswordResponse;
import cz.csas.cscore.locker.State;
import cz.csas.lockerui.config.FingerprintLock;
import cz.csas.lockerui.config.GestureLock;
import cz.csas.lockerui.config.LockType;
import cz.csas.lockerui.config.NoLock;
import cz.csas.lockerui.config.PinLock;
import cz.csas.lockerui.error.LockerUIErrorHandler;
import cz.csas.lockerui.utils.ColorUtils;
import cz.csas.lockerui.utils.ScreenUtils;
import cz.csas.lockerui.utils.TypefaceUtils;

/**
 * @author Jan Hauser <jan.hauser@applifting.cz>
 * @since 01/12/15.
 */
@SuppressLint("ValidFragment")
public class RegisterFragment extends Fragment {

    private FragmentCallback mFragmentCallback;
    private LinearLayout mLlRegisterParent;
    private RelativeLayout mRlRegisterParent;
    private Button mBtnPin;
    private Button mBtnFingerprint;
    private Button mBtnGesture;
    private Button mBtnNoSecurity;
    private ImageView mIvSecurity;
    private TextView mTvTitle;
    private TextView mTvDescription;
    private GestureLock mGestureLock;
    private NoLock mNoLock;
    private PinLock mPinLock;
    private FingerprintLock mFingerprintLock;
    private List<LockType> mLockTypes;
    private State mState;

    @SuppressWarnings("deprecation")
    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        mFragmentCallback = (FragmentCallback) activity;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        LinearLayout group = new LinearLayout(getActivity());
        populateViewForOrientation(inflater, group);
        return group;
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        populateViewForOrientation(LayoutInflater.from(getActivity()), (ViewGroup) getView());
    }

    private void populateViewForOrientation(LayoutInflater inflater, ViewGroup viewGroup) {
        viewGroup.removeAllViewsInLayout();
        View rootView = inflater.inflate(R.layout.register_fragment, viewGroup);
        init(rootView);
    }

    private void init(View rootView) {
        // init all components
        mBtnPin = (Button) rootView.findViewById(R.id.btn_pin_register_activity);
        mBtnFingerprint = (Button) rootView.findViewById(R.id.btn_fingerprint_register_activity);
        mBtnGesture = (Button) rootView.findViewById(R.id.btn_gesture_register_activity);
        mBtnNoSecurity = (Button) rootView.findViewById(R.id.btn_no_security_register_activity);
        mTvTitle = (TextView) rootView.findViewById(R.id.tv_security_register_activity);
        mTvDescription = (TextView) rootView.findViewById(R.id.tv_security_description_register_activity);
        mIvSecurity = (ImageView) rootView.findViewById(R.id.iv_security_register_activity);
        mLlRegisterParent = (LinearLayout) rootView.findViewById(R.id.ll_register_parent);
        mRlRegisterParent = (RelativeLayout) rootView.findViewById(R.id.rl_register_parent);

        mBtnGesture.setTypeface(TypefaceUtils.getRobotoBlack(getActivity()));
        mBtnNoSecurity.setTypeface(TypefaceUtils.getRobotoBlack(getActivity()));
        mBtnPin.setTypeface(TypefaceUtils.getRobotoBlack(getActivity()));
        mBtnFingerprint.setTypeface(TypefaceUtils.getRobotoBlack(getActivity()));
        mTvTitle.setTypeface(TypefaceUtils.getRobotoBlack(getActivity()));
        mTvDescription.setTypeface(TypefaceUtils.getRobotoRegular(getActivity()));

        // set ui
        int color = LockerUI.mLockerUIManager.getDarkColor();
        int darkColor = LockerUI.mLockerUIManager.getDarkColor();
        ColorUtils.colorizeButton(mBtnPin, darkColor, ColorUtils.setTransparency(60, darkColor));
        ColorUtils.colorizeButton(mBtnFingerprint, darkColor, ColorUtils.setTransparency(60, darkColor));
        ColorUtils.colorizeButton(mBtnGesture, darkColor, ColorUtils.setTransparency(60, darkColor));
        ColorUtils.colorizeButton(mBtnNoSecurity, darkColor, ColorUtils.setTransparency(60, darkColor));
        mIvSecurity.setImageDrawable(ColorUtils.createBackground(color, R.drawable.lock_ok_shape, R.drawable.lock_ok_screen, getActivity()));

        setLockTypes();
        mState = LockerUI.getInstance().getLocker().getStatus().getState();

        mBtnPin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                handleButtonClicked(cz.csas.cscore.locker.LockType.PIN);
            }
        });

        mBtnFingerprint.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                handleButtonClicked(cz.csas.cscore.locker.LockType.FINGERPRINT);
            }
        });

        mBtnGesture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                handleButtonClicked(cz.csas.cscore.locker.LockType.GESTURE);
            }
        });

        mBtnNoSecurity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                handleButtonClicked(cz.csas.cscore.locker.LockType.NONE);
            }
        });

        if (mLockTypes != null && mLockTypes.size() == 1) {
            switch (mLockTypes.get(0).getLockType()) {
                case PIN:
                    handleButtonClicked(cz.csas.cscore.locker.LockType.PIN);
                    break;
                case GESTURE:
                    handleButtonClicked(cz.csas.cscore.locker.LockType.GESTURE);
                    break;
                case FINGERPRINT:
                    handleButtonClicked(cz.csas.cscore.locker.LockType.FINGERPRINT);
                    break;
                case NONE:
                    handleButtonClicked(cz.csas.cscore.locker.LockType.NONE);
                    break;
            }
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        readFragmentDescription();
    }

    private void setLockTypes() {
        mLockTypes = ((LockerUIImpl) LockerUI.getInstance()).getLockerUIManager().getLockerUIOptions().getAllowedLockTypes();
        for (LockType lockType : mLockTypes) {
            if (lockType.getClass().equals(GestureLock.class) && ScreenUtils.checkTalkbackAvailability(getActivity()))
                mGestureLock = (GestureLock) lockType;
            else if (lockType.getClass().equals(PinLock.class))
                mPinLock = (PinLock) lockType;
            else if (lockType.getClass().equals(NoLock.class))
                mNoLock = (NoLock) lockType;
            else if (lockType.getClass().equals(FingerprintLock.class) && checkFingerprintAvailability())
                mFingerprintLock = (FingerprintLock) lockType;
        }
        if (mGestureLock == null)
            mBtnGesture.setVisibility(View.GONE);
        if (mPinLock == null)
            mBtnPin.setVisibility(View.GONE);
        if (mFingerprintLock == null)
            mBtnFingerprint.setVisibility(View.GONE);
        if (mNoLock == null) {
            mBtnNoSecurity.setVisibility(View.GONE);
            if (mGestureLock != null) {
                RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) mBtnGesture.getLayoutParams();
                params.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
                mBtnGesture.setLayoutParams(params);
            } else if (mFingerprintLock != null) {
                RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) mBtnFingerprint.getLayoutParams();
                params.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
                mBtnFingerprint.setLayoutParams(params);
            } else {
                RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) mBtnPin.getLayoutParams();
                params.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
                mBtnPin.setLayoutParams(params);
            }

        }
    }

    private void handleButtonClicked(cz.csas.cscore.locker.LockType lockType) {
        ((LockerUIImpl) LockerUI.getInstance()).getLockerUIManager().setChangingPassword(false);
        switch (lockType) {
            case PIN:
                mFragmentCallback.changeFragmentToPin();
                break;
            case GESTURE:
                mFragmentCallback.changeFragmentToGesture();
                break;
            case FINGERPRINT:
                mFragmentCallback.changeFragmentToFingerprint();
                break;
            case NONE:
                if (mState == State.USER_UNREGISTERED)
                    handleNoneUnregistered();
                else if (mState == State.USER_UNLOCKED)
                    handleNoneUnlocked();
                break;
        }
    }

    @SuppressLint("NewApi")
    private boolean checkFingerprintAvailability() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            FingerprintManager fingerprintManager = (FingerprintManager) getActivity().getSystemService(Context.FINGERPRINT_SERVICE);
            // this permission condition should be always true
            if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.USE_FINGERPRINT) == PackageManager.PERMISSION_GRANTED &&
                    !fingerprintManager.isHardwareDetected()) {
                return false;
            } else
                return true;
        }
        return false;
    }

    private void handleNoneUnregistered() {
        ((LockerUIImpl) LockerUI.getInstance()).getLockerUIManager().getLockerRegistrationProcess().finishRegistration(new Password(cz.csas.cscore.locker.LockType.NONE, String.valueOf(System.currentTimeMillis())));
        mFragmentCallback.changeFragmentToResult();
    }

    private void handleNoneUnlocked() {
        ((LockerUIImpl) LockerUI.getInstance()).getLockerUIManager().getLocker().changePassword(((LockerUIImpl) LockerUI.getInstance()).getLockerUIManager().getPassword(), new Password(cz.csas.cscore.locker.LockType.NONE, String.valueOf(System.currentTimeMillis())), new CsCallback<PasswordResponse>() {
                    @Override
                    public void success(PasswordResponse passwordResponse, Response response) {
                        MainActivity.onPasswordChangeSuccess(passwordResponse, cz.csas.cscore.locker.LockType.NONE);
                    }

                    @Override

                    public void failure(CsSDKError error) {
                        if (LockerUIErrorHandler.handleError(mFragmentCallback, error))
                            MainActivity.onPasswordChangeFailure();
                    }
                }

        );
        mFragmentCallback.changeFragmentToResult();
    }

    private void readFragmentDescription() {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if (mRlRegisterParent != null)
                    mRlRegisterParent.sendAccessibilityEvent(AccessibilityEvent.TYPE_VIEW_FOCUSED);
                else
                    mLlRegisterParent.sendAccessibilityEvent(AccessibilityEvent.TYPE_VIEW_FOCUSED);
            }
        }, 1000);
    }
}

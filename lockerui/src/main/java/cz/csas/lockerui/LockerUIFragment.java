package cz.csas.lockerui;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.content.res.Configuration;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.accessibility.AccessibilityEvent;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import csas.cz.lockerui.R;
import cz.csas.cscore.client.rest.Callback;
import cz.csas.cscore.client.rest.CallbackBasic;
import cz.csas.cscore.client.rest.CsRestError;
import cz.csas.cscore.client.rest.client.Response;
import cz.csas.cscore.locker.LockType;
import cz.csas.cscore.locker.LockerRegistrationProcess;
import cz.csas.cscore.locker.LockerStatus;
import cz.csas.cscore.locker.RegistrationOrUnlockResponse;
import cz.csas.cscore.locker.State;
import cz.csas.lockerui.config.SkipStatusScreen;
import cz.csas.lockerui.error.LockerUIErrorHandler;
import cz.csas.lockerui.utils.ColorUtils;
import cz.csas.lockerui.utils.TypefaceUtils;

/**
 * The type Locker ui fragment.
 *
 * @author Jan Hauser <jan.hauser@applifting.cz>
 * @since 01 /12/15.
 */
@SuppressLint("ValidFragment")
public class LockerUIFragment extends Fragment implements View.OnClickListener {

    private RelativeLayout mRlLockerUiActivity;
    private TextView mTvTitle;
    private TextView mTvUnlockOrRegistrationDescription;
    private TextView mTvUnlockOrRegistration;
    private Button mBtnUnlockOrRegistration;
    private Button mBtnUnlockOrRegistrationBackground;
    private ImageView mIvLockerImage;
    private Context mContext;
    private FragmentCallback mFragmentCallback;
    private State mState;
    private cz.csas.cscore.locker.LockType mLockType;

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
        return group;
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        populateViewForOrientation(LayoutInflater.from(getActivity()), (ViewGroup) getView());
    }

    private void populateViewForOrientation(LayoutInflater inflater, ViewGroup viewGroup) {
        viewGroup.removeAllViewsInLayout();
        View rootView = inflater.inflate(R.layout.lockerui_fragment, viewGroup);
        init(rootView);
    }

    private void init(View rootView) {
        // init all components
        mTvTitle = (TextView) rootView.findViewById(R.id.title_lockerui_activity);
        mTvUnlockOrRegistrationDescription = (TextView) rootView.findViewById(R.id.tv_unlock_or_register_description_lockerui_activity);
        mTvUnlockOrRegistration = (TextView) rootView.findViewById(R.id.tv_unlock_or_register_lockerui_activity);
        mBtnUnlockOrRegistration = (Button) rootView.findViewById(R.id.btn_unlock_or_register_lockerui_activity);
        mBtnUnlockOrRegistrationBackground = (Button) rootView.findViewById(R.id.btn_unlock_or_register_background_lockerui_activity);
        mIvLockerImage = (ImageView) rootView.findViewById(R.id.iv_lockerui_activity);
        mRlLockerUiActivity = (RelativeLayout) rootView.findViewById(R.id.rl_lockerui_activity);

        mBtnUnlockOrRegistration.setTypeface(TypefaceUtils.getRobotoBlack(getActivity()));
        mTvUnlockOrRegistrationDescription.setTypeface(TypefaceUtils.getRobotoRegular(getActivity()));
        mTvTitle.setTypeface(TypefaceUtils.getRobotoRegular(getActivity()));
        mTvUnlockOrRegistration.setTypeface(TypefaceUtils.getRobotoBlack(getActivity()));

        // set ui
        mContext = getActivity();
        LockerStatus lockerStatus = LockerUI.getInstance().getLocker().getStatus();
        mState = lockerStatus.getState();
        mLockType = lockerStatus.getLockType();

        setCustomizedAppName();
        setState();
        setCustomizedColor();
        mBtnUnlockOrRegistration.setOnClickListener(this);
        SkipStatusScreen skipStatusScreen = ((LockerUIImpl) LockerUI.getInstance()).getLockerUIManager().getAuthFlowOptions().getSkipStatusScreen();
        if (skipStatusScreen != null && (skipStatusScreen == SkipStatusScreen.ALWAYS || (skipStatusScreen == SkipStatusScreen.WHEN_LOCKED && mState == State.USER_LOCKED) || (skipStatusScreen == SkipStatusScreen.WHEN_NOT_REGISTERED && mState == State.USER_UNREGISTERED)))
            onClick(mBtnUnlockOrRegistration);
    }

    @Override
    public void onResume() {
        super.onResume();
        readFragmentDescription();
    }

    private void setState() {
        if (mState == State.USER_UNLOCKED) {
            ((LockerUIImpl) LockerUI.getInstance()).getLockerUIManager().getLockerUICallback().success(((LockerUIImpl) LockerUI.getInstance()).getLocker().getStatus());
            getActivity().finish();
        } else if (mState == State.USER_LOCKED) {
            mTvUnlockOrRegistration.setText(R.string.unlock_lockerui_activity);
            setCustomizedLockScreenText();
            mBtnUnlockOrRegistration.setText(R.string.unlock_button_lockerui_activity);
            if (mIvLockerImage != null)
                mIvLockerImage.setImageDrawable(ColorUtils.createBackground(LockerUI.mLockerUIManager.getMainColor(), R.drawable.lock_ok_shape, R.drawable.lock_ok_screen, getActivity()));
        } else {
            mTvUnlockOrRegistration.setText(R.string.register_lockerui_activity);
            setCustomizedRegistrationScreenText();
            mBtnUnlockOrRegistration.setText(R.string.register_button_lockerui_activity);
            if (mIvLockerImage != null)
                mIvLockerImage.setImageDrawable(ColorUtils.createBackground(LockerUI.mLockerUIManager.getMainColor(), R.drawable.icon_key_shape, R.drawable.icon_key_screen, getActivity()));
        }
    }

    @Override
    public void onClick(View view) {
        if (mState == State.USER_LOCKED) {
            if (mLockType == cz.csas.cscore.locker.LockType.PIN) {
                mFragmentCallback.changeFragmentToPin();
            } else if (mLockType == cz.csas.cscore.locker.LockType.GESTURE) {
                mFragmentCallback.changeFragmentToGesture();
            } else if (mLockType == LockType.FINGERPRINT) {
                mFragmentCallback.changeFragmentToFingerprint();
            } else if (mLockType == cz.csas.cscore.locker.LockType.NONE) {
                mFragmentCallback.changeFragmentToResult();
                ((LockerUIImpl) LockerUI.getInstance()).getLockerUIManager().getLocker().unlock(null, new Callback<RegistrationOrUnlockResponse>() {
                    @Override
                    public void success(RegistrationOrUnlockResponse registrationOrUnlockResponse, Response response) {
                        MainActivity.onUnlockSuccess(registrationOrUnlockResponse);
                    }

                    @Override
                    public void failure(CsRestError error) {
                        if (LockerUIErrorHandler.handleError(mFragmentCallback, error)) {
                            MainActivity.onUnlockFailed(error);
                            ((LockerUIImpl) LockerUI.getInstance()).getLockerUIManager().getLockerUICallback().failure(error);
                        }
                    }
                });
            }

        } else {
            mFragmentCallback.clearFragment();
            ((LockerUIImpl) LockerUI.getInstance()).getLockerUIManager().getLocker().register(mContext, new CallbackBasic<LockerRegistrationProcess>() {
                @Override
                public void success(LockerRegistrationProcess lockerRegistrationProcess) {
                    ((LockerUIImpl) LockerUI.getInstance()).getLockerUIManager().setLockerRegistrationProcess(lockerRegistrationProcess);
                }

                @Override
                public void failure() {
                    MainActivity.onRegistrationFailed();
                }
            }, new Callback<RegistrationOrUnlockResponse>() {
                @Override
                public void success(RegistrationOrUnlockResponse registrationOrUnlockResponse, Response response) {
                    MainActivity.onRegistrationSuccess();
                }

                @Override
                public void failure(CsRestError error) {
                    if (LockerUIErrorHandler.handleError(mFragmentCallback, error)) {
                        MainActivity.onRegistrationFailed();
                        ((LockerUIImpl) LockerUI.getInstance()).getLockerUIManager().getLockerUICallback().failure(error);
                    }
                }
            });
        }
    }

    private void setCustomizedAppName() {
        mTvTitle.setText(((LockerUIImpl) LockerUI.getInstance()).getLockerUIManager().getLockerUIOptions().getAppName());
    }

    private void setCustomizedLockScreenText() {
        mTvUnlockOrRegistrationDescription.setText(((LockerUIImpl) LockerUI.getInstance()).getLockerUIManager().getAuthFlowOptions().getLockedScreenText());
    }

    private void setCustomizedRegistrationScreenText() {
        mTvUnlockOrRegistrationDescription.setText(((LockerUIImpl) LockerUI.getInstance()).getLockerUIManager().getAuthFlowOptions().getRegistrationScreenText());
    }

    private void setCustomizedColor() {
        int lightColor = LockerUI.mLockerUIManager.getLightColor();
        ColorUtils.colorizeButton(mBtnUnlockOrRegistration, lightColor, Color.WHITE);
        ColorUtils.colorizeButtonBackground(mBtnUnlockOrRegistrationBackground, lightColor);
        mBtnUnlockOrRegistration.setTextColor(LockerUI.mLockerUIManager.getDarkColor());
    }

    private void readFragmentDescription() {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                mRlLockerUiActivity.sendAccessibilityEvent(AccessibilityEvent.TYPE_VIEW_FOCUSED);
            }
        }, 1000);
    }
}

package cz.csas.lockerui;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Fragment;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.List;

import csas.cz.lockerui.R;
import cz.csas.cscore.client.rest.CsCallback;
import cz.csas.cscore.client.rest.CsRestError;
import cz.csas.cscore.client.rest.client.Response;
import cz.csas.cscore.error.CsSDKError;
import cz.csas.cscore.locker.OfflineUnlockResponse;
import cz.csas.cscore.locker.Password;
import cz.csas.cscore.locker.PasswordResponse;
import cz.csas.cscore.locker.RegistrationOrUnlockResponse;
import cz.csas.cscore.locker.State;
import cz.csas.lockerui.config.GestureLock;
import cz.csas.lockerui.config.LockType;
import cz.csas.lockerui.error.LockerUIErrorHandler;
import cz.csas.lockerui.utils.ColorUtils;
import cz.csas.lockerui.utils.TypefaceUtils;

/**
 * The type Gesture fragment.
 *
 * @author Jan Hauser <jan.hauser@applifting.cz>
 * @since 02 /12/15.
 */
@SuppressLint("ValidFragment")
public class GestureFragment extends Fragment implements PatternView.OnPatternDetectedListener {

    private LockerUIManagerImpl mLockerUIManagerImpl;
    private FragmentCallback mFragmentCallback;
    private PatternView mPatternView;
    private LinearLayout mLlGestureParent;
    private RelativeLayout mRlGestureInfo;
    private RelativeLayout mRlGestureView;
    private TextView mTvTitle;
    private TextView mTvDescription;
    private ImageView mIvGesture;
    private RelativeLayout mRlSwitchButton;
    private int mGestureMinLength;
    private State mState;
    private LockTypeState mLockTypeState = LockTypeState.NEW_CODE;
    private String mOldGestureHash;
    private String mGestureHash;
    private String mRepeatGestureHash;
    private boolean isSwitched = false;

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
        mLockerUIManagerImpl = (LockerUIManagerImpl) ((LockerUIImpl) LockerUI.getInstance()).getLockerUIManager();
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
        View rootView = inflater.inflate(R.layout.register_gesture_fragment, viewGroup);
        init(rootView);
    }

    private void init(View rootView) {
        // init all components
        mPatternView = (PatternView) rootView.findViewById(R.id.pv_register_fragment);
        mTvTitle = (TextView) rootView.findViewById(R.id.tv_gesture_fragment);
        mTvDescription = (TextView) rootView.findViewById(R.id.tv_gesture_description_fragment);
        mIvGesture = (ImageView) rootView.findViewById(R.id.iv_gesture_fragment);
        mRlSwitchButton = (RelativeLayout) rootView.findViewById(R.id.rl_switch_button);
        mRlGestureInfo = (RelativeLayout) rootView.findViewById(R.id.rl_gesture_info);
        mRlGestureView = (RelativeLayout) rootView.findViewById(R.id.rl_gesture_view);
        mLlGestureParent = (LinearLayout) rootView.findViewById(R.id.ll_gesture_parent);

        mTvTitle.setTypeface(TypefaceUtils.getRobotoBlack(getActivity()));
        mTvDescription.setTypeface(TypefaceUtils.getRobotoRegular(getActivity()));

        // set ui
        mIvGesture.setImageDrawable(ColorUtils.createBackground(LockerUI.mLockerUIManager.getMainColor(), R.drawable.icon_gesture_shape, R.drawable.icon_gesture_screen, getActivity()));
        mPatternView.setOnPatternDetectedListener(this);
        getGestureLength();
        mState = LockerUI.getInstance().getLocker().getStatus().getState();

        if (mRlSwitchButton != null) {
            isSwitched = mLockerUIManagerImpl.getSwitchedPreference();
            setSwitchedView();
            mRlSwitchButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    isSwitched = !isSwitched;
                    setSwitchedView();
                    mLockerUIManagerImpl.saveSwitchedPreference(isSwitched);
                }
            });
        }
        if (mState == State.USER_LOCKED || mState == State.USER_UNLOCKED) {
            mTvTitle.setText(R.string.title_gesture_fragment);
            setMinGestureLengthText();
            if (((LockerUIImpl) LockerUI.getInstance()).getLockerUIManager().getUnlockRemainingAttempts() != null) {
                if (((LockerUIImpl) LockerUI.getInstance()).getLockerUIManager().getUnlockRemainingAttempts() == 1)
                    mTvDescription.setText(getString(R.string.single_attempt_wrong_gesture_description_unlock_gesture_activity, ((LockerUIImpl) LockerUI.getInstance()).getLockerUIManager().getUnlockRemainingAttempts()));
                else
                    mTvDescription.setText(getString(R.string.wrong_gesture_description_unlock_gesture_activity, ((LockerUIImpl) LockerUI.getInstance()).getLockerUIManager().getUnlockRemainingAttempts()));
            }
        } else if (mState == State.USER_UNREGISTERED) {
            mTvTitle.setText(R.string.title_gesture_fragment);
            setMinGestureLengthText();
        }

    }

    private void setSwitchedView() {
        mLlGestureParent.removeAllViews();
        if (isSwitched) {
            mLlGestureParent.addView(mRlGestureView);
            mLlGestureParent.addView(mRlGestureInfo);
        } else {
            mLlGestureParent.addView(mRlGestureInfo);
            mLlGestureParent.addView(mRlGestureView);
        }
    }

    private void getGestureLength() {
        List<LockType> lockTypes = ((LockerUIImpl) LockerUI.getInstance()).getLockerUIManager().getLockerUIOptions().getAllowedLockTypes();
        for (LockType lockType : lockTypes) {
            if (lockType.getClass().equals(GestureLock.class))
                mGestureMinLength = ((GestureLock) lockType).getMinGestureLength();
        }
    }

    @Override
    public void onPatternDetected() {
        if (mPatternView.getPattern().size() >= mGestureMinLength) {
            if (mState == State.USER_UNREGISTERED)
                handleRegistrationGesture();
            else if (mState == State.USER_LOCKED)
                handleUnlockGesture();
            else if (mState == State.USER_UNLOCKED) {
                if (((LockerUIImpl) LockerUI.getInstance()).getLockerUIManager().isChangingPassword())
                    handlePasswordCheckGesture();
                else
                    handlePasswordChangeGesture();
            }
        } else
            setMinGestureLengthText();
        mPatternView.clearPattern();
    }

    private void handleRegistrationGesture() {
        if (mLockTypeState == LockTypeState.NEW_CODE) {
            mLockTypeState = LockTypeState.REPEAT_CODE;
            mTvTitle.setText(R.string.title_again_gesture_fragment);
            mTvDescription.setVisibility(View.INVISIBLE);
            mGestureHash = mPatternView.getPatternString();
        } else if (mLockTypeState == LockTypeState.REPEAT_CODE) {
            mLockTypeState = LockTypeState.NEW_CODE;
            mRepeatGestureHash = mPatternView.getPatternString();
            checkRegistrationGestureResult();
        }
    }

    private void checkRegistrationGestureResult() {
        if (mGestureHash != null && mRepeatGestureHash != null && mGestureHash.equals(mRepeatGestureHash)) {
            ((LockerUIImpl) LockerUI.getInstance()).getLockerUIManager().getLockerRegistrationProcess().finishRegistration(new Password(cz.csas.cscore.locker.LockType.GESTURE, mGestureHash, mPatternView.getGridSize()));
            mFragmentCallback.changeFragmentToResult();
        } else {
            mTvTitle.setText(R.string.title_gesture_fragment);
            mTvDescription.setText(R.string.description_failure_gesture_fragment);
        }
    }

    private void handleUnlockGesture() {
        mGestureHash = mPatternView.getPatternString();
        checkUnlockGestureResult();
    }

    private void checkUnlockGestureResult() {
        ((LockerUIImpl) LockerUI.getInstance()).getLockerUIManager().getLocker().unlock(mGestureHash, new CsCallback<RegistrationOrUnlockResponse>() {
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

    private void handlePasswordChangeGesture() {
        if (mLockTypeState == LockTypeState.NEW_CODE) {
            mLockTypeState = LockTypeState.REPEAT_CODE;
            mTvTitle.setText(R.string.title_again_gesture_fragment);
            mTvDescription.setVisibility(View.INVISIBLE);
            mGestureHash = mPatternView.getPatternString();
        } else if (mLockTypeState == LockTypeState.REPEAT_CODE) {
            mLockTypeState = LockTypeState.NEW_CODE;
            mRepeatGestureHash = mPatternView.getPatternString();
            checkPasswordChangeGestureResult();
        }
    }

    private void handlePasswordCheckGesture() {
        if (mLockTypeState == LockTypeState.NEW_CODE) {
            mOldGestureHash = mPatternView.getPatternString();
            mFragmentCallback.changeFragmentToResult();
            ((LockerUIImpl) LockerUI.getInstance()).getLockerUIManager().getLocker().unlock(mOldGestureHash, new CsCallback<RegistrationOrUnlockResponse>() {
                @Override
                public void success(RegistrationOrUnlockResponse registrationOrUnlockResponse, Response response) {
                    ((LockerUIImpl) LockerUI.getInstance()).getLockerUIManager().setPassword(mOldGestureHash);
                    mOldGestureHash = null;
                    MainActivity.onPasswordCheckSuccess(registrationOrUnlockResponse, cz.csas.cscore.locker.LockType.GESTURE);
                }

                @Override
                public void failure(CsSDKError error) {
                    if (LockerUIErrorHandler.handleError(mFragmentCallback, error))
                        MainActivity.onPasswordCheckFailure();
                }
            });
        }
    }

    private void checkPasswordChangeGestureResult() {
        if (mOldGestureHash == null)
            mOldGestureHash = ((LockerUIImpl) LockerUI.getInstance()).getLockerUIManager().getPassword();
        ((LockerUIImpl) LockerUI.getInstance()).getLockerUIManager().setPassword(null);

        if (mGestureHash != null && mRepeatGestureHash != null && mGestureHash.equals(mRepeatGestureHash)) {
            ((LockerUIImpl) LockerUI.getInstance()).getLockerUIManager().getLocker().changePassword(mOldGestureHash, new Password(cz.csas.cscore.locker.LockType.GESTURE, mGestureHash, mPatternView.getGridSize()), new CsCallback<PasswordResponse>() {
                        @Override
                        public void success(PasswordResponse passwordResponse, Response response) {
                            MainActivity.onPasswordChangeSuccess(passwordResponse, cz.csas.cscore.locker.LockType.GESTURE);
                        }

                        @Override
                        public void failure(CsSDKError error) {
                            if (LockerUIErrorHandler.handleError(mFragmentCallback, error))
                                MainActivity.onPasswordChangeFailure();
                        }
                    }

            );
            mFragmentCallback.changeFragmentToResult();
        } else {
            mTvTitle.setText(R.string.title_gesture_fragment);
            mTvDescription.setVisibility(View.VISIBLE);
            mTvDescription.setText(R.string.description_failure_gesture_fragment);
        }
    }

    private void setMinGestureLengthText() {
        if (mGestureMinLength == 4)
            mTvDescription.setText(getString(R.string.description_tutorial_low_gesture_fragment, mGestureMinLength));
        else
            mTvDescription.setText(getString(R.string.description_tutorial_gesture_fragment, mGestureMinLength));
    }
}

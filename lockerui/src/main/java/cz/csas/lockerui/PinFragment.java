package cz.csas.lockerui;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Fragment;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.accessibility.AccessibilityEvent;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
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
import cz.csas.lockerui.components.PinButton;
import cz.csas.lockerui.components.PinPad;
import cz.csas.lockerui.components.PinPoint;
import cz.csas.lockerui.components.SwitchButton;
import cz.csas.lockerui.config.LockType;
import cz.csas.lockerui.config.LockerUIOptions;
import cz.csas.lockerui.config.PinLock;
import cz.csas.lockerui.error.LockerUIErrorHandler;
import cz.csas.lockerui.utils.ColorUtils;
import cz.csas.lockerui.utils.ScreenUtils;
import cz.csas.lockerui.utils.TypefaceUtils;

import static cz.csas.lockerui.LockTypeState.NEW_CODE;
import static cz.csas.lockerui.utils.ColorUtils.setBackground;

/**
 * The type Pin fragment.
 *
 * @author Jan Hauser <jan.hauser@applifting.cz>
 * @since 01 /12/15.
 */
@SuppressLint("ValidFragment")
public class PinFragment extends Fragment implements View.OnClickListener {

    private LockerUIManagerImpl mLockerUIManagerImpl;
    private RelativeLayout mRlPinParent;
    private TextView mTvTitle;
    private TextView mTvDescription;
    private LinearLayout mLlPinPoints;
    private LinearLayout mLlPinParent;
    private RelativeLayout mRlPinInfo;
    private RelativeLayout mRlPinView;
    private PinPad mPpPinPad;
    private int mPinMaxLength;
    private StringBuilder mPinBuilder;
    private String mCode;
    private String mRepeatCode;
    private String mOldCode;
    private List<PinPoint> mPinPoints;
    private LockTypeState mLockTypeState = NEW_CODE;
    private State mState;
    private LockerUIOptions mLockerUIOptions;
    private cz.csas.cscore.locker.LockType mLockType;
    private FragmentCallback mFragmentCallback;
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
        mLockerUIOptions = LockerUI.mLockerUIManager.getLockerUIOptions();
        mPinBuilder = new StringBuilder();
        mState = LockerUI.getInstance().getLocker().getStatus().getState();
        mLockType = LockerUI.getInstance().getLocker().getStatus().getLockType();
        mLockerUIManagerImpl = (LockerUIManagerImpl) ((LockerUIImpl) LockerUI.getInstance()).getLockerUIManager();

        populateViewForOrientation(inflater, group, false);

        mPpPinPad.getRlPbDelete().setVisibility(View.INVISIBLE);
        return group;
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        populateViewForOrientation(LayoutInflater.from(getActivity()), (ViewGroup) getView(), false);
    }

    private void populateViewForOrientation(LayoutInflater inflater, ViewGroup viewGroup, boolean switched) {
        viewGroup.removeAllViewsInLayout();
        View rootView = inflater.inflate(R.layout.register_pin_fragment, viewGroup);
        init(rootView);
    }

    private void init(View rootView) {
        // init all components
        mRlPinParent = (RelativeLayout) rootView.findViewById(R.id.rl_pin_parent);
        mTvTitle = (TextView) rootView.findViewById(R.id.tv_title_register_pin_activity);
        mTvDescription = (TextView) rootView.findViewById(R.id.tv_description_register_pin_activity);
        mLlPinPoints = (LinearLayout) rootView.findViewById(R.id.ll_points_pin_activity);
        mPpPinPad = (PinPad) rootView.findViewById(R.id.pp_pinpad_pin_activity);
        mRlPinInfo = (RelativeLayout) rootView.findViewById(R.id.rl_pin_info);
        mRlPinView = (RelativeLayout) rootView.findViewById(R.id.rl_pin_view);
        mLlPinParent = (LinearLayout) rootView.findViewById(R.id.ll_pin_parent);
        mPpPinPad.setPinButtonsColor(ColorUtils.setTransparency(60, LockerUI.mLockerUIManager.getDarkColor()));

        mTvTitle.setTypeface(TypefaceUtils.getRobotoBlack(getActivity()));
        mTvDescription.setTypeface(TypefaceUtils.getRobotoRegular(getActivity()));

        // set ui
        getPinMaxLength();
        mTvDescription.setVisibility(View.INVISIBLE);
        mPinPoints = new ArrayList<PinPoint>();
        setPinPoints();
        checkPinPoint();
        setMainViewContentDescription(mLockTypeState);

        mPpPinPad.getPbOne().setOnClickListener(this);
        mPpPinPad.getPbTwo().setOnClickListener(this);
        mPpPinPad.getPbThree().setOnClickListener(this);
        mPpPinPad.getPbFour().setOnClickListener(this);
        mPpPinPad.getPbFive().setOnClickListener(this);
        mPpPinPad.getPbSix().setOnClickListener(this);
        mPpPinPad.getPbSeven().setOnClickListener(this);
        mPpPinPad.getPbEight().setOnClickListener(this);
        mPpPinPad.getPbNine().setOnClickListener(this);
        mPpPinPad.getPbZero().setOnClickListener(this);
        mPpPinPad.getRlPbDelete().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mPinBuilder.length() > 0) {
                    mPinBuilder.deleteCharAt(mPinBuilder.length() - 1);
                    checkPinPoint();
                }
            }
        });

        SwitchButton switchButton = mPpPinPad.getSwitchButton();
        if (switchButton != null) {
            isSwitched = mLockerUIManagerImpl.getSwitchedPreference();
            setSwitchedView();
            switchButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    isSwitched = !isSwitched;
                    setSwitchedView();
                    mLockerUIManagerImpl.saveSwitchedPreference(isSwitched);
                }
            });
        }

        if (mState == State.USER_LOCKED || mState == State.USER_UNLOCKED) {
            if (mLockType == cz.csas.cscore.locker.LockType.PIN)
                if (mLockTypeState == NEW_CODE)
                    mTvTitle.setText(R.string.pin_unlock_pin_activity);
                else
                    mTvTitle.setText(R.string.again_pin_register_pin_activity);
            else
                mTvTitle.setText(R.string.new_pin_register_pin_activity);
            if (((LockerUIImpl) LockerUI.getInstance()).getLockerUIManager().getUnlockRemainingAttempts() != null) {
                String descriptionText = getString(R.string.wrong_pin_description_unlock_pin_activity, ((LockerUIImpl) LockerUI.getInstance()).getLockerUIManager().getUnlockRemainingAttempts());
                mTvDescription.setVisibility(View.VISIBLE);
                mTvDescription.setText(descriptionText);
                // show toast message if unlock failed
                if (!ScreenUtils.checkTalkbackAvailability(getActivity()))
                    Toast.makeText(getActivity(), descriptionText, Toast.LENGTH_SHORT).show();
            }
        } else if (mState == State.USER_UNREGISTERED)
            mTvTitle.setText(R.string.new_pin_register_pin_activity);
    }

    @Override
    public void onResume() {
        super.onResume();
        readFragmentDescription();
    }

    @Override
    public void onClick(View view) {
        if (mPinBuilder.length() < mPinMaxLength) {
            mPinBuilder.append(((PinButton) view).getTvNumber().getText().toString());
            checkPinPoint();
        }

        if (mPinBuilder.length() == mPinMaxLength) {
            if (mState == State.USER_LOCKED)
                handleUnlockPin();
            else if (mState == State.USER_UNREGISTERED)
                handleRegistrationPin();
            else if (mState == State.USER_UNLOCKED) {
                if (((LockerUIImpl) LockerUI.getInstance()).getLockerUIManager().isChangingPassword())
                    handlePasswordCheckPin();
                else
                    handlePasswordChangePin();
            }
        }
    }

    private void setPinPoints() {
        for (int i = 0; i < mPinMaxLength; i++) {
            addPinPoint();
        }
    }

    private void addPinPoint() {
        PinPoint pinPoint = new PinPoint(getActivity());
        if (mPinPoints.size() < mPinMaxLength - 1)
            pinPoint.setPadding(0, 0, (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 10, getResources().getDisplayMetrics()), 0);
        mLlPinPoints.addView(pinPoint);
        mPinPoints.add(pinPoint);
    }

    private void checkPinPoint() {
        if (mPinBuilder.length() > 0)
            mPpPinPad.getRlPbDelete().setVisibility(View.VISIBLE);
        else
            mPpPinPad.getRlPbDelete().setVisibility(View.INVISIBLE);
        for (int i = 0; i < mPinMaxLength; i++) {
            if (i < mPinBuilder.length())
                setBackground(mPinPoints.get(i).getRlPoint(), ContextCompat.getDrawable(getActivity(), R.drawable.pin_point_white));
            else
                setBackground(mPinPoints.get(i).getRlPoint(), ContextCompat.getDrawable(getActivity(), R.drawable.pin_point_light));
        }
    }

    private void getPinMaxLength() {
        List<LockType> lockTypes = ((LockerUIImpl) LockerUI.getInstance()).getLockerUIManager().getLockerUIOptions().getAllowedLockTypes();
        for (LockType lockType : lockTypes) {
            if (lockType.getClass().equals(PinLock.class))
                mPinMaxLength = ((PinLock) lockType).getPinLength();
        }
    }

    private void setSwitchedView() {
        mLlPinParent.removeAllViews();
        if (isSwitched) {
            mLlPinParent.addView(mRlPinView);
            mLlPinParent.addView(mRlPinInfo);
        } else {
            mLlPinParent.addView(mRlPinInfo);
            mLlPinParent.addView(mRlPinView);
        }
    }

    private void handleRegistrationPin() {
        if (mLockTypeState == NEW_CODE) {
            mLockTypeState = LockTypeState.REPEAT_CODE;
            mTvTitle.setText(R.string.again_pin_register_pin_activity);
            mTvTitle.sendAccessibilityEvent(AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED);
            setMainViewContentDescription(mLockTypeState);
            mTvDescription.setVisibility(View.INVISIBLE);
            mCode = mPinBuilder.toString();
            mPinBuilder.setLength(0);
            checkPinPoint();
        } else if (mLockTypeState == LockTypeState.REPEAT_CODE) {
            mLockTypeState = NEW_CODE;
            mRepeatCode = mPinBuilder.toString();
            mPinBuilder.setLength(0);
            checkPinPoint();
            checkRegistrationPinResult();
        }
    }

    private void handleUnlockPin() {
        mCode = mPinBuilder.toString();
        mPinBuilder.setLength(0);
        checkUnlockPinResult();
    }

    private void checkRegistrationPinResult() {
        if (mCode != null && mRepeatCode != null && mCode.equals(mRepeatCode)) {
            ((LockerUIImpl) LockerUI.getInstance()).getLockerUIManager().getLockerRegistrationProcess().finishRegistration(new Password(cz.csas.cscore.locker.LockType.PIN, mCode, mCode.length()));
            mFragmentCallback.changeFragmentToResult();
        } else {
            mTvTitle.setText(R.string.new_pin_register_pin_activity);
            mTvDescription.setVisibility(View.VISIBLE);
            mTvDescription.setText(R.string.wrong_pin_description_register_pin_activity);
            mTvDescription.sendAccessibilityEvent(AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED);
            setMainViewContentDescription(mLockTypeState);
        }
    }

    private void checkUnlockPinResult() {
        ((LockerUIImpl) LockerUI.getInstance()).getLockerUIManager().getLocker().unlock(mCode, new CsCallback<RegistrationOrUnlockResponse>() {
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

    private void handlePasswordChangePin() {
        if (mLockTypeState == NEW_CODE) {
            mLockTypeState = LockTypeState.REPEAT_CODE;
            mTvTitle.setText(R.string.again_pin_register_pin_activity);
            mTvTitle.sendAccessibilityEvent(AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED);
            setMainViewContentDescription(mLockTypeState);
            mTvDescription.setVisibility(View.INVISIBLE);
            mCode = mPinBuilder.toString();
            mPinBuilder.setLength(0);
            checkPinPoint();
        } else if (mLockTypeState == LockTypeState.REPEAT_CODE) {
            mLockTypeState = NEW_CODE;
            mRepeatCode = mPinBuilder.toString();
            mPinBuilder.setLength(0);
            checkPinPoint();
            checkPasswordChangePinResult();
        }
    }

    private void handlePasswordCheckPin() {
        if (mLockTypeState == NEW_CODE) {
            mOldCode = mPinBuilder.toString();
            mPinBuilder.setLength(0);
            checkPinPoint();
            mFragmentCallback.changeFragmentToResult();
            ((LockerUIImpl) LockerUI.getInstance()).getLockerUIManager().getLocker().unlock(mOldCode, new CsCallback<RegistrationOrUnlockResponse>() {
                @Override
                public void success(RegistrationOrUnlockResponse registrationOrUnlockResponse, Response response) {
                    ((LockerUIImpl) LockerUI.getInstance()).getLockerUIManager().setPassword(mOldCode);
                    mOldCode = null;
                    MainActivity.onPasswordCheckSuccess(registrationOrUnlockResponse, cz.csas.cscore.locker.LockType.PIN);
                }

                @Override
                public void failure(CsSDKError error) {
                    if (LockerUIErrorHandler.handleError(mFragmentCallback, error))
                        MainActivity.onPasswordCheckFailure();
                }
            });
        }
    }

    private void checkPasswordChangePinResult() {
        if (mOldCode == null)
            mOldCode = ((LockerUIImpl) LockerUI.getInstance()).getLockerUIManager().getPassword();
        ((LockerUIImpl) LockerUI.getInstance()).getLockerUIManager().setPassword(null);

        if (mCode != null && mRepeatCode != null && mCode.equals(mRepeatCode)) {
            ((LockerUIImpl) LockerUI.getInstance()).getLockerUIManager().getLocker().changePassword(mOldCode, new Password(cz.csas.cscore.locker.LockType.PIN, mCode, mCode.length()), new CsCallback<PasswordResponse>() {
                        @Override
                        public void success(PasswordResponse passwordResponse, Response response) {
                            MainActivity.onPasswordChangeSuccess(passwordResponse, cz.csas.cscore.locker.LockType.PIN);
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
            mTvTitle.setText(R.string.new_pin_register_pin_activity);
            mTvDescription.setVisibility(View.VISIBLE);
            mTvDescription.setText(R.string.wrong_pin_description_register_pin_activity);
            mTvDescription.sendAccessibilityEvent(AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED);
            setMainViewContentDescription(mLockTypeState);
        }
    }

    private void setMainViewContentDescription(LockTypeState lockTypeState) {
        switch (lockTypeState) {
            case REPEAT_CODE:
                if (mRlPinParent != null)
                    mRlPinParent.setContentDescription(getString(R.string.pin_fragment_content_repeat_description));
                else
                    mLlPinParent.setContentDescription(getString(R.string.pin_fragment_content_repeat_description));
                break;
            case NEW_CODE:
                if (mRlPinParent != null)
                    mRlPinParent.setContentDescription(getString(R.string.pin_fragment_content_description));
                else
                    mLlPinParent.setContentDescription(getString(R.string.pin_fragment_content_description));
                break;
        }
    }

    private void readFragmentDescription() {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if (mRlPinParent != null)
                    mRlPinParent.sendAccessibilityEvent(AccessibilityEvent.TYPE_VIEW_FOCUSED);
                else
                    mLlPinParent.sendAccessibilityEvent(AccessibilityEvent.TYPE_VIEW_FOCUSED);
            }
        }, 1000);
    }
}

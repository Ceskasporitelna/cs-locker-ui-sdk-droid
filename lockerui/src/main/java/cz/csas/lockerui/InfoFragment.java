package cz.csas.lockerui;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Fragment;
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
import cz.csas.cscore.CoreSDK;
import cz.csas.cscore.client.rest.CsCallback;
import cz.csas.cscore.client.rest.client.Response;
import cz.csas.cscore.error.CsSDKError;
import cz.csas.cscore.locker.LockType;
import cz.csas.cscore.locker.LockerStatus;
import cz.csas.cscore.locker.State;
import cz.csas.cscore.logger.LogLevel;
import cz.csas.cscore.logger.LogManager;
import cz.csas.cscore.utils.StringUtils;
import cz.csas.lockerui.components.MaterialDialog;
import cz.csas.lockerui.utils.ColorUtils;
import cz.csas.lockerui.utils.TypefaceUtils;

import static cz.csas.lockerui.LockerUI.LOCKER_UI_MODULE;

/**
 * The type Info fragment.
 *
 * @author Jan Hauser <jan.hauser@applifting.cz>
 * @since 01 /12/15.
 */
@SuppressLint("ValidFragment")
public class InfoFragment extends Fragment {

    private LogManager mLogManager;
    private RelativeLayout mRlInfoParent;
    private Button mBtnUnregister;
    private Button mBtnChangeLock;
    private Button mBtnChangeLockBackground;
    private TextView mTvInfoTitle;
    private TextView mTvInfoDescription;
    private ImageView mIvInfo;
    private FragmentCallback mFragmentCallback;
    private MaterialDialog mUnregisterDialog;
    private LockType mLockType;

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
        View rootView = inflater.inflate(R.layout.info_fragment, viewGroup);
        init(rootView);
    }

    private void init(View rootView) {
        // init all components
        mBtnUnregister = (Button) rootView.findViewById(R.id.btn_unregister_info_activity);
        mBtnChangeLock = (Button) rootView.findViewById(R.id.btn_change_info_activity);
        mBtnChangeLockBackground = (Button) rootView.findViewById(R.id.btn_change_info_background_activity);
        mTvInfoDescription = (TextView) rootView.findViewById(R.id.tv_description_info_activity);
        mTvInfoTitle = (TextView) rootView.findViewById(R.id.tv_info_activity);
        mIvInfo = (ImageView) rootView.findViewById(R.id.iv_info_fragment);
        mRlInfoParent = (RelativeLayout) rootView.findViewById(R.id.rl_info_parent);
        mUnregisterDialog = new MaterialDialog(getActivity());

        mBtnChangeLock.setTypeface(TypefaceUtils.getRobotoBlack(getActivity()));
        mBtnUnregister.setTypeface(TypefaceUtils.getRobotoBlack(getActivity()));
        mTvInfoTitle.setTypeface(TypefaceUtils.getRobotoBlack(getActivity()));
        mTvInfoDescription.setTypeface(TypefaceUtils.getRobotoRegular(getActivity()));

        // set ui
        mLogManager = CoreSDK.getInstance().getLogger();
        int lightColor = LockerUI.mLockerUIManager.getLightColor();
        mIvInfo.setImageDrawable(ColorUtils.createBackground(LockerUI.mLockerUIManager.getMainColor(), R.drawable.settings_shape, R.drawable.settings_screen, getActivity()));
        ColorUtils.colorizeButton(mBtnChangeLock, lightColor, Color.WHITE);
        ColorUtils.colorizeButtonBackground(mBtnChangeLockBackground, lightColor);
        mBtnChangeLock.setTextColor(LockerUI.mLockerUIManager.getDarkColor());

        State state = LockerUI.getInstance().getLocker().getStatus().getState();
        mLockType = LockerUI.getInstance().getLocker().getStatus().getLockType();
        setLockTypeText();

        mTvInfoTitle.setText(R.string.info_title_info_activity);

        if (state != State.USER_UNLOCKED)
            mFragmentCallback.changeFragmentToLockerUI();

        mBtnUnregister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mUnregisterDialog != null) {
                    mUnregisterDialog.setTitle(R.string.dialog_unregister_title_info_activity)
                            .setMessage(setCustomizedMessage())
                            .setPositiveButton(R.string.dialog_unregister_cancel_info_activity, new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    ((LockerUIImpl) LockerUI.getInstance()).getLockerUIManager().getLocker().unregister(new CsCallback<LockerStatus>() {
                                        @Override
                                        public void success(LockerStatus lockerStatus, Response response) {
                                            MainActivity.onUnregistrationSuccess();
                                        }

                                        @Override
                                        public void failure(CsSDKError error) {

                                        }
                                    });
                                }
                            })
                            .setNegativeButton(R.string.dialog_unregister_back_info_activity, new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    mUnregisterDialog.dismiss();
                                }
                            })
                            .setCanceledOnTouchOutside(true)
                            .show();
                }
            }
        });

        mBtnChangeLock.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mLogManager.log(StringUtils.logLine(LOCKER_UI_MODULE, "DisplayInfoFlowFinished", "Display info flow finished via change security flow."), LogLevel.DEBUG);
                mLogManager.log(StringUtils.logLine(LOCKER_UI_MODULE, "ChangeSecurityFlowFinished", "Start change security flow."), LogLevel.DEBUG);
                ((LockerUIImpl) LockerUI.getInstance()).getLockerUIManager().setChangingPassword(true);
                if (mLockType == LockType.PIN)
                    mFragmentCallback.changeFragmentToPin();
                else if (mLockType == LockType.NONE)
                    mFragmentCallback.changeFragmentToRegister();
                else if (mLockType == LockType.GESTURE)
                    mFragmentCallback.changeFragmentToGesture();
                else if (mLockType == LockType.FINGERPRINT)
                    mFragmentCallback.changeFragmentToFingerprint();
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        readFragmentDescription();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mUnregisterDialog != null)
            mUnregisterDialog.dismiss();
        mUnregisterDialog = null;
    }

    private String setCustomizedMessage() {
        return ((LockerUIImpl) LockerUI.getInstance()).getLockerUIManager().getDisplayInfoOptions().getUnregisterPromptText();
    }

    private void setLockTypeText() {
        if (mLockType == LockType.GESTURE)
            mTvInfoDescription.setText(getString(R.string.actual_lock_type_info_activity, getString(R.string.lock_type_gesture)));
        else if (mLockType == LockType.PIN)
            mTvInfoDescription.setText(getString(R.string.actual_lock_type_info_activity, getString(R.string.lock_type_pin)));
        else if (mLockType == LockType.FINGERPRINT)
            mTvInfoDescription.setText(getString(R.string.actual_lock_type_info_activity, getString(R.string.lock_type_fingerprint)));
        else
            mTvInfoDescription.setText(getString(R.string.actual_lock_type_info_activity, getString(R.string.lock_type_none)));
    }

    private void readFragmentDescription() {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                mRlInfoParent.sendAccessibilityEvent(AccessibilityEvent.TYPE_VIEW_FOCUSED);
            }
        }, 1000);
    }
}

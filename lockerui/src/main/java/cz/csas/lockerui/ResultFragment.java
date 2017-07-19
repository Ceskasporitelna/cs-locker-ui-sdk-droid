package cz.csas.lockerui;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Fragment;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.accessibility.AccessibilityEvent;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.view.animation.Transformation;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import csas.cz.lockerui.R;
import cz.csas.cscore.CoreSDK;
import cz.csas.cscore.client.rest.CallbackBasic;
import cz.csas.cscore.client.rest.CsCallback;
import cz.csas.cscore.client.rest.CsRestError;
import cz.csas.cscore.client.rest.client.Response;
import cz.csas.cscore.error.CsLockerError;
import cz.csas.cscore.error.CsSDKError;
import cz.csas.cscore.locker.LockerRegistrationProcess;
import cz.csas.cscore.locker.RegistrationOrUnlockResponse;
import cz.csas.cscore.locker.State;
import cz.csas.cscore.logger.LogLevel;
import cz.csas.cscore.logger.LogManager;
import cz.csas.cscore.utils.StringUtils;
import cz.csas.lockerui.error.LockerUIErrorHandler;
import cz.csas.lockerui.utils.ColorUtils;
import cz.csas.lockerui.utils.NumberUtils;
import cz.csas.lockerui.utils.ScreenUtils;
import cz.csas.lockerui.utils.TypefaceUtils;

import static cz.csas.lockerui.LockerUI.LOCKER_UI_MODULE;

/**
 * @author Jan Hauser <jan.hauser@applifting.cz>
 * @since 01/12/15.
 */
@SuppressLint("ValidFragment")
public class ResultFragment extends Fragment {

    private RelativeLayout mRlResultParent;
    private RelativeLayout mRlSafelock;
    private ImageView mIvSafelockBackground;
    private ImageView mIvSafelockSpinner;
    private ImageView mIvResult;
    private RelativeLayout mRlProgress;
    private RelativeLayout mRlResult;
    private Button mBtnNewRegistration;
    private TextView mTvFailure;
    private TextView mTvFailureDescription;
    private TextView mTvProgress;
    private TextView mTvProgressDescription;
    private FragmentCallback mFragmentCallback;
    private LogManager mLogManager;
    private boolean mOrientationChanged = true;
    private boolean mHasError = false;

    private float fromDegrees = 0;
    private float toDegrees = 0;


    @SuppressWarnings("deprecation")
    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        mFragmentCallback = (FragmentCallback) activity;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mLogManager = CoreSDK.getInstance().getLogger();
        RelativeLayout group = new RelativeLayout(getActivity());
        populateViewForOrientation(inflater, group);
        return group;
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        mOrientationChanged = false;
        mIvSafelockSpinner.clearAnimation();
        super.onConfigurationChanged(newConfig);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                populateViewForOrientation(LayoutInflater.from(getActivity()), (ViewGroup) getView());
            }
        }, 1000);
    }

    private void populateViewForOrientation(LayoutInflater inflater, ViewGroup viewGroup) {
        // orientation change delay due to animation clearing cause null pointer exception on viewGroup
        if (viewGroup != null) {
            viewGroup.removeAllViewsInLayout();
            View rootView = inflater.inflate(R.layout.result_fragment, viewGroup);
            init(rootView);
            mOrientationChanged = true;
            setAnimation();
        }
    }


    private void init(View rootView) {
        mRlSafelock = (RelativeLayout) rootView.findViewById(R.id.rl_safelock_result_activity);
        mRlProgress = (RelativeLayout) rootView.findViewById(R.id.rl_progress_result_activity);
        mRlResult = (RelativeLayout) rootView.findViewById(R.id.rl_result_result_activity);
        mIvSafelockBackground = (ImageView) rootView.findViewById(R.id.iv_safelock_background_result_activity);
        mIvSafelockSpinner = (ImageView) rootView.findViewById(R.id.iv_safelock_spinner_result_activity);
        mIvResult = (ImageView) rootView.findViewById(R.id.iv_result_activity);
        mBtnNewRegistration = (Button) rootView.findViewById(R.id.btn_new_registration_result_activity);
        mTvFailure = (TextView) rootView.findViewById(R.id.tv_failure_result_activity);
        mTvFailureDescription = (TextView) rootView.findViewById(R.id.tv_failure_description_result_activity);
        mTvProgress = (TextView) rootView.findViewById(R.id.tv_progress_result_activity);
        mTvProgressDescription = (TextView) rootView.findViewById(R.id.tv_progress_description_result_activity);
        mRlResultParent = (RelativeLayout) rootView.findViewById(R.id.rl_result_parent);

        mTvProgress.setTypeface(TypefaceUtils.getRobotoBlack(getActivity()));
        mTvProgressDescription.setTypeface(TypefaceUtils.getRobotoRegular(getActivity()));
        mTvFailure.setTypeface(TypefaceUtils.getRobotoBlack(getActivity()));
        mTvFailureDescription.setTypeface(TypefaceUtils.getRobotoRegular(getActivity()));
        mBtnNewRegistration.setTypeface(TypefaceUtils.getRobotoBlack(getActivity()));

        int color = LockerUI.mLockerUIManager.getMainColor();
        mIvSafelockSpinner.setImageDrawable(ColorUtils.createBackground(color, R.drawable.safelock_spinner_shape, R.drawable.safelock_spinner_screen, getActivity()));
        mIvSafelockBackground.setImageDrawable(ColorUtils.createBackground(color, R.drawable.safelock_background_shape, R.drawable.safelock_background_screen, getActivity()));
        mIvResult.setImageDrawable(ColorUtils.createBackground(color, R.drawable.lock_broken_shape, R.drawable.lock_broken_screen, getActivity()));

        if (ScreenUtils.checkScreenHeight(getActivity(), 600)) {
            RelativeLayout.LayoutParams lp = (RelativeLayout.LayoutParams) mRlSafelock.getLayoutParams();
            int margin = Math.round(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 20, getResources().getDisplayMetrics()));
            lp.setMargins(0, margin, 0, margin);
            mRlSafelock.setLayoutParams(lp);
        }

        mBtnNewRegistration.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
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
        });

        State state = ((LockerUIImpl) LockerUI.getInstance()).getLockerUIManager().getLocker().getStatus().getState();

        mTvProgress.setText(R.string.spinlock_result_activity);
        if (state == State.USER_LOCKED)
            mTvProgressDescription.setText(R.string.unlock_spinlock_result_activity);
        else if (state == State.USER_UNLOCKED)
            mTvProgressDescription.setText(R.string.check_spinlock_result_activity);
        else
            mTvProgressDescription.setText(R.string.registration_spinlock_result_activity);


        Bundle bundle = this.getArguments();
        if (bundle != null && bundle.get(Constants.ERROR_KIND_EXTRA) != null) {
            handleError(bundle.getString(Constants.ERROR_KIND_EXTRA));
            mHasError = true;
        }

        setMainViewContentDescription();
    }

    @Override
    public void onResume() {
        super.onResume();
        readFragmentDescription();
    }

    public void onUnlockFailed(CsSDKError error) {
        mTvFailure.setText(R.string.authorization_failed_result_activity);
        if (error != null && error.getCause() instanceof CsLockerError && ((CsLockerError) error.getCause()).getKind() == CsLockerError.Kind.OFFLINE_VERIFICATION)
            mTvFailureDescription.setText(R.string.authorization_failed_unregistered_description_result_activity);
        else
            mTvFailureDescription.setText(R.string.authorization_failed_description_result_activity);
        stopProgress();
    }

    public void onRegistrationFailed() {
        mTvFailure.setText(R.string.registration_failed_result_activity);
        mTvFailureDescription.setText(R.string.registration_failed_description_result_activity);
        stopProgress();
    }

    private void stopProgress() {
        mIvSafelockSpinner.setAnimation(null);
        mRlProgress.setVisibility(View.INVISIBLE);
        mRlResult.setVisibility(View.VISIBLE);
    }

    private void setAnimation() {
        toDegrees = NumberUtils.generateRandomAngle();
        RotateAnimation anim = new RotateAnimation(fromDegrees, toDegrees, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f) {
            protected void applyTransformation(float interpolatedTime, Transformation t) {
                fromDegrees = fromDegrees + (toDegrees - fromDegrees) * interpolatedTime;
                super.applyTransformation(interpolatedTime, t);
            }

            ;
        };
        anim.setInterpolator(new LinearInterpolator());
        anim.setDuration(1000);
        anim.setFillAfter(true);
        anim.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                if (mOrientationChanged)
                    setAnimation();
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        mIvSafelockSpinner.startAnimation(anim);
    }

    private void handleError(String kind) {
        if (kind.equals(CsRestError.Kind.NETWORK.toString())) {
            mTvFailure.setText(R.string.network_error_title);
            mTvFailureDescription.setText(R.string.network_error_message);
        } else {
            mTvFailure.setText(R.string.different_error_title);
            mTvFailureDescription.setText(R.string.different_error_message);
        }
        mBtnNewRegistration.setText(R.string.error_repeat);
        setErrorButton();
        stopProgress();
    }

    private void setErrorButton() {
        mBtnNewRegistration.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (hasDisplayInfoInBackStack()) {
                    mLogManager.log(StringUtils.logLine(LOCKER_UI_MODULE, "ChangeSecurityFlowFinished", "Password change failed."), LogLevel.DEBUG);
                    mFragmentCallback.changeFragmentToDisplayInfo();
                } else
                    mFragmentCallback.changeFragmentToLockerUI();
            }
        });
    }

    private boolean hasDisplayInfoInBackStack() {
        for (int entry = 0; entry < getFragmentManager().getBackStackEntryCount(); entry++) {
            if (getFragmentManager().getBackStackEntryAt(entry).getName().equals(Constants.FRAGMENT_INFO))
                return true;
        }
        return false;
    }

    private void setMainViewContentDescription() {
        if (mHasError)
            mRlResultParent.setContentDescription(getString(R.string.result_failure_fragment_content_description));
        else
            mRlResultParent.setContentDescription(getString(R.string.result_fragment_content_description));
    }

    private void readFragmentDescription() {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                mRlResultParent.sendAccessibilityEvent(AccessibilityEvent.TYPE_VIEW_FOCUSED);
            }
        }, 1000);
    }
}

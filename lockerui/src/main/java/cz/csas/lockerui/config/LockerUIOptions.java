package cz.csas.lockerui.config;

import android.graphics.drawable.Drawable;

import java.util.List;

import cz.csas.cscore.locker.CsNavBarColor;
import cz.csas.lockerui.error.CsLockerUIError;

/**
 * The type Locker ui options.
 *
 * @author Jan Hauser <jan.hauser@applifting.cz>
 * @since 24 /11/15.
 */
public class LockerUIOptions {

    private String mAppName;
    private List<LockType> mAllowedLockTypes;
    private Drawable mBackgrounImage;
    private Integer mCustomColor;
    private CsNavBarColor mCsNavBarColor;
    private Integer mCustomNavBarColor;
    private ShowLogo mShowLogo = ShowLogo.ALWAYS;

    /**
     * Instantiates a new Locker ui options. Allows you to customize LockerUI.
     *
     * @param appName          set your app name to be shown
     * @param allowedLockTypes set what kind of LockType you want user to decide of.
     * @param backgrounImage   set your background image layer
     * @param customColor      set color laye as well
     * @param csNavBarColor    the nav bar color
     */
    public LockerUIOptions(String appName, List<LockType> allowedLockTypes, Drawable backgrounImage, Integer customColor, CsNavBarColor csNavBarColor, ShowLogo showLogo) {
        if (appName == null)
            throw new CsLockerUIError(CsLockerUIError.Kind.BAD_APP_NAME);
        if (allowedLockTypes == null)
            throw new CsLockerUIError(CsLockerUIError.Kind.BAD_ALLOWED_LOCK_TYPE);
        mAppName = appName;
        mAllowedLockTypes = allowedLockTypes;
        mBackgrounImage = backgrounImage;
        mCustomColor = customColor;
        mCsNavBarColor = csNavBarColor;
        mShowLogo = showLogo;
    }

    /**
     * Instantiates a new Locker ui options. Allows you to customize LockerUI.
     *
     * @param appName           set your app name to be shown
     * @param allowedLockTypes  set what kind of LockType you want user to decide of.
     * @param backgroundImage    set your background image layer
     * @param customColor       set color laye as well
     * @param customNavBarColor set custom nav bar color
     */
    public LockerUIOptions(String appName, List<LockType> allowedLockTypes, Drawable backgroundImage, Integer customColor, Integer customNavBarColor, ShowLogo showLogo) {
        if (appName == null)
            throw new CsLockerUIError(CsLockerUIError.Kind.BAD_APP_NAME);
        if (allowedLockTypes == null)
            throw new CsLockerUIError(CsLockerUIError.Kind.BAD_ALLOWED_LOCK_TYPE);
        mAppName = appName;
        mAllowedLockTypes = allowedLockTypes;
        mBackgrounImage = backgroundImage;
        mCustomColor = customColor;
        mShowLogo = showLogo;
        mCustomNavBarColor = customNavBarColor;
    }

    /**
     * Gets app name.
     *
     * @return the app name
     */
    public String getAppName() {
        return mAppName;
    }

    /**
     * Gets allowed lock types.
     *
     * @return the allowed lock types
     */
    public List<LockType> getAllowedLockTypes() {
        return mAllowedLockTypes;
    }

    /**
     * Gets backgroun image.
     *
     * @return the backgroun image
     */
    public Drawable getBackgrounImage() {
        return mBackgrounImage;
    }

    /**
     * Gets custom color.
     *
     * @return the custom color
     */
    public Integer getCustomColor() {
        return mCustomColor;
    }

    /**
     * Gets nav bar color.
     *
     * @return the nav bar color
     */
    public CsNavBarColor getNavBarColor() {
        return mCsNavBarColor;
    }

    /**
     * Gets custom nav bar color
     *
     * @return the custom nav bar color
     */
    public Integer getCustomNavBarColor() {
        return mCustomNavBarColor;
    }

    /**
     * Gets show logo.
     *
     * @return the show logo
     */
    public ShowLogo getShowLogo() {
        return mShowLogo;
    }

    /**
     * The type Builder.
     */
    public static class Builder {

        private String mAppName;

        private List<LockType> mAllowedLockTypes;

        private Drawable mBackgrounImage;

        private Integer mCustomColor;

        private CsNavBarColor mCsNavBarColor;

        private Integer mCustomNavBarColor;

        private ShowLogo mShowLogo;


        /**
         * Sets app name.
         *
         * @param mAppName the m app name
         * @return the app name
         */
        public Builder setAppName(String mAppName) {
            this.mAppName = mAppName;
            return this;
        }

        /**
         * Sets allowed lock types.
         *
         * @param allowedLockTypes the allowed lock types
         * @return the allowed lock types
         */
        public Builder setAllowedLockTypes(List<LockType> allowedLockTypes) {
            mAllowedLockTypes = allowedLockTypes;
            return this;
        }

        /**
         * Sets backgroun image.
         *
         * @param backgrounImage the backgroun image
         * @return the backgroun image
         */
        public Builder setBackgrounImage(Drawable backgrounImage) {
            mBackgrounImage = backgrounImage;
            return this;
        }

        /**
         * Sets custom color.
         *
         * @param customColor the custom color
         * @return the custom color
         */
        public Builder setCustomColor(Integer customColor) {
            mCustomColor = customColor;
            return this;
        }

        /**
         * Sets nav bar color.
         *
         * @param csNavBarColor the nav bar color
         * @return the nav bar color
         */
        public Builder setNavBarColor(CsNavBarColor csNavBarColor) {
            mCsNavBarColor = csNavBarColor;
            return this;
        }

        /**
         * Sets nav bar color.
         *
         * @param customNavBarColor the custom nav bar color
         * @return builder
         */
        public Builder setNavBarColor(int customNavBarColor) {
            mCustomNavBarColor = customNavBarColor;
            return this;
        }


        public Builder setShowLogo(ShowLogo showLogo) {
            mShowLogo = showLogo;
            return this;
        }

        /**
         * Create locker ui options.
         *
         * @return the locker ui options
         */
        public LockerUIOptions create() {
            return mCustomNavBarColor != null ?
                    new LockerUIOptions(mAppName, mAllowedLockTypes, mBackgrounImage, mCustomColor, mCustomNavBarColor, mShowLogo) :
                    new LockerUIOptions(mAppName, mAllowedLockTypes, mBackgrounImage, mCustomColor, mCsNavBarColor, mShowLogo);
        }
    }
}

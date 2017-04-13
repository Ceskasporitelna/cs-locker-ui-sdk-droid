package cz.csas.lockerui.config;

/**
 * The type Auth flow options.
 *
 * @author Jan Hauser <jan.hauser@applifting.cz>
 * @since 24 /11/15.
 */
public class AuthFlowOptions {

    private SkipStatusScreen mSkipStatusScreen;
    private String mRegistrationScreenText;
    private String mLockedScreenText;
    private boolean mOfflineAuthEnabled = false;

    /**
     * Instantiates a new Auth flow options. Allows you to customize authentication flow. Note that
     * this constructor does not include offlineAuthEnabled parameter of AuthFlowOptions.
     *
     * @param skipStatusScreen       you can decide if you want to see status screens or not
     * @param registrationScreenText you can set registration screen text
     * @param lockedScreenText       you can set locked screen text
     */
    public AuthFlowOptions(SkipStatusScreen skipStatusScreen, String registrationScreenText, String lockedScreenText) {
        mSkipStatusScreen = skipStatusScreen;
        mRegistrationScreenText = registrationScreenText;
        mLockedScreenText = lockedScreenText;
    }

    /**
     * Instantiates a new Auth flow options. Allows you to customize authentication flow:
     *
     * @param skipStatusScreen       you can decide if you want to see status screens or not
     * @param registrationScreenText you can set registration screen text
     * @param lockedScreenText       you can set locked screen text
     * @param offlineAuthEnabled     you can enable the offline auth. Locker has to be also properly
     *                               set using {@link cz.csas.cscore.locker.LockerConfig}
     */
    public AuthFlowOptions(SkipStatusScreen skipStatusScreen, String registrationScreenText, String lockedScreenText, boolean offlineAuthEnabled) {
        mSkipStatusScreen = skipStatusScreen;
        mRegistrationScreenText = registrationScreenText;
        mLockedScreenText = lockedScreenText;
        mOfflineAuthEnabled = offlineAuthEnabled;
    }

    /**
     * Gets skip status screen.
     *
     * @return the skip status screen
     */
    public SkipStatusScreen getSkipStatusScreen() {
        return mSkipStatusScreen;
    }

    /**
     * Gets registration screen text.
     *
     * @return the registration screen text
     */
    public String getRegistrationScreenText() {
        return mRegistrationScreenText;
    }

    /**
     * Gets locked screen text.
     *
     * @return the locked screen text
     */
    public String getLockedScreenText() {
        return mLockedScreenText;
    }

    /**
     * Is offline auth enabled boolean.
     *
     * @return the boolean
     */
    public boolean isOfflineAuthEnabled() {
        return mOfflineAuthEnabled;
    }

    /**
     * Sets skip status screen.
     *
     * @param skipStatusScreen the skip status screen
     */
    public void setSkipStatusScreen(SkipStatusScreen skipStatusScreen) {
        mSkipStatusScreen = skipStatusScreen;
    }

    /**
     * Sets registration screen text.
     *
     * @param registrationScreenText the registration screen text
     */
    public void setRegistrationScreenText(String registrationScreenText) {
        mRegistrationScreenText = registrationScreenText;
    }

    /**
     * Sets locked screen text.
     *
     * @param lockedScreenText the locked screen text
     */
    public void setLockedScreenText(String lockedScreenText) {
        mLockedScreenText = lockedScreenText;
    }

    /**
     * Sets offline auth enabled.
     *
     * @param offlineAuthEnabled the offline auth enabled
     */
    public void setOfflineAuthEnabled(boolean offlineAuthEnabled) {
        mOfflineAuthEnabled = offlineAuthEnabled;
    }

    /**
     * The type Builder.
     */
    public static class Builder {

        private SkipStatusScreen mSkipStatusScreen;
        private String mRegistrationScreenText;
        private String mLockedScreenText;
        private boolean mOfflineAuthEnabled;

        /**
         * Set skip status screen.
         * You can decide if you want to see status screens or not
         *
         * @param skipStatusScreen the skip status screen
         * @return the skip status screen
         */
        public Builder setSkipStatusScreen(SkipStatusScreen skipStatusScreen) {
            mSkipStatusScreen = skipStatusScreen;
            return this;
        }

        /**
         * Set registration screen text.
         * You can set registration screen text
         *
         * @param registrationScreenText the registration screen text
         * @return the registration screen text
         */
        public Builder setRegistrationScreenText(String registrationScreenText) {
            mRegistrationScreenText = registrationScreenText;
            return this;
        }

        /**
         * Set locked screen text.
         * You can set locked screen text
         *
         * @param lockedScreenText the locked screen text
         * @return the locked screen text
         */
        public Builder setLockedScreenText(String lockedScreenText) {
            mLockedScreenText = lockedScreenText;
            return this;
        }

        /**
         * Set offline auth enabled.
         * You can enable the offline auth
         * Locker has to be also properly set using {@link cz.csas.cscore.locker.LockerConfig}
         *
         * @return the offline auth enabled
         */
        public Builder setOfflineAuthEnabled() {
            mOfflineAuthEnabled = true;
            return this;
        }

        /**
         * Create auth flow options.
         *
         * @return the auth flow options
         */
        public AuthFlowOptions create() {
            return new AuthFlowOptions(mSkipStatusScreen, mRegistrationScreenText, mLockedScreenText, mOfflineAuthEnabled);
        }

    }

}

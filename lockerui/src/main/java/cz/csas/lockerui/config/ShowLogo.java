package cz.csas.lockerui.config;

/**
 * The enum Show logo.
 *
 * @author Jan Hauser <jan.hauser@applifting.cz>
 * @since 09 /10/16.
 */
public enum ShowLogo {

    /**
     * Logo is displayed on all screen.
     */
    ALWAYS,

    /**
     * Logo is displayed everywhere besides WebView registration screen
     */
    EXCEPT_REGISTRATION,

    /**
     * Never show logo. Logo is never displayed
     */
    NEVER;
}

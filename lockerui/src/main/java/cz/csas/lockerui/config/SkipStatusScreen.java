package cz.csas.lockerui.config;

/**
 * The enum Skip status screen.
 *
 * @author Jan Hauser <jan.hauser@applifting.cz>
 * @since 24 /11/15.
 */
public enum SkipStatusScreen {

    /**
     * Always skip status screen.
     */
    ALWAYS,

    /**
     * When locked skip status screen.
     */
    WHEN_LOCKED,

    /**
     * When not registered skip status screen.
     */
    WHEN_NOT_REGISTERED;
}

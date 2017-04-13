package cz.csas.lockerui;

import cz.csas.cscore.client.rest.CsRestError;

/**
 * The interface Fragment callback.
 *
 * @author Jan Hauser <jan.hauser@applifting.cz>
 * @since 01 /12/15.
 */
public interface FragmentCallback {

    /**
     * Change fragment to locker ui.
     */
    public void changeFragmentToLockerUI();

    /**
     * Change fragment to register.
     */
    public void changeFragmentToRegister();

    /**
     * Change fragment to pin.
     */
    public void changeFragmentToPin();

    /**
     * Change fragment to fingerprint.
     */
    public void changeFragmentToFingerprint();

    /**
     * Change fragment to gesture.
     */
    public void changeFragmentToGesture();

    /**
     * Change fragment to result.
     */
    public void changeFragmentToResult();

    /**
     * Change fragment to result.
     *
     * @param kind the kind
     */
    public void changeFragmentToResult(CsRestError.Kind kind);

    /**
     * Change fragment to display info.
     */
    public void changeFragmentToDisplayInfo();

    /**
     * Clear fragment stack.
     */
    public void clearFragment();
}

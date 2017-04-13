package cz.csas.lockerui.error;

import cz.csas.cscore.client.rest.CsRestError;
import cz.csas.cscore.error.CsLockerError;
import cz.csas.lockerui.FragmentCallback;

/**
 * The type Locker ui error handler.
 *
 * @author Jan Hauser <hauseja3@gmail.com>
 * @since 14 /12/15.
 */
public class LockerUIErrorHandler {


    /**
     * Handle error boolean.
     *
     * @param fragmentCallback the fragment callback
     * @param cause            the cause
     * @return the boolean
     */
    public static boolean handleError(FragmentCallback fragmentCallback, CsRestError cause) {
        if (
                cause.getKind() == CsRestError.Kind.NETWORK ||
                        (cause.getKind() == CsRestError.Kind.UNEXPECTED
                                && !(cause.getCause() instanceof CsLockerError
                                && ((CsLockerError) cause.getCause()).getKind() == CsLockerError.Kind.OFFLINE_VERIFICATION)) ||
                        cause.getKind() == CsRestError.Kind.CONVERSION ||
                        //Or we get a HTTP status that is not 401 unauthorized
                        (cause.getKind() == CsRestError.Kind.HTTP &&
                                cause.getResponse() != null &&
                                cause.getResponse().getStatus() != 401)) {
            fragmentCallback.changeFragmentToResult(cause.getKind());
            return false;
        } else
            return true;
    }
}

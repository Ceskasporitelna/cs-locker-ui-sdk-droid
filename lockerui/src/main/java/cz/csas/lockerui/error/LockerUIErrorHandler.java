package cz.csas.lockerui.error;

import cz.csas.cscore.client.rest.CsRestError;
import cz.csas.cscore.error.CsLockerError;
import cz.csas.cscore.error.CsSDKError;
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
     * @param error            the error
     * @return the boolean
     */
    public static boolean handleError(FragmentCallback fragmentCallback, CsSDKError error) {
        if (error instanceof CsRestError && checkRestError((CsRestError) error)) {
            fragmentCallback.changeFragmentToResult(((CsRestError) error).getKind());
            return false;
        } else
            return true;
    }

    private static boolean checkRestError(CsRestError error) {
        return error.getKind() == CsRestError.Kind.NETWORK ||
                (error.getKind() == CsRestError.Kind.UNEXPECTED
                        && !(error.getCause() instanceof CsLockerError
                        && ((CsLockerError) error.getCause()).getKind() == CsLockerError.Kind.OFFLINE_VERIFICATION)) ||
                error.getKind() == CsRestError.Kind.CONVERSION ||
                //Or we get a HTTP status that is not 401 unauthorized
                (error.getKind() == CsRestError.Kind.HTTP &&
                        error.getResponse() != null &&
                        error.getResponse().getStatus() != 401);
    }
}

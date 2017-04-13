package cz.csas.lockerui.config;

import cz.csas.lockerui.error.CsLockerUIError;

/**
 * The type Pin lock.
 *
 * @author Jan Hauser <jan.hauser@applifting.cz>
 * @since 24 /11/15.
 */
public class PinLock implements LockType {

    private final cz.csas.cscore.locker.LockType LOCK_TYPE_PIN = cz.csas.cscore.locker.LockType.PIN;

    private Integer mPinLength;

    /**
     * Instantiates a new Pin lock.
     *
     * @param mPinLength the m pin length
     */
    public PinLock(Integer mPinLength) {
        if (mPinLength == null || mPinLength < 4 || mPinLength > 8)
            throw new CsLockerUIError(CsLockerUIError.Kind.BAD_PIN_LENGTH);
        this.mPinLength = mPinLength;
    }

    /**
     * Gets pin length.
     *
     * @return the pin length
     */
    public int getPinLength() {
        return mPinLength;
    }

    @Override
    public cz.csas.cscore.locker.LockType getLockType() {
        return LOCK_TYPE_PIN;
    }
}

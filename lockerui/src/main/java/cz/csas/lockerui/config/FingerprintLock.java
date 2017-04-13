package cz.csas.lockerui.config;

/**
 * The type Fingerprint lock.
 *
 * @author Jan Hauser <hauseja3@gmail.com>
 * @since 22 /02/16.
 */
public class FingerprintLock implements LockType {

    private final cz.csas.cscore.locker.LockType LOCK_TYPE_FINGERPRINT = cz.csas.cscore.locker.LockType.FINGERPRINT;

    /**
     * Instantiates a new Fingerprint lock.
     */
    public FingerprintLock() {
    }

    @Override
    public cz.csas.cscore.locker.LockType getLockType() {
        return LOCK_TYPE_FINGERPRINT;
    }
}

package cz.csas.lockerui.config;

/**
 * The type No lock.
 *
 * @author Jan Hauser <jan.hauser@applifting.cz>
 * @since 24 /11/15.
 */
public class NoLock implements LockType {

    private final cz.csas.cscore.locker.LockType LOCK_TYPE_NONE = cz.csas.cscore.locker.LockType.NONE;

    /**
     * Instantiates a new No lock.
     */
    public NoLock() {
    }


    @Override
    public cz.csas.cscore.locker.LockType getLockType() {
        return LOCK_TYPE_NONE;
    }
}

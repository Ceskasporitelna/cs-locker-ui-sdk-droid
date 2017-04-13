package cz.csas.lockerui.config;

import cz.csas.lockerui.error.CsLockerUIError;

/**
 * The type Gesture lock.
 *
 * @author Jan Hauser <jan.hauser@applifting.cz>
 * @since 24 /11/15.
 */
public class GestureLock implements LockType {

    private final cz.csas.cscore.locker.LockType LOCK_TYPE_GESTURE = cz.csas.cscore.locker.LockType.GESTURE;
    private Integer mGridSize;
    private Integer mMinGestureLength;

    /**
     * Instantiates a new Gesture lock.
     *
     * @param gridSize         the grid size
     * @param minGestureLength the min gesture length
     */
    public GestureLock(Integer gridSize, Integer minGestureLength) {
        if (gridSize == null || gridSize < 3 || gridSize > 5)
            throw new CsLockerUIError(CsLockerUIError.Kind.BAD_GRID_SIZE);
        mGridSize = gridSize;
        if (minGestureLength == null || minGestureLength < 4)
            throw new CsLockerUIError(CsLockerUIError.Kind.BAD_MIN_GESTURE_LENGTH);
        mMinGestureLength = minGestureLength;
    }

    /**
     * Gets grid size.
     *
     * @return the grid size
     */
    public int getGridSize() {
        return mGridSize;
    }

    /**
     * Gets min gesture length.
     *
     * @return the min gesture length
     */
    public Integer getMinGestureLength() {
        return mMinGestureLength;
    }

    @Override
    public cz.csas.cscore.locker.LockType getLockType() {
        return LOCK_TYPE_GESTURE;
    }
}

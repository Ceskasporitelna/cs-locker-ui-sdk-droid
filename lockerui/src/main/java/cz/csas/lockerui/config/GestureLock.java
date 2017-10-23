package cz.csas.lockerui.config;

import cz.csas.lockerui.error.CsLockerUIError;

/**
 * The type Gesture lock.
 *
 * @author Jan Hauser <jan.hauser@applifting.cz>
 * @since 24 /11/15.
 */
public class GestureLock implements LockType {

    private final int MIN_GESTURE_LENGTH = 4;
    private final cz.csas.cscore.locker.LockType LOCK_TYPE_GESTURE = cz.csas.cscore.locker.LockType.GESTURE;
    private Integer mMinGestureLength;
    private GestureGridSize mGestureGridSize;

    /**
     * Instantiates a new Gesture lock.
     *
     * @param minGestureLength the min gesture length
     */
    public GestureLock(GestureGridSize gestureGridSize, Integer minGestureLength) {
        if (gestureGridSize == null)
            throw new CsLockerUIError(CsLockerUIError.Kind.BAD_GRID_SIZE);
        if (minGestureLength == null || minGestureLength < MIN_GESTURE_LENGTH)
            throw new CsLockerUIError(CsLockerUIError.Kind.BAD_MIN_GESTURE_LENGTH);
        mGestureGridSize = gestureGridSize;
        mMinGestureLength = minGestureLength;
    }

    /**
     * Gets row grid size.
     *
     * @return the row grid size
     */
    public int getGridSizeRow() {
        return mGestureGridSize.getRows();
    }


    /**
     * Gets row grid size.
     *
     * @return the row grid size
     */
    public int getGridSizeColumn() {
        return mGestureGridSize.getColumns();
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

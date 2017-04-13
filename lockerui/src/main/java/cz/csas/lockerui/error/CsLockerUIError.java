package cz.csas.lockerui.error;

import cz.csas.cscore.error.CsSDKError;

/**
 * The type Cs locker ui error.
 *
 * @author Jan Hauser <hauseja3@gmail.com>
 * @since 05 /10/16.
 */
public class CsLockerUIError extends CsSDKError {

    /**
     * The enum Kind.
     */
    public enum Kind {

        /**
         * The Bad allowed lock type.
         */
        BAD_ALLOWED_LOCK_TYPE("AllowedLockTypes cannot be null"),

        /**
         * The Bad app name.
         */
        BAD_APP_NAME("AppName cannot be null"),

        /**
         * The Bad grid size.
         */
        BAD_GRID_SIZE("GridSize cannot be null and its length has to be less than 6 and more than 2"),

        /**
         * The Bad locker.
         */
        BAD_LOCKER("Locker cannot be null"),

        /**
         * The Bad locker ui.
         */
        BAD_LOCKER_UI("LockerUIOptions cannot be null"),

        /**
         * The Bad min gesture length.
         */
        BAD_MIN_GESTURE_LENGTH("MinGestureLength cannot be null and its length has to be more than 3"),

        /**
         * The Bad pin length.
         */
        BAD_PIN_LENGTH("PinLength cannot be null and its length has to be less than 9 and more than 3"),

        /**
         * The Bad context.
         */
        BAD_CONTEXT("Context cannot be null");

        private String detailedMessage;

        Kind(String detailedMessage) {
            this.detailedMessage = detailedMessage;
        }
    }

    private final Kind kind;

    /**
     * Instantiates a new Cs locker ui error.
     *
     * @param kind          the kind
     * @param detailMessage the detail message
     */
    public CsLockerUIError(Kind kind, String detailMessage) {
        super(detailMessage);
        this.kind = kind;
    }

    /**
     * Instantiates a new Cs locker ui error.
     *
     * @param kind the kind
     */
    public CsLockerUIError(Kind kind) {
        super(kind.detailedMessage);
        this.kind = kind;
    }

    /**
     * Gets kind.
     *
     * @return the kind
     */
    public Kind getKind() {
        return kind;
    }

}

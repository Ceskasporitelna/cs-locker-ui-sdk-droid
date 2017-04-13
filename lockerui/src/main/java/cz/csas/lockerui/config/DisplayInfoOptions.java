package cz.csas.lockerui.config;

/**
 * The type Display info options.
 *
 * @author Jan Hauser <jan.hauser@applifting.cz>
 * @since 24 /11/15.
 */
public class DisplayInfoOptions {

    private String mUnregisterPromptText;

    /**
     * Instantiates a new Display info options. Allows you to customize display info screen.
     *
     * @param unregisterPromptText you can set unregister prompt text.
     */
    public DisplayInfoOptions(String unregisterPromptText) {
        mUnregisterPromptText = unregisterPromptText;
    }

    /**
     * Gets unregister prompt text.
     *
     * @return the unregister prompt text
     */
    public String getUnregisterPromptText() {
        return mUnregisterPromptText;
    }

    /**
     * The type Builder.
     */
    public static class Builder {

        private String mUnregisterPromptText;

        /**
         * Sets unregister prompt text.
         *
         * @param unregisterPromptText the unregister prompt text
         * @return the unregister prompt text
         */
        public Builder setUnregisterPromptText(String unregisterPromptText) {
            mUnregisterPromptText = unregisterPromptText;
            return this;
        }

        /**
         * Create display info options.
         *
         * @return the display info options
         */
        public DisplayInfoOptions create(){
            return new DisplayInfoOptions(mUnregisterPromptText);
        }
    }
}

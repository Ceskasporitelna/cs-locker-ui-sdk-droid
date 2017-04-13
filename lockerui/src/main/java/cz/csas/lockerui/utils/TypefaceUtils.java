package cz.csas.lockerui.utils;

import android.content.Context;
import android.graphics.Typeface;

/**
 * The type Typeface utils.
 *
 * @author Jan Hauser <jan.hauser@applifting.cz>
 * @since 28 /11/15.
 */
public class TypefaceUtils {

    /**
     * Gets roboto regular.
     *
     * @return the roboto regular
     */
    public static Typeface getRobotoRegular(Context context) {
        return Typeface.createFromAsset(context.getAssets(),"fonts/Roboto-Regular.ttf");
    }

    /**
     * Gets roboto black.
     *
     * @return the roboto black
     */
    public static Typeface getRobotoBlack(Context context) {
        return Typeface.createFromAsset(context.getAssets(),"fonts/Roboto-Black.ttf");
    }

    /**
     * Get roboto medium typeface.
     *
     * @return the typeface
     */
    public static Typeface getRobotoMedium(Context context){
        return Typeface.createFromAsset(context.getAssets(),"fonts/Roboto-Medium.ttf");
    }

    /**
     * Get roboto bold typeface.
     *
     * @return the typeface
     */
    public static Typeface getRobotoBold(Context context){
        return Typeface.createFromAsset(context.getAssets(),"fonts/Roboto-Bold.ttf");
    }
}

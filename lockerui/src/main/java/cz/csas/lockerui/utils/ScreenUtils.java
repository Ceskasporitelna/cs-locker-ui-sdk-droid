package cz.csas.lockerui.utils;

import android.app.Activity;
import android.content.Context;
import android.content.res.Configuration;
import android.util.DisplayMetrics;
import android.view.accessibility.AccessibilityManager;

/**
 * The type Screen utils.
 *
 * @author Jan Hauser <hauseja3@gmail.com>
 * @since 07 /06/16.
 */
public class ScreenUtils {

    /**
     * Check screen height.
     *
     * @param context     the context
     * @param checkHeight the check height
     * @return the boolean
     */
    public static boolean checkScreenHeight(Context context, int checkHeight) {
        DisplayMetrics displaymetrics = new DisplayMetrics();
        ((Activity) context).getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);
        int height = displaymetrics.heightPixels;
        return height < checkHeight;
    }

    /**
     * Is landscape mode.
     *
     * @param context the context
     * @return the boolean
     */
    public static boolean isLandscape(Context context) {
        return context.getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE;
    }

    /**
     * Check talkback availability.
     *
     * @param context the context
     * @return the boolean
     */
    public static boolean checkTalkbackAvailability(Context context) {
        AccessibilityManager am = (AccessibilityManager) context.getSystemService(Context.ACCESSIBILITY_SERVICE);
        return !(am.isEnabled() && am.isTouchExplorationEnabled());
    }
}

package cz.csas.lockerui.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.PorterDuffXfermode;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.DrawableContainer;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.StateListDrawable;
import android.os.Build;
import android.view.View;
import android.widget.Button;

/**
 * @author Jan Hauser <jan.hauser@applifting.cz>
 * @since 26/11/15.
 */
public class ColorUtils {

    public static float[] transformHslColor(float[] colorHsl, float newHue, float newSaturation, float newLightness) {
        return new float[]{colorHsl[0] + newHue, colorHsl[1] + newSaturation, colorHsl[2] + newLightness};
    }

    public static int setTransparency(int transparency, int color) {
        String colorString = Integer.toHexString(color).substring(2);
        switch (transparency) {
            case 100:
                return Color.parseColor("#00" + colorString);
            case 95:
                return Color.parseColor("#0D" + colorString);
            case 90:
                return Color.parseColor("#1A" + colorString);
            case 85:
                return Color.parseColor("#26" + colorString);
            case 80:
                return Color.parseColor("#33" + colorString);
            case 75:
                return Color.parseColor("#40" + colorString);
            case 70:
                return Color.parseColor("#4D" + colorString);
            case 65:
                return Color.parseColor("#59" + colorString);
            case 60:
                return Color.parseColor("#66" + colorString);
            case 55:
                return Color.parseColor("#73" + colorString);
            case 50:
                return Color.parseColor("#80" + colorString);
            case 45:
                return Color.parseColor("#8C" + colorString);
            case 40:
                return Color.parseColor("#99" + colorString);
            case 35:
                return Color.parseColor("#A6" + colorString);
            case 30:
                return Color.parseColor("#B3" + colorString);
            case 25:
                return Color.parseColor("#BF" + colorString);
            case 20:
                return Color.parseColor("#CC" + colorString);
            case 15:
                return Color.parseColor("#D9" + colorString);
            case 10:
                return Color.parseColor("#E6" + colorString);
            case 5:
                return Color.parseColor("#F2" + colorString);
            default:
                return Color.parseColor("#FF" + colorString);
        }
    }

    public static Drawable createBackground(Integer color, Integer drawableShapeId, Bitmap overlay, Context context) {
        Bitmap shape = null;
        if (drawableShapeId != null)
            shape = BitmapFactory.decodeResource(context.getResources(), drawableShapeId);
        int w = overlay.getWidth();
        int h = overlay.getHeight();
        Paint p = new Paint(Paint.ANTI_ALIAS_FLAG);
        Bitmap background = Bitmap.createBitmap(w, h, overlay.getConfig());
        Canvas canvas = new Canvas(background);
        // draw shape
        if (shape != null)
            canvas.drawBitmap(shape, new Matrix(), null);
        else
            canvas.drawRect(0, 0, w, h, p);
        // set color filter
        p.setColorFilter(new PorterDuffColorFilter(color, PorterDuff.Mode.SRC_IN));
        canvas.drawBitmap(background, 0, 0, p);
        p.setColorFilter(null);
        // draw overlay
        p.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SCREEN));
        canvas.drawBitmap(overlay, new Matrix(), p);
        // create and return drawable
        return new BitmapDrawable(context.getResources(), background);
    }

    public static Drawable createBackground(int color, int drawableShapeId, int drawableOverlayId, Context context) {
        Bitmap overlay = BitmapFactory.decodeResource(context.getResources(), drawableOverlayId);
        return createBackground(color, drawableShapeId, overlay, context);
    }

    public static void colorizeButton(Button button, int selected, int unselected) {
        StateListDrawable stateListDrawable = (StateListDrawable) button.getBackground();
        DrawableContainer.DrawableContainerState drawableContainerState = (DrawableContainer.DrawableContainerState) stateListDrawable.getConstantState();
        Drawable[] children = drawableContainerState.getChildren();
        GradientDrawable selectedButton = (GradientDrawable) children[0];
        selectedButton.mutate();
        selectedButton.setColor(selected);
        GradientDrawable unselectedButton = (GradientDrawable) children[1];
        unselectedButton.setColor(unselected);
        unselectedButton.mutate();
        setBackground(button,stateListDrawable.mutate());
    }

    public static void colorizeButtonBackground(Button button, int color) {
        StateListDrawable stateListDrawable = (StateListDrawable) button.getBackground();
        DrawableContainer.DrawableContainerState drawableContainerState = (DrawableContainer.DrawableContainerState) stateListDrawable.getConstantState();
        Drawable[] children = drawableContainerState.getChildren();
        GradientDrawable unselectedButton = (GradientDrawable) children[0];
        unselectedButton.setColor(color);
        unselectedButton.mutate();
        setBackground(button,stateListDrawable.mutate());
    }

    public static void setBackground(View view, Drawable drawable) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN)
            view.setBackground(drawable);
        else
            view.setBackgroundDrawable(drawable);
    }
}

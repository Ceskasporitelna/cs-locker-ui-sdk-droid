package cz.csas.lockerui.components;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.DrawableContainer;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.StateListDrawable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.RelativeLayout;
import android.widget.TextView;

import csas.cz.lockerui.R;
import cz.csas.lockerui.utils.ColorUtils;
import cz.csas.lockerui.utils.ScreenUtils;

/**
 * The type Pin pad.
 *
 * @author Jan Hauser <jan.hauser@applifting.cz>
 * @since 30 /11/15.
 */
public class PinPad extends RelativeLayout {

    private PinButton pbOne;

    private PinButton pbTwo;

    private PinButton pbThree;

    private PinButton pbFour;

    private PinButton pbFive;

    private PinButton pbSix;

    private PinButton pbSeven;

    private PinButton pbEight;

    private PinButton pbNine;

    private PinButton pbZero;

    private RelativeLayout rlPbDelete;

    private SwitchButton swButton;

    private TextView tvPbDelete;

    private LayoutInflater mInflater;

    /**
     * Instantiates a new Pin pad.
     *
     * @param context the context
     * @param attrs   the attrs
     */
    public PinPad(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    private void init(Context context) {
        mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        if (ScreenUtils.isLandscape(context)) {
            if (ScreenUtils.checkScreenHeight(context, 490))
                mInflater.inflate(R.layout.pinpad_component_smallest, this, true);
            else if (ScreenUtils.checkScreenHeight(context, 1500))
                mInflater.inflate(R.layout.pinpad_component_small, this, true);
            else
                mInflater.inflate(R.layout.pinpad_component, this, true);
        } else {
            if (ScreenUtils.checkScreenHeight(context, 800))
                mInflater.inflate(R.layout.pinpad_component_small, this, true);
            else
                mInflater.inflate(R.layout.pinpad_component, this, true);
        }
        pbOne = (PinButton) findViewById(R.id.pb_one_pin_activity);
        pbTwo = (PinButton) findViewById(R.id.pb_two_pin_activity);
        pbThree = (PinButton) findViewById(R.id.pb_three_pin_activity);
        pbFour = (PinButton) findViewById(R.id.pb_four_pin_activity);
        pbFive = (PinButton) findViewById(R.id.pb_five_pin_activity);
        pbSix = (PinButton) findViewById(R.id.pb_six_pin_activity);
        pbSeven = (PinButton) findViewById(R.id.pb_seven_pin_activity);
        pbEight = (PinButton) findViewById(R.id.pb_eight_pin_activity);
        pbNine = (PinButton) findViewById(R.id.pb_nine_pin_activity);
        pbZero = (PinButton) findViewById(R.id.pb_zero_pin_activity);
        rlPbDelete = (RelativeLayout) findViewById(R.id.rl_pin_button_delete);
        tvPbDelete = (TextView) findViewById(R.id.tv_text_pin_button_delete);
        swButton = (SwitchButton) findViewById(R.id.sw_switch_button);
    }

    /**
     * Gets pb one.
     *
     * @return the pb one
     */
    public PinButton getPbOne() {
        return pbOne;
    }

    /**
     * Gets pb two.
     *
     * @return the pb two
     */
    public PinButton getPbTwo() {
        return pbTwo;
    }

    /**
     * Gets pb three.
     *
     * @return the pb three
     */
    public PinButton getPbThree() {
        return pbThree;
    }

    /**
     * Gets pb four.
     *
     * @return the pb four
     */
    public PinButton getPbFour() {
        return pbFour;
    }

    /**
     * Gets pb five.
     *
     * @return the pb five
     */
    public PinButton getPbFive() {
        return pbFive;
    }

    /**
     * Gets pb six.
     *
     * @return the pb six
     */
    public PinButton getPbSix() {
        return pbSix;
    }

    /**
     * Gets pb seven.
     *
     * @return the pb seven
     */
    public PinButton getPbSeven() {
        return pbSeven;
    }

    /**
     * Gets pb eight.
     *
     * @return the pb eight
     */
    public PinButton getPbEight() {
        return pbEight;
    }

    /**
     * Gets pb nine.
     *
     * @return the pb nine
     */
    public PinButton getPbNine() {
        return pbNine;
    }

    /**
     * Gets pb zero.
     *
     * @return the pb zero
     */
    public PinButton getPbZero() {
        return pbZero;
    }

    /**
     * Gets rl pb delete.
     *
     * @return the rl pb delete
     */
    public RelativeLayout getRlPbDelete() {
        return rlPbDelete;
    }

    /**
     * Gets rl switch button.
     *
     * @return the rl switch button
     */
    public SwitchButton getSwitchButton() {
        return swButton;
    }

    /**
     * Sets pip buttons color.
     *
     * @param color the color
     */
    public void setPinButtonsColor(int color) {
        setPinButtonColor(pbZero, color);
        setPinButtonColor(pbOne, color);
        setPinButtonColor(pbTwo, color);
        setPinButtonColor(pbThree, color);
        setPinButtonColor(pbFour, color);
        setPinButtonColor(pbFive, color);
        setPinButtonColor(pbSix, color);
        setPinButtonColor(pbSeven, color);
        setPinButtonColor(pbEight, color);
        setPinButtonColor(pbNine, color);
        if (swButton != null)
            setSwitchButtonColor(swButton, color);
        tvPbDelete.setTextColor(color);
    }

    private void setPinButtonColor(PinButton pinButton, int color) {
        StateListDrawable stateListDrawable = (StateListDrawable) pinButton.getRlPinButton().getBackground();
        DrawableContainer.DrawableContainerState drawableContainerState = (DrawableContainer.DrawableContainerState) stateListDrawable.getConstantState();
        Drawable[] children = drawableContainerState.getChildren();
        GradientDrawable unselectedDrawable = (GradientDrawable) children[1];
        unselectedDrawable.setColor(color);
        ColorUtils.setBackground(pinButton.getRlPinButton(), stateListDrawable.mutate());
    }

    private void setSwitchButtonColor(SwitchButton swButton, int color) {
        StateListDrawable stateListDrawable = (StateListDrawable) swButton.getRlSwitchButton().getBackground();
        DrawableContainer.DrawableContainerState drawableContainerState = (DrawableContainer.DrawableContainerState) stateListDrawable.getConstantState();
        Drawable[] children = drawableContainerState.getChildren();
        GradientDrawable unselectedDrawable = (GradientDrawable) children[1];
        unselectedDrawable.setColor(color);
        ColorUtils.setBackground(swButton.getRlSwitchButton(), stateListDrawable.mutate());
    }
}

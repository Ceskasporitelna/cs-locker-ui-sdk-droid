package cz.csas.lockerui.components;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.RelativeLayout;

import csas.cz.lockerui.R;
import cz.csas.lockerui.utils.ScreenUtils;

/**
 * The type Pin button.
 *
 * @author Jan Hauser <jan.hauser@applifting.cz>
 * @since 30 /11/15.
 */
public class SwitchButton extends RelativeLayout {

    private RelativeLayout rlPinButton;

    private LayoutInflater mInflater;

    /**
     * Instantiates a new Pin button.
     *
     * @param context the context
     * @param attrs   the attrs
     */
    public SwitchButton(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);

        TypedArray ta = context.obtainStyledAttributes(attrs,
                R.styleable.PinButton, 0, 0);

        ta.recycle();
    }

    private void init(Context context) {
        mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        if (ScreenUtils.checkScreenHeight(context, 490))
            mInflater.inflate(R.layout.switch_button_component_smallest, this, true);
        else if (ScreenUtils.checkScreenHeight(context, 1500))
            mInflater.inflate(R.layout.switch_button_component_small, this, true);
        else
            mInflater.inflate(R.layout.switch_button_component, this, true);
        rlPinButton = (RelativeLayout) findViewById(R.id.rl_pin_button);
    }

    /**
     * Gets rl switch button.
     *
     * @return the rl switch button
     */
    public RelativeLayout getRlSwitchButton() {
        return rlPinButton;
    }

}

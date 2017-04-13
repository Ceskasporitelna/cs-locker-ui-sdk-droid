package cz.csas.lockerui.components;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.RelativeLayout;
import android.widget.TextView;

import csas.cz.lockerui.R;
import cz.csas.lockerui.utils.ScreenUtils;
import cz.csas.lockerui.utils.TypefaceUtils;

/**
 * The type Pin button.
 *
 * @author Jan Hauser <jan.hauser@applifting.cz>
 * @since 30 /11/15.
 */
public class PinButton extends RelativeLayout {

    private RelativeLayout rlPinButton;

    private TextView tvNumber;

    private TextView tvText;

    private LayoutInflater mInflater;

    /**
     * Instantiates a new Pin button.
     *
     * @param context the context
     * @param attrs   the attrs
     */
    public PinButton(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);

        TypedArray ta = context.obtainStyledAttributes(attrs,
                R.styleable.PinButton, 0, 0);

        if (ta.getString(R.styleable.PinButton_setNumber) != null) {
            setNumber(ta.getString(R.styleable.PinButton_setNumber));
        }

        if (ta.getString(R.styleable.PinButton_setText) != null) {
            setText(ta.getString(R.styleable.PinButton_setText));
        }

        if (ta.getString(R.styleable.PinButton_setCentral) != null) {
            setCentral(ta.getBoolean(R.styleable.PinButton_setCentral, false));
        }

        ta.recycle();
    }

    private void init(Context context) {
        mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        if (ScreenUtils.isLandscape(context)) {
            if (ScreenUtils.checkScreenHeight(context, 490))
                mInflater.inflate(R.layout.pin_button_component_smallest, this, true);
            else if (ScreenUtils.checkScreenHeight(context, 1500))
                mInflater.inflate(R.layout.pin_button_component_small, this, true);
            else
                mInflater.inflate(R.layout.pin_button_component, this, true);
        } else {
            if (ScreenUtils.checkScreenHeight(context, 800))
                mInflater.inflate(R.layout.pin_button_component_small, this, true);
            else
                mInflater.inflate(R.layout.pin_button_component, this, true);
        }
        rlPinButton = (RelativeLayout) findViewById(R.id.rl_pin_button);
        tvNumber = (TextView) findViewById(R.id.tv_number_pin_button);
        tvText = (TextView) findViewById(R.id.tv_text_pin_button);
    }

    /**
     * Gets tv number.
     *
     * @return the tv number
     */
    public TextView getTvNumber() {
        return tvNumber;
    }

    /**
     * Gets tv text.
     *
     * @return the tv text
     */
    public TextView getTvText() {
        return tvText;
    }

    /**
     * Gets rl pin button.
     *
     * @return the rl pin button
     */
    public RelativeLayout getRlPinButton() {
        return rlPinButton;
    }

    /**
     * Set number.
     *
     * @param number the number
     */
    public void setNumber(String number) {
        tvNumber.setText(number);
        tvNumber.setTypeface(TypefaceUtils.getRobotoMedium(getContext()));
    }

    /**
     * Set text.
     *
     * @param text the text
     */
    public void setText(String text) {
        tvText.setText(text);
        tvText.setTypeface(TypefaceUtils.getRobotoRegular(getContext()));
    }

    /**
     * Set central.
     *
     * @param central the central
     */
    public void setCentral(boolean central) {
        if (central) {
            LayoutParams layoutParams = (LayoutParams) tvNumber.getLayoutParams();
            layoutParams.addRule(RelativeLayout.CENTER_IN_PARENT, RelativeLayout.TRUE);
            tvNumber.setLayoutParams(layoutParams);
            tvText.setVisibility(GONE);
        }
    }
}

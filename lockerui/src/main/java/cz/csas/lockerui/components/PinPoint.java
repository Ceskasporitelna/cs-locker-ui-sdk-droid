package cz.csas.lockerui.components;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.RelativeLayout;

import csas.cz.lockerui.R;

/**
 * The type Pin point.
 *
 * @author Jan Hauser <jan.hauser@applifting.cz>
 * @since 30 /11/15.
 */
public class PinPoint extends RelativeLayout {

    private RelativeLayout rlPoint;

    private LayoutInflater mInflater;

    public PinPoint(Context context) {
        super(context);
        init(context);
    }

    /**
     * Instantiates a new Pin point.
     *
     * @param context the context
     * @param attrs   the attrs
     */
    public PinPoint(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    private void init(Context context) {
        mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mInflater.inflate(R.layout.pin_point_component, this, true);
        rlPoint = (RelativeLayout) findViewById(R.id.rl_pin_point);
    }

    /**
     * Gets rl point.
     *
     * @return the rl point
     */
    public RelativeLayout getRlPoint() {
        return rlPoint;
    }
}

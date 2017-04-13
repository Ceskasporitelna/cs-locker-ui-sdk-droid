package cz.csas.lockerui.utils;

import java.util.Random;

/**
 * @author Jan Hauser <jan.hauser@applifting.cz>
 * @since 01/12/15.
 */
public class NumberUtils {

    public static float generateRandomAngle() {

        Random rand = new Random();
        int randomNum = (rand.nextInt((360 - 0) + 1))-rand.nextInt((360-0)+1);

        return (float)randomNum;
    }
}

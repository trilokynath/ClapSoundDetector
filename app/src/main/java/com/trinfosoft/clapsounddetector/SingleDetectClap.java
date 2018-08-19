package com.trinfosoft.clapsounddetector;

/**
 * Created by Trilokynath Wagh on 24/04/2018.
 */

import android.util.Log;
import android.widget.Toast;


public class SingleDetectClap implements AmplitudeClipListener {
    public static final int AMPLITUDE_DIFF_LOW = 5000;
    public static final int AMPLITUDE_DIFF_MED = 9000;
    public static final int AMPLITUDE_DIFF_HIGH = 30000;

    public static final int DEFAULT_AMPLITUDE_DIFF = AMPLITUDE_DIFF_HIGH;
    public int maxAmp;
    public SingleDetectClap(int maxAmp){
        this.maxAmp = maxAmp;
    }
    @Override
    public boolean heard(int maxAmplitude) {
        boolean result = false;

        if(maxAmp >= maxAmplitude){
            Log.d("Clap", result+ " " +maxAmp+" "+maxAmplitude);
            result = true;
        }
        return result;
    }
}
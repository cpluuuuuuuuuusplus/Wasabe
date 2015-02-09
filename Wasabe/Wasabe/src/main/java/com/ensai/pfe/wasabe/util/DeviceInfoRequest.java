package com.ensai.pfe.wasabe.util;

import android.content.Context;

/**
 * Runs a device info request
 * <p/>
 * <p/>
 * MAYBE UNUSED
 * <p/>
 * Created by nicolas on 09/02/15.
 */
public class DeviceInfoRequest implements Runnable {

    DeviceInfo di;

    DeviceInfoRequest(Context ctx, DeviceInfo di) {
        this.di = di;


    }


    public void run() {

    }
}

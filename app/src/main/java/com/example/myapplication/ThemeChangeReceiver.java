package com.example.myapplication;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.app.Activity;


public class ThemeChangeReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        if (ThemeUtil.THEME_CHANGED_ACTION.equals(intent.getAction())) {
            if (context instanceof Activity) {
                ((Activity) context).recreate();
            }
        }
    }
}

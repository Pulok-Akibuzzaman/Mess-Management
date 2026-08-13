package com.project.messmanagement;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

public class MyBroadcastReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();

        if (action == null) return;
        switch (action) {
            case Intent.ACTION_POWER_CONNECTED:
                Toast.makeText(context, "Charger Connected", Toast.LENGTH_SHORT).show();
                break;

            case Intent.ACTION_POWER_DISCONNECTED:
                Toast.makeText(context, "Charger Disconnected", Toast.LENGTH_SHORT).show();
                break;

            case Intent.ACTION_AIRPLANE_MODE_CHANGED:
                boolean isAirplaneModeOn = intent.getBooleanExtra("state", false);
                String point = "";
                if(isAirplaneModeOn)
                    point = "On";
                else
                    point="Off";
                Toast.makeText(context, "Airplane Mode is " +point, Toast.LENGTH_SHORT).show();
                break;
        }
    }
}

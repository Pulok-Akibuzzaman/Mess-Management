package com.project.messmanagement;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.widget.Toast;

public class SOSReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        // This runs when the background timer expires
        Toast.makeText(context, "SAFETY TIMER EXPIRED!", Toast.LENGTH_LONG).show();
        
        // Open the SOS Activity to trigger the dialer
        Intent sosIntent = new Intent(context, SOSActivity.class);
        sosIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        sosIntent.putExtra("TRIGGER_DIAL", true);
        context.startActivity(sosIntent);
    }
}

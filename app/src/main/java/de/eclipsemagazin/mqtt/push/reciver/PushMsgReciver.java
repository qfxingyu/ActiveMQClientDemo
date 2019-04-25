package de.eclipsemagazin.mqtt.push.reciver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

import nari.mip.pushsdk.util.PushConstants;

public class PushMsgReciver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        Toast.makeText(context, intent.getStringExtra(PushConstants.MESSAGECONTENT), Toast.LENGTH_SHORT).show();
    }
}

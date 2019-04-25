package nari.mip.pushsdk.services;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.IBinder;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import nari.mip.pushsdk.util.PushCoreTool;
import nari.mip.pushsdk.keeplive.KeepAliveJobPush;

/**
 * author:xmf
 * date:2019/4/24 0024
 * description:启动消息推送和保活
 */
public class PushService extends Service {

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    private ScheduledExecutorService scheduler;
    private boolean isLive = true;

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        startReconnect();
        startKeepAliveJob(this.getApplicationContext());
        return super.onStartCommand(intent, flags, startId);
    }

    private void startKeepAliveJob(Context context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            context.startService(new Intent(context, KeepAliveJobPush.class));
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        isLive = false;
        PushCoreTool.getInstance().stopPush(this);
    }

    private void startReconnect() {
        scheduler = Executors.newSingleThreadScheduledExecutor();
        scheduler.scheduleAtFixedRate(new Runnable() {

            @Override
            public void run() {
                if (!PushCoreTool.getInstance().checkeIsLive() && isLive) {
                    PushCoreTool.getInstance().startPush(getApplicationContext());
                }
            }
        }, 0 * 1000, 3 * 1000, TimeUnit.MILLISECONDS);
    }

}

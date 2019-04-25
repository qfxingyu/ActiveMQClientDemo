package nari.mip.pushsdk.keeplive;

import android.app.ActivityManager;
import android.app.job.JobParameters;
import android.app.job.JobService;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.util.Log;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import nari.mip.pushsdk.PushManagerTool;
import nari.mip.pushsdk.util.PushCoreTool;

public class KeepAliveJobPush extends JobService {
    private static final String TAG = "KeepAliveJobPush_xmf";

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        try {
            KeepPushTools.scheduleJob(this);
            Log.e(TAG, "jobServiceonStartCommand");
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return START_STICKY;
    }

    @Override
    public boolean onStartJob(JobParameters params) {
        try {

            checkService();
            if (Build.VERSION.SDK_INT >= 24) {
                KeepPushTools.scheduleJob(this);
                jobFinished(params, true);
            } else {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {//API需要在21及以上
                    jobFinished(params, false);
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return true;
    }

    @Override
    public boolean onStopJob(JobParameters params) {
        return true;
    }


    private void checkService() {
        try {

            executor.execute(new Runnable() {

                @Override
                public void run() {
                    try {

                        if (getFlag()) {
                            try {
                                Thread.sleep(10000);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                            setFlag(false);
                        }
                        if (!isRunningTaskExist(KeepAliveJobPush.this, KeepAliveJobPush.this.getPackageName() + ":mippush_v1")) {
                            Log.e(TAG, "--------------------------------消息推送服务不存在,启动服务--------------------------------");
                            startPush();
                            setFlag(true);
                        }
//                        else {
//                            Log.e(TAG, "--------------------------------消息推送服务存在--------------------------------");
//                        }
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                }
            });
        } catch (Exception ex) {
            ex.printStackTrace();
        }

    }

    ExecutorService executor = Executors.newFixedThreadPool(2);

    boolean flag = false;

    public synchronized boolean getFlag() {

        return flag;
    }

    public synchronized void setFlag(boolean bFlag) {

        flag = bFlag;
    }

    private void startPush() {
        try {
            PushManagerTool.getInstance().startPush(getApplicationContext());
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public boolean isRunningTaskExist(Context context, String processName) {
        ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningAppProcessInfo> processList = am.getRunningAppProcesses();
        for (ActivityManager.RunningAppProcessInfo info : processList) {
            if (info.processName.equals(processName)) {
                return true;
            }
        }
        return false;
    }


}
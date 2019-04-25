package nari.mip.pushsdk.keeplive;

import android.app.job.JobInfo;
import android.app.job.JobScheduler;
import android.content.ComponentName;
import android.content.Context;
import android.os.Build;
import android.util.Log;


/**
 * author:xmf
 * date:2018/8/30 0030
 * description:保活工具类
 */
public class KeepPushTools {
    /***
     * 默认3秒时间执行一次
     */
    private static final long DEFAULT_INITIAL_BACKOFF_MILLIS = 3000L;
    /***
     * 任务id
     */
    private static final int kJobId = 98272;

    /***
     *执行JobService任务
     * @param mContext
     */
    public static void scheduleJob(Context mContext) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {//API需要在21及以上
            return;
        }
        scheduleJob(mContext, -101, -101);
    }

    /***
     *
     * @param mContext
     * @param mkJobId 任务id mip默认id使用 98272
     * @param default_initial_backoff_millis 默认3秒时间执行一次
     */
    public static void scheduleJob(Context mContext, int mkJobId, long default_initial_backoff_millis) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {//API需要在21及以上
            return;
        }
        try {
            if (-101 == mkJobId) {
                mkJobId = kJobId;
            }
            if (-101 == default_initial_backoff_millis) {
                default_initial_backoff_millis = DEFAULT_INITIAL_BACKOFF_MILLIS;
            }
            JobInfo.Builder builder = new JobInfo.Builder(mkJobId,
                    new ComponentName(mContext, mContext.getClass()));
            if (Build.VERSION.SDK_INT >= 24) {
                builder.setMinimumLatency(default_initial_backoff_millis);//执行的最小延迟时间
                builder.setOverrideDeadline(default_initial_backoff_millis); //执行的最长延时时间
                builder.setBackoffCriteria(default_initial_backoff_millis, JobInfo.BACKOFF_POLICY_LINEAR);//线性重试方案
            } else {
                // 间隔3000毫秒
                builder.setPeriodic(default_initial_backoff_millis);// 设置间隔时间
            }
            JobInfo t = builder.build();
            builder.setPersisted(true);// 设备重启之后你的任务是否还要继续执行
            builder.setRequiredNetworkType(JobInfo.NETWORK_TYPE_ANY);
            builder.setRequiresCharging(false);// 设置是否充电的条件,默认false
            builder.setRequiresDeviceIdle(false);// 设置手机是否空闲的条件,默认false
            JobScheduler tm = (JobScheduler) mContext.getSystemService(Context.JOB_SCHEDULER_SERVICE);
            tm.schedule(t);
        } catch (Exception ex) {
            Log.e("push_xmf", "KeepLiveTools errois:" + ex.toString());
        }
    }


}

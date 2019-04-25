package nari.mip.pushsdk;

import android.content.Context;
import android.content.Intent;

import nari.mip.pushsdk.interfaces.IPushMethod;
import nari.mip.pushsdk.services.PushService;
import nari.mip.pushsdk.util.PushCoreTool;

public class PushManagerTool implements IPushMethod {
    private PushManagerTool() {

    }

    private static class Holder {
        private static final PushManagerTool mHander = new PushManagerTool();
    }

    public static PushManagerTool getInstance() {
        return Holder.mHander;
    }

    @Override
    public String init(Context mContext, String host, String userName, String passWord, String clientId, String[] topID) {
        return PushCoreTool.getInstance().init(mContext, host, userName, passWord, 10, 20, clientId, topID);
    }

    @Override
    public String init(Context mContext, String host, String userName, String passWord, int connectionTimeout, int keepAliveInterval, String clientId, String[] topID) {
        return PushCoreTool.getInstance().init(mContext, host, userName, passWord, connectionTimeout, keepAliveInterval, clientId, topID);
    }

    @Override
    public void startPush(Context mContext) {
        Intent intent = new Intent(mContext, PushService.class);
        intent.setPackage(mContext.getPackageName());
        mContext.startService(intent);
    }

    @Override
    public boolean stopPush(Context mContext) {
        try {
            Intent intent = new Intent(mContext, PushService.class);
            intent.setPackage(mContext.getPackageName());
            mContext.stopService(intent);
        } catch (Exception ex) {
            ex.printStackTrace();
            return false;
        }
        return true;
    }

}

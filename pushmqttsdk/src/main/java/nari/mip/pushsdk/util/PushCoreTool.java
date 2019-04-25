package nari.mip.pushsdk.util;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;

import nari.mip.pushsdk.interfaces.IPushMethod;

public class PushCoreTool implements IPushMethod {
    private final String TAG = "PushMsgTool_xmf";
    private MqttConnectOptions options;
    private final String splitTopID = "@%";

    private PushCoreTool() {

    }

    private static class Holder {
        private static final PushCoreTool mHander = new PushCoreTool();
    }

    public static PushCoreTool getInstance() {
        return Holder.mHander;
    }

    private MqttClient client;

    /***
     *
     * @param mContext 上下文
     * @param host 连接的主机名
     * @param userName 连接的用户名
     * @param passWord 连接的密码
     * @param clientId  连接MQTT的客户端ID，一般以客户端唯一标识符表示
     * @param topID 标签
     */
    @Override
    public String init(Context mContext, String host, String userName, String passWord, String clientId, String[] topID) {
        return init(mContext, host, userName, passWord, 10, 20, clientId, topID);
    }

    /***
     *
     * @param mContext 上下文
     * @param host 连接的主机名
     * @param userName 连接的用户名
     * @param passWord 连接的密码
     * @param connectionTimeout 超时时间 单位为秒
     * @param keepAliveInterval 设置会话心跳时间 单位为秒
     * @param clientId  连接MQTT的客户端ID，一般以客户端唯一标识符表示
     * @param topID 标签
     */
    @Override
    public String init(Context mContext, String host, String userName, String passWord, int connectionTimeout, int keepAliveInterval, String clientId, String[] topID) {
        try {
            SPUtils.getInstance().init(mContext.getApplicationContext());
            SPUtils.getInstance().putString(SPFinal.HOST, host);
            SPUtils.getInstance().putString(SPFinal.CLIENTID, clientId);
            StringBuffer sbf = new StringBuffer();
            if (null == topID || topID.length <= 0) {
                return "标签不能为空";
            }
            if (topID.length == 1) {
                sbf = sbf.append(topID[0]);
            } else {
                for (int i = 0; i < topID.length; i++) {
                    if (i == topID.length - 1) {
                        sbf = sbf.append(topID[i]);
                    } else {
                        sbf = sbf.append(topID[i] + splitTopID);
                    }

                }
            }
            SPUtils.getInstance().putString(SPFinal.TOPID, sbf.toString());
            SPUtils.getInstance().putString(SPFinal.USERNAME, userName);
            SPUtils.getInstance().putString(SPFinal.PASSWORD, passWord);
            SPUtils.getInstance().putInt(SPFinal.CONNECTIONTIMEOUT, connectionTimeout);
            SPUtils.getInstance().putInt(SPFinal.KEEPALIVEINTERVAL, keepAliveInterval);
            initClient(mContext);
            initOptions(mContext);
        } catch (Exception e) {
            Log.e(TAG, "PushMsgTool---init--erro-" + e.toString());
            return "初始化失败:" + e.toString();
        }
        return "初始化成功";
    }

    private void initClient(final Context mContext) {
        SPUtils.getInstance().init(mContext.getApplicationContext());
        String host = SPUtils.getInstance().getString(SPFinal.HOST);
        String clientId = SPUtils.getInstance().getString(SPFinal.CLIENTID);
        try {
            client = new MqttClient(host, clientId,
                    new MemoryPersistence());

            //设置回调
            client.setCallback(new MqttCallback() {

                @Override
                public void connectionLost(Throwable cause) {
                    //连接丢失后，一般在这里面进行重连
                    Log.e(TAG, "connectionLost----------");
                    sendBrodcast(mContext, PushConstants.PUSHMESSAGE_ERROR_ACTION, "连接丢失:" + cause.getMessage());
                }

                @Override
                public void deliveryComplete(IMqttDeliveryToken token) {
                    //publish后会执行到这里
                    Log.e(TAG, "deliveryComplete---------" + token.isComplete());
                    sendBrodcast(mContext, PushConstants.PUSHMESSAGE_PUBLISH_ACTION, "publish执行:" + token.isComplete());
                }

                @Override
                public void messageArrived(String topicName, MqttMessage message) {
                    //subscribe后得到的消息会执行到这里面
                    Log.e(TAG, "messageArrived----------" + topicName + "---" + message.toString());
                    sendBrodcast(mContext, PushConstants.PUSHMESSAGE_ACTION, topicName + "---" + message.toString());
                }
            });
        } catch (Exception ex) {
            Log.e(TAG, "PushMsgTool---initClient---" + ex.toString());
        }
    }

    private void sendBrodcast(Context mContext, String action, String values) {
        if (null == mContext) {
            return;
        }
        try {
            Intent intent = new Intent();
            String pkg = mContext.getPackageName();
            String className = pkg + ".reciver.PushMsgReciver";
            intent.setComponent(new ComponentName(pkg, className));
            intent.setAction(pkg + action);
            intent.putExtra(PushConstants.MESSAGECONTENT, values);
            mContext.sendBroadcast(intent);
        } catch (Exception ex) {
            Log.e(TAG, "PushMsgTool---sendBrodcast--erro-" + ex.toString());
        }

    }

    private void initOptions(Context mContext) {
        SPUtils.getInstance().init(mContext.getApplicationContext());
        String userName = SPUtils.getInstance().getString(SPFinal.USERNAME);
        String passWord = SPUtils.getInstance().getString(SPFinal.USERNAME);
        int connectionTimeout = SPUtils.getInstance().getInt(SPFinal.CONNECTIONTIMEOUT);
        int keepAliveInterval = SPUtils.getInstance().getInt(SPFinal.KEEPALIVEINTERVAL);
        try {
            //MQTT的连接设置
            options = new MqttConnectOptions();
            // 设置是否清空session,这里如果设置为false表示服务器会保留客户端的连接记录，这里设置为true表示每次连接到服务器都以新的身份连接
            //换而言之，设置为false时可以客户端可以接受离线消息
            options.setCleanSession(false);
            //设置连接的用户名
            options.setUserName(userName);
            //设置连接的密码
            options.setPassword(passWord.toCharArray());
            // 设置超时时间 单位为秒
            options.setConnectionTimeout(connectionTimeout);
            // 设置会话心跳时间 单位为秒 服务器会每隔1.5*20秒的时间向客户端发送个消息判断客户端是否在线，但这个方法并没有重连的机制
            options.setKeepAliveInterval(keepAliveInterval);
        } catch (Exception ex) {
            Log.e(TAG, "PushMsgTool---initOptions--erro-" + ex.toString());
        }
    }

    @Override
    public void startPush(final Context mContext) {
        new Thread(new Runnable() {

            @Override
            public void run() {
                try {
                    if (null == client) {
                        initClient(mContext);
                    }
                    if (null == options) {
                        initOptions(mContext);
                    }
                    //setWill方法，如果项目中需要知道客户端是否掉线可以调用该方法。设置最终端口的通知消息
//                    options.setWill(topic, "close".getBytes(), 2, true);
                    //mqtt客户端连接服务器
                    if (!client.isConnected()) {
                        client.connect(options);
                        //mqtt客户端订阅主题
                        //在mqtt中用QoS来标识服务质量
                        //QoS=0时，报文最多发送一次，有可能丢失
                        //QoS=1时，报文至少发送一次，有可能重复
                        //QoS=2时，报文只发送一次，并且确息只保消到次一达。
                        SPUtils.getInstance().init(mContext.getApplicationContext());
                        String[] topID = SPUtils.getInstance().getString(SPFinal.TOPID).split(splitTopID);
                        int[] Qos = new int[topID.length];
                        for (int i = 0; i < topID.length; i++) {
                            Qos[i] = 2;
                        }
                        client.subscribe(topID, Qos);
                    }
                    Log.e(TAG, "---消息推送启动成功---");

                } catch (Exception e) {
                    Log.e(TAG, "PushMsgTool---startPush--erro-" + e.toString());
                }
            }
        }).start();
    }


    @Override
    public boolean stopPush(final Context mContext) {
        try {
            SPUtils.getInstance().init(mContext.getApplicationContext());
            String[] topID = SPUtils.getInstance().getString(SPFinal.TOPID).split(splitTopID);
            if (client.isConnected()) {
                client.unsubscribe(topID);
                client.disconnect();
            }
            Log.e(TAG, "---消息推送停止成功---");
            return true;
        } catch (Exception e) {
            Log.e(TAG, "PushMsgTool---stopPush--erro-" + e.toString());
        }
        return false;
    }

    /***
     * 判断当前消息长连接是否存在
     * @return ture 存在 false不存在
     */
    public boolean checkeIsLive() {
        try {
            if (null == client) {
                return false;
            }
            if (!client.isConnected()) {
                return false;
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            return false;
        }

        return true;
    }

}

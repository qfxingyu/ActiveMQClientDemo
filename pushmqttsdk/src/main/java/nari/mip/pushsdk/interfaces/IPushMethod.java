package nari.mip.pushsdk.interfaces;

import android.content.Context;

public interface IPushMethod {
    /***
     *
     * @param mContext 上下文
     * @param host 连接的主机名
     * @param userName 连接的用户名
     * @param passWord 连接的密码
     * @param clientId  连接MQTT的客户端ID，一般以客户端唯一标识符表示
     * @param topID 标签
     */
    String init(Context mContext, String host, String userName, String passWord, String clientId, String[] topID);

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
    String init(Context mContext, String host, String userName, String passWord, int connectionTimeout, int keepAliveInterval, String clientId, String[] topID);

    /***
     * 启动消息推送
     * @param mContext
     */
    void startPush(final Context mContext);

    /****
     * 停止消息推送
     * @param mContext
     * @return
     */
    boolean stopPush(final Context mContext);

}

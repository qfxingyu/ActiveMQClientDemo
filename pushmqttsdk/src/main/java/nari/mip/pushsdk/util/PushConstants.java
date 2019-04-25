package nari.mip.pushsdk.util;

/**
 * author:xmf
 * date:2019/4/3 0003
 * description:消息推送常量
 */
public class PushConstants {
    /***
     * 接收消息的action
     */
    public static final String PUSHMESSAGE_ACTION = ".reciver.pushmsg";

    /***
     * 推送出现错误的action
     */
    public static final String PUSHMESSAGE_ERROR_ACTION = ".reciver.pushmsg.erro";
    /***
     * 推送执行后的action
     */
    public static final String PUSHMESSAGE_PUBLISH_ACTION = ".reciver.pushmsg.publish";
    /***
     * 消息内容
     */
    public static final String MESSAGECONTENT = "messagecontent";
    public static final String PUSH_MSG_ID = "push_msg_id";
}

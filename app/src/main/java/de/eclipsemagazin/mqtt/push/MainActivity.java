package de.eclipsemagazin.mqtt.push;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import nari.mip.pushsdk.PushManagerTool;
import nari.mip.pushsdk.util.PushCoreTool;

/**
 * author:xmf
 * date:2019/4/24 0024
 * description:
 */
public class MainActivity extends Activity {

    private TextView resultTv;
    private String host = "tcp://192.168.50.27:9005";
    private String userName = "admin";
    private String passWord = "password";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        resultTv = findViewById(R.id.result);
        //初始化，ip不变只需要一次
        String[] topiID = {"a111", "江苏省", "项目统筹部"};
        PushManagerTool.getInstance().init(getApplicationContext(), host, userName, passWord, "333", topiID);
        findViewById(R.id.startPush).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PushManagerTool.getInstance().startPush(getApplicationContext());
            }
        });
        findViewById(R.id.stopPush).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PushManagerTool.getInstance().stopPush(getApplicationContext());
            }
        });
    }
}

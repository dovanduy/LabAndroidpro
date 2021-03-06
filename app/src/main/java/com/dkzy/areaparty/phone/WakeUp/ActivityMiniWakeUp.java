package com.dkzy.areaparty.phone.WakeUp;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.baidu.speech.EventListener;
import com.baidu.speech.EventManager;
import com.baidu.speech.EventManagerFactory;
import com.baidu.speech.asr.SpeechConstant;
import com.dkzy.areaparty.phone.R;
import com.dkzy.areaparty.phone.fragment03.utils.TVAppHelper;
import com.google.gson.Gson;

import org.json.JSONObject;

import java.util.Map;
import java.util.TreeMap;

public class ActivityMiniWakeUp extends AppCompatActivity implements EventListener {
    protected TextView txtLog;
    protected TextView txtResult;
    protected Button btn;
    protected Button stopBtn;
    private static String DESC_TEXT = "精简版唤醒，带有SDK唤醒运行的最少代码，仅仅展示如何调用，\n" +
            "也可以用来反馈测试SDK输入参数及输出回调。\n" +
            "本示例需要自行根据文档填写参数，可以使用之前唤醒示例中的日志中的参数。\n" +
            "需要完整版请参见之前的唤醒示例。\n\n" +
            "唤醒词是纯离线功能，需要获取正式授权文件（与离线命令词的正式授权文件是同一个）。 第一次联网使用唤醒词功能自动获取正式授权文件。之后可以断网测试\n" +
            "请说“小度你好”或者 “百度一下”\n\n";

    private EventManager wakeup;

    private boolean logTime = true;

    /**
     * 测试参数填在这里
     */
    private void start() {
        txtLog.setText("");
        Map<String, Object> params = new TreeMap<String, Object>();

        params.put(SpeechConstant.ACCEPT_AUDIO_VOLUME, false);
        params.put(SpeechConstant.WP_WORDS_FILE, "assets:///WakeUp.bin");
        // "assets:///WakeUp.bin" 表示WakeUp.bin文件定义在assets目录下

        String json = null; // 这里可以替换成你需要测试的json
        json = new JSONObject(params).toString();
        wakeup.send(SpeechConstant.WAKEUP_START, json, null, 0, 0);
        printLog("输入参数：" + json);
//        ---------------------------------
//        System.out.println(params);
    }

    private void stop() {
        wakeup.send(SpeechConstant.WAKEUP_STOP, null, null, 0, 0); //
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.common_mini);
        initView();
        wakeup = EventManagerFactory.create(this, "wp");
        wakeup.registerListener(this); //  EventListener 中 onEvent方法
        btn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                start();
            }
        });
        stopBtn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                stop();
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        wakeup.send(SpeechConstant.WAKEUP_STOP, "{}", null, 0, 0);
    }

    //   EventListener  回调方法
    @Override
    public void onEvent(String name, String params, byte[] data, int offset, int length) {
    /*    String logTxt = "name: " + name;
        if (params != null && !params.isEmpty()) {
            logTxt += " ;params :" + params;
        } else if (data != null) {
            logTxt += " ;data length=" + data.length;
        }
        printLog(logTxt);
        System.out.println(params);*/
//    以上为DEMO自带的调用方式，现在改换自己写
//        Log.d(TAG, String.format("event: name=%s, params=%s", name, params));

        //唤醒成功
        if(name.equals("wp.data")){
            try {
                Gson gson = new Gson();
                WakeUpInstruction wakeUpInstruction = gson.fromJson(params,WakeUpInstruction.class);
                int errorCode = wakeUpInstruction.getErrorCode();
                if(errorCode == 0){
                    //唤醒成功
                    if(wakeUpInstruction.getWord().equals("增大音量")){
                        TVAppHelper.vedioPlayControlVolumeUp();
                        printLog(wakeUpInstruction.getWord());
                    }
                    else if(wakeUpInstruction.getWord().equals("减小音量")){
                        TVAppHelper.vedioPlayControlVolumeDown();
                        printLog(wakeUpInstruction.getWord());
                    }
                    printLog(wakeUpInstruction.getErrorDesc());
                    printLog(wakeUpInstruction.getWord());
                } else {
                    //唤醒失败
                    printLog(wakeUpInstruction.getErrorDesc());
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else if("wp.exit".equals(name)){
            //唤醒已停止
        }


    }

    private void printLog(String text) {
        if (logTime) {
            text += "  ;time=" + System.currentTimeMillis();
        }
        text += "\n";
        Log.i(getClass().getName(), text);
        txtLog.append(text + "\n");
    }


    private void initView() {
        txtResult = (TextView) findViewById(R.id.txtResult);
        txtLog = (TextView) findViewById(R.id.txtLog);
        btn = (Button) findViewById(R.id.btn);
        stopBtn = (Button) findViewById(R.id.btn_stop);
        txtLog.setText(DESC_TEXT + "\n");
    }


}

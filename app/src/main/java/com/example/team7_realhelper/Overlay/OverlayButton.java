package com.example.team7_realhelper.Overlay;


import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.PixelFormat;
import android.os.Build;
import android.speech.RecognizerIntent;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import com.example.team7_realhelper.MainActivity;
import com.example.team7_realhelper.R;
import com.example.team7_realhelper.chatbot.*;

import java.nio.Buffer;
import java.util.ArrayList;
import java.util.Locale;
import java.util.ServiceLoader;

public class OverlayButton {
    private  final Context context;
    private final OverlayManager manager;
    private final WindowManager windowManager;
    // 송금 버튼
    private Button sendBtn;
    private WindowManager.LayoutParams sendParams;

    // 큐알 결제 버튼
    private Button qrBtn;
    private WindowManager.LayoutParams qrParams;

    // 음성 버튼
    private Button voiceBtn;
    private WindowManager.LayoutParams voiceParams;

    private VoiceService voiceService;

    // 생성자
    public OverlayButton(Context context, OverlayManager manager) {
        this.context = context;
        this.manager = manager;
        windowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);

        voiceService = new VoiceService(context, new VoiceListener() {
            @Override
            public void onSpeechResult(String result) {
                Log.d("VC","결과 도출");
                // 👉 여기서 DialogflowClient.sendTextRequest(...) 호출 가능

                ChatbotService.sendMessageToChatbot(context, result);
                //Log.d("VC", "음성 이후 결과" + fac);
            }

            @Override
            public void onSpeechError(String error) {
                Log.d("VC","에러 발생: " + error);
            }
        });
    }

    public void show(){
        sendBtn = new Button(context);
        qrBtn=new Button(context);
        voiceBtn=new Button(context);

        // 텍스트 설정
        sendBtn.setText("송금");
        qrBtn.setText("큐알 결제");
        voiceBtn.setText("음성");

        // 버튼 디자인 설정
        sendBtn.setBackgroundResource(R.drawable.custom_button);
        qrBtn.setBackgroundResource(R.drawable.custom_button);
        voiceBtn.setBackgroundResource(R.drawable.custom_button);

        sendBtn.setMinHeight(0);
        sendBtn.setMinWidth(0);
        sendBtn.setPadding(0,0,0,0);
        qrBtn.setMinHeight(0);
        qrBtn.setMinWidth(0);
        qrBtn.setPadding(0,0,0,0);
        voiceBtn.setMinHeight(0);
        voiceBtn.setMinWidth(0);
        voiceBtn.setPadding(0,0,0,0);

        int baseX = manager.getIconX()-40;  // 좌측 위치 (x좌표)
        int baseY = manager.getIconY()+150; // 첫 번째 버튼의 y좌표 시작 위치
        int buttonHeight = 60; // 버튼 높이 (LayoutParams 높이와 동일)
        int buttonWidth=250;

        sendParams = new WindowManager.LayoutParams(
                //WindowManager.LayoutParams.WRAP_CONTENT,
                //WindowManager.LayoutParams.WRAP_CONTENT,
                buttonWidth,buttonHeight,
                Build.VERSION.SDK_INT >= Build.VERSION_CODES.O ?
                        WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY :
                        WindowManager.LayoutParams.TYPE_PHONE,
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
                PixelFormat.TRANSLUCENT);

        sendParams.gravity = Gravity.TOP | Gravity.LEFT;
        sendParams.x = baseX;
        sendParams.y = baseY;

        qrParams = new WindowManager.LayoutParams(
                buttonWidth,buttonHeight,
                Build.VERSION.SDK_INT >= Build.VERSION_CODES.O ?
                        WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY :
                        WindowManager.LayoutParams.TYPE_PHONE,
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
                PixelFormat.TRANSLUCENT);

        qrParams.gravity = Gravity.TOP | Gravity.LEFT;
        qrParams.x = baseX;
        qrParams.y = baseY + buttonHeight; // 첫 번째 버튼 아래

        voiceParams = new WindowManager.LayoutParams(
                buttonWidth,buttonHeight,
                Build.VERSION.SDK_INT >= Build.VERSION_CODES.O ?
                        WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY :
                        WindowManager.LayoutParams.TYPE_PHONE,
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
                PixelFormat.TRANSLUCENT);

        voiceParams.gravity = Gravity.TOP | Gravity.LEFT;
        voiceParams.x = baseX;
        voiceParams.y = baseY + buttonHeight * 2; // 두 번째 버튼 아래


        // 버튼 클릭 이벤트
        // 송금 버튼 클릭 시
        sendBtn.setOnClickListener(v->{
            remove();  // 버튼지우고
            manager.setFirstClick(true);   // 다음 클릭시 버튼 다시 뜸

        });

        // qr버튼 클릭 시
        qrBtn.setOnClickListener(v->{
            remove();
            manager.setFirstClick(true);


        });

        // 음성 버튼 클릭 시
        voiceBtn.setOnClickListener(v->{
            remove();
            manager.setFirstClick(true);

            Log.d("VC", "버튼 눌려짐");
            voiceService.startListening();
            Log.d("VC", "버튼 끝남");

        });


        windowManager.addView(sendBtn, sendParams);
        windowManager.addView(qrBtn,qrParams);
        windowManager.addView(voiceBtn,voiceParams);
    }

    public void updatePosition(int x,int y){
        int buttonHeight = 60;
        sendParams.x=x-40;
        sendParams.y=y;
        windowManager.updateViewLayout(sendBtn,sendParams);

        qrParams.x=x-40;
        qrParams.y=y+buttonHeight;
        windowManager.updateViewLayout(qrBtn,qrParams);

        voiceParams.x=x-40;
        voiceParams.y=y+buttonHeight*2;
        windowManager.updateViewLayout(voiceBtn,voiceParams);

    }


    public void remove() {
        if (sendBtn != null) {
            windowManager.removeView(sendBtn);
            sendBtn = null;
        }

        if (qrBtn != null) {
            windowManager.removeView(qrBtn);
            qrBtn = null;
        }

        if (voiceBtn != null) {
            windowManager.removeView(voiceBtn);
            voiceBtn = null;
        }
    }

    private void startVoice(Activity activity) {
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);

        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());
        intent.putExtra(RecognizerIntent.EXTRA_PROMPT, "말을 해주세요");

        try {
            int SPEECH_REQUEST_CODE = 1;
            activity.startActivityForResult(intent, SPEECH_REQUEST_CODE);

        } catch (Exception e) {

        }
    }


}

package com.example.testrestapi;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.ainirobot.coreservice.client.ApiListener;
import com.ainirobot.coreservice.client.listener.TextListener;
import com.ainirobot.coreservice.client.speech.SkillApi;
import com.ainirobot.coreservice.client.speech.SkillCallback;
import com.ainirobot.coreservice.client.speech.entity.TTSEntity;
import com.example.testrestapi.model.IncomingOrder;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.*;
import java.util.Objects;

public class MainActivity extends AppCompatActivity {

    private static final int SLEEP_TIME = 1 * 1000;

    Button btnPrueba;

    private Context mContext;
    private SkillApi mSkillApi;
    private SkillCallback mSkillCallback;
    boolean finishedLastOrder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        System.out.println("Arrancando.....");
        initSkillApi();
        finishedLastOrder = true;

        mContext = this;
        btnPrueba = (Button)findViewById(R.id.btn_prueba);
        btnPrueba.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                playText("This is a test");
            }
        });

        onStartActivity();
    }

    @Override
    protected void onStart() {
        super.onStart();
        System.out.println("ENTRANDO A ON START");
    }

    private void onStartActivity() {
        System.out.println("OnStart.....");
        Thread mainThread = new Thread(new MainLoop(this));
        mainThread.start();
    }


    private void initSkillApi() {
        System.out.println("OnSkill Api........");
        mSkillApi = new SkillApi();
        ApiListener apiListener = new ApiListener() {
            @Override
            public void handleApiDisabled() {
            }

            @Override
            public void handleApiConnected() {
                mSkillApi.registerCallBack(mSkillCallback);
            }

            @Override
            public void handleApiDisconnected() {
            }
        };
        mSkillApi.addApiEventListener(apiListener);
        mSkillApi.connectApi(this);
    }

    public void playText(String text) {
        if (mSkillApi != null) {
            mSkillApi.playText(new TTSEntity("sid-1234567890", text), mTextListener);
        }
    }


    private TextListener mTextListener = new TextListener() {
        @Override
        public void onStart() {
            super.onStart();
            System.out.printf("%1$TH:%1$TM:%1$TS%n", System.currentTimeMillis());
            System.out.println("onstart");
        }

        @Override
        public void onStop() {
            super.onStop();
            System.out.println("onstop");
        }

        @Override
        public void onComplete() {
            super.onComplete();
            finishedLastOrder = true;
            System.out.printf("%1$TH:%1$TM:%1$TS%n", System.currentTimeMillis());
            System.out.println("oncomplete");
        }

        @Override
        public void onError() {
            System.out.println("onerror");
            super.onError();
        }
    };
    private void stopTTS(){
        if(mSkillApi != null){
            mSkillApi.stopTTS();
        }
    }

    private void queryByText(String text){
        if(mSkillApi != null){
            mSkillApi.queryByText(text);
        }
    }

    static class MainLoop implements Runnable{

        private MainActivity ma;

        public MainLoop(MainActivity activity) {
            this.ma = activity;

        }

        @Override
        public void run() {
            System.out.println("run....................");
            IncomingOrder lastOrder = null;
            while(true) {
                if(ma.finishedLastOrder) lastOrder = startListening();
                if(lastOrder != null && ma.finishedLastOrder) {
                    ma.finishedLastOrder = false;
                    System.out.printf("%1$TH:%1$TM:%1$TS%n", System.currentTimeMillis());
                    System.out.println("Procesando orden antes");
                    processOrder(lastOrder);
                    System.out.printf("%1$TH:%1$TM:%1$TS%n", System.currentTimeMillis());
                    System.out.println("Procesando orden despues ");

                } else {
                    try {
//                        System.out.println("sleep");
                        Thread.sleep(SLEEP_TIME);
                    } catch (InterruptedException e) {
                        System.out.println(e.getMessage());
                    }
                }
            }
        }

        private void processOrder(IncomingOrder lastOrder) {

            switch (lastOrder.getAction()) {
                case "talk":
                    try {
                        ma.playText(Objects.requireNonNull(lastOrder.getArguments().get("text")).toString());
                        System.out.println("texto leido: " + Objects.requireNonNull(lastOrder.getArguments().get("text")).toString());
                    }
                    catch(Exception e) {
                        System.out.println(e.getMessage());
                        System.out.println("tipo de argumento no reconocido");
                    }
                    break;
                default:
                    System.out.println("Acción no reconocida");
                    ma.playText("No known action");
                    break;
            }
        }

        private IncomingOrder startListening() {
            IncomingOrder order;

            try {
                URL url = new URL("http:/192.168.10.53:80/api/v1/order/pending_orders");
                URLConnection urlConnection = url.openConnection();

                Reader r = new InputStreamReader(urlConnection.getInputStream());
                BufferedReader br = new BufferedReader(r);
                String linea;
                while ((linea = br.readLine()) != null) {
                    GsonBuilder builder = new GsonBuilder();
                    builder.setPrettyPrinting();
                    Gson gson = builder.create();
                    if (linea.trim().isEmpty()) {
                        return null;
                    }

                    order = gson.fromJson(linea, IncomingOrder.class);
                    System.out.println(order);
                    return order;
                }
                return null;
            } catch (IOException e) {
                System.out.println("catch");
                e.printStackTrace();
                return null;
            }
        }

    }



}

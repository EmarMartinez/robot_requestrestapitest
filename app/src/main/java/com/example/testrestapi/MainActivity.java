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

public class MainActivity extends AppCompatActivity {

    private static final int SLEEP_TIME = 60 * 1000;

    Button btnEscucha;
    Button btnPrueba;

    private Context mContext;
    private SkillApi mSkillApi;
    private SkillCallback mSkillCallback;
    IncomingOrder lastOrder;
    boolean isOrderReceived = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

//        initSkillApi();

        btnEscucha = (Button)findViewById(R.id.iniciar_escucha);
        btnEscucha.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                startListening();
//                checkOrder();
            }
        });
        mContext = this;
        btnPrueba = (Button)findViewById(R.id.btn_prueba);
        btnPrueba.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                playText("This is a test");
            }
        });


    }
    private void checkOrder() {
        while(!isOrderReceived){
            System.out.println("Esperando respuesta de REST");
        }
        System.out.println(lastOrder);
        this.isOrderReceived = false;
    }

    private void initSkillApi() {
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

    private void playText(String text) {
        if (mSkillApi != null) {
            mSkillApi.playText(new TTSEntity("sid-1234567890", text), mTextListener);
        }
    }
    private TextListener mTextListener = new TextListener() {
        @Override
        public void onStart() {
            super.onStart();
        }

        @Override
        public void onStop() {
            super.onStop();
        }

        @Override
        public void onComplete() {
            super.onComplete();
        }

        @Override
        public void onError() {
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

    private void startListening()  {

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    URL url = new URL("http:/192.168.1.58:8080/api/v1/order/pending_orders");
                    URLConnection urlConnection = url.openConnection();

                    Reader r = new InputStreamReader(urlConnection.getInputStream());
                    BufferedReader br = new BufferedReader(r);
                    String linea;
                    while((linea = br.readLine()) != null) {
                        GsonBuilder builder = new GsonBuilder();
                        builder.setPrettyPrinting();
                        Gson gson = builder.create();
                        IncomingOrder order = gson.fromJson(linea, IncomingOrder.class);
                        lastOrder = order;
                        System.out.println(lastOrder);
                        isOrderReceived = true;
//                        System.out.println(order.toString());
                    }


                } catch (IOException e) {
                    System.out.println("catch");
                    e.printStackTrace();
                }
            }
        }).start();


    }



}
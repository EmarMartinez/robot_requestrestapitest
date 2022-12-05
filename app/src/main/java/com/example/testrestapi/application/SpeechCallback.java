package com.example.testrestapi.application;

import android.os.RemoteException;

import com.ainirobot.coreservice.client.speech.SkillCallback;

public class SpeechCallback extends SkillCallback {


    @Override
    public void onSpeechParResult(String s) throws RemoteException {

    }

    @Override
    public void onStart() throws RemoteException {
        System.out.println("Empezando a leer");
    }

    @Override
    public void onStop() throws RemoteException {
        System.out.println("Terminado de leer");
    }

    @Override
    public void onVolumeChange(int i) throws RemoteException {

    }

    @Override
    public void onQueryEnded(int i) throws RemoteException {

    }

    @Override
    public void onQueryAsrResult(String asrResult) throws RemoteException {

    }
}

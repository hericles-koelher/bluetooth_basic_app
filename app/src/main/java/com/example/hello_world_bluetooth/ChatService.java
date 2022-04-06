package com.example.hello_world_bluetooth;

import static android.bluetooth.BluetoothAdapter.STATE_ON;

import android.annotation.SuppressLint;
import android.app.Service;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@SuppressLint("MissingPermission")
public class ChatService extends Service {
    private BluetoothDevice remoteDevice;
    private ExecutorService executorService;

    private static final UUID uuid = UUID.fromString("ee816df4-b4e9-11ec-b909-0242ac120002");

    @Override
    public void onCreate() {
        executorService = Executors.newSingleThreadExecutor();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

        while(bluetoothAdapter.getState() != STATE_ON){
            Log.d("INFO", "Bluetooth ainda não foi ativado...");
        }

        Set<BluetoothDevice> pairedDevices = bluetoothAdapter.getBondedDevices();

        for (BluetoothDevice bt : pairedDevices) {
            if(bt.getName().equals("Galaxy J4+")){
                remoteDevice = bt;
                break;
            }
        }

        BluetoothDevice finalRemoteDevice = remoteDevice;
        executorService.execute(()->{
            // Esse é o servidor...
            if(finalRemoteDevice == null){
                try {
                    BluetoothServerSocket serverSocket = bluetoothAdapter.listenUsingRfcommWithServiceRecord(
                            "Hello_World_Bluetooth", uuid
                    );

                    while (true){
                        try {
                            BluetoothSocket socket = serverSocket.accept();

                            byte[] buffer = new byte[14];

                            socket.getInputStream().read(buffer);

                            Log.d("INFO", new String(buffer, StandardCharsets.UTF_8));

                            socket.getOutputStream().write("Olá Cliente".getBytes());
                        }catch (IOException e) {
                            Log.d("INFO", "Deu merda no loop");
                        }
                    }
                } catch (IOException e) {
                    Log.d("INFO", "Deu merda na criação do servidor");
                }
            }else{
                // esse o cliente...
                try {
                    BluetoothSocket socket = finalRemoteDevice.createRfcommSocketToServiceRecord(uuid);

                    socket.connect();
                    Log.d("INFO", "Conectado");

                    socket.getOutputStream().write("Olá Servidor!".getBytes());
                    Log.d("INFO", "Mensagem Enviada");

                    byte[] buffer = new byte[12];
                    socket.getInputStream().read(buffer);

                    Log.d("INFO", "Resposta: "+ new String(buffer, StandardCharsets.UTF_8));
                } catch (IOException e) {
                    Log.d("INFO", "Deu merda");
                }
            }
        });

        return START_NOT_STICKY;
    }

    @Override
    public void onDestroy() {
        executorService.shutdown();
        super.onDestroy();
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        return null;
    }
}
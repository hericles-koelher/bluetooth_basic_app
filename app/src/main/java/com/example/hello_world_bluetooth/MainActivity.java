package com.example.hello_world_bluetooth;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.bluetooth.BluetoothAdapter;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Switch;
import android.widget.Toast;

@SuppressLint("MissingPermission")
public class MainActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

        if (bluetoothAdapter == null) {
            Toast.makeText(
                    this,
                    "Bluetooth Not Supported",
                    Toast.LENGTH_SHORT
            ).show();

            finish();
        }

        @SuppressLint("UseSwitchCompatOrMaterialCode")
        Switch sw = findViewById(R.id.enableBluetooth);

        sw.setOnCheckedChangeListener(
                (compoundButton, isChecked) -> {
                    if (isChecked) {
                        startActivity(new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE));

                        startService(new Intent(this, ChatService.class));

                        Toast.makeText(
                                this, "Turned On", Toast.LENGTH_SHORT
                        ).show();
                    } else {
                        stopService(new Intent(this, ChatService.class));

                        bluetoothAdapter.disable();

                        Toast.makeText(
                                this, "Turned Off", Toast.LENGTH_SHORT
                        ).show();
                    }
                }
        );

        if (bluetoothAdapter.isEnabled()) {
            sw.setChecked(true);
        }
    }
}
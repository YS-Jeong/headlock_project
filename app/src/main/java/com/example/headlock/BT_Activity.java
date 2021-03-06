package com.example.headlock;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.headlock.ConnectedThread;

import com.example.headlock.R;
import com.example.headlock.Util;

import java.io.IOException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Set;
import java.util.UUID;

public class BT_Activity extends AppCompatActivity {

    // Member Variable
    private static final String TAG = "BT_Activity";

    // 블루투스 허용 상수
    public static final int REQUEST_ENABLE_BT = 1000;
    private final UUID BT_MODULE_UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");


    private ListView BT_list;

    private ArrayList<String> address_data;
    private ArrayAdapter<String> BTArrayAdapter;

    private BluetoothAdapter BTAdapter;
    private BluetoothSocket btSocket1 = null;
    private BluetoothSocket btSocket2 = null;
    private BluetoothServerSocket tmp = null;


    public int count = 0;
    private Handler mBluetoothHandler;
    final static int BT_CONNECTING_STATUS = 3;

    // Member Method - override
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_b_t_);
        Log.i(TAG, "onCreate");
        String[] permission_list = {
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.BLUETOOTH,
                Manifest.permission.BLUETOOTH_ADMIN
        };

        ActivityCompat.requestPermissions(BT_Activity.this, permission_list, 1);

        init();

        BTAdapter = BluetoothAdapter.getDefaultAdapter();


        // 블루투스가 비활성화 상태이면 활성화를 위한 Intent 시작
        if (!BTAdapter.isEnabled()) {
            Log.i(TAG, "onCreate - if");
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
        } else {
            Log.i(TAG, "onCreate - else");
            SelectBT(); // 페어링된 블루투스 선택 메소드

        }
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_ENABLE_BT) {
            // 블루투스를 허용하면 페어링된 블루투스 선택 메소드 호출
            if (resultCode == RESULT_OK) {
                SelectBT();
            } else {
                // 블루투스 허용 안 했을때 코드
                moveTaskToBack(true);                        // 태스크를 백그라운드로 이동
                finishAndRemoveTask();                        // 액티비티 종료 + 태스크 리스트에서 지우기
                android.os.Process.killProcess(android.os.Process.myPid());
            }
        }

    }

    // Member Method - custom
    private void init() {
        Intent intent = getIntent();
        int headlock = intent.getExtras().getInt("headlock");
        BT_list = findViewById(R.id.BT_list);

        // listItem 클릭시 블루투스 연결
        BT_list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String BTname = BTArrayAdapter.getItem(position);
                String BTaddress = address_data.get(position);
                boolean flag = true;

                BluetoothDevice device1 = BTAdapter.getRemoteDevice(BTaddress);
                BluetoothDevice device2 = BTAdapter.getRemoteDevice(BTaddress);
                // 소켓 생성 및 연결
               if (headlock == 0) {

                    try {
                        btSocket1 = device1.createRfcommSocketToServiceRecord(BT_MODULE_UUID);
                        btSocket1.connect();
                        MainActivity.changeTextView(0);

                    } catch (IOException e) {

                        flag = false;
                        Toast.makeText(BT_Activity.this, "헬멧 연결 실패", Toast.LENGTH_SHORT).show();
                        e.printStackTrace();
                    }
                    if (flag) {
                        Toast.makeText(BT_Activity.this, "헬멧 연결 성공!", Toast.LENGTH_SHORT).show();
                        Log.d(TAG, "디바이스1 찾기 : " + device1.getName() + " - " + device1.getAddress());
                        Util.connectedThread = new ConnectedThread(btSocket1);
                        Util.connectedThread.start();
                        Log.d(TAG, "signal : 헬멧으로");
                        finish();
                    }

                   // count++;
                }
                else if (headlock == 1) {

                    try {
                        btSocket2 = device2.createRfcommSocketToServiceRecord(BT_MODULE_UUID);
                        btSocket2.connect();
                        MainActivity.changeTextView(1);
                    } catch (IOException e) {
                        flag = false;
                        Toast.makeText(BT_Activity.this, "킥보드 연결 실패", Toast.LENGTH_SHORT).show();
                        e.printStackTrace();
                    }
                    if (flag) {
                        Toast.makeText(BT_Activity.this, "킥보드 연결 성공!", Toast.LENGTH_SHORT).show();
                        Log.d(TAG, "디바이스1 찾기 : " + device2.getName() + " - " + device2.getAddress() + "디바이스2 찾기 : " + device2.getName() + " - " + device2.getAddress());
                        Util.connectedThread2 = new ConnectedThread2(btSocket2);
                        Util.connectedThread2.start();
                        Log.d(TAG, "signal : 킥보드로");
                       finish();
                    }
                }
            }
        });
        //name_data = new ArrayList<>();
        BTArrayAdapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1);
        address_data = new ArrayList<>();

        BT_list.setAdapter(BTArrayAdapter);
    }

    private void SelectBT() {
        Log.i(TAG, "SelectBT");

        //BTArrayAdapter.clear();
        if ((address_data != null) && !address_data.isEmpty()) {
            // address_data.clear();
        }

        Set<BluetoothDevice> pairedDevices = BTAdapter.getBondedDevices();
        if (pairedDevices.size() > 0) {
            Log.i(TAG, "SelectBT - paired if");

            for (BluetoothDevice device : pairedDevices) {
                String deviceName = device.getName();
                String deviceHardwareAddress = device.getAddress(); // MAC address
                Log.i(TAG, "SelectBT - name : " + deviceName + " address : " + deviceHardwareAddress);

                BTArrayAdapter.add(deviceName);
                address_data.add(deviceHardwareAddress);
            }
        }
    }

    public void onClick(View v) {
        if (BTAdapter.isDiscovering()) {
            //   BTAdapter.cancelDiscovery();
        } else {
            if (BTAdapter.isEnabled()) {
                BTAdapter.startDiscovery();
                // BTArrayAdapter.clear();
                // address_data.clear();

                if (BTArrayAdapter != null && !BTArrayAdapter.isEmpty()) {
                    BTArrayAdapter.clear();
                    address_data.clear();
                }
                IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
                registerReceiver(receiver, filter);
            } else {
                Toast.makeText(getApplicationContext(), "bluetooth not on", Toast.LENGTH_SHORT).show();
            }
        }
    }

    // Create a BroadcastReceiver for ACTION_FOUND.
    private final BroadcastReceiver receiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                // Discovery has found a device. Get the BluetoothDevice
                // object and its info from the Intent.
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                if (device.getName() == null) return;
                if (address_data.contains(device.getAddress())) return;
                Log.d(TAG, "null 찾기 : " + device.getName() + " - " + device.getAddress());
                String deviceName = device.getName();
                String deviceHardwareAddress = device.getAddress();
                BTArrayAdapter.add(deviceName);
                address_data.add(deviceHardwareAddress);
                BTArrayAdapter.notifyDataSetChanged();// MAC address
            }
        }
    };



    @Override
    protected void onDestroy() {
        super.onDestroy();

        // Don't forget to unregister the ACTION_FOUND receiver.
        try {
         //  unregisterReceiver(receiver);
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        }
    }


/*
    private BluetoothSocket createBluetoothSocket(BluetoothDevice device) throws IOException {
        try {
            final Method m = device.getClass().getMethod("createInsecureRfcommSocketToServiceRecord", UUID.class);
            return (BluetoothSocket) m.invoke(device, BT_MODULE_UUID);
        } catch (Exception e) {
            Log.e(TAG, "Could not create Insecure RFComm Connection", e);
        }
        return device.createRfcommSocketToServiceRecord(BT_MODULE_UUID);
    }

 */

    /*
    private class AcceptThread extends Thread {
        private final BluetoothServerSocket mmServerSocket;

        public AcceptThread() {
            // Use a temporary object that is later assigned to mmServerSocket,
            // because mmServerSocket is final

            try {
                // MY_UUID is the app's UUID string, also used by the client code
                tmp = BTAdapter.listenUsingRfcommWithServiceRecord("server", BT_MODULE_UUID);
            } catch (IOException e) {
            }
            mmServerSocket = tmp;
        }

        public void run() {
            BluetoothSocket socket = null;
            // Keep listening until exception occurs or a socket is returned
            while (true) {
                try {
                    socket = mmServerSocket.accept();
                } catch (IOException e) {
                    break;
                }
                // If a connection was accepted
                if (socket != null) {
                    // Do work to manage the connection (in a separate thread)
                    manageConnectedSocket(socket);
                    mmServerSocket.close();
                    break;
                }
            }
        }
    }

     */
}



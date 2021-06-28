package com.example.headlock;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.bluetooth.BluetoothDevice;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Base64;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import net.daum.mf.map.api.MapView;

import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import app.akexorcist.bluetotohspp.library.BluetoothSPP;
import app.akexorcist.bluetotohspp.library.BluetoothState;
import app.akexorcist.bluetotohspp.library.DeviceList;
import static com.example.headlock.Util.connectedThread;
import static com.example.headlock.Util.connectedThread2;


public class MainActivity extends AppCompatActivity {
    // 로그 확인
    private static final String TAG = "MainActivity";
    private BluetoothSPP bt;

    // xml variable
    private ImageView logo_img;
    private Button on_BTN, off_BTN, h_btnConnect, k_btnConnect, cancelBT;
    private EditText idEDIT, pwdEDIT;
    private String id, pwd, a;
    private Object ConnectedThread;
    public static TextView h_stateTXT, k_stateTXT, stateTXT, breakTXT;
    private Button motor_on, motor_off, helmet_LED_on, helmet_LED_off;
    private Handler mBluetoothHandler;
    final static int BT_MESSAGE_READ = 2;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

//        bt = new BluetoothSPP(this);
//
//        bt.setOnDataReceivedListener(new BluetoothSPP.OnDataReceivedListener() {
//            //데이터 수신
//            public void onDataReceived(byte[] data, String message) {
//                Toast.makeText(MainActivity.this, message, Toast.LENGTH_SHORT).show();
//                Log.d(TAG, "signal : AA");
//            }
//        });

        init();

            if(stateTXT.getText() == "착용"){
                connectedThread2.write("C");
                Log.d(TAG, "signal : CC");
                Toast.makeText(this, "Moter on", Toast.LENGTH_SHORT).show();
            }
            else if(stateTXT.getText() == "미착용"){
                connectedThread2.write("D");
                Log.d(TAG, "signal : DD");
                Toast.makeText(this, "Moter off", Toast.LENGTH_SHORT).show();
            }
            else if(breakTXT.getText() == "작동"){
                connectedThread.write("E");
                Log.d(TAG, "signal : EE");
                Toast.makeText(this, "Break LED on", Toast.LENGTH_SHORT).show();
            }
            else if(breakTXT.getText() == "미작동") {
                connectedThread.write("F");
                Log.d(TAG, "signal : FF");
                Toast.makeText(this, "Break LED off", Toast.LENGTH_SHORT).show();
            }



    }


    public static void changeTextView(int num) {
        switch (num) {
            case 0: {
                h_stateTXT.setText("연결완료");
                break;
            }
            case 1: {
                k_stateTXT.setText("연결완료");

                break;
            }
            case 2: {
                stateTXT.setText("착용");

                break;
            }
            case 3: {
                stateTXT.setText("미착용");
                break;
            }
            case 4: {
                breakTXT.setText("작동");

                break;
            }
            case 5: {
                breakTXT.setText("미작동");
                break;
            }
        }

    }

//    private void StateChange() {
//        if(stateTXT.getText() == "착용"){
//            connectedThread2.write("C");
//            Log.d(TAG, "signal : CC");
//            Toast.makeText(this, "Moter on", Toast.LENGTH_SHORT).show();
//        }
//        else if(stateTXT.getText() != "미착용"){
//            connectedThread2.write("D");
//            Log.d(TAG, "signal : DD");
//            Toast.makeText(this, "Moter off", Toast.LENGTH_SHORT).show();
//        }
//        else if(breakTXT.getText() == "작동"){
//            connectedThread.write("E");
//            Log.d(TAG, "signal : EE");
//            Toast.makeText(this, "Break LED on", Toast.LENGTH_SHORT).show();
//        }
//        else if(breakTXT.getText() != "미작동"){
//            connectedThread.write("F");
//            Log.d(TAG, "signal : FF");
//            Toast.makeText(this, "Break LED off", Toast.LENGTH_SHORT).show();
//        }
//
//    }

    private void init() {
        h_btnConnect = (Button) findViewById(R.id.h_btnConnect);
        k_btnConnect = (Button) findViewById(R.id.k_btnConnect);
        cancelBT = (Button) findViewById(R.id.cancelBT);
        h_stateTXT = (TextView) findViewById(R.id.h_stateTXT);
        k_stateTXT = (TextView) findViewById(R.id.k_stateTXT);
        stateTXT = (TextView) findViewById(R.id.stateTXT);
        breakTXT = (TextView) findViewById(R.id.breakTXT);
        motor_on = (Button) findViewById(R.id.motor_on);
        motor_off = (Button) findViewById(R.id.motor_off);
        helmet_LED_on = (Button) findViewById(R.id.helmet_LED_on);
        helmet_LED_off = (Button) findViewById(R.id.helmet_LED_off);

    }


    public void onClick(View v) {

        //       LED 제어


        if (v.getId() == R.id.motor_on) {
            if (connectedThread != null && connectedThread2 != null) {
                stateTXT.setText("착용");
                connectedThread2.write("C");
                //databaseReference.child(clickid).child("value").setValue("H");
                Log.d(TAG, "signal : motor on");
            }
        } else if (v.getId() == R.id.motor_off) {
            if (connectedThread != null && connectedThread2 != null) {
                stateTXT.setText("미착용");
                connectedThread2.write("D");
                //databaseReference.child(clickid).child("value").setValue("F");
                Log.d(TAG, "signal : motor off");
            }
            } else if (v.getId() == R.id.helmet_LED_on) {
                if (connectedThread != null && connectedThread2 != null) {
                    breakTXT.setText("작동");
                    connectedThread.write("E");
                    //databaseReference.child(clickid).child("value").setValue("H");
                    Log.d(TAG, "signal : break led on");
                }
            } else if (v.getId() == R.id.helmet_LED_off) {
                if (connectedThread != null && connectedThread2 != null) {
                    breakTXT.setText("미작동");
                    connectedThread.write("F");
                    //databaseReference.child(clickid).child("value").setValue("F");
                    Log.d(TAG, "signal : break led off");
                }}
/*
        if (v.getId() == R.id.on_BTN) {
            //if (connectedThread != null){
            if (connectedThread != null && connectedThread2 != null) {
                connectedThread.write("A");
                connectedThread2.write("A");
                //databaseReference.child(clickid).child("value").setValue("H");
                Log.d(TAG, "signal : AA");
                Toast.makeText(this, "on", Toast.LENGTH_SHORT).show();
            }
        } else if (v.getId() == R.id.off_BTN) {
            //if (connectedThread != null){
            if (connectedThread != null && connectedThread2 != null) {
                connectedThread.write("B");
                connectedThread2.write("B");
                //databaseReference.child(clickid).child("value").setValue("F");
                Log.d(TAG, "signal : BB");

                Toast.makeText(this, "off", Toast.LENGTH_SHORT).show();

            }



 */

                if (v.getId() == R.id.h_btnConnect) {
                    Log.d(TAG, "signal : 헬멧");
                    //헬멧 블루투스 인텐트
                    Intent intent = new Intent(MainActivity.this, BT_Activity.class);
                    intent.putExtra("headlock", 0);
                    startActivity(intent);
                } else if (v.getId() == R.id.k_btnConnect) {
                    Log.d(TAG, "signal : 킥보드");
                    //킥보드 블루투스 인텐트
                    Intent intent = new Intent(MainActivity.this, BT_Activity.class);
                    intent.putExtra("headlock", 1);
                    startActivity(intent);

                } else if (v.getId() == R.id.cancelBT) {
                    if (connectedThread != null && connectedThread2 != null) {
                        connectedThread.cancel();
                        connectedThread2.cancel();
                        h_stateTXT.setText("연결하세요");
                        k_stateTXT.setText("연결하세요");
                    } else if (connectedThread != null) {
                        connectedThread.cancel();
                        h_stateTXT.setText("연결하세요");
                    } else if (connectedThread2 != null) {
                        connectedThread2.cancel();
                        k_stateTXT.setText("연결하세요");
                    }

                }
            }

    private void getHashKey(){
        PackageInfo packageInfo = null;
        try {
            packageInfo = getPackageManager().getPackageInfo(getPackageName(), PackageManager.GET_SIGNATURES);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        if (packageInfo == null)
            Log.e("KeyHash", "KeyHash:null");

        for (Signature signature : packageInfo.signatures) {
            try {
                MessageDigest md = MessageDigest.getInstance("SHA");
                md.update(signature.toByteArray());
                Log.d("KeyHash", Base64.encodeToString(md.digest(), Base64.DEFAULT));
            } catch (NoSuchAlgorithmException e) {
                Log.e("KeyHash", "Unable to get MessageDigest. signature=" + signature, e);
            }
        }
    }

//    public boolean onLongClick(View v) {
//        if (v.getId() == R.id.h_btnConnect) {
//            connectedThread.cancel();
//            Toast.makeText(getApplicationContext(),
//                    "연결 해제", Toast.LENGTH_SHORT).show();
//            k_stateTXT.setText("연결하세요");
//        } else if (v.getId() == R.id.k_btnConnect) {
//            connectedThread.cancel();
//            Toast.makeText(getApplicationContext(),
//                    "연결 해제", Toast.LENGTH_SHORT).show();
//            k_stateTXT.setText("연결하세요");
//        }
//
//
//    }


//    public void onDataReceived(byte[] data, String message) {
//
//
//        String[] array = message.split(",");
//
//        stateTXT.setText(array[0].concat("C"));
//       // humd.setText(array[1].concat("%") );
//
//    }


        }






    

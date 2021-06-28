package com.example.headlock;

import android.bluetooth.BluetoothSocket;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;

public class ConnectedThread2 extends Thread {

    // Member Variable
    private static final String TAG = "ConnectedThread";
    private  BluetoothSocket mmSocket;
   // private final BluetoothSocket mmSocket2;
    private  InputStream mmInStream;
    private  OutputStream mmOutStream;
    final static int BT_MESSAGE_READ = 2; // 블루투스가 보내는 메세지를 읽었을 때 반환하는 메세지 코드
    private Handler mBluetoothHandler;


    // Constructor
    public ConnectedThread2(BluetoothSocket socket) {
        mmSocket = socket;
        //mmSocket2 = socket;

        InputStream tmpIn = null;
        OutputStream tmpOut = null;

        // Get the input and output streams, using temp objects because
        // member streams are final
        try {
            tmpIn = socket.getInputStream();
            tmpOut = socket.getOutputStream();
        } catch (IOException e) {
            Log.e(TAG, "소켓이 생성되지 않았습니다.", e);
        }
        mmInStream = tmpIn;
        mmOutStream = tmpOut;
        Log.i(TAG, "mmInStream : " + mmInStream + " mmOutStream : " + mmOutStream);

        mBluetoothHandler = new Handler() {
            public void handleMessage(android.os.Message msg) {
                if (msg.what == BT_MESSAGE_READ) {
                    String readMsg = null;
                    try {
                        readMsg = new String((byte[]) msg.obj, "ASCII");
                       // readMsg = new String((byte[]) msg.obj,0,1);
                        Log.d(TAG, "signal : handler2 " + msg);
                    } catch (UnsupportedEncodingException e) {
                        e.printStackTrace();
                    }
                    if(readMsg == "C")
                        MainActivity.changeTextView(4);
                    else if(readMsg == "D")
                        MainActivity.changeTextView(5);
                }
            }
        };
    }


    // run()의 경우 정확히 어떤 동작을 하는지 이해하지 못했음. 좀 더 학습이 필요함

    @Override
    public void run() {
        Log.i(TAG, "Run mconnectedThread");
        byte[] buffer = new byte[1024];  // buffer store for the stream
        int bytes; // bytes returned from read()

        // Keep listening to the InputStream until an exception occurs

        while (true) {
            try {
                // Read from the InputStream
                //int byteAvailable = mmInStream.available();
                bytes = mmInStream.read(buffer);
                //bytes = mmInStream.available();

                if (bytes > 0) {
                    buffer = new byte[1024];
                    SystemClock.sleep(100);
                    bytes = mmInStream.available();
                    //bytes = mmInStream.read(buffer, 0, bytes);
                    mBluetoothHandler.obtainMessage(BT_MESSAGE_READ, bytes, -1, buffer).sendToTarget();
                    String readMsg = new String(buffer, 0, bytes);
                    //mBluetoothHandler.obtainMessage(BT_MESSAGE_READ, bytes, -1, buffer).sendToTarget();
                    Log.d(TAG, "signal : 받음2 " + readMsg);
                    //readMsg = new String(buffer,"");

                }
            } catch (IOException e) {
                e.printStackTrace();
                break;
            }
        }
    }

    /* Call this from the main activity to send data to the remote device */
    /*
    public void write(String input) {
        byte[] bytes = input.getBytes();           // 입력받은 문자열을 바이트 배열로 변환
        Log.i(TAG, "Write");
        try {
            Log.i(TAG, "write - try");
            mmOutStream.write(bytes);
        } catch (IOException e) {
            Log.d(TAG, "전송에 실패했습니다.");
        }
    }
*/

    /* Call this from the main activity to send data to the remote device */
    public void write(String input) {
        byte[] bytes = input.getBytes();           //converts entered String into bytes
        try {
            mmOutStream.write(bytes);
            Log.d(TAG, "signal : 보냄");
        } catch (IOException e) {
        }
    }

    /* Call this from the main activity to shutdown the connection */
    public void cancel() {
        try {
            mmSocket.close();
        } catch (IOException e) {
        }
    }

    /*

    // 버튼을 클릭했을 때 실행되는 메소드
    public void ClickBTN(char bytes) {
        Log.i(TAG, "ClickBTN");
        try {
            Log.i(TAG, "ClickBTN - try");
            mmOutStream.write(bytes);
        } catch (IOException e) {
            Log.d(TAG,"전송에 실패했습니다.");
        }
    }

    /* Call this from the main activity to shutdown the connection */
 /*
    public void cancel() {
        try {
            mmSocket.close();
        } catch (IOException e) {
            Log.e(TAG,"소켓을 닫을 수 없습니다.");
        }
    }

*/
}
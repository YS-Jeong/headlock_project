package com.example.headlock;

import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.lang.annotation.Target;
import java.text.BreakIterator;

public class ConnectedThread extends Thread {
    // Member Variable
    private static final String TAG = "ConnectedThread";
    private BluetoothSocket mmSocket;
    // private final BluetoothSocket mmSocket2;
    private InputStream mmInStream;
    private OutputStream mmOutStream;
    private int readBufferPosition;
    final static int BT_MESSAGE_READ = 2; // 블루투스가 보내는 메세지를 읽었을 때 반환하는 메세지 코드
    private Handler mBluetoothHandler;
    private Thread workerThread = null;




    // Constructor
    public ConnectedThread(BluetoothSocket socket) {
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
                        //readMsg = new String((byte[]) msg.obj, "UTF-8");
                        readMsg = new String((byte[]) msg.obj,"ASCII");
                        Log.d(TAG, "signal : handler " + readMsg);
//                        if(readMsg == "A")
//                            MainActivity.changeTextView(2);
//                        else if(readMsg == "B")
//                            MainActivity.changeTextView(3);

                    } catch (UnsupportedEncodingException e) {
                        e.printStackTrace();
                    }
                    switch (readMsg){
                        case "A" :
                            MainActivity.changeTextView(2);
                            break;
                        case "B" :
                            MainActivity.changeTextView(3);
                            break;

                    }

                    Log.d(TAG, "signal : handler111 " + readMsg);
                }
            }
        };
    }



//    public String bytesToBinaryString(Byte b) {
//        StringBuilder builder = new StringBuilder();
//        for (int i = 0; i < 8; i++) {
//            builder.append(((0x80 >>> i) & b) == 0 ? '0' : '1');
//        }
//
//        return builder.toString();
//    }
//    public void receiveData() {
//        final Handler handler = new Handler();
//        // 데이터를 수신하기 위한 버퍼를 생성
//        readBufferPosition = 0;
//        readBuffer = new byte[1024];
//
//        // 데이터를 수신하기 위한 쓰레드 생성
//        workerThread = new Thread(new Runnable() {
//            @Override
//            public void run() {
//                while(Thread.currentThread().isInterrupted()) {
//                    try {
//                        // 데이터를 수신했는지 확인합니다.
//                        int byteAvailable = mmInStream.available();
//                        Log.e(TAG, "받았니."+byteAvailable);
//                        // 데이터가 수신 된 경우
//                        if(byteAvailable > 0) {
//                            // 입력 스트림에서 바이트 단위로 읽어 옵니다.
//                            byte[] bytes = new byte[byteAvailable];
//                            mmInStream.read(bytes);
//                            // 입력 스트림 바이트를 한 바이트씩 읽어 옵니다.
//                            for(int i = 0; i < byteAvailable; i++) {
//                                byte tempByte = bytes[i];
//                                // 개행문자를 기준으로 받음(한줄)
//                                if(tempByte == '\n') {
//                                    // readBuffer 배열을 encodedBytes로 복사
//                                    byte[] encodedBytes = new byte[readBufferPosition];
//                                    System.arraycopy(readBuffer, 0, encodedBytes, 0, encodedBytes.length);
//                                    // 인코딩 된 바이트 배열을 문자열로 변환
//                                    final String text = new String(encodedBytes, "US-ASCII");
//                                    readBufferPosition = 0;
//                                    handler.post(new Runnable() {
//                                        @Override
//                                        public void run() {
//                                            // 텍스트 뷰에 출력
//                                           // textViewReceive.append(text + "\n");
//                                            MainActivity.changeTextView(2);
//                                            Log.d(TAG, "signal : text " + text);
//
//                                        }
//                                    });
//                                } // 개행 문자가 아닐 경우
//                                else {
//                                    readBuffer[readBufferPosition++] = tempByte;
//                                }
//                            }
//                        }
//                    } catch (IOException e) {
//                        e.printStackTrace();
//                    }
//                    try {
//                        // 1초마다 받아옴
//                        Thread.sleep(1000);
//                    } catch (InterruptedException e) {
//                        e.printStackTrace();
//                    }
//                }
//            }
//        });
//        workerThread.start();
//    }




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
                            String readMsg = new String(buffer, 0, bytes);
                            mBluetoothHandler.obtainMessage(BT_MESSAGE_READ, bytes, -1, buffer).sendToTarget();
                            Log.d(TAG, "signal : 받음1 " + bytes);
                            //readMsg = new String(buffer,"");

                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                        break;
                    }
                }
            }




            /* Call this from the main activity to send data to the remote device */
            public void write(String input) {
                byte[] bytes = input.getBytes();           //converts entered String into bytes
                try {
                    mmOutStream.write(bytes);
                    Log.d(TAG, "signal : 보냄1");
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


        }
package com.project.messmanagement;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.UUID;

public class BluetoothChatService {
    private static final String TAG = "BT_ChatService";
    private static final String NAME = "MessMateChat";
    private static final UUID MY_UUID = UUID.fromString("fa87c0d0-afac-11de-8a39-0800200c9a66");

    private final BluetoothAdapter mAdapter;
    private final Handler mHandler;
    private AcceptThread mAcceptThread;
    private ConnectThread mConnectThread;
    private ConnectedThread mConnectedThread;
    private int mState;

    public static final int STATE_NONE = 0;
    public static final int STATE_LISTEN = 1;
    public static final int STATE_CONNECTING = 2;
    public static final int STATE_CONNECTED = 3;

    public BluetoothChatService(Context context, Handler handler) {
        mAdapter = BluetoothAdapter.getDefaultAdapter();
        mState = STATE_NONE;
        mHandler = handler;
    }

    private synchronized void setState(int state) {
        mState = state;
        mHandler.obtainMessage(BTChatActivity.MESSAGE_STATE_CHANGE, state, -1).sendToTarget();
    }

    public synchronized int getState() {
        return mState;
    }

    public synchronized void start() {
        if (mConnectThread != null) { mConnectThread.cancel(); mConnectThread = null; }
        if (mConnectedThread != null) { mConnectedThread.cancel(); mConnectedThread = null; }
        setState(STATE_LISTEN);
        if (mAcceptThread == null) {
            mAcceptThread = new AcceptThread();
            mAcceptThread.start();
        }
    }

    public synchronized void connect(BluetoothDevice device) {
        if (mState == STATE_CONNECTING) {
            if (mConnectThread != null) { mConnectThread.cancel(); mConnectThread = null; }
        }
        if (mConnectedThread != null) { mConnectedThread.cancel(); mConnectedThread = null; }
        mConnectThread = new ConnectThread(device);
        mConnectThread.start();
        setState(STATE_CONNECTING);
    }

    @android.annotation.SuppressLint("MissingPermission")
    public synchronized void connected(BluetoothSocket socket, BluetoothDevice device) {
        if (mConnectThread != null) { mConnectThread.cancel(); mConnectThread = null; }
        if (mConnectedThread != null) { mConnectedThread.cancel(); mConnectedThread = null; }
        if (mAcceptThread != null) { mAcceptThread.cancel(); mAcceptThread = null; }
        mConnectedThread = new ConnectedThread(socket);
        mConnectedThread.start();
        Message msg = mHandler.obtainMessage(BTChatActivity.MESSAGE_DEVICE_NAME);
        Bundle bundle = new Bundle();
        bundle.putString(BTChatActivity.DEVICE_NAME, device.getName());
        msg.setData(bundle);
        mHandler.sendMessage(msg);
        setState(STATE_CONNECTED);
    }

    public synchronized void stop() {
        if (mConnectThread != null) { mConnectThread.cancel(); mConnectThread = null; }
        if (mConnectedThread != null) { mConnectedThread.cancel(); mConnectedThread = null; }
        if (mAcceptThread != null) { mAcceptThread.cancel(); mAcceptThread = null; }
        setState(STATE_NONE);
    }

    public void write(byte[] out) {
        ConnectedThread r;
        synchronized (this) {
            if (mState != STATE_CONNECTED) return;
            r = mConnectedThread;
        }
        r.write(out);
    }

    private void connectionFailed() {
        Message msg = mHandler.obtainMessage(BTChatActivity.MESSAGE_TOAST);
        Bundle bundle = new Bundle();
        bundle.putString(BTChatActivity.TOAST, "Unable to connect device");
        msg.setData(bundle);
        mHandler.sendMessage(msg);
        BluetoothChatService.this.start();
    }

    private void connectionLost() {
        Message msg = mHandler.obtainMessage(BTChatActivity.MESSAGE_TOAST);
        Bundle bundle = new Bundle();
        bundle.putString(BTChatActivity.TOAST, "Device connection was lost");
        msg.setData(bundle);
        mHandler.sendMessage(msg);
        BluetoothChatService.this.start();
    }

    private class AcceptThread extends Thread {
        private final BluetoothServerSocket mmServerSocket;
        public AcceptThread() {
            BluetoothServerSocket tmp = null;
            try { mmServerSocket = mAdapter.listenUsingRfcommWithServiceRecord(NAME, MY_UUID); } 
            catch (IOException e) { Log.e(TAG, "listen() failed", e); throw new RuntimeException(e); }
        }
        public void run() {
            BluetoothSocket socket = null;
            while (mState != STATE_CONNECTED) {
                try { socket = mmServerSocket.accept(); } 
                catch (IOException e) { break; }
                if (socket != null) {
                    synchronized (BluetoothChatService.this) {
                        switch (mState) {
                            case STATE_LISTEN:
                            case STATE_CONNECTING:
                                connected(socket, socket.getRemoteDevice());
                                break;
                            case STATE_NONE:
                            case STATE_CONNECTED:
                                try { socket.close(); } catch (IOException e) {}
                                break;
                        }
                    }
                }
            }
        }
        public void cancel() { try { mmServerSocket.close(); } catch (IOException e) {} }
    }

    private class ConnectThread extends Thread {
        private final BluetoothSocket mmSocket;
        private final BluetoothDevice mmDevice;
        public ConnectThread(BluetoothDevice device) {
            mmDevice = device;
            BluetoothSocket tmp = null;
            try { tmp = device.createRfcommSocketToServiceRecord(MY_UUID); } 
            catch (IOException e) {}
            mmSocket = tmp;
        }
        public void run() {
            mAdapter.cancelDiscovery();
            try { mmSocket.connect(); } 
            catch (IOException e) {
                try { mmSocket.close(); } catch (IOException e2) {}
                connectionFailed();
                return;
            }
            synchronized (BluetoothChatService.this) { mConnectThread = null; }
            connected(mmSocket, mmDevice);
        }
        public void cancel() { try { mmSocket.close(); } catch (IOException e) {} }
    }

    private class ConnectedThread extends Thread {
        private final BluetoothSocket mmSocket;
        private final InputStream mmInStream;
        private final OutputStream mmOutStream;
        public ConnectedThread(BluetoothSocket socket) {
            mmSocket = socket;
            InputStream tmpIn = null;
            OutputStream tmpOut = null;
            try {
                tmpIn = socket.getInputStream();
                tmpOut = socket.getOutputStream();
            } catch (IOException e) {}
            mmInStream = tmpIn;
            mmOutStream = tmpOut;
        }
        public void run() {
            byte[] buffer = new byte[1024];
            int bytes;
            while (true) {
                try {
                    bytes = mmInStream.read(buffer);
                    mHandler.obtainMessage(BTChatActivity.MESSAGE_READ, bytes, -1, buffer).sendToTarget();
                } catch (IOException e) {
                    connectionLost();
                    break;
                }
            }
        }
        public void write(byte[] buffer) {
            try {
                mmOutStream.write(buffer);
                mHandler.obtainMessage(BTChatActivity.MESSAGE_WRITE, -1, -1, buffer).sendToTarget();
            } catch (IOException e) {}
        }
        public void cancel() { try { mmSocket.close(); } catch (IOException e) {} }
    }
}

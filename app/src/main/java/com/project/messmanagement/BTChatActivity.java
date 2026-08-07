package com.project.messmanagement;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.annotation.SuppressLint;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import java.util.ArrayList;
import java.util.Set;

public class BTChatActivity extends AppCompatActivity {

    private static final String TAG = "BTChatActivity";
    
    // Message types sent from the BluetoothChatService Handler
    public static final int MESSAGE_STATE_CHANGE = 1;
    public static final int MESSAGE_READ = 2;
    public static final int MESSAGE_WRITE = 3;
    public static final int MESSAGE_DEVICE_NAME = 4;
    public static final int MESSAGE_TOAST = 5;

    // Key names received from the BluetoothChatService Handler
    public static final String DEVICE_NAME = "device_name";
    public static final String TOAST = "toast";

    // Intent request codes
    private static final int REQUEST_ENABLE_BT = 3;
    private static final int PERMISSION_REQUEST_CODE = 101;

    private ListView mConversationView;
    private EditText mOutEditText;
    private ImageButton mSendButton;
    private TextView mStatusView;

    private String mConnectedDeviceName = null;
    private ArrayAdapter<String> mConversationArrayAdapter;
    private StringBuffer mOutStringBuffer;
    private BluetoothAdapter mBluetoothAdapter = null;
    private BluetoothChatService mChatService = null;
    
    private ArrayList<BluetoothDevice> discoveredDevices = new ArrayList<>();
    private ArrayAdapter<String> deviceListAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bt_chat);

        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (mBluetoothAdapter == null) {
            Toast.makeText(this, "Bluetooth is not available", Toast.LENGTH_LONG).show();
            finish();
            return;
        }

        mStatusView = findViewById(R.id.status);
        mConversationView = findViewById(R.id.in);
        mOutEditText = findViewById(R.id.edit_text_out);
        mSendButton = findViewById(R.id.button_send);

        setupNavigation();
        checkPermissions();
    }

    @SuppressLint("MissingPermission")
    private void checkPermissions() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_SCAN) != PackageManager.PERMISSION_GRANTED ||
                ContextCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{
                        Manifest.permission.BLUETOOTH_SCAN,
                        Manifest.permission.BLUETOOTH_CONNECT,
                        Manifest.permission.ACCESS_FINE_LOCATION
                }, PERMISSION_REQUEST_CODE);
            } else {
                setupChat();
            }
        } else {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, PERMISSION_REQUEST_CODE);
            } else {
                setupChat();
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                setupChat();
            } else {
                Toast.makeText(this, "Permissions required for Bluetooth Chat", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        if (!mBluetoothAdapter.isEnabled()) {
            Intent enableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableIntent, REQUEST_ENABLE_BT);
        } else if (mChatService == null) {
            setupChat();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mChatService != null) mChatService.stop();
        unregisterReceiver(mReceiver);
    }

    @Override
    public void onResume() {
        super.onResume();
        if (mChatService != null) {
            if (mChatService.getState() == BluetoothChatService.STATE_NONE) {
                mChatService.start();
            }
        }
    }

    private void setupChat() {
        mConversationArrayAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1);
        mConversationView.setAdapter(mConversationArrayAdapter);

        mSendButton.setOnClickListener(v -> {
            String message = mOutEditText.getText().toString();
            sendMessage(message);
        });

        mChatService = new BluetoothChatService(this, mHandler);
        mOutStringBuffer = new StringBuffer("");
        
        // Register for discovery results
        IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
        registerReceiver(mReceiver, filter);
        filter = new IntentFilter(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
        registerReceiver(mReceiver, filter);
    }

    private void sendMessage(String message) {
        if (mChatService.getState() != BluetoothChatService.STATE_CONNECTED) {
            Toast.makeText(this, "You are not connected", Toast.LENGTH_SHORT).show();
            return;
        }
        if (message.length() > 0) {
            byte[] send = message.getBytes();
            mChatService.write(send);
            mOutStringBuffer.setLength(0);
            mOutEditText.setText(mOutStringBuffer);
        }
    }

    @SuppressLint("MissingPermission")
    private final Handler mHandler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(@NonNull Message msg) {
            switch (msg.what) {
                case MESSAGE_STATE_CHANGE:
                    switch (msg.arg1) {
                        case BluetoothChatService.STATE_CONNECTED:
                            mStatusView.setText("Connected to " + mConnectedDeviceName);
                            mConversationArrayAdapter.clear();
                            break;
                        case BluetoothChatService.STATE_CONNECTING:
                            mStatusView.setText("Connecting...");
                            break;
                        case BluetoothChatService.STATE_LISTEN:
                        case BluetoothChatService.STATE_NONE:
                            mStatusView.setText("Not Connected");
                            break;
                    }
                    break;
                case MESSAGE_WRITE:
                    byte[] writeBuf = (byte[]) msg.obj;
                    String writeMessage = new String(writeBuf);
                    mConversationArrayAdapter.add("Me:  " + writeMessage);
                    break;
                case MESSAGE_READ:
                    byte[] readBuf = (byte[]) msg.obj;
                    String readMessage = new String(readBuf, 0, msg.arg1);
                    mConversationArrayAdapter.add(mConnectedDeviceName + ":  " + readMessage);
                    break;
                case MESSAGE_DEVICE_NAME:
                    mConnectedDeviceName = msg.getData().getString(DEVICE_NAME);
                    Toast.makeText(BTChatActivity.this, "Connected to " + mConnectedDeviceName, Toast.LENGTH_SHORT).show();
                    break;
                case MESSAGE_TOAST:
                    Toast.makeText(BTChatActivity.this, msg.getData().getString(TOAST), Toast.LENGTH_SHORT).show();
                    break;
            }
            return true;
        }
    });

    // Discovery Logic
    @SuppressLint("MissingPermission")
    private void discoverDevices() {
        if (mBluetoothAdapter.isDiscovering()) {
            mBluetoothAdapter.cancelDiscovery();
        }
        discoveredDevices.clear();
        deviceListAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1);
        
        mBluetoothAdapter.startDiscovery();
        
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Select Device to Chat");
        builder.setAdapter(deviceListAdapter, (dialog, which) -> {
            mBluetoothAdapter.cancelDiscovery();
            BluetoothDevice device = discoveredDevices.get(which);
            mChatService.connect(device);
        });
        builder.setNegativeButton("Cancel", (dialog, which) -> mBluetoothAdapter.cancelDiscovery());
        builder.show();
        
        Toast.makeText(this, "Scanning for devices...", Toast.LENGTH_SHORT).show();
    }

    private final BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @SuppressLint("MissingPermission")
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                if (device != null) {
                    String name = device.getName();
                    if (name != null) {
                        if (!discoveredDevices.contains(device)) {
                            discoveredDevices.add(device);
                            deviceListAdapter.add(name + "\n" + device.getAddress());
                            deviceListAdapter.notifyDataSetChanged();
                        }
                    }
                }
            }
        }
    };

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_bt_chat, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.scan) {
            discoverDevices();
            return true;
        } else if (item.getItemId() == R.id.discoverable) {
            ensureDiscoverable();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @SuppressLint("MissingPermission")
    private void ensureDiscoverable() {
        if (mBluetoothAdapter.getScanMode() != BluetoothAdapter.SCAN_MODE_CONNECTABLE_DISCOVERABLE) {
            Intent discoverableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
            discoverableIntent.putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, 300);
            startActivity(discoverableIntent);
        }
    }

    private void setupNavigation() {
        SharedPreferences pref = getSharedPreferences("UserPrefs", MODE_PRIVATE);
        String role = pref.getString("role", "Member");
        boolean isBuaRole = "Bua".equalsIgnoreCase(role);

        if (isBuaRole) {
            findViewById(R.id.btn_bazar_layout).setVisibility(View.GONE);
            findViewById(R.id.btn_cash_layout).setVisibility(View.GONE);
            findViewById(R.id.btn_meals_layout).setVisibility(View.GONE);
            
            android.widget.LinearLayout btnSalaryNav = findViewById(R.id.btn_member_layout);
            if (btnSalaryNav != null) {
                ((TextView) btnSalaryNav.getChildAt(1)).setText("Salary");
                ((android.widget.ImageView) btnSalaryNav.getChildAt(0)).setImageResource(R.drawable.ic_briefcase);
            }
        }

        if (findViewById(R.id.btn_home_layout) != null) {
            findViewById(R.id.btn_home_layout).setOnClickListener(v -> {
                startActivity(new Intent(this, MainActivity.class));
                finish();
            });
        }
        if (findViewById(R.id.btn_member_layout) != null) {
            findViewById(R.id.btn_member_layout).setOnClickListener(v -> {
                if (isBuaRole) {
                    startActivity(new Intent(this, BuaManagementActivity.class));
                } else {
                    startActivity(new Intent(this, MemberActivity.class));
                }
                finish();
            });
        }
        if (findViewById(R.id.btn_meals_layout) != null) {
            findViewById(R.id.btn_meals_layout).setOnClickListener(v -> {
                startActivity(new Intent(this, MealRoutineActivity.class));
                finish();
            });
        }
        if (findViewById(R.id.btn_bazar_layout) != null) {
            findViewById(R.id.btn_bazar_layout).setOnClickListener(v -> {
                startActivity(new Intent(this, BazarActivity.class));
                finish();
            });
        }
        if (findViewById(R.id.btn_cash_layout) != null) {
            findViewById(R.id.btn_cash_layout).setOnClickListener(v -> {
                startActivity(new Intent(this, CashLedgerActivity.class));
                finish();
            });
        }
        if (findViewById(R.id.btn_more_layout) != null) {
            findViewById(R.id.btn_more_layout).setOnClickListener(v -> {
                startActivity(new Intent(this, AllFeaturesActivity.class));
                finish();
            });
        }
    }
}

package io.treehouses.remote.Fragments;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentActivity;
import io.treehouses.remote.FragmentsOld.CustomHandler;
import io.treehouses.remote.InitialActivity;
import io.treehouses.remote.MiscOld.Constants;
import io.treehouses.remote.Network.BluetoothChatService;
import io.treehouses.remote.R;
import io.treehouses.remote.bases.BaseDialogFragment;

public class RPIDialogFragment extends BaseDialogFragment {

    private static BluetoothChatService mChatService = null;

    private static final String TAG = "RaspberryDialogFragment";
    private static boolean isRead = false;
    AlertDialog mDialog;
    private ArrayAdapter<String> mConversationArrayAdapter;
    ListView listView;
    BluetoothAdapter mBluetoothAdapter;
    List<String> s = new ArrayList<String>();
    List<BluetoothDevice> devices = new ArrayList<BluetoothDevice>();
    static BluetoothDevice mainDevice = null;
    ProgressDialog dialog;

    public static androidx.fragment.app.DialogFragment newInstance(int num) {
        RPIDialogFragment rpiDialogFragment = new RPIDialogFragment();
        Bundle bundle = new Bundle();
        bundle.putInt("num", num);
        rpiDialogFragment.setArguments(bundle);

        return rpiDialogFragment;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        // Build the dialog and set up the button click handlers
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        bluetoothCheck();
        if (mBluetoothAdapter.isDiscovering()) {
            mBluetoothAdapter.cancelDiscovery();
        }
        mBluetoothAdapter.startDiscovery();
        LayoutInflater inflater = getActivity().getLayoutInflater();
        final View mView = inflater.inflate(R.layout.activity_rpi_dialog_fragment, null);
        dialog = new ProgressDialog(getActivity(), ProgressDialog.THEME_HOLO_DARK);
        listView = mView.findViewById(R.id.listView);
        mDialog = getAlertDialog(mView);
        mDialog.setTitle(R.string.select_device);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                mainDevice = devices.get(position);
                mChatService.connect(devices.get(position), true);
                int status = mChatService.getState();
                mDialog.cancel();
//                InitialActivity initialActivity = new InitialActivity();
//                initialActivity.setChatService(mChatService);
                finish(status, mView);
                Log.e("Connecting Bluetooth", "Position: " + position + " ;; Status: " + status);
                dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                dialog.setTitle("Connecting...");
                dialog.setMessage("Device Name: " + mainDevice.getName() + "\nDevice Address: " + mainDevice.getAddress());
                dialog.show();

            }
        });

        if (mChatService == null) {
            setupBluetoothService();
        }

        Set<BluetoothDevice> pairedDevice = mBluetoothAdapter.getBondedDevices();
        if (pairedDevice.size() > 0) {
            for (BluetoothDevice device : pairedDevice) {
                devices.add(device);
                String deviceName = device.getName();
                String deviceHardwareAddress = device.getAddress(); // MAC address
                s.add(deviceName + "\n" + deviceHardwareAddress);
                setAdapterNotNull(s);
            }
        }

        IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
        filter.addAction(BluetoothAdapter.ACTION_DISCOVERY_STARTED);
        filter.addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
        filter.addAction(BluetoothAdapter.ACTION_SCAN_MODE_CHANGED);
        getActivity().registerReceiver(mReceiver, filter);

        return mDialog;
    }

    public BluetoothDevice getMainDevice() {
        return mainDevice;
    }

    public BluetoothChatService getChatService() {
        return mChatService;
    }

    public void finish(int status, View mView) {
        final AlertDialog mDialog = getAlertDialog(mView);
        if (status == 3) {
            mDialog.setTitle("BLUETOOTH IS CONNECTED");
        } else if (status == 2) {
            mDialog.setTitle("BLUETOOTH IS CONNECTING...");
        } else {
            mDialog.setTitle("BLUETOOTH IS NOT CONNECTED");
        }
        List<String> empty = new ArrayList<>();
        setAdapterNotNull(empty);
    }

//    private void connectDevice(Intent data) {
//        // Get the device MAC address
//        String address = data.getExtras()
//                .getString(DeviceListActivity.EXTRA_DEVICE_ADDRESS);
//        // Get the BluetoothDevice object
//        mainDevice = mBluetoothAdapter.getRemoteDevice(address);
//        // Attempt to connect to the device
//        mChatService.connect(mainDevice, true);
//    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onStop() {
        super.onStop();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    protected AlertDialog getAlertDialog(View mView) {
        return new AlertDialog.Builder(getActivity())
                .setView(mView)
//                .setTitle(R.string.dialog_message)
                .setIcon(R.drawable.dialog_icon)
//                .setPositiveButton(R.string.start_configuration, new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface dialog, int which) {
//                        int selected = listView.getSelectedItemPosition();
//                        String item = s.get(selected);
//                    }
//                })
                .setNegativeButton(R.string.material_drawer_close, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        getActivity().unregisterReceiver(mReceiver);
                        Intent intent = new Intent();
                        Bundle bundle = new Bundle();
                        intent.putExtra("mChatService", mChatService);
                        getTargetFragment().onActivityResult(getTargetRequestCode(), Activity.RESULT_OK, intent);
//                        getTargetFragment().onActivityResult(getTargetRequestCode(), Activity.RESULT_CANCELED, getActivity().getIntent());
                    }
                })
                .create();
    }

    protected void bluetoothCheck() {
        if (mBluetoothAdapter == null) {
            Log.i("Bluetooth Adapter", "Bluetooth not supported");
            Toast.makeText(getActivity(), "Your Bluetooth Is Not Enabled or Not Supported", Toast.LENGTH_LONG).show();
            getTargetFragment().onActivityResult(getTargetRequestCode(), Activity.RESULT_CANCELED, getActivity().getIntent());
            getActivity().unregisterReceiver(mReceiver);
        }
    }

    public void setAdapterNotNull(List<String> listVal) {
        if (getActivity() == null) {
            Log.e("RPI DIALOG ACTIVITY", "null");
        } else {
            listView.setAdapter(new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_1, listVal));
        }
    }

    // Create a BroadcastReceiver for ACTION_FOUND.
    public final BroadcastReceiver mReceiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                String deviceName = device.getName();
                String deviceHardwareAddress = device.getAddress(); // MAC address
                boolean alreadyExist = false;
                for (BluetoothDevice checkDevices : devices) {
                    if (checkDevices.equals(device)) {
                        alreadyExist = true;
                    }
                }
                if (!alreadyExist) {
                    devices.add(device);
                    s.add(deviceName + "\n" + deviceHardwareAddress);
                    setAdapterNotNull(s);
                }
                Log.e("Broadcast BT", device.getName() + "\n" + device.getAddress());
            }
        }
    };

    private void setupBluetoothService() {
        Log.d(TAG, "setupChat()");

        mChatService = new BluetoothChatService(mHandler);
    }

    //    private final CustomHandler mHandler = new CustomHandler(getActivity()){
//        @Override
//        public void handleMessage(Message msg) {
//            super.handleMessage(msg);
//            if(msg.what == Constants.MESSAGE_DEVICE_NAME){
//                String mConnectedDeviceName = msg.getData().getString(Constants.DEVICE_NAME);
//                Toast.makeText(getContext(), "Connected to "+mConnectedDeviceName, Toast.LENGTH_LONG).show();
//                dialog.dismiss();
//                mDialog.cancel();
//            }
//        }
//    };
    public final Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            Log.e("RPIDialogFragment", "" + msg.what);
            FragmentActivity activity = getActivity();
            switch (msg.what) {
                case Constants.MESSAGE_STATE_CHANGE:
                    switch (msg.arg1) {
                        case Constants.STATE_CONNECTED:
                            Log.e("RPIDialogFragment", "Bluetooth Connection Status Change: State Listen");
                            dialog.dismiss();
                            listener.setChatService(mChatService);
                            break;
                        case Constants.STATE_NONE:
                            Log.e("RPIDialogFragment", "Bluetooth Connection Status Change: State None");
                            break;
                    }
                    break;
//                case Constants.MESSAGE_READ:
//    //                    isRead = true;
//    //                    byte[] readBuf = (byte[]) msg.obj;
//    //                     construct a string from the valid bytes in the buffer
//    //                    String readMessage = new String(readBuf, 0, msg.arg1);
//    //                    String readMessage = new String(readBuf);
//                    readMessage = (String)msg.obj;
//                    Log.d(TAG, "readMessage = " + readMessage);
//                    //TODO: if message is json -> callback from RPi
//    //                    if(isJson(readMessage)){
//    //                        //handleCallback(readMessage);
//    //                    }else{
//    //                        if(isCountdown){
//    //                            //mHandler.removeCallbacks(watchDogTimeOut);
//    //                            isCountdown = false;
//    //                        }
//    //                        //remove the space at the very end of the readMessage -> eliminate space between items
//    //                        readMessage = readMessage.substring(0,readMessage.length()-1);
//    //                        //mConversationArrayAdapter.add(mConnectedDeviceName + ":  " + readMessage);
//    //
//    //                        //check if ping was successful
//    //                        if(readMessage.contains("1 packets")){
//    //                            mConnect();
//    //                        }
//    //                        if(readMessage.contains("Unreachable") || readMessage.contains("failure")){
//    //                            mOffline();
//    //                        }
//    //                        //make it so text doesn't show on chat (need a better way to check multiple strings since mConversationArrayAdapter only takes messages line by line)
//    //                        if (!readMessage.contains("1 packets") && !readMessage.contains("64 bytes") && !readMessage.contains("google.com") &&
//    //                                !readMessage.contains("rtt") && !readMessage.trim().isEmpty()){
//    //                            mConversationArrayAdapter.add(readMessage);
//    //                        }
//    //                    }
//                    break;
                case Constants.MESSAGE_DEVICE_NAME:
                    // save the connected device's name
                    Log.e("RPIDialogFragment", "Device Name " + msg.getData().getString(Constants.DEVICE_NAME));
//                    mConnectedDeviceName = msg.getData().getString(Constants.DEVICE_NAME);
                    //                    if (null != activity) {
                    //                        Toast.makeText(activity, "Connected to "
                    //                                + mConnectedDeviceName, Toast.LENGTH_SHORT).show();
                    //                    }
                    break;
            }
        }
    };
}

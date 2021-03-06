//package io.treehouses.remote.FragmentsOld;
//
//import android.app.Activity;
//import android.app.ProgressDialog;
//import android.bluetooth.BluetoothAdapter;
//import android.bluetooth.BluetoothDevice;
//import android.content.Context;
//import android.content.Intent;
//import android.graphics.Color;
//import android.os.Bundle;
//import android.os.Handler;
//import android.os.Message;
//import android.util.Log;
//import android.view.LayoutInflater;
//import android.view.View;
//import android.view.ViewGroup;
//import android.widget.ArrayAdapter;
//import android.widget.Button;
//import android.widget.EditText;
//import android.widget.ListView;
//import android.widget.TextView;
//import android.widget.Toast;
//
//import org.json.JSONException;
//import org.json.JSONObject;
//
//import androidx.annotation.NonNull;
//import androidx.annotation.Nullable;
//import androidx.fragment.app.Fragment;
//import androidx.fragment.app.FragmentActivity;
//import io.treehouses.remote.MiscOld.Constants;
//import io.treehouses.remote.Network.BluetoothChatService;
//import io.treehouses.remote.Network.DeviceListActivity;
//import io.treehouses.remote.R;
//
///**
// * Created by Lalitha S Oruganty on 3/22/2018.
// */
//
//public class DockerFragment extends Fragment implements View.OnClickListener{
//
//    private static final String TAG = "DockerFragment" ;
//
//    private FragmentActivity activity = getActivity();
//
//    // System variables
//    private BluetoothAdapter mBluetoothAdapter = null;
//    private BluetoothChatService mChatService = null;
//    private StringBuffer mOutStringBuffer;
//    private ArrayAdapter<String> outputArrayAdapter;
//
//    // Layout variables
//    private ListView consoleOutput;
//    private Button dockerPs;
//    private EditText mOutEditText;
//    private ProgressDialog mProgressDialog;
//
//    static String currentStatus = "not connected";
//    private static boolean isRead = false;
//    private String mConnectedDeviceName = null;
//    private static boolean isCountdown = false;
//
//    @Override
//    public void onCreate(@Nullable Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
//        if (mBluetoothAdapter == null) {
//            //FragmentActivity activity = getActivity();
//            Toast.makeText(activity, "Bluetooth is not available", Toast.LENGTH_LONG).show();
//            activity.finish();
//        }
//    }
//
//    @Override
//    public void onStart() {
//        super.onStart();
//        // If BT is not on, request that it be enabled.
//        // setupChat() will then be called during onActivityResult
//        if (!mBluetoothAdapter.isEnabled()) {
//            Intent enableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
//            startActivityForResult(enableIntent, Constants.REQUEST_ENABLE_BT);
//            // Otherwise, setup the chat session
//        } else if (mChatService == null) {
//            setupChat();
//        }
//    }
//
//    @Override
//    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
//        return inflater.inflate(R.layout.docker_layout, container, false);
//    }
//
//    @Override
//    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
//        dockerPs = (Button) view.findViewById(R.id.btn_docker_ps);
//        consoleOutput = (ListView) view.findViewById(R.id.lv_console_output);
//        dockerPs.setOnClickListener(this);
//    }
//
//    @Override
//    public void onClick(View v){
//        switch (v.getId()) {
//            case R.id.btn_docker_ps:
//                sendMessage("docker ps");
//                break;
//        }
//    }
//
//    @Override
//    public void onActivityResult(int requestCode, int resultCode, Intent data) {
//        switch (requestCode) {
//            case Constants.REQUEST_CONNECT_DEVICE_SECURE:
//                // When DeviceListActivity returns with a device to connect
//                if (resultCode == Activity.RESULT_OK) {
//                    connectDevice(data, true);
//                }
//                break;
//            case Constants.REQUEST_CONNECT_DEVICE_INSECURE:
//                // When DeviceListActivity returns with a device to connect
//                if (resultCode == Activity.RESULT_OK) {
//                    connectDevice(data, false);
//                }
//                break;
//            case Constants.REQUEST_ENABLE_BT:
//                // When the request to enable Bluetooth returns
//                if (resultCode == Activity.RESULT_OK) {
//                    // Bluetooth is now enabled, so set up a chat session
//                    setupChat();
//                } else {
//                    // User did not enable Bluetooth or an error occurred
//                    Log.d(TAG, "BT not enabled");
//                    Toast.makeText(getActivity(), R.string.bt_not_enabled_leaving,
//                            Toast.LENGTH_SHORT).show();
//                    //getApplication().finish();
//                }
//            case Constants.REQUEST_DIALOG_FRAGMENT:
//                if(resultCode == Activity.RESULT_OK){
//
//                    //check status
//                    if(mChatService.getState() != Constants.STATE_CONNECTED){
//                        Toast.makeText(getActivity(), R.string.not_connected,
//                                Toast.LENGTH_SHORT).show();
//                        return;
//                    }
//
//                    //show the progress bar, disable user interaction
//                    mProgressDialog.show();
//                    //TODO: start watchdog
//                    isCountdown = true;
//                    mHandler.postDelayed(watchDogTimeOut,30000);
//                    Log.d(TAG, "watchDog start");
//
//                    //get SSID & PWD from user input
//                    String SSID = data.getStringExtra("SSID") == null? "":data.getStringExtra("SSID");
//                    String PWD = data.getStringExtra("PWD") == null? "":data.getStringExtra("PWD");
//
//                    Log.d(TAG, "back from dialog: ok, SSID = " + SSID + ", PWD = " + PWD);
//
//                    //TODO: 1. check Valid input  2. get the SSID and password from data object and send it to RPi through sendMessage() method
////                    Toast.makeText(getActivity(), R.string.config_success,
////                            Toast.LENGTH_SHORT).show();
//
//                    sendMessage(SSID,PWD);
//                    //TODO:1. lock the app when configuring. 2. listen to configuration result and do the logic
//
//                }else{
//                    Log.d(TAG, "back from dialog, fail");
//                }
//        }
//    }
//
//    private void setupChat() {
//        Log.d(TAG, "setupChat()");
//
//        // Initialize the array adapter for the conversation thread
//        outputArrayAdapter = new ArrayAdapter<String>(getActivity(),R.layout.message){
//            @NonNull
//            @Override
//            public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
//                View view = super.getView(position, convertView, parent);
//                TextView tView = (TextView) view.findViewById(R.id.listItem);
//                if(isRead){
//                    tView.setTextColor(Color.BLUE);
//                }else{
//                    tView.setTextColor(Color.RED);
//                }
//                return view;
//            }
//        };
//
//        consoleOutput.setAdapter(outputArrayAdapter);
//
//        // Initialize the BluetoothChatService to perform bluetooth connections
//        mChatService = new BluetoothChatService(mHandler);
//
//        // Initialize the buffer for outgoing messages
//        mOutStringBuffer = new StringBuffer("");
//
//        //get spinner
//        mProgressDialog = new ProgressDialog(getActivity());
//        mProgressDialog.setTitle(R.string.progress_dialog_title);
//        mProgressDialog.setMessage(getString(R.string.progress_dialog_message));
//        mProgressDialog.setCancelable(false); // disable dismiss by tapping outside of the dialog
//
//    }
//
//    /**
//     * Sends a message.
//     *
//     * @param message A string of text to send.
//     */
//    private void sendMessage(String message) {
//        // Check that we're actually connected before trying anything
//        if (mChatService.getState() != Constants.STATE_CONNECTED) {
//            Toast.makeText(getActivity(), R.string.not_connected, Toast.LENGTH_SHORT).show();
//            return;
//        }
//
//        // Check that there's actually something to send
//        if (message.length() > 0) {
//            // Get the message bytes and tell the BluetoothChatService to write
//            byte[] send = message.getBytes();
//            mChatService.write(send);
//
//            // Reset out string buffer to zero and clear the edit text field
//            mOutStringBuffer.setLength(0);
//            mOutEditText.setText(mOutStringBuffer);
//        }
//
//    }
//
//    private void sendMessage(String SSID, String PWD) {
//        // Check that we're actually connected before trying anything
//        if (mChatService.getState() != Constants.STATE_CONNECTED) {
//            Toast.makeText(getActivity(), R.string.not_connected, Toast.LENGTH_SHORT).show();
//            return;
//        }
//
//        // Check that there's actually something to send
//        if (SSID.length() > 0) {
//            // Get the message bytes and tell the BluetoothChatService to write
//            JSONObject mJson = new JSONObject();
//            try {
//                mJson.put("SSID",SSID);
//                mJson.put("PWD",PWD);
//            } catch (JSONException e) {
//                e.printStackTrace();
//            }
//
//            byte[] send = mJson.toString().getBytes();
//            mChatService.write(send);
//
//            // Reset out string buffer to zero and clear the edit text field
//            mOutStringBuffer.setLength(0);
//            mOutEditText.setText(mOutStringBuffer);
//        }
//    }
//
//    private final Handler mHandler = new Handler() {
//        @Override
//        public void handleMessage(Message msg) {
//            FragmentActivity activity = getActivity();
//            switch (msg.what) {
//                case Constants.MESSAGE_STATE_CHANGE:
//                    switch (msg.arg1) {
//                        case Constants.STATE_LISTEN:
//                    }
//                    break;
//                case Constants.MESSAGE_WRITE:
//                    isRead = false;
//                    byte[] writeBuf = (byte[]) msg.obj;
//                    // construct a string from the buffer
//                    String writeMessage = new String(writeBuf);
//                    Log.d(TAG, "writeMessage = " + writeMessage);
//                    outputArrayAdapter.add("Command:  " + writeMessage);
//                    break;
//                case Constants.MESSAGE_READ:
//                    isRead = true;
//                    //                    byte[] readBuf = (byte[]) msg.obj;
//                    //                     construct a string from the valid bytes in the buffer
//                    //                    String readMessage = new String(readBuf, 0, msg.arg1);
//                    //                    String readMessage = new String(readBuf);
//                    String readMessage = (String) msg.obj;
//                    Log.d(TAG, "readMessage = " + readMessage);
//                    //TODO: if message is json -> callback from RPi
//                    if (isJson(readMessage)) {
//                        handleCallback(readMessage);
//                    } else {
//                        if (isCountdown) {
//                            mHandler.removeCallbacks(watchDogTimeOut);
//                            isCountdown = false;
//                        }
//                        if (mProgressDialog.isShowing()) {
//                            mProgressDialog.dismiss();
//                            Toast.makeText((Context) activity, R.string.config_alreadyConfig, Toast.LENGTH_SHORT).show();
//                        }
//                        //remove the space at the very end of the readMessage -> eliminate space between items
//                        readMessage = readMessage.substring(0, readMessage.length() - 1);
//                        //mConversationArrayAdapter.add(mConnectedDeviceName + ":  " + readMessage);
//                        outputArrayAdapter.add(readMessage);
//                    }
//                    break;
//                case Constants.MESSAGE_DEVICE_NAME:
//                    // save the connected device's name
//                    mConnectedDeviceName = msg.getData().getString(Constants.DEVICE_NAME);
//                    if (null != activity) {
//                        Toast.makeText(activity, "Connected to "
//                                + mConnectedDeviceName, Toast.LENGTH_SHORT).show();
//                    }
//                    break;
//                case Constants.MESSAGE_TOAST:
//                    if (null != activity) {
//                        Toast.makeText(activity, msg.getData().getString(Constants.TOAST),
//                                Toast.LENGTH_SHORT).show();
//                    }
//                    break;
//            }
//        }
//    };
//
//    private void connectDevice(Intent data, boolean secure) {
//        // Get the device MAC address
//        String address = data.getExtras()
//                .getString(DeviceListActivity.EXTRA_DEVICE_ADDRESS);
//        // Get the BluetoothDevice object
//        BluetoothDevice device = mBluetoothAdapter.getRemoteDevice(address);
//        // Attempt to connect to the device
//        mChatService.connect(device, secure);
//    }
//
//    private final Runnable watchDogTimeOut = new Runnable() {
//        @Override
//        public void run() {
//            isCountdown = false;
//            //time out
//            if(mProgressDialog.isShowing()){
//                mProgressDialog.dismiss();
//                Toast.makeText(getActivity(),"No response from RPi",Toast.LENGTH_LONG).show();
//            }
//        }
//    };
//
//    public boolean isJson(String str) {
//        try {
//            new JSONObject(str);
//        } catch (JSONException ex) {
//            return false;
//        }
//        return true;
//    }
//
//    public void handleCallback(String str){
//        String result;
//        String ip;
//        if(isCountdown){
//            mHandler.removeCallbacks(watchDogTimeOut);
//            isCountdown = false;
//        }
//
//        //enable user interaction
//        try{
//            JSONObject mJSON = new JSONObject(str);
//            result = mJSON.getString("result") == null? "" : mJSON.getString("result");
//            ip = mJSON.getString("IP") == null? "" : mJSON.getString("IP");
//            //Toast.makeText(getActivity(), "result: "+result+", IP: "+ip, Toast.LENGTH_LONG).show();
//
//            if(!result.equals("SUCCESS")){
//                Toast.makeText(getActivity(), R.string.config_fail,
//                        Toast.LENGTH_LONG).show();
//            }else{
////                Toast.makeText(getActivity(), R.string.config_success,
////                            Toast.LENGTH_SHORT).show();
//                Toast.makeText(getActivity(),getString(R.string.config_success) + ip,Toast.LENGTH_LONG).show();
//            }
//
//        }catch (JSONException e){
//            // error handling
//            Toast.makeText(getActivity(), "SOMETHING WENT WRONG", Toast.LENGTH_LONG).show();
//        }
//    }
//}

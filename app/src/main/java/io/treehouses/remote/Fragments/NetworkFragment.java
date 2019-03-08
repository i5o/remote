package io.treehouses.remote.Fragments;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.DialogFragment;
import android.app.Fragment;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import java.util.ArrayList;

import androidx.fragment.app.FragmentActivity;
import io.treehouses.remote.InitialActivity;
import io.treehouses.remote.MiscOld.Constants;
import io.treehouses.remote.Network.BluetoothChatService;
import io.treehouses.remote.R;

import static android.content.Context.WIFI_SERVICE;

public class NetworkFragment extends androidx.fragment.app.Fragment {

    View view;

    InitialActivity initialActivity;

//    EditText HotspotPasswordEditText;
//    EditText PasswordEditText;

    public NetworkFragment(){}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.activity_network_fragment, container, false);

        initialActivity = new InitialActivity();
        BluetoothChatService chatService = initialActivity.getChatService();
        chatService.updateHandler(mHandler);

        ArrayList<String> list = new ArrayList<String>();
        list.add("Ethernet");
        list.add("Wi-Fi");
        list.add("Hotspot");
        list.add("Bridge");
        list.add("Reset");
        list.add("Reboot");

        ListView listView = view.findViewById(R.id.listView);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_1, list);
        listView.setAdapter(adapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                getListFragment(position);
            }
        });

        return view;
    }

    public void getListFragment(int position){
        switch (position){
            case 0:
                showEthernetDialog();
                break;
            case 1:
                showWifiDialog();
                break;
            case 2:
                showHotspotDialog();
                break;
            case 3:
                showBridgeDialog();
                break;
            case 4:
                initialActivity.sendMessage("treehouses default network");
                break;
            case 5:
                initialActivity.sendMessage("reboot");
                break;
            default:
                Log.e("Default Network Switch", "Nothing...");
        }
    }
    public void showBridgeDialog(){
        androidx.fragment.app.DialogFragment dialogFrag = BridgeDialogFragment.newInstance(123);
        dialogFrag.setTargetFragment(this, Constants.REQUEST_DIALOG_FRAGMENT_HOTSPOT);
        dialogFrag.show(getFragmentManager().beginTransaction(),"bridgeDialog");
    }
    public void showEthernetDialog(){
        androidx.fragment.app.DialogFragment dialogFrag = EthernetDialogFragment.newInstance(123);
        dialogFrag.setTargetFragment(this, Constants.REQUEST_DIALOG_FRAGMENT_HOTSPOT);
        dialogFrag.show(getFragmentManager().beginTransaction(),"ethernetDialog");
    }
    public void showHotspotDialog(){
        androidx.fragment.app.DialogFragment dialogFrag = HotspotDialogFragment.newInstance(123);
        dialogFrag.setTargetFragment(this, Constants.REQUEST_DIALOG_FRAGMENT_HOTSPOT);
        dialogFrag.show(getFragmentManager().beginTransaction(),"hotspotDialog");
    }
    public void showWifiDialog() {
        androidx.fragment.app.DialogFragment dialogFrag = WifiDialogFragment.newInstance(123);
        dialogFrag.setTargetFragment(this, Constants.REQUEST_DIALOG_FRAGMENT);
        dialogFrag.show(getFragmentManager().beginTransaction(), "wifiDialog");
    }
//    public void showResetFragment(){
//        androidx.fragment.app.DialogFragment dialogFrag = ResetFragment.newInstance(123);
//        dialogFrag.setTargetFragment(this, Constants.REQUEST_DIALOG_FRAGMENT);
//        dialogFrag.show(getFragmentManager().beginTransaction(), "resetDialog");
//    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(resultCode == Activity.RESULT_OK){
            Bundle bundle = data.getExtras();
            String type = bundle.getString("type");
            Log.e("ON ACTIVITY RESULT","Request Code: "+requestCode+" ;; Result Code: "+resultCode+" ;; Intent: "+bundle+" ;; Type: "+bundle.getString("type"));
            switch (type){
                case "wifi":
                    wifiOn(bundle);
                    break;
                case "hotspot":
                    hotspotOn(bundle);
                    break;
                case "ethernet":
                    ethernetOn(bundle);
                    break;
                case "bridge":
                    bridgeOn(bundle);
                    break;
                default:
                    break;
            }
        }
    }

    private void wifiOn(Bundle bundle){

        initialActivity.sendMessage("treehouses wifi \""+bundle.getString("SSID")+"\" \""+bundle.getString("PWD")+"\"");

//        WifiManager wifi = (WifiManager) getContext().getApplicationContext().getSystemService(WIFI_SERVICE);
//        WifiConfiguration wc = new WifiConfiguration();
//        wc.SSID = "\""+bundle.getString("SSID")+"\""; //IMP! This should be in Quotes!!
//        wc.hiddenSSID = true;
//        wc.status = WifiConfiguration.Status.DISABLED;
//        wc.priority = 40;
//        wc.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.NONE);
//        wc.allowedProtocols.set(WifiConfiguration.Protocol.RSN);
//        wc.allowedProtocols.set(WifiConfiguration.Protocol.WPA);
//        wc.allowedAuthAlgorithms.set(WifiConfiguration.AuthAlgorithm.OPEN);
//        wc.allowedAuthAlgorithms.set(WifiConfiguration.AuthAlgorithm.SHARED);
//        wc.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.CCMP);
//        wc.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.TKIP);
//        wc.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.WEP40);
//        wc.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.WEP104);
//
//        wc.wepKeys[0] = "\""+bundle.getString("PWD")+"\""; //This is the WEP Password
//        wc.wepTxKeyIndex = 0;
//
//        boolean res1 = wifi.setWifiEnabled(true);
//        int res = wifi.addNetwork(wc);
//        Log.d("WifiPreference", "add Network returned " + res );
//        boolean es = wifi.saveConfiguration();
//        Log.d("WifiPreference", "saveConfiguration returned " + es );
//        boolean b = wifi.enableNetwork(res, true);
//        Log.d("WifiPreference", "enableNetwork returned " + b );
    }
    private void hotspotOn (Bundle bundle){
        if(bundle.getString("HPWD").equals("")){
            initialActivity.sendMessage("treehouses ap \""+bundle.getString("hotspotType")+"\" \""+bundle.getString("HSSID")+"\"");
        }else{
            initialActivity.sendMessage("treehouses ap \""+bundle.getString("hotspotType")+"\" \""+bundle.getString("HSSID")+"\" \""+bundle.getString("HPWD")+"\"");
        }
    }
    private void ethernetOn(Bundle bundle){
        initialActivity.sendMessage("treehouses ethernet \""+bundle.getString("ip")+"\" \""+bundle.getString("mask")+"\" \""+bundle.getString("gateway")+"\" \""+bundle.getString("dns")+"\"");
    }

    private void bridgeOn(Bundle bundle){
        String overallMessage = "treehouses bridge \""+(bundle.getString("essid"))+"\" \""+bundle.getString("hssid")+"\" ";

        if(TextUtils.isEmpty(bundle.getString("password"))){
            overallMessage+="\"\"";
        }else{
            overallMessage+="\""+bundle.getString("password")+"\"";
        }
        overallMessage+=" ";
        if(!TextUtils.isEmpty(bundle.getString("hpassword"))){
            overallMessage+="\""+bundle.getString("hpassword")+"\"";
        }
        Log.e("NetworkFragment","Bridge RPI Message = "+overallMessage);
        initialActivity.sendMessage(overallMessage);
    }

    private AlertDialog showAlertDialog(String message){
        return new AlertDialog.Builder(getContext())
                .setTitle("OUTPUT:")
                .setMessage(message)
                .setIcon(R.drawable.wificon)
                .setNegativeButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) { dialog.cancel(); }
                })
                .show();
    }

    /**
     * The Handler that gets information back from the BluetoothChatService
     */
    public final Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            FragmentActivity activity = getActivity();
            switch (msg.what) {
                case Constants.MESSAGE_READ:
                    String readMessage = (String)msg.obj;
                    showAlertDialog(readMessage);
                    break;
            }
        }
    };
}
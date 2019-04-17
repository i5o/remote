package io.treehouses.remote;

import android.content.Context;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import com.google.android.material.textfield.TextInputEditText;
import java.util.ArrayList;
import io.treehouses.remote.Fragments.HomeFragment;
import io.treehouses.remote.MiscOld.Constants;
import io.treehouses.remote.Network.BluetoothChatService;
import io.treehouses.remote.bases.BaseFragment;
import io.treehouses.remote.callback.HomeInteractListener;

import static io.treehouses.remote.MiscOld.Constants.getGroups;

public class ExpandableListAdapter extends BaseExpandableListAdapter {

    private Boolean child1 = false;
    private Boolean child2 = false;
    private Boolean child3 = false;
    private Boolean child4 = false;
    private String child_first;
    private String child_second;
    private String child_third;
    private String child_forth;
    private final LayoutInflater inf;
    private ArrayList<String> groups;
    private String[][] children;
    private BluetoothChatService mChatService;
    private Context context;
    private Bundle bundle = new Bundle();
    private ViewHolder holder = new ViewHolder();
    private BaseFragment baseFragment = new BaseFragment();


    public ExpandableListAdapter(Context context, ArrayList<String> groups, String[][] children, BluetoothChatService mChatService) {
        this.groups = groups;
        this.children = children;
        inf = LayoutInflater.from(context);
        this.mChatService = mChatService;
        this.context = context;
    }

    public Context getContext() {
        return context;
    }

    @Override
    public int getGroupCount() {
        return groups.size();
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        return children[groupPosition].length;
    }

    @Override
    public Object getGroup(int groupPosition) {
        return groups.get(groupPosition);
    }

    @Override
    public Object getChild(int groupPosition, int childPosition) {
        return children[groupPosition][childPosition];
    }

    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }

    @Override
    public boolean hasStableIds() {
        return true;
    }

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
        convertView = inf.inflate(R.layout.list_group, parent, false);
        TextView listHeader = convertView.findViewById(R.id.lblListHeader);
        convertView.setTag(holder);
        holder = (ViewHolder) convertView.getTag();
        listHeader.setText(getGroup(groupPosition).toString());
        return convertView;
    }

    @Override
    public View getChildView(final int groupPosition, final int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
        final String child = getChild(groupPosition, childPosition).toString().trim();

        if (child.contains("Reset") || child.contains("Reboot")) {
            convertView = buttonLayout(parent, child, groupPosition, childPosition);
        } else {
            convertView = group(child, convertView, parent, groupPosition, childPosition);

            if (child1) {
                child(childPosition, holder.getEditText1());
                child1 = false;
            } else if (child2) {
                child(childPosition, holder.getEditText2());
                child2 = false;
            } else if (child3) {
                child(childPosition, holder.getEditText3());
                child3 = false;
            } else if (child4) {
                child(childPosition, holder.getEditText4());
                child4 = false;
            }
        }
        return convertView;
    }

    private View group(String child, View convertView, ViewGroup parent, int groupPosition, int childPosition) {

        if (child.equals("ESSID") || child.equals("IP Address")) {
            convertView = inf.inflate(R.layout.list_child, parent, false);
            holder.setEditText1((TextInputEditText) convertView.findViewById(R.id.lblListItem));
            convertView.setTag(holder);
            holder.getEditText1().setHint(child);
            child1 = true;
        } else if (child.equals("Password") || child.equals("DNS")) {
            convertView = inf.inflate(R.layout.list_child2, parent, false);
            holder.setEditText2((TextInputEditText) convertView.findViewById(R.id.lblListItem2));
            convertView.setTag(holder);
            holder.getEditText2().setHint(child);
            child2 = true;
        } else if (child.equals("Hotspot ESSID") || child.equals("Gateway")) {
            convertView = inf.inflate(R.layout.list_child3, parent, false);
            holder.setEditText3((TextInputEditText) convertView.findViewById(R.id.lblListItem3));
            convertView.setTag(holder);
            holder.getEditText3().setHint(child);
            child3 = true;
        } else if (child.equals("Hotspot Password") || child.equals("Mask")) {
            convertView = inf.inflate(R.layout.list_child4, parent, false);
            holder.setEditText4((TextInputEditText) convertView.findViewById(R.id.lblListItem4));
            convertView.setTag(holder);
            holder.getEditText4().setHint(child);
            child4 = true;
        } else if (child.contains("Start")) {
            convertView = buttonLayout(parent, child, groupPosition, childPosition);
        }
        return convertView;
    }

    private View buttonLayout(final ViewGroup parent, final String child, final int groupPosition, final int childPosition) {
        View convertView = inf.inflate(R.layout.list_button, parent, false);
        holder.setButton((Button) convertView.findViewById(R.id.listButton));
        convertView.setTag(holder);
        holder.getButton().setText(child);
        holder.getButton().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                BaseFragment baseFragment = new BaseFragment();
                baseFragment.listener = (HomeInteractListener) context;

                if (groupPosition == 4 && childPosition == 0) {
                    baseFragment.listener.sendMessage("treehouses default network");
                } else if (groupPosition == 5 && childPosition == 0) {
                   reboot(baseFragment);
                }
                if (!bundle.equals("")) {
                   childData(groupPosition);
                }
            }
        });
        return convertView;
    }

    private void reboot(BaseFragment baseFragment) {
        try {
            baseFragment.listener.sendMessage("reboot");
            Thread.sleep(1000);
            if (mChatService.getState() != Constants.STATE_CONNECTED) {
                Toast.makeText(context, "Bluetooth Disconnected: Reboot in progress", Toast.LENGTH_LONG).show();
                baseFragment.listener.openCallFragment(new HomeFragment());
            } else {
                Toast.makeText(context, "Reboot Unsuccessful", Toast.LENGTH_LONG).show();
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private void child(final int childPosition, EditText editText) {

        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }
            @Override
            public void afterTextChanged(Editable s) {
                if (childPosition == 0) {
                    child_first = s.toString();
                    storeData(childPosition, child_first);
                } else if (childPosition == 1) {
                    child_second = s.toString();
                    storeData(childPosition, child_second);
                } else if (childPosition == 2) {
                    child_third = s.toString();
                    storeData(childPosition, child_third);
                } else if (childPosition == 3) {
                    child_forth = s.toString();
                    storeData(childPosition, child_forth);
                }
            }
        });
    }

    private void storeData(int childPosition, String child) {
        if (childPosition == 0) {
            bundle.putString("first", child);
        } else if (childPosition == 1) {
            bundle.putString("second", child);
        } else if (childPosition == 2) {
            bundle.putString("third", child);
        } else if (childPosition == 3) {
            bundle.putString("forth", child);
        }
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }

    private void childData(int groupPosition) {
        String type = getGroups().get(groupPosition);
        baseFragment.listener = (HomeInteractListener) context;

        switch (type) {
            case "WiFi":
                baseFragment.listener.sendMessage("treehouses wifi \"" + bundle.getString("first") + "\" \"" + bundle.getString("second") + "\"");
                Toast.makeText(getContext(), "Connecting...", Toast.LENGTH_LONG).show();
                break;
            case "Hotspot":
                if (bundle.getString("HPWD").equals("")) {
                    baseFragment.listener.sendMessage("treehouses ap \"" + bundle.getString("hotspotType") + "\" \"" + bundle.getString("HSSID") + "\"");
                } else {
                    baseFragment.listener.sendMessage("treehouses ap \"" + bundle.getString("hotspotType") + "\" \"" + bundle.getString("HSSID") + "\" \"" + bundle.getString("HPWD") + "\"");
                }
                break;
            case "Ethernet: Automatic":
                baseFragment.listener.sendMessage("treehouses ethernet \"" + bundle.getString("first") + "\" \"" + bundle.getString("forth") + "\" \"" + bundle.getString("third") + "\" \"" + bundle.getString("second") + "\"");
                break;
            case "Bridge":
                String temp = "treehouses bridge \"" + (bundle.getString("first")) + "\" \"" + bundle.getString("third") + "\" ";
                String overallMessage = TextUtils.isEmpty(bundle.getString("second")) ? (temp += "\"\"") : (temp += "\"") + bundle.getString("second") + "\"";
                overallMessage += " ";
                if (!TextUtils.isEmpty(bundle.getString("forth"))) {
                    overallMessage += "\"" + bundle.getString("forth") + "\"";
                }
                baseFragment.listener.sendMessage(overallMessage);
                break;
            default:
                break;
        }
    }
}



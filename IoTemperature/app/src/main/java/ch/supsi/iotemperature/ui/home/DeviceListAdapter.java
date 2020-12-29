package ch.supsi.iotemperature.ui.home;

import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

import ch.supsi.iotemperature.R;

public class DeviceListAdapter extends ArrayAdapter<BluetoothDevice> {

    public DeviceListAdapter(Context context, List<BluetoothDevice> devices) {
        super(context, 0, devices);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Get the data item for this position
        BluetoothDevice device = getItem(position);
        // Check if an existing view is being reused, otherwise inflate the view
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.list_item, parent, false);
        }

        TextView deviceName = convertView.findViewById(R.id.deviceName);
        deviceName.setText(device.getName());

        TextView deviceMac = convertView.findViewById(R.id.deviceMac);
        deviceMac.setText(device.getAddress());

        // API LEVEL 30
//        TextView deviceAlias = convertView.findViewById(R.id.deviceAlias);
//        deviceAlias.setText(device.getAlias());

        return convertView;
    }

    public void addDevice(BluetoothDevice device) {
        this.add(device);
    }
}